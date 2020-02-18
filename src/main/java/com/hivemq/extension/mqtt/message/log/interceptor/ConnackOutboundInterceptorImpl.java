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

package com.hivemq.extension.mqtt.message.log.interceptor;

import com.hivemq.extension.mqtt.message.log.MessageLogUtil;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.connack.ConnackOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.connack.parameter.ConnackOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.connack.parameter.ConnackOutboundOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Walter
 * @since 1.1.0
 */
public class ConnackOutboundInterceptorImpl implements ConnackOutboundInterceptor {

    @NotNull
    private static final Logger log = LoggerFactory.getLogger(ConnackOutboundInterceptorImpl.class);
    private final boolean verbose;

    public ConnackOutboundInterceptorImpl(final boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void onOutboundConnack(@NotNull ConnackOutboundInput connackOutboundInput, @NotNull ConnackOutboundOutput connackOutboundOutput) {
        try {
            MessageLogUtil.logConnack(connackOutboundInput, verbose);
        } catch (final Exception e) {
            log.debug("Exception thrown at outbound connack logging: ", e);
        }
    }
}
