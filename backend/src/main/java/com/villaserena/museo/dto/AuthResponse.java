package com.villaserena.museo.dto;

public class AuthResponse {
    private String token;
    private String ruolo;
    private Long dipendenteId;
    public AuthResponse(String token, String ruolo, Long dipendenteId) {
        this.token = token;
        this.ruolo = ruolo;
        this.dipendenteId = dipendenteId;
    }
    public String getToken() { return token; }
    public String getRuolo() { return ruolo; }
    public Long getDipendenteId() { return dipendenteId; }
}
