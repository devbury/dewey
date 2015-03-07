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

    private HashMap<String, UserEntry> userEntryById = new HashMap<>();
    private HashMap<String, UserEntry> userEntryByName = new HashMap<>();
    private HashMap<String, UserEntry> userEntryByMentionName = new HashMap<>();
    private HashMap<String, String> xmppJidById = new HashMap<>();

    @Scheduled(fixedDelay = THIRTY_MINUTES)
    public void configureCaches() {
        logger.debug("loading cache");
        UserEntries userEntries = api.findUserEntries();
        HashMap<String, UserEntry> buildById = new HashMap<>();
        HashMap<String, UserEntry> buildByName = new HashMap<>();
        HashMap<String, UserEntry> buildByMentionName = new HashMap<>();

        userEntries.getUserEntries().forEach(u -> {
            buildById.put(u.getId(), u);
            buildByName.put(u.getName(), u);
            buildByMentionName.put(u.getMentionName(), u);
        });

        userEntryById = buildById;
        userEntryByName = buildByName;
        userEntryByMentionName = buildByMentionName;
        logger.debug("finished loading cache with {} users", userEntries.getUserEntries().size());
    }

    protected String findXmppJidById(String id) {
        String xmppJid = xmppJidById.get(id);
        if (xmppJid == null) {
            UserInfo userInfo = findUserInfoById(id);
            xmppJid = userInfo.getXmppJid();
            addToXmppJidCache(id, xmppJid);
        }
        return xmppJid;
    }

    protected synchronized void addToXmppJidCache(String id, String xmppJid) {
        if (xmppJidById.get(id) == null) {
            xmppJidById.put(id, xmppJid);
        }
    }

    public UserEntry findUserEntryById(String id) {
        return userEntryById.get(id);
    }

    public UserEntry findUserEntryByName(String name) {
        return userEntryByName.get(name);
    }

    public UserEntry findUserEntryByMentionName(String mentionName) {
        return userEntryByMentionName.get(mentionName);
    }

    public UserInfo findUserInfoById(String id) {
        UserInfo userInfo = api.findById(id);
        if (xmppJidById.get(id) == null) {
            addToXmppJidCache(id, userInfo.getXmppJid());
        }
        return userInfo;
    }

    public UserInfo findUserInfoByName(String name) {
        return findUserInfoById(findUserEntryByName(name).getId());
    }

    public UserInfo findUserInfoByMentionName(String mentionName) {
        return findUserInfoById(findUserEntryByMentionName(mentionName).getId());
    }

    public UserInfo findUserInfoByEmail(String email) {
        return api.findByEmail(email);
    }
}
