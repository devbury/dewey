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

package devbury.dewey.plugins.help;

import devbury.dewey.core.event.MessageEvent;
import devbury.dewey.core.event.MessageEventListener;
import devbury.dewey.core.model.Message;
import devbury.dewey.core.server.ChatServer;
import devbury.dewey.core.server.Plugin;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Help plugin that provides the user with information about how to use other plugins in the system.
 */
@Plugin
public class Help implements MessageEventListener {

    private final Pattern pattern = Pattern.compile("^help +(\\w*)(.*)$");

    @Autowired
    private List<Usage> usages;

    @Autowired
    private ChatServer chatServer;

    @Override
    public void onEvent(MessageEvent event) {
        Message message = event.getMessage();
        if (message.isCommand()) {
            if ("help".equals(message.getCommand())) {
                String response = usages.stream()
                        .flatMap(hp -> hp.usageSummary().getCommands().stream())
                        .distinct()
                        .collect(Collectors.joining(", ", "I know about these commands: ", ""));

                chatServer.respondToMessage(message, response);
            } else {
                Matcher matcher = pattern.matcher(message.getCommand());
                if (matcher.matches()) {
                    String command = matcher.group(1);
                    String args = matcher.group(2);
                    usages.stream()
                            .filter(hp -> hp.usageSummary().getCommands().contains(command))
                            .forEach(hp -> chatServer.respondToMessage(message, hp.usageSummary().getTitle() + "\n" +
                                    hp.usageDetails(command, args)));
                }
            }
        }
    }
}