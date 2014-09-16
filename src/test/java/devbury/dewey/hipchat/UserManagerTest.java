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
import mockit.Cascading;
import mockit.Injectable;
import mockit.NonStrictExpectations;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JMockit.class)
public class UserManagerTest {

    @Tested
    UserManager userManager;

    @Injectable
    Api api;

    @Cascading
    LoggerFactory loggerFactory;


    @Test
    public void testFinders() {
        UserEntries userEntries = new UserEntries();
        UserEntry userEntry = new UserEntry();
        userEntry.setId("1");
        userEntry.setName("name");
        userEntry.setMentionName("mentionName");
        userEntries.setUserEntries(Collections.singletonList(userEntry));

        UserInfo userInfo = new UserInfo();
        userInfo.setMentionName("mentionName");
        userInfo.setName("name");
        userInfo.setEmail("email@example.com");
        userInfo.setId("1");
        userInfo.setPhotoUrl("http://photos.example.com/photo.jpg");
        userInfo.setTimezone("timezone");
        userInfo.setTitle("title");
        userInfo.setXmppJid("xmppjid");

        new NonStrictExpectations() {
            {
                api.findUserEntries();
                result = userEntries;

                api.findById("1");
                result = userInfo;

                api.findByEmail("email@example.com");
                result = userInfo;
            }
        };

        userManager.configureCaches();

        assertEquals("xmppjid", userManager.findXmppJidById("1"));
        assertEquals("name", userManager.findUserEntryById("1").getName());
        assertEquals("1", userManager.findUserEntryByName("name").getId());
        assertEquals("name", userManager.findUserEntryByMentionName("@mentionName").getName());
        assertEquals("name", userManager.findUserInfoByEmail("email@example.com").getName());
        assertEquals("@mentionName", userManager.findUserInfoById("1").getMentionName());
        assertEquals("1", userManager.findUserInfoByName("name").getId());
        assertEquals("email@example.com", userManager.findUserInfoByMentionName("@mentionName").getEmail());
    }

    @Test
    public void testScheduledAnnotationExists() throws NoSuchMethodException {
        assertTrue(UserManager.class.getMethod("configureCaches").isAnnotationPresent(Scheduled.class));
    }
}
