package com.villaserena.museo.security;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

public class AesUtil {

    private static final int LUNGHEZZA_TAG_GCM = 128;
    private static final int LUNGHEZZA_IV = 12;

    public static String cifra(String testoInChiaro, SecretKey chiave) {
        try {
            byte[] iv = new byte[LUNGHEZZA_IV];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, chiave, new GCMParameterSpec(LUNGHEZZA_TAG_GCM, iv));
            byte[] testoCifrato = cipher.doFinal(testoInChiaro.getBytes(StandardCharsets.UTF_8));

            byte[] risultato = new byte[iv.length + testoCifrato.length];
            System.arraycopy(iv, 0, risultato, 0, iv.length);
            System.arraycopy(testoCifrato, 0, risultato, iv.length, testoCifrato.length);

            return Base64.getEncoder().encodeToString(risultato);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la cifratura del campo", e);
        }
    }

    public static String decifra(String testoCifratoBase64, SecretKey chiave) {
        try {
            byte[] dati = Base64.getDecoder().decode(testoCifratoBase64);

            byte[] iv = new byte[LUNGHEZZA_IV];
            byte[] testoCifrato = new byte[dati.length - LUNGHEZZA_IV];
            System.arraycopy(dati, 0, iv, 0, LUNGHEZZA_IV);
            System.arraycopy(dati, LUNGHEZZA_IV, testoCifrato, 0, testoCifrato.length);

            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, chiave, new GCMParameterSpec(LUNGHEZZA_TAG_GCM, iv));
            byte[] testoInChiaro = cipher.doFinal(testoCifrato);

            return new String(testoInChiaro, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante la decifratura del campo", e);
        }
    }

    public static SecretKey chiaveDaStringaBase64(String chiaveBase64) {
        byte[] decodificata = Base64.getDecoder().decode(chiaveBase64);
        return new SecretKeySpec(decodificata, "AES");
    }
}