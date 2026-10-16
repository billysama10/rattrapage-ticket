package com.kfokam.ticket.dto;

import com.kfokam.ticket.model.StatutTicket;

import java.time.LocalDateTime;

public record TicketResponse(
        Long id,
        String titre,
        String description,
        StatutTicket statut,
        LocalDateTime dateCreation
) {
}
