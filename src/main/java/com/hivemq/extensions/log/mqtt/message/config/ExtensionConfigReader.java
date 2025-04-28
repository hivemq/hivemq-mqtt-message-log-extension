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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Properties;

import static com.hivemq.extensions.log.mqtt.message.ExtensionConstants.EXTENSION_CONFIG_PROPERTIES_LOCATION;
import static com.hivemq.extensions.log.mqtt.message.ExtensionConstants.EXTENSION_CONFIG_XML_LOCATION;
import static com.hivemq.extensions.log.mqtt.message.ExtensionConstants.EXTENSION_NAME;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.CLIENT_CONNECT;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.CLIENT_DISCONNECT;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.CONNACK_SEND;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.FALSE;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.JSON;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PAYLOAD;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PING_REQUEST_RECEIVED;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PING_RESPONSE_SEND;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PUBACK_RECEIVED;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PUBACK_SEND;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PUBCOMP_RECEIVED;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PUBCOMP_SEND;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PUBLISH_RECEIVED;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PUBLISH_SEND;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PUBREC_RECEIVED;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PUBREC_SEND;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PUBREL_RECEIVED;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.PUBREL_SEND;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.SUBACK_SEND;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.SUBSCRIBE_RECEIVED;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.TRUE;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.UNSUBACK_SEND;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.UNSUBSCRIBE_RECEIVED;
import static com.hivemq.extensions.log.mqtt.message.config.ExtensionConfigProperties.VERBOSE;

/**
 * @since 1.2.0
 */
public class ExtensionConfigReader {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(ExtensionConfigReader.class);

    /**
     * Reads config.xml from /conf directory if present, otherwise reads mqttMessageLog.properties from extension home
     * directory. Uses the defaults if neither is present/readable.
     *
     * @param extensionHomeFolder home folder of the extension
     * @return user defined extension configuration or default configuration
     */
    public static @NotNull ExtensionConfig read(final @NotNull File extensionHomeFolder) {
        final File configXmlFile = extensionHomeFolder.toPath().resolve(EXTENSION_CONFIG_XML_LOCATION).toFile();

        if (!configXmlFile.exists()) {
            return readPropertiesFile(extensionHomeFolder);
        }

        return readConfigXmlFile(configXmlFile);
    }

    private static ExtensionConfig readPropertiesFile(final @NotNull File extensionHomeFolder) {
        final Properties properties = setDefaults();
        final File propertiesFile = new File(extensionHomeFolder, EXTENSION_CONFIG_PROPERTIES_LOCATION);

        LOG.debug("{}: Will try to read config properties from {}",
                EXTENSION_NAME,
                EXTENSION_CONFIG_PROPERTIES_LOCATION);

        if (propertiesFile.exists()) {
            final Path extensionHomePath = extensionHomeFolder.toPath();
            final Path extensionLegacyConfigPropertiesPath =
                    extensionHomePath.resolve(EXTENSION_CONFIG_PROPERTIES_LOCATION);
            final Path extensionConfigXmlPath = extensionHomePath.resolve(EXTENSION_CONFIG_XML_LOCATION);
            LOG.warn("{}: The configuration file is using the legacy location and format '{}'. " +
                            "Please update the configuration file to the new location and format '{}'. " +
                            "Support for the legacy location and format will be removed in a future release.",
                    EXTENSION_NAME,
                    extensionLegacyConfigPropertiesPath,
                    extensionConfigXmlPath);
        }

        if (!propertiesFile.canRead()) {
            LOG.info("{}: Cannot read properties file {}", EXTENSION_NAME, propertiesFile.getAbsolutePath());
        } else {
            try (final InputStream is = new FileInputStream(propertiesFile)) {
                properties.load(is);
            } catch (final Exception e) {
                LOG.warn("{}: Could not load properties file, reason {}", EXTENSION_NAME, e.getMessage());
            }
        }

        LOG.info("{}: Properties initialized to: {}", EXTENSION_NAME, properties);
        return new ExtensionConfigProperties(properties);
    }

    private static @NotNull ExtensionConfigXml readConfigXmlFile(final @NotNull File configXmlFile) {
        ExtensionConfigXml extensionConfigXml;

        LOG.debug("{}: Will try to read config properties from {}", EXTENSION_NAME, EXTENSION_CONFIG_XML_LOCATION);

        if (!configXmlFile.canRead()) {
            LOG.warn("{}: Unable to read configuration file {}, using defaults",
                    EXTENSION_NAME,
                    configXmlFile.getAbsolutePath());
            extensionConfigXml = new ExtensionConfigXml();
        } else {
            try {
                extensionConfigXml = XmlParser.unmarshalExtensionConfig(configXmlFile);
            } catch (final IOException e) {
                extensionConfigXml = new ExtensionConfigXml();
                LOG.warn("{}: Could not read configuration file, reason: {}, using defaults",
                        EXTENSION_NAME,
                        e.getMessage());
            }
        }

        LOG.info("{}: Properties initialized to: {}", EXTENSION_NAME, extensionConfigXml);
        return extensionConfigXml;
    }

    private static @NotNull Properties setDefaults() {
        // these defaults must be kept in sync with those in ExtensionConfigXml
        final Properties properties = new Properties();
        properties.setProperty(CLIENT_CONNECT, TRUE);
        properties.setProperty(CONNACK_SEND, TRUE);

        properties.setProperty(CLIENT_DISCONNECT, TRUE);

        properties.setProperty(PUBLISH_RECEIVED, TRUE);
        properties.setProperty(PUBLISH_SEND, TRUE);

        properties.setProperty(SUBSCRIBE_RECEIVED, TRUE);
        properties.setProperty(SUBACK_SEND, TRUE);

        properties.setProperty(UNSUBSCRIBE_RECEIVED, TRUE);
        properties.setProperty(UNSUBACK_SEND, TRUE);

        properties.setProperty(PING_REQUEST_RECEIVED, TRUE);
        properties.setProperty(PING_RESPONSE_SEND, TRUE);

        properties.setProperty(PUBACK_RECEIVED, TRUE);
        properties.setProperty(PUBACK_SEND, TRUE);

        properties.setProperty(PUBREC_RECEIVED, TRUE);
        properties.setProperty(PUBREC_SEND, TRUE);

        properties.setProperty(PUBREL_RECEIVED, TRUE);
        properties.setProperty(PUBREL_SEND, TRUE);

        properties.setProperty(PUBCOMP_RECEIVED, TRUE);
        properties.setProperty(PUBCOMP_SEND, TRUE);

        properties.setProperty(VERBOSE, FALSE);

        properties.setProperty(PAYLOAD, TRUE);
        properties.setProperty(JSON, FALSE);

        return properties;
    }
}
