package com.villaserena.museo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${app.upload-dir}")
    private String uploadDir;

    // Riservato a Operatore/HR: solo chi gestisce il catalogo può caricare immagini
    @PostMapping
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public ResponseEntity<?> carica(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("errore", "Nessun file ricevuto"));
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(Map.of("errore", "Il file deve essere un'immagine"));
        }

        try {
            Path cartella = Paths.get(uploadDir);
            if (!Files.exists(cartella)) {
                Files.createDirectories(cartella);
            }

            String estensione = "";
            String nomeOriginale = file.getOriginalFilename();
            if (nomeOriginale != null && nomeOriginale.contains(".")) {
                estensione = nomeOriginale.substring(nomeOriginale.lastIndexOf("."));
            }
            String nomeFile = UUID.randomUUID() + estensione;

            Path destinazione = cartella.resolve(nomeFile);
            Files.copy(file.getInputStream(), destinazione);

            return ResponseEntity.ok(Map.of("url", "/uploads/" + nomeFile));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("errore", "Salvataggio del file non riuscito"));
        }
    }
}