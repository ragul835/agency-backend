package com.agency.backend.modules.contact.service;

import com.agency.backend.common.dto.PagedResponse;
import com.agency.backend.common.exception.ResourceNotFoundException;
import com.agency.backend.modules.contact.dto.ContactRequest;
import com.agency.backend.modules.contact.dto.ContactResponse;
import com.agency.backend.modules.contact.dto.UpdateStatusRequest;
import com.agency.backend.modules.contact.entity.ContactStatus;
import com.agency.backend.modules.contact.entity.ContactSubmission;
import com.agency.backend.modules.contact.repository.ContactRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactServiceImpl implements ContactService {

    private static final Logger log = LoggerFactory.getLogger(ContactServiceImpl.class);

    private final ContactRepository contactRepository;
    private final EmailService emailService;

    public ContactServiceImpl(ContactRepository contactRepository, EmailService emailService) {
        this.contactRepository = contactRepository;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public void submit(ContactRequest request) {
        ContactSubmission submission = ContactSubmission.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .service(request.getService())
                .message(request.getMessage())
                .build();
        ContactSubmission saved = contactRepository.save(submission);
        log.info("Contact submission saved: id={}, email={}, service={}", saved.getId(), saved.getEmail(), saved.getService());
        emailService.sendNewContactNotification(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<ContactResponse> getAll(ContactStatus status, Pageable pageable) {
        Page<ContactSubmission> page = (status != null)
                ? contactRepository.findByStatus(status, pageable)
                : contactRepository.findAll(pageable);
        return PagedResponse.from(page.map(ContactResponse::from));
    }

    @Override
    @Transactional(readOnly = true)
    public ContactResponse getById(Long id) {
        ContactSubmission submission = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactSubmission", id));
        return ContactResponse.from(submission);
    }

    @Override
    @Transactional
    public ContactResponse updateStatus(Long id, UpdateStatusRequest request) {
        ContactSubmission submission = contactRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ContactSubmission", id));
        submission.setStatus(request.getStatus());
        if (request.getAdminNotes() != null) {
            submission.setAdminNotes(request.getAdminNotes());
        }
        log.info("Admin updated contact id={} status to {}", id, request.getStatus());
        return ContactResponse.from(contactRepository.save(submission));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!contactRepository.existsById(id)) {
            throw new ResourceNotFoundException("ContactSubmission", id);
        }
        contactRepository.deleteById(id);
        log.info("Admin deleted contact submission id={}", id);
    }
}
