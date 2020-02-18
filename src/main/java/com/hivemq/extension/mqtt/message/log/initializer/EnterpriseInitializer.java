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
import com.hivemq.extension.sdk.api.services.Services;

import static com.hivemq.extension.mqtt.message.log.interceptor.InterceptorUtil.*;

/**
 * @author Michael Walter
 * @version 1.1.0
 */
public class EnterpriseInitializer {

    private final @NotNull ClientContext clientContext;

    private final @NotNull String version;

    private final @NotNull MqttMessageLogConfig config;

    public EnterpriseInitializer(final @NotNull ClientContext clientContext,
                                 final @NotNull String version,
                                 final @NotNull MqttMessageLogConfig config) {
        this.clientContext = clientContext;
        this.version = version;
        this.config = config;


    }

    public void init() {

        if (version.startsWith("4.2")) {
            registerMessage42Logger();
            return;
        }

        registerMessage43Logger();
    }

    private void registerMessage42Logger() {

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

        createSubscribeInboundInterceptor(config).ifPresent(clientContext::addSubscribeInboundInterceptor);
        createPublishInboundInterceptor(config).ifPresent(clientContext::addPublishInboundInterceptor);
        createPublishOutboundInterceptor(config).ifPresent(clientContext::addPublishOutboundInterceptor);
    }

    private void registerMessage43Logger() {

        createConnectOutboundInterceptor(config).ifPresent(connectInboundInterceptor ->
                Services.interceptorRegistry().setConnectInboundInterceptorProvider((input) -> connectInboundInterceptor));

        createConnackOutboundInterceptor(config).ifPresent(connackOutboundInterceptor ->
                Services.interceptorRegistry().setConnackOutboundInterceptorProvider(input -> connackOutboundInterceptor));

        createDisconnectInboundInterceptor(config).ifPresent(clientContext::addDisconnectInboundInterceptor);
        createDisconnectOutboundInterceptor(config).ifPresent(clientContext::addDisconnectOutboundInterceptor);

        createSubscribeInboundInterceptor(config).ifPresent(clientContext::addSubscribeInboundInterceptor);
        createSubackOutboundInterceptor(config).ifPresent(clientContext::addSubackOutboundInterceptor);

        createPingreqInboundInterceptor(config).ifPresent(clientContext::addPingReqInboundInterceptor);
        createPingrespOutboundInterceptor(config).ifPresent(clientContext::addPingRespOutboundInterceptor);

        createUnsubscribeInboundInterceptor(config).ifPresent(clientContext::addUnsubscribeInboundInterceptor);
        createUnsubackOutboundInterceptor(config).ifPresent(clientContext::addUnsubackOutboundInterceptor);

        createPublishInboundInterceptor(config).ifPresent(clientContext::addPublishInboundInterceptor);
        createPublishOutboundInterceptor(config).ifPresent(clientContext::addPublishOutboundInterceptor);

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
