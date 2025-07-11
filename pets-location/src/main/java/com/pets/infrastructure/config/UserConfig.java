package com.pets.infrastructure.config;

import com.pets.application.user.DeleteUserByIdUseCase;
import com.pets.application.user.GetAllUsersUseCase;
import com.pets.application.user.RegisterUserUseCase;
import com.pets.domain.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository) {
        return new RegisterUserUseCase(userRepository);
    }
    @Bean
    public GetAllUsersUseCase getAllUsersUseCase(UserRepository userRepository) {
        return new GetAllUsersUseCase(userRepository);
    }
//    @Bean
//    public GetUserByIdUseCase getUserByIdUseCase(UserRepository userRepository) {
//        return new GetUserByIdUseCase(userRepository);
//    }
//    @Bean
//    public GetUserByEmailUseCase getUserByEmailUseCase(UserRepository userRepository){
//        return new GetUserByEmailUseCase(userRepository);
//    }
    @Bean public DeleteUserByIdUseCase deleteUserByIdUseCase(UserRepository userRepository){
        return new DeleteUserByIdUseCase(userRepository);
    }
}
