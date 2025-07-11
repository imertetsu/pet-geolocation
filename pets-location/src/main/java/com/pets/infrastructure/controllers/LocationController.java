package com.pets.infrastructure.controllers;

import com.pets.application.location.GetAllLocationsByPetIdUseCase;
import com.pets.application.location.GetLatestLocationByPetIdUseCase;
import com.pets.application.location.RegisterLocationUseCase;
import com.pets.application.location.SendLocationUpdateUseCase;
import com.pets.domain.model.Location;
import com.pets.domain.records.LocationRequest;
import com.pets.domain.records.LocationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/locations")
@RequiredArgsConstructor
public class LocationController {

    private final RegisterLocationUseCase registerLocationUseCase;
    private final GetLatestLocationByPetIdUseCase getLatestLocationByPetIdUseCase;
    private final GetAllLocationsByPetIdUseCase getAllLocationsByPetIdUseCase;
    private final SendLocationUpdateUseCase sendLocationUpdateUseCase;

    @PostMapping
    public ResponseEntity<Void> register(@RequestBody LocationRequest request) {
        Location location = new Location(
                null,
                request.petId(),
                request.latitude(),
                request.longitude(),
                LocalDateTime.now()
        );
        registerLocationUseCase.execute(location);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/pet/{petId}/latest")
    public ResponseEntity<LocationResponse> getLatest(@PathVariable Long petId) {
        return getLatestLocationByPetIdUseCase.execute(petId)
                .map(LocationResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<List<LocationResponse>> getAll(@PathVariable Long petId) {
        List<Location> locations = getAllLocationsByPetIdUseCase.execute(petId);
        return ResponseEntity.ok(
                locations.stream().map(LocationResponse::from).toList()
        );
    }

    @PostMapping("/simulate")
    public ResponseEntity<Void> simulate(@RequestBody LocationRequest locationRequest) {
        Location location = new Location(
                null,
                locationRequest.petId(),
                locationRequest.latitude(),
                locationRequest.longitude(),
                LocalDateTime.now()
        );

        sendLocationUpdateUseCase.execute(location);
        return ResponseEntity.ok().build();
    }
}