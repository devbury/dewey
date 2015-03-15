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

package devbury.dewey.hipchat;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;

public class HipChatSettingsTest {

    @Test
    public void groupsToJoinFormat() {
        HipChatSettings victim = new HipChatSettings();
        victim.setGroupsToJoin("  g1,g2,   g3 ,  g4,  g5");

        List<String> groupsToJoin = victim.getGroupsToJoin();

        Arrays.asList("g1", "g2", "g3", "g4", "g5")
                .forEach(g -> assertTrue(g + " not found", groupsToJoin.contains(g)));
    }

}