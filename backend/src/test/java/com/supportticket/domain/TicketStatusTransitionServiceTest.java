package com.supportticket.domain;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TicketStatusTransitionServiceTest {

    private final TicketStatusTransitionService service = new TicketStatusTransitionService();

    @ParameterizedTest
    @MethodSource("allowedTransitions")
    void shouldAllowValidTransitions(TicketStatus from, TicketStatus to) {
        assertEquals(to, service.transition(from, to));
    }

    @ParameterizedTest
    @MethodSource("forbiddenTransitions")
    void shouldRejectInvalidTransitions(TicketStatus from, TicketStatus to) {
        assertThrows(InvalidTicketStateTransitionException.class, () -> service.transition(from, to));
    }

    static Stream<Arguments> allowedTransitions() {
        return Stream.of(
                Arguments.of(TicketStatus.OPEN, TicketStatus.IN_PROGRESS),
                Arguments.of(TicketStatus.OPEN, TicketStatus.CANCELLED),
                Arguments.of(TicketStatus.IN_PROGRESS, TicketStatus.RESOLVED),
                Arguments.of(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED),
                Arguments.of(TicketStatus.RESOLVED, TicketStatus.CLOSED)
        );
    }

    static Stream<Arguments> forbiddenTransitions() {
        return Stream.of(
                Arguments.of(TicketStatus.CLOSED, TicketStatus.OPEN),
                Arguments.of(TicketStatus.RESOLVED, TicketStatus.OPEN),
                Arguments.of(TicketStatus.CANCELLED, TicketStatus.OPEN),
                Arguments.of(TicketStatus.CLOSED, TicketStatus.IN_PROGRESS),
                Arguments.of(TicketStatus.RESOLVED, TicketStatus.CANCELLED),
                Arguments.of(TicketStatus.OPEN, TicketStatus.CLOSED),
                Arguments.of(TicketStatus.OPEN, TicketStatus.RESOLVED)
        );
    }
}
