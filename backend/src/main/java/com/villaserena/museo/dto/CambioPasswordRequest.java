package com.villaserena.museo.dto;

public class CambioPasswordRequest {
    private String passwordAttuale;
    private String nuovaPassword;

    public String getPasswordAttuale() { return passwordAttuale; }
    public void setPasswordAttuale(String p) { this.passwordAttuale = p; }
    public String getNuovaPassword() { return nuovaPassword; }
    public void setNuovaPassword(String p) { this.nuovaPassword = p; }
}