/*
 * Copyright 2015 devbury LLC
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

import devbury.dewey.core.event.MessageEvent;
import devbury.dewey.core.model.Group;
import devbury.dewey.core.model.User;
import devbury.dewey.hipchat.api.model.UserEntry;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MessagePacketListener extends FilteredPacketListener<Message> {

    private final Pattern chatFrom = Pattern.compile("^.*_([0-9]*).*$");

    private final Pattern groupFrom = Pattern.compile("^.*_(.*)@.*/(.*)$");

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserManager userManager;

    @Override
    public PacketTypeFilter getPacketTypeFilter() {
        return new PacketTypeFilter(Message.class);
    }

    @Override
    public void handlePacket(Message packet) {
        logger.debug("Packet is {}", packet.toXML());

        if (packet.getBody() == null) {
            logger.debug("not processing message with no body");
            return;
        }

        switch (packet.getType()) {
            case chat:
                processChatPacket(packet);
                break;
            case groupchat:
                processGroupChatPacket(packet);
                break;
        }
    }

    protected void processChatPacket(Message packet) {
        Matcher fromMatcher = chatFrom.matcher(packet.getFrom());

        if (!fromMatcher.matches()) {
            logger.error("chat packet from does not match expected format {}", packet.getFrom());
            return;
        }

        UserEntry userEntry = userManager.findUserEntryById(fromMatcher.group(1));
        User fromUser = new User(userEntry.getName(), userEntry.getMentionName());
        devbury.dewey.core.model.Message message = new devbury.dewey.core.model.Message(null, fromUser,
                packet.getBody(), hipChatSettings.getMentionName());
        eventPublisher.publishEvent(new MessageEvent(message));
    }

    protected void processGroupChatPacket(Message packet) {
        Matcher fromMatcher = groupFrom.matcher(packet.getFrom());

        if (!fromMatcher.matches()) {
            logger.error("groupchat packet from does not match expected format {}", packet.getFrom());
            return;
        }

        Group group = new Group(fromMatcher.group(1));
        UserEntry userEntry = userManager.findUserEntryByName(fromMatcher.group(2));
        User fromUser = new User(userEntry.getName(), userEntry.getMentionName());
        devbury.dewey.core.model.Message message = new devbury.dewey.core.model.Message(group, fromUser,
                packet.getBody(), hipChatSettings.getMentionName());
        eventPublisher.publishEvent(new MessageEvent(message));
    }

    void setEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}
