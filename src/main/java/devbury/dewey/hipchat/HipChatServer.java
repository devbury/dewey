/*
 * Copyright 2014 devbury LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package devbury.dewey.hipchat;

import com.google.common.annotations.VisibleForTesting;
import devbury.dewey.hipchat.api.model.UserInfo;
import devbury.dewey.model.Address;
import devbury.dewey.model.AddressType;
import devbury.dewey.model.Group;
import devbury.dewey.model.User;
import devbury.dewey.server.ChatServer;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HipChatServer implements ChatServer {

    public static final long ROOM_REFRESH_INTERVAL = 1000 * 60 * 2;

    private static final String GROUP_SERVICE_NAME = "conf.hipchat.com";

    private static final Logger logger = LoggerFactory.getLogger(HipChatServer.class);

    @Autowired
    private HipChatSettings hipChatSettings;

    @Autowired
    private UserManager userManager;

    @Autowired
    private List<FilteredPacketListener> filteredPacketListeners;

    private Map<String, MultiUserChat> joinedRooms = new HashMap<>();

    private XMPPConnection xmppConnection;

    @PostConstruct
    public void start() throws Exception {
        UserInfo userInfo = userManager.findUserInfoByEmail(hipChatSettings.getEmail());
        hipChatSettings.setMentionName(userInfo.getMentionName());
        hipChatSettings.setName(userInfo.getName());
        hipChatSettings.setXmppJid(userInfo.getXmppJid());

        logger.debug("HipChat settings {}", hipChatSettings);

        logger.info("Connecting to HipChat");
        ConnectionConfiguration config = new ConnectionConfiguration(hipChatSettings.getServer(),
                hipChatSettings.getPort());

        xmppConnection = new XMPPConnection(config);

        xmppConnection.connect();
        xmppConnection.login(hipChatSettings.getXmppJid(), hipChatSettings.getPassword(), hipChatSettings.getResource());

        joinRooms();

        Thread.sleep(3000); // sleep for a bit to try to avoid receiving messages about us joining rooms and
        // receiving chat history

        for (FilteredPacketListener filteredPacketListener : filteredPacketListeners) {
            logger.info("registering PacketListener {}", filteredPacketListener);
            xmppConnection.addPacketListener(filteredPacketListener, filteredPacketListener.getPacketTypeFilter());
        }
        if (AllPacketListener.logger.isTraceEnabled()) {
            // register a trace listener
            AllPacketListener.logger.trace("registering debug AllPacketListener");
            AllPacketListener allPacketListener = new AllPacketListener();
            xmppConnection.addPacketListener(allPacketListener, allPacketListener.getPacketTypeFilter());
        }
    }

    @PreDestroy
    public void stop() throws Exception {
        logger.info("disconnecting from HipChat");
        xmppConnection.disconnect();
    }

    @Scheduled(fixedDelay = HipChatServer.ROOM_REFRESH_INTERVAL)
    protected void joinRooms() throws Exception {
        logger.debug("looking for rooms to join");
        for (HostedRoom hostedRoom : MultiUserChat.getHostedRooms(xmppConnection, GROUP_SERVICE_NAME)) {
            if (!joinedRooms.containsKey(hostedRoom.getName())) {
                MultiUserChat room = new MultiUserChat(xmppConnection, hostedRoom.getJid());
                if (!room.isJoined()) {
                    logger.debug("joining room {}", hostedRoom.getName());
                    room.join(hipChatSettings.getName());
                }
                joinedRooms.put(hostedRoom.getName(), room);
            }
        }
    }

    @Override
    public void sendMessage(Address address, String message) {
        if (address.getAddressType() == AddressType.GROUP) {
            Group group = (Group) address;
            logger.debug("sending message to room {}", group.getName());
            MultiUserChat room = joinedRooms.get(group.getName());
            try {
                room.sendMessage(message);
            } catch (Exception e) {
                logger.warn("Could not send message to room {}, {}", room.getNickname(), e);
            }
        } else {
            User user = (User) address;
            logger.debug("sending message to user {}", user.getName());
            String xmppJid = userManager.findXmppJidById(userManager.findUserEntryByName(user.getName()).getId());
            try {
                xmppConnection.getChatManager().createChat(xmppJid, null).sendMessage(message);
            } catch (Exception e) {
                logger.warn("Could not send message to user {}, {}", xmppJid, e);
            }
        }
    }

    @VisibleForTesting
    void setXmppConnection(XMPPConnection xmppConnection) {
        this.xmppConnection = xmppConnection;
    }

    @VisibleForTesting
    void setJoinedRooms(Map<String, MultiUserChat> joinedRooms) {
        this.joinedRooms = joinedRooms;
    }

    @VisibleForTesting
    void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
