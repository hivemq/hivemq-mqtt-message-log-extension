/*
 * Copyright 2019 dc-square GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.hivemq.extension.mqtt.message.log.config;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import static com.hivemq.extension.mqtt.message.log.config.MqttMessageLogConfig.*;

public class MqttMessageLogConfigReader {

    @NotNull
    private static final Logger log = LoggerFactory.getLogger(MqttMessageLogConfigReader.class);

    @NotNull
    private static final String PROPERTIES_FILE_NAME = "mqttMessageLog.properties";

    @NotNull
    private final Properties properties;

    @NotNull
    private final File extensionHomeFolder;

    public MqttMessageLogConfigReader(final @NotNull File extensionHomeFolder) {
        this.extensionHomeFolder = extensionHomeFolder;
        this.properties = new Properties();
        setDefaults();
    }

    private void setDefaults() {
        properties.setProperty(CLIENT_CONNECT, TRUE);
        properties.setProperty(CONNACK_SEND, TRUE);

        properties.setProperty(CLIENT_DISCONNECT, TRUE);

        properties.setProperty(PUBLISH_RECEIVED, TRUE);
        properties.setProperty(PUBLISH_SEND, TRUE);

        properties.setProperty(SUBSCRIBE_RECEIVED, TRUE);
        properties.setProperty(SUBACK_SEND, TRUE);

        properties.setProperty(UNSUBSCRIBE_RECEIVED, TRUE);
        properties.setProperty(UNSUBACK_SEND, TRUE);

        properties.setProperty(PING_REQ_RECEIVED, TRUE);
        properties.setProperty(PING_RESP_SEND, TRUE);

        properties.setProperty(PUBACK_RECEIVED, TRUE);
        properties.setProperty(PUBACK_SEND, TRUE);

        properties.setProperty(PUBREC_RECEIVED, TRUE);
        properties.setProperty(PUBREC_SEND, TRUE);

        properties.setProperty(PUBREL_RECEIVED, TRUE);
        properties.setProperty(PUBREL_SEND, TRUE);

        properties.setProperty(PUBCOMP_RECEIVED, TRUE);
        properties.setProperty(PUBCOMP_SEND, TRUE);

        properties.setProperty(VERBOSE, FALSE);
    }

    @NotNull
    public Properties readProperties() {
        final File propertiesFile = new File(extensionHomeFolder, PROPERTIES_FILE_NAME);

        log.debug("HiveMQ MQTT Message Log Extension: Will try to read config properties from {}", PROPERTIES_FILE_NAME);

        if (!propertiesFile.canRead()) {
            log.info("HiveMQ MQTT Message Log Extension: Cannot read properties file {}", propertiesFile.getAbsolutePath());
        } else {
            try (final InputStream is = new FileInputStream(propertiesFile)) {
                properties.load(is);
            } catch (final Exception e) {
                log.warn("HiveMQ MQTT Message Log Extension: Could not load properties file, reason {}", e.getMessage());
            }
        }
        log.info("HiveMQ MQTT Message Log Extension: Properties initialized to: {}", properties);
        return properties;
    }
}
