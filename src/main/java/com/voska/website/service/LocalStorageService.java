package com.voska.website.service;

import com.voska.website.exception.ImageStorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {

    private final Path uploadRoot;

    public LocalStorageService(
            @Value("${app.storage.upload-dir:uploads}") String uploadDir
    ) {
        this.uploadRoot = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(uploadRoot);
        } catch (IOException exception) {
            throw new ImageStorageException(
                    "Upload klasörü oluşturulamadı.",
                    exception
            );
        }
    }

    @Override
    public String storeProjectImage(Long projectId, MultipartFile file) {
        validateFile(file);

        String extension = getExtension(file.getOriginalFilename());
        String fileName = UUID.randomUUID() + extension;

        Path projectDirectory = uploadRoot
                .resolve("projects")
                .resolve(projectId.toString());

        try {
            Files.createDirectories(projectDirectory);

            Path targetPath = projectDirectory
                    .resolve(fileName)
                    .normalize();

            ensurePathIsSafe(targetPath);

            file.transferTo(targetPath);

            return "/uploads/projects/" + projectId + "/" + fileName;

        } catch (IOException exception) {
            throw new ImageStorageException(
                    "Görsel kaydedilemedi.",
                    exception
            );
        }
    }

    @Override
    public void delete(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            return;
        }

        String relativePath = imageUrl.replaceFirst("^/uploads/", "");
        Path filePath = uploadRoot.resolve(relativePath).normalize();

        ensurePathIsSafe(filePath);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException exception) {
            throw new ImageStorageException(
                    "Görsel silinemedi.",
                    exception
            );
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ImageStorageException("Yüklenecek görsel boş olamaz.");
        }

        String contentType = file.getContentType();

        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ImageStorageException(
                    "Yalnızca görsel dosyaları yüklenebilir."
            );
        }
    }

    private String getExtension(String originalFileName) {
        if (originalFileName == null || originalFileName.isBlank()) {
            return "";
        }

        int dotIndex = originalFileName.lastIndexOf(".");

        if (dotIndex < 0) {
            return "";
        }

        return originalFileName.substring(dotIndex).toLowerCase();
    }

    private void ensurePathIsSafe(Path path) {
        if (!path.startsWith(uploadRoot)) {
            throw new ImageStorageException("Geçersiz dosya yolu.");
        }
    }
}
