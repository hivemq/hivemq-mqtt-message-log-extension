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

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @since 1.0.0
 */
class ExtensionConfigReaderTest {

    private final int totalAvailableFlags = 22;

    private final @NotNull List<String> defaultProperties = List.of(ExtensionConfigProperties.CLIENT_CONNECT,
            ExtensionConfigProperties.CONNACK_SEND,
            ExtensionConfigProperties.CLIENT_DISCONNECT,
            ExtensionConfigProperties.PUBLISH_RECEIVED,
            ExtensionConfigProperties.PUBLISH_SEND,
            ExtensionConfigProperties.SUBSCRIBE_RECEIVED,
            ExtensionConfigProperties.SUBACK_SEND,
            ExtensionConfigProperties.UNSUBSCRIBE_RECEIVED,
            ExtensionConfigProperties.UNSUBACK_SEND,
            ExtensionConfigProperties.PING_REQUEST_RECEIVED,
            ExtensionConfigProperties.PING_RESPONSE_SEND,
            ExtensionConfigProperties.PUBACK_RECEIVED,
            ExtensionConfigProperties.PUBACK_SEND,
            ExtensionConfigProperties.PUBREC_RECEIVED,
            ExtensionConfigProperties.PUBREC_SEND,
            ExtensionConfigProperties.PUBREL_RECEIVED,
            ExtensionConfigProperties.PUBREL_SEND,
            ExtensionConfigProperties.PUBCOMP_RECEIVED,
            ExtensionConfigProperties.PUBCOMP_SEND,
            ExtensionConfigProperties.VERBOSE,
            ExtensionConfigProperties.PAYLOAD,
            ExtensionConfigProperties.JSON);

    @Test
    void defaultPropertiesWhenNoPropertyFileInConfigFolder(@TempDir final @NotNull Path tempDir) {
        final ExtensionConfig extensionConfig = ExtensionConfigReader.read(tempDir.toFile());
        assertInstanceOf(ExtensionConfigProperties.class, extensionConfig);

        final ExtensionConfigProperties extensionConfigProperties = ((ExtensionConfigProperties) extensionConfig);
        final Properties properties = extensionConfigProperties.getProperties();

        assertEquals(properties.size(), totalAvailableFlags);
        assertTrue(properties.stringPropertyNames().containsAll(defaultProperties));
        assertTrue(defaultProperties.containsAll(properties.stringPropertyNames()));

        assertTrue(extensionConfigProperties.isClientConnect());
        assertTrue(extensionConfigProperties.isClientDisconnect());
        assertTrue(extensionConfigProperties.isConnackSend());
        assertTrue(extensionConfigProperties.isPublishReceived());
        assertTrue(extensionConfigProperties.isPublishSend());
        assertTrue(extensionConfigProperties.isSubscribeReceived());
        assertTrue(extensionConfigProperties.isSubackSend());
        assertTrue(extensionConfigProperties.isUnsubscribeReceived());
        assertTrue(extensionConfigProperties.isUnsubackSend());
        assertTrue(extensionConfigProperties.isPingRequestReceived());
        assertTrue(extensionConfigProperties.isPingResponseSend());
        assertTrue(extensionConfigProperties.isPubackReceived());
        assertTrue(extensionConfigProperties.isPubackSend());
        assertTrue(extensionConfigProperties.isPubrelReceived());
        assertTrue(extensionConfigProperties.isPubrelSend());
        assertTrue(extensionConfigProperties.isPubrecReceived());
        assertTrue(extensionConfigProperties.isPubrecSend());
        assertTrue(extensionConfigProperties.isPubcompReceived());
        assertTrue(extensionConfigProperties.isPubcompSend());
        assertFalse(extensionConfigProperties.isVerbose());
        assertTrue(extensionConfigProperties.isPayload());
        assertFalse(extensionConfigProperties.isJson());
    }

    @Test
    void nonEmptyPropertiesWhenPropertyFileInConfigFolder() {
        final String path = Objects.requireNonNull(getClass().getResource("/test-conf")).getPath();
        final ExtensionConfig extensionConfig = ExtensionConfigReader.read(new File(path));
        assertInstanceOf(ExtensionConfigProperties.class, extensionConfig);

        final ExtensionConfigProperties extensionConfigProperties = ((ExtensionConfigProperties) extensionConfig);
        final Properties properties = extensionConfigProperties.getProperties();

        assertEquals(properties.size(), totalAvailableFlags);
        assertTrue(properties.stringPropertyNames().containsAll(defaultProperties));
        assertTrue(defaultProperties.containsAll(properties.stringPropertyNames()));

        assertFalse(extensionConfigProperties.isVerbose());
        assertTrue(extensionConfigProperties.isPayload());
        assertFalse(extensionConfigProperties.isJson());
        assertFalse(extensionConfigProperties.isPublishReceived());
        assertFalse(extensionConfigProperties.isPublishSend());
    }

    @Test
    void nonEmptyPropertiesWhenConfigFileInConfFolder() {
        final String path = Objects.requireNonNull(getClass().getResource("/test-xml-conf")).getPath();
        final ExtensionConfig extensionConfig = ExtensionConfigReader.read(new File(path));
        assertInstanceOf(ExtensionConfigXml.class, extensionConfig);

        final ExtensionConfigXml extensionConfigXml = (ExtensionConfigXml) extensionConfig;

        assertFalse(extensionConfigXml.isVerbose());
        assertTrue(extensionConfigXml.isPayload());
        assertFalse(extensionConfigXml.isJson());
        assertFalse(extensionConfigXml.isPublishReceived());
        assertFalse(extensionConfigXml.isPublishSend());
    }

    @Test
    void defaultPropertiesWhenInvalidConfigFileInConfFolder() {
        final String path = Objects.requireNonNull(getClass().getResource("/test-invalid-xml-conf")).getPath();
        final ExtensionConfig extensionConfig = ExtensionConfigReader.read(new File(path));
        assertInstanceOf(ExtensionConfigXml.class, extensionConfig);

        final ExtensionConfigXml extensionConfigXml = (ExtensionConfigXml) extensionConfig;

        assertTrue(extensionConfigXml.isClientConnect());
        assertTrue(extensionConfigXml.isClientDisconnect());
        assertTrue(extensionConfigXml.isConnackSend());
        assertTrue(extensionConfigXml.isPublishReceived());
        assertTrue(extensionConfigXml.isPublishSend());
        assertTrue(extensionConfigXml.isSubscribeReceived());
        assertTrue(extensionConfigXml.isSubackSend());
        assertTrue(extensionConfigXml.isUnsubscribeReceived());
        assertTrue(extensionConfigXml.isUnsubackSend());
        assertTrue(extensionConfigXml.isPingRequestReceived());
        assertTrue(extensionConfigXml.isPingResponseSend());
        assertTrue(extensionConfigXml.isPubackReceived());
        assertTrue(extensionConfigXml.isPubackSend());
        assertTrue(extensionConfigXml.isPubrelReceived());
        assertTrue(extensionConfigXml.isPubrelSend());
        assertTrue(extensionConfigXml.isPubrecReceived());
        assertTrue(extensionConfigXml.isPubrecSend());
        assertTrue(extensionConfigXml.isPubcompReceived());
        assertTrue(extensionConfigXml.isPubcompSend());
        assertFalse(extensionConfigXml.isVerbose());
        assertTrue(extensionConfigXml.isPayload());
        assertFalse(extensionConfigXml.isJson());
    }

}
