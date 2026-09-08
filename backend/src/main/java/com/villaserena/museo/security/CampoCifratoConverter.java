package com.villaserena.museo.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Converter
@Component
public class CampoCifratoConverter implements AttributeConverter<String, String> {

    @Value("${sicurezza.chiave-cifratura}")
    private String chiaveBase64;

    @Override
    public String convertToDatabaseColumn(String valoreInChiaro) {
        if (valoreInChiaro == null || valoreInChiaro.isBlank()) return null;
        return AesUtil.cifra(valoreInChiaro, chiave());
    }

    @Override
    public String convertToEntityAttribute(String valoreCifrato) {
        if (valoreCifrato == null || valoreCifrato.isBlank()) return null;
        return AesUtil.decifra(valoreCifrato, chiave());
    }

    private SecretKey chiave() {
        String chiaveNormalizzata = (chiaveBase64 + "00000000000000000000000000000000").substring(0, 32);
        return AesUtil.chiaveDaStringaBase64(
                java.util.Base64.getEncoder().encodeToString(chiaveNormalizzata.getBytes()));
    }
}