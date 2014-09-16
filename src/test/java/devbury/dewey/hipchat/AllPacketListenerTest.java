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

import mockit.Cascading;
import mockit.Verifications;
import mockit.integration.junit4.JMockit;
import org.jivesoftware.smack.packet.Packet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

@RunWith(JMockit.class)
public class AllPacketListenerTest {

    @Test
    public void getPacketTypeFilter() {
        Packet p = new Packet() {
            @Override
            public String toXML() {
                return null;
            }
        };
        assertTrue(new AllPacketListener().getPacketTypeFilter().accept(p));
    }

    @Test
    public void handlePacket(@Cascading LoggerFactory loggerFactory) {
        Packet p = new Packet() {
            @Override
            public String toXML() {
                return "myxml";
            }
        };
        new AllPacketListener().handlePacket(p);

        new Verifications() {
            {
                LoggerFactory.getLogger(AllPacketListener.class).trace(anyString, "myxml");
            }
        };
    }
}
