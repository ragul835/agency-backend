package com.agency.backend.modules.contact.controller;

import com.agency.backend.common.dto.ApiResponse;
import com.agency.backend.common.dto.PagedResponse;
import com.agency.backend.config.RateLimitConfig;
import com.agency.backend.modules.contact.dto.ContactRequest;
import com.agency.backend.modules.contact.dto.ContactResponse;
import com.agency.backend.modules.contact.dto.UpdateStatusRequest;
import com.agency.backend.modules.contact.entity.ContactStatus;
import com.agency.backend.modules.contact.service.ContactService;
import io.github.bucket4j.Bucket;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/contact")
@Tag(name = "Contact", description = "Contact form submissions")
public class ContactController {

    private final ContactService contactService;
    private final RateLimitConfig rateLimitConfig;

    public ContactController(ContactService contactService, RateLimitConfig rateLimitConfig) {
        this.contactService = contactService;
        this.rateLimitConfig = rateLimitConfig;
    }

    @PostMapping
    @Operation(summary = "Submit a contact form (public, rate-limited)")
    public ResponseEntity<ApiResponse<Void>> submit(@Valid @RequestBody ContactRequest request,
                                                    HttpServletRequest httpRequest) {
        String ip = rateLimitConfig.extractIp(httpRequest);
        Bucket bucket = rateLimitConfig.resolveContactBucket(ip);
        if (!bucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ApiResponse.error("Too many requests. Please try again in a minute."));
        }
        contactService.submit(request);
        return ResponseEntity.ok(ApiResponse.success("Thank you! We'll get back to you within 24 hours."));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all contact submissions (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<PagedResponse<ContactResponse>>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) ContactStatus status,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(ApiResponse.success("Submissions fetched", contactService.getAll(status, pageable)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get contact submission by id (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ContactResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Submission fetched", contactService.getById(id)));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update contact submission status (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<ContactResponse>> updateStatus(@PathVariable Long id,
                                                                      @Valid @RequestBody UpdateStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", contactService.updateStatus(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete contact submission (admin)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        contactService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Submission deleted"));
    }
}
