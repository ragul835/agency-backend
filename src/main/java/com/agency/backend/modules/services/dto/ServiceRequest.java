package com.agency.backend.modules.services.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class ServiceRequest {

    @NotBlank(message = "title is required")
    private String title;

    @NotBlank(message = "description is required")
    private String description;

    @NotBlank(message = "icon is required")
    private String icon;

    @NotEmpty(message = "features must not be empty")
    private List<String> features;

    private int displayOrder = 0;
}
