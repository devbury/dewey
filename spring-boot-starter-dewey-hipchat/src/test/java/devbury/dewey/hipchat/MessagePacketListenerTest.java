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
import devbury.dewey.hipchat.api.model.UserEntry;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.jivesoftware.smack.packet.Message;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;

import static org.junit.Assert.*;

@RunWith(JMockit.class)
public class MessagePacketListenerTest {

    MessagePacketListener victim;

    @Mocked
    LoggerFactory loggerFactory;

    @Before
    public void before() {
        victim = new MessagePacketListener();
    }

    @Test
    public void getPacketTypeFilter() {
        assertTrue(victim.getPacketTypeFilter().accept(new Message()));
    }

    @Test
    public void handlePacketNoBody() {
        Message message = new Message() {
            @Override
            public Type getType() {
                fail("getType should not be called");
                return null;
            }
        };
        message.setBody(null);

        victim.handlePacket(message);
    }

    @Test
    public void handlePacketChatInvalidFromFormat() {
        Message message = new Message();
        message.setFrom("fromsomeroom/Robot Dewey");
        message.setBody("some message");
        message.setType(Message.Type.chat);

        victim.handlePacket(message);

        // verify that an error message was logged
        new Verifications() {
            {
                LoggerFactory.getLogger(MessagePacketListener.class).error(anyString, any);
            }
        };
    }

    @Test
    public void handlePacketChat(@Mocked UserManager userManager, @Mocked ApplicationEventPublisher eventPublisher) {
        UserEntry userEntry = new UserEntry();
        userEntry.setName("name");
        userEntry.setMentionName("mention_name");

        new NonStrictExpectations() {
            {
                userManager.findUserEntryById("900");
                result = userEntry;
            }
        };

        victim.setUserManager(userManager);
        victim.setEventPublisher(eventPublisher);

        HipChatSettings hipChatSettings = new HipChatSettings();
        hipChatSettings.setMentionName("my_mention_name");

        victim.setHipChatSettings(hipChatSettings);

        Message message = new Message();
        message.setFrom("group_900@chat.hipchat.com/morestuff");
        message.setBody("body");
        message.setType(Message.Type.chat);

        victim.handlePacket(message);

        new Verifications() {
            {
                MessageEvent e;
                eventPublisher.publishEvent(e = withCapture());
                assertTrue(e.getMessage().isToMe());
                assertEquals("name", e.getMessage().getFrom().getName());
                assertEquals("body", e.getMessage().getBody());
            }
        };
    }

    @Test
    public void handlePacketGroupChatInvalidFromFormat() {
        Message message = new Message();
        message.setFrom("fromsomeroom/Robot Dewey");
        message.setBody("some message");
        message.setType(Message.Type.groupchat);

        victim.handlePacket(message);

        // verify that an error message was logged
        new Verifications() {
            {
                LoggerFactory.getLogger(MessagePacketListener.class).error(anyString, any);
            }
        };
    }

    @Test
    public void handlePacketGroupChat(@Mocked UserManager userManager, @Mocked ApplicationEventPublisher eventPublisher) {
        UserEntry userEntry = new UserEntry();
        userEntry.setName("Some User");
        userEntry.setMentionName("mention_name");

        new NonStrictExpectations() {
            {
                userManager.findUserEntryByName("Some User");
                result = userEntry;
            }
        };

        victim.setUserManager(userManager);
        victim.setEventPublisher(eventPublisher);

        HipChatSettings hipChatSettings = new HipChatSettings();
        hipChatSettings.setMentionName("my_mention_name");

        victim.setHipChatSettings(hipChatSettings);

        Message message = new Message();
        message.setFrom("somestring_group_name@hipchat.com/Some User");
        message.setBody("@my_mention_name body");
        message.setType(Message.Type.groupchat);

        victim.handlePacket(message);

        new Verifications() {
            {
                MessageEvent e;
                eventPublisher.publishEvent(e = withCapture());
                assertTrue(e.getMessage().isToGroup());
                assertEquals("group_name", e.getMessage().getGroup().getName());
                assertEquals("Some User", e.getMessage().getFrom().getName());
                assertEquals("@my_mention_name body", e.getMessage().getBody());
                assertTrue(e.getMessage().isDirectedToMe());
            }
        };
    }
}
