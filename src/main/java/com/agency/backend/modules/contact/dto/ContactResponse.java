package com.agency.backend.modules.contact.dto;

import com.agency.backend.modules.contact.entity.ContactStatus;
import com.agency.backend.modules.contact.entity.ContactSubmission;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactResponse {

    private Long id;
    private String name;
    private String email;
    private String phone;
    private String service;
    private String message;
    private ContactStatus status;
    private String adminNotes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ContactResponse from(ContactSubmission submission) {
        return ContactResponse.builder()
                .id(submission.getId())
                .name(submission.getName())
                .email(submission.getEmail())
                .phone(submission.getPhone())
                .service(submission.getService())
                .message(submission.getMessage())
                .status(submission.getStatus())
                .adminNotes(submission.getAdminNotes())
                .createdAt(submission.getCreatedAt())
                .updatedAt(submission.getUpdatedAt())
                .build();
    }
}
