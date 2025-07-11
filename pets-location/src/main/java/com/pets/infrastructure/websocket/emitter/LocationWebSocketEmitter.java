package com.pets.infrastructure.websocket.emitter;

import com.pets.domain.model.Location;
import com.pets.domain.records.LocationRequest;
import com.pets.infrastructure.controllers.LocationController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class LocationWebSocketEmitter {

    private static final Logger logger = LoggerFactory.getLogger(LocationWebSocketEmitter.class);
    private final SimpMessagingTemplate messagingTemplate;

    public LocationWebSocketEmitter(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void emitLocation(Location location) {
        messagingTemplate.convertAndSend(
                "/topic/location/" + location.getPetId(),
                new LocationRequest(
                        location.getPetId(),
                        location.getLatitude(),
                        location.getLongitude()
                )
        );
        logger.info("📡 Emitida ubicación para petId {}: lat={}, lon={}, locationId={}",
                location.getPetId(), location.getLatitude(), location.getLongitude(), location.getId());
    }
}

