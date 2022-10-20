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
import com.hivemq.extension.sdk.api.interceptor.pingresp.PingRespOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.pingresp.parameter.PingRespOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.pingresp.parameter.PingRespOutboundOutput;
import com.hivemq.extensions.log.mqtt.util.MessageLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Michael Walter
 * @since 1.1.0
 */
class PingrespOutboundInterceptorImpl implements PingRespOutboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(PingrespOutboundInterceptorImpl.class);

    @Override
    public void onOutboundPingResp(
            final @NotNull PingRespOutboundInput pingRespOutboundInput,
            final @NotNull PingRespOutboundOutput pingRespOutboundOutput) {
        try {
            MessageLogUtil.logPingresp(pingRespOutboundInput);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at outbound ping response logging: ", e);
        }
    }
}
