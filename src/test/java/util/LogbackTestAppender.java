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
package util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.LinkedList;
import java.util.List;

public class LogbackTestAppender extends AppenderBase<ILoggingEvent> implements BeforeEachCallback, AfterEachCallback {

    public static @NotNull LogbackTestAppender createFor(final @NotNull org.slf4j.Logger logger) {
        return new LogbackTestAppender((Logger) logger);
    }

    private final @NotNull Logger logger;
    private final @NotNull Level initialLevel;
    private final @NotNull List<ILoggingEvent> events = new LinkedList<>();

    private LogbackTestAppender(final @NotNull Logger logger) {
        this.logger = logger;
        initialLevel = logger.getLevel();
    }

    public void attach() {
        logger.setLevel(Level.ALL);
        logger.addAppender(this);
        start();
    }

    public void detach() {
        logger.setLevel(initialLevel);
        logger.detachAppender(this);
        stop();
    }

    @Override
    public void beforeEach(final @NotNull ExtensionContext ignored) {
        attach();
    }

    @Override
    public void afterEach(final @NotNull ExtensionContext ignored) {
        detach();
    }

    @Override
    protected void append(final @NotNull ILoggingEvent e) {
        events.add(e);
    }

    public @NotNull List<ILoggingEvent> getEvents() {
        return events;
    }
}
