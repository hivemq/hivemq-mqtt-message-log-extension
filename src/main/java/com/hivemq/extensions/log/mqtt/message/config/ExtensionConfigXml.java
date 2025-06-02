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


import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

/**
 * @since 1.2.0
 */
@SuppressWarnings({"FieldMayBeFinal", "FieldCanBeLocal", "unused"})
@XmlRootElement(name = "hivemq-mqtt-message-log-extension")
@XmlType(propOrder = {})
@XmlAccessorType(XmlAccessType.NONE)
public class ExtensionConfigXml implements ExtensionConfig {

    // these defaults must be kept in sync with those in ExtensionConfigReader
    @XmlElement(name = "verbose", defaultValue = "false")
    private boolean verbose = false;

    @XmlElement(name = "payload", defaultValue = "true")
    private boolean payload = true;

    @XmlElement(name = "passwordinverbose", defaultValue = "true")
    private boolean passwordinverbose = true;

    @XmlElement(name = "publish-received", defaultValue = "true")
    private boolean publishReceived = true;

    @XmlElement(name = "publish-send", defaultValue = "true")
    private boolean publishSend = true;

    @XmlElement(name = "client-connect", defaultValue = "true")
    private boolean clientConnect = true;

    @XmlElement(name = "connack-send", defaultValue = "true")
    private boolean connackSend = true;

    @XmlElement(name = "client-disconnect", defaultValue = "true")
    private boolean clientDisconnect = true;

    @XmlElement(name = "subscribe-received", defaultValue = "true")
    private boolean subscribeReceived = true;

    @XmlElement(name = "suback-send", defaultValue = "true")
    private boolean subackSend = true;

    @XmlElement(name = "unsubscribe-received", defaultValue = "true")
    private boolean unsubscribeReceived = true;

    @XmlElement(name = "unsuback-send", defaultValue = "true")
    private boolean unsubackSend = true;

    @XmlElement(name = "ping-request-received", defaultValue = "true")
    private boolean pingRequestReceived = true;

    @XmlElement(name = "ping-response-send", defaultValue = "true")
    private boolean pingResponseSend = true;

    @XmlElement(name = "puback-received", defaultValue = "true")
    private boolean pubackReceived = true;

    @XmlElement(name = "puback-send", defaultValue = "true")
    private boolean pubackSend = true;

    @XmlElement(name = "pubrec-received", defaultValue = "true")
    private boolean pubrecReceived = true;

    @XmlElement(name = "pubrec-send", defaultValue = "true")
    private boolean pubrecSend = true;

    @XmlElement(name = "pubrel-received", defaultValue = "true")
    private boolean pubrelReceived = true;

    @XmlElement(name = "pubrel-send", defaultValue = "true")
    private boolean pubrelSend = true;

    @XmlElement(name = "pubcomp-received", defaultValue = "true")
    private boolean pubcompReceived = true;

    @XmlElement(name = "pubcomp-send", defaultValue = "true")
    private boolean pubcompSend = true;

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isPayload() {
        return payload;
    }

    public boolean isPasswordInVerbose() {
        return passwordinverbose;
    }

    public boolean isPublishReceived() {
        return publishReceived;
    }

    public boolean isPublishSend() {
        return publishSend;
    }

    public boolean isClientConnect() {
        return clientConnect;
    }

    public boolean isConnackSend() {
        return connackSend;
    }

    public boolean isClientDisconnect() {
        return clientDisconnect;
    }

    public boolean isSubscribeReceived() {
        return subscribeReceived;
    }

    public boolean isSubackSend() {
        return subackSend;
    }

    public boolean isUnsubscribeReceived() {
        return unsubscribeReceived;
    }

    public boolean isUnsubackSend() {
        return unsubackSend;
    }

    public boolean isPingRequestReceived() {
        return pingRequestReceived;
    }

    public boolean isPingResponseSend() {
        return pingResponseSend;
    }

    public boolean isPubackReceived() {
        return pubackReceived;
    }

    public boolean isPubackSend() {
        return pubackSend;
    }

    public boolean isPubrecReceived() {
        return pubrecReceived;
    }

    public boolean isPubrecSend() {
        return pubrecSend;
    }

    public boolean isPubrelReceived() {
        return pubrelReceived;
    }

    public boolean isPubrelSend() {
        return pubrelSend;
    }

    public boolean isPubcompReceived() {
        return pubcompReceived;
    }

    public boolean isPubcompSend() {
        return pubcompSend;
    }

    @Override
    public String toString() {
        return "{" +
                "verbose=" +
                verbose +
                ", payload=" +
                payload +
                ", passwordInVerbose=" +
                passwordinverbose +
                ", publishReceived=" +
                publishReceived +
                ", publishSend=" +
                publishSend +
                ", clientConnect=" +
                clientConnect +
                ", connackSend=" +
                connackSend +
                ", clientDisconnect=" +
                clientDisconnect +
                ", subscribeReceived=" +
                subscribeReceived +
                ", subackSend=" +
                subackSend +
                ", unsubscribeReceived=" +
                unsubscribeReceived +
                ", unsubackSend=" +
                unsubackSend +
                ", pingRequestReceived=" +
                pingRequestReceived +
                ", pingResponseSend=" +
                pingResponseSend +
                ", pubackReceived=" +
                pubackReceived +
                ", pubackSend=" +
                pubackSend +
                ", pubrecReceived=" +
                pubrecReceived +
                ", pubrecSend=" +
                pubrecSend +
                ", pubrelReceived=" +
                pubrelReceived +
                ", pubrelSend=" +
                pubrelSend +
                ", pubcompReceived=" +
                pubcompReceived +
                ", pubcompSend=" +
                pubcompSend +
                '}';
    }
}
