package com.hivemq.extension.mqtt;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.parameter.ClientInformation;
import com.hivemq.extension.sdk.api.client.parameter.ConnectionInformation;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.interceptor.connack.parameter.ConnackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.pingreq.parameter.PingReqInboundInput;
import com.hivemq.extension.sdk.api.interceptor.pingresp.parameter.PingRespOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.suback.parameter.SubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.subscribe.parameter.SubscribeInboundInput;
import com.hivemq.extension.sdk.api.interceptor.unsuback.parameter.UnsubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.unsubscribe.parameter.UnsubscribeInboundInput;
import com.hivemq.extension.sdk.api.packets.connack.ConnackPacket;
import com.hivemq.extension.sdk.api.packets.connect.ConnackReasonCode;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extension.sdk.api.packets.connect.WillPublishPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectPacket;
import com.hivemq.extension.sdk.api.packets.disconnect.DisconnectReasonCode;
import com.hivemq.extension.sdk.api.packets.general.*;
import com.hivemq.extension.sdk.api.packets.puback.PubackPacket;
import com.hivemq.extension.sdk.api.packets.pubcomp.PubcompPacket;
import com.hivemq.extension.sdk.api.packets.pubcomp.PubcompReasonCode;
import com.hivemq.extension.sdk.api.packets.publish.AckReasonCode;
import com.hivemq.extension.sdk.api.packets.publish.PayloadFormatIndicator;
import com.hivemq.extension.sdk.api.packets.publish.PublishPacket;
import com.hivemq.extension.sdk.api.packets.pubrec.PubrecPacket;
import com.hivemq.extension.sdk.api.packets.pubrel.PubrelPacket;
import com.hivemq.extension.sdk.api.packets.pubrel.PubrelReasonCode;
import com.hivemq.extension.sdk.api.packets.suback.SubackPacket;
import com.hivemq.extension.sdk.api.packets.subscribe.RetainHandling;
import com.hivemq.extension.sdk.api.packets.subscribe.SubackReasonCode;
import com.hivemq.extension.sdk.api.packets.subscribe.SubscribePacket;
import com.hivemq.extension.sdk.api.packets.subscribe.Subscription;
import com.hivemq.extension.sdk.api.packets.unsuback.UnsubackPacket;
import com.hivemq.extension.sdk.api.packets.unsuback.UnsubackReasonCode;
import com.hivemq.extension.sdk.api.packets.unsubscribe.UnsubscribePacket;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @author Florian Limpöck
 * @author Michael Walter
 * @version 1.1.0
 */
public class PacketUtil {

    public static SubscribeInboundInput createEmptySubscribe() {
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
                        return new PacketUtil.TestUserProperties(0);
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

    public static SubscribeInboundInput createFullSubsribe() {
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
                        return new PacketUtil.TestUserProperties(2);
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

    public static SubackOutboundInput createEmptySuback() {
        return new SubackOutboundInput() {
            @Override
            public @NotNull SubackPacket getSubackPacket() {
                return new SubackPacket() {
                    @Override
                    public int getPacketIdentifier() {
                        return 1;
                    }

                    @Override
                    public @NotNull List<SubackReasonCode> getReasonCodes() {
                        return List.of(SubackReasonCode.GRANTED_QOS_1);
                    }

                    @Override
                    public @NotNull Optional<String> getReasonString() {
                        return Optional.empty();
                    }

                    @Override
                    public @NotNull UserProperties getUserProperties() {
                        return new PacketUtil.TestUserProperties(0);
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

    public static SubackOutboundInput createFullSuback() {
        return new SubackOutboundInput() {
            @Override
            public @NotNull SubackPacket getSubackPacket() {
                return new SubackPacket() {
                    @Override
                    public int getPacketIdentifier() {
                        return 1;
                    }

                    @Override
                    public @NotNull List<SubackReasonCode> getReasonCodes() {
                        return List.of(SubackReasonCode.GRANTED_QOS_1, SubackReasonCode.GRANTED_QOS_0);
                    }

                    @Override
                    public @NotNull Optional<String> getReasonString() {
                        return Optional.of("Okay");
                    }

                    @Override
                    public @NotNull UserProperties getUserProperties() {
                        return new PacketUtil.TestUserProperties(2);
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

    public static UnsubscribeInboundInput createEmptyUnsubscribe() {
        return new UnsubscribeInboundInput() {
            @Override
            public @NotNull UnsubscribePacket getUnsubscribePacket() {
                return new UnsubscribePacket() {
                    @Override
                    public int getPacketIdentifier() {
                        return 1;
                    }

                    @Override
                    public @NotNull List<String> getTopicFilters() {
                        return List.of("topic1", "topic2");
                    }

                    @Override
                    public @NotNull UserProperties getUserProperties() {
                        return new PacketUtil.TestUserProperties(0);
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

    public static UnsubscribeInboundInput createFullUnsubsribe() {
        return new UnsubscribeInboundInput() {
            @Override
            public @NotNull UnsubscribePacket getUnsubscribePacket() {
                return new UnsubscribePacket() {
                    @Override
                    public int getPacketIdentifier() {
                        return 1;
                    }

                    @Override
                    public @NotNull List<String> getTopicFilters() {
                        return List.of("topic1");
                    }

                    @Override
                    public @NotNull UserProperties getUserProperties() {
                        return new PacketUtil.TestUserProperties(2);
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

    public static UnsubackOutboundInput createEmptyUnsuback() {
        return new UnsubackOutboundInput() {
            @Override
            public @NotNull UnsubackPacket getUnsubackPacket() {
                return new UnsubackPacket() {
                    @Override
                    public int getPacketIdentifier() {
                        return 1;
                    }

                    @Override
                    public @NotNull List<UnsubackReasonCode> getReasonCodes() {
                        return List.of(UnsubackReasonCode.NOT_AUTHORIZED);
                    }

                    @Override
                    public @NotNull Optional<String> getReasonString() {
                        return Optional.empty();
                    }

                    @Override
                    public @NotNull UserProperties getUserProperties() {
                        return new PacketUtil.TestUserProperties(0);
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

    public static UnsubackOutboundInput createFullUnsuback() {
        return new UnsubackOutboundInput() {
            @Override
            public @NotNull UnsubackPacket getUnsubackPacket() {
                return new UnsubackPacket() {
                    @Override
                    public int getPacketIdentifier() {
                        return 1;
                    }

                    @Override
                    public @NotNull List<UnsubackReasonCode> getReasonCodes() {
                        return List.of(UnsubackReasonCode.NOT_AUTHORIZED, UnsubackReasonCode.SUCCESS);
                    }

                    @Override
                    public @NotNull Optional<String> getReasonString() {
                        return Optional.of("Okay");
                    }

                    @Override
                    public @NotNull UserProperties getUserProperties() {
                        return new PacketUtil.TestUserProperties(3);
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

    public static PublishPacket createEmptyPublish() {
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

    public static PublishPacket createFullPublish() {
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
                return List.of(1, 2, 3, 4);
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
                return new PacketUtil.TestUserProperties(2);
            }
        };
    }

    public static ConnectPacket createEmptyConnect() {
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

    public static ConnectPacket createFullConnect() {
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
                        return List.of(1, 2, 3, 4);
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
                        return new PacketUtil.TestUserProperties(3);
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
                return new PacketUtil.TestUserProperties(2);
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

    public static DisconnectPacket createEmptyDisconnect() {
        return new DisconnectPacket() {
            @Override
            public @NotNull DisconnectReasonCode getReasonCode() {
                return DisconnectReasonCode.NOT_AUTHORIZED;
            }

            @Override
            public @NotNull Optional<String> getReasonString() {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<Long> getSessionExpiryInterval() {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<String> getServerReference() {
                return Optional.empty();
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return null;
            }
        };
    }

    public static DisconnectPacket createFullDisconnect() {
        return new DisconnectPacket() {
            @Override
            public @NotNull DisconnectReasonCode getReasonCode() {
                return DisconnectReasonCode.NOT_AUTHORIZED;
            }

            @Override
            public @NotNull Optional<String> getReasonString() {
                return Optional.of("Okay");
            }

            @Override
            public @NotNull Optional<Long> getSessionExpiryInterval() {
                return Optional.of(123L);
            }

            @Override
            public @NotNull Optional<String> getServerReference() {
                return Optional.of("Server2");
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return new PacketUtil.TestUserProperties(2);
            }
        };
    }

    public static DisconnectPacket createLifeCycleCompareDisconnect() {
        return new DisconnectPacket() {
            @Override
            public @NotNull DisconnectReasonCode getReasonCode() {
                return DisconnectReasonCode.BAD_AUTHENTICATION_METHOD;
            }

            @Override
            public @NotNull Optional<String> getReasonString() {
                return Optional.of("Okay");
            }

            @Override
            public @NotNull Optional<Long> getSessionExpiryInterval() {
                return Optional.empty();
            }

            @Override
            public @NotNull Optional<String> getServerReference() {
                return Optional.empty();
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return new PacketUtil.TestUserProperties(3);
            }
        };
    }

    public static ConnackOutboundInput createEmptyConnack() {
        return new ConnackOutboundInput() {
            @Override
            public @NotNull ConnackPacket getConnackPacket() {
                return new ConnackPacket() {
                    @Override
                    public @NotNull Optional<Long> getSessionExpiryInterval() {
                        return Optional.empty();
                    }

                    @Override
                    public @NotNull Optional<Integer> getServerKeepAlive() {
                        return Optional.empty();
                    }

                    @Override
                    public int getReceiveMaximum() {
                        return 1;
                    }

                    @Override
                    public int getMaximumPacketSize() {
                        return 2;
                    }

                    @Override
                    public int getTopicAliasMaximum() {
                        return 3;
                    }

                    @Override
                    public @NotNull Optional<Qos> getMaximumQoS() {
                        return Optional.empty();
                    }

                    @Override
                    public @NotNull UserProperties getUserProperties() {
                        return null;
                    }

                    @Override
                    public @NotNull ConnackReasonCode getReasonCode() {
                        return ConnackReasonCode.SUCCESS;
                    }

                    @Override
                    public boolean getSessionPresent() {
                        return false;
                    }

                    @Override
                    public boolean getRetainAvailable() {
                        return false;
                    }

                    @Override
                    public @NotNull Optional<String> getAssignedClientIdentifier() {
                        return Optional.empty();
                    }

                    @Override
                    public @NotNull Optional<String> getReasonString() {
                        return Optional.empty();
                    }

                    @Override
                    public boolean getWildCardSubscriptionAvailable() {
                        return false;
                    }

                    @Override
                    public boolean getSubscriptionIdentifiersAvailable() {
                        return false;
                    }

                    @Override
                    public boolean getSharedSubscriptionsAvailable() {
                        return false;
                    }

                    @Override
                    public @NotNull Optional<String> getResponseInformation() {
                        return Optional.empty();
                    }

                    @Override
                    public @NotNull Optional<String> getServerReference() {
                        return Optional.empty();
                    }

                    @Override
                    public @NotNull Optional<String> getAuthenticationMethod() {
                        return Optional.empty();
                    }

                    @Override
                    public @NotNull Optional<ByteBuffer> getAuthenticationData() {
                        return Optional.empty();
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

    public static ConnackOutboundInput createFullConnack() {
        return new ConnackOutboundInput() {
            @Override
            public @NotNull ConnackPacket getConnackPacket() {
                return new ConnackPacket() {
                    @Override
                    public @NotNull Optional<Long> getSessionExpiryInterval() {
                        return Optional.of(100L);
                    }

                    @Override
                    public @NotNull Optional<Integer> getServerKeepAlive() {
                        return Optional.of(100);
                    }

                    @Override
                    public int getReceiveMaximum() {
                        return 10;
                    }

                    @Override
                    public int getMaximumPacketSize() {
                        return 5;
                    }

                    @Override
                    public int getTopicAliasMaximum() {
                        return 5;
                    }

                    @Override
                    public @NotNull Optional<Qos> getMaximumQoS() {
                        return Optional.of(Qos.AT_MOST_ONCE);
                    }

                    @Override
                    public @NotNull UserProperties getUserProperties() {
                        return new PacketUtil.TestUserProperties(2);
                    }

                    @Override
                    public @NotNull ConnackReasonCode getReasonCode() {
                        return ConnackReasonCode.SUCCESS;
                    }

                    @Override
                    public boolean getSessionPresent() {
                        return false;
                    }

                    @Override
                    public boolean getRetainAvailable() {
                        return false;
                    }

                    @Override
                    public @NotNull Optional<String> getAssignedClientIdentifier() {
                        return Optional.of("overwriteClientId");
                    }

                    @Override
                    public @NotNull Optional<String> getReasonString() {
                        return Optional.of("Okay");
                    }

                    @Override
                    public boolean getWildCardSubscriptionAvailable() {
                        return false;
                    }

                    @Override
                    public boolean getSubscriptionIdentifiersAvailable() {
                        return false;
                    }

                    @Override
                    public boolean getSharedSubscriptionsAvailable() {
                        return false;
                    }

                    @Override
                    public @NotNull Optional<String> getResponseInformation() {
                        return Optional.of("Everything fine");
                    }

                    @Override
                    public @NotNull Optional<String> getServerReference() {
                        return Optional.of("Server2");
                    }

                    @Override
                    public @NotNull Optional<String> getAuthenticationMethod() {
                        return Optional.of("JSON");
                    }

                    @Override
                    public @NotNull Optional<ByteBuffer> getAuthenticationData() {
                        return Optional.of(ByteBuffer.wrap("auth data".getBytes()));
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

    public static PingReqInboundInput createPingreq() {
        return new PingReqInboundInput() {
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

    public static PingRespOutboundInput createPingresp() {
        return new PingRespOutboundInput() {
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

    public static PubackPacket createEmptyPuback() {
        return new PubackPacket() {
            @Override
            public int getPacketIdentifier() {
                return 10;
            }

            @Override
            public @NotNull AckReasonCode getReasonCode() {
                return AckReasonCode.NO_MATCHING_SUBSCRIBERS;
            }

            @Override
            public @NotNull Optional<String> getReasonString() {
                return Optional.empty();
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return null;
            }
        };
    }

    public static PubackPacket createFullPuback() {
        return new PubackPacket() {
            @Override
            public int getPacketIdentifier() {
                return 10;
            }

            @Override
            public @NotNull AckReasonCode getReasonCode() {
                return AckReasonCode.NO_MATCHING_SUBSCRIBERS;
            }

            @Override
            public @NotNull Optional<String> getReasonString() {
                return Optional.of("Okay");
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return new PacketUtil.TestUserProperties(2);
            }
        };
    }

    public static PubrecPacket createEmptyPubrec() {
        return new PubrecPacket() {
            @Override
            public int getPacketIdentifier() {
                return 10;
            }

            @Override
            public @NotNull AckReasonCode getReasonCode() {
                return AckReasonCode.NO_MATCHING_SUBSCRIBERS;
            }

            @Override
            public @NotNull Optional<String> getReasonString() {
                return Optional.empty();
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return null;
            }
        };
    }

    public static PubrecPacket createFullPubrec() {
        return new PubrecPacket() {
            @Override
            public int getPacketIdentifier() {
                return 10;
            }

            @Override
            public @NotNull AckReasonCode getReasonCode() {
                return AckReasonCode.SUCCESS;
            }

            @Override
            public @NotNull Optional<String> getReasonString() {
                return Optional.of("Okay");
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return new PacketUtil.TestUserProperties(1);
            }
        };
    }

    public static PubrelPacket createEmptyPubrel() {
        return new PubrelPacket() {
            @Override
            public int getPacketIdentifier() {
                return 10;
            }

            @Override
            public @NotNull PubrelReasonCode getReasonCode() {
                return PubrelReasonCode.SUCCESS;
            }

            @Override
            public @NotNull Optional<String> getReasonString() {
                return Optional.empty();
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return null;
            }
        };
    }

    public static PubrelPacket createFullPubrel() {
        return new PubrelPacket() {
            @Override
            public int getPacketIdentifier() {
                return 10;
            }

            @Override
            public @NotNull PubrelReasonCode getReasonCode() {
                return PubrelReasonCode.PACKET_IDENTIFIER_NOT_FOUND;
            }

            @Override
            public @NotNull Optional<String> getReasonString() {
                return Optional.of("Okay");
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return new PacketUtil.TestUserProperties(1);
            }
        };
    }

    public static PubcompPacket createEmptyPubcomp() {
        return new PubcompPacket() {
            @Override
            public int getPacketIdentifier() {
                return 10;
            }

            @Override
            public @NotNull PubcompReasonCode getReasonCode() {
                return PubcompReasonCode.SUCCESS;
            }

            @Override
            public @NotNull Optional<String> getReasonString() {
                return Optional.empty();
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return null;
            }
        };
    }

    public static PubcompPacket createFullPubcomp() {
        return new PubcompPacket() {
            @Override
            public int getPacketIdentifier() {
                return 10;
            }

            @Override
            public @NotNull PubcompReasonCode getReasonCode() {
                return PubcompReasonCode.PACKET_IDENTIFIER_NOT_FOUND;
            }

            @Override
            public @NotNull Optional<String> getReasonString() {
                return Optional.of("Okay");
            }

            @Override
            public @NotNull UserProperties getUserProperties() {
                return new PacketUtil.TestUserProperties(1);
            }
        };
    }

    @SuppressWarnings("NullabilityAnnotations")
    public static class TestDisconnect implements DisconnectEventInput {

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

    public static class TestUserProperties implements UserProperties {

        private final List<UserProperty> userProperties;

        public TestUserProperties(final int amount) {
            final List<UserProperty> properties = new ArrayList<>();
            for (int i = 0; i < amount; i++) {
                properties.add(new PacketUtil.TestUserProperty("name" + i, "value" + i));
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

    public static class TestUserProperty implements UserProperty {

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
