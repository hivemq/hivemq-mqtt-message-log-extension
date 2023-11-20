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

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.disconnect.DisconnectInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.disconnect.parameter.DisconnectInboundInput;
import com.hivemq.extension.sdk.api.interceptor.disconnect.parameter.DisconnectInboundOutput;
import com.hivemq.extensions.log.mqtt.message.util.MessageLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Walter
 * @since 1.1.0
 */
class DisconnectInboundInterceptorImpl implements DisconnectInboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(DisconnectInboundInterceptorImpl.class);
    private final boolean verbose;

    DisconnectInboundInterceptorImpl(final boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void onInboundDisconnect(
            final @NotNull DisconnectInboundInput disconnectInboundInput,
            final @NotNull DisconnectInboundOutput disconnectInboundOutput) {
        try {
            final String clientId = disconnectInboundInput.getClientInformation().getClientId();
            MessageLogUtil.logDisconnect(disconnectInboundInput.getDisconnectPacket(), clientId, true, verbose);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at inbound disconnect logging: ", e);
        }
    }
}
