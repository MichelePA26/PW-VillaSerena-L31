package com.villaserena.museo.service;

import com.villaserena.museo.dto.TurnoDTO;
import com.villaserena.museo.model.Dipendente;
import com.villaserena.museo.model.RichiestaFerie;
import com.villaserena.museo.model.Turno;
import com.villaserena.museo.repository.DipendenteRepository;
import com.villaserena.museo.repository.RichiestaFerieRepository;
import com.villaserena.museo.repository.TurnoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TurniService {

    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final TurnoRepository turnoRepository;
    private final DipendenteRepository dipendenteRepository;
    private final RichiestaFerieRepository richiestaFerieRepository;
    private final NotificaAppService notificaAppService;

    public TurniService(TurnoRepository turnoRepository,
                         DipendenteRepository dipendenteRepository,
                         RichiestaFerieRepository richiestaFerieRepository,
                         NotificaAppService notificaAppService) {
        this.turnoRepository = turnoRepository;
        this.dipendenteRepository = dipendenteRepository;
        this.richiestaFerieRepository = richiestaFerieRepository;
        this.notificaAppService = notificaAppService;
    }

    public List<TurnoDTO> findAll() {
        return turnoRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<TurnoDTO> byDipendente(Long dipendenteId) {
        return turnoRepository.findAll().stream()
                .filter(t -> t.getDipendente().getId().equals(dipendenteId))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TurnoDTO create(TurnoDTO dto) {
        Dipendente dipendente = dipendenteRepository.findById(dto.getDipendenteId())
                .orElseThrow(() -> new RuntimeException("Dipendente non trovato"));

        verificaAssenzaSovrapposizioni(dto, dipendente.getId(), null);
        verificaAssenzaFerieApprovate(dto, dipendente.getId());

        Turno turno = new Turno();
        applica(dto, turno, dipendente);
        Turno salvato = turnoRepository.save(turno);

        notificaAppService.crea(dipendente.getUtente(),
                "Ti è stato assegnato un turno il " + dto.getData().format(FORMATO_DATA) + " (" + dto.getOraInizio() + "-" + dto.getOraFine() + ")",
                "/area-personale");

        return toDTO(salvato);
    }

    public TurnoDTO update(Long id, TurnoDTO dto) {
        Turno turno = turnoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno non trovato"));

        Dipendente dipendente = dto.getDipendenteId() != null
                ? dipendenteRepository.findById(dto.getDipendenteId()).orElseThrow(() -> new RuntimeException("Dipendente non trovato"))
                : turno.getDipendente();

        verificaAssenzaSovrapposizioni(dto, dipendente.getId(), id);
        verificaAssenzaFerieApprovate(dto, dipendente.getId());

        applica(dto, turno, dipendente);
        return toDTO(turnoRepository.save(turno));
    }

    public void delete(Long id) {
        turnoRepository.deleteById(id);
    }

    // Usato da FerieService: elenco dei turni di un dipendente che cadono
    // (anche parzialmente) nell'intervallo indicato — per avvisare l'HR
    // in fase di approvazione di una richiesta di ferie.
    public List<TurnoDTO> turniInConflittoConPeriodo(Long dipendenteId, LocalDate dal, LocalDate al) {
        return turnoRepository.findAll().stream()
                .filter(t -> t.getDipendente().getId().equals(dipendenteId))
                .filter(t -> !t.getData().isBefore(dal) && !t.getData().isAfter(al))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // Usato da PersonaleService alla cessazione di un dipendente:
    // rimuove i turni futuri non ancora svolti.
    public void eliminaTurniFuturi(Long dipendenteId) {
        LocalDate oggi = LocalDate.now();
        turnoRepository.findAll().stream()
                .filter(t -> t.getDipendente().getId().equals(dipendenteId))
                .filter(t -> !t.getData().isBefore(oggi))
                .forEach(t -> turnoRepository.deleteById(t.getId()));
    }

    private void verificaAssenzaSovrapposizioni(TurnoDTO dto, Long dipendenteId, Long turnoIdEscluso) {
        boolean sovrapposto = turnoRepository.findAll().stream()
                .filter(t -> turnoIdEscluso == null || !t.getId().equals(turnoIdEscluso))
                .filter(t -> t.getDipendente().getId().equals(dipendenteId))
                .filter(t -> t.getData().equals(dto.getData()))
                .anyMatch(t -> dto.getOraInizio().isBefore(t.getOraFine()) && t.getOraInizio().isBefore(dto.getOraFine()));
        if (sovrapposto) {
            throw new RuntimeException("Il dipendente ha già un turno che si sovrappone in questa data/orario");
        }
    }

    private void verificaAssenzaFerieApprovate(TurnoDTO dto, Long dipendenteId) {
        boolean inFerie = richiestaFerieRepository.findAll().stream()
                .filter(f -> f.getDipendente().getId().equals(dipendenteId))
                .filter(f -> f.getStato() == RichiestaFerie.Stato.APPROVATA)
                .anyMatch(f -> !dto.getData().isBefore(f.getDataInizio()) && !dto.getData().isAfter(f.getDataFine()));
        if (inFerie) {
            throw new RuntimeException("Il dipendente ha ferie approvate in questa data: impossibile assegnare un turno");
        }
    }

    private void applica(TurnoDTO dto, Turno turno, Dipendente dipendente) {
        turno.setDipendente(dipendente);
        turno.setData(dto.getData());
        turno.setOraInizio(dto.getOraInizio());
        turno.setOraFine(dto.getOraFine());
        turno.setReparto(dto.getReparto());
    }

    private TurnoDTO toDTO(Turno t) {
        TurnoDTO dto = new TurnoDTO();
        dto.setId(t.getId());
        dto.setDipendenteId(t.getDipendente().getId());
        dto.setNomeDipendente(t.getDipendente().getUtente().getNome() + " " + t.getDipendente().getUtente().getCognome());
        dto.setData(t.getData());
        dto.setOraInizio(t.getOraInizio());
        dto.setOraFine(t.getOraFine());
        dto.setReparto(t.getReparto());
        return dto;
    }
}