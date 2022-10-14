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
import com.hivemq.extension.sdk.api.interceptor.publish.PublishOutboundInterceptor;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundOutput;
import com.hivemq.extensions.mqtt.message.log.MessageLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
class PublishOutboundInterceptorImpl implements PublishOutboundInterceptor {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(PublishOutboundInterceptorImpl.class);
    private final boolean verbose;

    PublishOutboundInterceptorImpl(final boolean verbose) {
        this.verbose = verbose;
    }

    @Override
    public void onOutboundPublish(
            final @NotNull PublishOutboundInput publishOutboundInput,
            final @NotNull PublishOutboundOutput publishOutboundOutput) {
        try {
            final String clientID = publishOutboundInput.getClientInformation().getClientId();
            MessageLogUtil.logPublish(String.format("Sent PUBLISH to client '%s' on topic", clientID),
                    publishOutboundInput.getPublishPacket(),
                    verbose);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at outbound publish logging: ", e);
        }
    }
}
