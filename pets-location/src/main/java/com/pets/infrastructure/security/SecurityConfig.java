package com.pets.infrastructure.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity(securedEnabled = true)//esto para habilitar el @Secured y utilizar en los metodos de los servicios y asignar roles
public class SecurityConfig {
    private final JwtFilter jwtFilter;

    @Autowired
    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        String[] roles = { "ADMIN", "CUSTOMER"};
        httpSecurity.authorizeHttpRequests(
                customizeRequests -> {
                    customizeRequests
                            //aca ya no ponemos /api/ porque esa es la raiz, se pone directamente la ruta del controlador
                            //con un * solo permitimos el primer nivel con ** permitimos todo para adelante de la ruta
                            //.requestMatchers("/**").permitAll()
                            .requestMatchers(HttpMethod.POST,"/users").permitAll()
                            .requestMatchers(HttpMethod.GET,"/users/**").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/users/**").permitAll()
                            .requestMatchers(HttpMethod.POST,"/auth/login").anonymous()
                            //PETS
                            .requestMatchers(HttpMethod.GET, "/pets/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/pets/**").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/pets/**").permitAll()
                            // LOCATIONS
                            .requestMatchers(HttpMethod.GET, "/locations/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/locations/**").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/locations/**").permitAll()
                            // DEVICES
                            .requestMatchers(HttpMethod.GET, "/devices/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/devices/**").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/devices/**").permitAll()
                            // NEWS
                            .requestMatchers(HttpMethod.GET, "/news/**").permitAll()
                            .requestMatchers(HttpMethod.PUT, "/news/**").permitAll()
                            .requestMatchers(HttpMethod.POST, "/news/**").permitAll()
                            .requestMatchers(HttpMethod.DELETE, "/news/**").permitAll()
                            // WEBSOCKETS
                            .requestMatchers(HttpMethod.GET, "/**").permitAll()
                            // Acceso general a reservas para ADMIN y CUSTOMER
                            .requestMatchers(HttpMethod.GET, "/reservations/**").hasAnyRole("ADMIN", "CUSTOMER")
                            //.requestMatchers(HttpMethod.PUT,"/users").hasAnyRole(Rol.ADMIN.name(), Rol.CUSTOMER.name())
                            //.requestMatchers(HttpMethod.GET, "/users/*").hasRole("ADMIN")
                            //.requestMatchers(HttpMethod.DELETE).hasRole("ADMIN")
                            .requestMatchers("/ws-location/**").permitAll()
                            .anyRequest().authenticated();
                }).csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);//httpBasic(Customizer.withDefaults());
        return httpSecurity.build();

    }
    /*@Bean //estos son usuarios en memorias
    public UserDetailsService memoryUsers(){
        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode("admin"))
                .roles("ADMIN")
                .build();
        UserDetails customer = User.builder()
                .username("customer")
                .password(passwordEncoder().encode("customer123"))
                .roles("CUSTOMER")
                .build();
        return new InMemoryUserDetailsManager(admin, customer);
    }*/

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
