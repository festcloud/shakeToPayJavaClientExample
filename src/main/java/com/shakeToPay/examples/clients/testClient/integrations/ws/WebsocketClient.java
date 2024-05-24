package com.shakeToPay.examples.clients.testClient.integrations.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.ClientEndpointConfig;
import javax.websocket.ClientEndpointConfig.Builder;
import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.MessageHandler.Whole;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Encoder;
import java.util.Collections;
import java.util.List;
import java.util.Map;

class WebsocketClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketClient.class);
    private final WebsocketEndpoint websocketEndpoint;
    private final ClientEndpointConfig clientConfig;
    private final URI uri;
    private boolean connected;

    WebsocketClient(String uri, String user, String password, Whole<String> messageHandler) throws URISyntaxException {
        Builder configBuilder = Builder.create();
        Encoder encoder = Base64.getEncoder();
        String var10000 = encoder.encodeToString((user + ":" + password).getBytes());
        String authHeaderValue = "Basic " + var10000;
        configBuilder.configurator(new WebsocketClientConfig(authHeaderValue));
        this.clientConfig = configBuilder.build();
        this.uri = new URI(uri);
        LOGGER.info("Connect ws params uri: {}, Authorization header: {}", uri, authHeaderValue);
        this.websocketEndpoint = new WebsocketEndpoint(messageHandler, new EventListenerI() {
            @Override
            public void notify(Object o) {
                connected = false;
            }
        });
    }

    void connect() throws IOException {
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();

        try {
            container.connectToServer(this.websocketEndpoint, this.clientConfig, this.uri);
            this.connected = true;
        } catch (DeploymentException var3) {
            this.connected = false;
            throw new IOException("Error while connecting to websocket server: " + this.uri, var3);
        }
    }

    boolean sendMessage(String message) throws IOException {
        return this.sendMessage(message, false);
    }

    boolean sendMessage(String message, boolean isPing) throws IOException {
        if (this.websocketEndpoint.session == null) {
            return false;
        } else {
            if (isPing) {
                this.websocketEndpoint.session.getBasicRemote().sendPing(ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8)));
            } else {
                this.websocketEndpoint.session.getBasicRemote().sendText(message);
            }

            return true;
        }
    }

    void close() throws IOException {
        this.connected = false;
        if (this.websocketEndpoint.session != null) {
            this.websocketEndpoint.session.close();
        }

    }

    boolean isConnected() {
        return this.connected;
    }

    class WebsocketClientConfig extends ClientEndpointConfig.Configurator {
        private final String authHeaderValue;
        private static final String COOKIE_HEADER_NAME = "cookie";
        private static final String COOKIE_HEADER_VALUE = "system=!FEST";

        WebsocketClientConfig(final String authHeaderValue) {
            this.authHeaderValue = authHeaderValue;
        }

        public void beforeRequest(Map<String, List<String>> headers) {
            headers.put("Authorization", Arrays.asList(this.authHeaderValue));
            headers.put(COOKIE_HEADER_NAME, Collections.singletonList(COOKIE_HEADER_VALUE));
        }
    }
}
