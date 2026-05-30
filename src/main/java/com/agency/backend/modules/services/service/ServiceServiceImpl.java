package com.agency.backend.modules.services.service;

import com.agency.backend.common.dto.PagedResponse;
import com.agency.backend.common.exception.ResourceNotFoundException;
import com.agency.backend.modules.services.dto.ServiceRequest;
import com.agency.backend.modules.services.dto.ServiceResponse;
import com.agency.backend.modules.services.entity.ServiceEntity;
import com.agency.backend.modules.services.repository.ServiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServiceServiceImpl implements ServiceService {

    private static final Logger log = LoggerFactory.getLogger(ServiceServiceImpl.class);

    private final ServiceRepository serviceRepository;

    public ServiceServiceImpl(ServiceRepository serviceRepository) {
        this.serviceRepository = serviceRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ServiceResponse> getAll(Pageable pageable) {
        Page<ServiceEntity> page = serviceRepository.findByIsActiveTrue(pageable);
        return PagedResponse.from(page.map(ServiceResponse::from));
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResponse getById(Long id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .filter(ServiceEntity::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        return ServiceResponse.from(entity);
    }

    @Override
    @Transactional
    public ServiceResponse create(ServiceRequest request) {
        ServiceEntity entity = ServiceEntity.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .icon(request.getIcon())
                .features(request.getFeatures())
                .displayOrder(request.getDisplayOrder())
                .build();
        ServiceEntity saved = serviceRepository.save(entity);
        log.info("Admin created service id={}, title={}", saved.getId(), saved.getTitle());
        return ServiceResponse.from(saved);
    }

    @Override
    @Transactional
    public ServiceResponse update(Long id, ServiceRequest request) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setIcon(request.getIcon());
        entity.setFeatures(request.getFeatures());
        entity.setDisplayOrder(request.getDisplayOrder());
        log.info("Admin updated service id={}", id);
        return ServiceResponse.from(serviceRepository.save(entity));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ServiceEntity entity = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service", id));
        entity.setActive(false);
        serviceRepository.save(entity);
        log.info("Admin soft-deleted service id={}", id);
    }
}
