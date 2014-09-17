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

package devbury.dewey.hipchat.api;

import devbury.dewey.hipchat.HipChatSettings;
import devbury.dewey.hipchat.api.model.UserEntries;
import devbury.dewey.hipchat.api.model.UserInfo;
import mockit.Cascading;
import mockit.Mocked;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpRequest;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Collections;

@RunWith(JMockit.class)
public class ApiTest {

    @Mocked
    RestTemplate restTemplate;

    @Cascading
    HttpRequest httpRequest;

    @Test
    public void intercept() throws IOException {
        HipChatSettings hipChatSettings = new HipChatSettings();
        hipChatSettings.setApiToken("TOKEN");
        Api api = new Api();
        api.setHipChatSettings(hipChatSettings);

        api.init();
        api.intercept(httpRequest, null, (request, body) -> null);

        new Verifications() {
            {
                restTemplate.setInterceptors(Collections.singletonList(api));
                httpRequest.getHeaders().add("Authorization", "Bearer TOKEN");
            }
        };
    }

    @Test
    public void findUserEntries() {
        HipChatSettings hipChatSettings = new HipChatSettings();
        hipChatSettings.setApiToken("TOKEN");
        Api api = new Api();
        api.setHipChatSettings(hipChatSettings);

        api.init();
        api.findUserEntries();

        new Verifications() {
            {
                restTemplate.getForObject("https://api.hipchat.com/v2/user?maxResults=1000", UserEntries.class);
            }
        };
    }

    @Test
    public void findById() {
        HipChatSettings hipChatSettings = new HipChatSettings();
        hipChatSettings.setApiToken("TOKEN");
        Api api = new Api();
        api.setHipChatSettings(hipChatSettings);
        api.init();

        api.findById("ID");

        new Verifications() {
            {
                restTemplate.getForObject("https://api.hipchat.com/v2/user/{key}", UserInfo.class, "ID");
            }
        };
    }
}
