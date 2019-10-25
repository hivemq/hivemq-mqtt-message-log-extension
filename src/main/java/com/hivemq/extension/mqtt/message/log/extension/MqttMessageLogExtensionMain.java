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

package com.hivemq.extension.mqtt.message.log.extension;

import com.hivemq.extension.mqtt.message.log.config.MqttMessageLogConfig;
import com.hivemq.extension.mqtt.message.log.config.MqttMessageLogConfigReader;
import com.hivemq.extension.mqtt.message.log.interceptor.*;
import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.subscribe.SubscribeInboundInterceptor;
import com.hivemq.extension.sdk.api.parameter.*;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.intializer.ClientInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
public class MqttMessageLogExtensionMain implements ExtensionMain {

    private static final @NotNull Logger log = LoggerFactory.getLogger(MqttMessageLogExtensionMain.class);

    @Override
    public void extensionStart(final @NotNull ExtensionStartInput extensionStartInput, final @NotNull ExtensionStartOutput extensionStartOutput) {

        try {
            final MqttMessageLogConfigReader configReader = new MqttMessageLogConfigReader(extensionStartInput.getExtensionInformation().getExtensionHomeFolder());
            final MqttMessageLogConfig config = new MqttMessageLogConfig(configReader.readProperties());

            if (config.isClientConnect() && config.isClientDisconnect()) {
                final ConnectDisconnectEventListener connectDisconnectEventListener = new ConnectDisconnectEventListener(true);
                Services.eventRegistry().setClientLifecycleEventListener((input) -> connectDisconnectEventListener);
            } else if (config.isClientDisconnect()) {
                final ConnectDisconnectEventListener connectDisconnectEventListener = new ConnectDisconnectEventListener(false);
                Services.eventRegistry().setClientLifecycleEventListener((input) -> connectDisconnectEventListener);
            } else if (config.isClientConnect()) {
                final ConnectInboundInterceptorImpl connectInboundInterceptor = new ConnectInboundInterceptorImpl();
                Services.interceptorRegistry().setConnectInboundInterceptorProvider((input) -> connectInboundInterceptor);
            } else {
                //neither connect nor disconnect enabled
            }
            final PublishInboundInterceptor publishInboundInterceptor = createPublishInboundInterceptor(config);
            final PublishOutboundInterceptor publishOutboundInterceptor = createPublishOutboundInterceptor(config);
            final SubscribeInboundInterceptor subscribeInboundInterceptor = createSubscribeInboundInterceptor(config);

            if (subscribeInboundInterceptor == null && publishInboundInterceptor == null && publishOutboundInterceptor == null) {
                if(!config.isClientConnect() && !config.isClientDisconnect()){
                    extensionStartOutput.preventExtensionStartup(extensionStartInput.getExtensionInformation().getName() + " start prevented because all properties set to false.");
                }
                return;
            }

            final ClientInitializer initializer = (initializerInput, clientContext) -> {
                if (publishInboundInterceptor != null) {
                    clientContext.addPublishInboundInterceptor(publishInboundInterceptor);
                }
                if (publishOutboundInterceptor != null) {
                    clientContext.addPublishOutboundInterceptor(publishOutboundInterceptor);
                }
                if (subscribeInboundInterceptor != null) {
                    clientContext.addSubscribeInboundInterceptor(subscribeInboundInterceptor);
                }
            };

            Services.initializerRegistry().setClientInitializer(initializer);

        } catch (final Exception e) {
            extensionStartOutput.preventExtensionStartup(extensionStartInput.getExtensionInformation().getName() + " cannot be started.");
            log.error(extensionStartInput.getExtensionInformation().getName() + " could not be started. An exception was thrown!", e);
        }

    }

    @Override
    public void extensionStop(final @NotNull ExtensionStopInput extensionStopInput, final @NotNull ExtensionStopOutput extensionStopOutput) {
        final ExtensionInformation extensionInformation = extensionStopInput.getExtensionInformation();
        log.info("Stopped " + extensionInformation.getName() + ":" + extensionInformation.getVersion());
    }

    @Nullable
    private SubscribeInboundInterceptor createSubscribeInboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        final SubscribeInboundInterceptor subscribeInboundInterceptor;
        if (config.isSubscribeReceived()) {
            subscribeInboundInterceptor = new SubscribeInboundInterceptorImpl();
        } else {
            subscribeInboundInterceptor = null;
        }
        return subscribeInboundInterceptor;
    }

    @Nullable
    private PublishOutboundInterceptor createPublishOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        final PublishOutboundInterceptor publishOutboundInterceptor;
        if (config.isPublishSend()) {
            publishOutboundInterceptor = new PublishOutboundInterceptorImpl();
        } else {
            publishOutboundInterceptor = null;
        }
        return publishOutboundInterceptor;
    }

    @Nullable
    private PublishInboundInterceptor createPublishInboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        final PublishInboundInterceptor publishInboundInterceptor;
        if (config.isPublishReceived()) {
            publishInboundInterceptor = new PublishInboundInterceptorImpl();
        } else {
            publishInboundInterceptor = null;
        }
        return publishInboundInterceptor;
    }
}
