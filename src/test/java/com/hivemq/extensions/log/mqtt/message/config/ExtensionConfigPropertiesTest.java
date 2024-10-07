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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        final Properties properties = new Properties();
        emptyConfig = new ExtensionConfigProperties(properties);

        final Properties allFalseProperties = new Properties();
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

        final Properties allTrueProperties = new Properties();
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

        final Properties mixedProperties = new Properties();
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
        assertTrue(emptyConfig.isClientConnect());
        assertFalse(allFalseConfig.isClientConnect());
        assertTrue(allTrueConfig.isClientConnect());
        assertTrue(mixedConfig.isClientConnect());
    }

    @Test
    void isClientDisconnect() {
        assertTrue(emptyConfig.isClientDisconnect());
        assertFalse(allFalseConfig.isClientDisconnect());
        assertTrue(allTrueConfig.isClientDisconnect());
        assertFalse(mixedConfig.isClientDisconnect());
    }

    @Test
    void isPublishReceived() {
        assertTrue(emptyConfig.isPublishReceived());
        assertFalse(allFalseConfig.isPublishReceived());
        assertTrue(allTrueConfig.isPublishReceived());
        assertFalse(mixedConfig.isPublishReceived());
    }

    @Test
    void isPublishSend() {
        assertTrue(emptyConfig.isPublishSend());
        assertFalse(allFalseConfig.isPublishSend());
        assertTrue(allTrueConfig.isPublishSend());
        assertTrue(mixedConfig.isPublishSend());
    }

    @Test
    void isSubscribeReceived() {
        assertTrue(emptyConfig.isSubscribeReceived());
        assertFalse(allFalseConfig.isSubscribeReceived());
        assertTrue(allTrueConfig.isSubscribeReceived());
        assertTrue(mixedConfig.isSubscribeReceived());
    }

    @Test
    void isSubackSend() {
        assertTrue(emptyConfig.isSubackSend());
        assertFalse(allFalseConfig.isSubackSend());
        assertTrue(allTrueConfig.isSubackSend());
        assertFalse(mixedConfig.isSubackSend());
    }

    @Test
    void isUnsubscribeReceived() {
        assertTrue(emptyConfig.isUnsubscribeReceived());
        assertFalse(allFalseConfig.isUnsubscribeReceived());
        assertTrue(allTrueConfig.isUnsubscribeReceived());
        assertFalse(mixedConfig.isUnsubscribeReceived());
    }

    @Test
    void isUnsubackSend() {
        assertTrue(emptyConfig.isUnsubackSend());
        assertFalse(allFalseConfig.isUnsubackSend());
        assertTrue(allTrueConfig.isUnsubackSend());
        assertTrue(mixedConfig.isUnsubackSend());
    }

    @Test
    void isPingRequestReceived() {
        assertTrue(emptyConfig.isPingRequestReceived());
        assertFalse(allFalseConfig.isPingRequestReceived());
        assertTrue(allTrueConfig.isPingRequestReceived());
        assertFalse(mixedConfig.isPingRequestReceived());
    }

    @Test
    void isPingResponseSend() {
        assertTrue(emptyConfig.isPingResponseSend());
        assertFalse(allFalseConfig.isPingResponseSend());
        assertTrue(allTrueConfig.isPingResponseSend());
        assertFalse(mixedConfig.isPingResponseSend());
    }

    @Test
    void isPubackReceived() {
        assertTrue(emptyConfig.isPubackReceived());
        assertFalse(allFalseConfig.isPubackReceived());
        assertTrue(allTrueConfig.isPubackReceived());
        assertFalse(mixedConfig.isPubackReceived());
    }

    @Test
    void isPubackSend() {
        assertTrue(emptyConfig.isPubackSend());
        assertFalse(allFalseConfig.isPubackSend());
        assertTrue(allTrueConfig.isPubackSend());
        assertTrue(mixedConfig.isPubackSend());
    }

    @Test
    void isPubrecReceived() {
        assertTrue(emptyConfig.isPubrecReceived());
        assertFalse(allFalseConfig.isPubrecReceived());
        assertTrue(allTrueConfig.isPubrecReceived());
        assertFalse(mixedConfig.isPubrecReceived());
    }

    @Test
    void isPubrecSend() {
        assertTrue(emptyConfig.isPubrecSend());
        assertFalse(allFalseConfig.isPubrecSend());
        assertTrue(allTrueConfig.isPubrecSend());
        assertFalse(mixedConfig.isPubrecSend());
    }

    @Test
    void isPubrelReceived() {
        assertTrue(emptyConfig.isPubrelReceived());
        assertFalse(allFalseConfig.isPubrelReceived());
        assertTrue(allTrueConfig.isPubrelReceived());
        assertTrue(mixedConfig.isPubrelReceived());
    }

    @Test
    void isPubrelSend() {
        assertTrue(emptyConfig.isPubrelSend());
        assertFalse(allFalseConfig.isPubrelSend());
        assertTrue(allTrueConfig.isPubrelSend());
        assertTrue(mixedConfig.isPubrelSend());
    }

    @Test
    void isPubcompReceived() {
        assertTrue(emptyConfig.isPubcompReceived());
        assertFalse(allFalseConfig.isPubcompReceived());
        assertTrue(allTrueConfig.isPubcompReceived());
        assertFalse(mixedConfig.isPubcompReceived());
    }

    @Test
    void isPubcompSend() {
        assertTrue(emptyConfig.isPubcompSend());
        assertFalse(allFalseConfig.isPubcompSend());
        assertTrue(allTrueConfig.isPubcompSend());
        assertFalse(mixedConfig.isPubcompSend());
    }
}
