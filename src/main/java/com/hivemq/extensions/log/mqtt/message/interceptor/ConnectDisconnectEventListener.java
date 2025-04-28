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

import com.hivemq.extension.sdk.api.events.client.ClientLifecycleEventListener;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationFailedInput;
import com.hivemq.extension.sdk.api.events.client.parameters.AuthenticationSuccessfulInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ClientInitiatedDisconnectInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionLostInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ConnectionStartInput;
import com.hivemq.extension.sdk.api.events.client.parameters.DisconnectEventInput;
import com.hivemq.extension.sdk.api.events.client.parameters.ServerInitiatedDisconnectInput;
import com.hivemq.extension.sdk.api.packets.connect.ConnectPacket;
import com.hivemq.extensions.log.mqtt.message.util.MessageLogUtil;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @since 1.0.0
 */
public class ConnectDisconnectEventListener implements ClientLifecycleEventListener {

    private static final @NotNull Logger LOG = LoggerFactory.getLogger(ConnectDisconnectEventListener.class);

    private final boolean logConnect;
    private final boolean verbose;
    private final boolean payload;
    private final boolean passwordInVerbose;

    public ConnectDisconnectEventListener(final boolean logConnect, final boolean verbose, final boolean payload,
                                          final boolean passwordInVerbose) {
        this.logConnect = logConnect;
        this.verbose = verbose;
        this.payload = payload;
        this.passwordInVerbose = passwordInVerbose;
    }

    @Override
    public void onMqttConnectionStart(final @NotNull ConnectionStartInput connectionStartInput) {
        if (!logConnect) {
            return;
        }
        try {
            final ConnectPacket connectPacket = connectionStartInput.getConnectPacket();
            MessageLogUtil.logConnect(connectPacket, verbose, payload, passwordInVerbose);
        } catch (final Exception e) {
            LOG.debug("Exception thrown at inbound connect logging: ", e);
        }
    }

    @Override
    public void onAuthenticationSuccessful(final @NotNull AuthenticationSuccessfulInput authenticationSuccessfulInput) {
        //NOOP
    }

    @Override
    public void onDisconnect(final @NotNull DisconnectEventInput disconnectEventInput) {
        //NOOP
    }

    @Override
    public void onAuthenticationFailedDisconnect(final @NotNull AuthenticationFailedInput authenticationFailedInput) {
        MessageLogUtil.logDisconnect(String.format("Sent DISCONNECT to client '%s' because authentication failed.",
                authenticationFailedInput.getClientInformation().getClientId()), authenticationFailedInput, verbose);
    }

    @Override
    public void onConnectionLost(final @NotNull ConnectionLostInput connectionLostInput) {
        //NOOP since no mqtt message is sent.
    }

    @Override
    public void onClientInitiatedDisconnect(final @NotNull ClientInitiatedDisconnectInput clientInitiatedDisconnectInput) {
        MessageLogUtil.logDisconnect(String.format("Received DISCONNECT from client '%s':",
                        clientInitiatedDisconnectInput.getClientInformation().getClientId()),
                clientInitiatedDisconnectInput,
                verbose);
    }

    @Override
    public void onServerInitiatedDisconnect(final @NotNull ServerInitiatedDisconnectInput serverInitiatedDisconnectInput) {
        MessageLogUtil.logDisconnect(String.format("Sent DISCONNECT to client '%s':",
                        serverInitiatedDisconnectInput.getClientInformation().getClientId()),
                serverInitiatedDisconnectInput,
                verbose);
    }
}
