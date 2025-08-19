package com.pets.domain.model;

import com.pets.infrastructure.persistence.entities.AuthProvider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private Boolean isVerified;
    private String photoUrl;
    private AuthProvider provider;
    private String providerId;
    private Boolean hasGpsDevice;
    private List<UserRole> roles;
    private List<Pet> pets;
}
