package com.hivemq.extensions.mqtt.message.log.extension;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.parameter.ServerInformation;
import com.hivemq.extension.sdk.api.parameter.ExtensionInformation;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
class MqttMessageLogExtensionMainTest {

    private @NotNull ExtensionStartInput extensionStartInput;
    private @NotNull ExtensionStartOutput extensionStartOutput;
    private @NotNull MqttMessageLogExtensionMain extensionMain;

    @BeforeEach
    void setUp() {
        extensionStartInput = mock(ExtensionStartInput.class);
        extensionStartOutput = mock(ExtensionStartOutput.class);
        extensionMain = new MqttMessageLogExtensionMain();
    }

    @Test
    void test_extension_start_prevented_no_access_to_static_components() {
        final ExtensionInformation information = mock(ExtensionInformation.class);
        final ServerInformation serverInformation = mock(ServerInformation.class);

        when(extensionStartInput.getExtensionInformation()).thenReturn(information);
        when(extensionStartInput.getServerInformation()).thenReturn(serverInformation);
        when(serverInformation.getVersion()).thenReturn("4.2.1");
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
}
