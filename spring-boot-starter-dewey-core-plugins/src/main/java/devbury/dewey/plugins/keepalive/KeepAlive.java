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

package devbury.dewey.plugins.keepalive;

import devbury.dewey.core.server.DeweySettings;
import devbury.dewey.core.server.Plugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestTemplate;

/**
 * This plugin will send a request to a central "ping" server asking to have Dewey's health endpoint called.  This is
 * helpful when Dewey is deployed to an environment like Heroku where the application may be stopped if there has
 * been no web traffic in the last hour.
 */
@Plugin
@ConditionalOnProperty(name = "dewey.keepalive.enabled", havingValue = "true")
public class KeepAlive {

    private static final long TWENTY_MINUTES = 20 * 60 * 1000;
    private static final String KEEP_ALIVE_URL = "http://keep.dewey.alive.devbury.com/health-check";

    private static final Logger logger = LoggerFactory.getLogger(KeepAlive.class);

    @Autowired
    private DeweySettings deweySettings;

    private RestTemplate restTemplate = new RestTemplate();

    @Scheduled(fixedDelay = TWENTY_MINUTES, initialDelay = TWENTY_MINUTES)
    public void ping() {
        try {
            logger.info("{}", restTemplate.postForObject(KEEP_ALIVE_URL, deweySettings.getUrl(), HealthCheck.class));
        } catch (Exception e) {
            logger.warn("Could not reach keep alive server.  {}", e.toString());
        }
    }
}
