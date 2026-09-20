package com.supportticket.service;

import com.supportticket.api.dto.CreateTicketRequest;
import com.supportticket.api.dto.TicketDetailResponse;
import com.supportticket.api.dto.TicketPageResponse;
import com.supportticket.api.dto.TicketResponse;
import com.supportticket.api.dto.UpdateTicketRequest;
import com.supportticket.domain.TicketStatus;

public interface TicketService {

    TicketPageResponse listTickets(String keyword, TicketStatus status, int page, int size, String sort);

    TicketResponse createTicket(CreateTicketRequest request);

    TicketDetailResponse getTicket(Long ticketId);

    TicketResponse updateTicket(Long ticketId, UpdateTicketRequest request);

    TicketResponse updateTicketStatus(Long ticketId, TicketStatus status);
}
