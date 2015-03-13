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

import devbury.dewey.core.server.ChatServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import static devbury.dewey.core.server.DeweySettings.DEWEY_SERVER;

@ConditionalOnProperty(value = DEWEY_SERVER, havingValue = "false", matchIfMissing = true)
public class Configuration {

    @Bean
    @ConditionalOnMissingBean(CommandLineRunner.class)
    public Runner runner() {
        return new Runner();
    }

    @Bean
    @ConditionalOnMissingBean(ChatServer.class)
    public ConsoleServer consoleServer() {
        return new ConsoleServer();
    }
}
