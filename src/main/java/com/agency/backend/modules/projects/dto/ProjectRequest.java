package com.agency.backend.modules.projects.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ProjectRequest {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "category is required")
    private String category;

    @NotEmpty(message = "technologies must not be empty")
    private List<String> technologies;

    private String imageUrl;
    private String liveUrl;
    private String githubUrl;
    private boolean isFeatured = false;
    private int displayOrder = 0;
}
