package com.agency.backend.modules.services.service;

import com.agency.backend.common.dto.PagedResponse;
import com.agency.backend.modules.services.dto.ServiceRequest;
import com.agency.backend.modules.services.dto.ServiceResponse;
import org.springframework.data.domain.Pageable;

public interface ServiceService {
    PagedResponse<ServiceResponse> getAll(Pageable pageable);
    ServiceResponse getById(Long id);
    ServiceResponse create(ServiceRequest request);
    ServiceResponse update(Long id, ServiceRequest request);
    void delete(Long id);
}
