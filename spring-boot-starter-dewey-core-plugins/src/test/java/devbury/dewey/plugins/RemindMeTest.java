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

import devbury.dewey.developer.PluginTest;
import org.junit.Test;
import org.springframework.boot.test.SpringApplicationConfiguration;

@SpringApplicationConfiguration(classes = RemindMeTest.class)
public class RemindMeTest extends PluginTest {

    @Test
    public void messageFromUser() throws Exception {
        sendMessageToDewey("remind me in 1 second to send my message");
        sleep();
        assertUserResponseEquals(" Sure, I'll remind you");
        assertUserResponseEquals(" You asked me to remind you to send your message");
        assertResponseCountEquals(2);
    }

    @Test
    public void messageFromUserInGroupChatMentioningDewey() throws Exception {
        sendMessageToGroup("@dewey remind us in 1 second to send my message");
        sleep();
        assertGroupResponseEquals("@user Sure, I'll remind everyone");
        assertGroupResponseEquals("@All, @user asked me to remind everyone to 'send my message'");
        assertResponseCountEquals(2);
    }

    @Test
    public void messageFromUserInGroupChatNotMentioningDewey() throws Exception {
        sendMessageToGroup("remind us in 1 second to send my message");
        sleep();
        assertResponseCountEquals(0);
    }

    @Test
    public void messageFromUserInGroupChat() throws Exception {
        sendMessageToGroup("@dewey remind me in 1 second to send my message");
        sleep();
        assertGroupResponseEquals("@user Sure, I'll remind you");
        assertGroupResponseEquals("@user You asked me to remind you to send your message");
        assertResponseCountEquals(2);
    }

    private void sleep() throws Exception {
        Thread.sleep(1010);
    }
}
