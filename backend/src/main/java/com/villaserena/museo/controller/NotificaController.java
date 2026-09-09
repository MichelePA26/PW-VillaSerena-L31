package com.villaserena.museo.controller;

import com.villaserena.museo.dto.NotificaDTO;
import com.villaserena.museo.service.NotificaAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifiche")
public class NotificaController {

    private final NotificaAppService notificaAppService;

    public NotificaController(NotificaAppService notificaAppService) {
        this.notificaAppService = notificaAppService;
    }

    @GetMapping("/mie")
    public List<NotificaDTO> mie() {
        return notificaAppService.mie();
    }

    @PutMapping("/{id}/letta")
    public void segnaComeLetta(@PathVariable Long id) {
        notificaAppService.segnaComeLetta(id);
    }
}