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

import com.hivemq.extension.sdk.api.interceptor.publish.PublishInboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishInboundOutput;
import com.hivemq.extensions.log.mqtt.message.util.MessageLogUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.0.0
 */
public class PublishInboundInterceptorImpl implements PublishInboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(PublishInboundInterceptorImpl.class);

    private final boolean verbose;
    private final boolean payload;

    public PublishInboundInterceptorImpl(final boolean verbose, final boolean payload) {
        this.verbose = verbose;
        this.payload = payload;
    }

    @Override
    public void onInboundPublish(
            final @NotNull PublishInboundInput publishInboundInput,
            final @NotNull PublishInboundOutput publishInboundOutput) {
        try {
            final var clientID = publishInboundInput.getClientInformation().getClientId();
            MessageLogUtil.logPublish(String.format("Received PUBLISH from client '%s' for topic", clientID),
                    publishInboundInput.getPublishPacket(),
                    verbose,
                    payload);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at inbound publish logging: ", e);
        }
    }
}
