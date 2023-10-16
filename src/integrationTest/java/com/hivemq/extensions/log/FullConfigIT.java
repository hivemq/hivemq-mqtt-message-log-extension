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
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import static com.hivemq.extensions.log.DockerImageNames.HIVEMQ;
import static org.awaitility.Awaitility.await;

/**
 * @author Yannick Weber
 * @since 1.1.3
 */
@Testcontainers
public class FullConfigIT {

    @Container
    final @NotNull HiveMQContainer hivemq = new HiveMQContainer(HIVEMQ) //
            .withExtension(MountableFile.forClasspathResource("hivemq-mqtt-message-log-extension"))
            .waitForExtension("HiveMQ Mqtt Message Log Extension")
            .withFileInExtensionHomeFolder(MountableFile.forClasspathResource("fullConfig.properties"),
                    "hivemq-mqtt-message-log-extension",
                    "/mqttMessageLog.properties")
            .withLogConsumer(outputFrame -> System.out.print("HiveMQ: " + outputFrame.getUtf8String()));

    @Test
    void test() {
        final Mqtt5BlockingClient client =
                Mqtt5Client.builder().serverHost(hivemq.getHost()).serverPort(hivemq.getMqttPort()).buildBlocking();

        client.connectWith().send();
        await().until(() -> hivemq.getLogs().contains("Received CONNECT from client"));
        await().until(() -> hivemq.getLogs().contains("Sent CONNACK to client"));

        client.subscribeWith().topicFilter("#").send();
        await().until(() -> hivemq.getLogs().contains("Received SUBSCRIBE from client"));
        await().until(() -> hivemq.getLogs().contains("Sent SUBACK to client"));

        client.publishWith().topic("topic").qos(MqttQos.EXACTLY_ONCE).send();
        await().until(() -> hivemq.getLogs().contains("Received PUBLISH from client"));
        await().until(() -> hivemq.getLogs().contains("Sent PUBREC to client"));
        await().until(() -> hivemq.getLogs().contains("Received PUBREL from client"));
        await().until(() -> hivemq.getLogs().contains("Sent PUBCOMP to client"));

        await().until(() -> hivemq.getLogs().contains("Sent PUBLISH to client"));
        await().until(() -> hivemq.getLogs().contains("Received PUBREC from client"));
        await().until(() -> hivemq.getLogs().contains("Sent PUBREL to client"));
        await().until(() -> hivemq.getLogs().contains("Received PUBCOMP from client"));

        client.publishWith().topic("topic").qos(MqttQos.AT_LEAST_ONCE).send();
        await().until(() -> hivemq.getLogs().contains("Received PUBLISH from client"));
        await().until(() -> hivemq.getLogs().contains("Sent PUBACK to client"));
        await().until(() -> hivemq.getLogs().contains("Sent PUBLISH to client"));
        await().until(() -> hivemq.getLogs().contains("Received PUBACK from client"));

        client.unsubscribeWith().topicFilter("#").send();
        await().until(() -> hivemq.getLogs().contains("Received UNSUBSCRIBE from client"));
        await().until(() -> hivemq.getLogs().contains("Sent UNSUBACK to client"));

        client.disconnect();
        await().until(() -> hivemq.getLogs().contains("Received DISCONNECT from client"));
    }
}
