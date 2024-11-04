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

import com.hivemq.extension.sdk.api.packets.general.DisconnectedReasonCode;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.TestDisconnect;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.TestUserProperties;
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
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullPubrec;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullPubrel;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullSuback;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullSubsribe;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullUnsuback;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createFullUnsubsribe;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createLifeCycleCompareDisconnect;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createPingreq;
import static com.hivemq.extensions.log.mqtt.message.util.PacketUtil.createPingresp;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @since 1.0.0
 */
class MessageLogUtilTest {

    @RegisterExtension
    private final @NotNull LogbackTestAppender logbackTestAppender = LogbackTestAppender.createFor(MessageLogUtil.LOG);

    @Test
    void test_lifecycle_and_interceptor_disconnect_logs_have_same_format() {
        final String expectedLog =
                "Received DISCONNECT from client 'clientId': Reason Code: 'BAD_AUTHENTICATION_METHOD'";

        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientId':",
                new TestDisconnect(DisconnectedReasonCode.BAD_AUTHENTICATION_METHOD, "Okay", new TestUserProperties(3)),
                false,false);

        assertEquals(expectedLog, logbackTestAppender.getEvents().get(0).getFormattedMessage());

        MessageLogUtil.logDisconnect(createLifeCycleCompareDisconnect(), "clientId", true,false,false);
        assertEquals(expectedLog, logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_lifecycle_disconnect_verbose_all_set() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid':",
                new TestDisconnect(DisconnectedReasonCode.BAD_AUTHENTICATION_METHOD,
                        "ReasonString",
                        new TestUserProperties(5)),
                true,false);
        assertEquals(
                "Received DISCONNECT from client 'clientid': Reason Code: 'BAD_AUTHENTICATION_METHOD', Reason String: 'ReasonString', User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1'], [Name: 'name2', Value: 'value2'], [Name: 'name3', Value: 'value3'], [Name: 'name4', Value: 'value4']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_lifecycle_disconnect_verbose_none_set() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid':",
                new TestDisconnect(null, null, null),
                true,false);
        assertEquals(
                "Received DISCONNECT from client 'clientid': Reason Code: 'null', Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_lifecycle_disconnect_verbose_user_properties_empty() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid':",
                new TestDisconnect(null, null, new TestUserProperties(0)),
                true,false);
        assertEquals(
                "Received DISCONNECT from client 'clientid': Reason Code: 'null', Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_lifecycle_disconnect_not_verbose() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid':",
                new TestDisconnect(null, null, null),
                false,false);
        assertEquals("Received DISCONNECT from client 'clientid': Reason Code: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_inbound_disconnect_verbose_all_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(), "clientId", true, true,false);
        assertEquals(
                "Received DISCONNECT from client 'clientId': Reason Code: 'NOT_AUTHORIZED', Reason String: 'Okay', Server Reference: 'Server2', Session Expiry: '123', User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_inbound_disconnect_verbose_none_set() {
        MessageLogUtil.logDisconnect(createEmptyDisconnect(), "clientId", true, true,false);
        assertEquals(
                "Received DISCONNECT from client 'clientId': Reason Code: 'NOT_AUTHORIZED', Reason String: 'null', Server Reference: 'null', Session Expiry: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_inbound_disconnect_not_verbose_all_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(), "clientId", true, false,false);
        assertEquals("Received DISCONNECT from client 'clientId': Reason Code: 'NOT_AUTHORIZED'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_inbound_disconnect_not_verbose_none_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(), "clientId", true, false,false);
        assertEquals("Received DISCONNECT from client 'clientId': Reason Code: 'NOT_AUTHORIZED'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_outound_disconnect_verbose_all_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(), "clientId", false, true,false);
        assertEquals(
                "Sent DISCONNECT to client 'clientId': Reason Code: 'NOT_AUTHORIZED', Reason String: 'Okay', Server Reference: 'Server2', Session Expiry: '123', User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_outound_disconnect_verbose_none_set() {
        MessageLogUtil.logDisconnect(createEmptyDisconnect(), "clientId", false, true,false);
        assertEquals(
                "Sent DISCONNECT to client 'clientId': Reason Code: 'NOT_AUTHORIZED', Reason String: 'null', Server Reference: 'null', Session Expiry: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_outound_disconnect_not_verbose_all_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(), "clientId", false, false,false);
        assertEquals("Sent DISCONNECT to client 'clientId': Reason Code: 'NOT_AUTHORIZED'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_outound_disconnect_not_verbose_none_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(), "clientId", false, false,false);
        assertEquals("Sent DISCONNECT to client 'clientId': Reason Code: 'NOT_AUTHORIZED'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_connect_verbose_all_set() {
        MessageLogUtil.logConnect(createFullConnect(), true, true,false);
        assertEquals("Received CONNECT from client 'clientid': Protocol version: 'V_5', Clean Start: 'false', " +
                        "Session Expiry Interval: '10000', Keep Alive: '20000', Maximum Packet Size: '40000', " +
                        "Receive Maximum: '30000', Topic Alias Maximum: '50000', Request Problem Information: 'true', " +
                        "Request Response Information: 'false',  Username: 'the username', Password: 'the password', " +
                        "Auth Method: 'auth method', Auth Data (Base64): 'YXV0aCBkYXRh', " +
                        "User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1'], " +
                        "Will: { Topic: 'willtopic', Payload: 'payload', QoS: '1', Retained: 'false', " +
                        "Message Expiry Interval: '1234', Duplicate Delivery: 'false', Correlation Data: 'data', " +
                        "Response Topic: 'response topic', Content Type: 'content type', " +
                        "Payload Format Indicator: 'UTF_8', Subscription Identifiers: '[1, 2, 3, 4]', " +
                        "User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1'], " +
                        "[Name: 'name2', Value: 'value2'], Will Delay: '100' }",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_connect_verbose_no_payload_all_set() {
        MessageLogUtil.logConnect(createFullConnect(), true,false,false);
        assertEquals("Received CONNECT from client 'clientid': Protocol version: 'V_5', Clean Start: 'false', " +
                        "Session Expiry Interval: '10000', Keep Alive: '20000', Maximum Packet Size: '40000', " +
                        "Receive Maximum: '30000', Topic Alias Maximum: '50000', Request Problem Information: 'true', " +
                        "Request Response Information: 'false',  Username: 'the username', Password: 'the password', " +
                        "Auth Method: 'auth method', Auth Data (Base64): 'YXV0aCBkYXRh', " +
                        "User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1'], " +
                        "Will: { Topic: 'willtopic', QoS: '1', Retained: 'false', " +
                        "Message Expiry Interval: '1234', Duplicate Delivery: 'false', Correlation Data: 'data', " +
                        "Response Topic: 'response topic', Content Type: 'content type', " +
                        "Payload Format Indicator: 'UTF_8', Subscription Identifiers: '[1, 2, 3, 4]', " +
                        "User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1'], " +
                        "[Name: 'name2', Value: 'value2'], Will Delay: '100' }",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_connect_verbose_none_set() {
        MessageLogUtil.logConnect(createEmptyConnect(), true, true,false);
        assertEquals("Received CONNECT from client 'clientid': Protocol version: 'V_5', Clean Start: 'false', " +
                        "Session Expiry Interval: '10000', Keep Alive: '0', Maximum Packet Size: '0', Receive Maximum: '0', " +
                        "Topic Alias Maximum: '0', Request Problem Information: 'false', Request Response Information: 'false',  " +
                        "Username: 'null', Password: 'null', Auth Method: 'null', Auth Data (Base64): 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_connect_not_verbose_all_set() {
        MessageLogUtil.logConnect(createFullConnect(), false, true,false);
        assertEquals("Received CONNECT from client 'clientid': Protocol version: 'V_5', Clean Start: 'false', " +
                "Session Expiry Interval: '10000'", logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_connack_verbose_all_set() {
        MessageLogUtil.logConnack(createFullConnack(), true,false);

        assertEquals("Sent CONNACK to client 'clientid': Reason Code: 'SUCCESS', Session Present: 'false'," +
                        " Session Expiry Interval: '100', Assigned ClientId 'overwriteClientId', Maximum QoS: 'AT_MOST_ONCE'," +
                        " Maximum Packet Size: '5', Receive Maximum: '10', Topic Alias Maximum: '5', Reason String: 'Okay'," +
                        " Response Information: 'Everything fine', Server Keep Alive: '100'," +
                        " Server Reference: 'Server2', Shared Subscription Available: 'false'," +
                        " Wildcards Available: 'false', Retain Available: 'false', Subscription Identifiers Available: 'false'," +
                        " Auth Method: 'JSON', Auth Data (Base64): 'YXV0aCBkYXRh', User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_connack_not_verbose_all_set() {
        MessageLogUtil.logConnack(createFullConnack(), false,false);
        assertEquals("Sent CONNACK to client 'clientid': Reason Code: 'SUCCESS', Session Present: 'false'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_connack_verbose_none_set() {
        MessageLogUtil.logConnack(createEmptyConnack(), true,false);
        assertEquals("Sent CONNACK to client 'clientid': Reason Code: 'SUCCESS', Session Present: 'false'," +
                        " Session Expiry Interval: 'null', Assigned ClientId 'null', Maximum QoS: 'null'," +
                        " Maximum Packet Size: '2', Receive Maximum: '1', Topic Alias Maximum: '3', Reason String: 'null'," +
                        " Response Information: 'null', Server Keep Alive: 'null'," +
                        " Server Reference: 'null', Shared Subscription Available: 'false'," +
                        " Wildcards Available: 'false', Retain Available: 'false', Subscription Identifiers Available: 'false'," +
                        " Auth Method: 'null', Auth Data (Base64): 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_connack_not_verbose_none_set() {
        MessageLogUtil.logConnack(createEmptyConnack(), false,false);
        assertEquals("Sent CONNACK to client 'clientid': Reason Code: 'SUCCESS', Session Present: 'false'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_publish_verbose_all_set() {
        MessageLogUtil.logPublish("Sent PUBLISH to client 'clientid' on topic", createFullPublish(), true, true,false);
        assertEquals(
                "Sent PUBLISH to client 'clientid' on topic 'topic': Payload: 'message', QoS: '1', Retained: 'false', " +
                        "Message Expiry Interval: '10000', Duplicate Delivery: 'false', Correlation Data: 'data', " +
                        "Response Topic: 'response topic', Content Type: 'content type', Payload Format Indicator: 'UTF_8', " +
                        "Subscription Identifiers: '[1, 2, 3, 4]', " +
                        "User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_publish_verbose_no_payload_all_set() {
        MessageLogUtil.logPublish("Sent PUBLISH to client 'clientid' on topic", createFullPublish(), true, false,false);
        assertEquals(
                "Sent PUBLISH to client 'clientid' on topic 'topic': QoS: '1', Retained: 'false', " +
                        "Message Expiry Interval: '10000', Duplicate Delivery: 'false', Correlation Data: 'data', " +
                        "Response Topic: 'response topic', Content Type: 'content type', Payload Format Indicator: 'UTF_8', " +
                        "Subscription Identifiers: '[1, 2, 3, 4]', " +
                        "User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_publish_not_verbose_all_set() {
        MessageLogUtil.logPublish("Sent PUBLISH to client 'clientid' on topic", createFullPublish(), false, true,false);
        assertEquals(
                "Sent PUBLISH to client 'clientid' on topic 'topic': Payload: 'message', QoS: '1', Retained: 'false'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_publish_not_verbose_no_payload_all_set() {
        MessageLogUtil.logPublish("Sent PUBLISH to client 'clientid' on topic", createFullPublish(), false, false,false);
        assertEquals(
                "Sent PUBLISH to client 'clientid' on topic 'topic': QoS: '1', Retained: 'false'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_publish_verbose_none_set() {
        MessageLogUtil.logPublish("Sent PUBLISH to client 'clientid' on topic", createEmptyPublish(), true, true,false);
        assertEquals(
                "Sent PUBLISH to client 'clientid' on topic 'topic': Payload: 'message', QoS: '1', Retained: 'false'," +
                        " Message Expiry Interval: 'null', Duplicate Delivery: 'false', Correlation Data: 'null'," +
                        " Response Topic: 'null', Content Type: 'null', Payload Format Indicator: 'null'," +
                        " Subscription Identifiers: '[]', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_publish_verbose_no_payload_none_set() {
        MessageLogUtil.logPublish("Sent PUBLISH to client 'clientid' on topic", createEmptyPublish(), true, false,false);
        assertEquals(
                "Sent PUBLISH to client 'clientid' on topic 'topic': QoS: '1', Retained: 'false'," +
                        " Message Expiry Interval: 'null', Duplicate Delivery: 'false', Correlation Data: 'null'," +
                        " Response Topic: 'null', Content Type: 'null', Payload Format Indicator: 'null'," +
                        " Subscription Identifiers: '[]', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_subscribe_verbose_all_set() {
        MessageLogUtil.logSubscribe(createFullSubsribe(), true,false);
        assertEquals("Received SUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic1', QoS: '2', Retain As Published: 'false', No Local: 'false', Retain Handling: 'DO_NOT_SEND'], " +
                        "[Topic: 'topic2', QoS: '0', Retain As Published: 'true', No Local: 'true', Retain Handling: 'SEND_IF_NEW_SUBSCRIPTION'] }, " +
                        "Subscription Identifier: '10', User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_subscribe_not_verbose_all_set() {
        MessageLogUtil.logSubscribe(createFullSubsribe(), false,false);
        assertEquals("Received SUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic1', QoS: '2'], [Topic: 'topic2', QoS: '0'] }",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_subscribe_verbose_none_set() {
        MessageLogUtil.logSubscribe(createEmptySubscribe(), true,false);
        assertEquals("Received SUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic', QoS: '0', Retain As Published: 'false', No Local: 'false', Retain Handling: 'SEND'] }, " +
                        "Subscription Identifier: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_subscribe_not_verbose_none_set() {
        MessageLogUtil.logSubscribe(createEmptySubscribe(), false,false);
        assertEquals("Received SUBSCRIBE from client 'clientid': Topics: { " + "[Topic: 'topic', QoS: '0'] }",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_suback_verbose_all_set() {
        MessageLogUtil.logSuback(createFullSuback(), true,false);
        assertEquals("Sent SUBACK to client 'clientid': Suback Reason Codes: { " +
                        "[Reason Code: 'GRANTED_QOS_1'], [Reason Code: 'GRANTED_QOS_0'] }, Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_suback_not_verbose_all_set() {
        MessageLogUtil.logSuback(createFullSuback(), false,false);
        assertEquals("Sent SUBACK to client 'clientid': Suback Reason Codes: { " +
                        "[Reason Code: 'GRANTED_QOS_1'], [Reason Code: 'GRANTED_QOS_0'] }",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_suback_verbose_none_set() {
        MessageLogUtil.logSuback(createEmptySuback(), true,false);
        assertEquals("Sent SUBACK to client 'clientid': Suback Reason Codes: { " +
                        "[Reason Code: 'GRANTED_QOS_1'] }, Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_suback_not_verbose_none_set() {
        MessageLogUtil.logSuback(createEmptySuback(), false,false);
        assertEquals("Sent SUBACK to client 'clientid': Suback Reason Codes: { " + "[Reason Code: 'GRANTED_QOS_1'] }",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_unsuback_verbose_all_set() {
        MessageLogUtil.logUnsuback(createFullUnsuback(), true,false);
        assertEquals("Sent UNSUBACK to client 'clientid': Unsuback Reason Codes: { " +
                        "[Reason Code: 'NOT_AUTHORIZED'], [Reason Code: 'SUCCESS'] }, Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1'], [Name: 'name2', Value: 'value2']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_unsuback_not_verbose_all_set() {
        MessageLogUtil.logUnsuback(createFullUnsuback(), false,false);
        assertEquals("Sent UNSUBACK to client 'clientid': Unsuback Reason Codes: { " +
                        "[Reason Code: 'NOT_AUTHORIZED'], [Reason Code: 'SUCCESS'] }",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_unsuback_verbose_none_set() {
        MessageLogUtil.logUnsuback(createEmptyUnsuback(), true,false);
        assertEquals("Sent UNSUBACK to client 'clientid': Unsuback Reason Codes: { " +
                        "[Reason Code: 'NOT_AUTHORIZED'] }, Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_unsuback_not_verbose_none_set() {
        MessageLogUtil.logUnsuback(createEmptyUnsuback(), false,false);
        assertEquals("Sent UNSUBACK to client 'clientid': Unsuback Reason Codes: { " +
                "[Reason Code: 'NOT_AUTHORIZED'] }", logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_unsubscribe_verbose_all_set() {
        MessageLogUtil.logUnsubscribe(createFullUnsubsribe(), true,false);
        assertEquals("Received UNSUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic1'] }, User Properties: " +
                        "[Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_unsubscribe_not_verbose_all_set() {
        MessageLogUtil.logUnsubscribe(createFullUnsubsribe(), false,false);
        assertEquals("Received UNSUBSCRIBE from client 'clientid': Topics: { " + "[Topic: 'topic1'] }",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_unsubscribe_verbose_none_set() {
        MessageLogUtil.logUnsubscribe(createEmptyUnsubscribe(), true,false);
        assertEquals("Received UNSUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic1'], [Topic: 'topic2'] }, User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_unsubscribe_not_verbose_none_set() {
        MessageLogUtil.logUnsubscribe(createEmptyUnsubscribe(), false,false);
        assertEquals("Received UNSUBSCRIBE from client 'clientid': Topics: { " +
                "[Topic: 'topic1'], [Topic: 'topic2'] }", logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pingreq() {
        MessageLogUtil.logPingreq(createPingreq(),false);
        assertEquals("Received PING REQUEST from client 'clientid'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pingresp() {
        MessageLogUtil.logPingresp(createPingresp(),false);
        assertEquals("Sent PING RESPONSE to client 'clientid'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_puback_inbound_verbose_all_set() {
        MessageLogUtil.logPuback(createFullPuback(), "clientid", true, true,false);
        assertEquals(
                "Received PUBACK from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_puback_inbound_not_verbose_all_set() {
        MessageLogUtil.logPuback(createFullPuback(), "clientid", true, false,false);
        assertEquals("Received PUBACK from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_puback_inbound_verbose_none_set() {
        MessageLogUtil.logPuback(createEmptyPuback(), "clientid", true, true,false);
        assertEquals(
                "Received PUBACK from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_puback_inbound_not_verbose_none_set() {
        MessageLogUtil.logPuback(createEmptyPuback(), "clientid", true, false,false);
        assertEquals("Received PUBACK from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_puback_outbound_verbose_all_set() {
        MessageLogUtil.logPuback(createFullPuback(), "clientid", false, true,false);
        assertEquals(
                "Sent PUBACK to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_puback_outbound_not_verbose_all_set() {
        MessageLogUtil.logPuback(createFullPuback(), "clientid", false, false,false);
        assertEquals("Sent PUBACK to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_puback_outbound_verbose_none_set() {
        MessageLogUtil.logPuback(createEmptyPuback(), "clientid", false, true,false);
        assertEquals(
                "Sent PUBACK to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_puback_outbound_not_verbose_none_set() {
        MessageLogUtil.logPuback(createEmptyPuback(), "clientid", false, false,false);
        assertEquals("Sent PUBACK to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrec_inbound_verbose_all_set() {
        MessageLogUtil.logPubrec(createFullPubrec(), "clientid", true, true,false);
        assertEquals(
                "Received PUBREC from client 'clientid': Reason Code: 'SUCCESS', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrec_inbound_not_verbose_all_set() {
        MessageLogUtil.logPubrec(createFullPubrec(), "clientid", true, false,false);
        assertEquals("Received PUBREC from client 'clientid': Reason Code: 'SUCCESS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrec_inbound_verbose_none_set() {
        MessageLogUtil.logPubrec(createEmptyPubrec(), "clientid", true, true,false);
        assertEquals(
                "Received PUBREC from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrec_inbound_not_verbose_none_set() {
        MessageLogUtil.logPubrec(createEmptyPubrec(), "clientid", true, false,false);
        assertEquals("Received PUBREC from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrec_outbound_verbose_all_set() {
        MessageLogUtil.logPubrec(createFullPubrec(), "clientid", false, true,false);
        assertEquals(
                "Sent PUBREC to client 'clientid': Reason Code: 'SUCCESS', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrec_outbound_not_verbose_all_set() {
        MessageLogUtil.logPubrec(createFullPubrec(), "clientid", false, false,false);
        assertEquals("Sent PUBREC to client 'clientid': Reason Code: 'SUCCESS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrec_outbound_verbose_none_set() {
        MessageLogUtil.logPubrec(createEmptyPubrec(), "clientid", false, true,false);
        assertEquals(
                "Sent PUBREC to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrec_outbound_not_verbose_none_set() {
        MessageLogUtil.logPubrec(createEmptyPubrec(), "clientid", false, false,false);
        assertEquals("Sent PUBREC to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrel_inbound_verbose_all_set() {
        MessageLogUtil.logPubrel(createFullPubrel(), "clientid", true, true,false);
        assertEquals(
                "Received PUBREL from client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrel_inbound_not_verbose_all_set() {
        MessageLogUtil.logPubrel(createFullPubrel(), "clientid", true, false,false);
        assertEquals("Received PUBREL from client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrel_inbound_verbose_none_set() {
        MessageLogUtil.logPubrel(createEmptyPubrel(), "clientid", true, true,false);
        assertEquals(
                "Received PUBREL from client 'clientid': Reason Code: 'SUCCESS', Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrel_inbound_not_verbose_none_set() {
        MessageLogUtil.logPubrel(createEmptyPubrel(), "clientid", true, false,false);
        assertEquals("Received PUBREL from client 'clientid': Reason Code: 'SUCCESS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrel_outbound_verbose_all_set() {
        MessageLogUtil.logPubrel(createFullPubrel(), "clientid", false, true,false);
        assertEquals(
                "Sent PUBREL to client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrel_outbound_not_verbose_all_set() {
        MessageLogUtil.logPubrel(createFullPubrel(), "clientid", false, false,false);
        assertEquals("Sent PUBREL to client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrel_outbound_verbose_none_set() {
        MessageLogUtil.logPubrel(createEmptyPubrel(), "clientid", false, true,false);
        assertEquals(
                "Sent PUBREL to client 'clientid': Reason Code: 'SUCCESS', Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubrel_outbound_not_verbose_none_set() {
        MessageLogUtil.logPubrel(createEmptyPubrel(), "clientid", false, false,false);
        assertEquals("Sent PUBREL to client 'clientid': Reason Code: 'SUCCESS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubcomp_inbound_verbose_all_set() {
        MessageLogUtil.logPubcomp(createFullPubcomp(), "clientid", true, true,false);
        assertEquals(
                "Received PUBCOMP from client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubcomp_inbound_not_verbose_all_set() {
        MessageLogUtil.logPubcomp(createFullPubcomp(), "clientid", true, false,false);
        assertEquals("Received PUBCOMP from client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubcomp_inbound_verbose_none_set() {
        MessageLogUtil.logPubcomp(createEmptyPubcomp(), "clientid", true, true,false);
        assertEquals(
                "Received PUBCOMP from client 'clientid': Reason Code: 'SUCCESS', Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubcomp_inbound_not_verbose_none_set() {
        MessageLogUtil.logPubcomp(createEmptyPubcomp(), "clientid", true, false,false);
        assertEquals("Received PUBCOMP from client 'clientid': Reason Code: 'SUCCESS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubcomp_outbound_verbose_all_set() {
        MessageLogUtil.logPubcomp(createFullPubcomp(), "clientid", false, true,false);
        assertEquals(
                "Sent PUBCOMP to client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubcomp_outbound_not_verbose_all_set() {
        MessageLogUtil.logPubcomp(createFullPubcomp(), "clientid", false, false,false);
        assertEquals("Sent PUBCOMP to client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubcomp_outbound_verbose_none_set() {
        MessageLogUtil.logPubcomp(createEmptyPubcomp(), "clientid", false, true,false);
        assertEquals(
                "Sent PUBCOMP to client 'clientid': Reason Code: 'SUCCESS', Reason String: 'null', User Properties: 'null'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }

    @Test
    void test_log_pubcomp_outbound_not_verbose_none_set() {
        MessageLogUtil.logPubcomp(createEmptyPubcomp(), "clientid", false, false,false);
        assertEquals("Sent PUBCOMP to client 'clientid': Reason Code: 'SUCCESS'",
                logbackTestAppender.getEvents().get(0).getFormattedMessage());
    }
}
