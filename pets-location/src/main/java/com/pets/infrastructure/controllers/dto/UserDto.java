package com.pets.infrastructure.controllers.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UserDto {
    public UUID id;
    public String name;
}
