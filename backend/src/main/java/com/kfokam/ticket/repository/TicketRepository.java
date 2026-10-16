package com.kfokam.ticket.repository;

import com.kfokam.ticket.model.StatutTicket;
import com.kfokam.ticket.model.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {

    boolean existsByTitreIgnoreCase(String titre);

    List<Ticket> findByStatutOrderByDateCreationDesc(StatutTicket statut);

    List<Ticket> findAllByOrderByDateCreationDesc();
}
