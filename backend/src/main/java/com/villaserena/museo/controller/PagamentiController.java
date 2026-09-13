package com.villaserena.museo.controller;

import com.villaserena.museo.dto.CreaOrdineRequest;
import com.villaserena.museo.model.Pagamento;
import com.villaserena.museo.service.PagamentiService;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/pagamenti")
public class PagamentiController {

    private final PagamentiService pagamentiService;

    public PagamentiController(PagamentiService pagamentiService) {
        this.pagamentiService = pagamentiService;
    }

    @PostMapping("/crea-ordine")
    public Map<String, Object> creaOrdine(@RequestBody CreaOrdineRequest request) {
        return pagamentiService.creaOrdine(request.getPrenotazioneId());
    }

    @PostMapping("/{orderId}/cattura")
    public Pagamento catturaOrdine(@PathVariable String orderId) {
        return pagamentiService.catturaOrdine(orderId);
    }
}