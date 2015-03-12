/*
 * Copyright 2015 devbury LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package devbury.dewey.developer;

import devbury.dewey.core.model.Group;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static devbury.dewey.developer.Addresses.DEFAULT_GROUP;
import static devbury.dewey.developer.Addresses.USER;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@EnableAutoConfiguration
@Import(TestFrameworkConfiguration.class)
public abstract class PluginTest {

    private int messageIndex = 0;

    @Autowired
    private PluginTester pt;

    @Before
    public void init() {
        pt.clearMessages();
        messageIndex = 0;
    }

    public void sendMessageToGroup(String groupName, String message) {
        sendMessageToGroup(new Group(groupName), message);
    }

    public void sendMessageToGroup(String message) {
        sendMessageToGroup(DEFAULT_GROUP, message);
    }

    public void sendMessageToGroup(Group group, String message) {
        pt.sendMessageToGroup(group, message);
    }

    public void sendMessageToDewey(String message) {
        pt.sendMessageToDewey(message);
    }

    public void assertUserResponseEquals(int messageId, Object value) {
        assertEquals("Message was not sent to the developer", USER, pt.message(messageId).address());
        assertEquals(value, pt.message(messageId).body());
    }

    public void assertUserResponseEquals(Object value) {
        assertUserResponseEquals(messageIndex++, value);
    }

    public void assertGroupResponseEquals(Group group, int messageId, Object value) {
        assertEquals("message was not sent to the group", group, pt.message(messageId).address());
        assertEquals(value, pt.message(messageId).body());
    }

    public void assertGroupResponseEquals(String groupName, int messageId, Object value) {
        assertGroupResponseEquals(new Group(groupName), messageId, value);
    }

    public void assertGroupResponseEquals(Object value) {
        assertGroupResponseEquals(DEFAULT_GROUP, messageIndex++, value);
    }

    public void assertResponseCountEquals(int size) {
        assertEquals("Response count is different than expected", size, pt.messages().size());
    }

}
