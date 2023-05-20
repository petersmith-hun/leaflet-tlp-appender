package hu.psprog.leaflet.tlp.appender.client;

import com.fasterxml.jackson.jakarta.rs.json.JacksonJsonProvider;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;

/**
 * Factory than can create a {@link WebTarget} so the appender is able to communicate with TLP.
 *
 * @author Peter Smith
 */
public class TLPClientFactory {

    private static final String PATH_LOGS = "logs";

    private TLPClientFactory() {
    }

    /**
     * Creates the {@link WebTarget} object for given host.
     * Built {@link WebTarget} instance will point to /logs path.
     *
     * @param host host full URL
     * @return built {@link WebTarget} object
     */
    public static WebTarget createWebTarget(String host) {

        return ClientBuilder.newBuilder()
                .register(JacksonJsonProvider.class)
                .build()
                .target(host)
                .path(PATH_LOGS);
    }
}
