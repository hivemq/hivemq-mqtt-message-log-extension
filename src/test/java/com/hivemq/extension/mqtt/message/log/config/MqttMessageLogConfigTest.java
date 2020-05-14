package com.hivemq.extension.mqtt.message.log.config;

import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

import static com.hivemq.extension.mqtt.message.log.config.MqttMessageLogConfig.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
        allFalseProperties.setProperty(SUBACK_SEND, FALSE);
        allFalseProperties.setProperty(UNSUBSCRIBE_RECEIVED, FALSE);
        allFalseProperties.setProperty(UNSUBACK_SEND, FALSE);
        allFalseProperties.setProperty(PING_REQ_RECEIVED, FALSE);
        allFalseProperties.setProperty(PING_RESP_SEND, FALSE);
        allFalseProperties.setProperty(PUBACK_RECEIVED, FALSE);
        allFalseProperties.setProperty(PUBACK_SEND, FALSE);
        allFalseProperties.setProperty(PUBREC_RECEIVED, FALSE);
        allFalseProperties.setProperty(PUBREC_SEND, FALSE);
        allFalseProperties.setProperty(PUBREL_RECEIVED, FALSE);
        allFalseProperties.setProperty(PUBREL_SEND, FALSE);
        allFalseProperties.setProperty(PUBCOMP_RECEIVED, FALSE);
        allFalseProperties.setProperty(PUBCOMP_SEND, FALSE);
        allFalseConfig = new MqttMessageLogConfig(allFalseProperties);

        final Properties allTrueProperties = new Properties();
        allTrueProperties.setProperty(CLIENT_CONNECT, TRUE);
        allTrueProperties.setProperty(CLIENT_DISCONNECT, TRUE);
        allTrueProperties.setProperty(PUBLISH_RECEIVED, TRUE);
        allTrueProperties.setProperty(PUBLISH_SEND, TRUE);
        allTrueProperties.setProperty(SUBSCRIBE_RECEIVED, TRUE);
        allTrueProperties.setProperty(SUBACK_SEND, TRUE);
        allTrueProperties.setProperty(UNSUBSCRIBE_RECEIVED, TRUE);
        allTrueProperties.setProperty(UNSUBACK_SEND, TRUE);
        allTrueProperties.setProperty(PING_REQ_RECEIVED, TRUE);
        allTrueProperties.setProperty(PING_RESP_SEND, TRUE);
        allTrueProperties.setProperty(PUBACK_RECEIVED, TRUE);
        allTrueProperties.setProperty(PUBACK_SEND, TRUE);
        allTrueProperties.setProperty(PUBREC_RECEIVED, TRUE);
        allTrueProperties.setProperty(PUBREC_SEND, TRUE);
        allTrueProperties.setProperty(PUBREL_RECEIVED, TRUE);
        allTrueProperties.setProperty(PUBREL_SEND, TRUE);
        allTrueProperties.setProperty(PUBCOMP_RECEIVED, TRUE);
        allTrueProperties.setProperty(PUBCOMP_SEND, TRUE);
        allTrueConfig = new MqttMessageLogConfig(allTrueProperties);

        final Properties mixedProperties = new Properties();
        mixedProperties.setProperty(CLIENT_CONNECT, TRUE);
        mixedProperties.setProperty(CLIENT_DISCONNECT, FALSE);
        mixedProperties.setProperty(PUBLISH_RECEIVED, FALSE);
        mixedProperties.setProperty(SUBSCRIBE_RECEIVED, TRUE);
        mixedProperties.setProperty(SUBACK_SEND, FALSE);
        mixedProperties.setProperty(UNSUBSCRIBE_RECEIVED, FALSE);
        mixedProperties.setProperty(UNSUBACK_SEND, TRUE);
        mixedProperties.setProperty(PING_REQ_RECEIVED, FALSE);
        mixedProperties.setProperty(PING_RESP_SEND, FALSE);
        mixedProperties.setProperty(PUBACK_RECEIVED, FALSE);
        mixedProperties.setProperty(PUBACK_SEND, TRUE);
        mixedProperties.setProperty(PUBREC_RECEIVED, FALSE);
        mixedProperties.setProperty(PUBREC_SEND, FALSE);
        mixedProperties.setProperty(PUBREL_RECEIVED, TRUE);
        mixedProperties.setProperty(PUBREL_SEND, TRUE);
        mixedProperties.setProperty(PUBCOMP_RECEIVED, FALSE);
        mixedProperties.setProperty(PUBCOMP_SEND, FALSE);
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

    @Test
    public void isSubackSend() {
        assertTrue(emptyConfig.isSubackSend());
        assertFalse(allFalseConfig.isSubackSend());
        assertTrue(allTrueConfig.isSubackSend());
        assertFalse(mixedConfig.isSubackSend());
    }

    @Test
    public void isUnsubscribeReceived() {
        assertTrue(emptyConfig.isUnsubscribeReceived());
        assertFalse(allFalseConfig.isUnsubscribeReceived());
        assertTrue(allTrueConfig.isUnsubscribeReceived());
        assertFalse(mixedConfig.isUnsubscribeReceived());
    }

    @Test
    public void isUnsubackSend() {
        assertTrue(emptyConfig.isUnsubackSend());
        assertFalse(allFalseConfig.isUnsubackSend());
        assertTrue(allTrueConfig.isUnsubackSend());
        assertTrue(mixedConfig.isUnsubackSend());
    }

    @Test
    public void isPingreqReceived() {
        assertTrue(emptyConfig.isPingreqReceived());
        assertFalse(allFalseConfig.isPingreqReceived());
        assertTrue(allTrueConfig.isPingreqReceived());
        assertFalse(mixedConfig.isPingreqReceived());
    }

    @Test
    public void isPingrespSend() {
        assertTrue(emptyConfig.isPingrespSend());
        assertFalse(allFalseConfig.isPingrespSend());
        assertTrue(allTrueConfig.isPingrespSend());
        assertFalse(mixedConfig.isPingrespSend());
    }

    @Test
    public void isPubackReceived() {
        assertTrue(emptyConfig.isPubackReceived());
        assertFalse(allFalseConfig.isPubackReceived());
        assertTrue(allTrueConfig.isPubackReceived());
        assertFalse(mixedConfig.isPubackReceived());
    }

    @Test
    public void isPubackSend() {
        assertTrue(emptyConfig.isPubackSend());
        assertFalse(allFalseConfig.isPubackSend());
        assertTrue(allTrueConfig.isPubackSend());
        assertTrue(mixedConfig.isPubackSend());
    }

    @Test
    public void isPubrecReceived() {
        assertTrue(emptyConfig.isPubrecReceived());
        assertFalse(allFalseConfig.isPubrecReceived());
        assertTrue(allTrueConfig.isPubrecReceived());
        assertFalse(mixedConfig.isPubrecReceived());
    }

    @Test
    public void isPubrecSend() {
        assertTrue(emptyConfig.isPubrecSend());
        assertFalse(allFalseConfig.isPubrecSend());
        assertTrue(allTrueConfig.isPubrecSend());
        assertFalse(mixedConfig.isPubrecSend());
    }

    @Test
    public void isPubrelReceived() {
        assertTrue(emptyConfig.isPubrelReceived());
        assertFalse(allFalseConfig.isPubrelReceived());
        assertTrue(allTrueConfig.isPubrelReceived());
        assertTrue(mixedConfig.isPubrelReceived());
    }

    @Test
    public void isPubrelSend() {
        assertTrue(emptyConfig.isPubrelSend());
        assertFalse(allFalseConfig.isPubrelSend());
        assertTrue(allTrueConfig.isPubrelSend());
        assertTrue(mixedConfig.isPubrelSend());
    }

    @Test
    public void isPubcompReceived() {
        assertTrue(emptyConfig.isPubcompReceived());
        assertFalse(allFalseConfig.isPubcompReceived());
        assertTrue(allTrueConfig.isPubcompReceived());
        assertFalse(mixedConfig.isPubcompReceived());
    }

    @Test
    public void isPubcompSend() {
        assertTrue(emptyConfig.isPubcompSend());
        assertFalse(allFalseConfig.isPubcompSend());
        assertTrue(allTrueConfig.isPubcompSend());
        assertFalse(mixedConfig.isPubcompSend());
    }
}