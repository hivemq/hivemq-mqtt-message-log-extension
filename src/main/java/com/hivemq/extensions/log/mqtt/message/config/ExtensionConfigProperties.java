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
package com.hivemq.extensions.log.mqtt.message.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;

import java.util.Properties;

public class ExtensionConfigProperties implements ExtensionConfig {

    static final @NotNull String TRUE = "true";
    static final @NotNull String FALSE = "false";
    static final @NotNull String VERBOSE = "verbose";
    static final @NotNull String PAYLOAD = "payload";
    static final @NotNull String PASSWORDINVERBOSE = "passwordinverbose";
    static final @NotNull String CLIENT_CONNECT = "client-connect";
    static final @NotNull String CLIENT_DISCONNECT = "client-disconnect";
    static final @NotNull String CONNACK_SEND = "connack-send";
    static final @NotNull String PUBLISH_RECEIVED = "publish-received";
    static final @NotNull String PUBLISH_SEND = "publish-send";
    static final @NotNull String SUBSCRIBE_RECEIVED = "subscribe-received";
    static final @NotNull String SUBACK_SEND = "suback-send";
    static final @NotNull String UNSUBSCRIBE_RECEIVED = "unsubscribe-received";
    static final @NotNull String UNSUBACK_SEND = "unsuback-send";
    static final @NotNull String PING_REQUEST_RECEIVED = "ping-request-received";
    static final @NotNull String PING_RESPONSE_SEND = "ping-response-send";
    static final @NotNull String PUBACK_RECEIVED = "puback-received";
    static final @NotNull String PUBACK_SEND = "puback-send";
    static final @NotNull String PUBREC_RECEIVED = "pubrec-received";
    static final @NotNull String PUBREC_SEND = "pubrec-send";
    static final @NotNull String PUBREL_RECEIVED = "pubrel-received";
    static final @NotNull String PUBREL_SEND = "pubrel-send";
    static final @NotNull String PUBCOMP_RECEIVED = "pubcomp-received";
    static final @NotNull String PUBCOMP_SEND = "pubcomp-send";

    private final @NotNull Properties properties;

    public ExtensionConfigProperties(final @NotNull Properties properties) {
        this.properties = properties;
    }

    public boolean isClientConnect() {
        return getForKey(CLIENT_CONNECT);
    }

    public boolean isClientDisconnect() {
        return getForKey(CLIENT_DISCONNECT);
    }

    public boolean isConnackSend() {
        return getForKey(CONNACK_SEND);
    }

    public boolean isPublishReceived() {
        return getForKey(PUBLISH_RECEIVED);
    }

    public boolean isPublishSend() {
        return getForKey(PUBLISH_SEND);
    }

    public boolean isSubscribeReceived() {
        return getForKey(SUBSCRIBE_RECEIVED);
    }

    public boolean isSubackSend() {
        return getForKey(SUBACK_SEND);
    }

    public boolean isUnsubscribeReceived() {
        return getForKey(UNSUBSCRIBE_RECEIVED);
    }

    public boolean isUnsubackSend() {
        return getForKey(UNSUBACK_SEND);
    }

    public boolean isPingRequestReceived() {
        return getForKey(PING_REQUEST_RECEIVED);
    }

    public boolean isPingResponseSend() {
        return getForKey(PING_RESPONSE_SEND);
    }

    public boolean isPubackReceived() {
        return getForKey(PUBACK_RECEIVED);
    }

    public boolean isPubackSend() {
        return getForKey(PUBACK_SEND);
    }

    public boolean isPubrelReceived() {
        return getForKey(PUBREL_RECEIVED);
    }

    public boolean isPubrelSend() {
        return getForKey(PUBREL_SEND);
    }

    public boolean isPubrecReceived() {
        return getForKey(PUBREC_RECEIVED);
    }

    public boolean isPubrecSend() {
        return getForKey(PUBREC_SEND);
    }

    public boolean isPubcompReceived() {
        return getForKey(PUBCOMP_RECEIVED);
    }

    public boolean isPubcompSend() {
        return getForKey(PUBCOMP_SEND);
    }

    public boolean isVerbose() {
        return getForKey(VERBOSE);
    }

    public boolean isPayload() {
        return getForKey(PAYLOAD);
    }

    public boolean isPasswordInVerbose() {
        return getForKey(PASSWORDINVERBOSE);
    }

    private boolean getForKey(final @NotNull String key) {
        return properties.getProperty(key, TRUE).equalsIgnoreCase(TRUE);
    }

    @VisibleForTesting
    @NotNull Properties getProperties() {
        return properties;
    }
}
