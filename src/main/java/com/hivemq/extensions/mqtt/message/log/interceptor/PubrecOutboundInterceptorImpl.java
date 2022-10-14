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
package com.hivemq.extensions.mqtt.message.log.interceptor;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.pubrec.PubrecOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.pubrec.parameter.PubrecOutboundOutput;
import com.hivemq.extensions.mqtt.message.log.MessageLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Walter
 * @since 1.1.0
 */
class PubrecOutboundInterceptorImpl implements PubrecOutboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(PubrecOutboundInterceptorImpl.class);
    private final boolean verbose;

    PubrecOutboundInterceptorImpl(final boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void onOutboundPubrec(
            final @NotNull PubrecOutboundInput pubrecOutboundInput,
            final @NotNull PubrecOutboundOutput pubrecOutboundOutput) {
        try {
            final String clientId = pubrecOutboundInput.getClientInformation().getClientId();
            MessageLogUtil.logPubrec(pubrecOutboundInput.getPubrecPacket(), clientId, false, verbose);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at outbound pubrec logging: ", e);
        }
    }
}
