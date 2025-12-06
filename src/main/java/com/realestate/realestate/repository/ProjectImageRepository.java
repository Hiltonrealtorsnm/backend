package com.realestate.realestate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.realestate.realestate.model.ProjectImage;

public interface ProjectImageRepository extends JpaRepository<ProjectImage, Long> {

    int countByProjectId(Long projectId);

    List<ProjectImage> findByProjectIdOrderBySortOrder(Long projectId);

    void deleteByProjectId(Long projectId);
}

