package com.supportticket.domain;

import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Service
public class TicketStatusTransitionService {

    private static final Map<TicketStatus, Set<TicketStatus>> ALLOWED = buildAllowed();

    private static Map<TicketStatus, Set<TicketStatus>> buildAllowed() {
        Map<TicketStatus, Set<TicketStatus>> map = new EnumMap<>(TicketStatus.class);
        map.put(TicketStatus.OPEN, EnumSet.of(TicketStatus.IN_PROGRESS, TicketStatus.CANCELLED));
        map.put(TicketStatus.IN_PROGRESS, EnumSet.of(TicketStatus.RESOLVED, TicketStatus.CANCELLED));
        map.put(TicketStatus.RESOLVED, EnumSet.of(TicketStatus.CLOSED));
        map.put(TicketStatus.CLOSED, EnumSet.noneOf(TicketStatus.class));
        map.put(TicketStatus.CANCELLED, EnumSet.noneOf(TicketStatus.class));
        return map;
    }

    public TicketStatus transition(TicketStatus current, TicketStatus target) {
        if (!isAllowed(current, target)) {
            throw new InvalidTicketStateTransitionException(current, target);
        }
        return target;
    }

    public boolean isAllowed(TicketStatus current, TicketStatus target) {
        return ALLOWED.getOrDefault(current, EnumSet.noneOf(TicketStatus.class)).contains(target);
    }
}
