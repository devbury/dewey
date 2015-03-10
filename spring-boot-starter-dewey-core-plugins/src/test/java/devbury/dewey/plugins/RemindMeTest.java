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
import devbury.dewey.developer.PluginTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static devbury.dewey.developer.Addresses.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RemindMeTest.class)
@PluginTest
public class RemindMeTest {

    @Autowired
    PluginTester pluginTester;

    @Before
    public void init() {
        pluginTester.clearMessages();
    }

    @Test
    public void messageFromUser() throws Exception {
        pluginTester.sendMessageToDewey("remind me in 1 second to send my message");

        sleep();

        assertEquals(2, pluginTester.messages().size());

        assertEquals(DEVELOPER, pluginTester.message(0).address());
        assertEquals(" Sure, I'll remind you", pluginTester.message(0).body());

        assertEquals(DEVELOPER, pluginTester.message(1).address());
        assertEquals(" You asked me to remind you to send your message", pluginTester.message(1).body());
    }

    @Test
    public void messageFromUserInGroupChatMentioningDewey() throws Exception {
        pluginTester.sendMessageToGroup(DEWEY + " remind us in 1 second to send my message");

        sleep();

        assertEquals(2, pluginTester.messages().size());

        assertEquals(DEFAULT_GROUP, pluginTester.message(0).address());
        assertEquals(DEVELOPER + " Sure, I'll remind everyone", pluginTester.message(0).body());

        assertEquals(DEFAULT_GROUP, pluginTester.message(1).address());
        assertEquals("@All, " + DEVELOPER + " asked me to remind everyone to 'send my message'",
                pluginTester.message(1).body());
    }

    @Test
    public void messageFromUserInGroupChatNotMentioningDewey() throws Exception {
        pluginTester.sendMessageToGroup("remind us in 1 second to send my message");

        sleep();

        assertEquals(0, pluginTester.messages().size());
    }

    @Test
    public void messageFromUserInGroupChat() throws Exception {
        pluginTester.sendMessageToGroup(DEWEY + " remind me in 1 second to send my message");

        sleep();

        assertEquals(2, pluginTester.messages().size());

        assertEquals(DEFAULT_GROUP, pluginTester.message(0).address());
        assertEquals(DEVELOPER + " Sure, I'll remind you", pluginTester.message(0).body());

        assertEquals(DEFAULT_GROUP, pluginTester.message(1).address());
        assertEquals(DEVELOPER + " You asked me to remind you to send your message", pluginTester.message(1).body());
    }

    private void sleep() throws Exception {
        Thread.sleep(1010);
    }
}
