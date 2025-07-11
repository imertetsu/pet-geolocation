package com.pets.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private Long id;
    private Long petId;
    private double latitude;
    private double longitude;
    private LocalDateTime timestamp;
}
