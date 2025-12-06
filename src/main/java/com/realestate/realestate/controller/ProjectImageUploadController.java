package com.realestate.realestate.controller;

import com.realestate.realestate.service.ProjectImageService;
import com.realestate.realestate.repository.ProjectImageRepository;
import com.realestate.realestate.model.ProjectImage;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/project")
@CrossOrigin
public class ProjectImageUploadController {

    @Autowired
    private ProjectImageService projectImageService;

    @Autowired
    private ProjectImageRepository projectImageRepo;

    // UPLOAD SINGLE IMAGE
    @PostMapping("/uploadImage/{projectId}")
    public ResponseEntity<?> uploadSingleImage(
            @PathVariable Long projectId,
            @RequestParam("image") MultipartFile file) {

        try {
            String url = projectImageService.uploadImage(file, projectId);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // UPLOAD MULTIPLE IMAGES
    @PostMapping("/uploadImages/{projectId}")
    public ResponseEntity<?> uploadMultipleImages(
            @PathVariable Long projectId,
            @RequestParam("images") List<MultipartFile> files) {

        try {
            List<String> urls = projectImageService.uploadMultipleImages(files, projectId);
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // DELETE IMAGE
    @DeleteMapping("/deleteImage/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable Long imageId) {
        try {
            projectImageService.deleteImage(imageId);
            return ResponseEntity.ok("Image deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // REPLACE IMAGE
    @PutMapping("/replaceImage/{imageId}")
    public ResponseEntity<?> replaceImage(
            @PathVariable Long imageId,
            @RequestParam("image") MultipartFile newImage) {

        try {
            String newUrl = projectImageService.replaceImage(imageId, newImage);
            return ResponseEntity.ok(newUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // REORDER IMAGES
    @PutMapping("/reorderImages/{projectId}")
    public ResponseEntity<?> reorderImages(
            @PathVariable Long projectId,
            @RequestBody List<Long> newOrder) {

        try {
            projectImageService.reorderImages(projectId, newOrder);
            return ResponseEntity.ok("Images reordered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // GET ALL IMAGES FOR A PROJECT
    @GetMapping("/getImages/{projectId}")
    public ResponseEntity<?> getImages(@PathVariable Long projectId) {
        return ResponseEntity.ok(
            projectImageRepo.findByProjectIdOrderBySortOrder(projectId)
                            .stream()
                            .map(ProjectImage::getImageUrl)
                            .toList()
        );
    }
}
