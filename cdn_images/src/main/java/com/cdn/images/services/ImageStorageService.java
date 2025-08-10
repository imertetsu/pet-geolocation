package com.cdn.images.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class ImageStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String storeImage(MultipartFile file, String userId) throws IOException {
        // Ruta del directorio del usuario
        Path userDir = Paths.get(uploadDir, userId);

        // Crear carpeta si no existe
        if (!Files.exists(userDir)) {
            Files.createDirectories(userDir);
        }

        // Obtener extensión
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // Generar nombre con fecha y hora
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String newFileName = timestamp + "_" + originalFilename.replaceAll("\\s+", "_");

        // Ruta final
        Path targetPath = userDir.resolve(newFileName);

        // Guardar archivo
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

        // Aquí agregamos el dominio y puerto
        String baseUrl = "http://10.0.2.2:9090";
        // Retornar URL pública
        return baseUrl + "/images/" + userId + "/" + newFileName;
    }

    public void deleteFile(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        // Ejemplo: http://10.0.2.2:9090/images/user123/file.png
        String relativePath = fileUrl.replace("http://10.0.2.2:9090/images/", "");
        Path targetPath = Paths.get(uploadDir).resolve(relativePath);

        Files.deleteIfExists(targetPath);
    }

}
