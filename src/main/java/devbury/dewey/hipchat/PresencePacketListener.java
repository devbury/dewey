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

import devbury.dewey.model.Status;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Presence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;

public class PresencePacketListener extends FilteredPacketListener<Presence> {

    private static final Logger logger = LoggerFactory.getLogger(PresencePacketListener.class);

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public PacketTypeFilter getPacketTypeFilter() {
        return new PacketTypeFilter(Presence.class);
    }

    @Override
    public void handlePacket(Presence packet) {
        Status status = new Status();
    }
}
