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

import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class FilteredPacketListenerTest {

    @Test
    public void processPacketSkipMyPackets() {

        FilteredPacketListener<Packet> filteredPacketListener = new FilteredPacketListener<Packet>() {
            @Override
            public PacketTypeFilter getPacketTypeFilter() {
                return null;
            }

            @Override
            public void handlePacket(Packet packet) {
                fail("Packet should not be handled");
            }
        };

        HipChatSettings hipChatSettings = new HipChatSettings();
        hipChatSettings.setName("Robot Dewey");
        filteredPacketListener.setHipChatSettings(hipChatSettings);

        Packet p = new Packet() {
            @Override
            public String toXML() {
                return null;
            }
        };
        p.setFrom("SomeInfo/Robot Dewey");

        filteredPacketListener.processPacket(p);
    }

    @Test
    public void processPacket() {
        FilteredPacketListener<Packet> filteredPacketListener = new FilteredPacketListener<Packet>() {
            @Override
            public PacketTypeFilter getPacketTypeFilter() {
                return null;
            }

            @Override
            public void handlePacket(Packet packet) {
                packet.setTo("testto");
            }
        };

        HipChatSettings hipChatSettings = new HipChatSettings();
        hipChatSettings.setName("Robot Dewey");
        filteredPacketListener.setHipChatSettings(hipChatSettings);

        Packet p = new Packet() {
            @Override
            public String toXML() {
                return null;
            }
        };
        p.setFrom("SomeInfo/Not Dewey");

        filteredPacketListener.processPacket(p);
        assertEquals("testto", p.getTo());
    }
}
