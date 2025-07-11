package com.pets.application.location;

import com.pets.domain.model.Location;
import com.pets.infrastructure.websocket.emitter.LocationWebSocketEmitter;

public class SendLocationUpdateUseCase {

    private final LocationWebSocketEmitter emitter;

    public SendLocationUpdateUseCase(LocationWebSocketEmitter emitter) {
        this.emitter = emitter;
    }

    public void execute(Location location) {
        emitter.emitLocation(location);
    }
}
