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

package devbury.dewey.hipchat;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@ConfigurationProperties(prefix = "hipchat")
public class HipChatSettings {

    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String apiToken;

    private String xmppJid;
    private String name;
    private String mentionName;
    private String resource = "DeweyServer";
    private String server = "chat.hipchat.com";
    private int port = 5222;

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
}
