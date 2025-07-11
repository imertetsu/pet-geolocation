package com.pets.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private static String SECRET_KEY = "imertetsu";
    private static Algorithm ALGORIIHM = Algorithm.HMAC256(SECRET_KEY);
    public String create(String username){
        System.out.println("ALGORIIHM "+ALGORIIHM);
        return JWT.create()
                .withSubject(username)
                .withIssuer("tetsu")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis()+ TimeUnit.DAYS.toMillis(15)))
                .sign(ALGORIIHM);
    }

    public boolean isValid(String jwt){
        try {
            JWT.require(ALGORIIHM)
                    .build()
                    .verify(jwt);
            return true;
        }catch (JWTVerificationException e){
            return false;
        }
    }
    public String getUsername(String jwt){
        return JWT.require(ALGORIIHM)
                .build()
                .verify(jwt)
                .getSubject();
    }
}
