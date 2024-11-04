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

import com.hivemq.extension.sdk.api.interceptor.pubrec.PubrecInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecInboundInput;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecInboundOutput;
import com.hivemq.extensions.log.mqtt.message.util.MessageLogUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1.0
 */
public class PubrecInboundInterceptorImpl implements PubrecInboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(PubrecInboundInterceptorImpl.class);
    private final boolean verbose;
    private final boolean json;

    public PubrecInboundInterceptorImpl(final boolean verbose, final boolean json) {
        this.verbose = verbose;
        this.json = json;
    }

    @Override
    public void onInboundPubrec(
            final @NotNull PubrecInboundInput pubrecInboundInput,
            final @NotNull PubrecInboundOutput pubrecInboundOutput) {
        try {
            final String clientId = pubrecInboundInput.getClientInformation().getClientId();
            MessageLogUtil.logPubrec(pubrecInboundInput.getPubrecPacket(), clientId, true, verbose, json);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at inbound pubrec logging: ", e);
        }
    }
}
