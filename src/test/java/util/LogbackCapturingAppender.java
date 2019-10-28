package util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.classic.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Florian Limpöck
 */
public class LogbackCapturingAppender extends AppenderBase<ILoggingEvent> {
    public static class Factory {
        private static final List<LogbackCapturingAppender> ALL = new ArrayList<>();

        public static LogbackCapturingAppender weaveInto(final org.slf4j.Logger sl4jLogger) {
            LogbackCapturingAppender appender = new LogbackCapturingAppender(sl4jLogger);
            ALL.add(appender);
            return appender;
        }
    }

    private final Logger log;
    private ILoggingEvent captured;

    private final List<ILoggingEvent> allCaptured = new ArrayList<>();

    public LogbackCapturingAppender(final org.slf4j.Logger sl4jLogger) {
        this.log = (Logger) sl4jLogger;
        addAppender(log);
        detachDefaultConsoleAppender();
    }

    private void detachDefaultConsoleAppender() {
        final Logger rootLogger = getRootLogger();
        final Appender<ILoggingEvent> consoleAppender = rootLogger.getAppender("console");
        rootLogger.detachAppender(consoleAppender);
    }

    private Logger getRootLogger() {
        return log.getLoggerContext().getLogger("ROOT");
    }

    private void addAppender(final Logger logger) {
        logger.setLevel(Level.ALL);
        logger.addAppender(this);
        this.start();
    }

    public ILoggingEvent getLastCapturedLog() {
        return captured;
    }

    @Override
    protected void append(final ILoggingEvent iLoggingEvent) {
        allCaptured.add(iLoggingEvent);
        captured = iLoggingEvent;
    }
}