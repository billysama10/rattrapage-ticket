package com.kfokam.ticket.controller;

import com.kfokam.ticket.dto.TicketRequest;
import com.kfokam.ticket.dto.TicketResponse;
import com.kfokam.ticket.exception.ErreurApi;
import com.kfokam.ticket.model.StatutTicket;
import com.kfokam.ticket.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Tickets", description = "Suivi des demandes du service support")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Créer un ticket", description = "Le ticket est créé avec le statut OUVERT. La date de création est fixée par le serveur.")
    @ApiResponse(responseCode = "201", description = "Ticket créé")
    @ApiResponse(responseCode = "400", description = "Titre absent ou de plus de 100 caractères",
            content = @Content(schema = @Schema(implementation = ErreurApi.class)))
    @ApiResponse(responseCode = "409", description = "RG-02 : un ticket porte déjà ce titre",
            content = @Content(schema = @Schema(implementation = ErreurApi.class)))
    public TicketResponse creer(@Valid @RequestBody TicketRequest requete) {
        return service.creer(requete);
    }

    @GetMapping
    @Operation(summary = "Lister les tickets", description = "Du plus récent au plus ancien, filtrable par statut.")
    @ApiResponse(responseCode = "200", description = "Liste des tickets (éventuellement vide)")
    @ApiResponse(responseCode = "400", description = "Statut inconnu",
            content = @Content(schema = @Schema(implementation = ErreurApi.class)))
    public List<TicketResponse> lister(
            @Parameter(description = "Filtre : OUVERT, EN_COURS ou FERME. Absent = tous les tickets")
            @RequestParam(required = false) StatutTicket statut) {
        return service.lister(statut);
    }

    @PatchMapping("/{id}/fermer")
    @Operation(summary = "Fermer un ticket", description = "Possible uniquement si le ticket est OUVERT ou EN_COURS.")
    @ApiResponse(responseCode = "200", description = "Ticket fermé")
    @ApiResponse(responseCode = "404", description = "Aucun ticket avec cet identifiant",
            content = @Content(schema = @Schema(implementation = ErreurApi.class)))
    @ApiResponse(responseCode = "409", description = "RG-03 : le ticket est déjà FERME",
            content = @Content(schema = @Schema(implementation = ErreurApi.class)))
    public TicketResponse fermer(@PathVariable Long id) {
        return service.fermer(id);
    }
}
