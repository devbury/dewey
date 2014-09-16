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
import devbury.dewey.hipchat.api.model.UserEntry;
import devbury.dewey.model.Group;
import devbury.dewey.model.User;
import mockit.*;
import mockit.integration.junit4.JMockit;
import org.jivesoftware.smack.packet.Message;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(JMockit.class)
public class MessagePacketListenerTest {

    @Tested
    MessagePacketListener messagePacketListener;

    @Cascading LoggerFactory loggerFactory;

    @Test
    public void getPacketTypeFilter() {
        assertTrue(messagePacketListener.getPacketTypeFilter().accept(new Message()));
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

        messagePacketListener.handlePacket(message);
    }

    @Test
    public void handlePacketChatInvalidFromFormat() {
        Message message = new Message();
        message.setFrom("fromsomeroom/Robot Dewey");
        message.setBody("some message");
        message.setType(Message.Type.chat);

        messagePacketListener.handlePacket(message);

        // verify that an error message was logged
        new Verifications() {
            {
                LoggerFactory.getLogger(MessagePacketListener.class).error(anyString, any);
            }
        };
    }

    @Test
    public void handlePacketChat(@Mocked UserManager userManager) {
        UserEntry userEntry = new UserEntry();
        userEntry.setName("name");
        userEntry.setMentionName("mention_name");

        new NonStrictExpectations() {
            {
                userManager.findUserEntryById("900");
                result = userEntry;
            }
        };

        messagePacketListener.setUserManager(userManager);

        final ArrayList<String> called = new ArrayList<>();

        ApplicationEventPublisher applicationEventPublisher = new ApplicationEventPublisher() {
            @Override
            public void publishEvent(ApplicationEvent event) {
                called.add("publishEvent called");
                MessageEvent messageEvent = (MessageEvent) event;
                devbury.dewey.model.Message message = messageEvent.getMessage();
                assertTrue(message.isToMe());
                User user = message.getFrom();
                assertEquals("name", user.getName());
                assertEquals("body", message.getBody());
            }
        };

        messagePacketListener.setEventPublisher(applicationEventPublisher);

        HipChatSettings hipChatSettings = new HipChatSettings();
        hipChatSettings.setMentionName("my_mention_name");

        messagePacketListener.setHipChatSettings(hipChatSettings);

        Message message = new Message();
        message.setFrom("group_900@chat.hipchat.com/morestuff");
        message.setBody("body");
        message.setType(Message.Type.chat);

        messagePacketListener.handlePacket(message);
        assertEquals(1, called.size());
    }

    @Test
    public void handlePacketGroupChatInvalidFromFormat() {
        Message message = new Message();
        message.setFrom("fromsomeroom/Robot Dewey");
        message.setBody("some message");
        message.setType(Message.Type.groupchat);

        messagePacketListener.handlePacket(message);

        // verify that an error message was logged
        new Verifications() {
            {
                LoggerFactory.getLogger(MessagePacketListener.class).error(anyString, any);
            }
        };
    }

    @Test
    public void handlePacketGroupChat(@Mocked UserManager userManager) {
        UserEntry userEntry = new UserEntry();
        userEntry.setName("Some User");
        userEntry.setMentionName("mention_name");

        new NonStrictExpectations() {
            {
                userManager.findUserEntryByName("Some User");
                result = userEntry;
            }
        };

        messagePacketListener.setUserManager(userManager);

        final ArrayList<String> called = new ArrayList<>();

        ApplicationEventPublisher applicationEventPublisher = new ApplicationEventPublisher() {
            @Override
            public void publishEvent(ApplicationEvent event) {
                called.add("publishEvent called");
                MessageEvent messageEvent = (MessageEvent) event;
                devbury.dewey.model.Message message = messageEvent.getMessage();
                assertTrue(message.isToGroup());
                Group group = message.getGroup();
                assertEquals("group", group.getName());
                User user = message.getFrom();
                assertEquals("Some User", user.getName());
                assertEquals("@my_mention_name body", message.getBody());
                assertTrue(message.isDirectedToMe());
            }
        };

        messagePacketListener.setEventPublisher(applicationEventPublisher);

        HipChatSettings hipChatSettings = new HipChatSettings();
        hipChatSettings.setMentionName("my_mention_name");

        messagePacketListener.setHipChatSettings(hipChatSettings);

        Message message = new Message();
        message.setFrom("somestring_group@hipchat.com/Some User");
        message.setBody("@my_mention_name body");
        message.setType(Message.Type.groupchat);

        messagePacketListener.handlePacket(message);
        assertEquals(1, called.size());
    }
}
