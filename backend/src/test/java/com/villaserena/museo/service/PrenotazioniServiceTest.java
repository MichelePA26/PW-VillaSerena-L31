package com.villaserena.museo.service;

import com.villaserena.museo.dto.PrenotazioneDTO;
import com.villaserena.museo.dto.PrenotazioneRequest;
import com.villaserena.museo.model.Evento;
import com.villaserena.museo.model.Prenotazione;
import com.villaserena.museo.model.Utente;
import com.villaserena.museo.repository.EventoRepository;
import com.villaserena.museo.repository.PagamentoRepository;
import com.villaserena.museo.repository.PrenotazioneRepository;
import com.villaserena.museo.repository.UtenteRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Verifica in isolamento la regola di business più delicata del sistema:
 * il controllo della capienza massima di un evento in fase di
 * prenotazione (metodo PrenotazioniService.crea()).
 *
 * I repository sono simulati con Mockito, così il test verifica solo la
 * logica del service, senza dover interrogare un database reale.
 */
@ExtendWith(MockitoExtension.class)
class PrenotazioniServiceTest {

    @Mock private PrenotazioneRepository prenotazioneRepository;
    @Mock private EventoRepository eventoRepository;
    @Mock private UtenteRepository utenteRepository;
    @Mock private PagamentoRepository pagamentoRepository;
    @Mock private PagamentiService pagamentiService;
    @Mock private NotificaAppService notificaAppService;

    @InjectMocks
    private PrenotazioniService prenotazioniService;

    private Utente utenteCorrente;

    @BeforeEach
    void impostaUtenteAutenticato() {
        // Il service ricava l'utente dal contesto di sicurezza (SecurityContextHolder),
        // non da un parametro: lo simuliamo per ogni test.
        utenteCorrente = new Utente();
        ReflectionTestUtils.setField(utenteCorrente, "id", 42L);

        Authentication authentication = mock(Authentication.class);
        lenient().when(authentication.getName()).thenReturn("visitatore@example.com");

        SecurityContext securityContext = mock(SecurityContext.class);
        lenient().when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        lenient().when(utenteRepository.findByEmail("visitatore@example.com"))
                .thenReturn(Optional.of(utenteCorrente));
    }

    @AfterEach
    void ripulisciContesto() {
        SecurityContextHolder.clearContext();
    }

    private Evento eventoGratuitoConCapienza(long id, int capienzaMax) {
        Evento evento = new Evento();
        ReflectionTestUtils.setField(evento, "id", id);
        evento.setTitolo("Visita guidata");
        evento.setCapienzaMax(capienzaMax);
        evento.setPrezzo(null); // evento gratuito: la prenotazione nasce già CONFERMATA
        return evento;
    }

    private Prenotazione prenotazioneEsistente(Evento evento, int numeroPosti, Prenotazione.Stato stato) {
        Prenotazione p = new Prenotazione();
        p.setEvento(evento);
        p.setNumeroPosti(numeroPosti);
        p.setStato(stato);
        return p;
    }

    @Test
    void creaPrenotazione_conPostiDisponibili_vieneSalvataEConfermata() {
        Evento evento = eventoGratuitoConCapienza(1L, 10);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        // 3 posti già occupati su una capienza di 10: ne restano 7 disponibili
        when(prenotazioneRepository.findAll()).thenReturn(
                List.of(prenotazioneEsistente(evento, 3, Prenotazione.Stato.CONFERMATA))
        );
        when(prenotazioneRepository.save(any(Prenotazione.class)))
            .thenAnswer(invocazione -> {
                Prenotazione p = invocazione.getArgument(0);
                ReflectionTestUtils.setField(p, "id", 100L);
                return p;
            });

        PrenotazioneRequest request = new PrenotazioneRequest();
        request.setEventoId(1L);
        request.setNumeroPosti(5);

        PrenotazioneDTO risultato = prenotazioniService.crea(request);

        assertNotNull(risultato);
        assertEquals(Prenotazione.Stato.CONFERMATA, risultato.getStato());
        assertEquals("VS-1-100", risultato.getCodiceBiglietto());
        verify(prenotazioneRepository, times(2)).save(any(Prenotazione.class));
    }

    @Test
    void creaPrenotazione_quandoCapienzaSuperata_lanciaEccezioneENonSalva() {
        Evento evento = eventoGratuitoConCapienza(1L, 10);
        when(eventoRepository.findById(1L)).thenReturn(Optional.of(evento));
        // 8 posti già occupati su una capienza di 10: richiederne altri 5 supera il limite
        when(prenotazioneRepository.findAll()).thenReturn(
                List.of(prenotazioneEsistente(evento, 8, Prenotazione.Stato.CONFERMATA))
        );

        PrenotazioneRequest request = new PrenotazioneRequest();
        request.setEventoId(1L);
        request.setNumeroPosti(5);

        RuntimeException eccezione = assertThrows(RuntimeException.class,
                () -> prenotazioniService.crea(request));

        assertTrue(eccezione.getMessage().contains("Capienza massima"));
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void creaPrenotazione_perEventoInesistente_lanciaEccezione() {
        when(eventoRepository.findById(99L)).thenReturn(Optional.empty());

        PrenotazioneRequest request = new PrenotazioneRequest();
        request.setEventoId(99L);
        request.setNumeroPosti(1);

        RuntimeException eccezione = assertThrows(RuntimeException.class,
                () -> prenotazioniService.crea(request));

        assertTrue(eccezione.getMessage().contains("Evento non trovato"));
        verify(prenotazioneRepository, never()).save(any());
    }

    @Test
    void creaPrenotazione_contaSoloIPostiDelloStessoEvento_nonDiAltriEventi() {
        // Verifica la correzione del bug di precedenza tra && e ||:
        // una prenotazione IN_ATTESA_PAGAMENTO su un evento DIVERSO non deve
        // mai essere conteggiata nella capienza dell'evento richiesto.
        Evento eventoRichiesto = eventoGratuitoConCapienza(1L, 10);
        Evento altroEvento = eventoGratuitoConCapienza(2L, 5);

        when(eventoRepository.findById(1L)).thenReturn(Optional.of(eventoRichiesto));
        when(prenotazioneRepository.findAll()).thenReturn(List.of(
                // 4 posti in attesa di pagamento, ma sull'evento 2, non sull'evento 1
                prenotazioneEsistente(altroEvento, 4, Prenotazione.Stato.IN_ATTESA_PAGAMENTO)
        ));
        when(prenotazioneRepository.save(any(Prenotazione.class)))
            .thenAnswer(invocazione -> {
                Prenotazione p = invocazione.getArgument(0);
                ReflectionTestUtils.setField(p, "id", 200L);
                return p;
            });

        PrenotazioneRequest request = new PrenotazioneRequest();
        request.setEventoId(1L);
        request.setNumeroPosti(8); // 8 su 10 disponibili sull'evento 1: deve riuscire

        PrenotazioneDTO risultato = prenotazioniService.crea(request);

        assertNotNull(risultato);
        assertEquals(Prenotazione.Stato.CONFERMATA, risultato.getStato());
    }
}
