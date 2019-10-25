/*
 * Copyright 2019 HiveMQ GmbH
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

    private boolean getForKey(final @NotNull String key) {
        final boolean keyEnabled = properties.getProperty(key, TRUE).equalsIgnoreCase(TRUE);
        log.debug("HiveMQ MQTT Message Log Extension: Setting {} to '{}'", key, keyEnabled);
        return keyEnabled;
    }
}
