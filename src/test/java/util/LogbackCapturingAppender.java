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
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.annotations.Nullable;

/**
 * @author Florian Limpöck
 */
public class LogbackCapturingAppender extends AppenderBase<ILoggingEvent> {
    public static class Factory {

        public static LogbackCapturingAppender weaveInto(final @NotNull org.slf4j.Logger sl4jLogger) {
            return new LogbackCapturingAppender(sl4jLogger);
        }
    }

    private final @NotNull Logger log;
    private @Nullable ILoggingEvent captured;

    public LogbackCapturingAppender(final @NotNull org.slf4j.Logger sl4jLogger) {
        this.log = (Logger) sl4jLogger;
        addAppender(log);
        detachDefaultConsoleAppender();
    }

    private void detachDefaultConsoleAppender() {
        final Logger rootLogger = getRootLogger();
        final Appender<ILoggingEvent> consoleAppender = rootLogger.getAppender("console");
        rootLogger.detachAppender(consoleAppender);
    }

    private @NotNull Logger getRootLogger() {
        return log.getLoggerContext().getLogger("ROOT");
    }

    private void addAppender(final Logger logger) {
        logger.setLevel(Level.ALL);
        logger.addAppender(this);
        this.start();
    }

    public @Nullable ILoggingEvent getLastCapturedLog() {
        return captured;
    }

    @Override
    protected void append(final @NotNull ILoggingEvent iLoggingEvent) {
        captured = iLoggingEvent;
    }
}
