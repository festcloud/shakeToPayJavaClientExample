package com.shakeToPay.examples.clients.testClient;

import com.shakeToPay.examples.clients.testClient.handler.WebsocketDataHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PerformController {
    private final WebsocketDataHandler dataHandler;

    @GetMapping(
            path = {"/pullMessages"},
            produces = {"application/json"}
    )
    public String performRequest() {
        return this.dataHandler.getAllData();
    }

    public PerformController(final WebsocketDataHandler dataHandler) {
        this.dataHandler = dataHandler;
    }
}
