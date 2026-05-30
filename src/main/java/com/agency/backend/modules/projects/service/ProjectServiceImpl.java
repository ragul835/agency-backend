package com.agency.backend.modules.projects.service;

import com.agency.backend.common.dto.PagedResponse;
import com.agency.backend.common.exception.ResourceNotFoundException;
import com.agency.backend.modules.projects.dto.ProjectRequest;
import com.agency.backend.modules.projects.dto.ProjectResponse;
import com.agency.backend.modules.projects.entity.Project;
import com.agency.backend.modules.projects.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectServiceImpl implements ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ProjectResponse> getAll(String category, Boolean featured, Pageable pageable) {
        Page<Project> page;
        if (category != null && featured != null && featured) {
            page = projectRepository.findByIsActiveTrueAndCategoryAndIsFeaturedTrue(category, true, pageable);
        } else if (category != null) {
            page = projectRepository.findByIsActiveTrueAndCategory(category, pageable);
        } else if (featured != null && featured) {
            page = projectRepository.findByIsActiveTrueAndIsFeaturedTrue(pageable);
        } else {
            page = projectRepository.findByIsActiveTrue(pageable);
        }
        return PagedResponse.from(page.map(ProjectResponse::from));
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getById(Long id) {
        Project project = projectRepository.findById(id)
                .filter(Project::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        return ProjectResponse.from(project);
    }

    @Override
    @Transactional
    public ProjectResponse create(ProjectRequest request) {
        Project project = Project.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .technologies(request.getTechnologies())
                .imageUrl(request.getImageUrl())
                .liveUrl(request.getLiveUrl())
                .githubUrl(request.getGithubUrl())
                .isFeatured(request.isFeatured())
                .displayOrder(request.getDisplayOrder())
                .build();
        Project saved = projectRepository.save(project);
        log.info("Admin created project id={}, title={}", saved.getId(), saved.getTitle());
        return ProjectResponse.from(saved);
    }

    @Override
    @Transactional
    public ProjectResponse update(Long id, ProjectRequest request) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        project.setTitle(request.getTitle());
        project.setDescription(request.getDescription());
        project.setCategory(request.getCategory());
        project.setTechnologies(request.getTechnologies());
        project.setImageUrl(request.getImageUrl());
        project.setLiveUrl(request.getLiveUrl());
        project.setGithubUrl(request.getGithubUrl());
        project.setFeatured(request.isFeatured());
        project.setDisplayOrder(request.getDisplayOrder());
        log.info("Admin updated project id={}", id);
        return ProjectResponse.from(projectRepository.save(project));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
        project.setActive(false);
        projectRepository.save(project);
        log.info("Admin soft-deleted project id={}", id);
    }
}
