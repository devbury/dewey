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
import mockit.Expectations;
import mockit.Mocked;
import mockit.NonStrictExpectations;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JMockit.class)
public class HipChatServerTest {

    @Test
    public void stop(@Mocked XMPPConnection xmppConnection) throws Exception {
        HipChatServer victim = new HipChatServer();
        victim.setXmppConnection(xmppConnection);
        victim.stop();

        new Verifications() {
            {
                xmppConnection.disconnect();
            }
        };
    }

    @Test
    public void sendMessageGroup(@Mocked MultiUserChat multiUserChat) throws XMPPException {
        HipChatServer victim = new HipChatServer();
        victim.setJoinedRoomsByName(Collections.singletonMap("group", multiUserChat));

        Group groupAddress = new Group("group");

        victim.sendMessage(groupAddress, "message");

        new Verifications() {
            {
                multiUserChat.sendMessage("message");
            }
        };
    }

    @Test
    public void sendMessageUser(@Mocked UserManager userManager, @Mocked XMPPConnection xmppConnection) throws
            XMPPException {
        HipChatServer victim = new HipChatServer();
        victim.setUserManager(userManager);
        victim.setXmppConnection(xmppConnection);
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

        victim.sendMessage(userAddress, "message");

        new Verifications() {
            {
                xmppConnection.getChatManager().createChat("xmppjid", null).sendMessage("message");
            }
        };
    }

    @Test
    public void canJoinGroupWhenNoGroupsSet(@Mocked MultiUserChat multiUserChat, @Mocked HostedRoom hostedRoom) throws
            Exception {
        HipChatServer victim = new HipChatServer();
        HipChatSettings hipChatSettings = new HipChatSettings();
        victim.setHipChatSettings(hipChatSettings);
        HashMap<String, MultiUserChat> joinedRoomsByName = new HashMap<>();
        victim.setJoinedRoomsByName(joinedRoomsByName);

        new Expectations() {
            {
                MultiUserChat.getHostedRooms(null, HipChatServer.GROUP_SERVICE_NAME);
                result = Collections.singletonList(hostedRoom);

                hostedRoom.getName();
                result = "room";
            }
        };

        victim.joinRooms();

        assertEquals(1, joinedRoomsByName.size());
        assertTrue(joinedRoomsByName.containsKey("room"));
    }

    @Test
    public void cantJoinGroupWhenNotSpecified(@Mocked MultiUserChat multiUserChat, @Mocked HostedRoom hostedRoom) throws
            Exception {
        HipChatServer victim = new HipChatServer();
        HipChatSettings hipChatSettings = new HipChatSettings();
        hipChatSettings.setGroupsToJoin("goodGroup");
        victim.setHipChatSettings(hipChatSettings);
        HashMap<String, MultiUserChat> joinedRoomsByName = new HashMap<>();
        victim.setJoinedRoomsByName(joinedRoomsByName);

        new Expectations() {
            {
                MultiUserChat.getHostedRooms(null, HipChatServer.GROUP_SERVICE_NAME);
                result = Collections.singletonList(hostedRoom);

                hostedRoom.getName();
                result = "badGroup";
            }
        };

        victim.joinRooms();

        assertEquals(0, joinedRoomsByName.size());
    }


}
