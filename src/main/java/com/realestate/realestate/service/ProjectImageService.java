package com.realestate.realestate.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ProjectImageService {

    // Upload a single image
    String uploadImage(MultipartFile file, Long projectId) throws Exception;

    // Upload multiple images
    List<String> uploadMultipleImages(List<MultipartFile> files, Long projectId) throws Exception;

    // Delete an image
    boolean deleteImage(Long imageId) throws Exception;

    // Replace an image
    String replaceImage(Long imageId, MultipartFile newImage) throws Exception;

    // Reorder images (drag & drop)
    void reorderImages(Long projectId, List<Long> newOrder) throws Exception;
}
