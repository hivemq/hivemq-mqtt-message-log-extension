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
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.client.ClientContext;
import com.hivemq.extension.sdk.api.client.parameter.InitializerInput;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.intializer.ClientInitializer;

import static com.hivemq.extension.mqtt.message.log.interceptor.InterceptorUtil.*;

/**
 * Creates a {@link ClientInitializer} that is usable for the community edition.
 *
 * @author Michael Walter
 * @version 1.1.0
 */
public class CommunityInitializer implements ClientInitializer {

    private final @NotNull MqttMessageLogConfig config;

    public CommunityInitializer(final @NotNull MqttMessageLogConfig config) {
        this.config = config;

        init();
    }

    /**
     * Initialize any logging logic that can be done without a {@link ClientInitializer}.
     */
    private void init() {
        createConnectOutboundInterceptor(config).ifPresent(connectInboundInterceptor ->
                Services.interceptorRegistry().setConnectInboundInterceptorProvider((input) -> connectInboundInterceptor));

        createConnackOutboundInterceptor(config).ifPresent(connackOutboundInterceptor ->
                Services.interceptorRegistry().setConnackOutboundInterceptorProvider(input -> connackOutboundInterceptor));
    }

    @Override
    public void initialize(final @NotNull InitializerInput initializerInput,
                           final @NotNull ClientContext clientContext) {
        createDisconnectInboundInterceptor(config).ifPresent(clientContext::addDisconnectInboundInterceptor);
        createDisconnectOutboundInterceptor(config).ifPresent(clientContext::addDisconnectOutboundInterceptor);

        createPublishInboundInterceptor(config).ifPresent(clientContext::addPublishInboundInterceptor);
        createPublishOutboundInterceptor(config).ifPresent(clientContext::addPublishOutboundInterceptor);

        createSubscribeInboundInterceptor(config).ifPresent(clientContext::addSubscribeInboundInterceptor);
        createSubackOutboundInterceptor(config).ifPresent(clientContext::addSubackOutboundInterceptor);

        createPingreqInboundInterceptor(config).ifPresent(clientContext::addPingReqInboundInterceptor);
        createPingrespOutboundInterceptor(config).ifPresent(clientContext::addPingRespOutboundInterceptor);

        createUnsubscribeInboundInterceptor(config).ifPresent(clientContext::addUnsubscribeInboundInterceptor);
        createUnsubackOutboundInterceptor(config).ifPresent(clientContext::addUnsubackOutboundInterceptor);

        createPubackInboundInterceptor(config).ifPresent(clientContext::addPubackInboundInterceptor);
        createPubackOutboundInterceptor(config).ifPresent(clientContext::addPubackOutboundInterceptor);

        createPubrecInboundInterceptor(config).ifPresent(clientContext::addPubrecInboundInterceptor);
        createPubrecOutboundInterceptor(config).ifPresent(clientContext::addPubrecOutboundInterceptor);

        createPubrelInboundInterceptor(config).ifPresent(clientContext::addPubrelInboundInterceptor);
        createPubrelOutboundInterceptor(config).ifPresent(clientContext::addPubrelOutboundInterceptor);

        createPubcompInboundInterceptor(config).ifPresent(clientContext::addPubcompInboundInterceptor);
        createPubcompOutboundInterceptor(config).ifPresent(clientContext::addPubcompOutboundInterceptor);
    }
}
