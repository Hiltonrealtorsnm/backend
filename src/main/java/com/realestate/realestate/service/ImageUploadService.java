package com.realestate.realestate.service;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface ImageUploadService {
    String uploadImage(MultipartFile file, int propertyId) throws Exception;

    List<String> uploadMultipleImages(List<MultipartFile> files, int propertyId) throws Exception;

	boolean deleteImage(int imageId) throws Exception;

	String replaceImage(int imageId, MultipartFile newImage) throws Exception;

	void reorderImages(int propertyId, List<Integer> newOrder) throws Exception;
}
