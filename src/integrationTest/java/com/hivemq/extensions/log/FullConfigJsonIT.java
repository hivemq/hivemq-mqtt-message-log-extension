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

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

/**
 * Integration test for JSON output format with verbose output, payloads enabled, and passwords not redacted.
 *
 * @since 1.3.0
 */
@Testcontainers
public class FullConfigJsonIT {

    @Container
    final @NotNull HiveMQContainer hivemq =
            new HiveMQContainer(OciImages.getImageName("hivemq/extensions/hivemq-mqtt-message-log-extension")
                    .asCompatibleSubstituteFor("hivemq/hivemq4")) //
                    .withCopyToContainer(MountableFile.forClasspathResource("fullConfigJson.xml"),
                            "/opt/hivemq/extensions/hivemq-mqtt-message-log-extension/conf/config.xml")
                    .withLogConsumer(outputFrame -> System.out.print("HiveMQ: " + outputFrame.getUtf8String()));

    @Test
    void test() {
        final var client = Mqtt5Client.builder()
                .identifier("json-test-client")
                .serverHost(hivemq.getHost())
                .serverPort(hivemq.getMqttPort())
                .simpleAuth()
                .username("test-user")
                .password("test-password".getBytes(StandardCharsets.UTF_8))
                .applySimpleAuth()
                .buildBlocking();

        client.connectWith()
                .willPublish()
                .topic("will/topic")
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload("willPayloadData".getBytes(StandardCharsets.UTF_8))
                .contentType("application/json")
                .correlationData("willCorr123".getBytes(StandardCharsets.UTF_8))
                .payloadFormatIndicator(Mqtt5PayloadFormatIndicator.UTF_8)
                .responseTopic("will/response")
                .retain(false)
                .messageExpiryInterval(30000)
                .userProperties()
                .add("willProp1", "willValue1")
                .add("willProp2", "willValue2")
                .applyUserProperties()
                .delayInterval(60000)
                .applyWillPublish()
                .send();
        assertJsonMessage("""
                {"timestamp":12345,\
                "messageType":"CONNECT",\
                "direction":"INBOUND",\
                "clientId":"json-test-client",\
                "protocolVersion":"V_5",\
                "cleanStart":true,\
                "sessionExpiryInterval":0,\
                "keepAlive":60,\
                "maximumPacketSize":268435460,\
                "receiveMaximum":65535,\
                "topicAliasMaximum":0,\
                "requestProblemInformation":true,\
                "requestResponseInformation":false,\
                "username":"test-user",\
                "password":"test-password",\
                "will":{"topic":"will/topic","qos":1,"retained":false,"willDelay":60000,\
                "payload":"willPayloadData","messageExpiryInterval":30000,\
                "correlationData":"willCorr123","responseTopic":"will/response",\
                "contentType":"application/json","payloadFormatIndicator":"UTF_8",\
                "userProperties":[{"name":"willProp1","value":"willValue1"},\
                {"name":"willProp2","value":"willValue2"}]}}""");
        assertJsonMessage("""
                {"timestamp":12345,\
                "messageType":"CONNACK",\
                "direction":"OUTBOUND",\
                "clientId":"json-test-client",\
                "reasonCode":"SUCCESS",\
                "sessionPresent":false,\
                "maximumQoS":2,\
                "maximumPacketSize":268435460,\
                "receiveMaximum":10,\
                "topicAliasMaximum":5,\
                "sharedSubscriptionsAvailable":true,\
                "wildCardSubscriptionAvailable":true,\
                "retainAvailable":true,\
                "subscriptionIdentifiersAvailable":true}""");

        client.subscribeWith().topicFilter("test/#").send();
        assertJsonMessage("""
                {"timestamp":12345,\
                "messageType":"SUBSCRIBE",\
                "direction":"INBOUND",\
                "clientId":"json-test-client",\
                "subscriptions":[{"topicFilter":"test/#","qos":2,\
                "retainAsPublished":false,"noLocal":false,"retainHandling":"SEND"}],\
                "subscriptionIdentifier":1}""");
        assertJsonMessage("""
                {"timestamp":12345,\
                "messageType":"SUBACK",\
                "direction":"OUTBOUND",\
                "clientId":"json-test-client",\
                "reasonCodes":["GRANTED_QOS_2"]}""");

        // publish with binary payload to test Base64 encoding
        final var binaryPayload = new byte[]{0x00, 0x01, 0x02, (byte) 0xFF, (byte) 0xFE, 0x7F};
        client.publishWith()
                .topic("test/binary")
                .qos(MqttQos.AT_LEAST_ONCE)
                .payload(binaryPayload)
                .contentType("application/octet-stream")
                .correlationData("corrData".getBytes(StandardCharsets.UTF_8))
                .payloadFormatIndicator(Mqtt5PayloadFormatIndicator.UNSPECIFIED)
                .responseTopic("test/response")
                .retain(false)
                .messageExpiryInterval(5000)
                .userProperties()
                .add("testProp", "testValue")
                .applyUserProperties()
                .send();
        assertJsonMessage("""
                {"timestamp":12345,\
                "messageType":"PUBLISH",\
                "direction":"INBOUND",\
                "topic":"test/binary",\
                "payloadBase64":"AAEC//5/",\
                "qos":1,\
                "retained":false,\
                "messageExpiryInterval":5000,\
                "duplicateDelivery":false,\
                "correlationData":"corrData",\
                "responseTopic":"test/response",\
                "contentType":"application/octet-stream",\
                "payloadFormatIndicator":"UNSPECIFIED",\
                "userProperties":[{"name":"testProp","value":"testValue"}]}""");
        assertJsonMessage("""
                {"timestamp":12345,\
                "messageType":"PUBACK",\
                "direction":"OUTBOUND",\
                "clientId":"json-test-client",\
                "reasonCode":"SUCCESS"}""");

        // publish with text payload
        client.publishWith()
                .topic("test/text")
                .qos(MqttQos.AT_MOST_ONCE)
                .payload("Hello JSON World!".getBytes(StandardCharsets.UTF_8))
                .payloadFormatIndicator(Mqtt5PayloadFormatIndicator.UTF_8)
                .send();
        assertJsonMessage("""
                {"timestamp":12345,\
                "messageType":"PUBLISH",\
                "direction":"INBOUND",\
                "topic":"test/text",\
                "payload":"Hello JSON World!",\
                "qos":0,\
                "retained":false,\
                "duplicateDelivery":false,\
                "payloadFormatIndicator":"UTF_8"}""");

        client.unsubscribeWith().topicFilter("test/#").send();
        assertJsonMessage("""
                {"timestamp":12345,\
                "messageType":"UNSUBSCRIBE",\
                "direction":"INBOUND",\
                "clientId":"json-test-client",\
                "topicFilters":["test/#"]}""");
        assertJsonMessage("""
                {"timestamp":12345,\
                "messageType":"UNSUBACK",\
                "direction":"OUTBOUND",\
                "clientId":"json-test-client",\
                "reasonCodes":["SUCCESS"]}""");

        client.disconnect();
        assertJsonMessage("""
                {"timestamp":12345,\
                "messageType":"DISCONNECT",\
                "direction":"INBOUND",\
                "clientId":"json-test-client",\
                "reasonCode":"NORMAL_DISCONNECTION"}""");
    }

    /**
     * Helper method to assert that the HiveMQ logs contain the expected JSON message.
     * This method waits until the message appears in the logs.
     * The timestamp is normalized to a fixed value (12345) to allow for exact matching.
     *
     * @param expectedJsonMessage the expected JSON messages to find in the logs
     */
    private void assertJsonMessage(final @NotNull String expectedJsonMessage) {
        await().untilAsserted(() -> {
            final var logs = hivemq.getLogs().replaceAll("\"timestamp\":\\d+,", "\"timestamp\":12345,");
            assertThat(logs).contains(expectedJsonMessage);
        });
    }
}
