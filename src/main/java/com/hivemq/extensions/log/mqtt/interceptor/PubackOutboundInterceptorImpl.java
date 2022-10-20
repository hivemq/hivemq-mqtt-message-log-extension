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
package com.hivemq.extensions.log.mqtt.interceptor;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.puback.PubackOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.puback.parameter.PubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.puback.parameter.PubackOutboundOutput;
import com.hivemq.extensions.log.mqtt.util.MessageLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Walter
 * @since 1.1.0
 */
class PubackOutboundInterceptorImpl implements PubackOutboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(PubackOutboundInterceptorImpl.class);
    private final boolean verbose;

    PubackOutboundInterceptorImpl(final boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void onOutboundPuback(
            final @NotNull PubackOutboundInput pubackOutboundInput,
            final @NotNull PubackOutboundOutput pubackOutboundOutput) {
        try {
            final String clientId = pubackOutboundInput.getClientInformation().getClientId();
            MessageLogUtil.logPuback(pubackOutboundInput.getPubackPacket(), clientId, false, verbose);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at outbound puback logging: ", e);
        }
    }
}
