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

package devbury.dewey.plugins;

import devbury.dewey.core.event.MessageEvent;
import devbury.dewey.core.model.Address;
import devbury.dewey.core.model.Group;
import devbury.dewey.core.model.Message;
import devbury.dewey.core.model.User;
import devbury.dewey.core.server.ChatServer;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.junit.Assert.assertEquals;

@RunWith(JMockit.class)
public class RemindMeTest {

    @Test
    public void onEventFromUser(@Mocked ChatServer chatServer) {
        User user = new User("fromUser", "@FromUser");
        Message message = new Message(null, user, "remind me in 1 minute to send my message", "@Dewey");

        MessageEvent event = new MessageEvent(message);
        RemindMe remindMe = new RemindMe() {
            @Override
            protected void scheduleMessage(Address replyTo, String message, Date notifyAt) {
                assertEquals(" You asked me to remind you to send your message", message);
                assertEquals(user, replyTo);
            }
        };

        remindMe.setChatServer(chatServer);
        remindMe.onEvent(event);

        new Verifications() {
            {
                chatServer.sendMessage(user, " Sure, I'll remind you");
            }
        };
    }

    @Test
    public void onEventFromGroup(@Mocked ChatServer chatServer) {
        User user = new User("fromUser", "@FromUser");
        Group group = new Group("group");
        Message message = new Message(group, user, "@Dewey remind us in 2 hours to send my message", "@Dewey");

        MessageEvent event = new MessageEvent(message);
        RemindMe remindMe = new RemindMe() {
            @Override
            protected void scheduleMessage(Address replyTo, String message, Date notifyAt) {
                assertEquals("@All, @FromUser asked me to remind everyone to 'send my message'", message);
                assertEquals(group, replyTo);
            }
        };

        remindMe.setChatServer(chatServer);
        remindMe.onEvent(event);

        new Verifications() {
            {
                chatServer.sendMessage(group, "@FromUser Sure, I'll remind everyone");
            }
        };
    }
}
