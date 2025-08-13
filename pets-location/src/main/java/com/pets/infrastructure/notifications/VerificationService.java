package com.pets.infrastructure.notifications;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationService {

    private static class CodeEntry {
        String code;
        LocalDateTime expiresAt;
        CodeEntry(String code, LocalDateTime expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }

    private final Map<String, CodeEntry> codes = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public void generateAndSendCode(String email, EmailService emailService) {
        String code = String.format("%05d", random.nextInt(100000)); // 5 dígitos
        codes.put(email, new CodeEntry(code, LocalDateTime.now().plusMinutes(15)));

        System.out.println("Tu código de verificación es: " + code + "\nCaduca en 15 minutos.");

        // Enviar email
        emailService.sendEmail(email, "Código de verificación",
                "Tu código de verificación es: " + code + "\nCaduca en 15 minutos.");
    }

    public boolean verifyCode(String email, String code) {
        CodeEntry entry = codes.get(email);
        if (entry == null) return false;
        if (LocalDateTime.now().isAfter(entry.expiresAt)) {
            codes.remove(email);
            return false;
        }
        boolean matches = entry.code.equals(code);
        if (matches) {
            codes.remove(email); // Eliminar tras uso
        }
        return matches;
    }
}

