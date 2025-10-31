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
import com.hivemq.extension.sdk.api.packets.general.DisconnectedReasonCode;
import com.hivemq.extensions.log.mqtt.message.util.LogbackTestAppender;
import com.hivemq.extensions.log.mqtt.message.util.PacketUtil;
import org.assertj.core.data.MapEntry;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.TestDisconnect;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.TestUserProperties;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createConnectWithBinaryPassword;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptyConnack;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptyConnect;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptyDisconnect;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptyPuback;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptyPubcomp;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptyPublish;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptyPubrec;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptyPubrel;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptySuback;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptySubscribe;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptyUnsuback;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createEmptyUnsubscribe;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullConnack;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullConnect;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullDisconnect;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullPuback;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullPubcomp;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullPublish;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullPublishWithBinaryPayload;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullPubrec;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullPubrel;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullSuback;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullSubsribe;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullUnsuback;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullUnsubsribe;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createPingreq;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createPingresp;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

/**
 * @since 1.3.0
 */
class JsonMessageLoggerTest {

    @RegisterExtension
    private final @NotNull LogbackTestAppender logbackTestAppender = LogbackTestAppender.createFor(MessageLogger.LOG);

    private final @NotNull DisconnectEventInput testDisconnect =
            new PacketUtil.TestDisconnect(DisconnectedReasonCode.BANNED,
                    "banned",
                    new PacketUtil.TestUserProperties(0));

    @Test
    void test_log_lifecycle_disconnect_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logDisconnect("Received DISCONNECT from client 'test-client-id'",
                new TestDisconnect(DisconnectedReasonCode.BAD_AUTHENTICATION_METHOD,
                        "ReasonString",
                        new TestUserProperties(5)));
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.EVENT,
                List.of(entry("message", "Received DISCONNECT from client 'test-client-id'"),
                        entry("reasonCode", "BAD_AUTHENTICATION_METHOD"),
                        entry("reasonString", "ReasonString"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1")),
                                        List.of(entry("name", "name2"), entry("value", "value2")),
                                        List.of(entry("name", "name3"), entry("value", "value3")),
                                        List.of(entry("name", "name4"), entry("value", "value4"))))))));
    }

    @Test
    void test_log_lifecycle_disconnect_verbose_none_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logDisconnect("Received DISCONNECT from client 'test-client-id'", new TestDisconnect(null, null, null));
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.EVENT,
                List.of(entry("message", "Received DISCONNECT from client 'test-client-id'"))));
    }

    @Test
    void test_log_lifecycle_disconnect_verbose_user_properties_empty() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logDisconnect("Received DISCONNECT from client 'test-client-id'",
                new TestDisconnect(null, null, new TestUserProperties(0)));
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.EVENT,
                List.of(entry("message", "Received DISCONNECT from client 'test-client-id'"))));
    }

    @Test
    void test_log_lifecycle_disconnect_not_verbose() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logDisconnect("Received DISCONNECT from client 'test-client-id'", new TestDisconnect(null, null, null));
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.EVENT,
                List.of(entry("message", "Received DISCONNECT from client 'test-client-id'"))));
    }

    @Test
    void test_log_inbound_disconnect_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logDisconnect(createFullDisconnect(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "NOT_AUTHORIZED"),
                        entry("reasonString", "Okay"),
                        entry("serverReference", "Server2"),
                        entry("sessionExpiryInterval", 123),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))))));
    }

    @Test
    void test_log_inbound_disconnect_verbose_none_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logDisconnect(createEmptyDisconnect(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NOT_AUTHORIZED"))));
    }

    @Test
    void test_log_inbound_disconnect_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logDisconnect(createFullDisconnect(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NOT_AUTHORIZED"))));
    }

    @Test
    void test_log_inbound_disconnect_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logDisconnect(createFullDisconnect(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NOT_AUTHORIZED"))));
    }

    @Test
    void test_log_outbound_disconnect_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logDisconnect(createFullDisconnect(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "NOT_AUTHORIZED"),
                        entry("reasonString", "Okay"),
                        entry("serverReference", "Server2"),
                        entry("sessionExpiryInterval", 123),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))))));
    }

    @Test
    void test_log_outbound_disconnect_verbose_none_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logDisconnect(createEmptyDisconnect(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NOT_AUTHORIZED"))));
    }

    @Test
    void test_log_outbound_disconnect_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logDisconnect(createFullDisconnect(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NOT_AUTHORIZED"))));
    }

    @Test
    void test_log_outbound_disconnect_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logDisconnect(createEmptyDisconnect(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.DISCONNECT,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NOT_AUTHORIZED"))));
    }

    @Test
    void test_log_connect_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logConnect(createFullConnect());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("protocolVersion", "V_5"),
                        entry("cleanStart", false),
                        entry("sessionExpiryInterval", 10000),
                        entry("keepAlive", 20000),
                        entry("maximumPacketSize", 40000),
                        entry("receiveMaximum", 30000),
                        entry("topicAliasMaximum", 50000),
                        entry("requestProblemInformation", true),
                        entry("requestResponseInformation", false),
                        entry("username", "the username"),
                        entry("password", "the password"),
                        entry("authMethod", "auth method"),
                        entry("authDataBase64", "YXV0aCBkYXRh"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))),
                        entry("will",
                                new ObjectType(List.of(entry("topic", "willtopic"),
                                        entry("qos", 1),
                                        entry("retained", false),
                                        entry("willDelay", 100),
                                        entry("payload", "payload"),
                                        entry("messageExpiryInterval", 1234),
                                        entry("correlationData", "data"),
                                        entry("responseTopic", "response topic"),
                                        entry("contentType", "content type"),
                                        entry("payloadFormatIndicator", "UTF_8"),
                                        entry("userProperties", new ArrayType( //
                                                List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                                        List.of(entry("name", "name1"), entry("value", "value1")),
                                                        List.of(entry("name", "name2"), entry("value", "value2")))) //
                                        )))))));
    }

    @Test
    void test_log_connect_verbose_no_payload_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logConnect(createFullConnect());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("protocolVersion", "V_5"),
                        entry("cleanStart", false),
                        entry("sessionExpiryInterval", 10000),
                        entry("keepAlive", 20000),
                        entry("maximumPacketSize", 40000),
                        entry("receiveMaximum", 30000),
                        entry("topicAliasMaximum", 50000),
                        entry("requestProblemInformation", true),
                        entry("requestResponseInformation", false),
                        entry("username", "the username"),
                        entry("password", "the password"),
                        entry("authMethod", "auth method"),
                        entry("authDataBase64", "YXV0aCBkYXRh"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))),
                        entry("will",
                                new ObjectType(List.of(entry("topic", "willtopic"),
                                        entry("qos", 1),
                                        entry("retained", false),
                                        entry("willDelay", 100),
                                        entry("messageExpiryInterval", 1234),
                                        entry("correlationData", "data"),
                                        entry("responseTopic", "response topic"),
                                        entry("contentType", "content type"),
                                        entry("payloadFormatIndicator", "UTF_8"),
                                        entry("userProperties", new ArrayType( //
                                                List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                                        List.of(entry("name", "name1"), entry("value", "value1")),
                                                        List.of(entry("name", "name2"), entry("value", "value2")))) //
                                        )))))));
    }

    @Test
    void test_log_connect_verbose_none_set() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logConnect(createEmptyConnect());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("protocolVersion", "V_5"),
                        entry("cleanStart", false),
                        entry("sessionExpiryInterval", 10000),
                        entry("keepAlive", 0),
                        entry("maximumPacketSize", 0),
                        entry("receiveMaximum", 0),
                        entry("topicAliasMaximum", 0),
                        entry("requestProblemInformation", false),
                        entry("requestResponseInformation", false))));
    }

    @Test
    void test_log_connect_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, true, false);
        logger.logConnect(createFullConnect());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("protocolVersion", "V_5"),
                        entry("cleanStart", false),
                        entry("sessionExpiryInterval", 10000))));
    }

    @Test
    void test_log_connect_verbose_redact_password_all_set() {
        final var logger = new JsonMessageLogger(true, true, true);
        logger.logConnect(createFullConnect());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("protocolVersion", "V_5"),
                        entry("cleanStart", false),
                        entry("sessionExpiryInterval", 10000),
                        entry("keepAlive", 20000),
                        entry("maximumPacketSize", 40000),
                        entry("receiveMaximum", 30000),
                        entry("topicAliasMaximum", 50000),
                        entry("requestProblemInformation", true),
                        entry("requestResponseInformation", false),
                        entry("username", "the username"),
                        entry("password", "<redacted>"),
                        entry("authMethod", "auth method"),
                        entry("authDataBase64", "YXV0aCBkYXRh"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))),
                        entry("will",
                                new ObjectType(List.of(entry("topic", "willtopic"),
                                        entry("qos", 1),
                                        entry("retained", false),
                                        entry("willDelay", 100),
                                        entry("payload", "payload"),
                                        entry("messageExpiryInterval", 1234),
                                        entry("correlationData", "data"),
                                        entry("responseTopic", "response topic"),
                                        entry("contentType", "content type"),
                                        entry("payloadFormatIndicator", "UTF_8"),
                                        entry("userProperties", new ArrayType( //
                                                List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                                        List.of(entry("name", "name1"), entry("value", "value1")),
                                                        List.of(entry("name", "name2"), entry("value", "value2")))) //
                                        )))))));
    }

    @Test
    void test_log_connect_verbose_redact_payload_no_password_all_set() {
        final var logger = new JsonMessageLogger(true, false, true);
        logger.logConnect(createFullConnect());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("protocolVersion", "V_5"),
                        entry("cleanStart", false),
                        entry("sessionExpiryInterval", 10000),
                        entry("keepAlive", 20000),
                        entry("maximumPacketSize", 40000),
                        entry("receiveMaximum", 30000),
                        entry("topicAliasMaximum", 50000),
                        entry("requestProblemInformation", true),
                        entry("requestResponseInformation", false),
                        entry("username", "the username"),
                        entry("password", "<redacted>"),
                        entry("authMethod", "auth method"),
                        entry("authDataBase64", "YXV0aCBkYXRh"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))),
                        entry("will",
                                new ObjectType(List.of(entry("topic", "willtopic"),
                                        entry("qos", 1),
                                        entry("retained", false),
                                        entry("willDelay", 100),
                                        entry("messageExpiryInterval", 1234),
                                        entry("correlationData", "data"),
                                        entry("responseTopic", "response topic"),
                                        entry("contentType", "content type"),
                                        entry("payloadFormatIndicator", "UTF_8"),
                                        entry("userProperties", new ArrayType( //
                                                List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                                        List.of(entry("name", "name1"), entry("value", "value1")),
                                                        List.of(entry("name", "name2"), entry("value", "value2")))) //
                                        )))))));
    }

    @Test
    void test_log_connect_verbose_redact_password_none_set() {
        final var logger = new JsonMessageLogger(true, true, true);
        logger.logConnect(createEmptyConnect());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("protocolVersion", "V_5"),
                        entry("cleanStart", false),
                        entry("sessionExpiryInterval", 10000),
                        entry("keepAlive", 0),
                        entry("maximumPacketSize", 0),
                        entry("receiveMaximum", 0),
                        entry("topicAliasMaximum", 0),
                        entry("requestProblemInformation", false),
                        entry("requestResponseInformation", false),
                        entry("password", "<redacted>"))));
    }

    @Test
    void test_log_connect_verbose_binary_password_shows_hex() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logConnect(createConnectWithBinaryPassword());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNECT,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("protocolVersion", "V_5"),
                        entry("cleanStart", false),
                        entry("sessionExpiryInterval", 10000),
                        entry("keepAlive", 0),
                        entry("maximumPacketSize", 0),
                        entry("receiveMaximum", 0),
                        entry("topicAliasMaximum", 0),
                        entry("requestProblemInformation", false),
                        entry("requestResponseInformation", false),
                        entry("username", "testuser"),
                        entry("passwordBase64", "AAEC//5/"))));
    }

    @Test
    void test_log_connack_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logConnack(createFullConnack());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "SUCCESS"),
                        entry("sessionPresent", false),
                        entry("sessionExpiryInterval", 100),
                        entry("assignedClientId", "overwriteClientId"),
                        entry("maximumQoS", 0),
                        entry("maximumPacketSize", 5),
                        entry("receiveMaximum", 10),
                        entry("topicAliasMaximum", 5),
                        entry("reasonString", "Okay"),
                        entry("responseInformation", "Everything fine"),
                        entry("serverKeepAlive", 100),
                        entry("serverReference", "Server2"),
                        entry("sharedSubscriptionsAvailable", false),
                        entry("wildCardSubscriptionAvailable", false),
                        entry("retainAvailable", false),
                        entry("subscriptionIdentifiersAvailable", false),
                        entry("authMethod", "JSON"),
                        entry("authDataBase64", "YXV0aCBkYXRh"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))))));
    }

    @Test
    void test_log_connack_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logConnack(createFullConnack());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "SUCCESS"),
                        entry("sessionPresent", false))));
    }

    @Test
    void test_log_connack_verbose_none_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logConnack(createEmptyConnack());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "SUCCESS"),
                        entry("sessionPresent", false),
                        entry("maximumPacketSize", 2),
                        entry("receiveMaximum", 1),
                        entry("topicAliasMaximum", 3),
                        entry("sharedSubscriptionsAvailable", false),
                        entry("wildCardSubscriptionAvailable", false),
                        entry("retainAvailable", false),
                        entry("subscriptionIdentifiersAvailable", false))));
    }

    @Test
    void test_log_connack_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logConnack(createEmptyConnack());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.CONNACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "SUCCESS"),
                        entry("sessionPresent", false))));
    }

    @Test
    void test_log_publish_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logPublish("Sent PUBLISH to client 'test-client-id' on topic", createFullPublish());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBLISH,
                Direction.OUTBOUND,
                List.of(entry("topic", "topic"),
                        entry("payload", "message"),
                        entry("qos", 1),
                        entry("retained", false),
                        entry("messageExpiryInterval", 10000),
                        entry("duplicateDelivery", false),
                        entry("correlationData", "data"),
                        entry("responseTopic", "response topic"),
                        entry("contentType", "content type"),
                        entry("payloadFormatIndicator", "UTF_8"),
                        entry("subscriptionIdentifiers", "[1,2,3,4]"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))))));
    }

    @Test
    void test_log_publish_verbose_no_payload_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPublish("Sent PUBLISH to client 'test-client-id' on topic", createFullPublish());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBLISH,
                Direction.OUTBOUND,
                List.of(entry("topic", "topic"),
                        entry("qos", 1),
                        entry("retained", false),
                        entry("messageExpiryInterval", 10000),
                        entry("duplicateDelivery", false),
                        entry("correlationData", "data"),
                        entry("responseTopic", "response topic"),
                        entry("contentType", "content type"),
                        entry("payloadFormatIndicator", "UTF_8"),
                        entry("subscriptionIdentifiers", "[1,2,3,4]"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))))));
    }

    @Test
    void test_log_publish_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, true, false);
        logger.logPublish("Sent PUBLISH to client 'test-client-id' on topic", createFullPublish());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBLISH,
                Direction.OUTBOUND,
                List.of(entry("topic", "topic"),
                        entry("payload", "message"),
                        entry("qos", 1),
                        entry("retained", false))));
    }

    @Test
    void test_log_publish_not_verbose_no_payload_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPublish("Sent PUBLISH to client 'test-client-id' on topic", createFullPublish());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBLISH,
                Direction.OUTBOUND,
                List.of(entry("topic", "topic"), entry("qos", 1), entry("retained", false))));
    }

    @Test
    void test_log_publish_verbose_none_set() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logPublish("Sent PUBLISH to client 'test-client-id' on topic", createEmptyPublish());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBLISH,
                Direction.OUTBOUND,
                List.of(entry("topic", "topic"),
                        entry("payload", "message"),
                        entry("qos", 1),
                        entry("retained", false),
                        entry("duplicateDelivery", false))));
    }

    @Test
    void test_log_publish_verbose_no_payload_none_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPublish("Sent PUBLISH to client 'test-client-id' on topic", createEmptyPublish());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBLISH,
                Direction.OUTBOUND,
                List.of(entry("topic", "topic"),
                        entry("qos", 1),
                        entry("retained", false),
                        entry("duplicateDelivery", false))));
    }

    @Test
    void test_log_publish_verbose_binary_payload_shows_base64() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logPublish("Sent PUBLISH to client 'test-client-id' on topic", createFullPublishWithBinaryPayload());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBLISH,
                Direction.OUTBOUND,
                List.of(entry("topic", "topic"),
                        entry("payloadBase64", "AAEC//5/SGVsbG8="),
                        entry("qos", 1),
                        entry("retained", false),
                        entry("messageExpiryInterval", 10000),
                        entry("duplicateDelivery", false),
                        entry("correlationData", "data"),
                        entry("responseTopic", "response topic"),
                        entry("contentType", "application/octet-stream"),
                        entry("payloadFormatIndicator", "UNSPECIFIED"),
                        entry("subscriptionIdentifiers", "[1,2,3,4]"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))))));
    }

    @Test
    void test_log_publish_not_verbose_binary_payload_shows_base64() {
        final var logger = new JsonMessageLogger(false, true, false);
        logger.logPublish("Sent PUBLISH to client 'test-client-id' on topic", createFullPublishWithBinaryPayload());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBLISH,
                Direction.OUTBOUND,
                List.of(entry("topic", "topic"),
                        entry("payloadBase64", "AAEC//5/SGVsbG8="),
                        entry("qos", 1),
                        entry("retained", false))));
    }

    @Test
    void test_log_subscribe_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logSubscribe(createFullSubsribe());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.SUBSCRIBE,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("subscriptions",
                                new ArrayType(List.of(List.of(entry("topicFilter", "topic1"),
                                                entry("qos", 2),
                                                entry("retainAsPublished", false),
                                                entry("noLocal", false),
                                                entry("retainHandling", "DO_NOT_SEND")),
                                        List.of(entry("topicFilter", "topic2"),
                                                entry("qos", 0),
                                                entry("retainAsPublished", true),
                                                entry("noLocal", true),
                                                entry("retainHandling", "SEND_IF_NEW_SUBSCRIPTION"))))),
                        entry("subscriptionIdentifier", 10),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))))));
    }

    @Test
    void test_log_subscribe_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logSubscribe(createFullSubsribe());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.SUBSCRIBE,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("subscriptions",
                                new ArrayType(List.of(List.of(entry("topicFilter", "topic1"), entry("qos", 2)),
                                        List.of(entry("topicFilter", "topic2"), entry("qos", 0))))))));
    }

    @Test
    void test_log_subscribe_verbose_none_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logSubscribe(createEmptySubscribe());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.SUBSCRIBE,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("subscriptions",
                                new ArrayType(List.of(List.of(entry("topicFilter", "topic"),
                                        entry("qos", 0),
                                        entry("retainAsPublished", false),
                                        entry("noLocal", false),
                                        entry("retainHandling", "SEND"))))))));
    }

    @Test
    void test_log_subscribe_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logSubscribe(createEmptySubscribe());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.SUBSCRIBE,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("subscriptions",
                                new ArrayType(List.of(List.of(entry("topicFilter", "topic"), entry("qos", 0))))))));
    }

    @Test
    void test_log_suback_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logSuback(createFullSuback());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.SUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCodes", """
                                ["GRANTED_QOS_1","GRANTED_QOS_0"]"""),
                        entry("reasonString", "Okay"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))))));
    }

    @Test
    void test_log_suback_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logSuback(createFullSuback());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.SUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCodes", """
                        ["GRANTED_QOS_1","GRANTED_QOS_0"]"""))));
    }

    @Test
    void test_log_suback_verbose_none_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logSuback(createEmptySuback());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.SUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCodes", """
                        ["GRANTED_QOS_1"]"""))));
    }

    @Test
    void test_log_suback_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logSuback(createEmptySuback());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.SUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCodes", """
                        ["GRANTED_QOS_1"]"""))));
    }

    @Test
    void test_log_unsuback_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logUnsuback(createFullUnsuback());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.UNSUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCodes", """
                                ["NOT_AUTHORIZED","SUCCESS"]"""),
                        entry("reasonString", "Okay"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1")),
                                        List.of(entry("name", "name2"), entry("value", "value2"))))))));
    }

    @Test
    void test_log_unsuback_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logUnsuback(createFullUnsuback());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.UNSUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCodes", """
                        ["NOT_AUTHORIZED","SUCCESS"]"""))));
    }

    @Test
    void test_log_unsuback_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logUnsuback(createEmptyUnsuback());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.UNSUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCodes", """
                        ["NOT_AUTHORIZED"]"""))));
    }

    @Test
    void test_log_unsuback_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logUnsuback(createEmptyUnsuback());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.UNSUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCodes", """
                        ["NOT_AUTHORIZED"]"""))));
    }

    @Test
    void test_log_unsubscribe_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logUnsubscribe(createFullUnsubsribe());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.UNSUBSCRIBE,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("topicFilters", """
                                ["topic1"]"""),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))))));
    }

    @Test
    void test_log_unsubscribe_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logUnsubscribe(createFullUnsubsribe());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.UNSUBSCRIBE,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("topicFilters", """
                        ["topic1"]"""))));
    }

    @Test
    void test_log_unsubscribe_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logUnsubscribe(createEmptyUnsubscribe());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.UNSUBSCRIBE,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("topicFilters", """
                        ["topic1","topic2"]"""))));
    }

    @Test
    void test_log_unsubscribe_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logUnsubscribe(createEmptyUnsubscribe());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.UNSUBSCRIBE,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("topicFilters", """
                        ["topic1","topic2"]"""))));
    }

    @Test
    void test_log_pingreq() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPingreq(createPingreq());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PINGREQ,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"))));
    }

    @Test
    void test_log_pingresp() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPingresp(createPingresp());
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PINGRESP,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"))));
    }

    @Test
    void test_log_puback_inbound_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPuback(createFullPuback(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBACK,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"),
                        entry("reasonString", "Okay"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))))));
    }

    @Test
    void test_log_puback_inbound_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPuback(createFullPuback(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBACK,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"))));
    }

    @Test
    void test_log_puback_inbound_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPuback(createEmptyPuback(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBACK,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"))));
    }

    @Test
    void test_log_puback_inbound_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPuback(createEmptyPuback(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBACK,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"))));
    }

    @Test
    void test_log_puback_outbound_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPuback(createFullPuback(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"),
                        entry("reasonString", "Okay"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0")),
                                        List.of(entry("name", "name1"), entry("value", "value1"))))))));
    }

    @Test
    void test_log_puback_outbound_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPuback(createFullPuback(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"))));
    }

    @Test
    void test_log_puback_outbound_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPuback(createEmptyPuback(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"))));
    }

    @Test
    void test_log_puback_outbound_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPuback(createEmptyPuback(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBACK,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"))));
    }

    @Test
    void test_log_pubrec_inbound_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPubrec(createFullPubrec(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREC,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "SUCCESS"),
                        entry("reasonString", "Okay"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0"))))))));
    }

    @Test
    void test_log_pubrec_inbound_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubrec(createFullPubrec(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREC,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "SUCCESS"))));
    }

    @Test
    void test_log_pubrec_inbound_verbose_none_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPubrec(createEmptyPubrec(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREC,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"))));
    }

    @Test
    void test_log_pubrec_inbound_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubrec(createEmptyPubrec(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREC,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"))));
    }

    @Test
    void test_log_pubrec_outbound_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPubrec(createFullPubrec(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREC,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "SUCCESS"),
                        entry("reasonString", "Okay"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0"))))))));
    }

    @Test
    void test_log_pubrec_outbound_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubrec(createFullPubrec(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREC,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "SUCCESS"))));
    }

    @Test
    void test_log_pubrec_outbound_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubrec(createEmptyPubrec(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREC,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"))));
    }

    @Test
    void test_log_pubrec_outbound_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubrec(createEmptyPubrec(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREC,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "NO_MATCHING_SUBSCRIBERS"))));
    }

    @Test
    void test_log_pubrel_inbound_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPubrel(createFullPubrel(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREL,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "PACKET_IDENTIFIER_NOT_FOUND"),
                        entry("reasonString", "Okay"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0"))))))));
    }

    @Test
    void test_log_pubrel_inbound_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubrel(createFullPubrel(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREL,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "PACKET_IDENTIFIER_NOT_FOUND"))));
    }

    @Test
    void test_log_pubrel_inbound_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubrel(createEmptyPubrel(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREL,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "SUCCESS"))));
    }

    @Test
    void test_log_pubrel_inbound_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubrel(createEmptyPubrel(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREL,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "SUCCESS"))));
    }

    @Test
    void test_log_pubrel_outbound_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPubrel(createFullPubrel(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREL,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "PACKET_IDENTIFIER_NOT_FOUND"),
                        entry("reasonString", "Okay"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0"))))))));
    }

    @Test
    void test_log_pubrel_outbound_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubrel(createFullPubrel(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREL,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "PACKET_IDENTIFIER_NOT_FOUND"))));
    }

    @Test
    void test_log_pubrel_outbound_verbose_none_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPubrel(createEmptyPubrel(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREL,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "SUCCESS"))));
    }

    @Test
    void test_log_pubrel_outbound_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubrel(createEmptyPubrel(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBREL,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "SUCCESS"))));
    }

    @Test
    void test_log_pubcomp_inbound_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPubcomp(createFullPubcomp(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBCOMP,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "PACKET_IDENTIFIER_NOT_FOUND"),
                        entry("reasonString", "Okay"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0"))))))));
    }

    @Test
    void test_log_pubcomp_inbound_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubcomp(createFullPubcomp(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBCOMP,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "PACKET_IDENTIFIER_NOT_FOUND"))));
    }

    @Test
    void test_log_pubcomp_inbound_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubcomp(createEmptyPubcomp(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBCOMP,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "SUCCESS"))));
    }

    @Test
    void test_log_pubcomp_inbound_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubcomp(createEmptyPubcomp(), "test-client-id", true);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBCOMP,
                Direction.INBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "SUCCESS"))));
    }

    @Test
    void test_log_pubcomp_outbound_verbose_all_set() {
        final var logger = new JsonMessageLogger(true, false, false);
        logger.logPubcomp(createFullPubcomp(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBCOMP,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"),
                        entry("reasonCode", "PACKET_IDENTIFIER_NOT_FOUND"),
                        entry("reasonString", "Okay"),
                        entry("userProperties",
                                new ArrayType(List.of(List.of(entry("name", "name0"), entry("value", "value0"))))))));
    }

    @Test
    void test_log_pubcomp_outbound_not_verbose_all_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubcomp(createFullPubcomp(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBCOMP,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "PACKET_IDENTIFIER_NOT_FOUND"))));
    }

    @Test
    void test_log_pubcomp_outbound_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubcomp(createEmptyPubcomp(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBCOMP,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "SUCCESS"))));
    }

    @Test
    void test_log_pubcomp_outbound_not_verbose_none_set() {
        final var logger = new JsonMessageLogger(false, false, false);
        logger.logPubcomp(createEmptyPubcomp(), "test-client-id", false);
        assertThat(getJsonMessage()).isEqualTo(expectedJson(MessageType.PUBCOMP,
                Direction.OUTBOUND,
                List.of(entry("clientId", "test-client-id"), entry("reasonCode", "SUCCESS"))));
    }

    @Test
    void appendJsonEscaped_doubleQuote() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logDisconnect("foo\"bar", testDisconnect);
        assertThat(getJsonMessage()).contains("\"message\":\"foo\\\"bar\"");
    }

    @Test
    void appendJsonEscaped_backslash() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logDisconnect("foo\\bar", testDisconnect);
        assertThat(getJsonMessage()).contains("\"message\":\"foo\\\\bar\"");
    }

    @Test
    void appendJsonEscaped_newline() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logDisconnect("foo\nbar", testDisconnect);
        assertThat(getJsonMessage()).contains("\"message\":\"foo\\nbar\"");
    }

    @Test
    void appendJsonEscaped_carriageReturn() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logDisconnect("foo\rbar", testDisconnect);
        assertThat(getJsonMessage()).contains("\"message\":\"foo\\rbar\"");
    }

    @Test
    void appendJsonEscaped_tab() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logDisconnect("foo\tbar", testDisconnect);
        assertThat(getJsonMessage()).contains("\"message\":\"foo\\tbar\"");
    }

    @Test
    void appendJsonEscaped_backspace() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logDisconnect("foo\bbar", testDisconnect);
        assertThat(getJsonMessage()).contains("\"message\":\"foo\\bbar\"");
    }

    @Test
    void appendJsonEscaped_formFeed() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logDisconnect("foo\fbar", testDisconnect);
        assertThat(getJsonMessage()).contains("\"message\":\"foo\\fbar\"");
    }

    @Test
    void appendJsonEscaped_controlCharacter() {
        // SOH (Start of Heading) control character
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logDisconnect("foo\u0001bar", testDisconnect);
        assertThat(getJsonMessage()).contains("\"message\":\"foo\\u0001bar\"");
    }

    @Test
    void appendJsonEscaped_multipleSpecialCharacters() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logDisconnect("foo\"bar\\baz\nqux", testDisconnect);
        assertThat(getJsonMessage()).contains("\"message\":\"foo\\\"bar\\\\baz\\nqux\"");
    }

    @Test
    void appendJsonEscaped_regularText() {
        final var logger = new JsonMessageLogger(true, true, false);
        logger.logDisconnect("foobar", testDisconnect);
        assertThat(getJsonMessage()).contains("\"message\":\"foobar\"");
    }

    private @NotNull String getJsonMessage() {
        final var events = logbackTestAppender.getEvents();
        assertThat(events).hasSize(1);
        try {
            return events.getFirst().getFormattedMessage().replaceAll("\"timestamp\":\\d+,", "\"timestamp\":12345,");
        } finally {
            logbackTestAppender.getEvents().clear();
        }
    }

    private static @NotNull String expectedJson(
            final @NotNull MessageType messageType,
            final @NotNull Direction direction,
            final @NotNull List<MapEntry<String, Object>> additionalEntries) {
        final var entries = new ArrayList<MapEntry<String, Object>>();
        entries.add(entry("timestamp", 12345));
        entries.add(entry("messageType", messageType.name()));
        entries.add(entry("direction", direction.name()));
        entries.addAll(additionalEntries);
        return expectedJson(entries);
    }

    private static @NotNull String expectedJson(final @NotNull List<MapEntry<String, Object>> jsonEntries) {
        return "{%s}".formatted(jsonEntries.stream().map(entry -> {
            final var value = entry.getValue();
            if (value instanceof ArrayType(final List<List<MapEntry<String, Object>>> entries)) {
                final var jsonList = new ArrayList<String>();
                entries.forEach(mapEntries -> jsonList.add(expectedJson(mapEntries)));
                return "\"%s\":[%s]".formatted(entry.getKey(), String.join(",", jsonList));
            }
            if (value instanceof ObjectType(final List<MapEntry<String, Object>> entries)) {
                return "\"%s\":%s".formatted(entry.getKey(), expectedJson(entries));
            }
            if (value instanceof final String stringValue) {
                if (!stringValue.startsWith("{") && !stringValue.startsWith("[")) {
                    return "\"%s\":\"%s\"".formatted(entry.getKey(), value);
                }
            }
            return "\"%s\":%s".formatted(entry.getKey(), value);
        }).collect(Collectors.joining(",")));
    }

    private enum MessageType {
        DISCONNECT,
        CONNECT,
        CONNACK,
        PUBLISH,
        SUBSCRIBE,
        SUBACK,
        UNSUBACK,
        UNSUBSCRIBE,
        PINGREQ,
        PINGRESP,
        PUBACK,
        PUBREC,
        PUBREL,
        PUBCOMP,
    }

    private enum Direction {
        EVENT,
        INBOUND,
        OUTBOUND,
    }

    private record ArrayType(@NotNull List<List<MapEntry<String, Object>>> entries) {
    }

    private record ObjectType(@NotNull List<MapEntry<String, Object>> entries) {
    }
}
