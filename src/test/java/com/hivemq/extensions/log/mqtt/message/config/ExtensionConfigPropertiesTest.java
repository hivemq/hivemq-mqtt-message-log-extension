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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @since 4.2.0
 */
class ExtensionConfigPropertiesTest {

    private static final @NotNull String FALSE = "false";

    private @NotNull ExtensionConfigProperties emptyConfig;
    private @NotNull ExtensionConfigProperties allFalseConfig;
    private @NotNull ExtensionConfigProperties allTrueConfig;
    private @NotNull ExtensionConfigProperties mixedConfig;

    @BeforeEach
    void setup() {
        final var properties = new Properties();
        emptyConfig = new ExtensionConfigProperties(properties);

        final var allFalseProperties = new Properties();
        allFalseProperties.setProperty(ExtensionConfigProperties.CLIENT_CONNECT, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.CLIENT_DISCONNECT, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PUBLISH_RECEIVED, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PUBLISH_SEND, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.SUBSCRIBE_RECEIVED, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.SUBACK_SEND, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.UNSUBSCRIBE_RECEIVED, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.UNSUBACK_SEND, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PING_REQUEST_RECEIVED, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PING_RESPONSE_SEND, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PUBACK_RECEIVED, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PUBACK_SEND, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PUBREC_RECEIVED, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PUBREC_SEND, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PUBREL_RECEIVED, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PUBREL_SEND, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PUBCOMP_RECEIVED, FALSE);
        allFalseProperties.setProperty(ExtensionConfigProperties.PUBCOMP_SEND, FALSE);
        allFalseConfig = new ExtensionConfigProperties(allFalseProperties);

        final var allTrueProperties = new Properties();
        allTrueProperties.setProperty(ExtensionConfigProperties.CLIENT_CONNECT, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.CLIENT_DISCONNECT, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PUBLISH_RECEIVED, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PUBLISH_SEND, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.SUBSCRIBE_RECEIVED, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.SUBACK_SEND, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.UNSUBSCRIBE_RECEIVED, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.UNSUBACK_SEND, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PING_REQUEST_RECEIVED, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PING_RESPONSE_SEND, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PUBACK_RECEIVED, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PUBACK_SEND, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PUBREC_RECEIVED, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PUBREC_SEND, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PUBREL_RECEIVED, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PUBREL_SEND, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PUBCOMP_RECEIVED, ExtensionConfigProperties.TRUE);
        allTrueProperties.setProperty(ExtensionConfigProperties.PUBCOMP_SEND, ExtensionConfigProperties.TRUE);
        allTrueConfig = new ExtensionConfigProperties(allTrueProperties);

        final var mixedProperties = new Properties();
        mixedProperties.setProperty(ExtensionConfigProperties.CLIENT_CONNECT, ExtensionConfigProperties.TRUE);
        mixedProperties.setProperty(ExtensionConfigProperties.CLIENT_DISCONNECT, FALSE);
        mixedProperties.setProperty(ExtensionConfigProperties.PUBLISH_RECEIVED, FALSE);
        mixedProperties.setProperty(ExtensionConfigProperties.SUBSCRIBE_RECEIVED, ExtensionConfigProperties.TRUE);
        mixedProperties.setProperty(ExtensionConfigProperties.SUBACK_SEND, FALSE);
        mixedProperties.setProperty(ExtensionConfigProperties.UNSUBSCRIBE_RECEIVED, FALSE);
        mixedProperties.setProperty(ExtensionConfigProperties.UNSUBACK_SEND, ExtensionConfigProperties.TRUE);
        mixedProperties.setProperty(ExtensionConfigProperties.PING_REQUEST_RECEIVED, FALSE);
        mixedProperties.setProperty(ExtensionConfigProperties.PING_RESPONSE_SEND, FALSE);
        mixedProperties.setProperty(ExtensionConfigProperties.PUBACK_RECEIVED, FALSE);
        mixedProperties.setProperty(ExtensionConfigProperties.PUBACK_SEND, ExtensionConfigProperties.TRUE);
        mixedProperties.setProperty(ExtensionConfigProperties.PUBREC_RECEIVED, FALSE);
        mixedProperties.setProperty(ExtensionConfigProperties.PUBREC_SEND, FALSE);
        mixedProperties.setProperty(ExtensionConfigProperties.PUBREL_RECEIVED, ExtensionConfigProperties.TRUE);
        mixedProperties.setProperty(ExtensionConfigProperties.PUBREL_SEND, ExtensionConfigProperties.TRUE);
        mixedProperties.setProperty(ExtensionConfigProperties.PUBCOMP_RECEIVED, FALSE);
        mixedProperties.setProperty(ExtensionConfigProperties.PUBCOMP_SEND, FALSE);
        mixedConfig = new ExtensionConfigProperties(mixedProperties);
    }

    @Test
    void isClientConnect() {
        assertThat(emptyConfig.isClientConnect()).isTrue();
        assertThat(allFalseConfig.isClientConnect()).isFalse();
        assertThat(allTrueConfig.isClientConnect()).isTrue();
        assertThat(mixedConfig.isClientConnect()).isTrue();
    }

    @Test
    void isClientDisconnect() {
        assertThat(emptyConfig.isClientDisconnect()).isTrue();
        assertThat(allFalseConfig.isClientDisconnect()).isFalse();
        assertThat(allTrueConfig.isClientDisconnect()).isTrue();
        assertThat(mixedConfig.isClientDisconnect()).isFalse();
    }

    @Test
    void isPublishReceived() {
        assertThat(emptyConfig.isPublishReceived()).isTrue();
        assertThat(allFalseConfig.isPublishReceived()).isFalse();
        assertThat(allTrueConfig.isPublishReceived()).isTrue();
        assertThat(mixedConfig.isPublishReceived()).isFalse();
    }

    @Test
    void isPublishSend() {
        assertThat(emptyConfig.isPublishSend()).isTrue();
        assertThat(allFalseConfig.isPublishSend()).isFalse();
        assertThat(allTrueConfig.isPublishSend()).isTrue();
        assertThat(mixedConfig.isPublishSend()).isTrue();
    }

    @Test
    void isSubscribeReceived() {
        assertThat(emptyConfig.isSubscribeReceived()).isTrue();
        assertThat(allFalseConfig.isSubscribeReceived()).isFalse();
        assertThat(allTrueConfig.isSubscribeReceived()).isTrue();
        assertThat(mixedConfig.isSubscribeReceived()).isTrue();
    }

    @Test
    void isSubackSend() {
        assertThat(emptyConfig.isSubackSend()).isTrue();
        assertThat(allFalseConfig.isSubackSend()).isFalse();
        assertThat(allTrueConfig.isSubackSend()).isTrue();
        assertThat(mixedConfig.isSubackSend()).isFalse();
    }

    @Test
    void isUnsubscribeReceived() {
        assertThat(emptyConfig.isUnsubscribeReceived()).isTrue();
        assertThat(allFalseConfig.isUnsubscribeReceived()).isFalse();
        assertThat(allTrueConfig.isUnsubscribeReceived()).isTrue();
        assertThat(mixedConfig.isUnsubscribeReceived()).isFalse();
    }

    @Test
    void isUnsubackSend() {
        assertThat(emptyConfig.isUnsubackSend()).isTrue();
        assertThat(allFalseConfig.isUnsubackSend()).isFalse();
        assertThat(allTrueConfig.isUnsubackSend()).isTrue();
        assertThat(mixedConfig.isUnsubackSend()).isTrue();
    }

    @Test
    void isPingRequestReceived() {
        assertThat(emptyConfig.isPingRequestReceived()).isTrue();
        assertThat(allFalseConfig.isPingRequestReceived()).isFalse();
        assertThat(allTrueConfig.isPingRequestReceived()).isTrue();
        assertThat(mixedConfig.isPingRequestReceived()).isFalse();
    }

    @Test
    void isPingResponseSend() {
        assertThat(emptyConfig.isPingResponseSend()).isTrue();
        assertThat(allFalseConfig.isPingResponseSend()).isFalse();
        assertThat(allTrueConfig.isPingResponseSend()).isTrue();
        assertThat(mixedConfig.isPingResponseSend()).isFalse();
    }

    @Test
    void isPubackReceived() {
        assertThat(emptyConfig.isPubackReceived()).isTrue();
        assertThat(allFalseConfig.isPubackReceived()).isFalse();
        assertThat(allTrueConfig.isPubackReceived()).isTrue();
        assertThat(mixedConfig.isPubackReceived()).isFalse();
    }

    @Test
    void isPubackSend() {
        assertThat(emptyConfig.isPubackSend()).isTrue();
        assertThat(allFalseConfig.isPubackSend()).isFalse();
        assertThat(allTrueConfig.isPubackSend()).isTrue();
        assertThat(mixedConfig.isPubackSend()).isTrue();
    }

    @Test
    void isPubrecReceived() {
        assertThat(emptyConfig.isPubrecReceived()).isTrue();
        assertThat(allFalseConfig.isPubrecReceived()).isFalse();
        assertThat(allTrueConfig.isPubrecReceived()).isTrue();
        assertThat(mixedConfig.isPubrecReceived()).isFalse();
    }

    @Test
    void isPubrecSend() {
        assertThat(emptyConfig.isPubrecSend()).isTrue();
        assertThat(allFalseConfig.isPubrecSend()).isFalse();
        assertThat(allTrueConfig.isPubrecSend()).isTrue();
        assertThat(mixedConfig.isPubrecSend()).isFalse();
    }

    @Test
    void isPubrelReceived() {
        assertThat(emptyConfig.isPubrelReceived()).isTrue();
        assertThat(allFalseConfig.isPubrelReceived()).isFalse();
        assertThat(allTrueConfig.isPubrelReceived()).isTrue();
        assertThat(mixedConfig.isPubrelReceived()).isTrue();
    }

    @Test
    void isPubrelSend() {
        assertThat(emptyConfig.isPubrelSend()).isTrue();
        assertThat(allFalseConfig.isPubrelSend()).isFalse();
        assertThat(allTrueConfig.isPubrelSend()).isTrue();
        assertThat(mixedConfig.isPubrelSend()).isTrue();
    }

    @Test
    void isPubcompReceived() {
        assertThat(emptyConfig.isPubcompReceived()).isTrue();
        assertThat(allFalseConfig.isPubcompReceived()).isFalse();
        assertThat(allTrueConfig.isPubcompReceived()).isTrue();
        assertThat(mixedConfig.isPubcompReceived()).isFalse();
    }

    @Test
    void isPubcompSend() {
        assertThat(emptyConfig.isPubcompSend()).isTrue();
        assertThat(allFalseConfig.isPubcompSend()).isFalse();
        assertThat(allTrueConfig.isPubcompSend()).isTrue();
        assertThat(mixedConfig.isPubcompSend()).isFalse();
    }
}
