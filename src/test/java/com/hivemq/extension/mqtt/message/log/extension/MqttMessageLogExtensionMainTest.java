package com.hivemq.extension.mqtt.message.log.extension;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.parameter.ExtensionInformation;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
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
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        extensionMain = new MqttMessageLogExtensionMain();
    }

    @Test
    public void test_extension_start_prevented_no_access_to_static_components() {

        final ExtensionInformation information = Mockito.mock(ExtensionInformation.class);
        when(input.getExtensionInformation()).thenReturn(information);
        when(information.getExtensionHomeFolder()).thenReturn(new File("some/not/existing/folder"));
        when(information.getName()).thenReturn("My Extension");
        extensionMain.extensionStart(input, output);

        verify(output).preventExtensionStartup("My Extension cannot be started.");

    }
}