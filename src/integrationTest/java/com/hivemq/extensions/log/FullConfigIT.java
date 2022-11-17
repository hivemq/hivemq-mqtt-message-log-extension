package com.hivemq.extensions.log;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.testcontainers.hivemq.HiveMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.MountableFile;

import static com.hivemq.extensions.log.DockerImageNames.HIVEMQ;
import static org.awaitility.Awaitility.await;

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
        client.connectWith().keepAlive(1).send();

        client.subscribeWith().topicFilter("#").send();
        client.publishWith().topic("topic").qos(MqttQos.EXACTLY_ONCE).send();
        client.publishWith().topic("topic").qos(MqttQos.AT_MOST_ONCE).send();
        client.unsubscribeWith().topicFilter("#").send();
        client.disconnect();

        await().until(() -> hivemq.getLogs().contains("Received CONNECT from client"));
        await().until(() -> hivemq.getLogs().contains("Sent CONNACK to client"));
        await().until(() -> hivemq.getLogs().contains("Received SUBSCRIBE from client"));
        await().until(() -> hivemq.getLogs().contains("Sent SUBACK to client"));
        await().until(() -> hivemq.getLogs().contains("Received PUBLISH from client"));
        await().until(() -> hivemq.getLogs().contains("Sent PUBREC to client"));
        await().until(() -> hivemq.getLogs().contains("Sent PUBLISH to client"));
        await().until(() -> hivemq.getLogs().contains("Received PUBREL from client"));
        await().until(() -> hivemq.getLogs().contains("Sent PUBCOMP to client"));
        await().until(() -> hivemq.getLogs().contains("Received PUBREC from client"));
        await().until(() -> hivemq.getLogs().contains("Sent PUBREL to client"));
        await().until(() -> hivemq.getLogs().contains("Received PUBLISH from client"));
        await().until(() -> hivemq.getLogs().contains("Received UNSUBSCRIBE from client"));
        await().until(() -> hivemq.getLogs().contains("Sent UNSUBACK to client"));
        await().until(() -> hivemq.getLogs().contains("Received PUBCOMP from client"));
        await().until(() -> hivemq.getLogs().contains("Sent PUBLISH to client"));
        await().until(() -> hivemq.getLogs().contains("Received DISCONNECT from client"));
    }
}
