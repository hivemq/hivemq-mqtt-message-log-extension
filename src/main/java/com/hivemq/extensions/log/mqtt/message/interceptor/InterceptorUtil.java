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
package com.hivemq.extensions.log.mqtt.message.interceptor;

import com.hivemq.extension.sdk.api.interceptor.connack.ConnackOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.connect.ConnectInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.disconnect.DisconnectInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.disconnect.DisconnectOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pingreq.PingReqInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pingresp.PingRespOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.puback.PubackInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.puback.PubackOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubcomp.PubcompInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubcomp.PubcompOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.PublishOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubrec.PubrecInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubrec.PubrecOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubrel.PubrelInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubrel.PubrelOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.suback.SubackOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.subscribe.SubscribeInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.unsuback.UnsubackOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.unsubscribe.UnsubscribeInboundInterceptor;
import com.hivemq.extensions.log.mqtt.message.config.MqttMessageLogConfig;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * @author Michael Walter
 * @version 1.1.0
 */
public class InterceptorUtil {

    public static @NotNull Optional<ConnectInboundInterceptor> createConnectOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isClientConnect()) {
            return Optional.of(new ConnectInboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<ConnackOutboundInterceptor> createConnackOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isConnackSend()) {
            return Optional.of(new ConnackOutboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<DisconnectInboundInterceptor> createDisconnectInboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isClientDisconnect()) {
            return Optional.of(new DisconnectInboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<DisconnectOutboundInterceptor> createDisconnectOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isClientDisconnect()) {
            return Optional.of(new DisconnectOutboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<SubscribeInboundInterceptor> createSubscribeInboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isSubscribeReceived()) {
            return Optional.of(new SubscribeInboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<SubackOutboundInterceptor> createSubackOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isSubackSend()) {
            return Optional.of(new SubackOutboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PublishOutboundInterceptor> createPublishOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPublishSend()) {
            return Optional.of(new PublishOutboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PublishInboundInterceptor> createPublishInboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPublishReceived()) {
            return Optional.of(new PublishInboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PingReqInboundInterceptor> createPingreqInboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPingreqReceived()) {
            return Optional.of(new PingreqInboundInterceptorImpl());
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PingRespOutboundInterceptor> createPingrespOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPingrespSend()) {
            return Optional.of(new PingrespOutboundInterceptorImpl());
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<UnsubscribeInboundInterceptor> createUnsubscribeInboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isUnsubscribeReceived()) {
            return Optional.of(new UnsubscribeInboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<UnsubackOutboundInterceptor> createUnsubackOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isUnsubackSend()) {
            return Optional.of(new UnsubackOutboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PubackInboundInterceptor> createPubackInboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPubackReceived()) {
            return Optional.of(new PubackInboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PubackOutboundInterceptor> createPubackOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPubackSend()) {
            return Optional.of(new PubackOutboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PubrecInboundInterceptor> createPubrecInboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPubrecReceived()) {
            return Optional.of(new PubrecInboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PubrecOutboundInterceptor> createPubrecOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPubrecSend()) {
            return Optional.of(new PubrecOutboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PubrelInboundInterceptor> createPubrelInboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPubrelReceived()) {
            return Optional.of(new PubrelInboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PubrelOutboundInterceptor> createPubrelOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPubrelSend()) {
            return Optional.of(new PubrelOutboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PubcompInboundInterceptor> createPubcompInboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPubcompReceived()) {
            return Optional.of(new PubcompInboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }

    public static @NotNull Optional<PubcompOutboundInterceptor> createPubcompOutboundInterceptor(final @NotNull MqttMessageLogConfig config) {
        if (config.isPubcompSend()) {
            return Optional.of(new PubcompOutboundInterceptorImpl(config.isVerbose()));
        } else {
            return Optional.empty();
        }
    }
}
