package com.hivemq.extension.mqtt.message.log.extension;

import com.hivemq.extension.sdk.api.client.parameter.ServerInformation;
import com.hivemq.extension.sdk.api.parameter.ExtensionInformation;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
public class MqttMessageLogExtensionMainTest {

    private MqttMessageLogExtensionMain extensionMain;

    @Mock
    private ExtensionStartInput input;

    @Mock
    private ExtensionStartOutput output;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        extensionMain = new MqttMessageLogExtensionMain();
    }

    @Test
    public void test_extension_start_prevented_no_access_to_static_components() {

        final ExtensionInformation information = Mockito.mock(ExtensionInformation.class);
        final ServerInformation serverInformation = Mockito.mock(ServerInformation.class);

        when(input.getExtensionInformation()).thenReturn(information);
        when(input.getServerInformation()).thenReturn(serverInformation);
        when(serverInformation.getVersion()).thenReturn("4.2.1");
        when(information.getExtensionHomeFolder()).thenReturn(new File("some/not/existing/folder"));
        when(information.getName()).thenReturn("My Extension");

        extensionMain.extensionStart(input, output);

        verify(output).preventExtensionStartup("My Extension cannot be started");

    }

    @Test
    public void test_extension_start_prevented_because_of_old_version() {
        when(input.getServerInformation()).thenThrow(NoSuchMethodError.class);

        extensionMain.extensionStart(input, output);

        verify(output).preventExtensionStartup("The HiveMQ version is not supported");
    }
}