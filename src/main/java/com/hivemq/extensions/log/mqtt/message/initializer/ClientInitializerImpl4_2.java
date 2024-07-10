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
import com.hivemq.extensions.log.mqtt.message.config.MqttMessageLogConfig;
import com.hivemq.extensions.log.mqtt.message.interceptor.ConnectDisconnectEventListener;
import com.hivemq.extensions.log.mqtt.message.interceptor.ConnectInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.InterceptorUtil;
import org.jetbrains.annotations.NotNull;

/**
 * Creates a {@link ClientInitializer} that is usable for any HiveMQ 4.2 Enterprise Version.
 *
 * @author Michael Walter
 * @version 1.1.0
 */
public class ClientInitializerImpl4_2 implements ClientInitializer {

    private final @NotNull MqttMessageLogConfig config;

    public ClientInitializerImpl4_2(final @NotNull MqttMessageLogConfig config) {
        this.config = config;
        init();
    }

    /**
     * Initialize any logging logic that can be done without a {@link ClientInitializer}.
     */
    private void init() {
        if (config.isClientConnect() && config.isClientDisconnect()) {
            final ConnectDisconnectEventListener connectDisconnectEventListener =
                    new ConnectDisconnectEventListener(true, config.isVerbose(), config.isPayload());
            Services.eventRegistry().setClientLifecycleEventListener((input) -> connectDisconnectEventListener);
        } else if (config.isClientDisconnect()) {
            final ConnectDisconnectEventListener connectDisconnectEventListener =
                    new ConnectDisconnectEventListener(false, config.isVerbose(), config.isPayload());
            Services.eventRegistry().setClientLifecycleEventListener((input) -> connectDisconnectEventListener);
        } else if (config.isClientConnect()) {
            final ConnectInboundInterceptorImpl connectInboundInterceptor =
                    new ConnectInboundInterceptorImpl(config.isVerbose(), config.isPayload());
            Services.interceptorRegistry().setConnectInboundInterceptorProvider((input) -> connectInboundInterceptor);
        }
    }

    @Override
    public void initialize(
            final @NotNull InitializerInput initializerInput, final @NotNull ClientContext clientContext) {
        InterceptorUtil.createSubscribeInboundInterceptor(config)
                .ifPresent(clientContext::addSubscribeInboundInterceptor);
        InterceptorUtil.createPublishInboundInterceptor(config).ifPresent(clientContext::addPublishInboundInterceptor);
        InterceptorUtil.createPublishOutboundInterceptor(config)
                .ifPresent(clientContext::addPublishOutboundInterceptor);
    }
}
