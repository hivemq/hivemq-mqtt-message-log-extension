package com.hivemq.extension.mqtt.message.log;

import com.hivemq.extension.mqtt.message.log.config.MqttMessageLogConfig;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.parameter.ClientInformation;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionInformation;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.general.*;
import com.hivemq.extension.sdk.api.packets.publish.PayloadFormatIndicator;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.RetainHandling;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.LogbackCapturingAppender;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author Florian Limpöck
 * @since 4.2.0
 */
@SuppressWarnings("NullabilityAnnotations")
public class MessageLogUtilTest {

    private LogbackCapturingAppender logCapture;

    @Before
    public void setUp() throws Exception {
        logCapture = LogbackCapturingAppender.Factory.weaveInto(MessageLogUtil.log);
    }

    @Test
    public void test_log_disconnect_verbose_all_set() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid'.", new TestDisconnect(DisconnectedReasonCode.BAD_AUTHENTICATION_METHOD, "ReasonString", new TestUserProperties(5)), true);
        assertEquals("Received DISCONNECT from client 'clientid'. Reason Code: 'BAD_AUTHENTICATION_METHOD', Reason String: 'ReasonString', User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1'], [Name: 'name2', Value: 'value2'], [Name: 'name3', Value: 'value3'], [Name: 'name4', Value: 'value4']",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_disconnect_verbose_none_set() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid'.", new TestDisconnect(null, null, null), true);
        assertEquals("Received DISCONNECT from client 'clientid'. Reason Code: 'null', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_disconnect_verbose_user_properties_empty() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid'.", new TestDisconnect(null, null, new TestUserProperties(0)), true);
        assertEquals("Received DISCONNECT from client 'clientid'. Reason Code: 'null', Reason String: 'null', User Properties: 'null'",
                logCapture.getLastCapturedLog().getFormattedMessage());
    }

    @Test
    public void test_log_disconnect_not_verbose() {
        MessageLogUtil.logDisconnect("Received DISCONNECT from client 'clientid'.", new TestDisconnect(null, null, null), false);
        assertEquals("Received DISCONNECT from client 'clientid'.",
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

    private SubscribeInboundInput createEmptySubscribe() {
        return new SubscribeInboundInput() {
            @Override
            public @NotNull SubscribePacket getSubscribePacket() {
                return new SubscribePacket() {
                    @Override
                    public @NotNull List<Subscription> getSubscriptions() {
                        return List.of(new Subscription() {
                            @Override
                            public @NotNull String getTopicFilter() {
                                return "topic";
                            }

                            @Override
                            public @NotNull Qos getQos() {
                                return Qos.AT_MOST_ONCE;
                            }

                            @Override
                            public @NotNull RetainHandling getRetainHandling() {
                                return RetainHandling.SEND;
                            }

                            @Override
                            public boolean getRetainAsPublished() {
                                return false;
                            }

                            @Override
                            public boolean getNoLocal() {
                                return false;
                            }
                        });
                    }

                    @Override
                    public @NotNull UserProperties getUserProperties() {
                        return new TestUserProperties(0);
                    }

                    @Override
                    public @NotNull Optional<Integer> getSubscriptionIdentifier() {
                        return Optional.empty();
                    }

                    @Override
                    public int getPacketId() {
                        return 1;
                    }
                };
            }

            @Override
            public @NotNull ConnectionInformation getConnectionInformation() {
                return null;
            }

            @Override
            public @NotNull ClientInformation getClientInformation() {
                return () -> "clientid";
            }
        };
    }

    private SubscribeInboundInput createFullSubsribe() {
        return new SubscribeInboundInput() {
            @Override
            public @NotNull SubscribePacket getSubscribePacket() {
                return new SubscribePacket() {
                    @Override
                    public @NotNull List<Subscription> getSubscriptions() {
                        return List.of(new Subscription() {
                            @Override
                            public @NotNull String getTopicFilter() {
                                return "topic1";
                            }

                            @Override
                            public @NotNull Qos getQos() {
                                return Qos.EXACTLY_ONCE;
                            }

                            @Override
                            public @NotNull RetainHandling getRetainHandling() {
                                return RetainHandling.DO_NOT_SEND;
                            }

                            @Override
                            public boolean getRetainAsPublished() {
                                return false;
                            }

                            @Override
                            public boolean getNoLocal() {
                                return false;
                            }
                        }, new Subscription() {
                            @Override
                            public @NotNull String getTopicFilter() {
                                return "topic2";
                            }

                            @Override
                            public @NotNull Qos getQos() {
                                return Qos.AT_MOST_ONCE;
                            }

                            @Override
                            public @NotNull RetainHandling getRetainHandling() {
                                return RetainHandling.SEND_IF_NEW_SUBSCRIPTION;
                            }

                            @Override
                            public boolean getRetainAsPublished() {
                                return true;
                            }

                            @Override
                            public boolean getNoLocal() {
                                return true;
                            }
                        });
                    }

                    @Override
                    public @NotNull UserProperties getUserProperties() {
                        return new TestUserProperties(2);
                    }

                    @Override
                    public @NotNull Optional<Integer> getSubscriptionIdentifier() {
                        return Optional.of(10);
                    }

                    @Override
                    public int getPacketId() {
                        return 1;
                    }
                };
            }

            @Override
            public @NotNull ConnectionInformation getConnectionInformation() {
                return null;
            }

            @Override
            public @NotNull ClientInformation getClientInformation() {
                return () -> "clientid";
            }
        };
    }

    private PublishPacket createEmptyPublish() {
        return new PublishPacket() {
            @Override
            public boolean getDupFlag() {
                return false;
            }

            @Override
            public @NotNull Qos getQos() {
                return Qos.AT_LEAST_ONCE;
            }

            @Override
            public boolean getRetain() {
                return false;
            }

            @Override
            public @NotNull String getTopic() {
                return "topic";
            }

            @Override
            public int getPacketId() {
                return 0;
            }

            @Override
            public @NotNull Optional<PayloadFormatIndicator> getPayloadFormatIndicator() {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<Long> getMessageExpiryInterval() {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<String> getResponseTopic() {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<ByteBuffer> getCorrelationData() {
                return Optional.empty();
            }

            @Override
            public @NotNull List<Integer> getSubscriptionIdentifiers() {
                return new ArrayList<>();
            }

            @Override
            public @NotNull Optional<String> getContentType() {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<ByteBuffer> getPayload() {
                return Optional.of(ByteBuffer.wrap("message".getBytes()));
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return null;
            }
        };
    }

    private PublishPacket createFullPublish() {
        return new PublishPacket() {
            @Override
            public boolean getDupFlag() {
                return false;
            }

            @Override
            public @NotNull Qos getQos() {
                return Qos.AT_LEAST_ONCE;
            }

            @Override
            public boolean getRetain() {
                return false;
            }

            @Override
            public @NotNull String getTopic() {
                return "topic";
            }

            @Override
            public int getPacketId() {
                return 0;
            }

            @Override
            public @NotNull Optional<PayloadFormatIndicator> getPayloadFormatIndicator() {
                return Optional.of(PayloadFormatIndicator.UTF_8);
            }

            @Override
            public @NotNull Optional<Long> getMessageExpiryInterval() {
                return Optional.of(10000L);
            }

            @Override
            public @NotNull Optional<String> getResponseTopic() {
                return Optional.of("response topic");
            }

            @Override
            public @NotNull Optional<ByteBuffer> getCorrelationData() {
                return Optional.of(ByteBuffer.wrap("data".getBytes()));
            }

            @Override
            public @NotNull List<Integer> getSubscriptionIdentifiers() {
                return List.of(1,2,3,4);
            }

            @Override
            public @NotNull Optional<String> getContentType() {
                return Optional.of("content type");
            }

            @Override
            public @NotNull Optional<ByteBuffer> getPayload() {
                return Optional.of(ByteBuffer.wrap("message".getBytes()));
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return new TestUserProperties(2);
            }
        };
    }

    private ConnectPacket createEmptyConnect() {
        return new ConnectPacket() {
            @Override
            public @NotNull MqttVersion getMqttVersion() {
                return MqttVersion.V_5;
            }

            @Override
            public @NotNull String getClientId() {
                return "clientid";
            }

            @Override
            public boolean getCleanStart() {
                return false;
            }

            @Override
            public @NotNull Optional<WillPublishPacket> getWillPublish() {
                return Optional.empty();
            }

            @Override
            public long getSessionExpiryInterval() {
                return 10000;
            }

            @Override
            public int getKeepAlive() {
                return 0;
            }

            @Override
            public int getReceiveMaximum() {
                return 0;
            }

            @Override
            public long getMaximumPacketSize() {
                return 0;
            }

            @Override
            public int getTopicAliasMaximum() {
                return 0;
            }

            @Override
            public boolean getRequestResponseInformation() {
                return false;
            }

            @Override
            public boolean getRequestProblemInformation() {
                return false;
            }

            @Override
            public @NotNull Optional<String> getAuthenticationMethod() {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<ByteBuffer> getAuthenticationData() {
                return Optional.empty();
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return null;
            }

            @Override
            public @NotNull Optional<String> getUserName() {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<ByteBuffer> getPassword() {
                return Optional.empty();
            }
        };
    }
    private ConnectPacket createFullConnect() {
        return new ConnectPacket() {
            @Override
            public @NotNull MqttVersion getMqttVersion() {
                return MqttVersion.V_5;
            }

            @Override
            public @NotNull String getClientId() {
                return "clientid";
            }

            @Override
            public boolean getCleanStart() {
                return false;
            }

            @Override
            public @NotNull Optional<WillPublishPacket> getWillPublish() {
                return Optional.of(new WillPublishPacket() {
                    @Override
                    public long getWillDelay() {
                        return 100;
                    }

                    @Override
                    public boolean getDupFlag() {
                        return false;
                    }

                    @Override
                    public @NotNull Qos getQos() {
                        return Qos.AT_LEAST_ONCE;
                    }

                    @Override
                    public boolean getRetain() {
                        return false;
                    }

                    @Override
                    public @NotNull String getTopic() {
                        return "willtopic";
                    }

                    @Override
                    public int getPacketId() {
                        return 1;
                    }

                    @Override
                    public @NotNull Optional<PayloadFormatIndicator> getPayloadFormatIndicator() {
                        return Optional.of(PayloadFormatIndicator.UTF_8);
                    }

                    @Override
                    public @NotNull Optional<Long> getMessageExpiryInterval() {
                        return Optional.of(1234L);
                    }

                    @Override
                    public @NotNull Optional<String> getResponseTopic() {
                        return Optional.of("response topic");
                    }

                    @Override
                    public @NotNull Optional<ByteBuffer> getCorrelationData() {
                        return Optional.of(ByteBuffer.wrap("data".getBytes()));
                    }

                    @Override
                    public @NotNull List<Integer> getSubscriptionIdentifiers() {
                        return List.of(1,2,3,4);
                    }

                    @Override
                    public @NotNull Optional<String> getContentType() {
                        return Optional.of("content type");
                    }

                    @Override
                    public @NotNull Optional<ByteBuffer> getPayload() {
                        return Optional.of(ByteBuffer.wrap("payload".getBytes()));
                    }

                    @Override
                    public @NotNull UserProperties getUserProperties() {
                        return new TestUserProperties(3);
                    }
                });
            }

            @Override
            public long getSessionExpiryInterval() {
                return 10000;
            }

            @Override
            public int getKeepAlive() {
                return 20000;
            }

            @Override
            public int getReceiveMaximum() {
                return 30000;
            }

            @Override
            public long getMaximumPacketSize() {
                return 40000;
            }

            @Override
            public int getTopicAliasMaximum() {
                return 50000;
            }

            @Override
            public boolean getRequestResponseInformation() {
                return false;
            }

            @Override
            public boolean getRequestProblemInformation() {
                return true;
            }

            @Override
            public @NotNull Optional<String> getAuthenticationMethod() {
                return Optional.of("auth method");
            }

            @Override
            public @NotNull Optional<ByteBuffer> getAuthenticationData() {
                return Optional.of(ByteBuffer.wrap("auth data".getBytes()));
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return new TestUserProperties(2);
            }

            @Override
            public @NotNull Optional<String> getUserName() {
                return Optional.of("the username");
            }

            @Override
            public @NotNull Optional<ByteBuffer> getPassword() {
                return Optional.of(ByteBuffer.wrap("the password".getBytes()));
            }
        };
    }

    @SuppressWarnings("NullabilityAnnotations")
    private class TestDisconnect implements DisconnectEventInput {

        private final DisconnectedReasonCode reasonCode;
        private final String reasonString;
        private final UserProperties userProperties;

        public TestDisconnect(final DisconnectedReasonCode reasonCode, final String reasonString, final UserProperties userProperties) {

            this.reasonCode = reasonCode;
            this.reasonString = reasonString;
            this.userProperties = userProperties;
        }

        @Override
        public @NotNull Optional<DisconnectedReasonCode> getReasonCode() {
            return Optional.ofNullable(reasonCode);
        }

        @Override
        public @NotNull Optional<String> getReasonString() {
            return Optional.ofNullable(reasonString);
        }

        @Override
        public @NotNull Optional<UserProperties> getUserProperties() {
            return Optional.ofNullable(userProperties);
        }

        @Override
        public @NotNull ConnectionInformation getConnectionInformation() {
            return null;
        }

        @Override
        public @NotNull ClientInformation getClientInformation() {
            return null;
        }
    }

    private static class TestUserProperties implements UserProperties {

        private final List<UserProperty> userProperties;

        TestUserProperties(final int amount) {
            final List<UserProperty> properties =new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                properties.add(new TestUserProperty("name" + i, "value" + i));
            }
            userProperties = properties;
        }

        @Override
        public @NotNull Optional<String> getFirst(@NotNull String s) {
            return Optional.empty();
        }

        @Override
        public @NotNull List<String> getAllForName(@NotNull String s) {
            return null;
        }

        @Override
        public @NotNull List<UserProperty> asList() {
            return userProperties;
        }

        @Override
        public boolean isEmpty() {
            return userProperties.isEmpty();
        }
    }

    private static class TestUserProperty implements UserProperty {

        private final String name;
        private final String value;

        public TestUserProperty(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        @Override
        public @NotNull String getName() {
            return name;
        }

        @Override
        public @NotNull String getValue() {
            return value;
        }
    }
}