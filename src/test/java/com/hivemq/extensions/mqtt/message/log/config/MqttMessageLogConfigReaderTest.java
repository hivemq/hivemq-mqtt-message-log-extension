package com.hivemq.extensions.mqtt.message.log.config;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
class MqttMessageLogConfigReaderTest {

    private final int totalAvailableFlags = 20;

    private final @NotNull List<String> defaultProperties = List.of(MqttMessageLogConfig.CLIENT_CONNECT,
            MqttMessageLogConfig.CONNACK_SEND,
            MqttMessageLogConfig.CLIENT_DISCONNECT,
            MqttMessageLogConfig.PUBLISH_RECEIVED,
            MqttMessageLogConfig.PUBLISH_SEND,
            MqttMessageLogConfig.SUBSCRIBE_RECEIVED,
            MqttMessageLogConfig.SUBACK_SEND,
            MqttMessageLogConfig.UNSUBSCRIBE_RECEIVED,
            MqttMessageLogConfig.UNSUBACK_SEND,
            MqttMessageLogConfig.PING_REQ_RECEIVED,
            MqttMessageLogConfig.PING_RESP_SEND,
            MqttMessageLogConfig.PUBACK_RECEIVED,
            MqttMessageLogConfig.PUBACK_SEND,
            MqttMessageLogConfig.PUBREC_RECEIVED,
            MqttMessageLogConfig.PUBREC_SEND,
            MqttMessageLogConfig.PUBREL_RECEIVED,
            MqttMessageLogConfig.PUBREL_SEND,
            MqttMessageLogConfig.PUBCOMP_RECEIVED,
            MqttMessageLogConfig.PUBCOMP_SEND,
            MqttMessageLogConfig.VERBOSE);

    @Test
    void defaultPropertiesWhenNoPropertyFileInConfigFolder() {
        final Properties properties = getProperties("src/test/resources/empty-conf");

        assertEquals(properties.size(), totalAvailableFlags);
        assertTrue(properties.stringPropertyNames().containsAll(defaultProperties));
        assertTrue(defaultProperties.containsAll(properties.stringPropertyNames()));
    }

    @Test
    void nonEmptyPropertiesWhenPropertyFileInConfigFolder() {
        final Properties properties = getProperties("src/test/resources/test-conf");

        assertEquals(properties.size(), totalAvailableFlags);
        assertTrue(properties.stringPropertyNames().containsAll(defaultProperties));
        assertTrue(defaultProperties.containsAll(properties.stringPropertyNames()));
    }

    private @NotNull Properties getProperties(final @NotNull String confPath) {
        final MqttMessageLogConfigReader mqttMessageLogConfigReader =
                new MqttMessageLogConfigReader(new File(confPath));
        return mqttMessageLogConfigReader.readProperties();
    }
}
