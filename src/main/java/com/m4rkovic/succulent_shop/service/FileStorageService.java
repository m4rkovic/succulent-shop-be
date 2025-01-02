package com.m4rkovic.succulent_shop.service;

import com.m4rkovic.succulent_shop.entity.Product;
import com.m4rkovic.succulent_shop.exceptions.FileStorageException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {

    @Value("${file.upload-dir}")
    private String uploadDir;  // Remove final to allow @Value injection

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        try {
            this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.fileStorageLocation);
            log.info("Created upload directory at: {}", this.fileStorageLocation);
        } catch (IOException e) {
            log.error("Could not create upload directory at: {}", this.fileStorageLocation);
            throw new RuntimeException("Could not create upload directory!", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.warn("No file provided or empty file");
            return null;
        }

        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            // Check for invalid file name
            if (fileName.contains("..")) {
                throw new RuntimeException("Invalid file path sequence in filename: " + fileName);
            }

            // Get file extension
            String fileExtension = "";
            int lastIndex = fileName.lastIndexOf('.');
            if (lastIndex > 0) {
                fileExtension = fileName.substring(lastIndex).toLowerCase();
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new RuntimeException("Only image files are allowed!");
            }

            // Generate unique filename
            String uniqueFileName = UUID.randomUUID().toString() + "_" +
                    System.currentTimeMillis() +
                    fileExtension;

            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);

            // Create directories if they don't exist
            Files.createDirectories(targetLocation.getParent());

            // Copy file
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            log.info("Successfully stored file: {} as {}", fileName, uniqueFileName);

            return uniqueFileName;

        } catch (IOException ex) {
            log.error("Failed to store file", ex);
            throw new RuntimeException("Could not store file. Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String fileName) {
        try {
            if (fileName == null || fileName.contains("..")) {
                throw new RuntimeException("Invalid file name: " + fileName);
            }

            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                log.error("File not found: {}", fileName);
                throw new RuntimeException("File not found: " + fileName);
            }
        } catch (MalformedURLException ex) {
            log.error("File not found: {}", fileName, ex);
            throw new RuntimeException("File not found: " + fileName);
        }
    }

    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.info("Successfully deleted file: {}", fileName);
            } else {
                log.warn("File does not exist: {}", fileName);
            }
        } catch (IOException ex) {
            log.error("Error deleting file {}", fileName, ex);
            throw new RuntimeException("Failed to delete file: " + fileName, ex);
        }
    }
    
    public void cleanupOrphanedFiles(Set<String> validFileNames) {
        try {
            // Get all files in the storage directory
            Files.list(fileStorageLocation)
                    .filter(Files::isRegularFile)
                    .forEach(file -> {
                        String fileName = file.getFileName().toString();
                        // If the file isn't in our valid files list, delete it
                        if (!validFileNames.contains(fileName)) {
                            try {
                                Files.delete(file);
                                log.debug("Deleted orphaned file: {}", fileName);
                            } catch (IOException e) {
                                log.error("Failed to delete orphaned file: {}", fileName, e);
                            }
                        }
                    });
        } catch (IOException e) {
            log.error("Failed to cleanup orphaned files", e);
            throw new FileStorageException("Failed to cleanup orphaned files", e);
        }
    }
}