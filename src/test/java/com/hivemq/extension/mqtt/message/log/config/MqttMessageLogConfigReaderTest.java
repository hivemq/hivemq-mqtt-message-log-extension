package com.hivemq.extension.mqtt.message.log.config;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.io.File;
import java.util.Properties;

import static com.hivemq.extension.mqtt.message.log.config.MqttMessageLogConfig.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
public class MqttMessageLogConfigReaderTest {

    private final int totalAvailableFlags = 20;

    private final Matcher<Iterable<? extends @NotNull String>> matcher = containsInAnyOrder(CLIENT_CONNECT, CONNACK_SEND,
            CLIENT_DISCONNECT,
            PUBLISH_RECEIVED, PUBLISH_SEND,
            SUBSCRIBE_RECEIVED, SUBACK_SEND,
            UNSUBSCRIBE_RECEIVED, UNSUBACK_SEND,
            PING_REQ_RECEIVED, PING_RESP_SEND,
            PUBACK_RECEIVED, PUBACK_SEND,
            PUBREC_RECEIVED, PUBREC_SEND,
            PUBREL_RECEIVED, PUBREL_SEND,
            PUBCOMP_RECEIVED, PUBCOMP_SEND,
            VERBOSE);

    @Test
    public void defaultPropertiesWhenNoPropertyFileInConfigFolder() {
        final Properties properties = getProperties("src/test/resources/empty-conf");

        assertThat(properties.size(), is(totalAvailableFlags));
        assertThat(properties.stringPropertyNames(), matcher);
    }

    @Test
    public void nonEmptyPropertiesWhenPropertyFileInConfigFolder() {
        final Properties properties = getProperties("src/test/resources/test-conf");

        assertThat(properties.size(), is(totalAvailableFlags));
        assertThat(properties.stringPropertyNames(), matcher);
    }

    private Properties getProperties(final String confPath) {
        final MqttMessageLogConfigReader mqttMessageLogConfigReader = new MqttMessageLogConfigReader(new File(confPath));
        return mqttMessageLogConfigReader.readProperties();
    }

}