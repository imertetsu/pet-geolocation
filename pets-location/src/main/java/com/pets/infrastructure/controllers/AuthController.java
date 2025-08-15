package com.pets.infrastructure.controllers;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.pets.application.auth.LoginWithGoogleUseCase;
import com.pets.domain.records.UserResponse;
import com.pets.domain.repository.UserRepository;
import com.pets.infrastructure.security.JwtUtil;
import com.pets.infrastructure.notifications.VerificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final LoginWithGoogleUseCase loginWithGoogleUseCase;
    @Autowired
    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService,
            UserRepository userRepository,
            LoginWithGoogleUseCase loginWithGoogleUseCase){
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
        this.loginWithGoogleUseCase = loginWithGoogleUseCase;
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginDTO loginDTO) {
        try {
            UsernamePasswordAuthenticationToken login =
                    new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password());
            Authentication authentication = this.authenticationManager.authenticate(login);

            String jwt = this.jwtUtil.create(loginDTO.email());

            User user = (User) userDetailsService.loadUserByUsername(loginDTO.email());
            String email = user.getUsername();
            String role = user.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("CUSTOMER");

            UUID userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("token", jwt);
            response.put("email", email);
            response.put("role", role);

            return ResponseEntity.ok(response);
        } catch (BadCredentialsException ex) {
            Map<String, Object> error = new HashMap<>();
            error.put("message", "Credenciales inválidas");
            return ResponseEntity.status(401).body(error);
        }
    }

    @PostMapping("/firebase")
    public ResponseEntity<?> loginWithFirebase(@RequestBody TokenRequest request) {
        log.info("Token recibido: {}", request.idToken());

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.idToken(), true);
            log.info("UID verificado: {}", decodedToken.getUid());
            log.info("EMAIL: {}", decodedToken.getEmail());

            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String picture = decodedToken.getPicture();

            // Ejecutar caso de uso que registra o retorna al usuario ya existente
            com.pets.domain.model.User domainUser = loginWithGoogleUseCase.execute(email, name, picture, uid);

            // Generar JWT
            String jwt = jwtUtil.create(email);

            // Obtener userId y role desde la base de datos
            User springUser = (User) userDetailsService.loadUserByUsername(email);
            String role = springUser.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("CUSTOMER");

            UUID userId = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found"))
                    .getId();

            Map<String, Object> response = new HashMap<>();
            response.put("userId", userId);
            response.put("token", jwt);
            response.put("email", email);
            response.put("role", role);
            response.put("photoUrl", picture);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error al verificar token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token inválido o expirado");
        }
    }
    public record LoginDTO(String email, String password) {
    }

    public record TokenRequest (String idToken) {
    }
}
