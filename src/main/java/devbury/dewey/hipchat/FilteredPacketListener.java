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

import com.google.common.annotations.VisibleForTesting;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class FilteredPacketListener<E extends Packet> implements PacketListener {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected HipChatSettings hipChatSettings;

    public abstract PacketTypeFilter getPacketTypeFilter();

    public abstract void handlePacket(E packet);

    @SuppressWarnings("unchecked")
    public void processPacket(Packet packet) {
        if (packet.getFrom().endsWith(hipChatSettings.getName())) {
            logger.debug("My Packet, skipping");
            return;
        }
        handlePacket((E) packet);
    }

    @VisibleForTesting
    void setHipChatSettings(HipChatSettings hipChatSettings) {
        this.hipChatSettings = hipChatSettings;
    }
}
