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
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.interceptor.suback.parameter.SubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.unsuback.parameter.UnsubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.unsubscribe.parameter.UnsubscribeInboundInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.general.UserProperties;
import com.hivemq.extension.sdk.api.packets.general.UserProperty;
import com.hivemq.extension.sdk.api.packets.publish.PayloadFormatIndicator;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.suback.SubackPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.SubackReasonCode;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import com.hivemq.extension.sdk.api.packets.unsuback.UnsubackPacket;
import com.hivemq.extension.sdk.api.packets.unsuback.UnsubackReasonCode;
import com.hivemq.extension.sdk.api.packets.unsubscribe.UnsubscribePacket;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
public class MessageLogUtil {

    @NotNull
    static final Logger log = LoggerFactory.getLogger(MessageLogUtil.class);

    @NotNull
    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static void logDisconnect(final @NotNull String message, final @NotNull DisconnectEventInput disconnectEventInput, final boolean verbose) {
        if (!verbose) {
            log.info(message);
            return;
        }
        final String userPropertiesAsString = getUserPropertiesAsString(disconnectEventInput.getUserProperties().orElse(null));
        log.info(message + " Reason Code: '{}', Reason String: '{}', {}",
                disconnectEventInput.getReasonCode().orElse(null),
                disconnectEventInput.getReasonString().orElse(null),
                userPropertiesAsString);
    }

    public static void logConnect(final @NotNull ConnectPacket connectPacket, final boolean verbose) {
        if (!verbose) {
            log.info("Received CONNECT from client '{}': Protocol version: '{}', Clean Start: '{}', Session Expiry Interval: '{}'",
                    connectPacket.getClientId(),
                    connectPacket.getMqttVersion().name(),
                    connectPacket.getCleanStart(),
                    connectPacket.getSessionExpiryInterval());
            return;
        }

        final String userPropertiesAsString = getUserPropertiesAsString(connectPacket.getUserProperties());
        final String passwordAsString = getStringFromByteBuffer(connectPacket.getPassword().orElse(null));
        final String passwordProperty;
        if (StringUtils.isAsciiPrintable(passwordAsString) || passwordAsString == null) {
            passwordProperty = "Password: '" + passwordAsString + "'";
        } else {
            passwordProperty = "Password (Hex): '" + getHexStringFromByteBuffer(connectPacket.getPassword().orElse(null)) + "'";
        }


        final String authDataAsString;
        if (connectPacket.getAuthenticationData().isPresent()) {
            authDataAsString = getStringFromByteBuffer(Base64.getEncoder().encode(connectPacket.getAuthenticationData().get()));
        } else {
            authDataAsString = null;
        }

        final String willString;
        if (connectPacket.getWillPublish().isPresent()) {
            willString = getWillAsString(connectPacket.getWillPublish().get());
        } else {
            willString = "";
        }

        log.info("Received CONNECT from client '{}': Protocol version: '{}', Clean Start: '{}', Session Expiry Interval: '{}'," +
                        " Keep Alive: '{}', Maximum Packet Size: '{}', Receive Maximum: '{}', Topic Alias Maximum: '{}'," +
                        " Request Problem Information: '{}', Request Response Information: '{}', " +
                        " Username: '{}', {}, Auth Method: '{}', Auth Data (Base64): '{}', {}{}",
                connectPacket.getClientId(),
                connectPacket.getMqttVersion().name(),
                connectPacket.getCleanStart(),
                connectPacket.getSessionExpiryInterval(),
                connectPacket.getKeepAlive(),
                connectPacket.getMaximumPacketSize(),
                connectPacket.getReceiveMaximum(),
                connectPacket.getTopicAliasMaximum(),
                connectPacket.getRequestProblemInformation(),
                connectPacket.getRequestResponseInformation(),
                connectPacket.getUserName().orElse(null),
                passwordProperty,
                connectPacket.getAuthenticationMethod().orElse(null),
                authDataAsString,
                userPropertiesAsString,
                willString);
    }

    @NotNull
    private static String getWillAsString(final @NotNull WillPublishPacket willPublishPacket) {

        final String topic = willPublishPacket.getTopic();
        final String publishAsString = getPublishAsString(willPublishPacket, true);
        final String willPublishAsString = publishAsString + ", Will Delay: '" + willPublishPacket.getWillDelay() + "'";

        return String.format(", Will: { Topic: '%s', %s }", topic, willPublishAsString);
    }

    public static void logPublish(final @NotNull String prefix, final @NotNull PublishPacket publishPacket, final boolean verbose) {

        final String topic = publishPacket.getTopic();
        final String publishString = getPublishAsString(publishPacket, verbose);

        log.info("{} '{}': {}", prefix, topic, publishString);

    }

    public static void logSubscribe(final @NotNull SubscribeInboundInput subscribeInboundInput, final boolean verbose) {
        final StringBuilder topics = new StringBuilder();
        final String clientId = subscribeInboundInput.getClientInformation().getClientId();
        @NotNull final SubscribePacket subscribePacket = subscribeInboundInput.getSubscribePacket();

        if (!verbose) {
            topics.append("Topics: {");
            for (final Subscription sub : subscribePacket.getSubscriptions()) {
                topics.append(" [Topic: '")
                        .append(sub.getTopicFilter())
                        .append("', QoS: '")
                        .append(sub.getQos().getQosNumber())
                        .append("'],");
            }
            topics.deleteCharAt(topics.length() - 1); //delete last comma
            topics.append(" }");
            log.info("Received SUBSCRIBE from client '{}': {}", clientId, topics.toString());
            return;
        }

        topics.append("Topics: {");
        for (final Subscription sub : subscribePacket.getSubscriptions()) {
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

        final Integer subscriptionIdentifier = subscribePacket.getSubscriptionIdentifier().orElse(null);
        final String userPropertiesAsString = getUserPropertiesAsString(subscribePacket.getUserProperties());

        log.info("Received SUBSCRIBE from client '{}': {}, Subscription Identifier: '{}', {}", clientId, topics.toString(), subscriptionIdentifier, userPropertiesAsString);
    }

    public static void logSuback(final @NotNull SubackOutboundInput subackOutboundInput, final boolean verbose) {
        final StringBuilder suback = new StringBuilder();
        final String clientId = subackOutboundInput.getClientInformation().getClientId();
        @NotNull final SubackPacket subackPacket = subackOutboundInput.getSubackPacket();

        suback.append("Suback Reason Codes: {");
        for (final SubackReasonCode sub : subackPacket.getReasonCodes()) {
            suback.append(" [Reason Code: '")
                    .append(sub.toString())
                    .append("'],");
        }

        if (!verbose) {
            suback.deleteCharAt(suback.length() - 1); //delete last comma
            suback.append(" }");
            log.info("Send SUBACK to client '{}': {}", clientId, suback.toString());
            return;
        }

        final String userPropertiesAsString = getUserPropertiesAsString(subackPacket.getUserProperties());

        log.info("Send SUBACK to client '{}': {}, {}", clientId, suback.toString(), userPropertiesAsString);
    }

    @NotNull
    public static void logUnsubscribe(final @NotNull UnsubscribeInboundInput unsubscribeInboundInput, final boolean verbose) {
        final StringBuilder topics = new StringBuilder();
        @NotNull final String clientId = unsubscribeInboundInput.getClientInformation().getClientId();
        @NotNull final UnsubscribePacket unsubscribePacket = unsubscribeInboundInput.getUnsubscribePacket();

        topics.append("Topics: {");
        for (final String unsub : unsubscribePacket.getTopicFilters()) {
            topics.append(" [Topic: '")
                    .append(unsub)
                    .append("'],");
        }

        if (!verbose) {
            topics.deleteCharAt(topics.length() - 1); //delete last comma
            topics.append(" }");

            log.info("Received UNSUBSCRIBE from client '{}': {}", clientId, topics.toString());
            return;
        }

        final String userPropertiesAsString = getUserPropertiesAsString(unsubscribePacket.getUserProperties());

        log.info("Received UNSUBSCRIBE from client '{}': {}, {}", clientId, topics.toString(), userPropertiesAsString);
    }

    public static void logUnsuback(final @NotNull UnsubackOutboundInput unsubackOutboundInput, final boolean verbose) {
        final StringBuilder unsuback = new StringBuilder();
        final String clientId = unsubackOutboundInput.getClientInformation().getClientId();
        @NotNull final UnsubackPacket unsubackPacket = unsubackOutboundInput.getUnsubackPacket();

        unsuback.append("Unsuback Reason Codes: {");
        for (final UnsubackReasonCode unsubackReasonCode : unsubackPacket.getReasonCodes()) {
            unsuback.append(" [Reason Code: '")
                    .append(unsubackReasonCode.toString())
                    .append("'],");
        }

        if (!verbose) {
            unsuback.deleteCharAt(unsuback.length() - 1); //delete last comma
            unsuback.append(" }");
            log.info("Send UNSUBACK to client '{}': {}", clientId, unsuback.toString());
            return;
        }

        final String userPropertiesAsString = getUserPropertiesAsString(unsubackPacket.getUserProperties());

        log.info("Send UNSUBACK to client '{}': {}, {}", clientId, unsuback.toString(), userPropertiesAsString);
    }

    @NotNull
    private static String getPublishAsString(final @NotNull PublishPacket publishPacket, final boolean verbose) {
        final int qos = publishPacket.getQos().getQosNumber();
        final boolean retained = publishPacket.getRetain();
        final Optional<ByteBuffer> payload = publishPacket.getPayload();
        final String payloadAsString = getStringFromByteBuffer(payload.orElse(null));

        if (!verbose) {
            return String.format("Payload: '%s'," +
                            " QoS: '%s'," +
                            " Retained: '%s'",
                    payloadAsString, qos, retained);
        }
        final Optional<String> contentType = publishPacket.getContentType();
        final String correlationDataString = getStringFromByteBuffer(publishPacket.getCorrelationData().orElse(null));
        final Optional<String> responseTopic = publishPacket.getResponseTopic();
        final Optional<Long> messageExpiryInterval = publishPacket.getMessageExpiryInterval();
        final boolean dupFlag = publishPacket.getDupFlag();
        final Optional<PayloadFormatIndicator> payloadFormatIndicator = publishPacket.getPayloadFormatIndicator();
        final List<Integer> subscriptionIdentifiers = publishPacket.getSubscriptionIdentifiers();

        final String userPropertiesAsString = getUserPropertiesAsString(publishPacket.getUserProperties());

        return String.format("Payload: '%s'," +
                        " QoS: '%s'," +
                        " Retained: '%s'," +
                        " Message Expiry Interval: '%s'," +
                        " Duplicate Delivery: '%s'," +
                        " Correlation Data: '%s'," +
                        " Response Topic: '%s'," +
                        " Content Type: '%s'," +
                        " Payload Format Indicator: '%s'," +
                        " Subscription Identifiers: '%s'," +
                        " %s", //user properties
                payloadAsString, qos, retained, messageExpiryInterval.orElse(null), dupFlag, correlationDataString,
                responseTopic.orElse(null), contentType.orElse(null), payloadFormatIndicator.orElse(null),
                subscriptionIdentifiers, userPropertiesAsString);
    }

    @Nullable
    private static String getStringFromByteBuffer(final @Nullable ByteBuffer buffer) {
        if (buffer == null) {
            return null;
        }
        final byte[] bytes = new byte[buffer.remaining()];
        for (int i = 0; i < buffer.remaining(); i++) {
            bytes[i] = buffer.get(i);
        }
        return new String(bytes, UTF_8);
    }

    @Nullable
    private static String getHexStringFromByteBuffer(final @Nullable ByteBuffer buffer) {
        if (buffer == null) {
            return null;
        }
        final byte[] bytes = new byte[buffer.remaining()];
        for (int i = 0; i < buffer.remaining(); i++) {
            bytes[i] = buffer.get(i);
        }

        return asHexString(bytes);
    }

    @NotNull
    private static String asHexString(final @NotNull byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return new String(out);
    }

    @NotNull
    private static String getUserPropertiesAsString(final @Nullable UserProperties userProperties) {
        if (userProperties == null) {
            return "User Properties: 'null'";
        }
        final List<UserProperty> userPropertyList = userProperties.asList();
        if (userPropertyList.size() == 0) {
            return "User Properties: 'null'";
        }
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < userPropertyList.size(); i++) {
            final UserProperty userProperty = userPropertyList.get(i);
            if (i == 0) {
                stringBuilder.append("User Properties: ");
            } else {
                stringBuilder.append(", ");
            }
            stringBuilder.append("[Name: '").append(userProperty.getName());
            stringBuilder.append("', Value: '").append(userProperty.getValue()).append("']");
        }
        return stringBuilder.toString();
    }
}
