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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Florian Limpöck
 * @since 4.2.0
 */
class MqttMessageLogConfigTest {

    private static final @NotNull String FALSE = "false";

    private @NotNull MqttMessageLogConfig emptyConfig;
    private @NotNull MqttMessageLogConfig allFalseConfig;
    private @NotNull MqttMessageLogConfig allTrueConfig;
    private @NotNull MqttMessageLogConfig mixedConfig;

    @BeforeEach
    void setup() {
        final Properties properties = new Properties();
        emptyConfig = new MqttMessageLogConfig(properties);

        final Properties allFalseProperties = new Properties();
        allFalseProperties.setProperty(MqttMessageLogConfig.CLIENT_CONNECT, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.CLIENT_DISCONNECT, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PUBLISH_RECEIVED, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PUBLISH_SEND, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.SUBSCRIBE_RECEIVED, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.SUBACK_SEND, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.UNSUBSCRIBE_RECEIVED, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.UNSUBACK_SEND, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PING_REQ_RECEIVED, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PING_RESP_SEND, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PUBACK_RECEIVED, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PUBACK_SEND, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PUBREC_RECEIVED, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PUBREC_SEND, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PUBREL_RECEIVED, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PUBREL_SEND, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PUBCOMP_RECEIVED, FALSE);
        allFalseProperties.setProperty(MqttMessageLogConfig.PUBCOMP_SEND, FALSE);
        allFalseConfig = new MqttMessageLogConfig(allFalseProperties);

        final Properties allTrueProperties = new Properties();
        allTrueProperties.setProperty(MqttMessageLogConfig.CLIENT_CONNECT, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.CLIENT_DISCONNECT, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PUBLISH_RECEIVED, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PUBLISH_SEND, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.SUBSCRIBE_RECEIVED, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.SUBACK_SEND, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.UNSUBSCRIBE_RECEIVED, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.UNSUBACK_SEND, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PING_REQ_RECEIVED, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PING_RESP_SEND, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PUBACK_RECEIVED, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PUBACK_SEND, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PUBREC_RECEIVED, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PUBREC_SEND, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PUBREL_RECEIVED, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PUBREL_SEND, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PUBCOMP_RECEIVED, MqttMessageLogConfig.TRUE);
        allTrueProperties.setProperty(MqttMessageLogConfig.PUBCOMP_SEND, MqttMessageLogConfig.TRUE);
        allTrueConfig = new MqttMessageLogConfig(allTrueProperties);

        final Properties mixedProperties = new Properties();
        mixedProperties.setProperty(MqttMessageLogConfig.CLIENT_CONNECT, MqttMessageLogConfig.TRUE);
        mixedProperties.setProperty(MqttMessageLogConfig.CLIENT_DISCONNECT, FALSE);
        mixedProperties.setProperty(MqttMessageLogConfig.PUBLISH_RECEIVED, FALSE);
        mixedProperties.setProperty(MqttMessageLogConfig.SUBSCRIBE_RECEIVED, MqttMessageLogConfig.TRUE);
        mixedProperties.setProperty(MqttMessageLogConfig.SUBACK_SEND, FALSE);
        mixedProperties.setProperty(MqttMessageLogConfig.UNSUBSCRIBE_RECEIVED, FALSE);
        mixedProperties.setProperty(MqttMessageLogConfig.UNSUBACK_SEND, MqttMessageLogConfig.TRUE);
        mixedProperties.setProperty(MqttMessageLogConfig.PING_REQ_RECEIVED, FALSE);
        mixedProperties.setProperty(MqttMessageLogConfig.PING_RESP_SEND, FALSE);
        mixedProperties.setProperty(MqttMessageLogConfig.PUBACK_RECEIVED, FALSE);
        mixedProperties.setProperty(MqttMessageLogConfig.PUBACK_SEND, MqttMessageLogConfig.TRUE);
        mixedProperties.setProperty(MqttMessageLogConfig.PUBREC_RECEIVED, FALSE);
        mixedProperties.setProperty(MqttMessageLogConfig.PUBREC_SEND, FALSE);
        mixedProperties.setProperty(MqttMessageLogConfig.PUBREL_RECEIVED, MqttMessageLogConfig.TRUE);
        mixedProperties.setProperty(MqttMessageLogConfig.PUBREL_SEND, MqttMessageLogConfig.TRUE);
        mixedProperties.setProperty(MqttMessageLogConfig.PUBCOMP_RECEIVED, FALSE);
        mixedProperties.setProperty(MqttMessageLogConfig.PUBCOMP_SEND, FALSE);
        mixedConfig = new MqttMessageLogConfig(mixedProperties);
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
    void isPingreqReceived() {
        assertTrue(emptyConfig.isPingreqReceived());
        assertFalse(allFalseConfig.isPingreqReceived());
        assertTrue(allTrueConfig.isPingreqReceived());
        assertFalse(mixedConfig.isPingreqReceived());
    }

    @Test
    void isPingrespSend() {
        assertTrue(emptyConfig.isPingrespSend());
        assertFalse(allFalseConfig.isPingrespSend());
        assertTrue(allTrueConfig.isPingrespSend());
        assertFalse(mixedConfig.isPingrespSend());
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
