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

import com.hivemq.extension.sdk.api.interceptor.disconnect.DisconnectOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.disconnect.parameter.DisconnectOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.disconnect.parameter.DisconnectOutboundOutput;
import com.hivemq.extensions.log.mqtt.message.logger.MessageLogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1.0
 */
public class DisconnectOutboundInterceptorImpl implements DisconnectOutboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(DisconnectOutboundInterceptorImpl.class);

    private final @NotNull MessageLogger messageLogger;

    public DisconnectOutboundInterceptorImpl(final @NotNull MessageLogger messageLogger) {
        this.messageLogger = messageLogger;
    }

    @Override
    public void onOutboundDisconnect(
            final @NotNull DisconnectOutboundInput disconnectOutboundInput,
            final @NotNull DisconnectOutboundOutput disconnectOutboundOutput) {
        try {
            final var clientId = disconnectOutboundInput.getClientInformation().getClientId();
            messageLogger.logDisconnect(disconnectOutboundInput.getDisconnectPacket(), clientId, false);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at outbound disconnect logging: ", e);
        }
    }
}
