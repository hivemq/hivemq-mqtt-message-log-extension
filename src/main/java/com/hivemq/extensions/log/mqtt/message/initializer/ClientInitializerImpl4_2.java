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
package com.hivemq.extensions.log.mqtt.message.initializer;

import com.hivemq.extension.sdk.api.client.ClientContext;
import com.hivemq.extension.sdk.api.client.parameter.InitializerInput;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.intializer.ClientInitializer;
import com.hivemq.extensions.log.mqtt.message.config.ExtensionConfig;
import com.hivemq.extensions.log.mqtt.message.interceptor.ConnectDisconnectEventListener;
import com.hivemq.extensions.log.mqtt.message.interceptor.ConnectInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PublishInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PublishOutboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.SubscribeInboundInterceptorImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Creates a {@link ClientInitializer} that is usable for any HiveMQ 4.2 Enterprise Version.
 *
 * @version 1.1.0
 */
public class ClientInitializerImpl4_2 implements ClientInitializer {

    private final @NotNull ExtensionConfig config;

    public ClientInitializerImpl4_2(final @NotNull ExtensionConfig config) {
        this.config = config;
        init();
    }

    /**
     * Initialize any logging logic that can be done without a {@link ClientInitializer}.
     */
    private void init() {
        if (config.isClientConnect() && config.isClientDisconnect()) {
            Services.eventRegistry().setClientLifecycleEventListener( //
                    input -> new ConnectDisconnectEventListener(true, config.isVerbose(), config.isPayload(),
                            config.isJson()));
        } else if (config.isClientDisconnect()) {
            Services.eventRegistry().setClientLifecycleEventListener( //
                    input -> new ConnectDisconnectEventListener(false, config.isVerbose(), config.isPayload(),
                            config.isJson()));
        } else if (config.isClientConnect()) {
            Services.interceptorRegistry().setConnectInboundInterceptorProvider( //
                    input -> new ConnectInboundInterceptorImpl(config.isVerbose(), config.isPayload(),
                            config.isJson()));
        }
    }

    @Override
    public void initialize(
            final @NotNull InitializerInput initializerInput, final @NotNull ClientContext clientContext) {
        if (config.isSubscribeReceived()) {
            clientContext.addSubscribeInboundInterceptor(new SubscribeInboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }
        if (config.isPublishReceived()) {
            clientContext.addPublishInboundInterceptor(new PublishInboundInterceptorImpl(config.isVerbose(),
                    config.isPayload(), config.isJson()));
        }
        if (config.isPublishSend()) {
            clientContext.addPublishOutboundInterceptor(new PublishOutboundInterceptorImpl(config.isVerbose(),
                    config.isPayload(), config.isJson()));
        }
    }
}
