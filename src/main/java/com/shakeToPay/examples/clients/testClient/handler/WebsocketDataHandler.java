package com.shakeToPay.examples.clients.testClient.handler;

import java.util.Queue;
import java.util.StringJoiner;
import java.util.concurrent.ArrayBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class WebsocketDataHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebsocketDataHandler.class);
    private final Queue<String> wsMessages = new ArrayBlockingQueue(200000, true);

    public boolean update(Object arg) {
        String wsMessage = String.valueOf(arg);
        LOGGER.info("ws message is: {}", wsMessage);
        return this.wsMessages.add(wsMessage);
    }

    public String getAllData() {
        StringBuilder builder = new StringBuilder("[");
        StringJoiner joiner = new StringJoiner(",");

        while(!this.wsMessages.isEmpty()) {
            joiner.add((CharSequence)this.wsMessages.poll());
        }

        builder.append(joiner);
        builder.append("\n]");
        return builder.toString();
    }
}
