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

import com.hivemq.extension.sdk.api.interceptor.pubrec.PubrecOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecOutboundOutput;
import com.hivemq.extensions.log.mqtt.message.util.MessageLogUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.1.0
 */
public class PubrecOutboundInterceptorImpl implements PubrecOutboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(PubrecOutboundInterceptorImpl.class);
    private final boolean verbose;
    private final boolean json;

    public PubrecOutboundInterceptorImpl(final boolean verbose, final boolean json) {
        this.verbose = verbose;
        this.json = json;
    }

    @Override
    public void onOutboundPubrec(
            final @NotNull PubrecOutboundInput pubrecOutboundInput,
            final @NotNull PubrecOutboundOutput pubrecOutboundOutput) {
        try {
            final String clientId = pubrecOutboundInput.getClientInformation().getClientId();
            MessageLogUtil.logPubrec(pubrecOutboundInput.getPubrecPacket(), clientId, false, verbose, json);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at outbound pubrec logging: ", e);
        }
    }
}
