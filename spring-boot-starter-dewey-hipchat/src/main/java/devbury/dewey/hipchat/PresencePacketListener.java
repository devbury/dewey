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

import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Presence;

public class PresencePacketListener extends FilteredPacketListener<Presence> {

    @Override
    public PacketTypeFilter getPacketTypeFilter() {
        return new PacketTypeFilter(Presence.class);
    }

    @Override
    public void handlePacket(Presence packet) {
    }
}
