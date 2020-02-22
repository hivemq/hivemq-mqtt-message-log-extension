/*
 * Copyright 2020 dc-square GmbH
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

package com.hivemq.extension.mqtt.message.log.initializer;

import com.hivemq.extension.mqtt.message.log.config.MqttMessageLogConfig;
import com.hivemq.extension.mqtt.message.log.interceptor.ConnectDisconnectEventListener;
import com.hivemq.extension.mqtt.message.log.interceptor.ConnectInboundInterceptorImpl;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.ClientContext;
import com.hivemq.extension.sdk.api.client.parameter.InitializerInput;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.intializer.ClientInitializer;

import static com.hivemq.extension.mqtt.message.log.interceptor.InterceptorUtil.*;

/**
 * Creates a {@link ClientInitializer} that is usable for any HiveMQ 4.2 Enterprise Version.
 *
 * @author Michael Walter
 * @version 1.1.0
 */
public class Enterprise42Initializer implements ClientInitializer {

    private final @NotNull MqttMessageLogConfig config;

    public Enterprise42Initializer(final @NotNull MqttMessageLogConfig config) {
        this.config = config;

        init();
    }

    /**
     * Initialize any logging logic that can be done without a {@link ClientInitializer}.
     */
    private void init() {
        if (config.isClientConnect() && config.isClientDisconnect()) {
            final ConnectDisconnectEventListener connectDisconnectEventListener = new ConnectDisconnectEventListener(true, config.isVerbose());
            Services.eventRegistry().setClientLifecycleEventListener((input) -> connectDisconnectEventListener);
        } else if (config.isClientDisconnect()) {
            final ConnectDisconnectEventListener connectDisconnectEventListener = new ConnectDisconnectEventListener(false, config.isVerbose());
            Services.eventRegistry().setClientLifecycleEventListener((input) -> connectDisconnectEventListener);
        } else if (config.isClientConnect()) {
            final ConnectInboundInterceptorImpl connectInboundInterceptor = new ConnectInboundInterceptorImpl(config.isVerbose());
            Services.interceptorRegistry().setConnectInboundInterceptorProvider((input) -> connectInboundInterceptor);
        }
    }

    @Override
    public void initialize(final @NotNull InitializerInput initializerInput,
                           final @NotNull ClientContext clientContext) {
        createSubscribeInboundInterceptor(config).ifPresent(clientContext::addSubscribeInboundInterceptor);
        createPublishInboundInterceptor(config).ifPresent(clientContext::addPublishInboundInterceptor);
        createPublishOutboundInterceptor(config).ifPresent(clientContext::addPublishOutboundInterceptor);
    }
}
