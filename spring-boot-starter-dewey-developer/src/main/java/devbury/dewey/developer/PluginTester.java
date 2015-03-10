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

import devbury.dewey.core.event.MessageEvent;
import devbury.dewey.core.model.Address;
import devbury.dewey.core.model.Group;
import devbury.dewey.core.model.Message;
import devbury.dewey.core.server.ChatServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;

import static devbury.dewey.developer.Addresses.*;

public class PluginTester implements CommandLineRunner, ChatServer {

    private static final Logger logger = LoggerFactory.getLogger(PluginTester.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    private ArrayList<MessageFromDewey> messages = new ArrayList<>();

    public void sendMessage(Address address, String message) {
        logger.debug("Sent {}", message);
        messages.add(new MessageFromDewey(address, message));
    }

    @Override
    public void run(String... args) throws Exception {
        // Replaces Runner setup through starter
    }

    public void sendMessageToGroup(String groupName, String message) {
        sendMessageToGroup(new Group(groupName), message);
    }

    public void sendMessageToGroup(String message) {
        sendMessageToGroup(DEFAULT_GROUP, message);
    }

    public void sendMessageToGroup(Group group, String message) {
        eventPublisher.publishEvent(new MessageEvent(
                new Message(group, DEVELOPER, message, DEWEY.getMentionName())));
    }

    public void sendMessageToDewey(String message) {
        eventPublisher.publishEvent(new MessageEvent(
                new Message(null, DEVELOPER, message, DEWEY.getMentionName())));
    }

    public void clearMessages() {
        messages = new ArrayList<>();
    }

    public ArrayList<MessageFromDewey> messages() {
        return messages;
    }

    public MessageFromDewey message(int index) {
        return messages().get(index);
    }
}
