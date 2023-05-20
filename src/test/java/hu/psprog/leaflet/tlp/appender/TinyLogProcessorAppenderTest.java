package hu.psprog.leaflet.tlp.appender;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxyVO;
import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import hu.psprog.leaflet.tlp.appender.domain.OptimizedLoggingEventVO;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.net.URI;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

/**
 * Unit tests for {@link TinyLogProcessorAppender}.
 *
 * @author Peter Smith
 */
@ExtendWith(MockitoExtension.class)
public class TinyLogProcessorAppenderTest {

    private static final String TEST_APP = "test-app";
    private static final String HOST = "https://local.dev";
    private static final boolean ENABLED = true;
    private static final String EXPECTED_URI = HOST + "/logs";
    private static final String THREAD_NAME = "main";
    private static final String LOGGER_NAME = TinyLogProcessorAppender.class.getName();
    private static final Level LEVEL = Level.INFO;
    private static final String FORMATTED_LOG_MESSAGE = "Formatted log message";
    private static final ThrowableProxy THROWABLE_PROXY = new ThrowableProxy(new RuntimeException("test exception"));
    private static final long TIMESTAMP = System.currentTimeMillis();

    @Mock
    private WebTarget webTarget;

    @Mock
    private Invocation.Builder invocationBuilder;

    @Mock
    private ILoggingEvent loggingEvent;

    @InjectMocks
    private TinyLogProcessorAppender appender;

    @BeforeEach
    public void setup() {
        appender.setAppID(TEST_APP);
        appender.setEnabled(ENABLED);
        appender.setHost(HOST);
    }

    @Test
    public void shouldStartWithCreatingWebTarget() {

        // when
        appender.start();

        // then
        WebTarget result = readWebTargetValue();
        assertThat(result.getConfiguration().isRegistered(JacksonJsonProvider.class), is(true));
        assertThat(result.getUri(), equalTo(URI.create(EXPECTED_URI)));
    }

    @Test
    public void shouldPushLogWhenAppenderIsEnabled() {

        // given
        given(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).willReturn(invocationBuilder);
        given(loggingEvent.getThreadName()).willReturn(THREAD_NAME);
        given(loggingEvent.getLoggerName()).willReturn(LOGGER_NAME);
        given(loggingEvent.getLevel()).willReturn(LEVEL);
        given(loggingEvent.getFormattedMessage()).willReturn(FORMATTED_LOG_MESSAGE);
        given(loggingEvent.getThrowableProxy()).willReturn(THROWABLE_PROXY);
        given(loggingEvent.getTimeStamp()).willReturn(TIMESTAMP);

        // when
        appender.append(loggingEvent);

        // then
        verify(invocationBuilder).post(Entity.entity(prepareOptimizedLoggingEventVO(), MediaType.APPLICATION_JSON_TYPE));
    }

    @Test
    public void shouldNotPushLogWhenAppenderIsDisabled() {

        // given
        appender.setEnabled(false);

        // when
        appender.append(loggingEvent);

        // then
        verifyNoInteractions(loggingEvent, invocationBuilder, webTarget);
    }

    @Test
    public void shouldAppendFailSilentlyOnError() {

        // given
        given(webTarget.request(MediaType.APPLICATION_JSON_TYPE)).willReturn(invocationBuilder);
        doThrow(RuntimeException.class).when(invocationBuilder).post(any(Entity.class));

        // when
        appender.append(loggingEvent);

        // then
        // fail silently
        verify(invocationBuilder).post(any(Entity.class));
    }

    private OptimizedLoggingEventVO prepareOptimizedLoggingEventVO() {

        OptimizedLoggingEventVO optimizedLoggingEventVO = new OptimizedLoggingEventVO();
        setFieldOfOptimizedLoggingEventVO(optimizedLoggingEventVO, "threadName", THREAD_NAME);
        setFieldOfOptimizedLoggingEventVO(optimizedLoggingEventVO, "loggerName", LOGGER_NAME);
        setFieldOfOptimizedLoggingEventVO(optimizedLoggingEventVO, "level", LEVEL);
        setFieldOfOptimizedLoggingEventVO(optimizedLoggingEventVO, "formattedMessage", FORMATTED_LOG_MESSAGE);
        setFieldOfOptimizedLoggingEventVO(optimizedLoggingEventVO, "throwableProxy", ThrowableProxyVO.build(THROWABLE_PROXY));
        setFieldOfOptimizedLoggingEventVO(optimizedLoggingEventVO, "timeStamp", TIMESTAMP);
        setFieldOfOptimizedLoggingEventVO(optimizedLoggingEventVO, "source", TEST_APP);

        return optimizedLoggingEventVO;
    }

    private void setFieldOfOptimizedLoggingEventVO(OptimizedLoggingEventVO optimizedLoggingEventVO, String fieldName, Object value) {

        try {
            Field field = OptimizedLoggingEventVO.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(optimizedLoggingEventVO, value);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            fail("Failed to set OptimizedLoggingEventVO field '" + fieldName + "'");
        }
    }

    private WebTarget readWebTargetValue() {

        WebTarget webTargetFieldValue = null;
        try {
            webTargetFieldValue = (WebTarget) getWebTargetField().get(appender);
        } catch (IllegalAccessException e) {
            fail("Failed to read webTarget field");
        }

        return webTargetFieldValue;
    }

    private Field getWebTargetField() {

        Field webTargetField = null;
        try {
            webTargetField = appender.getClass().getDeclaredField("webTarget");
            webTargetField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            fail("Failed to access webTarget field");
        }

        return webTargetField;
    }
}