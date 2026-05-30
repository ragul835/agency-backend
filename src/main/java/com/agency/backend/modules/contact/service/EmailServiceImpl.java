package com.agency.backend.modules.contact.service;

import com.agency.backend.modules.contact.entity.ContactSubmission;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final JavaMailSender mailSender;

    @Value("${agency.admin-email}")
    private String adminEmail;

    @Value("${spring.mail.username:}")
    private String mailUsername;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public void sendNewContactNotification(ContactSubmission submission) {
        if (mailUsername == null || mailUsername.isBlank()) {
            log.info("Email not configured — skipping notification for contact submission id={}", submission.getId());
            return;
        }

        // Mock email if using default placeholder credentials
        if ("your-email@gmail.com".equals(mailUsername) || mailUsername.contains("example.com")) {
            log.info("======================================================");
            log.info("MOCK EMAIL SENT (SMTP not configured with real credentials)");
            log.info("To: {}", adminEmail);
            log.info("Subject: New Contact Submission — {} | {}", submission.getName(), submission.getService());
            log.info("Body:\nName: {}\nEmail: {}\nPhone/WhatsApp: {}\nService: {}\nMessage: {}", 
                submission.getName(), submission.getEmail(), submission.getPhone(), 
                submission.getService(), submission.getMessage());
            log.info("======================================================");
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailUsername, "NexCore Agency");
            helper.setTo(adminEmail);
            helper.setSubject("New Contact Submission — " + submission.getName() + " | " + submission.getService());
            helper.setText(buildEmailBody(submission), true);
            mailSender.send(message);
            log.info("Contact notification email sent for submission id={}", submission.getId());
        } catch (Exception e) {
            log.error("Failed to send contact notification email for submission id={}: {}", submission.getId(), e.getMessage(), e);
        }
    }

    private String buildEmailBody(ContactSubmission s) {
        String ts = s.getCreatedAt() != null ? s.getCreatedAt().format(FORMATTER) : "N/A";
        return """
                <html><body style="font-family: Arial, sans-serif; color: #333; max-width: 600px; margin: 0 auto;">
                  <h2 style="color: #2563eb;">New Contact Submission — NexCore</h2>
                  <table style="width:100%%;border-collapse:collapse;">
                    <tr><td style="padding:8px;border-bottom:1px solid #eee;font-weight:bold;">Name</td><td style="padding:8px;border-bottom:1px solid #eee;">%s</td></tr>
                    <tr><td style="padding:8px;border-bottom:1px solid #eee;font-weight:bold;">Email</td><td style="padding:8px;border-bottom:1px solid #eee;">%s</td></tr>
                    <tr><td style="padding:8px;border-bottom:1px solid #eee;font-weight:bold;">WhatsApp Number</td><td style="padding:8px;border-bottom:1px solid #eee;">%s</td></tr>
                    <tr><td style="padding:8px;border-bottom:1px solid #eee;font-weight:bold;">Service</td><td style="padding:8px;border-bottom:1px solid #eee;">%s</td></tr>
                    <tr><td style="padding:8px;border-bottom:1px solid #eee;font-weight:bold;">Submitted</td><td style="padding:8px;border-bottom:1px solid #eee;">%s</td></tr>
                  </table>
                  <h3 style="margin-top:16px;">Message</h3>
                  <p style="background:#f9f9f9;padding:12px;border-radius:4px;">%s</p>
                  <p style="color:#888;font-size:12px;margin-top:24px;">Log in to the admin panel to review and respond.</p>
                </body></html>
                """.formatted(
                s.getName(), s.getEmail(),
                orEmpty(s.getPhone()), s.getService(),
                ts, s.getMessage());
    }

    private String orEmpty(String value) {
        return value != null ? value : "—";
    }
}
