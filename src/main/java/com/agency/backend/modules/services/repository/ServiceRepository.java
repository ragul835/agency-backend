package com.agency.backend.modules.services.repository;

import com.agency.backend.modules.services.entity.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {
    Page<ServiceEntity> findByIsActiveTrue(Pageable pageable);
    boolean existsByIsActiveTrue();
}
