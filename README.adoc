:hivemq-link: http://www.hivemq.com
:hivemq-extension-docs-link: http://www.hivemq.com/docs/extensions/latest/
:hivemq-extension-docs-archetype-link: http://www.hivemq.com/docs/extensions/latest/#maven-archetype-chapter
:hivemq-blog-tools: http://www.hivemq.com/mqtt-toolbox
:maven-documentation-profile-link: http://maven.apache.org/guides/introduction/introduction-to-profiles.html
:hivemq-support: http://www.hivemq.com/support/

== HiveMQ Mqtt Message Log Extension

*Type*: Logging Extension

*Version*: 1.1.0

*License*: Apache License Version 2.0

=== Prerequisites

* HiveMQ Enterprise Edition (EE) 4.2.0 or later
* HiveMQ Community Edition (CE) 2020.1 or later

=== Purpose

The HiveMQ MQTT Message Log Extension is a very useful HiveMQ Extension for debugging and development purposes. It provides the possibility to follow up on any clients communicating with the broker on the terminal.

*The extension logs the following events:*

[cols="6,2,2"]
|===
|Event | Config Property | Minimum Version

|A client connects to HiveMQ | client-connect | 4.2 EE or 2020.1 CE
|A client disconnects from HiveMQ | client-disconnect | 4.2 EE or 2020.1 CE
|A client sends a publish message | publish-received | 4.2 EE or 2020.1 CE
|A client sends a subscribe message | subscribe-received |4.2 EE or 2020.1 CE
|HiveMQ sends a publish message to a client | publish-send |4.2 EE or 2020.1 CE
|A client sends a unsubscribe message | unsubscribe-received | 4.3 EE or 2020.1 CE
|A client send a ping request | ping-request-received | 4.3 EE or 2020.1 CE
|A client completes a received Qos1 publish with a puback message | puback-received | 4.3 EE or 2020.1 CE
|A client acknowledges the reception of a Qos2 publish with a pubrec message | pubrec-received | 4.3 EE or 2020.1 CE
|A client completes a sent Qos2 publish with a pubrel message | pubrel-received | 4.3 EE or 2020.1 CE
|A client completes a received Qos2 publish with a pubcomp message | pubcomp-received | 4.3 EE or 2020.1 CE
|HiveMQ sends a connack message to a client | connack-send | 4.3 EE or 2020.1 CE
|HiveMQ disconnects a client with a disconnect message | client-disconnect | 4.3 EE or 2020.1 CE
|HiveMQ sends a suback message to a client | suback-send | 4.3 EE or 2020.1 CE
|HiveMQ sends a unsuback message to a client | unsuback-send | 4.3 EE or 2020.1 CE
|HiveMQ sends a ping response to a client | ping-response-send | 4.3 EE or 2020.1 CE
|HiveMQ completes a received Qos1 publish with a puback message | puback-send | 4.3 EE or 2020.1 CE
|HiveMQ acknowledges the reception of a Qos2 publish with a pubrec message | pubrec-send | 4.3 EE or 2020.1 CE
|HiveMQ completes a sent Qos2 publish with a pubrel message | pubrel-send | 4.3 EE or 2020.1 CE
|HiveMQ completes a received Qos2 publish with a pubcomp message | pubcomp-send | 4.3 EE or 2020.1 CE

|===

=== Installation

. Download https://www.hivemq.com/releases/extensions/hivemq-mqtt-message-log-extension-1.1.0.zip[HiveMQ Mqtt Message Log Extension]
.. Move the file: "hivemq-mqtt-message-log-extension-1.1.0.zip" to the directory: "HIVEMQ_HOME/extensions"
. Or clone this repository into a Java 11 maven project
.. run `mvn package` goal from Maven to build the extension.
.. Move the file: "hivemq-mqtt-message-log-extension-1.1.0-distribution.zip" to the directory: "HIVEMQ_HOME/extensions"
. Unzip the file.
. Start HiveMQ.

=== Configuration

By default all MQTT events are logged. It is possible to opt out of specific log event types by adding a
*mqttMessageLog.properties* files to your `<HIVEMQ_HOME>/hivemq-mqtt-message-log-extension`  folder.
There is an example file available which removes the logging of publish messages. Just rename it to *mqttMessageLog.properties*.

The events log only important information, if it required that MQTT messages are logging all information available the `verbose` property can be set to true.
You can see the difference of a normal log versus a verbose log in the <<example, examples>>.

CAUTION: use verbose=true very carefully as it will flood your log immediately.

Missing entries default to true. *verbose* defaults to false.

=== First Steps

Connect with an {hivemq-blog-tools}[MQTT client] of your choice. You should see a log message with its client identifier, MQTT version, clean start flag and session expiry interval.

[[example]]
=== Examples

==== Verbose CONNECT message
[source,bash]
----
"17:26:23.602 INFO - Received CONNECT from client 'clientid': Protocol version: 'V_5', Clean Start: 'false',
Session Expiry Interval: '10000', Keep Alive: '20000', Maximum Packet Size: '40000',
Receive Maximum: '30000', Topic Alias Maximum: '50000', Request Problem Information: 'true',
Request Response Information: 'false',  Username: 'the username', Password: 'the password',
Auth Method: 'auth method', Auth Data (Base64): 'YXV0aCBkYXRh',
User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1'],
Will: { Topic: 'willtopic', Payload: 'payload', QoS: '1', Retained: 'false', Message Expiry Interval: '1234',
Duplicate Delivery: 'false', Correlation Data: 'data', Response Topic: 'response topic',
Content Type: 'content type', Payload Format Indicator: 'UTF_8', Subscription Identifiers: '[1, 2, 3, 4]',
User Properties: [Name: 'name0', Value: 'value0'], [Name: 'name1', Value: 'value1'],
[Name: 'name2', Value: 'value2'], Will Delay: '100' }"
----

==== Basic CONNECT message
[source,bash]
----
"17:26:23.602 INFO - Received CONNECT from client 'clientid': Protocol version: 'V_5', Clean Start: 'false', Session Expiry Interval: '10000'"
----

== Contributing

If you want to contribute to HiveMQ Mqtt Message Log Extension, see the link:CONTRIBUTING.md[contribution guidelines].

== License

HiveMQ Mqtt Message Log Extension is licensed under the `APACHE LICENSE, VERSION 2.0`. A copy of the license can be found link:LICENSE.txt[here].
