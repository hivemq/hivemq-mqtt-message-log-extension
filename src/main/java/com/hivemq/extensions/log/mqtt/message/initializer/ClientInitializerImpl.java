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
import com.hivemq.extensions.log.mqtt.message.interceptor.ConnackOutboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.ConnectInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.DisconnectInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.DisconnectOutboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PingreqInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PingrespOutboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PubackInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PubackOutboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PubcompInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PubcompOutboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PublishInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PublishOutboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PubrecInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PubrecOutboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PubrelInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.PubrelOutboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.SubackOutboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.SubscribeInboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.UnsubackOutboundInterceptorImpl;
import com.hivemq.extensions.log.mqtt.message.interceptor.UnsubscribeInboundInterceptorImpl;
import org.jetbrains.annotations.NotNull;

/**
 * Creates a {@link ClientInitializer} that is usable since HiveMQ 4.3 Enterprise Version or Community Version 2020.1.
 *
 * @version 1.1.0
 */
public class ClientInitializerImpl implements ClientInitializer {

    private final @NotNull ExtensionConfig config;

    public ClientInitializerImpl(final @NotNull ExtensionConfig config) {
        this.config = config;
        init();
    }

    /**
     * Initialize any logging logic that can be done without a {@link ClientInitializer}.
     */
    private void init() {
        if (config.isClientConnect()) {
            Services.interceptorRegistry().setConnectInboundInterceptorProvider( //
                    ignored -> new ConnectInboundInterceptorImpl(config.isVerbose(), config.isPayload(),
                            config.isJson()));
        }

        if (config.isConnackSend()) {
            Services.interceptorRegistry().setConnackOutboundInterceptorProvider( //
                    ignored -> new ConnackOutboundInterceptorImpl(config.isVerbose(), config.isJson()));
        }
    }

    @Override
    public void initialize(
            final @NotNull InitializerInput initializerInput, final @NotNull ClientContext clientContext) {
        if (config.isClientDisconnect()) {
            clientContext.addDisconnectInboundInterceptor(new DisconnectInboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
            clientContext.addDisconnectOutboundInterceptor(new DisconnectOutboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }

        if (config.isSubscribeReceived()) {
            clientContext.addSubscribeInboundInterceptor(new SubscribeInboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }
        if (config.isSubackSend()) {
            clientContext.addSubackOutboundInterceptor(new SubackOutboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }

        if (config.isPingRequestReceived()) {
            clientContext.addPingReqInboundInterceptor(new PingreqInboundInterceptorImpl(config.isJson()));
        }
        if (config.isPingResponseSend()) {
            clientContext.addPingRespOutboundInterceptor(new PingrespOutboundInterceptorImpl(config.isJson()));
        }

        if (config.isUnsubscribeReceived()) {
            clientContext.addUnsubscribeInboundInterceptor(new UnsubscribeInboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }
        if (config.isUnsubackSend()) {
            clientContext.addUnsubackOutboundInterceptor(new UnsubackOutboundInterceptorImpl(config.isVerbose(),
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

        if (config.isPubackReceived()) {
            clientContext.addPubackInboundInterceptor(new PubackInboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }
        if (config.isPubackSend()) {
            clientContext.addPubackOutboundInterceptor(new PubackOutboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }

        if (config.isPubrecReceived()) {
            clientContext.addPubrecInboundInterceptor(new PubrecInboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }
        if (config.isPubrecSend()) {
            clientContext.addPubrecOutboundInterceptor(new PubrecOutboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }

        if (config.isPubrelReceived()) {
            clientContext.addPubrelInboundInterceptor(new PubrelInboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }
        if (config.isPubrelSend()) {
            clientContext.addPubrelOutboundInterceptor(new PubrelOutboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }

        if (config.isPubcompReceived()) {
            clientContext.addPubcompInboundInterceptor(new PubcompInboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }
        if (config.isPubcompSend()) {
            clientContext.addPubcompOutboundInterceptor(new PubcompOutboundInterceptorImpl(config.isVerbose(),
                    config.isJson()));
        }
    }
}
