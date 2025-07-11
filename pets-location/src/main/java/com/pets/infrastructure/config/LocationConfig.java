package com.pets.infrastructure.config;

import com.pets.application.location.GetAllLocationsByPetIdUseCase;
import com.pets.application.location.GetLatestLocationByPetIdUseCase;
import com.pets.application.location.RegisterLocationUseCase;
import com.pets.application.location.SendLocationUpdateUseCase;
import com.pets.domain.repository.LocationRepository;
import com.pets.infrastructure.websocket.emitter.LocationWebSocketEmitter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocationConfig {
    @Bean
    public RegisterLocationUseCase registerLocationUseCase(LocationRepository locationRepository){
        return new RegisterLocationUseCase(locationRepository);
    }
    @Bean
    public GetLatestLocationByPetIdUseCase getLatestLocationByPetIdUseCase(LocationRepository locationRepository){
        return new GetLatestLocationByPetIdUseCase(locationRepository);
    }
    @Bean
    public GetAllLocationsByPetIdUseCase getAllLocationsByPetIdUseCase(LocationRepository locationRepository){
        return new GetAllLocationsByPetIdUseCase(locationRepository);
    }
    @Bean
    public SendLocationUpdateUseCase sendLocationUpdateUseCase(LocationWebSocketEmitter emitter){
        return new SendLocationUpdateUseCase(emitter);
    }
}
