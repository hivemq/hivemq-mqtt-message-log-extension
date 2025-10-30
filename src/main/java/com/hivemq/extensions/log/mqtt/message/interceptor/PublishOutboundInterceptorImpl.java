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

import com.hivemq.extension.sdk.api.interceptor.publish.PublishOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundOutput;
import com.hivemq.extensions.log.mqtt.message.MessageLogger;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.0.0
 */
public class PublishOutboundInterceptorImpl implements PublishOutboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(PublishOutboundInterceptorImpl.class);

    private final @NotNull MessageLogger messageLogger;

    public PublishOutboundInterceptorImpl(final @NotNull MessageLogger messageLogger) {
        this.messageLogger = messageLogger;
    }

    @Override
    public void onOutboundPublish(
            final @NotNull PublishOutboundInput publishOutboundInput,
            final @NotNull PublishOutboundOutput publishOutboundOutput) {
        try {
            final var clientID = publishOutboundInput.getClientInformation().getClientId();
            messageLogger.logPublish(String.format("Sent PUBLISH to client '%s' on topic", clientID),
                    publishOutboundInput.getPublishPacket());
        } catch (final Exception e) {
            LOG.debug("Exception thrown at outbound publish logging: ", e);
        }
    }
}
