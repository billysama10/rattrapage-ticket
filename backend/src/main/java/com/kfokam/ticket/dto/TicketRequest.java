package com.kfokam.ticket.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Données envoyées par le client pour créer un ticket")
public record TicketRequest(

        @Schema(description = "Titre du ticket (obligatoire, 100 caractères maximum)", example = "Impossible de me connecter")
        @NotBlank(message = "Le titre est obligatoire")
        @Size(max = 100, message = "Le titre ne doit pas dépasser 100 caractères")
        String titre,

        @Schema(description = "Description facultative", example = "Le formulaire de connexion renvoie une erreur 500")
        String description
) {
}
