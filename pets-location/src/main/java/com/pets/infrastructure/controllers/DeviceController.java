package com.pets.infrastructure.controllers;

import com.pets.application.device.*;
import com.pets.domain.model.Device;
import com.pets.domain.records.DeviceRequest;
import com.pets.domain.records.DeviceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final RegisterDeviceUseCase registerDeviceUseCase;
    private final GetDeviceUseCase getDeviceUseCase;
    private final DeleteDeviceUseCase deleteDeviceUseCase;
    private final GetAllDevicesUseCase getAllDevicesUseCase;
    private final ExistsDeviceByPetIdUseCase existsDeviceByPetIdUseCase;
    private final GetDeviceIdByPetIdUseCase getDeviceIdByPetIdUseCase;


    @PostMapping
    public ResponseEntity<Void> register(@RequestBody DeviceRequest request) {
        Device device = new Device(request.deviceId(), request.petId());
        registerDeviceUseCase.execute(device);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> get(@PathVariable String id) {
        return getDeviceUseCase.execute(id)
                .map(DeviceResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAll() {
        List<DeviceResponse> responses = getAllDevicesUseCase.execute()
                .stream()
                .map(DeviceResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/exists/pet/{petId}")
    public ResponseEntity<Boolean> existsByPetId(@PathVariable Long petId) {
        boolean exists = existsDeviceByPetIdUseCase.execute(petId);
        return ResponseEntity.ok(exists);
    }

    @GetMapping("/pet/{petId}")
    public ResponseEntity<Map<String, String>> getDeviceByPetId(@PathVariable Long petId) {
        return getDeviceIdByPetIdUseCase.execute(petId)
                .map(deviceId -> ResponseEntity.ok(Map.of("deviceId", deviceId)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        deleteDeviceUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}

