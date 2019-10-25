package com.hivemq.extension.mqtt.message.log.config;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static com.hivemq.extension.mqtt.message.log.config.MqttMessageLogConfig.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Florian Limpöck
 * @since 4.2.0
 */
public class MqttMessageLogConfigTest {

    private static final String FALSE = "false";

    MqttMessageLogConfig emptyConfig;
    MqttMessageLogConfig allFalseConfig;
    MqttMessageLogConfig allTrueConfig;
    MqttMessageLogConfig mixedConfig;

    @Before
    public void setup() {
        final Properties properties = new Properties();
        emptyConfig = new MqttMessageLogConfig(properties);

        final Properties allFalseProperties = new Properties();
        allFalseProperties.setProperty(CLIENT_CONNECT, FALSE);
        allFalseProperties.setProperty(CLIENT_DISCONNECT, FALSE);
        allFalseProperties.setProperty(PUBLISH_RECEIVED, FALSE);
        allFalseProperties.setProperty(PUBLISH_SEND, FALSE);
        allFalseProperties.setProperty(SUBSCRIBE_RECEIVED, FALSE);
        allFalseConfig = new MqttMessageLogConfig(allFalseProperties);

        final Properties allTrueProperties = new Properties();
        allTrueProperties.setProperty(CLIENT_CONNECT, TRUE);
        allTrueProperties.setProperty(CLIENT_DISCONNECT, TRUE);
        allTrueProperties.setProperty(PUBLISH_RECEIVED, TRUE);
        allTrueProperties.setProperty(PUBLISH_SEND, TRUE);
        allTrueProperties.setProperty(SUBSCRIBE_RECEIVED, TRUE);
        allTrueConfig = new MqttMessageLogConfig(allTrueProperties);

        final Properties mixedProperties = new Properties();
        mixedProperties.setProperty(CLIENT_CONNECT, TRUE);
        mixedProperties.setProperty(CLIENT_DISCONNECT, FALSE);
        mixedProperties.setProperty(PUBLISH_RECEIVED, FALSE);
        mixedConfig = new MqttMessageLogConfig(mixedProperties);
    }

    @Test
    public void isClientConnect() {
        assertTrue(emptyConfig.isClientConnect());
        assertFalse(allFalseConfig.isClientConnect());
        assertTrue(allTrueConfig.isClientConnect());
        assertTrue(mixedConfig.isClientConnect());
    }

    @Test
    public void isClientDisconnect() {
        assertTrue(emptyConfig.isClientDisconnect());
        assertFalse(allFalseConfig.isClientDisconnect());
        assertTrue(allTrueConfig.isClientDisconnect());
        assertFalse(mixedConfig.isClientDisconnect());
    }

    @Test
    public void isPublishReceived() {
        assertTrue(emptyConfig.isPublishReceived());
        assertFalse(allFalseConfig.isPublishReceived());
        assertTrue(allTrueConfig.isPublishReceived());
        assertFalse(mixedConfig.isPublishReceived());
    }

    @Test
    public void isPublishSend() {
        assertTrue(emptyConfig.isPublishSend());
        assertFalse(allFalseConfig.isPublishSend());
        assertTrue(allTrueConfig.isPublishSend());
        assertTrue(mixedConfig.isPublishSend());
    }

    @Test
    public void isSubscribeReceived() {
        assertTrue(emptyConfig.isSubscribeReceived());
        assertFalse(allFalseConfig.isSubscribeReceived());
        assertTrue(allTrueConfig.isSubscribeReceived());
        assertTrue(mixedConfig.isSubscribeReceived());
    }
}