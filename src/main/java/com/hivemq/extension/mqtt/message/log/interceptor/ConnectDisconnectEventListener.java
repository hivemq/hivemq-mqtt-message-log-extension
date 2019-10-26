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
import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.*;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Florian Limpöck
 * @since 1.0.0
 */
public class ConnectDisconnectEventListener implements ClientLifecycleEventListener {

    @NotNull
    private static final Logger log = LoggerFactory.getLogger(ConnectDisconnectEventListener.class);

    private final boolean logConnect;
    private final boolean verbose;

    public ConnectDisconnectEventListener(final boolean logConnect, final boolean verbose) {
        this.logConnect = logConnect;
        this.verbose = verbose;
    }

    @Override
    public void onMqttConnectionStart(@NotNull final ConnectionStartInput connectionStartInput) {
        if(!logConnect){
            return;
        }
        try {
            final ConnectPacket connectPacket = connectionStartInput.getConnectPacket();
            MessageLogUtil.logConnect(connectPacket, verbose);
        } catch (final Exception e){
            log.debug("Exception thrown at inbound connect logging: ", e);
        }

    }

    @Override
    public void onAuthenticationSuccessful(@NotNull final AuthenticationSuccessfulInput authenticationSuccessfulInput) {
        //NOOP
    }

    @Override
    public void onDisconnect(final @NotNull DisconnectEventInput disconnectEventInput) {
        //NOOP
    }

    @Override
    public void onAuthenticationFailedDisconnect(@NotNull final AuthenticationFailedInput authenticationFailedInput) {
        MessageLogUtil.logDisconnect(String.format("Sent DISCONNECT to client '%s' because authentication failed.", authenticationFailedInput.getClientInformation().getClientId()), authenticationFailedInput, verbose);
    }

    @Override
    public void onConnectionLost(@NotNull final ConnectionLostInput connectionLostInput) {
        //NOOP since no mqtt message is sent.
    }

    @Override
    public void onClientInitiatedDisconnect(@NotNull final ClientInitiatedDisconnectInput clientInitiatedDisconnectInput) {
        MessageLogUtil.logDisconnect(String.format("Received DISCONNECT from client '%s'.", clientInitiatedDisconnectInput.getClientInformation().getClientId()), clientInitiatedDisconnectInput, verbose);
    }

    @Override
    public void onServerInitiatedDisconnect(@NotNull final ServerInitiatedDisconnectInput serverInitiatedDisconnectInput) {
        MessageLogUtil.logDisconnect(String.format("Sent DISCONNECT to client '%s'.", serverInitiatedDisconnectInput.getClientInformation().getClientId()), serverInitiatedDisconnectInput, verbose);
    }
}
