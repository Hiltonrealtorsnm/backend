package com.realestate.realestate.controller;

import com.realestate.realestate.service.ImageUploadService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/property")
@CrossOrigin
public class ImageUploadController {

    @Autowired
    private ImageUploadService imageUploadService;

    // ==============================
    // UPLOAD SINGLE IMAGE
    // ==============================
    @PostMapping("/uploadImage/{propertyId}")
    public ResponseEntity<?> uploadSingleImage(
            @PathVariable int propertyId,
            @RequestParam("image") MultipartFile file) {

        try {
            String url = imageUploadService.uploadImage(file, propertyId);
            return ResponseEntity.ok(url);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==============================
    // UPLOAD MULTIPLE IMAGES
    // ==============================
    @PostMapping("/uploadImages/{propertyId}")
    public ResponseEntity<?> uploadMultipleImages(
            @PathVariable int propertyId,
            @RequestParam("images") List<MultipartFile> files) {

        try {
            List<String> urls = imageUploadService.uploadMultipleImages(files, propertyId);
            return ResponseEntity.ok(urls);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==============================
    // DELETE IMAGE
    // ==============================
    @DeleteMapping("/deleteImage/{imageId}")
    public ResponseEntity<?> deleteImage(@PathVariable int imageId) {
        try {
            imageUploadService.deleteImage(imageId);
            return ResponseEntity.ok("Image deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==============================
    // REPLACE IMAGE
    // ==============================
    @PutMapping("/replaceImage/{imageId}")
    public ResponseEntity<?> replaceImage(
            @PathVariable int imageId,
            @RequestParam("image") MultipartFile newImage) {

        try {
            String newUrl = imageUploadService.replaceImage(imageId, newImage);
            return ResponseEntity.ok(newUrl);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ==============================
    // REORDER IMAGES (Drag & Drop)
    // ==============================
    @PutMapping("/reorderImages/{propertyId}")
    public ResponseEntity<?> reorderImages(
            @PathVariable int propertyId,
            @RequestBody List<Integer> newOrder) {

        try {
            imageUploadService.reorderImages(propertyId, newOrder);
            return ResponseEntity.ok("Images reordered successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
