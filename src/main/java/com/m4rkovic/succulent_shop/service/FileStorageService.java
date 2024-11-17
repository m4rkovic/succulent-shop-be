package com.m4rkovic.succulent_shop.service;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {


    @Value("${file.upload-dir:uploads}")
    private final String uploadDir;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            log.info("Created upload directory: {}", uploadDir);
        } catch (IOException e) {
            log.error("Could not create upload directory: {}", uploadDir);
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            // Normalize file name
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            // Validate file name
            if (fileName.contains("..")) {
                throw new RuntimeException("Invalid file path sequence in filename: " + fileName);
            }

            // Get file extension
            String fileExtension = "";
            int lastIndex = fileName.lastIndexOf('.');
            if (lastIndex > 0) {
                fileExtension = fileName.substring(lastIndex);
            }

            // Generate unique filename with UUID and timestamp
            String uniqueFileName = UUID.randomUUID().toString() + "_" +
                    System.currentTimeMillis() +
                    fileExtension;

            // Create the file path
            Path targetLocation = Paths.get(uploadDir).resolve(uniqueFileName).normalize();

            // Validate file type (optional, add more types as needed)
            String contentType = file.getContentType();
            if (contentType != null && !contentType.startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed!");
            }

            // Copy file to the target location
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Stored file: {} as {}", fileName, uniqueFileName);

            return uniqueFileName;
        } catch (IOException ex) {
            log.error("Failed to store file: {}", ex.getMessage());
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            if (fileName == null || fileName.contains("..")) {
                throw new RuntimeException("Invalid file name: " + fileName);
            }

            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                log.error("File not found: {}", fileName);
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            log.error("File not found: {}", fileName);
            throw new RuntimeException("File not found: " + fileName);
        }
    }

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        try {
            if (fileName.contains("..")) {
                throw new RuntimeException("Invalid file name: " + fileName);
            }

            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("Successfully deleted file: {}", fileName);
            } else {
                log.warn("File does not exist: {}", fileName);
            }
        } catch (IOException ex) {
            log.error("Error deleting file {}: {}", fileName, ex.getMessage());
            throw new RuntimeException("Failed to delete file: " + fileName, ex);
        }
    }

    // Helper method to get upload directory path
    public Path getUploadPath() {
        return Paths.get(uploadDir).normalize();
    }
}