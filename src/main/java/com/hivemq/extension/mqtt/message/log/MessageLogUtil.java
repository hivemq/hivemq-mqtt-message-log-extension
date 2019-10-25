/*
 * Copyright 2019 dc-square GmbH
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
 *
 */

package com.hivemq.extension.mqtt.message.log;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.publish.PayloadFormatIndicator;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
public class MessageLogUtil {

    @NotNull
    private static final Logger log = LoggerFactory.getLogger(MessageLogUtil.class);

    public static void logDisconnect(final @NotNull String message) {
        log.info(message);
    }

    public static void logConnect(final @NotNull ConnectPacket connectPacket) {
        log.info("Client '{}' connected: Protocol version: '{}', Clean Start: '{}', Session Expiry Interval: '{}', Keep Alive: '{}', Maximum Packet Size: '{}', Receive Maximum: '{}', Topic Alias Maximum: '{}', Request Problem Information: '{}', Request Response Information: '{}'",
                connectPacket.getClientId(),
                connectPacket.getMqttVersion().name(),
                connectPacket.getCleanStart(),
                connectPacket.getSessionExpiryInterval(),
                connectPacket.getKeepAlive(),
                connectPacket.getMaximumPacketSize(),
                connectPacket.getReceiveMaximum(),
                connectPacket.getTopicAliasMaximum(),
                connectPacket.getRequestProblemInformation(),
                connectPacket.getRequestResponseInformation());
    }

    public static void logWill(final @NotNull WillPublishPacket willPublishPacket, final @NotNull String clientId) {

        final String topic = willPublishPacket.getTopic();
        final String publishAsString = getPublishAsString(willPublishPacket);
        final String willPublishAsString = publishAsString + ", Will Delay: ''" + willPublishPacket.getWillDelay() + "'";

        log.info("Client '{}' set Will. Topic: '{}', {}", clientId, topic, willPublishAsString);
    }

    public static void logPublish(final @NotNull String prefix, final @NotNull PublishPacket publishPacket) {

        final String topic = publishPacket.getTopic();
        final String publishString = getPublishAsString(publishPacket);

        log.info("{} '{}': {}", prefix, topic, publishString);

    }

    public static void logSubscribe(final @NotNull SubscribeInboundInput subscribeInboundInput) {
        final StringBuilder topics = new StringBuilder();
        topics.append("Topics: {");
        for (final Subscription sub : subscribeInboundInput.getSubscribePacket().getSubscriptions()) {
            topics.append(" [Topic: '")
                    .append(sub.getTopicFilter())
                    .append("', QoS: '")
                    .append(sub.getQos().getQosNumber())
                    .append("', Retain As Published: '")
                    .append(sub.getRetainAsPublished())
                    .append("', No Local: '")
                    .append(sub.getNoLocal())
                    .append("', Retain Handling: '")
                    .append(sub.getRetainHandling().name())
                    .append("'],");
        }

        topics.deleteCharAt(topics.length() - 1); //delete last comma

        topics.append(" }");

        log.info("Subscribe from client '{}' received: {}", subscribeInboundInput.getClientInformation().getClientId(), topics.toString());
    }

    @NotNull
    private static String getPublishAsString(final @NotNull PublishPacket publishPacket) {
        final int qos = publishPacket.getQos().getQosNumber();
        final boolean retained = publishPacket.getRetain();
        final Optional<String> contentType = publishPacket.getContentType();
        final String correlationDataString = MessageLogUtil.getStringFromByteBuffer(publishPacket.getCorrelationData());
        final Optional<String> responseTopic = publishPacket.getResponseTopic();
        final Optional<Long> messageExpiryInterval = publishPacket.getMessageExpiryInterval();
        final boolean dupFlag = publishPacket.getDupFlag();
        final Optional<PayloadFormatIndicator> payloadFormatIndicator = publishPacket.getPayloadFormatIndicator();
        final List<Integer> subscriptionIdentifiers = publishPacket.getSubscriptionIdentifiers();

        final Optional<ByteBuffer> payload = publishPacket.getPayload();
        final String payloadAsString = MessageLogUtil.getStringFromByteBuffer(payload);

        return String.format("Payload: '%s'," +
                        " QoS: '%s'," +
                        " Retained: '%s'," +
                        " Message Expiry Interval: '%s'," +
                        " Duplicate Delivery: '%s'," +
                        " Correlation Data: '%s'," +
                        " Response Topic: '%s'," +
                        " Content Type: '%s'," +
                        " Payload Format Indicator: '%s'," +
                        " Subscription Identifiers: '%s'",
                payloadAsString, qos, retained, messageExpiryInterval.orElse(null), dupFlag, correlationDataString,
                responseTopic.orElse(null), contentType.orElse(null), payloadFormatIndicator.orElse(null), subscriptionIdentifiers);
    }

    @Nullable
    private static String getStringFromByteBuffer(final @NotNull Optional<ByteBuffer> bufferOptional) {
        if (bufferOptional.isEmpty()) {
            return null;
        }
        final ByteBuffer buffer = bufferOptional.get();
        final byte[] bytes = new byte[buffer.remaining()];
        for (int i = 0; i < buffer.remaining(); i++) {
            bytes[i] = buffer.get(i);
        }
        return new String(bytes, UTF_8);
    }
}
