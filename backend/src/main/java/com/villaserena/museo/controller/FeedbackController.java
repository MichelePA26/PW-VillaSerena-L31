package com.villaserena.museo.controller;

import com.villaserena.museo.dto.FeedbackDTO;
import com.villaserena.museo.dto.FeedbackRequest;
import com.villaserena.museo.service.FeedbackService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feedback")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public FeedbackDTO crea(@RequestBody FeedbackRequest request) {
        return feedbackService.crea(request);
    }

    // Elenco completo: utile più avanti per la dashboard, riservato allo staff
    @GetMapping
    @PreAuthorize("hasAnyRole('OPERATORE','HR')")
    public List<FeedbackDTO> getAll() {
        return feedbackService.findAll();
    }
}