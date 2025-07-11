package com.pets.infrastructure.controllers;

import com.pets.domain.repository.UserRepository;
import com.pets.infrastructure.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService,
            UserRepository userRepository){
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }
    @PostMapping("/login")
    public ResponseEntity<Map> login(@RequestBody LoginDTO loginDTO){
        UsernamePasswordAuthenticationToken login = new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());
        System.out.println("LOGIN " + login);
        Authentication authentication = this.authenticationManager.authenticate(login);
        String jwt = this.jwtUtil.create(loginDTO.email());

        User user =  (User) userDetailsService.loadUserByUsername(loginDTO.email());
        System.out.println("USER "+user);
        String email = user.getUsername();
        String role = user.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority).orElse("CUSTOMER");

        UUID userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();

        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("token", jwt);
        response.put("email", email);
        response.put("role", role);

        System.out.println("Esta Auth? "+authentication.isAuthenticated());
        System.out.println(authentication.getPrincipal());
        System.out.println("JWT: "+jwt);

        return ResponseEntity.ok(response);
    }

    public record LoginDTO(String email, String password) {
    }
}
