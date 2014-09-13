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

import devbury.dewey.hipchat.api.Api;
import devbury.dewey.hipchat.api.model.UserEntries;
import devbury.dewey.hipchat.api.model.UserEntry;
import devbury.dewey.hipchat.api.model.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class UserManager {

    private static Logger logger = LoggerFactory.getLogger(UserManager.class);
    private static final long THIRTY_MINUTES = 1000 * 60 * 30;

    @Autowired
    private Api api;

    private HashMap<String, UserEntry> byId = new HashMap<>();
    private HashMap<String, UserEntry> byName = new HashMap<>();
    private HashMap<String, UserEntry> byMentionName = new HashMap<>();

    @Scheduled(fixedDelay = THIRTY_MINUTES)
    public void configureCaches() {
        logger.debug("loading cache");
        UserEntries userEntries = api.findUserEntries();
        HashMap<String, UserEntry> buildById = new HashMap<>();
        HashMap<String, UserEntry> buildByName = new HashMap<>();
        HashMap<String, UserEntry> buildByMentionName = new HashMap<>();

        for (UserEntry userEntry : userEntries.getUserEntries()) {
            buildById.put(userEntry.getId(), userEntry);
            buildByName.put(userEntry.getName(), userEntry);
            buildByMentionName.put(userEntry.getMentionName(), userEntry);
        }

        byId = buildById;
        byName = buildByName;
        byMentionName = buildByMentionName;
        logger.debug("finished loading cache with {} users", userEntries.getUserEntries().size());
    }

    UserEntry findUserEntryById(String id) {
        return byId.get(id);
    }

    UserEntry findUserEntryByName(String name) {
        return byName.get(name);
    }

    UserEntry findUserEntryByMentionName(String mentionName) {
        return byMentionName.get(mentionName);
    }

    UserInfo findUserInfoById(String id) {
        return api.findById(id);
    }

    UserInfo findUserInfoByName(String name) {
        return api.findById(findUserEntryByName(name).getId());
    }

    UserInfo findUserInfoByMentionName(String mentionName) {
        return api.findById(findUserEntryByMentionName(mentionName).getId());
    }

    UserInfo findUserInfoByEmail(String email) {
      return api.findByEmail(email);
    }
}
