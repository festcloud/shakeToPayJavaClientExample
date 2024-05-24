package com.shakeToPay.examples.clients.testClient.integrations.ws;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.CloseReason;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler.Whole;
import javax.websocket.Session;

class WebsocketEndpoint extends Endpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketEndpoint.class);
    Session session = null;
    private final Whole<String> messageHandler;
    private final EventListenerI closeEventListener;

    WebsocketEndpoint(Whole<String> messageHandler, EventListenerI closeEventListener) {
        this.messageHandler = messageHandler;
        this.closeEventListener = closeEventListener;
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
        this.session = session;
        if (this.messageHandler != null) {
            session.addMessageHandler(new WebsocketEndpointMessageHandler());
        }

    }

    public void onError(Session session, Throwable throwable) {
        try {
            LOGGER.error("websocket error: ", throwable);
            super.onError(session, throwable);
        } catch (Exception var4) {
            LOGGER.error("websocket onError: ", var4);
        }

    }

    public void onClose(Session session, CloseReason closeReason) {
        LOGGER.error("close websocket connection, resason: {}", closeReason);
        super.onClose(session, closeReason);
        this.closeEventListener.notify((Object) null);
    }

    class WebsocketEndpointMessageHandler implements Whole<String> {

        public void onMessage(String message) {
            messageHandler.onMessage(message);
        }
    }

}
