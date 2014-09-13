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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collections;

@Component
public class Api implements ClientHttpRequestInterceptor {

    @Autowired
    private HipChatSettings hipChatSettings;

    private String authorization;
    private RestTemplate restTemplate;

    @PostConstruct
    public void init() {
        this.authorization = "Bearer " + hipChatSettings.getApiToken();
        restTemplate = new RestTemplate();
        restTemplate.setInterceptors(Collections.singletonList(this));
    }

    public UserInfo findByEmail(String email) {
        return findByKey(email);
    }

    @Cacheable("api.findById")
    public UserInfo findById(String id) {
        return findByKey(id);
    }

    public UserEntries findUserEntries() {
        return restTemplate.getForObject("https://api.hipchat.com/v2/user?maxResults=1000", UserEntries.class);
    }

    protected UserInfo findByKey(String key) {
        return restTemplate.getForObject("https://api.hipchat.com/v2/user/{key}", UserInfo.class, key);
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        HttpHeaders headers = request.getHeaders();
        headers.add("Authorization", authorization);
        return execution.execute(request, body);
    }
}
