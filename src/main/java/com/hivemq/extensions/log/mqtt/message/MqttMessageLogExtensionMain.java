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
package com.hivemq.extensions.log.mqtt.message;

import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.parameter.ServerInformation;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopOutput;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.admin.LicenseEdition;
import com.hivemq.extension.sdk.api.services.intializer.ClientInitializer;
import com.hivemq.extensions.log.mqtt.message.config.MqttMessageLogConfig;
import com.hivemq.extensions.log.mqtt.message.config.MqttMessageLogConfigReader;
import com.hivemq.extensions.log.mqtt.message.initializer.ClientInitializerImpl;
import com.hivemq.extensions.log.mqtt.message.initializer.ClientInitializerImpl4_2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
public class MqttMessageLogExtensionMain implements ExtensionMain {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(MqttMessageLogExtensionMain.class);

    @Override
    public void extensionStart(
            final @NotNull ExtensionStartInput extensionStartInput,
            final @NotNull ExtensionStartOutput extensionStartOutput) {
        try {
            extensionStartInput.getServerInformation();
        } catch (final NoSuchMethodError e) {
            // only a version that is not supported will throw this exception
            extensionStartOutput.preventExtensionStartup("The HiveMQ version is not supported");
            return;
        }

        try {
            final MqttMessageLogConfigReader configReader =
                    new MqttMessageLogConfigReader(extensionStartInput.getExtensionInformation()
                            .getExtensionHomeFolder());
            final MqttMessageLogConfig config = new MqttMessageLogConfig(configReader.readProperties());

            if (config.allDisabled()) {
                extensionStartOutput.preventExtensionStartup(extensionStartInput.getExtensionInformation().getName() +
                        " start prevented because all properties set to false");
                return;
            }

            final ClientInitializer initializer =
                    getClientInitializerForEdition(extensionStartInput.getServerInformation(), config);

            Services.initializerRegistry().setClientInitializer(initializer);

        } catch (final Exception e) {
            extensionStartOutput.preventExtensionStartup(extensionStartInput.getExtensionInformation().getName() +
                    " cannot be started");
            LOG.error(extensionStartInput.getExtensionInformation().getName() +
                    " could not be started. An exception was thrown!", e);
        }
    }

    @Override
    public void extensionStop(
            final @NotNull ExtensionStopInput extensionStopInput,
            final @NotNull ExtensionStopOutput extensionStopOutput) {
    }

    private @NotNull ClientInitializer getClientInitializerForEdition(
            final @NotNull ServerInformation serverInformation, final @NotNull MqttMessageLogConfig config) {
        final LicenseEdition edition = Services.adminService().getLicenseInformation().getEdition();
        final String version = serverInformation.getVersion();

        if (LicenseEdition.COMMUNITY.equals(edition)) {
            return new ClientInitializerImpl(config);
        } else if (version.startsWith("4.2")) {
            return new ClientInitializerImpl4_2(config);
        } else {
            return new ClientInitializerImpl(config);
        }
    }
}
