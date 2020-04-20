package com.hivemq.extension.mqtt.message.log;

import com.hivemq.extension.sdk.api.packets.general.DisconnectedReasonCode;
import org.junit.Before;
import org.junit.Test;
import util.LogbackCapturingAppender;

import static com.hivemq.extension.mqtt.PacketUtil.*;
import static org.junit.Assert.assertEquals;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
@SuppressWarnings("NullabilityAnnotations")
public class MessageLogUtilTest {

    private LogbackCapturingAppender logCapture;

    @Before
    public void setUp() throws Exception {
        logCapture = LogbackCapturingAppender.Factory.weaveInto(MessageLogUtil.log);
    }

    @Test
    public void test_lifecycle_and_interceptor_disconnect_logs_have_same_format(){
        final String expectedLog = "Received DISCONNECT from client 'clientId': Reason Code: 'BAD_AUTHENTICATION_METHOD'";

        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientId':", new TestDisconnect(DisconnectedReasonCode.BAD_AUTHENTICATION_METHOD, "Okay", new TestUserProperties(3)), false);

        assertEquals(expectedLog, logCapture.getLastCapturedLog().getFormattedMessage());

        MessageLogUtil.logDisconnect(createLifeCycleCompareDisconnect(),"clientId",true, false);

        assertEquals(expectedLog, logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_lifecycle_disconnect_verbose_all_set() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid':", new TestDisconnect(DisconnectedReasonCode.BAD_AUTHENTICATION_METHOD, "ReasonString", new TestUserProperties(5)), true);
        assertEquals("Received DISCONNECT from client 'clientid': Reason Code: 'BAD_AUTHENTICATION_METHOD', Reason String: 'ReasonString', User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1'], [Name: 'name2', Value: 'value2'], [Name: 'name3', Value: 'value3'], [Name: 'name4', Value: 'value4']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_lifecycle_disconnect_verbose_none_set() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid':", new TestDisconnect(null, null, null), true);
        assertEquals("Received DISCONNECT from client 'clientid': Reason Code: 'null', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_lifecycle_disconnect_verbose_user_properties_empty() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid':", new TestDisconnect(null, null, new TestUserProperties(0)), true);
        assertEquals("Received DISCONNECT from client 'clientid': Reason Code: 'null', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_lifecycle_disconnect_not_verbose() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid':", new TestDisconnect(null, null, null), false);
        assertEquals("Received DISCONNECT from client 'clientid': Reason Code: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_inbound_disconnect_verbose_all_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(),"clientId",true, true);
        assertEquals("Received DISCONNECT from client 'clientId': Reason Code: 'NOT_AUTHORIZED', Reason String: 'Okay', Server Reference: 'Server2', Session Expiry: '123', User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_inbound_disconnect_verbose_none_set() {
        MessageLogUtil.logDisconnect(createEmptyDisconnect(), "clientId", true,true);
        assertEquals("Received DISCONNECT from client 'clientId': Reason Code: 'NOT_AUTHORIZED', Reason String: 'null', Server Reference: 'null', Session Expiry: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_inbound_disconnect_not_verbose_all_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(),"clientId",true, false);
        assertEquals("Received DISCONNECT from client 'clientId': Reason Code: 'NOT_AUTHORIZED'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_inbound_disconnect_not_verbose_none_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(),"clientId",true, false);
        assertEquals("Received DISCONNECT from client 'clientId': Reason Code: 'NOT_AUTHORIZED'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_outound_disconnect_verbose_all_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(),"clientId",false, true);
        assertEquals("Sent DISCONNECT to client 'clientId': Reason Code: 'NOT_AUTHORIZED', Reason String: 'Okay', Server Reference: 'Server2', Session Expiry: '123', User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_outound_disconnect_verbose_none_set() {
        MessageLogUtil.logDisconnect(createEmptyDisconnect(), "clientId", false,true);
        assertEquals("Sent DISCONNECT to client 'clientId': Reason Code: 'NOT_AUTHORIZED', Reason String: 'null', Server Reference: 'null', Session Expiry: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_outound_disconnect_not_verbose_all_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(),"clientId",false, false);
        assertEquals("Sent DISCONNECT to client 'clientId': Reason Code: 'NOT_AUTHORIZED'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_outound_disconnect_not_verbose_none_set() {
        MessageLogUtil.logDisconnect(createFullDisconnect(),"clientId",false, false);
        assertEquals("Sent DISCONNECT to client 'clientId': Reason Code: 'NOT_AUTHORIZED'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_connect_verbose_all_set() {
        MessageLogUtil.logConnect(createFullConnect(), true);
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
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_connect_verbose_none_set() {
        MessageLogUtil.logConnect(createEmptyConnect(), true);
        assertEquals("Received CONNECT from client 'clientid': Protocol version: 'V_5', Clean Start: 'false', " +
                        "Session Expiry Interval: '10000', Keep Alive: '0', Maximum Packet Size: '0', Receive Maximum: '0', " +
                        "Topic Alias Maximum: '0', Request Problem Information: 'false', Request Response Information: 'false',  " +
                        "Username: 'null', Password: 'null', Auth Method: 'null', Auth Data (Base64): 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_connect_not_verbose_all_set() {
        MessageLogUtil.logConnect(createFullConnect(), false);
        assertEquals("Received CONNECT from client 'clientid': Protocol version: 'V_5', Clean Start: 'false', " +
                        "Session Expiry Interval: '10000'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_connack_verbose_all_set() {
        MessageLogUtil.logConnack(createFullConnack(), true);

        assertEquals("Sent CONNACK to client 'clientid': Reason Code: 'SUCCESS', Session Present: 'false'," +
                        " Session Expiry Interval: '100', Assigned ClientId 'overwriteClientId', Maximum QoS: 'AT_MOST_ONCE'," +
                        " Maximum Packet Size: '5', Receive Maximum: '10', Topic Alias Maximum: '5', Reason String: 'Okay'," +
                        " Response Information: 'Everything fine', Server Keep Alive: '100'," +
                        " Server Reference: 'Server2', Shared Subscription Available: 'false'," +
                        " Wildcards Available: 'false', Retain Available: 'false', Subscription Identifiers Available: 'false'," +
                        " Auth Method: 'JSON', Auth Data (Base64): 'YXV0aCBkYXRh', User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_connack_not_verbose_all_set() {
        MessageLogUtil.logConnack(createFullConnack(), false);
        assertEquals("Sent CONNACK to client 'clientid': Reason Code: 'SUCCESS', Session Present: 'false'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_connack_verbose_none_set() {
        MessageLogUtil.logConnack(createEmptyConnack(), true);
        assertEquals("Sent CONNACK to client 'clientid': Reason Code: 'SUCCESS', Session Present: 'false'," +
                        " Session Expiry Interval: 'null', Assigned ClientId 'null', Maximum QoS: 'null'," +
                        " Maximum Packet Size: '2', Receive Maximum: '1', Topic Alias Maximum: '3', Reason String: 'null'," +
                        " Response Information: 'null', Server Keep Alive: 'null'," +
                        " Server Reference: 'null', Shared Subscription Available: 'false'," +
                        " Wildcards Available: 'false', Retain Available: 'false', Subscription Identifiers Available: 'false'," +
                        " Auth Method: 'null', Auth Data (Base64): 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_connack_not_verbose_none_set() {
        MessageLogUtil.logConnack(createEmptyConnack(), false);
        assertEquals("Sent CONNACK to client 'clientid': Reason Code: 'SUCCESS', Session Present: 'false'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_publish_verbose_all_set() {
        MessageLogUtil.logPublish("Sent PUBLISH to client 'clientid' on topic", createFullPublish(), true);
        assertEquals("Sent PUBLISH to client 'clientid' on topic 'topic': Payload: 'message', QoS: '1', Retained: 'false', " +
                        "Message Expiry Interval: '10000', Duplicate Delivery: 'false', Correlation Data: 'data', " +
                        "Response Topic: 'response topic', Content Type: 'content type', Payload Format Indicator: 'UTF_8', " +
                        "Subscription Identifiers: '[1, 2, 3, 4]', " +
                        "User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_publish_not_verbose_all_set() {
        MessageLogUtil.logPublish("Sent PUBLISH to client 'clientid' on topic", createFullPublish(), false);
        assertEquals("Sent PUBLISH to client 'clientid' on topic 'topic': Payload: 'message', QoS: '1', Retained: 'false'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_publish_verbose_none_set() {
        MessageLogUtil.logPublish("Sent PUBLISH to client 'clientid' on topic", createEmptyPublish(), true);
        assertEquals("Sent PUBLISH to client 'clientid' on topic 'topic': Payload: 'message', QoS: '1', Retained: 'false'," +
                        " Message Expiry Interval: 'null', Duplicate Delivery: 'false', Correlation Data: 'null'," +
                        " Response Topic: 'null', Content Type: 'null', Payload Format Indicator: 'null'," +
                        " Subscription Identifiers: '[]', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_subscribe_verbose_all_set() {
        MessageLogUtil.logSubscribe(createFullSubsribe(), true);
        assertEquals("Received SUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic1', QoS: '2', Retain As Published: 'false', No Local: 'false', Retain Handling: 'DO_NOT_SEND'], " +
                        "[Topic: 'topic2', QoS: '0', Retain As Published: 'true', No Local: 'true', Retain Handling: 'SEND_IF_NEW_SUBSCRIPTION'] }, " +
                        "Subscription Identifier: '10', User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_subscribe_not_verbose_all_set() {
        MessageLogUtil.logSubscribe(createFullSubsribe(), false);
        assertEquals("Received SUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic1', QoS: '2'], [Topic: 'topic2', QoS: '0'] }",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_subscribe_verbose_none_set() {
        MessageLogUtil.logSubscribe(createEmptySubscribe(), true);
        assertEquals("Received SUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic', QoS: '0', Retain As Published: 'false', No Local: 'false', Retain Handling: 'SEND'] }, " +
                        "Subscription Identifier: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_subscribe_not_verbose_none_set() {
        MessageLogUtil.logSubscribe(createEmptySubscribe(), false);
        assertEquals("Received SUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic', QoS: '0'] }",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_suback_verbose_all_set() {
        MessageLogUtil.logSuback(createFullSuback(), true);
        assertEquals("Sent SUBACK to client 'clientid': Suback Reason Codes: { " +
                        "[Reason Code: 'GRANTED_QOS_1'], [Reason Code: 'GRANTED_QOS_0'] }, Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_suback_not_verbose_all_set() {
        MessageLogUtil.logSuback(createFullSuback(), false);
        assertEquals("Sent SUBACK to client 'clientid': Suback Reason Codes: { " +
                        "[Reason Code: 'GRANTED_QOS_1'], [Reason Code: 'GRANTED_QOS_0'] }",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_suback_verbose_none_set() {
        MessageLogUtil.logSuback(createEmptySuback(), true);
        assertEquals("Sent SUBACK to client 'clientid': Suback Reason Codes: { " +
                        "[Reason Code: 'GRANTED_QOS_1'] }, Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_suback_not_verbose_none_set() {
        MessageLogUtil.logSuback(createEmptySuback(), false);
        assertEquals("Sent SUBACK to client 'clientid': Suback Reason Codes: { " +
                        "[Reason Code: 'GRANTED_QOS_1'] }",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_unsuback_verbose_all_set() {
        MessageLogUtil.logUnsuback(createFullUnsuback(), true);
        assertEquals("Sent UNSUBACK to client 'clientid': Unsuback Reason Codes: { " +
                        "[Reason Code: 'NOT_AUTHORIZED'], [Reason Code: 'SUCCESS'] }, Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1'], [Name: 'name2', Value: 'value2']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_unsuback_not_verbose_all_set() {
        MessageLogUtil.logUnsuback(createFullUnsuback(), false);
        assertEquals("Sent UNSUBACK to client 'clientid': Unsuback Reason Codes: { " +
                        "[Reason Code: 'NOT_AUTHORIZED'], [Reason Code: 'SUCCESS'] }",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_unsuback_verbose_none_set() {
        MessageLogUtil.logUnsuback(createEmptyUnsuback(), true);
        assertEquals("Sent UNSUBACK to client 'clientid': Unsuback Reason Codes: { " +
                        "[Reason Code: 'NOT_AUTHORIZED'] }, Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_unsuback_not_verbose_none_set() {
        MessageLogUtil.logUnsuback(createEmptyUnsuback(), false);
        assertEquals("Sent UNSUBACK to client 'clientid': Unsuback Reason Codes: { " +
                        "[Reason Code: 'NOT_AUTHORIZED'] }",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_unsubscribe_verbose_all_set() {
        MessageLogUtil.logUnsubscribe(createFullUnsubsribe(), true);
        assertEquals("Received UNSUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic1'] }, User Properties: " +
                        "[Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_unsubscribe_not_verbose_all_set() {
        MessageLogUtil.logUnsubscribe(createFullUnsubsribe(), false);
        assertEquals("Received UNSUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic1'] }",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_unsubscribe_verbose_none_set() {
        MessageLogUtil.logUnsubscribe(createEmptyUnsubscribe(), true);
        assertEquals("Received UNSUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic1'], [Topic: 'topic2'] }, User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_unsubscribe_not_verbose_none_set() {
        MessageLogUtil.logUnsubscribe(createEmptyUnsubscribe(), false);
        assertEquals("Received UNSUBSCRIBE from client 'clientid': Topics: { " +
                        "[Topic: 'topic1'], [Topic: 'topic2'] }",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pingreq() {
        MessageLogUtil.logPingreq(createPingreq());
        assertEquals("Received PING REQUEST from client 'clientid'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pingresp() {
        MessageLogUtil.logPingresp(createPingresp());
        assertEquals("Sent PING RESPONSE to client 'clientid'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_puback_inbound_verbose_all_set() {
        MessageLogUtil.logPuback(createFullPuback(), "clientid", true, true);
        assertEquals("Received PUBACK from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_puback_inbound_not_verbose_all_set() {
        MessageLogUtil.logPuback(createFullPuback(), "clientid", true, false);
        assertEquals("Received PUBACK from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_puback_inbound_verbose_none_set() {
        MessageLogUtil.logPuback(createEmptyPuback(), "clientid", true, true);
        assertEquals("Received PUBACK from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_puback_inbound_not_verbose_none_set() {
        MessageLogUtil.logPuback(createEmptyPuback(), "clientid", true, false);
        assertEquals("Received PUBACK from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_puback_outbound_verbose_all_set() {
        MessageLogUtil.logPuback(createFullPuback(), "clientid", false, true);
        assertEquals("Sent PUBACK to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_puback_outbound_not_verbose_all_set() {
        MessageLogUtil.logPuback(createFullPuback(), "clientid", false, false);
        assertEquals("Sent PUBACK to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_puback_outbound_verbose_none_set() {
        MessageLogUtil.logPuback(createEmptyPuback(), "clientid", false, true);
        assertEquals("Sent PUBACK to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_puback_outbound_not_verbose_none_set() {
        MessageLogUtil.logPuback(createEmptyPuback(), "clientid", false, false);
        assertEquals("Sent PUBACK to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrec_inbound_verbose_all_set() {
        MessageLogUtil.logPubrec(createFullPubrec(), "clientid", true, true);
        assertEquals("Received PUBREC from client 'clientid': Reason Code: 'SUCCESS', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrec_inbound_not_verbose_all_set() {
        MessageLogUtil.logPubrec(createFullPubrec(), "clientid", true, false);
        assertEquals("Received PUBREC from client 'clientid': Reason Code: 'SUCCESS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrec_inbound_verbose_none_set() {
        MessageLogUtil.logPubrec(createEmptyPubrec(), "clientid", true, true);
        assertEquals("Received PUBREC from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrec_inbound_not_verbose_none_set() {
        MessageLogUtil.logPubrec(createEmptyPubrec(), "clientid", true, false);
        assertEquals("Received PUBREC from client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrec_outbound_verbose_all_set() {
        MessageLogUtil.logPubrec(createFullPubrec(), "clientid", false, true);
        assertEquals("Sent PUBREC to client 'clientid': Reason Code: 'SUCCESS', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrec_outbound_not_verbose_all_set() {
        MessageLogUtil.logPubrec(createFullPubrec(), "clientid", false, false);
        assertEquals("Sent PUBREC to client 'clientid': Reason Code: 'SUCCESS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrec_outbound_verbose_none_set() {
        MessageLogUtil.logPubrec(createEmptyPubrec(), "clientid", false, true);
        assertEquals("Sent PUBREC to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrec_outbound_not_verbose_none_set() {
        MessageLogUtil.logPubrec(createEmptyPubrec(), "clientid", false, false);
        assertEquals("Sent PUBREC to client 'clientid': Reason Code: 'NO_MATCHING_SUBSCRIBERS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrel_inbound_verbose_all_set() {
        MessageLogUtil.logPubrel(createFullPubrel(), "clientid", true, true);
        assertEquals("Received PUBREL from client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrel_inbound_not_verbose_all_set() {
        MessageLogUtil.logPubrel(createFullPubrel(), "clientid", true, false);
        assertEquals("Received PUBREL from client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrel_inbound_verbose_none_set() {
        MessageLogUtil.logPubrel(createEmptyPubrel(), "clientid", true, true);
        assertEquals("Received PUBREL from client 'clientid': Reason Code: 'SUCCESS', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrel_inbound_not_verbose_none_set() {
        MessageLogUtil.logPubrel(createEmptyPubrel(), "clientid", true, false);
        assertEquals("Received PUBREL from client 'clientid': Reason Code: 'SUCCESS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrel_outbound_verbose_all_set() {
        MessageLogUtil.logPubrel(createFullPubrel(), "clientid", false, true);
        assertEquals("Sent PUBREL to client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrel_outbound_not_verbose_all_set() {
        MessageLogUtil.logPubrel(createFullPubrel(), "clientid", false, false);
        assertEquals("Sent PUBREL to client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrel_outbound_verbose_none_set() {
        MessageLogUtil.logPubrel(createEmptyPubrel(), "clientid", false, true);
        assertEquals("Sent PUBREL to client 'clientid': Reason Code: 'SUCCESS', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubrel_outbound_not_verbose_none_set() {
        MessageLogUtil.logPubrel(createEmptyPubrel(), "clientid", false, false);
        assertEquals("Sent PUBREL to client 'clientid': Reason Code: 'SUCCESS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubcomp_inbound_verbose_all_set() {
        MessageLogUtil.logPubcomp(createFullPubcomp(), "clientid", true, true);
        assertEquals("Received PUBCOMP from client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubcomp_inbound_not_verbose_all_set() {
        MessageLogUtil.logPubcomp(createFullPubcomp(), "clientid", true, false);
        assertEquals("Received PUBCOMP from client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubcomp_inbound_verbose_none_set() {
        MessageLogUtil.logPubcomp(createEmptyPubcomp(), "clientid", true, true);
        assertEquals("Received PUBCOMP from client 'clientid': Reason Code: 'SUCCESS', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubcomp_inbound_not_verbose_none_set() {
        MessageLogUtil.logPubcomp(createEmptyPubcomp(), "clientid", true, false);
        assertEquals("Received PUBCOMP from client 'clientid': Reason Code: 'SUCCESS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubcomp_outbound_verbose_all_set() {
        MessageLogUtil.logPubcomp(createFullPubcomp(), "clientid", false, true);
        assertEquals("Sent PUBCOMP to client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND', Reason String: 'Okay', User Properties: " +
                        "[Name: 'name0', Value: 'value0']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubcomp_outbound_not_verbose_all_set() {
        MessageLogUtil.logPubcomp(createFullPubcomp(), "clientid", false, false);
        assertEquals("Sent PUBCOMP to client 'clientid': Reason Code: 'PACKET_IDENTIFIER_NOT_FOUND'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubcomp_outbound_verbose_none_set() {
        MessageLogUtil.logPubcomp(createEmptyPubcomp(), "clientid", false, true);
        assertEquals("Sent PUBCOMP to client 'clientid': Reason Code: 'SUCCESS', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_pubcomp_outbound_not_verbose_none_set() {
        MessageLogUtil.logPubcomp(createEmptyPubcomp(), "clientid", false, false);
        assertEquals("Sent PUBCOMP to client 'clientid': Reason Code: 'SUCCESS'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }
}