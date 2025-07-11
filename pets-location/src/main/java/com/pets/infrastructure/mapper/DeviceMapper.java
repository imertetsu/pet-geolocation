package com.pets.infrastructure.mapper;

import com.pets.domain.model.Device;
import com.pets.infrastructure.persistence.entities.DeviceEntity;
import com.pets.infrastructure.persistence.entities.PetEntity;

public class DeviceMapper {
    public static Device toDomain(DeviceEntity entity) {
        return new Device(entity.getId(), entity.getPet().getId());
    }

    public static DeviceEntity toEntity(Device device, PetEntity petEntity) {
        DeviceEntity entity = new DeviceEntity();
        entity.setId(device.getId());
        entity.setPet(petEntity);
        return entity;
    }
}