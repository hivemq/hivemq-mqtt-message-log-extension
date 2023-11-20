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
package com.hivemq.extensions.log.mqtt.message.config;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
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
    void defaultPropertiesWhenNoPropertyFileInConfigFolder(@TempDir final @NotNull Path tempDir) {
        final Properties properties = new MqttMessageLogConfigReader(tempDir.toFile()).readProperties();

        assertEquals(properties.size(), totalAvailableFlags);
        assertTrue(properties.stringPropertyNames().containsAll(defaultProperties));
        assertTrue(defaultProperties.containsAll(properties.stringPropertyNames()));
    }

    @Test
    void nonEmptyPropertiesWhenPropertyFileInConfigFolder() {
        final String path = Objects.requireNonNull(getClass().getResource("/test-conf")).getPath();
        final Properties properties = new MqttMessageLogConfigReader(new File(path)).readProperties();

        assertEquals(properties.size(), totalAvailableFlags);
        assertTrue(properties.stringPropertyNames().containsAll(defaultProperties));
        assertTrue(defaultProperties.containsAll(properties.stringPropertyNames()));
    }
}
