package com.agency.backend.modules.projects.dto;

import com.agency.backend.modules.projects.entity.Project;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResponse {

    private Long id;
    private String title;
    private String description;
    private String category;
    private List<String> technologies;
    private String imageUrl;
    private String liveUrl;
    private String githubUrl;
    private boolean isFeatured;
    private int displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ProjectResponse from(Project project) {
        return ProjectResponse.builder()
                .id(project.getId())
                .title(project.getTitle())
                .description(project.getDescription())
                .category(project.getCategory())
                .technologies(project.getTechnologies())
                .imageUrl(project.getImageUrl())
                .liveUrl(project.getLiveUrl())
                .githubUrl(project.getGithubUrl())
                .isFeatured(project.isFeatured())
                .displayOrder(project.getDisplayOrder())
                .createdAt(project.getCreatedAt())
                .updatedAt(project.getUpdatedAt())
                .build();
    }
}
