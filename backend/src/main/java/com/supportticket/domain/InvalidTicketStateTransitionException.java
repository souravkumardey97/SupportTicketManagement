package com.supportticket.domain;

public class InvalidTicketStateTransitionException extends RuntimeException {

    private final TicketStatus from;
    private final TicketStatus to;

    public InvalidTicketStateTransitionException(TicketStatus from, TicketStatus to) {
        super("Cannot transition ticket from " + from + " to " + to);
        this.from = from;
        this.to = to;
    }

    public TicketStatus getFrom() {
        return from;
    }

    public TicketStatus getTo() {
        return to;
    }
}
