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

import com.hivemq.extension.sdk.api.interceptor.pubrel.PubrelOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pubrel.parameter.PubrelOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.pubrel.parameter.PubrelOutboundOutput;
import com.hivemq.extensions.log.mqtt.message.util.MessageLogUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Walter
 * @since 1.1.0
 */
class PubrelOutboundInterceptorImpl implements PubrelOutboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(PubrelOutboundInterceptorImpl.class);
    private final boolean verbose;

    PubrelOutboundInterceptorImpl(final boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void onOutboundPubrel(
            final @NotNull PubrelOutboundInput pubrelOutboundInput,
            final @NotNull PubrelOutboundOutput pubrelOutboundOutput) {
        try {
            final String clientId = pubrelOutboundInput.getClientInformation().getClientId();
            MessageLogUtil.logPubrel(pubrelOutboundInput.getPubrelPacket(), clientId, false, verbose);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at outbound pubrel logging: ", e);
        }
    }
}
