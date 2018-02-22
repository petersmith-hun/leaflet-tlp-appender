package hu.psprog.leaflet.tlp.appender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import hu.psprog.leaflet.tlp.appender.client.TLPClientFactory;
import hu.psprog.leaflet.tlp.appender.domain.OptimizedLoggingEventVO;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Logback appender implementation for TLP.
 * This appender pushes events to TLP (Tiny Log Processor) service via Jersey REST client.
 * Required parameters (mandatory):
 *  - enabled: set to {@code false} to disable event pushing
 *  - host: TLP host URL
 *  - appID: application ID
 *
 * @author Peter Smith
 */
public class TinyLogProcessorAppender extends AppenderBase<ILoggingEvent> {

    private WebTarget webTarget;
    private String host;
    private String appID;
    private boolean enabled;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public void start() {
        super.start();
        webTarget = TLPClientFactory.createWebTarget(host);
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        try {
            pushLog(eventObject);
        } catch (Exception exc) {
            addError("Failed to push log to TLP", exc);
        }
    }

    private void pushLog(ILoggingEvent loggingEvent) {
        if (enabled) {
            webTarget.request(MediaType.APPLICATION_JSON_TYPE)
                    .post(createEntity(loggingEvent));
        }
    }

    private Entity createEntity(ILoggingEvent loggingEvent) {
        return Entity.entity(OptimizedLoggingEventVO.build(loggingEvent, appID), MediaType.APPLICATION_JSON_TYPE);
    }
}
