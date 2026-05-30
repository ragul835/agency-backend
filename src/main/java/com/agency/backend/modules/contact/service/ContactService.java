package com.agency.backend.modules.contact.service;

import com.agency.backend.modules.contact.dto.ContactRequest;
import com.agency.backend.modules.contact.dto.ContactResponse;
import com.agency.backend.modules.contact.dto.UpdateStatusRequest;
import com.agency.backend.modules.contact.entity.ContactStatus;
import com.agency.backend.common.dto.PagedResponse;
import org.springframework.data.domain.Pageable;

public interface ContactService {
    void submit(ContactRequest request);
    PagedResponse<ContactResponse> getAll(ContactStatus status, Pageable pageable);
    ContactResponse getById(Long id);
    ContactResponse updateStatus(Long id, UpdateStatusRequest request);
    void delete(Long id);
}
