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
package com.hivemq.extensions.log;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.client.mqtt.mqtt5.message.publish.Mqtt5PayloadFormatIndicator;
import io.github.sgtsilvio.gradle.oci.junit.jupiter.OciImages;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import java.nio.charset.StandardCharsets;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * @since 1.2.0
 */
@Testcontainers
public class FullConfigXmlNoPayloadJsonIT {

    @Container
    final @NotNull HiveMQContainer hivemq =
            new HiveMQContainer(OciImages.getImageName("hivemq/extensions/hivemq-mqtt-message-log-extension")
                    .asCompatibleSubstituteFor("hivemq/hivemq4")) //
                    .withCopyToContainer(MountableFile.forClasspathResource("fullConfigNoPayloadJson.xml"),
                            "/opt/hivemq/extensions/hivemq-mqtt-message-log-extension/conf/config.xml")
                    .withLogConsumer(outputFrame -> System.out.print("HiveMQ: " + outputFrame.getUtf8String()));

    @Test
    void test() {
        final Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier("test-client")
                .serverHost(hivemq.getHost())
                .serverPort(hivemq.getMqttPort())
                .buildBlocking();

        client.connectWith()
                .willPublish()
                .topic("will")
                .qos(MqttQos.EXACTLY_ONCE)
                .payload("willPayload".getBytes(StandardCharsets.UTF_8))
                .contentType("text/plain")
                .correlationData("willCorrelationData".getBytes(StandardCharsets.UTF_8))
                .payloadFormatIndicator(Mqtt5PayloadFormatIndicator.UTF_8)
                .responseTopic("willResponse")
                .retain(false)
                .messageExpiryInterval(10_000)
                .userProperties()
                .add("willProperty", "willValue")
                .applyUserProperties()
                .delayInterval(50_000)
                .applyWillPublish()
                .send();
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Received CONNECT\", \"Client\": \"test-client\", \"Protocol version\": \"V_5\", \"Clean Start\": \"true\", \"Session Expiry Interval\": \"0\", \"Keep Alive\": \"60\", \"Maximum Packet Size\": \"268435460\", \"Receive Maximum\": \"65535\", \"Topic Alias Maximum\": \"0\", \"Request Problem Information\": \"true\", \"Request Response Information\": \"false\",  \"Username\": \"null\", \"Password\": \"null\", \"Auth Method\": \"null\", \"Auth Data (Base64)\": \"null\", \"User Properties\": \"null\", \"Will\": { \"Topic\": \"will\", \"QoS\": \"2\", \"Retained\": \"false\", \"Message Expiry Interval\": \"10000\", \"Duplicate Delivery\": \"false\", \"Correlation Data\": \"d2lsbENvcnJlbGF0aW9uRGF0YQ==\", \"Response Topic\": \"willResponse\", \"Content Type\": \"text/plain\", \"Payload Format Indicator\": \"UTF_8\", \"Subscription Identifiers\": \"[]\", \"User Properties (1)\": [{\"Name (0)\": \"willProperty\", \"Value (0)\": \"willValue\"}], \"Will Delay\": \"50000\" }}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Sent CONNACK\", \"Client\": \"test-client\", \"Reason Code\": \"SUCCESS\", \"Session Present\": \"false\", \"Session Expiry Interval\": \"null\", \"Assigned ClientId\": \"null\", \"Maximum QoS\": \"EXACTLY_ONCE\", \"Maximum Packet Size\": \"268435460\", \"Receive Maximum\": \"10\", \"Topic Alias Maximum\": \"5\", \"Reason String\": \"null\", \"Response Information\": \"null\", \"Server Keep Alive\": \"null\", \"Server Reference\": \"null\", \"Shared Subscription Available\": \"true\", \"Wildcards Available\": \"true\", \"Retain Available\": \"true\", \"Subscription Identifiers Available\": \"true\", \"Auth Method\": \"null\", \"Auth Data (Base64)\": \"null\", \"User Properties\": \"null\"}"));

        client.subscribeWith().topicFilter("#").send();
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Received SUBSCRIBE\", \"Client\": \"test-client\", \"Topics\": [{\"Topic\": \"#\", \"QoS\": \"2\", \"Retain As Published\": \"false\", \"No Local\": \"false\", \"Retain Handling\": \"SEND\"} ], \"Subscription Identifier\": \"1\", \"User Properties\": \"null\"}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Sent SUBACK\", \"Client\": \"test-client\", \"Suback Reason Codes (1)\": [{\"Reason Code\": \"GRANTED_QOS_2\"} ], \"Reason String\": \"null\", \"User Properties\": \"null\"}"));

        client.publishWith()
                .topic("publish")
                .qos(MqttQos.EXACTLY_ONCE)
                .payload("payload1".getBytes(StandardCharsets.UTF_8))
                .contentType("text/plain")
                .correlationData("willCorrelationData".getBytes(StandardCharsets.UTF_8))
                .payloadFormatIndicator(Mqtt5PayloadFormatIndicator.UTF_8)
                .responseTopic("publishResponse")
                .retain(false)
                .messageExpiryInterval(10_000)
                .userProperties()
                .add("publishProperty", "publishValue")
                .applyUserProperties()
                .send();
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Received PUBLISH\", \"Client\": \"test-client\", \"Topic\": \"publish\", \"QoS\": \"2\", \"Retained\": \"false\", \"Message Expiry Interval\": \"10000\", \"Duplicate Delivery\": \"false\", \"Correlation Data\": \"d2lsbENvcnJlbGF0aW9uRGF0YQ==\", \"Response Topic\": \"publishResponse\", \"Content Type\": \"text/plain\", \"Payload Format Indicator\": \"UTF_8\", \"Subscription Identifiers\": \"[]\", \"User Properties (1)\": [{\"Name (0)\": \"publishProperty\", \"Value (0)\": \"publishValue\"}]}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Sent PUBREC\", \"Client\": \"test-client\", \"Reason Code\": \"SUCCESS\", \"Reason String\": \"null\", \"User Properties\": \"null\"}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Received PUBREL\", \"Client\": \"test-client\", \"Reason Code\": \"SUCCESS\", \"Reason String\": \"null\", \"User Properties\": \"null\"}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Sent PUBCOMP\", \"Client\": \"test-client\", \"Reason Code\": \"SUCCESS\", \"Reason String\": \"null\", \"User Properties\": \"null\"}"));

        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Sent PUBLISH\", \"Client\": \"test-client\", \"Topic\": \"publish\", \"QoS\": \"2\", \"Retained\": \"false\", \"Message Expiry Interval\": \"10000\", \"Duplicate Delivery\": \"false\", \"Correlation Data\": \"d2lsbENvcnJlbGF0aW9uRGF0YQ==\", \"Response Topic\": \"publishResponse\", \"Content Type\": \"text/plain\", \"Payload Format Indicator\": \"UTF_8\", \"Subscription Identifiers\": \"[1]\", \"User Properties (1)\": [{\"Name (0)\": \"publishProperty\", \"Value (0)\": \"publishValue\"}]}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Received PUBREC\", \"Client\": \"test-client\", \"Reason Code\": \"SUCCESS\", \"Reason String\": \"null\", \"User Properties\": \"null\"}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Sent PUBREL\", \"Client\": \"test-client\", \"Reason Code\": \"SUCCESS\", \"Reason String\": \"null\", \"User Properties\": \"null\"}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Received PUBCOMP\", \"Client\": \"test-client\", \"Reason Code\": \"SUCCESS\", \"Reason String\": \"null\", \"User Properties\": \"null\"}"));

        client.publishWith()
                .topic("publish")
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload("payload2".getBytes(StandardCharsets.UTF_8))
                .contentType("text/plain")
                .correlationData("willCorrelationData".getBytes(StandardCharsets.UTF_8))
                .payloadFormatIndicator(Mqtt5PayloadFormatIndicator.UTF_8)
                .responseTopic("publishResponse")
                .retain(false)
                .messageExpiryInterval(10_000)
                .userProperties()
                .add("publishProperty", "publishValue")
                .applyUserProperties()
                .send();
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Received PUBLISH\", \"Client\": \"test-client\", \"Topic\": \"publish\", \"QoS\": \"1\", \"Retained\": \"false\", \"Message Expiry Interval\": \"10000\", \"Duplicate Delivery\": \"false\", \"Correlation Data\": \"d2lsbENvcnJlbGF0aW9uRGF0YQ==\", \"Response Topic\": \"publishResponse\", \"Content Type\": \"text/plain\", \"Payload Format Indicator\": \"UTF_8\", \"Subscription Identifiers\": \"[]\", \"User Properties (1)\": [{\"Name (0)\": \"publishProperty\", \"Value (0)\": \"publishValue\"}]}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Sent PUBACK\", \"Client\": \"test-client\", \"Reason Code\": \"SUCCESS\", \"Reason String\": \"null\", \"User Properties\": \"null\"}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Sent PUBLISH\", \"Client\": \"test-client\", \"Topic\": \"publish\", \"QoS\": \"1\", \"Retained\": \"false\", \"Message Expiry Interval\": \"10000\", \"Duplicate Delivery\": \"false\", \"Correlation Data\": \"d2lsbENvcnJlbGF0aW9uRGF0YQ==\", \"Response Topic\": \"publishResponse\", \"Content Type\": \"text/plain\", \"Payload Format Indicator\": \"UTF_8\", \"Subscription Identifiers\": \"[1]\", \"User Properties (1)\": [{\"Name (0)\": \"publishProperty\", \"Value (0)\": \"publishValue\"}]}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Received PUBACK\", \"Client\": \"test-client\", \"Reason Code\": \"SUCCESS\", \"Reason String\": \"null\", \"User Properties\": \"null\"}"));

        client.unsubscribeWith().topicFilter("#").send();
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Received UNSUBSCRIBE\", \"Client\": \"test-client\", \"Topics\": [ {\"Topic\": \"#\"} ],  \"User Properties\": \"null\"}"));
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Sent UNSUBACK\", \"Client\": \"test-client\", \"Unsuback Reason Codes (1)\": [ {\"Reason Code\": \"SUCCESS\"} ], \"Reason String\": \"null\", \"User Properties\": \"null\"}"));

        client.disconnect();
        await().until(() -> hivemq.getLogs()
                .contains(
                        "{\"Event\": \"Received DISCONNECT\", \"Client\": \"test-client\", \"Reason Code\": \"NORMAL_DISCONNECTION\", \"Reason String\": \"null\", \"Server Reference\": \"null\", \"Session Expiry\": \"null\", \"User Properties\": \"null\"}"));

        assertFalse(hivemq.getLogs()
                .contains(
                        "\"Payload (Base64)\": \"cGF5bG9hZDE=\""));
        assertFalse(hivemq.getLogs()
                .contains(
                        "\"Payload (Base64)\": \"cGF5bG9hZDI=\""));
    }
}
