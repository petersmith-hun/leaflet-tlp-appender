package hu.psprog.leaflet.tlp.appender.domain;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyVO;

import java.util.Objects;

/**
 * Logging event VO that contains only the required fields.
 *
 * @author Peter Smith
 */
public class OptimizedLoggingEventVO {

    private String threadName;
    private String loggerName;
    private Level level;
    private String formattedMessage;
    private ThrowableProxyVO throwableProxy;
    private long timeStamp;
    private String source;

    public static OptimizedLoggingEventVO build(ILoggingEvent le, String appID) {

        OptimizedLoggingEventVO event = new OptimizedLoggingEventVO();
        event.threadName = le.getThreadName();
        event.loggerName = le.getLoggerName();
        event.level = le.getLevel();
        event.formattedMessage = le.getFormattedMessage();
        event.throwableProxy = ThrowableProxyVO.build(le.getThrowableProxy());
        event.timeStamp = le.getTimeStamp();
        event.source = appID;

        return event;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getLoggerName() {
        return loggerName;
    }

    public Level getLevel() {
        return level;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public ThrowableProxyVO getThrowableProxy() {
        return throwableProxy;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public String getSource() {
        return source;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OptimizedLoggingEventVO that = (OptimizedLoggingEventVO) o;
        return timeStamp == that.timeStamp &&
                Objects.equals(threadName, that.threadName) &&
                Objects.equals(loggerName, that.loggerName) &&
                Objects.equals(level, that.level) &&
                Objects.equals(formattedMessage, that.formattedMessage) &&
                Objects.equals(throwableProxy, that.throwableProxy) &&
                Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        return Objects.hash(threadName, loggerName, level, formattedMessage, throwableProxy, timeStamp, source);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("OptimizedLoggingEventVO{");
        sb.append("threadName='").append(threadName).append('\'');
        sb.append(", loggerName='").append(loggerName).append('\'');
        sb.append(", level=").append(level);
        sb.append(", formattedMessage='").append(formattedMessage).append('\'');
        sb.append(", throwableProxy=").append(throwableProxy);
        sb.append(", timeStamp=").append(timeStamp);
        sb.append(", source='").append(source).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
