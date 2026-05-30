package com.agency.backend.modules.contact.dto;

import com.agency.backend.modules.contact.entity.ContactStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateStatusRequest {

    @NotNull(message = "status is required")
    private ContactStatus status;

    private String adminNotes;
}
