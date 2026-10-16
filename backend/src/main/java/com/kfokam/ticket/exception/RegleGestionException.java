package com.kfokam.ticket.exception;

/** Une règle de gestion (RG-xx) est violée : traduite en 409. */
public class RegleGestionException extends RuntimeException {

    public RegleGestionException(String message) {
        super(message);
    }
}
