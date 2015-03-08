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
import devbury.dewey.core.model.Message;
import devbury.dewey.core.model.User;
import devbury.dewey.core.server.ChatServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class ConsoleServer implements ChatServer {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void sendMessage(Address address, String message) {
        if ("Dewey".equals(address.getName())) {
            User from = new User("developer", "developer");
            eventPublisher.publishEvent(new MessageEvent(new Message(null, from, message, "Dewey")));
        } else {
            System.out.println("[" + address.getAddressType() + " " + address.getName() + "] " + message);
        }
    }
}
