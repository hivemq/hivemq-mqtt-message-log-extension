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

package com.hivemq.extension.mqtt.message.log.config;

import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class MqttMessageLogConfig {

    @NotNull
    private static final Logger log = LoggerFactory.getLogger(MqttMessageLogConfig.class);

    @NotNull
    static final String TRUE = "true";
    @NotNull
    static final String FALSE = "false";
    @NotNull
    static final String VERBOSE = "verbose";
    @NotNull
    static final String CLIENT_CONNECT = "client-connect";
    @NotNull
    static final String CLIENT_DISCONNECT = "client-disconnect";
    @NotNull
    static final String PUBLISH_RECEIVED = "publish-received";
    @NotNull
    static final String PUBLISH_SEND = "publish-send";
    @NotNull
    static final String SUBSCRIBE_RECEIVED = "subscribe-received";
    @NotNull
    static final String SUBACK_SEND = "suback-send";
    @NotNull
    static final String UNSUBSCRIBE_RECEIVED = "unsubscribe-received";
    @NotNull
    static final String UNSUBACK_SEND = "unsuback-send";
    @NotNull
    static final String PING_REQ_RECEIVED = "ping-request-received";
    @NotNull
    static final String PING_RESP_SEND = "ping-response-send";
    @NotNull
    static final String PUBACK_RECEIVED = "puback-received";
    @NotNull
    static final String PUBACK_SEND = "puback-send";
    @NotNull
    static final String PUBREC_RECEIVED = "pubrec-received";
    @NotNull
    static final String PUBREC_SEND = "pubrec-send";
    @NotNull
    static final String PUBREL_RECEIVED = "pubrel-received";
    @NotNull
    static final String PUBREL_SEND = "pubrel-send";
    @NotNull
    static final String PUBCOMP_RECEIVED = "pubcomp-received";
    @NotNull
    static final String PUBCOMP_SEND = "pubcomp-send";

    @NotNull
    private final Properties properties;

    public MqttMessageLogConfig(final @NotNull Properties properties) {
        this.properties = properties;
    }

    public boolean isClientConnect() {
        return getForKey(CLIENT_CONNECT);
    }

    public boolean isClientDisconnect() {
        return getForKey(CLIENT_DISCONNECT);
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

    public boolean isPingreqReceived() {
        return getForKey(PING_REQ_RECEIVED);
    }

    public boolean isPingrespSend() {
        return getForKey(PING_RESP_SEND);
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

    public boolean allDisabled() {
        return !isClientConnect() &&
                !isClientDisconnect() &&
                !isPublishSend() &&
                !isPublishReceived() &&
                !isSubscribeReceived() &&
                !isSubackSend() &&
                !isUnsubscribeReceived() &&
                !isUnsubackSend() &&
                !isPingreqReceived() &&
                !isPingrespSend() &&
                !isPubackReceived() &&
                !isPubackSend() &&
                !isPubrecReceived() &&
                !isPubrecSend() &&
                !isPubrelReceived() &&
                !isPubrelSend() &&
                !isPubcompReceived() &&
                !isPubcompSend();
    }

    private boolean getForKey(final @NotNull String key) {
        return properties.getProperty(key, TRUE).equalsIgnoreCase(TRUE);
    }
}
