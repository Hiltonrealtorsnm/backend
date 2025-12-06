package com.realestate.realestate.controller;

import com.realestate.realestate.model.Project;
import com.realestate.realestate.service.ProjectService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/project")
@CrossOrigin
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    // ==============================
    // ADD PROJECT
    // ==============================
    @PostMapping("/add")
    public ResponseEntity<?> addProject(@RequestBody Project project) {
        Project saved = projectService.create(project);
        return ResponseEntity.ok(saved);
    }

    // ==============================
    // UPDATE PROJECT
    // ==============================
    @PutMapping("/update/{projectId}")
    public ResponseEntity<?> updateProject(
            @PathVariable Long projectId,
            @RequestBody Project projectRequest) {

        Project updated = projectService.update(projectId, projectRequest);
        if (updated == null)
            return ResponseEntity.badRequest().body("Project not found");

        return ResponseEntity.ok(updated);
    }

    // ==============================
    // GET PROJECT BY ID
    // ==============================
    @GetMapping("/{projectId}")
    public ResponseEntity<?> getById(@PathVariable Long projectId) {
        Project p = projectService.findById(projectId);

        if (p == null)
            return ResponseEntity.badRequest().body("Project not found");

        return ResponseEntity.ok(p);
    }

    // ==============================
    // GET ALL PROJECTS (pagination + sort)
    // ==============================
    @GetMapping("/all")
    public ResponseEntity<?> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sort)));
        Page<Project> list = projectService.findAll(pageable);

        return ResponseEntity.ok(list);
    }

    // ==============================
    // GET PROJECTS BY STATUS
    // ==============================
    @GetMapping("/status")
    public ResponseEntity<?> getByStatus(
            @RequestParam Project.ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sort)));
        Page<Project> list = projectService.getAllByStatus(status, pageable);

        return ResponseEntity.ok(list);
    }

    // ==============================
    // SEARCH PROJECTS
    // ==============================
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Project.ProjectStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,desc") String[] sort) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(createSortOrder(sort)));
        Page<Project> results =
                projectService.search(city, type, minPrice, maxPrice, status, pageable);

        return ResponseEntity.ok(results);
    }

    // ==============================
    // MARK PROJECT AS COMPLETED
    // ==============================
    @PutMapping("/complete/{projectId}")
    public ResponseEntity<?> markComplete(@PathVariable Long projectId) {
        Project updated = projectService.markAsCompleted(projectId);

        if (updated == null)
            return ResponseEntity.badRequest().body("Project not found");

        return ResponseEntity.ok(updated);
    }

    // ==============================
    // DELETE PROJECT
    // ==============================
    @DeleteMapping("/delete/{projectId}")
    public ResponseEntity<?> deleteProject(@PathVariable Long projectId) {
        boolean removed = projectService.delete(projectId);

        if (!removed)
            return ResponseEntity.badRequest().body("Project not found");

        return ResponseEntity.ok("Project deleted successfully");
    }

    // ==============================
    // SAFE SORTING UTILITY (FIXED)
    // ==============================
    private Sort.Order createSortOrder(String[] sort) {

        // Allow sorting only by allowed fields
        String sortField = sort.length >= 1 ? sort[0] : "id";

        // VALID sortable fields
        if (!sortField.equals("id") && !sortField.equals("createdAt") && !sortField.equals("priceBigint")) {
            sortField = "id"; // Auto fallback
        }

        Sort.Direction direction =
                (sort.length == 2 && sort[1].equalsIgnoreCase("asc"))
                        ? Sort.Direction.ASC
                        : Sort.Direction.DESC;

        return new Sort.Order(direction, sortField);
    }
}
