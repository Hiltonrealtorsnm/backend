package com.realestate.realestate.service.impl;

import com.realestate.realestate.model.Project;
import com.realestate.realestate.model.ProjectImage;
import com.realestate.realestate.repository.ProjectImageRepository;
import com.realestate.realestate.repository.ProjectRepository;
import com.realestate.realestate.service.ProjectService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectImageRepository projectImageRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository,
                              ProjectImageRepository projectImageRepository) {
        this.projectRepository = projectRepository;
        this.projectImageRepository = projectImageRepository;
    }

    // FETCH ALL (pagination)
    @Override
    public Page<Project> findAll(Pageable pageable) {
        return projectRepository.findAll(pageable);
    }

    // FETCH ALL (full list)
    @Override
    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    // FETCH ONE
    @Override
    public Project findById(Long id) {
        return projectRepository.findById(id).orElse(null);
    }

    // CREATE
    @Override
    @Transactional
    public Project create(Project project) {

        project.setId(null); // Ensure ID is auto-generated

        Project saved = projectRepository.save(project);

        // Save images
        if (project.getImages() != null) {
            for (ProjectImage img : project.getImages()) {
                img.setProject(saved);
            }
            projectImageRepository.saveAll(project.getImages());
        }

        return saved;
    }

    // UPDATE
    @Override
    @Transactional
    public Project update(Long id, Project updated) {

        return projectRepository.findById(id).map(project -> {

            project.setTitle(updated.getTitle());
            project.setDescription(updated.getDescription());
            project.setLocation(updated.getLocation());
            project.setPriceRange(updated.getPriceRange());
            project.setPriceBigint(updated.getPriceBigint());
            project.setStatus(updated.getStatus());
            project.setType(updated.getType());

            // Clear old images
            projectImageRepository.deleteByProjectId(project.getId());

            // Save new images
            if (updated.getImages() != null) {
                for (ProjectImage img : updated.getImages()) {
                    img.setProject(project);
                }
                projectImageRepository.saveAll(updated.getImages());
                project.setImages(updated.getImages());
            }

            return projectRepository.save(project);

        }).orElse(null);
    }

    // DELETE
    @Override
    @Transactional
    public boolean delete(Long id) {

        if (!projectRepository.existsById(id)) return false;

        projectImageRepository.deleteByProjectId(id);
        projectRepository.deleteById(id);

        return true;
    }

    // FILTER BY STATUS
    @Override
    public Page<Project> getAllByStatus(Project.ProjectStatus status, Pageable pageable) {
        return projectRepository.findByStatus(status, pageable);
    }

    // SEARCH PROJECTS
    @Override
    public Page<Project> search(String city, String type, Double minPrice, Double maxPrice,
                                Project.ProjectStatus status, Pageable pageable) {
        return projectRepository.search(city, type, minPrice, maxPrice, status, pageable);
    }

    // MARK AS COMPLETED
    @Override
    @Transactional
    public Project markAsCompleted(Long id) {
        return projectRepository.findById(id).map(project -> {
            project.setStatus(Project.ProjectStatus.COMPLETED);
            return projectRepository.save(project);
        }).orElse(null);
    }
}
