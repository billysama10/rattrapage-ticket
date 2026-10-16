package com.kfokam.ticket.exception;

public class TicketIntrouvableException extends RuntimeException {

    public TicketIntrouvableException(Long id) {
        super("Aucun ticket ne porte l'identifiant " + id);
    }
}
