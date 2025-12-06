package com.realestate.realestate.service;

import com.realestate.realestate.model.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProjectService {

    // GET ALL (pagination)
    Page<Project> findAll(Pageable pageable);

    // GET ALL (without pagination)
    List<Project> findAll();

    // GET BY ID
    Project findById(Long id);

    // CREATE
    Project create(Project project);

    // UPDATE
    Project update(Long id, Project project);

    // DELETE
    boolean delete(Long id);

    // FILTER BY STATUS
    Page<Project> getAllByStatus(Project.ProjectStatus status, Pageable pageable);

    // SEARCH
    Page<Project> search(
            String city,
            String type,
            Double minPrice,
            Double maxPrice,
            Project.ProjectStatus status,
            Pageable pageable
    );

    // MARK PROJECT AS COMPLETED
    Project markAsCompleted(Long projectId);
}
