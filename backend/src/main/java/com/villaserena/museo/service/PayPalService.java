package com.villaserena.museo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PayPalService {

    private final RestTemplate restTemplate;

    @Value("${paypal.client-id}")
    private String clientId;

    @Value("${paypal.client-secret}")
    private String clientSecret;

    @Value("${paypal.base-url}")
    private String baseUrl;

    public PayPalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private String ottieniAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth(clientId, clientSecret);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");

        HttpEntity<MultiValueMap<String, String>> richiesta = new HttpEntity<>(body, headers);
        ResponseEntity<Map> risposta = restTemplate.postForEntity(baseUrl + "/v1/oauth2/token", richiesta, Map.class);
        return (String) risposta.getBody().get("access_token");
    }

    public Map<String, Object> creaOrdine(BigDecimal importo, String valuta, Long prenotazioneId) {
        String token = ottieniAccessToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> corpo = Map.of(
                "intent", "CAPTURE",
                "purchase_units", List.of(Map.of(
                        "reference_id", "prenotazione-" + prenotazioneId,
                        "amount", Map.of("currency_code", valuta, "value", importo.setScale(2).toString())
                ))
        );

        HttpEntity<Map<String, Object>> richiesta = new HttpEntity<>(corpo, headers);
        ResponseEntity<Map> risposta = restTemplate.postForEntity(baseUrl + "/v2/checkout/orders", richiesta, Map.class);
        return risposta.getBody();
    }

    public Map<String, Object> catturaOrdine(String orderId) {
        String token = ottieniAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> richiesta = new HttpEntity<>(headers);
        ResponseEntity<Map> risposta = restTemplate.postForEntity(
                baseUrl + "/v2/checkout/orders/" + orderId + "/capture", richiesta, Map.class);
        return risposta.getBody();
    }

    // Rimborso totale di una cattura già effettuata
    public Map<String, Object> rimborsaCattura(String captureId) {
        String token = ottieniAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> richiesta = new HttpEntity<>(Map.of(), headers);
        ResponseEntity<Map> risposta = restTemplate.postForEntity(
                baseUrl + "/v2/payments/captures/" + captureId + "/refund", richiesta, Map.class);
        return risposta.getBody();
    }
}