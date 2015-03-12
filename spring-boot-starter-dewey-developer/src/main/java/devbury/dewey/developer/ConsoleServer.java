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
import devbury.dewey.core.model.AddressType;
import devbury.dewey.core.model.Group;
import devbury.dewey.core.model.Message;
import devbury.dewey.core.server.ChatServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

import static devbury.dewey.developer.Addresses.USER;
import static devbury.dewey.developer.Addresses.DEWEY;

public class ConsoleServer implements ChatServer {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void sendMessage(Address address, String message) {
        System.out.println("[" + address.getAddressType() + " " + address.getName() + "] " + message);
    }

    public void sendMessageFromUser(Address address, String message) {
        Group group = null;
        if (address.getAddressType() == AddressType.GROUP) {
            group = (Group) address;
        }
        eventPublisher.publishEvent(new MessageEvent(new Message(group, USER, message, DEWEY.getMentionName())));
    }
}
