package com.cdn.images.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class UploadFolderConfig {

    public UploadFolderConfig(@Value("${file.upload-dir}") String uploadDir) {
        try {
            Path path = Paths.get(uploadDir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not create upload folder", e);
        }
    }
}