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
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.general.UserProperties;
import com.hivemq.extension.sdk.api.packets.puback.PubackPacket;
import com.hivemq.extension.sdk.api.packets.pubcomp.PubcompPacket;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.pubrec.PubrecPacket;
import com.hivemq.extension.sdk.api.packets.pubrel.PubrelPacket;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.ByteBuffer;
import java.util.Base64;
import java.util.List;

import static com.hivemq.extensions.log.mqtt.message.util.StringUtil.getBytes;
import static com.hivemq.extensions.log.mqtt.message.util.StringUtil.getStringFromByteBuffer;

/**
 * JSON formatter for MQTT message logging.
 * Produces structured JSON output suitable for machine parsing and log aggregation.
 *
 * @since 1.3.0
 */
class JsonMessageLogger implements MessageLogger {

    final boolean verbose;
    final boolean payload;
    final boolean redactPassword;

    /**
     * Creates a JsonMessageLogger with the specified configuration.
     *
     * @param verbose        whether to include verbose details
     * @param payload        whether to include message payloads
     * @param redactPassword whether to redact passwords
     */
    JsonMessageLogger(final boolean verbose, final boolean payload, final boolean redactPassword) {
        this.verbose = verbose;
        this.payload = payload;
        this.redactPassword = redactPassword;
    }

    @Override
    public void logDisconnect(final @NotNull String message, final @NotNull DisconnectEventInput disconnectEventInput) {
        final var sb = new StringBuilder(512);
        sb.append("{\"timestamp\":").append(System.currentTimeMillis());
        appendJsonField(sb, "messageType", "DISCONNECT");
        appendJsonField(sb, "direction", "EVENT");
        appendJsonField(sb, "message", message);
        if (disconnectEventInput.getReasonCode().isPresent()) {
            appendJsonField(sb, "reasonCode", disconnectEventInput.getReasonCode().get().name());
        }
        if (verbose) {
            if (disconnectEventInput.getReasonString().isPresent()) {
                appendJsonField(sb, "reasonString", disconnectEventInput.getReasonString().get());
            }
            if (disconnectEventInput.getUserProperties().isPresent()) {
                appendUserPropertiesJson(sb, disconnectEventInput.getUserProperties().get());
            }
        }
        sb.append("}");
        LOG.info(sb.toString());
    }

    @Override
    public void logDisconnect(
            final @NotNull DisconnectPacket disconnectPacket,
            final @NotNull String clientId,
            final boolean inbound) {
        final var sb = new StringBuilder(512);
        sb.append("{\"timestamp\":").append(System.currentTimeMillis());
        appendJsonField(sb, "messageType", "DISCONNECT");
        appendJsonField(sb, "direction", inbound ? "INBOUND" : "OUTBOUND");
        appendJsonField(sb, "clientId", clientId);
        appendJsonField(sb, "reasonCode", disconnectPacket.getReasonCode().name());
        if (verbose) {
            if (disconnectPacket.getReasonString().isPresent()) {
                appendJsonField(sb, "reasonString", disconnectPacket.getReasonString().get());
            }
            if (disconnectPacket.getServerReference().isPresent()) {
                appendJsonField(sb, "serverReference", disconnectPacket.getServerReference().get());
            }
            if (disconnectPacket.getSessionExpiryInterval().isPresent()) {
                appendJsonField(sb, "sessionExpiryInterval", disconnectPacket.getSessionExpiryInterval().get());
            }
            appendUserPropertiesJson(sb, disconnectPacket.getUserProperties());
        }
        sb.append("}");
        LOG.info(sb.toString());
    }

    @Override
    public void logConnect(final @NotNull ConnectPacket connectPacket) {
        final var sb = new StringBuilder(1024);
        sb.append("{\"timestamp\":").append(System.currentTimeMillis());
        appendJsonField(sb, "messageType", "CONNECT");
        appendJsonField(sb, "direction", "INBOUND");
        appendJsonField(sb, "clientId", connectPacket.getClientId());
        appendJsonField(sb, "protocolVersion", connectPacket.getMqttVersion().name());
        appendJsonField(sb, "cleanStart", connectPacket.getCleanStart());
        appendJsonField(sb, "sessionExpiryInterval", connectPacket.getSessionExpiryInterval());
        if (verbose) {
            appendJsonField(sb, "keepAlive", connectPacket.getKeepAlive());
            appendJsonField(sb, "maximumPacketSize", connectPacket.getMaximumPacketSize());
            appendJsonField(sb, "receiveMaximum", connectPacket.getReceiveMaximum());
            appendJsonField(sb, "topicAliasMaximum", connectPacket.getTopicAliasMaximum());
            appendJsonField(sb, "requestProblemInformation", connectPacket.getRequestProblemInformation());
            appendJsonField(sb, "requestResponseInformation", connectPacket.getRequestResponseInformation());
            if (connectPacket.getUserName().isPresent()) {
                appendJsonField(sb, "username", connectPacket.getUserName().get());
            }
            // password handling
            if (redactPassword) {
                appendJsonField(sb, "password", "<redacted>");
            } else if (connectPacket.getPassword().isPresent()) {
                final var passwordBuffer = connectPacket.getPassword().get();
                appendOptionalBinary(passwordBuffer, sb, "password", "passwordBase64");
            }
            if (connectPacket.getAuthenticationMethod().isPresent()) {
                appendJsonField(sb, "authMethod", connectPacket.getAuthenticationMethod().get());
            }
            if (connectPacket.getAuthenticationData().isPresent()) {
                appendJsonField(sb,
                        "authDataBase64",
                        Base64.getEncoder().encodeToString(getBytes(connectPacket.getAuthenticationData().get())));
            }
            appendUserPropertiesJson(sb, connectPacket.getUserProperties());
            // will message
            if (connectPacket.getWillPublish().isPresent()) {
                appendWillJson(sb, connectPacket.getWillPublish().get(), payload);
            }
        }
        sb.append("}");
        LOG.info(sb.toString());
    }

    @Override
    public void logConnack(final @NotNull ConnackOutboundInput connackOutboundInput) {
        final var sb = new StringBuilder(1024);
        final var clientId = connackOutboundInput.getClientInformation().getClientId();
        final var connackPacket = connackOutboundInput.getConnackPacket();
        sb.append("{\"timestamp\":").append(System.currentTimeMillis());
        appendJsonField(sb, "messageType", "CONNACK");
        appendJsonField(sb, "direction", "OUTBOUND");
        appendJsonField(sb, "clientId", clientId);
        appendJsonField(sb, "reasonCode", connackPacket.getReasonCode().name());
        appendJsonField(sb, "sessionPresent", connackPacket.getSessionPresent());
        if (verbose) {
            if (connackPacket.getSessionExpiryInterval().isPresent()) {
                appendJsonField(sb, "sessionExpiryInterval", connackPacket.getSessionExpiryInterval().get());
            }
            if (connackPacket.getAssignedClientIdentifier().isPresent()) {
                appendJsonField(sb, "assignedClientId", connackPacket.getAssignedClientIdentifier().get());
            }
            if (connackPacket.getMaximumQoS().isPresent()) {
                appendJsonField(sb, "maximumQoS", connackPacket.getMaximumQoS().get().getQosNumber());
            }
            appendJsonField(sb, "maximumPacketSize", connackPacket.getMaximumPacketSize());
            appendJsonField(sb, "receiveMaximum", connackPacket.getReceiveMaximum());
            appendJsonField(sb, "topicAliasMaximum", connackPacket.getTopicAliasMaximum());
            if (connackPacket.getReasonString().isPresent()) {
                appendJsonField(sb, "reasonString", connackPacket.getReasonString().get());
            }
            if (connackPacket.getResponseInformation().isPresent()) {
                appendJsonField(sb, "responseInformation", connackPacket.getResponseInformation().get());
            }
            if (connackPacket.getServerKeepAlive().isPresent()) {
                appendJsonField(sb, "serverKeepAlive", connackPacket.getServerKeepAlive().get());
            }
            if (connackPacket.getServerReference().isPresent()) {
                appendJsonField(sb, "serverReference", connackPacket.getServerReference().get());
            }
            appendJsonField(sb, "sharedSubscriptionsAvailable", connackPacket.getSharedSubscriptionsAvailable());
            appendJsonField(sb, "wildCardSubscriptionAvailable", connackPacket.getWildCardSubscriptionAvailable());
            appendJsonField(sb, "retainAvailable", connackPacket.getRetainAvailable());
            appendJsonField(sb,
                    "subscriptionIdentifiersAvailable",
                    connackPacket.getSubscriptionIdentifiersAvailable());
            if (connackPacket.getAuthenticationMethod().isPresent()) {
                appendJsonField(sb, "authMethod", connackPacket.getAuthenticationMethod().get());
            }
            if (connackPacket.getAuthenticationData().isPresent()) {
                appendJsonField(sb,
                        "authDataBase64",
                        Base64.getEncoder().encodeToString(getBytes(connackPacket.getAuthenticationData().get())));
            }
            appendUserPropertiesJson(sb, connackPacket.getUserProperties());
        }
        sb.append("}");
        LOG.info(sb.toString());
    }

    @Override
    public void logPublish(final @NotNull String prefix, final @NotNull PublishPacket publishPacket) {
        final var sb = new StringBuilder(1024);
        sb.append("{\"timestamp\":").append(System.currentTimeMillis());
        appendJsonField(sb, "messageType", "PUBLISH");
        // extract direction from prefix
        final var direction = prefix.contains("Received") ? "INBOUND" : "OUTBOUND";
        appendJsonField(sb, "direction", direction);
        appendJsonField(sb, "topic", publishPacket.getTopic());
        if (payload && publishPacket.getPayload().isPresent()) {
            final var payloadBuffer = publishPacket.getPayload().get();
            appendOptionalBinary(payloadBuffer, sb, "payload", "payloadBase64");
        }
        appendJsonField(sb, "qos", publishPacket.getQos().getQosNumber());
        appendJsonField(sb, "retained", publishPacket.getRetain());
        if (verbose) {
            if (publishPacket.getMessageExpiryInterval().isPresent()) {
                appendJsonField(sb, "messageExpiryInterval", publishPacket.getMessageExpiryInterval().get());
            }
            appendJsonField(sb, "duplicateDelivery", publishPacket.getDupFlag());
            if (publishPacket.getCorrelationData().isPresent()) {
                appendJsonField(sb,
                        "correlationData",
                        getStringFromByteBuffer(publishPacket.getCorrelationData().get()));
            }
            if (publishPacket.getResponseTopic().isPresent()) {
                appendJsonField(sb, "responseTopic", publishPacket.getResponseTopic().get());
            }
            if (publishPacket.getContentType().isPresent()) {
                appendJsonField(sb, "contentType", publishPacket.getContentType().get());
            }
            if (publishPacket.getPayloadFormatIndicator().isPresent()) {
                appendJsonField(sb, "payloadFormatIndicator", publishPacket.getPayloadFormatIndicator().get().name());
            }
            if (!publishPacket.getSubscriptionIdentifiers().isEmpty()) {
                appendJsonArray(sb, "subscriptionIdentifiers", publishPacket.getSubscriptionIdentifiers());
            }
            appendUserPropertiesJson(sb, publishPacket.getUserProperties());
        }
        sb.append("}");
        LOG.info(sb.toString());
    }

    @Override
    public void logSubscribe(final @NotNull SubscribeInboundInput subscribeInboundInput) {
        final var sb = new StringBuilder(1024);
        final var clientId = subscribeInboundInput.getClientInformation().getClientId();
        final var subscribePacket = subscribeInboundInput.getSubscribePacket();
        sb.append("{\"timestamp\":").append(System.currentTimeMillis());
        appendJsonField(sb, "messageType", "SUBSCRIBE");
        appendJsonField(sb, "direction", "INBOUND");
        appendJsonField(sb, "clientId", clientId);
        // subscriptions array
        sb.append(",\"subscriptions\":[");
        final var subscriptions = subscribePacket.getSubscriptions();
        for (int i = 0; i < subscriptions.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            final var sub = subscriptions.get(i);
            sb.append("{");
            sb.append("\"topicFilter\":\"");
            appendJsonEscaped(sb, sub.getTopicFilter());
            sb.append("\",\"qos\":").append(sub.getQos().getQosNumber());
            if (verbose) {
                sb.append(",\"retainAsPublished\":").append(sub.getRetainAsPublished());
                sb.append(",\"noLocal\":").append(sub.getNoLocal());
                sb.append(",\"retainHandling\":\"").append(sub.getRetainHandling().name()).append("\"");
            }
            sb.append("}");
        }
        sb.append("]");
        if (verbose) {
            if (subscribePacket.getSubscriptionIdentifier().isPresent()) {
                appendJsonField(sb, "subscriptionIdentifier", subscribePacket.getSubscriptionIdentifier().get());
            }
            appendUserPropertiesJson(sb, subscribePacket.getUserProperties());
        }
        sb.append("}");
        LOG.info(sb.toString());
    }

    @Override
    public void logUnsubscribe(final @NotNull UnsubscribeInboundInput unsubscribeInboundInput) {
        final var sb = new StringBuilder(512);
        final var clientId = unsubscribeInboundInput.getClientInformation().getClientId();
        final var unsubscribePacket = unsubscribeInboundInput.getUnsubscribePacket();
        sb.append("{\"timestamp\":").append(System.currentTimeMillis());
        appendJsonField(sb, "messageType", "UNSUBSCRIBE");
        appendJsonField(sb, "direction", "INBOUND");
        appendJsonField(sb, "clientId", clientId);
        // topic filters array
        sb.append(",\"topicFilters\":[");
        final var topicFilters = unsubscribePacket.getTopicFilters();
        for (int i = 0; i < topicFilters.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"");
            appendJsonEscaped(sb, topicFilters.get(i));
            sb.append("\"");
        }
        sb.append("]");
        if (verbose) {
            appendUserPropertiesJson(sb, unsubscribePacket.getUserProperties());
        }
        sb.append("}");
        LOG.info(sb.toString());
    }

    @Override
    public void logSuback(final @NotNull SubackOutboundInput subackOutboundInput) {
        final var subackPacket = subackOutboundInput.getSubackPacket();
        logSubackJson("SUBACK",
                subackOutboundInput.getClientInformation().getClientId(),
                subackPacket.getReasonCodes(),
                subackPacket.getReasonString().orElse(null),
                subackPacket.getUserProperties());
    }

    @Override
    public void logUnsuback(final @NotNull UnsubackOutboundInput unsubackOutboundInput) {
        final var unsubackPacket = unsubackOutboundInput.getUnsubackPacket();
        logSubackJson("UNSUBACK",
                unsubackOutboundInput.getClientInformation().getClientId(),
                unsubackPacket.getReasonCodes(),
                unsubackPacket.getReasonString().orElse(null),
                unsubackPacket.getUserProperties());
    }

    private void logSubackJson(
            final @NotNull String messageType,
            final @NotNull String clientId,
            final @NotNull List<? extends Enum<?>> reasonCodes,
            final @Nullable String reasonString,
            final @NotNull UserProperties userProperties) {
        final var sb = new StringBuilder(512);
        sb.append("{\"timestamp\":").append(System.currentTimeMillis());
        appendJsonField(sb, "messageType", messageType);
        appendJsonField(sb, "direction", "OUTBOUND");
        appendJsonField(sb, "clientId", clientId);
        // reason codes array
        sb.append(",\"reasonCodes\":[");
        for (int i = 0; i < reasonCodes.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("\"").append(reasonCodes.get(i).name()).append("\"");
        }
        sb.append("]");
        if (verbose) {
            if (reasonString != null) {
                appendJsonField(sb, "reasonString", reasonString);
            }
            appendUserPropertiesJson(sb, userProperties);
        }
        sb.append("}");
        LOG.info(sb.toString());
    }

    @Override
    public void logPingreq(final @NotNull PingReqInboundInput pingReqInboundInput) {
        final var sb = new StringBuilder(256);
        final var clientId = pingReqInboundInput.getClientInformation().getClientId();
        sb.append("{\"timestamp\":").append(System.currentTimeMillis());
        appendJsonField(sb, "messageType", "PINGREQ");
        appendJsonField(sb, "direction", "INBOUND");
        appendJsonField(sb, "clientId", clientId);
        sb.append("}");
        LOG.info(sb.toString());
    }

    @Override
    public void logPingresp(final @NotNull PingRespOutboundInput pingRespOutboundInput) {
        final var sb = new StringBuilder(256);
        final var clientId = pingRespOutboundInput.getClientInformation().getClientId();
        sb.append("{\"timestamp\":").append(System.currentTimeMillis());
        appendJsonField(sb, "messageType", "PINGRESP");
        appendJsonField(sb, "direction", "OUTBOUND");
        appendJsonField(sb, "clientId", clientId);
        sb.append("}");
        LOG.info(sb.toString());
    }

    @Override
    public void logPuback(
            final @NotNull PubackPacket pubackPacket,
            final @NotNull String clientId,
            final boolean inbound) {
        logPubJson("PUBACK",
                clientId,
                inbound,
                pubackPacket.getReasonCode().name(),
                pubackPacket.getReasonString().orElse(null),
                pubackPacket.getUserProperties());
    }

    @Override
    public void logPubrec(
            final @NotNull PubrecPacket pubrecPacket,
            final @NotNull String clientId,
            final boolean inbound) {
        logPubJson("PUBREC",
                clientId,
                inbound,
                pubrecPacket.getReasonCode().name(),
                pubrecPacket.getReasonString().orElse(null),
                pubrecPacket.getUserProperties());
    }

    @Override
    public void logPubrel(
            final @NotNull PubrelPacket pubrelPacket,
            final @NotNull String clientId,
            final boolean inbound) {
        logPubJson("PUBREL",
                clientId,
                inbound,
                pubrelPacket.getReasonCode().name(),
                pubrelPacket.getReasonString().orElse(null),
                pubrelPacket.getUserProperties());
    }

    @Override
    public void logPubcomp(
            final @NotNull PubcompPacket pubcompPacket,
            final @NotNull String clientId,
            final boolean inbound) {
        logPubJson("PUBCOMP",
                clientId,
                inbound,
                pubcompPacket.getReasonCode().name(),
                pubcompPacket.getReasonString().orElse(null),
                pubcompPacket.getUserProperties());
    }

    private void logPubJson(
            final @NotNull String messageType,
            final @NotNull String clientId,
            final boolean inbound,
            final @NotNull String reasonCode,
            final @Nullable String reasonString,
            final @NotNull UserProperties userProperties) {
        final var sb = new StringBuilder(512);
        sb.append("{\"timestamp\":").append(System.currentTimeMillis());
        appendJsonField(sb, "messageType", messageType);
        appendJsonField(sb, "direction", inbound ? "INBOUND" : "OUTBOUND");
        appendJsonField(sb, "clientId", clientId);
        appendJsonField(sb, "reasonCode", reasonCode);
        if (verbose) {
            if (reasonString != null) {
                appendJsonField(sb, "reasonString", reasonString);
            }
            appendUserPropertiesJson(sb, userProperties);
        }
        sb.append("}");
        LOG.info(sb.toString());
    }

    private static void appendJsonField(
            final @NotNull StringBuilder sb,
            final @NotNull String key,
            final @Nullable String value) {
        if (value == null) {
            return;
        }
        sb.append(",\"").append(key).append("\":\"");
        appendJsonEscaped(sb, value);
        sb.append("\"");
    }

    private static void appendJsonField(final @NotNull StringBuilder sb, final @NotNull String key, final long value) {
        sb.append(",\"").append(key).append("\":").append(value);
    }

    private static void appendJsonField(final @NotNull StringBuilder sb, final @NotNull String key, final int value) {
        sb.append(",\"").append(key).append("\":").append(value);
    }

    private static void appendJsonField(
            final @NotNull StringBuilder sb,
            final @NotNull String key,
            final boolean value) {
        sb.append(",\"").append(key).append("\":").append(value);
    }

    private static void appendJsonEscaped(final @NotNull StringBuilder sb, final @NotNull String str) {
        for (int i = 0; i < str.length(); i++) {
            final var c = str.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                default:
                    if (c < 32) {
                        // control character - Unicode escape
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                    break;
            }
        }
    }

    private static void appendUserPropertiesJson(
            final @NotNull StringBuilder sb,
            final @NotNull UserProperties userProperties) {
        final var list = userProperties.asList();
        if (list.isEmpty()) {
            return;
        }
        sb.append(",\"userProperties\":[");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append("{\"name\":\"");
            appendJsonEscaped(sb, list.get(i).getName());
            sb.append("\",\"value\":\"");
            appendJsonEscaped(sb, list.get(i).getValue());
            sb.append("\"}");
        }
        sb.append("]");
    }

    @SuppressWarnings("SameParameterValue")
    private static void appendJsonArray(
            final @NotNull StringBuilder sb,
            final @NotNull String key,
            final @NotNull List<Integer> values) {
        if (values.isEmpty()) {
            return;
        }
        sb.append(",\"").append(key).append("\":[");
        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(values.get(i));
        }
        sb.append("]");
    }

    private static void appendWillJson(
            final @NotNull StringBuilder sb,
            final @NotNull WillPublishPacket willPublishPacket,
            final boolean includePayload) {
        sb.append(",\"will\":{");
        sb.append("\"topic\":\"");
        appendJsonEscaped(sb, willPublishPacket.getTopic());
        sb.append("\",\"qos\":").append(willPublishPacket.getQos().getQosNumber());
        sb.append(",\"retained\":").append(willPublishPacket.getRetain());
        sb.append(",\"willDelay\":").append(willPublishPacket.getWillDelay());
        if (includePayload && willPublishPacket.getPayload().isPresent()) {
            final var payloadBuffer = willPublishPacket.getPayload().get();
            appendOptionalBinary(payloadBuffer, sb, "payload", "payloadBase64");
        }
        if (willPublishPacket.getMessageExpiryInterval().isPresent()) {
            sb.append(",\"messageExpiryInterval\":").append(willPublishPacket.getMessageExpiryInterval().get());
        }
        if (willPublishPacket.getCorrelationData().isPresent()) {
            final var stringFromByteBuffer = getStringFromByteBuffer(willPublishPacket.getCorrelationData().get());
            if (stringFromByteBuffer != null) {
                sb.append(",\"correlationData\":\"");
                appendJsonEscaped(sb, stringFromByteBuffer);
                sb.append("\"");
            }
        }
        if (willPublishPacket.getResponseTopic().isPresent()) {
            sb.append(",\"responseTopic\":\"");
            appendJsonEscaped(sb, willPublishPacket.getResponseTopic().get());
            sb.append("\"");
        }
        if (willPublishPacket.getContentType().isPresent()) {
            sb.append(",\"contentType\":\"");
            appendJsonEscaped(sb, willPublishPacket.getContentType().get());
            sb.append("\"");
        }
        if (willPublishPacket.getPayloadFormatIndicator().isPresent()) {
            sb.append(",\"payloadFormatIndicator\":\"");
            sb.append(willPublishPacket.getPayloadFormatIndicator().get().name());
            sb.append("\"");
        }
        final var userProps = willPublishPacket.getUserProperties();
        if (!userProps.asList().isEmpty()) {
            appendUserPropertiesJson(sb, userProps);
        }
        sb.append("}");
    }

    private static void appendOptionalBinary(
            final @NotNull ByteBuffer buffer,
            final @NotNull StringBuilder sb,
            final @NotNull String asciiKey,
            final @NotNull String base64Key) {
        final var bufferString = getStringFromByteBuffer(buffer);
        if (StringUtils.isAsciiPrintable(bufferString)) {
            appendJsonField(sb, asciiKey, bufferString);
        } else {
            appendJsonField(sb, base64Key, Base64.getEncoder().encodeToString(getBytes(buffer)));
        }
    }
}
