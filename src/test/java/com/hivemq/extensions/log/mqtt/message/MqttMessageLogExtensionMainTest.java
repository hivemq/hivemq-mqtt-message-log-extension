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

import com.hivemq.extension.sdk.api.client.parameter.ServerInformation;
import com.hivemq.extension.sdk.api.parameter.ExtensionInformation;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import com.hivemq.extension.sdk.api.services.admin.LicenseEdition;
import com.hivemq.extensions.log.mqtt.message.initializer.ClientInitializerImpl;
import com.hivemq.extensions.log.mqtt.message.initializer.ClientInitializerImpl4_2;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @since 1.0.0
 */
class MqttMessageLogExtensionMainTest {

    private final @NotNull ExtensionStartInput extensionStartInput = mock();
    private final @NotNull ExtensionStartOutput extensionStartOutput = mock();

    private final @NotNull MqttMessageLogExtensionMain extensionMain = new MqttMessageLogExtensionMain();

    @Test
    void test_extension_start_prevented_no_access_to_static_components() {
        final var information = mock(ExtensionInformation.class);
        final var serverInformation = mock(ServerInformation.class);

        when(extensionStartInput.getExtensionInformation()).thenReturn(information);
        when(extensionStartInput.getServerInformation()).thenReturn(serverInformation);
        when(serverInformation.getVersion()).thenReturn("4");
        when(information.getExtensionHomeFolder()).thenReturn(new File("some/not/existing/folder"));
        when(information.getName()).thenReturn("My Extension");

        extensionMain.extensionStart(extensionStartInput, extensionStartOutput);
        verify(extensionStartOutput).preventExtensionStartup("My Extension cannot be started");
    }

    @Test
    void test_extension_start_prevented_because_of_old_version() {
        when(extensionStartInput.getServerInformation()).thenThrow(NoSuchMethodError.class);

        extensionMain.extensionStart(extensionStartInput, extensionStartOutput);
        verify(extensionStartOutput).preventExtensionStartup("The HiveMQ version is not supported");
    }

    @Test
    void getClientInitializerForEdition_4_2_0_oldImplReturned() {
        final var clientInitializerForEdition =
                extensionMain.getClientInitializerForEdition(LicenseEdition.ENTERPRISE, "4.2.0", mock());
        assertThat(clientInitializerForEdition).isInstanceOf(ClientInitializerImpl4_2.class);
    }

    @Test
    void getClientInitializerForEdition_4_3_0_newImplReturned() {
        final var clientInitializerForEdition =
                extensionMain.getClientInitializerForEdition(LicenseEdition.ENTERPRISE, "4.3.0", mock());
        assertThat(clientInitializerForEdition).isInstanceOf(ClientInitializerImpl.class);
    }

    @Test
    void getClientInitializerForEdition_4_20_0_newImplReturned() {
        final var clientInitializerForEdition =
                extensionMain.getClientInitializerForEdition(LicenseEdition.ENTERPRISE, "4.20.0", mock());
        assertThat(clientInitializerForEdition).isInstanceOf(ClientInitializerImpl.class);
    }

    @Test
    void getClientInitializerForEdition_COMMUNITY_newImplReturned() {
        final var clientInitializerForEdition =
                extensionMain.getClientInitializerForEdition(LicenseEdition.COMMUNITY, "2024.1", mock());
        assertThat(clientInitializerForEdition).isInstanceOf(ClientInitializerImpl.class);
    }
}
