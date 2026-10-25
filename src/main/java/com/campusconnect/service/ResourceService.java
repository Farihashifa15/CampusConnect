package com.campusconnect.service;

import com.campusconnect.config.AppConfig;
import com.campusconnect.dao.ResourceDAO;
import com.campusconnect.exception.ValidationException;
import com.campusconnect.model.Resource;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ResourceService {

    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
        "pdf", "doc", "docx", "txt", "ppt", "pptx", "zip", "rar", "png", "jpg", "jpeg"
    ));
    private static final long MAX_FILE_SIZE = 25 * 1024 * 1024; // 25 MB

    private final ResourceDAO resourceDAO;
    private final CreditLedgerService creditService;

    public ResourceService() {
        this(new ResourceDAO(), new CreditLedgerService());
    }

    public ResourceService(ResourceDAO resourceDAO, CreditLedgerService creditService) {
        this.resourceDAO = resourceDAO;
        this.creditService = creditService;
    }

    public Resource uploadResource(int uploaderId, String title, String description,
                                  Resource.Category category, String tags,
                                  String originalFilename, long fileSize, String mimeType,
                                  InputStream fileContent) throws SQLException, ValidationException {
        if (title == null || title.trim().isEmpty()) {
            throw new ValidationException("Resource title is required.");
        }
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new ValidationException("A file must be selected for upload.");
        }
        if (fileSize > MAX_FILE_SIZE) {
            throw new ValidationException("File size exceeds 25 MB limit.");
        }

        // Validate extension
        String cleanName = Paths.get(originalFilename).getFileName().toString();
        int dotIndex = cleanName.lastIndexOf('.');
        if (dotIndex == -1) {
            throw new ValidationException("Uploaded file has no extension.");
        }
        String ext = cleanName.substring(dotIndex + 1).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new ValidationException("File type ." + ext + " is not permitted. Permitted formats: " + ALLOWED_EXTENSIONS);
        }

        // Generate safe random stored filename
        String storedFilename = "res_" + UUID.randomUUID().toString() + "." + ext;

        // Ensure storage directory exists
        Path uploadDirPath = Paths.get(AppConfig.getUploadDir());
        try {
            if (!Files.exists(uploadDirPath)) {
                Files.createDirectories(uploadDirPath);
            }
            Path targetFile = uploadDirPath.resolve(storedFilename);
            Files.copy(fileContent, targetFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new ValidationException("Failed to save uploaded file: " + e.getMessage(), e);
        }

        Resource r = new Resource();
        r.setUploaderId(uploaderId);
        r.setTitle(title.trim());
        r.setDescription(description != null ? description.trim() : "");
        r.setCategory(category != null ? category : Resource.Category.NOTES);
        r.setTags(tags != null ? tags.trim() : "");
        r.setStoredFilename(storedFilename);
        r.setOriginalFilename(cleanName);
        r.setFileSize(fileSize);
        r.setMimeType(mimeType != null ? mimeType : "application/octet-stream");
        r.setStatus(Resource.Status.APPROVED);

        int id = resourceDAO.createResource(r);
        r.setId(id);

        // Award Skill Credits for resource upload contribution
        creditService.awardResourceUpload(uploaderId, id, r.getTitle());

        return r;
    }

    public Resource getResource(int id) throws SQLException {
        return resourceDAO.getResourceById(id);
    }

    public List<Resource> searchResources(String keyword, String category, int limit, int offset) throws SQLException {
        return resourceDAO.getResources(keyword, category, limit, offset);
    }

    public File getResourceFile(Resource resource) throws ValidationException {
        if (resource == null) throw new ValidationException("Resource not found.");
        Path filePath = Paths.get(AppConfig.getUploadDir()).resolve(resource.getStoredFilename()).normalize();
        File file = filePath.toFile();
        if (!file.exists() || !file.isFile()) {
            throw new ValidationException("Requested file is not available on server.");
        }
        return file;
    }

    public void incrementDownloadCount(int id) throws SQLException {
        resourceDAO.incrementDownloadCount(id);
    }
}