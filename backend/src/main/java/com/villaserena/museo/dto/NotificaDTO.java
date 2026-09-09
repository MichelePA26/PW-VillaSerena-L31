package com.villaserena.museo.dto;

import com.villaserena.museo.model.Notifica;
import java.time.LocalDateTime;

public class NotificaDTO {
    private Long id;
    private String testo;
    private boolean letta;
    private LocalDateTime data;
    private String link;

    public static NotificaDTO daEntita(Notifica n) {
        NotificaDTO dto = new NotificaDTO();
        dto.id = n.getId();
        dto.testo = n.getTesto();
        dto.letta = n.isLetta();
        dto.data = n.getData();
        dto.link = n.getLink();
        return dto;
    }

    public Long getId() { return id; }
    public String getTesto() { return testo; }
    public boolean isLetta() { return letta; }
    public String getLink() { return link; }
    public LocalDateTime getData() { return data; }
}