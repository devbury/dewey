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
 *    WITHOUT WARRANTIE;S OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package devbury.dewey.hipchat;

import devbury.dewey.core.model.Group;
import devbury.dewey.core.model.User;
import devbury.dewey.hipchat.api.model.UserEntry;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;

@RunWith(JMockit.class)
public class HipChatServerTest {

    @Test
    public void stop(@Mocked XMPPConnection xmppConnection) throws Exception {
        HipChatServer hipChatServer = new HipChatServer();
        hipChatServer.setXmppConnection(xmppConnection);
        hipChatServer.stop();

        new Verifications() {
            {
                xmppConnection.disconnect();
            }
        };
    }

    @Test
    public void sendMessageGroup(@Mocked MultiUserChat multiUserChat) throws XMPPException {
        HipChatServer hipChatServer = new HipChatServer();
        hipChatServer.setJoinedRooms(Collections.singletonMap("group", multiUserChat));

        Group groupAddress = new Group("group");

        hipChatServer.sendMessage(groupAddress, "message");

        new Verifications() {
            {
                multiUserChat.sendMessage("message");
            }
        };
    }

    @Test
    public void sendMessageUser(@Mocked UserManager userManager, @Mocked XMPPConnection xmppConnection) throws
            XMPPException {
        HipChatServer hipChatServer = new HipChatServer();
        hipChatServer.setUserManager(userManager);
        hipChatServer.setXmppConnection(xmppConnection);
        User userAddress = new User("user", "@User");
        UserEntry userEntry = new UserEntry();
        userEntry.setId("1");

        new NonStrictExpectations() {
            {
                userManager.findUserEntryByName("user");
                result = userEntry;
                userManager.findXmppJidById("1");
                result = "xmppjid";
            }
        };

        hipChatServer.sendMessage(userAddress, "message");

        new Verifications() {
            {
                xmppConnection.getChatManager().createChat("xmppjid", null).sendMessage("message");
            }
        };
    }
}
