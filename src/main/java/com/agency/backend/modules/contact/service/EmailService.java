package com.agency.backend.modules.contact.service;

import com.agency.backend.modules.contact.entity.ContactSubmission;

public interface EmailService {
    void sendNewContactNotification(ContactSubmission submission);
}
