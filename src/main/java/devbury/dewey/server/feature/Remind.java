/*
 * Copyright 2014 devbury LLC
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

package devbury.dewey.server.feature;


import devbury.dewey.event.MessageEvent;
import devbury.dewey.event.MessageEventListener;
import devbury.dewey.model.Message;
import devbury.dewey.server.ChatServer;
import devbury.dewey.server.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin
public class Remind implements MessageEventListener {

    private static final Logger logger = LoggerFactory.getLogger(Remind.class);

    private Pattern pattern = Pattern.compile(
            "^remind +me +in +(\\d+) +(second|seconds|minute|minutes|hour|hours|day|days|week|weeks|month|months) +to +(.*)$");

    @Autowired
    private ChatServer chatServer;

    @Autowired
    private TaskScheduler taskScheduler;

    @Override
    public void onEvent(MessageEvent event) {
        Message message = event.getMessage();
        if (message.isCommand()) {
            Matcher matcher = pattern.matcher(message.getCommand());
            if (matcher.matches()) {
                final long timeUnits = Long.parseLong(matcher.group(1));
                ChronoUnit units = ChronoUnit.SECONDS;
                switch (matcher.group(2)) {
                    case "weeks":
                    case "week":
                        units = ChronoUnit.WEEKS;
                        break;
                    case "months":
                    case "month":
                        units = ChronoUnit.MONTHS;
                        break;
                    case "days":
                    case "day":
                        units = ChronoUnit.DAYS;
                        break;
                    case "hours":
                    case "hour":
                        units = ChronoUnit.HOURS;
                        break;
                    case "minutes":
                    case "minute":
                        units = ChronoUnit.MINUTES;
                        break;
                }
                final String body = "You asked me to remind you to " + matcher.group(3).replaceAll(" my ",
                        " your ").replaceAll(" me ", " you ").replaceAll(" [Ii] ", " you ");
                final String from = message.getFrom();
                Date date = Date.from(Instant.now().plus(timeUnits, units));
                taskScheduler.schedule(new Runnable() {
                    @Override
                    public void run() {
                        chatServer.sendMessageToUser(from, body);
                    }
                }, date);
                chatServer.sendMessageToUser(from, "Don't worry,  I'll remind you");
                logger.debug("processedMessage");
                return;
            }
        }
        logger.debug("did not process message {}", message.getBody());
    }
}
