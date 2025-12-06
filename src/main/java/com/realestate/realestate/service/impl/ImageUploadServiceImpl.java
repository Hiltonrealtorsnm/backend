package com.realestate.realestate.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.realestate.realestate.model.Property;
import com.realestate.realestate.model.PropertyImage;
import com.realestate.realestate.repository.PropertyImageRepository;
import com.realestate.realestate.repository.PropertyRepository;
import com.realestate.realestate.service.ImageUploadService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ImageUploadServiceImpl implements ImageUploadService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private PropertyRepository propertyRepo;

    @Autowired
    private PropertyImageRepository imageRepo;

    private static final int MAX_IMAGES = 10;

    // --------------------------------------------------
    // UPLOAD SINGLE IMAGE TO CLOUDINARY
    // --------------------------------------------------
    @Override
    public String uploadImage(MultipartFile file, int propertyId) throws Exception {

        Property property = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new Exception("Property not found"));

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "properties/" + propertyId));

        String imageUrl = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();

        PropertyImage img = new PropertyImage();
        img.setProperty(property);
        img.setImageUrl(imageUrl);
        img.setPublicId(publicId);

        int sortOrder = imageRepo.countByPropertyPropertyId(propertyId);
        img.setSortOrder(sortOrder);

        imageRepo.save(img);

        return imageUrl;
    }

    // --------------------------------------------------
    // UPLOAD MULTIPLE IMAGES
    // --------------------------------------------------
    @Override
    public List<String> uploadMultipleImages(List<MultipartFile> files, int propertyId) throws Exception {

        Property property = propertyRepo.findById(propertyId)
                .orElseThrow(() -> new Exception("Property not found"));

        int currentCount = imageRepo.countByPropertyPropertyId(propertyId);

        if (currentCount >= MAX_IMAGES)
            throw new Exception("Maximum " + MAX_IMAGES + " images allowed!");

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {

            if (currentCount + urls.size() >= MAX_IMAGES)
                throw new Exception("Cannot upload more than " + MAX_IMAGES + " images");

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "properties/" + propertyId));

            String imageUrl = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            PropertyImage img = new PropertyImage();
            img.setProperty(property);
            img.setImageUrl(imageUrl);
            img.setPublicId(publicId);
            img.setSortOrder(currentCount + urls.size());

            imageRepo.save(img);
            urls.add(imageUrl);
        }

        return urls;
    }

    // --------------------------------------------------
    // DELETE IMAGE FROM CLOUDINARY
    // --------------------------------------------------
    @Override
    public boolean deleteImage(int imageId) throws Exception {

        PropertyImage img = imageRepo.findById(imageId)
                .orElseThrow(() -> new Exception("Image not found!"));

        cloudinary.uploader().destroy(img.getPublicId(), ObjectUtils.emptyMap());

        imageRepo.delete(img);

        return true;
    }

    // --------------------------------------------------
    // REPLACE IMAGE
    // --------------------------------------------------
    @Override
    public String replaceImage(int imageId, MultipartFile newImage) throws Exception {

        PropertyImage img = imageRepo.findById(imageId)
                .orElseThrow(() -> new Exception("Image not found!"));

        cloudinary.uploader().destroy(img.getPublicId(), ObjectUtils.emptyMap());

        Map uploadResult = cloudinary.uploader().upload(newImage.getBytes(),
                ObjectUtils.asMap("folder", "properties/" + img.getProperty().getPropertyId()));

        String newUrl = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();

        img.setImageUrl(newUrl);
        img.setPublicId(publicId);

        imageRepo.save(img);

        return newUrl;
    }

    // --------------------------------------------------
    // REORDER IMAGES
    // --------------------------------------------------
    @Override
    public void reorderImages(int propertyId, List<Integer> newOrder) throws Exception {

        List<PropertyImage> imgs = imageRepo.findByPropertyPropertyIdOrderBySortOrder(propertyId);

        if (imgs.size() != newOrder.size())
            throw new Exception("Image count mismatch!");

        for (int i = 0; i < newOrder.size(); i++) {
            Integer imageId = newOrder.get(i);

            for (PropertyImage img : imgs) {
                if (img.getImageId() == imageId) {
                    img.setSortOrder(i);
                    imageRepo.save(img);
                }
            }
        }
    }
}
