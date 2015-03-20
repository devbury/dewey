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

package devbury.dewey.core.server;

import devbury.dewey.core.model.Address;
import devbury.dewey.core.model.Message;

public interface ChatServer {
    void sendMessage(Address address, String message);

    default void respondToMessage(Message message, String response) {
        String mention = message.isToGroup() ? message.getFrom().getMentionName() + " " : "";
        Address replyTo = message.isToGroup() ? message.getGroup() : message.getFrom();
        sendMessage(replyTo, mention + response);
    }
}
