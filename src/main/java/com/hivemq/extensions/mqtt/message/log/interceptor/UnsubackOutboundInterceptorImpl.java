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
import com.hivemq.extension.sdk.api.interceptor.unsuback.UnsubackOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.unsuback.parameter.UnsubackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.unsuback.parameter.UnsubackOutboundOutput;
import com.hivemq.extensions.mqtt.message.log.util.MessageLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Walter
 * @since 1.1.0
 */
class UnsubackOutboundInterceptorImpl implements UnsubackOutboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(UnsubackOutboundInterceptorImpl.class);
    private final boolean verbose;

    UnsubackOutboundInterceptorImpl(final boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void onOutboundUnsuback(
            final @NotNull UnsubackOutboundInput unsubackOutboundInput,
            final @NotNull UnsubackOutboundOutput unsubackOutboundOutput) {
        try {
            MessageLogUtil.logUnsuback(unsubackOutboundInput, verbose);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at outbound unsuback logging: ", e);
        }
    }
}