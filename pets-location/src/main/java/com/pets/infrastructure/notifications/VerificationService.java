package com.pets.infrastructure.security;

import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class VerificationService {
    private final Map<String, VerificationCode> codes = new ConcurrentHashMap<>();

    public void generateAndSendCode(String email) {
        String code = generateCode();
        // Guardar con timestamp para expiración
        codes.put(email, new VerificationCode(code, Instant.now().plus(15, ChronoUnit.MINUTES)));

        // Enviar email con código (ejemplo simple)
        emailService.sendVerificationCode(email, code);
    }

    public boolean verifyCode(String email, String code) {
        VerificationCode stored = codes.get(email);
        if (stored == null) return false;
        if (stored.getExpiry().isBefore(Instant.now())) {
            codes.remove(email);
            return false;
        }
        boolean valid = stored.getCode().equals(code);
        if (valid) codes.remove(email); // Consumir código una vez validado
        return valid;
    }

    private String generateCode() {
        Random random = new Random();
        int code = 10000 + random.nextInt(90000);
        return String.valueOf(code);
    }

    private static class VerificationCode {
        private final String code;
        private final Instant expiry;
        public VerificationCode(String code, Instant expiry) {
            this.code = code;
            this.expiry = expiry;
        }
        public String getCode() { return code; }
        public Instant getExpiry() { return expiry; }
    }
}

