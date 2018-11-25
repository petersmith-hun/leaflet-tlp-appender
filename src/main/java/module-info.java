module leaflet.component.tlp.appender {
    requires java.ws.rs;
    requires com.fasterxml.jackson.jaxrs.json;
    requires logback.classic;
    requires logback.core;

    exports hu.psprog.leaflet.tlp.appender;
}