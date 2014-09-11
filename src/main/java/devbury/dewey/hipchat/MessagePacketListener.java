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

import devbury.dewey.event.MessageEvent;
import devbury.dewey.model.MessageType;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class MessagePacketListener implements FilteredPacketListener<Message> {
    private static final Logger logger = LoggerFactory.getLogger(MessagePacketListener.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private HipChatSettings hipChatSettings;

    @Override
    public PacketTypeFilter getPacketTypeFilter() {
        return new PacketTypeFilter(Message.class);
    }

    @Override
    public void handlePacket(Message packet) {
        if (packet.getFrom().endsWith("/" + hipChatSettings.getNickname())) {
            logger.debug("not processing my own message");
            return;
        }

        logger.debug("Packet is " + packet.toXML());

        if (packet.getBody() == null) {
            logger.debug("not processing message with no body");
            return;
        }

        devbury.dewey.model.Message message = new devbury.dewey.model.Message();

        switch (packet.getType()) {
            case chat:
                message.setMessageType(MessageType.CHAT);
                message.setToMe(true);
                break;
            case groupchat:
                message.setMessageType(MessageType.GROUPCHAT);
                message.setToMe(false);
                break;
            default:
                // skip other types for now
                return;
        }
        message.setBody(packet.getBody());
        message.setTo(packet.getTo());
        message.setFrom(packet.getFrom());
        message.setMentionName(hipChatSettings.getMentionName());
        eventPublisher.publishEvent(new MessageEvent(message));
    }
}
