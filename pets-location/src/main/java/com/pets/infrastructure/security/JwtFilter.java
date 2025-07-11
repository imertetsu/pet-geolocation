package com.pets.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    @Autowired
    public JwtFilter(
            JwtUtil jwtUtil,
            UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        //1. Validar que sea un Header Authorization valido
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(authHeader == null || authHeader.isEmpty() || !authHeader.startsWith("Bearer")){
            //esto significa que siga continuando con su trabajo de filtrado al Spring Security
            filterChain.doFilter(request, response);
            return; //con esto garantizamos que no continue con el codigo de abajo
        }
        //2. Validar que el JWT sea valido
        // split convierte la cadena en arreglo separado por el espacio pos 0 es Bearer pos 1 el token
        // trim para asegurarnos que no tenga espacios al inicio ni al final
        String jwt = authHeader.split(" ")[1].trim();
        if(!jwtUtil.isValid(jwt)){
            filterChain.doFilter(request, response);
            return;
        }
        //3. Cargar el usuario del UserDetailsService
        String username = jwtUtil.getUsername(jwt);
        //esto lo que hace es cargar al usuario en un objeto User
        User user = (User) userDetailsService.loadUserByUsername(username);

        //4. Cargar al usuario en el contexto de seguridad
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                user.getUsername(), user.getPassword(), user.getAuthorities()
        );
        System.out.println("AUTHENTICATIONTOKEN: "+ authenticationToken);
        //agregamos detalles a nuestro contexto de seguridad
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        System.out.println("AUTH TOKEN DETAILS: "+ authenticationToken);
        filterChain.doFilter(request, response);
    }
}
