package com.cdn.images.controllers;

import com.cdn.images.services.ImageStorageService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class UploadController {

    private final ImageStorageService imageStorageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") String userId) {

        // Validar tipo MIME permitido
        String contentType = file.getContentType();
        if (contentType == null ||
                !(contentType.startsWith("image/") ||
                        contentType.equals("video/mp4") ||
                        contentType.equals("video/webm"))) {
            return ResponseEntity.badRequest().body("Only images and short videos are allowed");
        }

        // Validar tamaño máximo por tipo
        long maxImageSize = 5 * 1024 * 1024; // 5 MB
        long maxVideoSize = 10 * 1024 * 1024; // 10 MB

        if (contentType.startsWith("image/") && file.getSize() > maxImageSize) {
            return ResponseEntity.badRequest().body("Image size exceeds 5MB limit");
        }
        if ((contentType.equals("video/mp4") || contentType.equals("video/webm"))
                && file.getSize() > maxVideoSize) {
            return ResponseEntity.badRequest().body("Video size exceeds 10MB limit");
        }

        try {

            String fileUrl = imageStorageService.storeImage(file, userId);
            return ResponseEntity.ok(fileUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error uploading file");
        }
    }
}

