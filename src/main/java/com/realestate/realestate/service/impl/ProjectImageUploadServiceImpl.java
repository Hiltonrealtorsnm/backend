package com.realestate.realestate.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.realestate.realestate.model.Project;
import com.realestate.realestate.model.ProjectImage;
import com.realestate.realestate.repository.ProjectImageRepository;
import com.realestate.realestate.repository.ProjectRepository;
import com.realestate.realestate.service.ProjectImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ProjectImageUploadServiceImpl implements ProjectImageService {

    @Autowired
    private Cloudinary cloudinary;

    @Autowired
    private ProjectRepository projectRepo;

    @Autowired
    private ProjectImageRepository projectImageRepo;

    private static final int MAX_IMAGES = 10;

    // --------------------------------------------------
    // UPLOAD SINGLE IMAGE
    // --------------------------------------------------
    @Override
    public String uploadImage(MultipartFile file, Long projectId) throws Exception {

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new Exception("Project not found"));

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "projects/" + projectId));

        String url = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();

        int sortOrder = projectImageRepo.countByProjectId(projectId);

        ProjectImage img = new ProjectImage();
        img.setProject(project);
        img.setImageUrl(url);
        img.setPublicId(publicId);
        img.setSortOrder(sortOrder);

        projectImageRepo.save(img);

        return url;
    }

    // --------------------------------------------------
    // MULTIPLE IMAGE UPLOAD
    // --------------------------------------------------
    @Override
    public List<String> uploadMultipleImages(List<MultipartFile> files, Long projectId) throws Exception {

        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new Exception("Project not found"));

        int currentCount = projectImageRepo.countByProjectId(projectId);

        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {

            if (currentCount + urls.size() >= MAX_IMAGES)
                throw new Exception("Maximum images reached");

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("folder", "projects/" + projectId));

            String url = uploadResult.get("secure_url").toString();
            String publicId = uploadResult.get("public_id").toString();

            ProjectImage img = new ProjectImage();
            img.setProject(project);
            img.setImageUrl(url);
            img.setPublicId(publicId);
            img.setSortOrder(currentCount + urls.size());

            projectImageRepo.save(img);
            urls.add(url);
        }

        return urls;
    }

    // --------------------------------------------------
    // DELETE IMAGE
    // --------------------------------------------------
    @Override
    public boolean deleteImage(Long imageId) throws Exception {

        ProjectImage img = projectImageRepo.findById(imageId)
                .orElseThrow(() -> new Exception("Image not found"));

        cloudinary.uploader().destroy(img.getPublicId(), ObjectUtils.emptyMap());

        projectImageRepo.delete(img);

        return true;
    }

    // --------------------------------------------------
    // REPLACE IMAGE
    // --------------------------------------------------
    @Override
    public String replaceImage(Long imageId, MultipartFile file) throws Exception {

        ProjectImage img = projectImageRepo.findById(imageId)
                .orElseThrow(() -> new Exception("Image not found"));

        cloudinary.uploader().destroy(img.getPublicId(), ObjectUtils.emptyMap());

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                ObjectUtils.asMap("folder", "projects/" + img.getProject().getId()));

        String url = uploadResult.get("secure_url").toString();
        String publicId = uploadResult.get("public_id").toString();

        img.setImageUrl(url);
        img.setPublicId(publicId);

        projectImageRepo.save(img);

        return url;
    }

    // --------------------------------------------------
    // REORDER IMAGES
    // --------------------------------------------------
    @Override
    public void reorderImages(Long projectId, List<Long> newOrder) throws Exception {

        List<ProjectImage> imgs = projectImageRepo.findByProjectIdOrderBySortOrder(projectId);

        if (imgs.size() != newOrder.size())
            throw new Exception("Image count mismatch");

        for (int i = 0; i < newOrder.size(); i++) {

            Long imageId = newOrder.get(i);

            for (ProjectImage img : imgs) {
                if (img.getImageId().equals(imageId)) {
                    img.setSortOrder(i);
                    projectImageRepo.save(img);
                }
            }
        }
    }
}
