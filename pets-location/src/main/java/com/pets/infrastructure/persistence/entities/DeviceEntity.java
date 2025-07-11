package com.pets.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "devices")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DeviceEntity {

    @Id
    private String id; // Ej: "A9G-123456" (usado como deviceId único)

    @OneToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private PetEntity pet;
}
