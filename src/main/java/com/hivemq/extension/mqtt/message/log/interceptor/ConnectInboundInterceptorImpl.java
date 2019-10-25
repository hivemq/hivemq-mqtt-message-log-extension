/*
 * Copyright 2019 dc-square GmbH
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
import com.hivemq.extension.sdk.api.interceptor.connect.ConnectInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInboundInput;
import com.hivemq.extension.sdk.api.interceptor.connect.parameter.ConnectInboundOutput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
public class ConnectInboundInterceptorImpl implements ConnectInboundInterceptor {

    @NotNull
    private static final Logger log = LoggerFactory.getLogger(ConnectInboundInterceptorImpl.class);

    @Override
    public void onConnect(final @NotNull ConnectInboundInput connectInboundInput, final @NotNull ConnectInboundOutput connectInboundOutput) {
        try {
            final ConnectPacket connectPacket = connectInboundInput.getConnectPacket();
            MessageLogUtil.logConnect(connectPacket);
            connectPacket.getWillPublish()
                    .ifPresent(willPublishPacket -> MessageLogUtil.logWill(willPublishPacket, connectPacket.getClientId()));
        } catch (final Exception e) {
            log.debug("Exception thrown at inbound connect logging: ", e);
        }
    }
}
