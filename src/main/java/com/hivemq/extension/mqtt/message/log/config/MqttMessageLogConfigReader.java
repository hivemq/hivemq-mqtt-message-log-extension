/*
 * Copyright 2019 HiveMQ GmbH
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
import com.hivemq.extension.sdk.api.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

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
        properties.setProperty(MqttMessageLogConfig.CLIENT_CONNECT, MqttMessageLogConfig.TRUE);
        properties.setProperty(MqttMessageLogConfig.CLIENT_DISCONNECT, MqttMessageLogConfig.TRUE);
        properties.setProperty(MqttMessageLogConfig.PUBLISH_RECEIVED, MqttMessageLogConfig.TRUE);
        properties.setProperty(MqttMessageLogConfig.PUBLISH_SEND, MqttMessageLogConfig.TRUE);
        properties.setProperty(MqttMessageLogConfig.SUBSCRIBE_RECEIVED, MqttMessageLogConfig.TRUE);
    }

    @NotNull
    public Properties readProperties() {
        final File pluginFile = new File(extensionHomeFolder, PROPERTIES_FILE_NAME);

        log.debug("HiveMQ MQTT Message Log Extension: Will try to read config properties from {}", PROPERTIES_FILE_NAME);

        if (!pluginFile.canRead()) {
            log.info("HiveMQ MQTT Message Log Extension: No properties file {} available", pluginFile.getAbsolutePath());
            return properties;
        }

        try (final InputStream is = new FileInputStream(pluginFile)) {
            properties.load(is);
        } catch (final Exception e) {
            log.warn("HiveMQ MQTT Message Log Extension: Could not load properties file, reason {}", e.getMessage());
        }
        log.info("HiveMQ MQTT Message Log Extension: Properties initialized  to: {}", properties);
        return properties;
    }
}
