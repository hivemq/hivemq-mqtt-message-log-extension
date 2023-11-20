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

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.ClientContext;
import com.hivemq.extension.sdk.api.client.parameter.InitializerInput;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.intializer.ClientInitializer;
import com.hivemq.extensions.log.mqtt.message.config.MqttMessageLogConfig;
import com.hivemq.extensions.log.mqtt.message.interceptor.InterceptorUtil;

/**
 * Creates a {@link ClientInitializer} that is usable since HiveMQ 4.3 Enterprise Version or Community Version 2020.1.
 *
 * @author Michael Walter
 * @version 1.1.0
 */
public class ClientInitializerImpl implements ClientInitializer {

    private final @NotNull MqttMessageLogConfig config;

    public ClientInitializerImpl(final @NotNull MqttMessageLogConfig config) {
        this.config = config;
        init();
    }

    /**
     * Initialize any logging logic that can be done without a {@link ClientInitializer}.
     */
    private void init() {
        InterceptorUtil.createConnectOutboundInterceptor(config)
                .ifPresent(connectInboundInterceptor -> Services.interceptorRegistry()
                        .setConnectInboundInterceptorProvider((input) -> connectInboundInterceptor));

        InterceptorUtil.createConnackOutboundInterceptor(config)
                .ifPresent(connackOutboundInterceptor -> Services.interceptorRegistry()
                        .setConnackOutboundInterceptorProvider(input -> connackOutboundInterceptor));
    }

    @Override
    public void initialize(
            final @NotNull InitializerInput initializerInput, final @NotNull ClientContext clientContext) {
        InterceptorUtil.createDisconnectInboundInterceptor(config)
                .ifPresent(clientContext::addDisconnectInboundInterceptor);
        InterceptorUtil.createDisconnectOutboundInterceptor(config)
                .ifPresent(clientContext::addDisconnectOutboundInterceptor);

        InterceptorUtil.createSubscribeInboundInterceptor(config)
                .ifPresent(clientContext::addSubscribeInboundInterceptor);
        InterceptorUtil.createSubackOutboundInterceptor(config).ifPresent(clientContext::addSubackOutboundInterceptor);

        InterceptorUtil.createPingreqInboundInterceptor(config).ifPresent(clientContext::addPingReqInboundInterceptor);
        InterceptorUtil.createPingrespOutboundInterceptor(config)
                .ifPresent(clientContext::addPingRespOutboundInterceptor);

        InterceptorUtil.createUnsubscribeInboundInterceptor(config)
                .ifPresent(clientContext::addUnsubscribeInboundInterceptor);
        InterceptorUtil.createUnsubackOutboundInterceptor(config)
                .ifPresent(clientContext::addUnsubackOutboundInterceptor);

        InterceptorUtil.createPublishInboundInterceptor(config).ifPresent(clientContext::addPublishInboundInterceptor);
        InterceptorUtil.createPublishOutboundInterceptor(config)
                .ifPresent(clientContext::addPublishOutboundInterceptor);

        InterceptorUtil.createPubackInboundInterceptor(config).ifPresent(clientContext::addPubackInboundInterceptor);
        InterceptorUtil.createPubackOutboundInterceptor(config).ifPresent(clientContext::addPubackOutboundInterceptor);

        InterceptorUtil.createPubrecInboundInterceptor(config).ifPresent(clientContext::addPubrecInboundInterceptor);
        InterceptorUtil.createPubrecOutboundInterceptor(config).ifPresent(clientContext::addPubrecOutboundInterceptor);

        InterceptorUtil.createPubrelInboundInterceptor(config).ifPresent(clientContext::addPubrelInboundInterceptor);
        InterceptorUtil.createPubrelOutboundInterceptor(config).ifPresent(clientContext::addPubrelOutboundInterceptor);

        InterceptorUtil.createPubcompInboundInterceptor(config).ifPresent(clientContext::addPubcompInboundInterceptor);
        InterceptorUtil.createPubcompOutboundInterceptor(config)
                .ifPresent(clientContext::addPubcompOutboundInterceptor);
    }
}
