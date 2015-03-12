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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static devbury.dewey.developer.Addresses.*;

public class Runner implements CommandLineRunner {

    private final Pattern withGroupName = Pattern.compile("^g +:(\\w+) +(.*)$");
    private final Pattern withoutGroupName = Pattern.compile("^g +(.*)$");

    @Autowired
    private ConfigurableApplicationContext applicationContext;

    @Autowired
    private ConsoleServer consoleServer;

    @Override
    public void run(String... strings) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        welcome();
        while (true) {
            System.out.print("console> ");
            String message = br.readLine();
            if ("exit".equals(message)) {
                break;
            }
            Matcher matcher = withGroupName.matcher(message);
            if (matcher.matches()) {
                String groupName = matcher.group(1);
                String messageBody = matcher.group(2);
                Group group = new Group(groupName);
                consoleServer.sendMessageFromUser(group, messageBody);
            } else {
                matcher = withoutGroupName.matcher(message);
                if (matcher.matches()) {
                    String messageBody = matcher.group(1);
                    consoleServer.sendMessageFromUser(DEFAULT_GROUP, messageBody);
                } else {
                    consoleServer.sendMessageFromUser(DEWEY, message);
                }
            }
        }
        applicationContext.close();
    }

    private void welcome() {
        System.out.println("\n-== PLUGIN DEVELOPER SERVER ==-\n");
        System.out.println("Users :");
        System.out.println("  " + USER);
        System.out.println("  " + DEWEY);
        System.out.println("\nEx.");
        System.out.println("  Send a message from user to dewey");
        System.out.println("    remind me in 10 seconds to attend the meeting");
        System.out.println("Ex.");
        System.out.println("  Send a message from user to the default group addressing dewey");
        System.out.println("    g @dewey remind us in 10 seconds to attend the meeting");
        System.out.println("Ex.");
        System.out.println("  Send a message from user to a named group addressing dewey");
        System.out.println("    g :group1 @dewey remind us in 10 seconds to attend the meeting");
        System.out.println("\n");
        System.out.println("Enter 'exit' to quit.");
    }
}
