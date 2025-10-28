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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

/**
 * @since 1.0.0
 */
class ExtensionConfigReaderTest {

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
            ExtensionConfigProperties.REDACT_PASSWORD);

    @Test
    void defaultPropertiesWhenNoPropertyFileInConfigFolder(@TempDir final @NotNull Path tempDir) {
        final var extensionConfig = ExtensionConfigReader.read(tempDir.toFile());
        assertInstanceOf(ExtensionConfigProperties.class, extensionConfig);

        final var extensionConfigProperties = ((ExtensionConfigProperties) extensionConfig);
        final var properties = extensionConfigProperties.getProperties();

        assertThat(properties).hasSize(defaultProperties.size());
        assertThat(properties.stringPropertyNames()).containsAll(defaultProperties);
        assertThat(defaultProperties).containsAll(properties.stringPropertyNames());

        assertThat(extensionConfigProperties.isClientConnect()).isTrue();
        assertThat(extensionConfigProperties.isClientDisconnect()).isTrue();
        assertThat(extensionConfigProperties.isConnackSend()).isTrue();
        assertThat(extensionConfigProperties.isPublishReceived()).isTrue();
        assertThat(extensionConfigProperties.isPublishSend()).isTrue();
        assertThat(extensionConfigProperties.isSubscribeReceived()).isTrue();
        assertThat(extensionConfigProperties.isSubackSend()).isTrue();
        assertThat(extensionConfigProperties.isUnsubscribeReceived()).isTrue();
        assertThat(extensionConfigProperties.isUnsubackSend()).isTrue();
        assertThat(extensionConfigProperties.isPingRequestReceived()).isTrue();
        assertThat(extensionConfigProperties.isPingResponseSend()).isTrue();
        assertThat(extensionConfigProperties.isPubackReceived()).isTrue();
        assertThat(extensionConfigProperties.isPubackSend()).isTrue();
        assertThat(extensionConfigProperties.isPubrelReceived()).isTrue();
        assertThat(extensionConfigProperties.isPubrelSend()).isTrue();
        assertThat(extensionConfigProperties.isPubrecReceived()).isTrue();
        assertThat(extensionConfigProperties.isPubrecSend()).isTrue();
        assertThat(extensionConfigProperties.isPubcompReceived()).isTrue();
        assertThat(extensionConfigProperties.isPubcompSend()).isTrue();
        assertThat(extensionConfigProperties.isVerbose()).isFalse();
        assertThat(extensionConfigProperties.isPayload()).isTrue();
        assertThat(extensionConfigProperties.isRedactPassword()).isFalse();
    }

    @Test
    void nonEmptyPropertiesWhenPropertyFileInConfigFolder() {
        final var path = Objects.requireNonNull(getClass().getResource("/test-conf")).getPath();
        final var extensionConfig = ExtensionConfigReader.read(new File(path));
        assertInstanceOf(ExtensionConfigProperties.class, extensionConfig);

        final var extensionConfigProperties = ((ExtensionConfigProperties) extensionConfig);
        final var properties = extensionConfigProperties.getProperties();

        assertThat(properties).hasSize(defaultProperties.size());
        assertThat(properties.stringPropertyNames()).containsAll(defaultProperties);
        assertThat(defaultProperties).containsAll(properties.stringPropertyNames());

        assertThat(extensionConfigProperties.isVerbose()).isFalse();
        assertThat(extensionConfigProperties.isPayload()).isTrue();
        assertThat(extensionConfigProperties.isPublishReceived()).isFalse();
        assertThat(extensionConfigProperties.isPublishSend()).isFalse();
    }

    @Test
    void nonEmptyPropertiesWhenConfigFileInConfFolder() {
        final var path = Objects.requireNonNull(getClass().getResource("/test-xml-conf")).getPath();
        final var extensionConfig = ExtensionConfigReader.read(new File(path));
        assertInstanceOf(ExtensionConfigXml.class, extensionConfig);

        final var extensionConfigXml = (ExtensionConfigXml) extensionConfig;
        assertThat(extensionConfigXml.isVerbose()).isFalse();
        assertThat(extensionConfigXml.isPayload()).isTrue();
        assertThat(extensionConfigXml.isPublishReceived()).isFalse();
        assertThat(extensionConfigXml.isPublishSend()).isFalse();
    }

    @Test
    void defaultPropertiesWhenInvalidConfigFileInConfFolder() {
        final var path = Objects.requireNonNull(getClass().getResource("/test-invalid-xml-conf")).getPath();
        final var extensionConfig = ExtensionConfigReader.read(new File(path));
        assertInstanceOf(ExtensionConfigXml.class, extensionConfig);

        final var extensionConfigXml = (ExtensionConfigXml) extensionConfig;
        assertThat(extensionConfigXml.isClientConnect()).isTrue();
        assertThat(extensionConfigXml.isClientDisconnect()).isTrue();
        assertThat(extensionConfigXml.isConnackSend()).isTrue();
        assertThat(extensionConfigXml.isPublishReceived()).isTrue();
        assertThat(extensionConfigXml.isPublishSend()).isTrue();
        assertThat(extensionConfigXml.isSubscribeReceived()).isTrue();
        assertThat(extensionConfigXml.isSubackSend()).isTrue();
        assertThat(extensionConfigXml.isUnsubscribeReceived()).isTrue();
        assertThat(extensionConfigXml.isUnsubackSend()).isTrue();
        assertThat(extensionConfigXml.isPingRequestReceived()).isTrue();
        assertThat(extensionConfigXml.isPingResponseSend()).isTrue();
        assertThat(extensionConfigXml.isPubackReceived()).isTrue();
        assertThat(extensionConfigXml.isPubackSend()).isTrue();
        assertThat(extensionConfigXml.isPubrelReceived()).isTrue();
        assertThat(extensionConfigXml.isPubrelSend()).isTrue();
        assertThat(extensionConfigXml.isPubrecReceived()).isTrue();
        assertThat(extensionConfigXml.isPubrecSend()).isTrue();
        assertThat(extensionConfigXml.isPubcompReceived()).isTrue();
        assertThat(extensionConfigXml.isPubcompSend()).isTrue();
        assertThat(extensionConfigXml.isVerbose()).isFalse();
        assertThat(extensionConfigXml.isPayload()).isTrue();
        assertThat(extensionConfigXml.isRedactPassword()).isFalse();
    }
}
