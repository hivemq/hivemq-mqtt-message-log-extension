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
package com.hivemq.extensions.log.mqtt.message.logger;

import org.jetbrains.annotations.NotNull;

/**
 * Factory for creating MessageLogger instances based on the desired format.
 *
 * @since 1.3.0
 */
public class MessageLoggerFactory {

    /**
     * Creates an MessageLogger instance for the specified format.
     *
     * @param format the desired output format
     * @return a MessageLogger implementation
     */
    public static @NotNull MessageLogger createLogger(
            final boolean verbose,
            final boolean payload,
            final boolean redactPassword,
            final @NotNull OutputFormat format) {
        if (format == OutputFormat.JSON) {
            return new JsonMessageLogger(verbose, payload, redactPassword);
        } else {
            return new PlainTextMessageLogger(verbose, payload, redactPassword);
        }
    }
}
