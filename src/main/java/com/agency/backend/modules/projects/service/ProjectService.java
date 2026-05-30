package com.agency.backend.modules.projects.service;

import com.agency.backend.common.dto.PagedResponse;
import com.agency.backend.modules.projects.dto.ProjectRequest;
import com.agency.backend.modules.projects.dto.ProjectResponse;
import org.springframework.data.domain.Pageable;

public interface ProjectService {
    PagedResponse<ProjectResponse> getAll(String category, Boolean featured, Pageable pageable);
    ProjectResponse getById(Long id);
    ProjectResponse create(ProjectRequest request);
    ProjectResponse update(Long id, ProjectRequest request);
    void delete(Long id);
}
