/*
 * Copyright 2019-present HiveMQ GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.hivemq.extensions.log.mqtt.message.logger;

import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.interceptor.connack.parameter.ConnackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.pingreq.parameter.PingReqInboundInput;
import com.hivemq.extension.sdk.api.interceptor.pingresp.parameter.PingRespOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.suback.parameter.SubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.unsuback.parameter.UnsubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.unsubscribe.parameter.UnsubscribeInboundInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.puback.PubackPacket;
import com.hivemq.extension.sdk.api.packets.pubcomp.PubcompPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.pubrec.PubrecPacket;
import com.hivemq.extension.sdk.api.packets.pubrel.PubrelPacket;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interface for MQTT message logging.
 * Provides common configuration and logging methods.
 *
 * @since 1.3.0
 */
public interface MessageLogger {

    @NotNull Logger LOG = LoggerFactory.getLogger(MessageLogger.class);

    void logDisconnect(@NotNull String message, @NotNull DisconnectEventInput disconnectEventInput);

    void logDisconnect(@NotNull DisconnectPacket disconnectPacket, @NotNull String clientId, boolean inbound);

    void logConnect(@NotNull ConnectPacket connectPacket);

    void logConnack(@NotNull ConnackOutboundInput connackOutboundInput);

    void logPublish(@NotNull String prefix, @NotNull PublishPacket publishPacket);

    void logSubscribe(@NotNull SubscribeInboundInput subscribeInboundInput);

    void logSuback(@NotNull SubackOutboundInput subackOutboundInput);

    void logUnsubscribe(@NotNull UnsubscribeInboundInput unsubscribeInboundInput);

    void logUnsuback(@NotNull UnsubackOutboundInput unsubackOutboundInput);

    void logPingreq(@NotNull PingReqInboundInput pingReqInboundInput);

    void logPingresp(@NotNull PingRespOutboundInput pingRespOutboundInput);

    void logPuback(@NotNull PubackPacket pubackPacket, @NotNull String clientId, boolean inbound);

    void logPubrec(@NotNull PubrecPacket pubrecPacket, @NotNull String clientId, boolean inbound);

    void logPubrel(@NotNull PubrelPacket pubrelPacket, @NotNull String clientId, boolean inbound);

    void logPubcomp(@NotNull PubcompPacket pubcompPacket, @NotNull String clientId, boolean inbound);
}
