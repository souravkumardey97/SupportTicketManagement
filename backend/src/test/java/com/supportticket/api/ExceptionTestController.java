package com.supportticket.api;

import com.supportticket.api.dto.CreateTicketRequest;
import com.supportticket.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
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
}
