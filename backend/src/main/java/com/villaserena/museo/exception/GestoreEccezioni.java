package com.villaserena.museo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

// Intercetta le RuntimeException lanciate dai service 
// (es. "Capienza massima superata", "Evento non trovato") e le trasforma in una risposta pulita
//con status 400 e un messaggio leggibile, invece di un generico 500.
@RestControllerAdvice
public class GestoreEccezioni {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> gestisciRuntimeException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("errore", e.getMessage()));
    }
}