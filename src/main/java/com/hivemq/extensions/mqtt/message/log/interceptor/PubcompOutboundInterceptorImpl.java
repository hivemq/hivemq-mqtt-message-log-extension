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

package com.hivemq.extensions.mqtt.message.log.interceptor;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.pubcomp.PubcompOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubcomp.parameter.PubcompOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.pubcomp.parameter.PubcompOutboundOutput;
import com.hivemq.extensions.mqtt.message.log.MessageLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Walter
 * @since 1.1.0
 */
class PubcompOutboundInterceptorImpl implements PubcompOutboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(PubcompOutboundInterceptorImpl.class);
    private final boolean verbose;

    PubcompOutboundInterceptorImpl(final boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void onOutboundPubcomp(
            final @NotNull PubcompOutboundInput pubcompOutboundInput,
            final @NotNull PubcompOutboundOutput pubcompOutboundOutput) {
        try {
            final String clientId = pubcompOutboundInput.getClientInformation().getClientId();
            MessageLogUtil.logPubcomp(pubcompOutboundInput.getPubcompPacket(), clientId, false, verbose);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at outbound pubcomp logging: ", e);
        }
    }
}
