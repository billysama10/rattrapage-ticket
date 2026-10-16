package com.kfokam.ticket.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GestionnaireErreurs {

    /** Validation du corps (titre absent, trop long...) : 400 avec la raison. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErreurApi> validation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(erreur -> erreur.getDefaultMessage())
                .distinct()
                .collect(Collectors.joining(" ; "));
        return reponse(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErreurApi> corpsIllisible(HttpMessageNotReadableException e) {
        return reponse(HttpStatus.BAD_REQUEST, "Le corps de la requête est absent ou n'est pas un JSON valide");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErreurApi> parametreInvalide(MethodArgumentTypeMismatchException e) {
        return reponse(HttpStatus.BAD_REQUEST,
                "Valeur invalide pour le paramètre « " + e.getName() + " » : " + e.getValue()
                        + " (valeurs acceptées : OUVERT, EN_COURS, FERME)");
    }

    @ExceptionHandler(TicketIntrouvableException.class)
    public ResponseEntity<ErreurApi> introuvable(TicketIntrouvableException e) {
        return reponse(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /** RG-02, RG-03 : règle de gestion violée. */
    @ExceptionHandler(RegleGestionException.class)
    public ResponseEntity<ErreurApi> regleViolee(RegleGestionException e) {
        return reponse(HttpStatus.CONFLICT, e.getMessage());
    }

    private ResponseEntity<ErreurApi> reponse(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(ErreurApi.de(status.value(), status.getReasonPhrase(), message));
    }
}
