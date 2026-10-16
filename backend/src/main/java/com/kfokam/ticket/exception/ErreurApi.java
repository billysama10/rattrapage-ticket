package com.kfokam.ticket.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "Corps renvoyé en cas d'erreur")
public record ErreurApi(
        int status,
        String erreur,
        String message,
        LocalDateTime horodatage
) {
    public static ErreurApi de(int status, String erreur, String message) {
        return new ErreurApi(status, erreur, message, LocalDateTime.now());
    }
}
