package com.villaserena.museo.dto;

public class AuthResponse {
    private String token;
    private String ruolo;
    public AuthResponse(String token, String ruolo) { this.token = token; this.ruolo = ruolo; }
    public String getToken() { return token; }
    public String getRuolo() { return ruolo; }
}
