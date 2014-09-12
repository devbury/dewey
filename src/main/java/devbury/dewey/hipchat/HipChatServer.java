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

@Component
public class HipChatServer implements ChatServer {

    public static final long ROOM_REFRESH_INTERVAL = 1000 * 60 * 2;

    private static final String GROUP_SERVICE_NAME = "conf.hipchat.com";

    private static final Logger logger = LoggerFactory.getLogger(HipChatServer.class);

    @Autowired
    private HipChatSettings hipChatSettings;

    @Autowired
    private List<FilteredPacketListener> filteredPacketListeners;

    private HashMap<String, MultiUserChat> joinedRooms = new HashMap<>();

    private XMPPConnection xmppConnection;

    @PostConstruct
    public void start() throws Exception {
        logger.info("Connecting to HipChat");

        ConnectionConfiguration config = new ConnectionConfiguration(hipChatSettings.getServer(),
                hipChatSettings.getPort());
        xmppConnection = new XMPPConnection(config);

        xmppConnection.connect();
        xmppConnection.login(hipChatSettings.getUserId(), hipChatSettings.getPassword(), hipChatSettings.getResource());

        joinRooms();

        Thread.sleep(3000); // sleep for a bit to try to avoid receiving messages about us joining rooms and
        // receiving chat history

        for (FilteredPacketListener filteredPacketListener : filteredPacketListeners) {
            logger.debug("registering PacketListener {}", filteredPacketListener);
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
            if (!joinedRooms.containsKey(hostedRoom.getJid())) {
                MultiUserChat room = new MultiUserChat(xmppConnection, hostedRoom.getJid());
                if (!room.isJoined()) {
                    logger.debug("joining room {} {}", hostedRoom.getName(), hostedRoom.getJid());
                    room.join(hipChatSettings.getNickname());
                }
                joinedRooms.put(hostedRoom.getJid(), room);
            }
        }
    }

    @Override
    public void sendMessage(String jid, String message) {
        if (jid.contains(GROUP_SERVICE_NAME)) {
            logger.debug("sending message to room");
            try {
                joinedRooms.get(jid.replaceAll("/.*$", "")).sendMessage(message);
            } catch (Exception e) {
                logger.warn("Could not send message to room {}, {}", jid, e);
            }
        } else {
            logger.debug("sending message to user");
            try {
                xmppConnection.getChatManager().createChat(jid, null).sendMessage(message);
            } catch (Exception e) {
                logger.warn("Could not send message to user {}, {}", jid, e);
            }
        }
    }
}
