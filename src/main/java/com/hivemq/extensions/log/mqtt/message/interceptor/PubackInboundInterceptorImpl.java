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

import com.hivemq.extension.sdk.api.interceptor.puback.PubackInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.puback.parameter.PubackInboundInput;
import com.hivemq.extension.sdk.api.interceptor.puback.parameter.PubackInboundOutput;
import com.hivemq.extensions.log.mqtt.message.util.MessageLogUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1.0
 */
public class PubackInboundInterceptorImpl implements PubackInboundInterceptor {

    private static final @NotNull Logger log = LoggerFactory.getLogger(PubackInboundInterceptorImpl.class);
    private final boolean verbose;
    private final boolean json;

    public PubackInboundInterceptorImpl(final boolean verbose, final boolean json) {
        this.verbose = verbose;
        this.json = json;
    }

    @Override
    public void onInboundPuback(
            final @NotNull PubackInboundInput pubackInboundInput,
            final @NotNull PubackInboundOutput pubackInboundOutput) {
        try {
            @NotNull final String clientId = pubackInboundInput.getClientInformation().getClientId();
            MessageLogUtil.logPuback(pubackInboundInput.getPubackPacket(), clientId, true, verbose, json);
        } catch (final Exception e) {
            log.debug("Exception thrown at inbound puback logging: ", e);
        }
    }
}
