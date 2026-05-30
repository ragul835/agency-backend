package com.agency.backend.modules.projects.repository;

import com.agency.backend.modules.projects.entity.Project;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Page<Project> findByIsActiveTrue(Pageable pageable);
    Page<Project> findByIsActiveTrueAndCategory(String category, Pageable pageable);
    Page<Project> findByIsActiveTrueAndIsFeaturedTrue(Pageable pageable);
    Page<Project> findByIsActiveTrueAndCategoryAndIsFeaturedTrue(String category, boolean featured, Pageable pageable);
    boolean existsByIdAndIsActiveTrue(Long id);
}
