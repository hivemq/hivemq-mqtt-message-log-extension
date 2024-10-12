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

import com.hivemq.extensions.log.mqtt.message.ExtensionConstants;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @since 1.2.0
 */
class XmlParser {
    private static final @NotNull Logger LOG = LoggerFactory.getLogger(XmlParser.class);

    static @NotNull ExtensionConfigXml unmarshalExtensionConfig(final @NotNull File file) throws IOException {
        final @NotNull JAXBContext jaxb;

        try {
            jaxb = JAXBContext.newInstance(ExtensionConfigXml.class);
        } catch (final JAXBException e) {
            LOG.error("{}: Could not initialize XML parser", ExtensionConstants.EXTENSION_NAME, e);
            throw new RuntimeException(e);
        }

        try {
            final Unmarshaller unmarshaller = jaxb.createUnmarshaller();
            return (ExtensionConfigXml) unmarshaller.unmarshal(file);
        } catch (final JAXBException e) {
            throw new IOException(e);
        }
    }
}