package com.supportticket.api;

import com.supportticket.api.dto.CreateTicketRequest;
import com.supportticket.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class ExceptionTestController {

    @PostMapping("/test/validation")
    void validation(@Valid @RequestBody CreateTicketRequest request) {
    }

    @PostMapping("/test/not-found")
    void notFound() {
        throw new ResourceNotFoundException("Ticket not found");
    }

    @PostMapping("/test/forbidden")
    void forbidden() {
        throw new AccessDeniedException("Forbidden");
    }

    @PostMapping("/test/data-integrity")
    void dataIntegrity() {
        throw new DataIntegrityViolationException(
                "constraint violation",
                new RuntimeException(
                        "ERROR: duplicate key value violates unique constraint \"users_pkey\" "
                                + "Detail: Key (id)=(4) already exists."));
    }

    @PostMapping("/test/unexpected")
    void unexpected() {
        throw new IllegalStateException("boom");
    }
}
