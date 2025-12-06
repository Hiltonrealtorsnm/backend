package com.realestate.realestate.repository;

import com.realestate.realestate.model.Project;
import com.realestate.realestate.model.Project.ProjectStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    Page<Project> findByStatus(ProjectStatus status, Pageable pageable);

    @Query("""
            SELECT p FROM Project p
            WHERE (:city IS NULL OR p.location LIKE %:city%)
              AND (:type IS NULL OR p.type = :type)
              AND (:minPrice IS NULL OR p.priceBigint >= :minPrice)
              AND (:maxPrice IS NULL OR p.priceBigint <= :maxPrice)
              AND (:status IS NULL OR p.status = :status)
            """)
    Page<Project> search(
            String city,
            String type,
            Double minPrice,
            Double maxPrice,
            ProjectStatus status,
            Pageable pageable
    );
}
