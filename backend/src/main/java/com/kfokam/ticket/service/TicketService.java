package com.kfokam.ticket.service;

import com.kfokam.ticket.dto.TicketRequest;
import com.kfokam.ticket.dto.TicketResponse;
import com.kfokam.ticket.exception.RegleGestionException;
import com.kfokam.ticket.exception.TicketIntrouvableException;
import com.kfokam.ticket.model.StatutTicket;
import com.kfokam.ticket.model.Ticket;
import com.kfokam.ticket.repository.TicketRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository repository;

    public TicketService(TicketRepository repository) {
        this.repository = repository;
    }

    /** RG-01 : statut OUVERT à la création. RG-02 : titre unique. */
    @Transactional
    public TicketResponse creer(TicketRequest requete) {
        String titre = requete.titre().trim();

        if (repository.existsByTitreIgnoreCase(titre)) {
            throw titreDejaUtilise(titre);
        }

        Ticket ticket = new Ticket();
        ticket.setTitre(titre);
        ticket.setDescription(requete.description());
        ticket.setStatut(StatutTicket.OUVERT);

        try {
            return versReponse(repository.saveAndFlush(ticket));
        } catch (DataIntegrityViolationException e) {
            // deux créations simultanées avec le même titre : la contrainte d'unicité de la base tranche
            throw titreDejaUtilise(titre);
        }
    }

    @Transactional(readOnly = true)
    public List<TicketResponse> lister(StatutTicket statut) {
        List<Ticket> tickets = (statut == null)
                ? repository.findAllByOrderByDateCreationDesc()
                : repository.findByStatutOrderByDateCreationDesc(statut);
        return tickets.stream().map(this::versReponse).toList();
    }

    /** RG-03 : fermeture possible seulement depuis OUVERT ou EN_COURS ; un ticket FERME ne change plus. */
    @Transactional
    public TicketResponse fermer(Long id) {
        Ticket ticket = repository.findById(id)
                .orElseThrow(() -> new TicketIntrouvableException(id));

        if (ticket.getStatut() == StatutTicket.FERME) {
            throw new RegleGestionException(
                    "RG-03 : le ticket " + id + " est déjà FERME, son état ne peut plus changer");
        }

        ticket.setStatut(StatutTicket.FERME);
        return versReponse(repository.save(ticket));
    }

    private RegleGestionException titreDejaUtilise(String titre) {
        return new RegleGestionException("RG-02 : un ticket intitulé « " + titre + " » existe déjà");
    }

    private TicketResponse versReponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitre(),
                ticket.getDescription(),
                ticket.getStatut(),
                ticket.getDateCreation());
    }
}
