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

package devbury.dewey.hipchat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ConfigurationProperties(prefix = "hipchat")
public class HipChatSettings {

    private static final Logger logger = LoggerFactory.getLogger(HipChatSettings.class);

    private String email;
    private String password;
    private String apiToken;
    private String xmppJid;
    private String name;
    private String mentionName;
    private String resource = "DeweyServer";
    private String server = "chat.hipchat.com";
    private int port = 5222;
    private List<String> groupsToJoin = new ArrayList<>();

    @PostConstruct
    public void checkProperties() {
        Assert.hasText(email, "hipchat.email is not set!");
        Assert.hasText(password, "hipchat.password is not set!");
        Assert.hasText(apiToken, "hipchat.apiToken is not set!");
        if (!groupsToJoin.isEmpty()) {
            logger.info("{}", groupsToJoin.stream()
                    .collect(Collectors.joining(", ", "Only joining groups: ", "")));
        }
    }

    @Override
    public String toString() {
        return "HipChatSettings{" +
                "email='" + email + '\'' +
                ", xmppJid='" + xmppJid + '\'' +
                ", name='" + name + '\'' +
                ", mentionName='" + mentionName + '\'' +
                ", resource='" + resource + '\'' +
                ", server='" + server + '\'' +
                ", port=" + port +
                '}';
    }

    public String getMentionName() {
        return mentionName;
    }

    public void setMentionName(String mentionName) {
        this.mentionName = mentionName;
    }

    public String getPassword() {
        return password;
    }

    public String getResource() {
        return resource;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getApiToken() {
        return apiToken;
    }

    public void setApiToken(String apiToken) {
        this.apiToken = apiToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getXmppJid() {
        return xmppJid;
    }

    public void setXmppJid(String xmppJid) {
        this.xmppJid = xmppJid;
    }

    public List<String> getGroupsToJoin() {
        return groupsToJoin;
    }

    public void setGroupsToJoin(String groupsToJoin) {
        this.groupsToJoin = Arrays.asList(groupsToJoin.split(","))
                .stream()
                .map(String::trim)
                .collect(Collectors.toList());
    }
}
