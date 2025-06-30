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

/**
 * @since 1.2.0
 */
public interface ExtensionConfig {

    boolean isClientConnect();

    boolean isClientDisconnect();

    boolean isConnackSend();

    boolean isPublishReceived();

    boolean isPublishSend();

    boolean isSubscribeReceived();

    boolean isSubackSend();

    boolean isUnsubscribeReceived();

    boolean isUnsubackSend();

    boolean isPingRequestReceived();

    boolean isPingResponseSend();

    boolean isPubackReceived();

    boolean isPubackSend();

    boolean isPubrelReceived();

    boolean isPubrelSend();

    boolean isPubrecReceived();

    boolean isPubrecSend();

    boolean isPubcompReceived();

    boolean isPubcompSend();

    boolean isVerbose();

    boolean isPayload();

    boolean isPasswordInVerbose();

    default boolean allDisabled() {
        return !isClientConnect() &&
                !isClientDisconnect() &&
                !isConnackSend() &&
                !isPublishSend() &&
                !isPublishReceived() &&
                !isSubscribeReceived() &&
                !isSubackSend() &&
                !isUnsubscribeReceived() &&
                !isUnsubackSend() &&
                !isPingRequestReceived() &&
                !isPingResponseSend() &&
                !isPubackReceived() &&
                !isPubackSend() &&
                !isPubrecReceived() &&
                !isPubrecSend() &&
                !isPubrelReceived() &&
                !isPubrelSend() &&
                !isPubcompReceived() &&
                !isPubcompSend();
    }

}
