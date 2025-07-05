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
package com.hivemq.extensions.log.mqtt.message.util;

import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.interceptor.connack.parameter.ConnackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.pingreq.parameter.PingReqInboundInput;
import com.hivemq.extension.sdk.api.interceptor.pingresp.parameter.PingRespOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.suback.parameter.SubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.unsuback.parameter.UnsubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.unsubscribe.parameter.UnsubscribeInboundInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.UserProperties;
import com.hivemq.extension.sdk.api.packets.puback.PubackPacket;
import com.hivemq.extension.sdk.api.packets.pubcomp.PubcompPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.pubrec.PubrecPacket;
import com.hivemq.extension.sdk.api.packets.pubrel.PubrelPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @since 1.0.0
 */
public class MessageLogUtil {

    static final @NotNull Logger LOG = LoggerFactory.getLogger(MessageLogUtil.class);

    private static final char @NotNull [] DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static void logDisconnect(
            final @NotNull String message,
            final @NotNull DisconnectEventInput disconnectEventInput,
            final boolean verbose) {
        if (!verbose) {
            LOG.info(message + " Reason Code: '{}'", disconnectEventInput.getReasonCode().orElse(null));
            return;
        }
        final var userPropertiesAsString =
                getUserPropertiesAsString(disconnectEventInput.getUserProperties().orElse(null));
        LOG.info(message + " Reason Code: '{}', Reason String: '{}', {}",
                disconnectEventInput.getReasonCode().orElse(null),
                disconnectEventInput.getReasonString().orElse(null),
                userPropertiesAsString);
    }

    public static void logDisconnect(
            final @NotNull DisconnectPacket disconnectPacket,
            final @NotNull String clientId,
            final boolean inbound,
            final boolean verbose) {
        final var reasonCode = disconnectPacket.getReasonCode();
        if (!verbose) {
            if (inbound) {
                LOG.info("Received DISCONNECT from client '{}': Reason Code: '{}'", clientId, reasonCode);
            } else {
                LOG.info("Sent DISCONNECT to client '{}': Reason Code: '{}'", clientId, reasonCode);
            }
            return;
        }
        final var userPropertiesAsString = getUserPropertiesAsString(disconnectPacket.getUserProperties());
        final var reasonString = disconnectPacket.getReasonString().orElse(null);
        final var serverReference = disconnectPacket.getServerReference().orElse(null);
        final var sessionExpiry = disconnectPacket.getSessionExpiryInterval().orElse(null);
        if (inbound) {
            LOG.info(
                    "Received DISCONNECT from client '{}': Reason Code: '{}', Reason String: '{}', Server Reference: '{}', Session Expiry: '{}', {}",
                    clientId,
                    reasonCode,
                    reasonString,
                    serverReference,
                    sessionExpiry,
                    userPropertiesAsString);
        } else {
            LOG.info(
                    "Sent DISCONNECT to client '{}': Reason Code: '{}', Reason String: '{}', Server Reference: '{}', Session Expiry: '{}', {}",
                    clientId,
                    reasonCode,
                    reasonString,
                    serverReference,
                    sessionExpiry,
                    userPropertiesAsString);
        }
    }

    public static void logConnect(
            final @NotNull ConnectPacket connectPacket,
            final boolean verbose,
            final boolean payload) {
        if (!verbose) {
            LOG.info(
                    "Received CONNECT from client '{}': Protocol version: '{}', Clean Start: '{}', Session Expiry Interval: '{}'",
                    connectPacket.getClientId(),
                    connectPacket.getMqttVersion().name(),
                    connectPacket.getCleanStart(),
                    connectPacket.getSessionExpiryInterval());
            return;
        }
        final var userPropertiesAsString = getUserPropertiesAsString(connectPacket.getUserProperties());
        final var passwordAsString = getStringFromByteBuffer(connectPacket.getPassword().orElse(null));
        final String passwordProperty;
        if (StringUtils.isAsciiPrintable(passwordAsString) || passwordAsString == null) {
            passwordProperty = "Password: '" + passwordAsString + "'";
        } else {
            passwordProperty =
                    "Password (Hex): '" + getHexStringFromByteBuffer(connectPacket.getPassword().orElse(null)) + "'";
        }
        final String authDataAsString;
        if (connectPacket.getAuthenticationData().isPresent()) {
            authDataAsString =
                    getStringFromByteBuffer(Base64.getEncoder().encode(connectPacket.getAuthenticationData().get()));
        } else {
            authDataAsString = null;
        }
        final String willString;
        if (connectPacket.getWillPublish().isPresent()) {
            willString = getWillAsString(connectPacket.getWillPublish().get(), payload);
        } else {
            willString = "";
        }
        LOG.info(
                "Received CONNECT from client '{}': Protocol version: '{}', Clean Start: '{}', Session Expiry Interval: '{}'," +
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

    public static void logConnack(final @NotNull ConnackOutboundInput connackOutboundInput, final boolean verbose) {
        final var clientId = connackOutboundInput.getClientInformation().getClientId();
        final var connackPacket = connackOutboundInput.getConnackPacket();
        if (!verbose) {
            LOG.info("Sent CONNACK to client '{}': Reason Code: '{}', Session Present: '{}'",
                    clientId,
                    connackPacket.getReasonCode(),
                    connackPacket.getSessionPresent());
            return;
        }
        final var userPropertiesAsString = getUserPropertiesAsString(connackPacket.getUserProperties());
        final String authDataAsString;
        if (connackPacket.getAuthenticationData().isPresent()) {
            authDataAsString =
                    getStringFromByteBuffer(Base64.getEncoder().encode(connackPacket.getAuthenticationData().get()));
        } else {
            authDataAsString = null;
        }
        LOG.info("Sent CONNACK to client '{}': Reason Code: '{}', Session Present: '{}', Session Expiry Interval: '{}'," +
                        " Assigned ClientId '{}', Maximum QoS: '{}', Maximum Packet Size: '{}', Receive Maximum: '{}'," +
                        " Topic Alias Maximum: '{}', Reason String: '{}', Response Information: '{}', Server Keep Alive: '{}'," +
                        " Server Reference: '{}', Shared Subscription Available: '{}', Wildcards Available: '{}'," +
                        " Retain Available: '{}', Subscription Identifiers Available: '{}'," +
                        " Auth Method: '{}', Auth Data (Base64): '{}', {}",
                clientId,
                connackPacket.getReasonCode(),
                connackPacket.getSessionPresent(),
                connackPacket.getSessionExpiryInterval().orElse(null),
                connackPacket.getAssignedClientIdentifier().orElse(null),
                connackPacket.getMaximumQoS().orElse(null),
                connackPacket.getMaximumPacketSize(),
                connackPacket.getReceiveMaximum(),
                connackPacket.getTopicAliasMaximum(),
                connackPacket.getReasonString().orElse(null),
                connackPacket.getResponseInformation().orElse(null),
                connackPacket.getServerKeepAlive().orElse(null),
                connackPacket.getServerReference().orElse(null),
                connackPacket.getSharedSubscriptionsAvailable(),
                connackPacket.getWildCardSubscriptionAvailable(),
                connackPacket.getRetainAvailable(),
                connackPacket.getSubscriptionIdentifiersAvailable(),
                connackPacket.getAuthenticationMethod().orElse(null),
                authDataAsString,
                userPropertiesAsString);
    }

    private static @NotNull String getWillAsString(
            final @NotNull WillPublishPacket willPublishPacket,
            final boolean payload) {
        final var topic = willPublishPacket.getTopic();
        final var publishAsString = getPublishAsString(willPublishPacket, true, payload);
        final var willPublishAsString = publishAsString + ", Will Delay: '" + willPublishPacket.getWillDelay() + "'";
        return String.format(", Will: { Topic: '%s', %s }", topic, willPublishAsString);
    }

    public static void logPublish(
            final @NotNull String prefix,
            final @NotNull PublishPacket publishPacket,
            final boolean verbose,
            final boolean payload) {
        final var topic = publishPacket.getTopic();
        final var publishString = getPublishAsString(publishPacket, verbose, payload);
        LOG.info("{} '{}': {}", prefix, topic, publishString);
    }

    public static void logSubscribe(final @NotNull SubscribeInboundInput subscribeInboundInput, final boolean verbose) {
        final var topics = new StringBuilder();
        final var clientId = subscribeInboundInput.getClientInformation().getClientId();
        final var subscribePacket = subscribeInboundInput.getSubscribePacket();
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
            LOG.info("Received SUBSCRIBE from client '{}': {}", clientId, topics);
            return;
        }
        topics.append("Topics: {");
        for (final var sub : subscribePacket.getSubscriptions()) {
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
        final var subscriptionIdentifier = subscribePacket.getSubscriptionIdentifier().orElse(null);
        final var userPropertiesAsString = getUserPropertiesAsString(subscribePacket.getUserProperties());
        LOG.info("Received SUBSCRIBE from client '{}': {}, Subscription Identifier: '{}', {}",
                clientId,
                topics,
                subscriptionIdentifier,
                userPropertiesAsString);
    }

    public static void logSuback(final @NotNull SubackOutboundInput subackOutboundInput, final boolean verbose) {
        final var suback = new StringBuilder();
        final var clientId = subackOutboundInput.getClientInformation().getClientId();
        final var subackPacket = subackOutboundInput.getSubackPacket();
        suback.append("Suback Reason Codes: {");
        for (final var sub : subackPacket.getReasonCodes()) {
            suback.append(" [Reason Code: '").append(sub).append("'],");
        }
        suback.deleteCharAt(suback.length() - 1); //delete last comma
        suback.append(" }");
        if (!verbose) {
            LOG.info("Sent SUBACK to client '{}': {}", clientId, suback);
            return;
        }
        final var userPropertiesAsString = getUserPropertiesAsString(subackPacket.getUserProperties());
        final var reasonString = subackPacket.getReasonString().orElse(null);
        LOG.info("Sent SUBACK to client '{}': {}, Reason String: '{}', {}",
                clientId,
                suback,
                reasonString,
                userPropertiesAsString);
    }

    public static void logUnsubscribe(
            final @NotNull UnsubscribeInboundInput unsubscribeInboundInput,
            final boolean verbose) {
        final var topics = new StringBuilder();
        final var clientId = unsubscribeInboundInput.getClientInformation().getClientId();
        final var unsubscribePacket = unsubscribeInboundInput.getUnsubscribePacket();
        topics.append("Topics: {");
        for (final var unsub : unsubscribePacket.getTopicFilters()) {
            topics.append(" [Topic: '").append(unsub).append("'],");
        }
        topics.deleteCharAt(topics.length() - 1); //delete last comma
        topics.append(" }");
        if (!verbose) {
            LOG.info("Received UNSUBSCRIBE from client '{}': {}", clientId, topics);
            return;
        }
        final var userPropertiesAsString = getUserPropertiesAsString(unsubscribePacket.getUserProperties());
        LOG.info("Received UNSUBSCRIBE from client '{}': {}, {}", clientId, topics, userPropertiesAsString);
    }

    public static void logUnsuback(final @NotNull UnsubackOutboundInput unsubackOutboundInput, final boolean verbose) {
        final var unsuback = new StringBuilder();
        final var clientId = unsubackOutboundInput.getClientInformation().getClientId();
        final var unsubackPacket = unsubackOutboundInput.getUnsubackPacket();
        unsuback.append("Unsuback Reason Codes: {");
        for (final var unsubackReasonCode : unsubackPacket.getReasonCodes()) {
            unsuback.append(" [Reason Code: '").append(unsubackReasonCode).append("'],");
        }
        unsuback.deleteCharAt(unsuback.length() - 1); //delete last comma
        unsuback.append(" }");
        if (!verbose) {
            LOG.info("Sent UNSUBACK to client '{}': {}", clientId, unsuback);
            return;
        }
        final var userPropertiesAsString = getUserPropertiesAsString(unsubackPacket.getUserProperties());
        final var reasonString = unsubackPacket.getReasonString().orElse(null);
        LOG.info("Sent UNSUBACK to client '{}': {}, Reason String: '{}', {}",
                clientId,
                unsuback,
                reasonString,
                userPropertiesAsString);
    }

    public static void logPingreq(final @NotNull PingReqInboundInput pingReqInboundInput) {
        final var clientId = pingReqInboundInput.getClientInformation().getClientId();
        LOG.info("Received PING REQUEST from client '{}'", clientId);
    }

    public static void logPingresp(final @NotNull PingRespOutboundInput pingRespOutboundInput) {
        final var clientId = pingRespOutboundInput.getClientInformation().getClientId();
        LOG.info("Sent PING RESPONSE to client '{}'", clientId);
    }

    public static void logPuback(
            final @NotNull PubackPacket pubackPacket,
            final @NotNull String clientId,
            final boolean inbound,
            final boolean verbose) {
        final var reasonCode = pubackPacket.getReasonCode();
        if (!verbose) {
            if (inbound) {
                LOG.info("Received PUBACK from client '{}': Reason Code: '{}'", clientId, reasonCode);
            } else {
                LOG.info("Sent PUBACK to client '{}': Reason Code: '{}'", clientId, reasonCode);
            }
            return;
        }
        final var userPropertiesAsString = getUserPropertiesAsString(pubackPacket.getUserProperties());
        final var reasonString = pubackPacket.getReasonString().orElse(null);
        if (inbound) {
            LOG.info("Received PUBACK from client '{}': Reason Code: '{}', Reason String: '{}', {}",
                    clientId,
                    reasonCode,
                    reasonString,
                    userPropertiesAsString);
        } else {
            LOG.info("Sent PUBACK to client '{}': Reason Code: '{}', Reason String: '{}', {}",
                    clientId,
                    reasonCode,
                    reasonString,
                    userPropertiesAsString);
        }
    }

    public static void logPubrec(
            final @NotNull PubrecPacket pubrecPacket,
            final @NotNull String clientId,
            final boolean inbound,
            final boolean verbose) {
        final var reasonCode = pubrecPacket.getReasonCode();
        if (!verbose) {
            if (inbound) {
                LOG.info("Received PUBREC from client '{}': Reason Code: '{}'", clientId, reasonCode);
            } else {
                LOG.info("Sent PUBREC to client '{}': Reason Code: '{}'", clientId, reasonCode);
            }
            return;
        }
        final var userPropertiesAsString = getUserPropertiesAsString(pubrecPacket.getUserProperties());
        final var reasonString = pubrecPacket.getReasonString().orElse(null);
        if (inbound) {
            LOG.info("Received PUBREC from client '{}': Reason Code: '{}', Reason String: '{}', {}",
                    clientId,
                    reasonCode,
                    reasonString,
                    userPropertiesAsString);
        } else {
            LOG.info("Sent PUBREC to client '{}': Reason Code: '{}', Reason String: '{}', {}",
                    clientId,
                    reasonCode,
                    reasonString,
                    userPropertiesAsString);
        }
    }

    public static void logPubrel(
            final @NotNull PubrelPacket pubrelPacket,
            final @NotNull String clientId,
            final boolean inbound,
            final boolean verbose) {
        final var reasonCode = pubrelPacket.getReasonCode();
        if (!verbose) {
            if (inbound) {
                LOG.info("Received PUBREL from client '{}': Reason Code: '{}'", clientId, reasonCode);
            } else {
                LOG.info("Sent PUBREL to client '{}': Reason Code: '{}'", clientId, reasonCode);
            }
            return;
        }
        final var userPropertiesAsString = getUserPropertiesAsString(pubrelPacket.getUserProperties());
        final var reasonString = pubrelPacket.getReasonString().orElse(null);
        if (inbound) {
            LOG.info("Received PUBREL from client '{}': Reason Code: '{}', Reason String: '{}', {}",
                    clientId,
                    reasonCode,
                    reasonString,
                    userPropertiesAsString);
        } else {
            LOG.info("Sent PUBREL to client '{}': Reason Code: '{}', Reason String: '{}', {}",
                    clientId,
                    reasonCode,
                    reasonString,
                    userPropertiesAsString);
        }
    }

    public static void logPubcomp(
            final @NotNull PubcompPacket pubcompPacket,
            final @NotNull String clientId,
            final boolean inbound,
            final boolean verbose) {
        final var reasonCode = pubcompPacket.getReasonCode();
        if (!verbose) {
            if (inbound) {
                LOG.info("Received PUBCOMP from client '{}': Reason Code: '{}'", clientId, reasonCode);
            } else {
                LOG.info("Sent PUBCOMP to client '{}': Reason Code: '{}'", clientId, reasonCode);
            }
            return;
        }
        final var userPropertiesAsString = getUserPropertiesAsString(pubcompPacket.getUserProperties());
        final var reasonString = pubcompPacket.getReasonString().orElse(null);
        if (inbound) {
            LOG.info("Received PUBCOMP from client '{}': Reason Code: '{}', Reason String: '{}', {}",
                    clientId,
                    reasonCode,
                    reasonString,
                    userPropertiesAsString);
        } else {
            LOG.info("Sent PUBCOMP to client '{}': Reason Code: '{}', Reason String: '{}', {}",
                    clientId,
                    reasonCode,
                    reasonString,
                    userPropertiesAsString);
        }
    }

    private static @NotNull String getPublishAsString(
            final @NotNull PublishPacket publishPacket,
            final boolean verbose,
            final boolean payload) {
        final var qos = publishPacket.getQos().getQosNumber();
        final var retained = publishPacket.getRetain();
        final String payloadAsString;
        if (payload && publishPacket.getPayload().isPresent()) {
            payloadAsString = getStringFromByteBuffer(publishPacket.getPayload().get());
        } else {
            payloadAsString = null;
        }
        if (!verbose && !payload) {
            return String.format("QoS: '%s'," + " Retained: '%s'", qos, retained);
        } else if (!verbose) {
            return String.format("Payload: '%s'," + " QoS: '%s'," + " Retained: '%s'", payloadAsString, qos, retained);
        }
        final var contentType = publishPacket.getContentType();
        final var correlationDataString = getStringFromByteBuffer(publishPacket.getCorrelationData().orElse(null));
        final var responseTopic = publishPacket.getResponseTopic();
        final var messageExpiryInterval = publishPacket.getMessageExpiryInterval();
        final var dupFlag = publishPacket.getDupFlag();
        final var payloadFormatIndicator = publishPacket.getPayloadFormatIndicator();
        final var subscriptionIdentifiers = publishPacket.getSubscriptionIdentifiers();
        final var userPropertiesAsString = getUserPropertiesAsString(publishPacket.getUserProperties());
        if (!payload) {
            return String.format("QoS: '%s'," +
                            " Retained: '%s'," +
                            " Message Expiry Interval: '%s'," +
                            " Duplicate Delivery: '%s'," +
                            " Correlation Data: '%s'," +
                            " Response Topic: '%s'," +
                            " Content Type: '%s'," +
                            " Payload Format Indicator: '%s'," +
                            " Subscription Identifiers: '%s'," +
                            " %s",
                    qos,
                    retained,
                    messageExpiryInterval.orElse(null),
                    dupFlag,
                    correlationDataString,
                    responseTopic.orElse(null),
                    contentType.orElse(null),
                    payloadFormatIndicator.orElse(null),
                    subscriptionIdentifiers,
                    userPropertiesAsString);
        }
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
                        " %s",
                payloadAsString,
                qos,
                retained,
                messageExpiryInterval.orElse(null),
                dupFlag,
                correlationDataString,
                responseTopic.orElse(null),
                contentType.orElse(null),
                payloadFormatIndicator.orElse(null),
                subscriptionIdentifiers,
                userPropertiesAsString);
    }

    private static @Nullable String getStringFromByteBuffer(final @Nullable ByteBuffer buffer) {
        if (buffer == null) {
            return null;
        }
        final var bytes = new byte[buffer.remaining()];
        for (int i = 0; i < buffer.remaining(); i++) {
            bytes[i] = buffer.get(i);
        }
        return new String(bytes, UTF_8);
    }

    private static @Nullable String getHexStringFromByteBuffer(final @Nullable ByteBuffer buffer) {
        if (buffer == null) {
            return null;
        }
        final var bytes = new byte[buffer.remaining()];
        for (var i = 0; i < buffer.remaining(); i++) {
            bytes[i] = buffer.get(i);
        }
        return asHexString(bytes);
    }

    private static @NotNull String asHexString(final byte @NotNull [] data) {
        final var length = data.length;
        final var out = new char[length << 1];
        for (int i = 0, j = 0; i < length; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }
        return new String(out);
    }

    private static @NotNull String getUserPropertiesAsString(final @Nullable UserProperties userProperties) {
        if (userProperties == null) {
            return "User Properties: 'null'";
        }
        final var userPropertyList = userProperties.asList();
        if (userPropertyList.isEmpty()) {
            return "User Properties: 'null'";
        }
        final var stringBuilder = new StringBuilder();
        for (var i = 0; i < userPropertyList.size(); i++) {
            final var userProperty = userPropertyList.get(i);
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
