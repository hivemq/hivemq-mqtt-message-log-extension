package com.hivemq.extension.mqtt.message.log.config;

import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.*;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
public class MqttMessageLogConfigReaderTest {

    @Test
    public void defaultPropertiesWhenNopropertyFileInConfigFolder() {
        final Properties properties = getProperties("src/test/resources/empty-conf");

        assertThat(properties.size(), is(5));
        assertThat(properties.stringPropertyNames(), containsInAnyOrder(MqttMessageLogConfig.CLIENT_CONNECT, MqttMessageLogConfig.CLIENT_DISCONNECT, MqttMessageLogConfig.PUBLISH_RECEIVED, MqttMessageLogConfig.PUBLISH_SEND, MqttMessageLogConfig.SUBSCRIBE_RECEIVED));
    }

    @Test
    public void nonEmptyPropertiesWhenpropertyFileInConfigFolder() {
        final Properties properties = getProperties("src/test/resources/test-conf");

        assertThat(properties.size(), is(5));
        assertThat(properties.stringPropertyNames(), containsInAnyOrder(MqttMessageLogConfig.CLIENT_CONNECT, MqttMessageLogConfig.CLIENT_DISCONNECT, MqttMessageLogConfig.PUBLISH_RECEIVED, MqttMessageLogConfig.PUBLISH_SEND, MqttMessageLogConfig.SUBSCRIBE_RECEIVED));
    }

    private Properties getProperties(final String confPath) {
        final MqttMessageLogConfigReader mqttMessageLogConfigReader = new MqttMessageLogConfigReader(new File(confPath));
        return mqttMessageLogConfigReader.readProperties();
    }

}