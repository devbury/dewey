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

package devbury.dewey.core.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "dewey")
public class DeweySettings {
    public static final String DEWEY_SERVER = "dewey.server";

    private static final String DEFAULT_URL = "http://localhost:8080";
    private static final String DEFAULT_SERVER = "developer";

    private static final Logger logger = LoggerFactory.getLogger(DeweySettings.class);

    private String server = DEFAULT_SERVER;
    private String url = DEFAULT_URL;

    @PostConstruct
    public void checkProperties() {
        Assert.state(DEFAULT_SERVER.equals(server) || !DEFAULT_URL.equals(url), "dewey.url is not set!");
        logger.info("Dewey server: {}", getServer());
        logger.info("Dewey url: {}", getUrl());
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
