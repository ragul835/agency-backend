package com.agency.backend.modules.contact.repository;

import com.agency.backend.modules.contact.entity.ContactStatus;
import com.agency.backend.modules.contact.entity.ContactSubmission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<ContactSubmission, Long> {
    Page<ContactSubmission> findByStatus(ContactStatus status, Pageable pageable);
}
