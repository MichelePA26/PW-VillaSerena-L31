
-- Museo "Villa Serena"  Schema del database (MySQL 8)


DROP DATABASE IF EXISTS museo_villaserena;
CREATE DATABASE museo_villaserena CHARACTER SET utf8mb4;
USE museo_villaserena;

CREATE TABLE utente (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  cognome VARCHAR(100) NOT NULL,
  email VARCHAR(150) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  ruolo ENUM('VISITATORE','OPERATORE','HR') NOT NULL DEFAULT 'VISITATORE',
  data_registrazione DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dipendente (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  utente_id BIGINT NOT NULL UNIQUE,
  mansione VARCHAR(100),
  data_assunzione DATE NOT NULL,
  data_cessazione DATE NULL,
  stato ENUM('ATTIVO','CESSATO') DEFAULT 'ATTIVO',
  codice_fiscale VARCHAR(255),
  data_nascita DATE,
  telefono VARCHAR(30),
  indirizzo VARCHAR(255),
  tipo_contratto ENUM('TEMPO_DETERMINATO','TEMPO_INDETERMINATO','PART_TIME','STAGIONALE'),
  livello_inquadramento VARCHAR(50),
  iban VARCHAR(255),
  FOREIGN KEY (utente_id) REFERENCES utente(id)
);

CREATE TABLE collezione (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(150) NOT NULL,
  descrizione TEXT
);

CREATE TABLE opera (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  collezione_id BIGINT,
  titolo VARCHAR(200) NOT NULL,
  autore VARCHAR(150),
  anno INT,
  tecnica VARCHAR(150),
  descrizione TEXT,
  immagine_url VARCHAR(500),
  data_creazione DATETIME,
  data_modifica DATETIME,
  creato_da BIGINT,
  FOREIGN KEY (collezione_id) REFERENCES collezione(id),
  FOREIGN KEY (creato_da) REFERENCES utente(id)
);

CREATE TABLE evento (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  titolo VARCHAR(200) NOT NULL,
  descrizione TEXT,
  tipo ENUM('VISITA_GUIDATA','MOSTRA','LABORATORIO') NOT NULL,
  data_inizio DATETIME NOT NULL,
  data_fine DATETIME NOT NULL,
  capienza_max INT NOT NULL,
  stato ENUM('PROGRAMMATO','DA_RIPROGRAMMARE','ANNULLATO') NOT NULL DEFAULT 'PROGRAMMATO',
  prezzo DECIMAL(10,2) NULL
);

-- PRENOTAZIONE
-- codice_biglietto: generato come VS-{eventoId}-{prenotazioneId}

CREATE TABLE prenotazione (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  utente_id BIGINT NOT NULL,
  evento_id BIGINT NOT NULL,
  numero_posti INT NOT NULL DEFAULT 1,
  data_prenotazione DATETIME DEFAULT CURRENT_TIMESTAMP,
  stato ENUM('CONFERMATA','ANNULLATA','IN_ATTESA_PAGAMENTO','IN_ATTESA_MIGRAZIONE','RIMBORSATA') DEFAULT 'CONFERMATA',
  data_scadenza_risposta DATETIME NULL,
  codice_biglietto VARCHAR(50) UNIQUE,
  check_in_effettuato BOOLEAN NOT NULL DEFAULT FALSE,
  data_ora_checkin DATETIME NULL,
  deciso_da ENUM('UTENTE','OPERATORE') NULL,
  FOREIGN KEY (utente_id) REFERENCES utente(id),
  FOREIGN KEY (evento_id) REFERENCES evento(id)
);

CREATE TABLE feedback (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  utente_id BIGINT NOT NULL,
  prenotazione_id BIGINT NOT NULL,
  voto INT NOT NULL CHECK (voto BETWEEN 1 AND 5),
  commento TEXT,
  data DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (utente_id) REFERENCES utente(id),
  FOREIGN KEY (prenotazione_id) REFERENCES prenotazione(id)
);

CREATE TABLE richiesta_ferie (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dipendente_id BIGINT NOT NULL,
  tipo ENUM('FERIE','PERMESSO') NOT NULL,
  data_inizio DATE NOT NULL,
  data_fine DATE NOT NULL,
  ora_inizio TIME NULL,
  ora_fine TIME NULL,
  motivo VARCHAR(500),
  stato ENUM('IN_ATTESA','APPROVATA','RIFIUTATA') DEFAULT 'IN_ATTESA',
  approvata_da BIGINT NULL,
  FOREIGN KEY (dipendente_id) REFERENCES dipendente(id),
  FOREIGN KEY (approvata_da) REFERENCES dipendente(id)
);

CREATE TABLE turno (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dipendente_id BIGINT NOT NULL,
  data DATE NOT NULL,
  ora_inizio TIME NOT NULL,
  ora_fine TIME NOT NULL,
  reparto VARCHAR(100),
  FOREIGN KEY (dipendente_id) REFERENCES dipendente(id)
);


-- NOTIFICA
-- Notifiche in-app generiche: qualsiasi Utente può essere destinatario
-- (dipendenti per ferie/turni, chiunque per pagamenti/eventi annullati).

CREATE TABLE notifica (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  destinatario_id BIGINT NOT NULL,
  testo VARCHAR(500) NOT NULL,
  letta BOOLEAN NOT NULL DEFAULT FALSE,
  data DATETIME DEFAULT CURRENT_TIMESTAMP,
  link VARCHAR(255),
  FOREIGN KEY (destinatario_id) REFERENCES utente(id)
);


-- PAGAMENTO
-- id_cattura_paypal è necessario (oltre a id_ordine_paypal) per poter
-- effettuare un eventuale rimborso tramite le API PayPal.

CREATE TABLE pagamento (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  prenotazione_id BIGINT NOT NULL UNIQUE,
  importo DECIMAL(10,2) NOT NULL,
  valuta VARCHAR(3) NOT NULL DEFAULT 'EUR',
  stato ENUM('IN_ATTESA','COMPLETATO','FALLITO','RIMBORSATO') DEFAULT 'IN_ATTESA',
  id_ordine_paypal VARCHAR(50),
  id_cattura_paypal VARCHAR(50),
  data DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (prenotazione_id) REFERENCES prenotazione(id)
);

-- Dati di esempio
-- Nota: i campi cifrati (codice_fiscale, iban) sono lasciati NULL in
-- questo script perché la cifratura avviene a livello applicativo
-- (JPA AttributeConverter) con una chiave scelta da chi installa il
-- progetto: un valore inserito qui in chiaro (o cifrato con una
-- chiave diversa da quella configurata) non verrebbe letto
-- correttamente dall'applicazione. Per popolarli, usare l'endpoint
-- POST /api/personale oppure "Modifica dipendente" dal pannello HR.
--
-- Le date di eventi e turni sono calcolate rispetto alla data di
-- esecuzione dello script (CURDATE()), non fissate a valori assoluti:
-- così restano sempre "vicine ad oggi", indipendentemente da quando
-- il progetto viene installato, invece di invecchiare nel tempo.

INSERT INTO utente (nome, cognome, email, password_hash, ruolo) VALUES
('Maria', 'Rossi', 'maria.rossi@example.com', '$2a$10$examplehash1', 'HR'),
('Luca', 'Bianchi', 'luca.bianchi@example.com', '$2a$10$examplehash2', 'OPERATORE'),
('Marco', 'Biccari', 'mario.biccari@example.com', '$2e$14$examplehash3', 'OPERATORE'),
('Enrico', 'Orsi', 'EnricOrsi23@example.com', '$2rwa$10$examplehash4', 'VISITATORE'),
('Giulia', 'Rendi', 'giuliaRendi34@example.com', '$2yr$16$examplehash5', 'VISITATORE'),
('Davide', 'Ferrari', 'davide.ferrari@example.com', '$2a$10$examplehash5', 'OPERATORE'),
('Chiara', 'Romano', 'chiara.romano@example.com', '$2a$10$examplehash6', 'OPERATORE'),
('Marco', 'Greco', 'marco.greco@example.com', '$2a$10$examplehash7', 'OPERATORE');

INSERT INTO dipendente (utente_id, mansione, data_assunzione, tipo_contratto, livello_inquadramento) VALUES
(1, 'Responsabile HR', '2019-03-01', 'TEMPO_INDETERMINATO', 'Quadro'),
(2, 'Operatore di sala', '2021-06-15', 'TEMPO_INDETERMINATO', 'Livello 3'),
(3, 'Curatrice collezioni', '2020-01-10', 'TEMPO_INDETERMINATO', 'Livello 4'),
(8, 'Addetto biglietteria', '2022-09-01', 'PART_TIME', 'Livello 2'),
(6, 'Guida turistica', '2023-04-15', 'TEMPO_DETERMINATO', 'Livello 2'),
(7, 'Addetto laboratori didattici', '2024-05-20', 'STAGIONALE', 'Livello 1');

INSERT INTO collezione (nome, descrizione) VALUES
('Arte del Novecento', 'Collezione permanente di pittura e scultura del XX secolo'),
('Contemporanea', 'Opere di artisti contemporanei italiani e internazionali'),
('Fotografia moderna', 'Percorso fotografico dagli anni Sessanta a oggi'),
('Arte digitale e new media', 'Installazioni interattive e opere generative');

INSERT INTO opera (collezione_id, titolo, autore, anno, tecnica, descrizione, immagine_url, data_creazione, data_modifica, creato_da) VALUES
(1, 'Composizione n.3', 'A. Ferretti', 1965, 'Olio su tela', 'Composizione astratta a colori caldi, esempio della fase matura dell''artista.', 'http://localhost:8080/uploads/0174aa27-7982-468b-b138-34b663ea9366.png', NOW(), NOW(), 1),
(1, 'Ritratto senza volto', 'E. Marchetti', 1978, 'Tecnica mista su tavola', 'Ritratto che elude i tratti del volto, riflessione sull''identità e l''anonimato.', 'http://localhost:8080/uploads/Ritratto_senza_volto.jpg', NOW(), NOW(), 1),
(1, 'Studio per una piazza', 'G. Bruno', 1958, 'Olio su tela', 'Studio preparatorio per un dipinto di più ampio respiro dedicato agli spazi urbani.', 'http://localhost:8080/uploads/Studio_per_una_piazza.jpg', NOW(), NOW(), 1),
(2, 'Frammenti urbani', 'S. Conti', 2018, 'Installazione mista', 'Installazione che assembla materiali di recupero raccolti in contesti metropolitani.', 'http://localhost:8080/uploads/Frammenti_urbani.jpg', NOW(), NOW(), 1),
(2, 'Silenzio metropolitano', 'L. De Angelis', 2021, 'Acrilico su tela', 'Paesaggio urbano notturno, giocato su toni freddi e atmosfere sospese.', 'http://localhost:8080/uploads/Silenzio_metropolitano.jpg', NOW(), NOW(), 1),
(2, 'Geometrie sospese', 'F. Rinaldi', 2019, 'Scultura in acciaio', 'Scultura modulare in acciaio che esplora l''equilibrio tra vuoto e pieno.', 'http://localhost:8080/uploads/Geometrie_sospese.jpg', NOW(), NOW(), 1),
(3, 'Volti della città', 'P. Moretti', 1985, 'Stampa fotografica b/n', 'Reportage fotografico sulla vita quotidiana nei quartieri storici.', 'http://localhost:8080/uploads/Volti_della_città.jpg', NOW(), NOW(), 1),
(3, 'Istanti', 'R. Galli', 2002, 'Stampa fotografica a colori', 'Serie di scatti che catturano momenti fugaci della vita urbana contemporanea.', 'http://localhost:8080/uploads/Istanti.jpg', NOW(), NOW(), 1),
(4, 'Flusso #1', 'Collettivo Nimbus', 2023, 'Installazione video generativa', 'Installazione generativa che rielabora in tempo reale i dati di affluenza del museo.', 'http://localhost:8080/uploads/Flusso1.jpg', NOW(), NOW(), 1);

-- Date sempre relative al momento di esecuzione dello script (CURDATE()),
-- così restano "nel futuro prossimo" indipendentemente da quando viene
-- installato il progetto.
INSERT INTO evento (titolo, descrizione, tipo, data_inizio, data_fine, capienza_max, stato, prezzo) VALUES
('Visita guidata collezione permanente', 'Percorso guidato tra le opere del Novecento', 'VISITA_GUIDATA',
  TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 3 DAY), '11:30:00'),
  20, 'PROGRAMMATO', NULL),
('Mostra: Fotografia moderna', 'Apertura della mostra temporanea di fotografia', 'MOSTRA',
  TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 5 DAY), '18:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 5 DAY), '21:00:00'),
  60, 'PROGRAMMATO', 15.00),
('Laboratorio per famiglie', 'Attività creativa dedicata a bambini e famiglie', 'LABORATORIO',
  TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 9 DAY), '15:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 9 DAY), '17:00:00'),
  15, 'PROGRAMMATO', NULL),
('Visita guidata serale', 'Apertura straordinaria serale con visita guidata', 'VISITA_GUIDATA',
  TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 16 DAY), '19:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 16 DAY), '20:30:00'),
  25, 'PROGRAMMATO', 10.00),
('Laboratorio arte digitale', 'Introduzione alle installazioni interattive per ragazzi', 'LABORATORIO',
  TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 22 DAY), '16:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 22 DAY), '18:00:00'),
  12, 'PROGRAMMATO', NULL),
('Mostra: Arte contemporanea', 'Nuovo allestimento della collezione contemporanea', 'MOSTRA',
  TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 30 DAY), '17:00:00'), TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 30 DAY), '20:00:00'),
  50, 'PROGRAMMATO', 15.00);

-- Prenotazioni di esempio: solo utenti con ruolo VISITATORE (Enrico
-- Orsi id 4, Giulia Rendi id 5), coerentemente con il fatto che
-- "Le mie prenotazioni" è una funzionalità riservata a quel ruolo.
-- Il codice biglietto segue lo schema VS-{evento_id}-{prenotazione_id}
-- usato dall'applicazione; con una tabella vuota gli id generati sono
-- prevedibili (1, 2, 3, ...), quindi i codici sottostanti sono corretti
-- solo se questo blocco INSERT viene eseguito su una tabella prenotazione
-- vuota, come avviene qui.
INSERT INTO prenotazione (utente_id, evento_id, numero_posti, stato, codice_biglietto) VALUES
(4, 1, 2, 'CONFERMATA', 'VS-1-1'),
(5, 1, 1, 'CONFERMATA', 'VS-1-2'),
(4, 2, 2, 'CONFERMATA', 'VS-2-3'),
(5, 3, 3, 'CONFERMATA', 'VS-3-4'),
(4, 4, 1, 'CONFERMATA', 'VS-4-5'),
(5, 2, 1, 'CONFERMATA', 'VS-2-6');

-- Pagamenti completati per le prenotazioni relative a eventi a pagamento
-- (evento 2: Mostra Fotografia moderna, 15.00; evento 4: Visita serale, 10.00).
INSERT INTO pagamento (prenotazione_id, importo, valuta, stato, id_ordine_paypal, id_cattura_paypal) VALUES
(3, 30.00, 'EUR', 'COMPLETATO', 'DEMO-ORDER-3', 'DEMO-CAPTURE-3'),
(4, 45.00, 'EUR', 'COMPLETATO', 'DEMO-ORDER-4', 'DEMO-CAPTURE-4'),
(5, 10.00, 'EUR', 'COMPLETATO', 'DEMO-ORDER-5', 'DEMO-CAPTURE-5');

-- Feedback: l'utente che lascia il feedback è sempre lo stesso utente
-- della prenotazione a cui si riferisce (regola applicata anche lato
-- back-end in FeedbackService).
INSERT INTO feedback (utente_id, prenotazione_id, voto, commento) VALUES
(4, 1, 5, 'Visita molto interessante, guida preparata e disponibile.'),
(5, 2, 4, 'Bella esperienza, magari un po'' affollato negli orari centrali.'),
(4, 3, 5, 'I bambini si sono divertiti moltissimo, consigliato.');


-- Turni distribuiti attorno alla data odierna (offset da -2 a +3
-- giorni), così la vista calendario settimanale di default (che mostra
-- la settimana contenente "oggi") mostra sempre almeno alcuni turni,
-- indipendentemente da quando il progetto viene installato.
INSERT INTO turno (dipendente_id, data, ora_inizio, ora_fine, reparto) VALUES
(2, CURDATE(), '09:00:00', '13:00:00', 'Sala espositiva'),
(2, CURDATE(), '13:00:00', '17:00:00', 'Biglietteria'),
(3, CURDATE(), '09:00:00', '17:00:00', 'Curatela mostre'),
(4, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '09:00:00', '13:00:00', 'Biglietteria'),
(5, DATE_ADD(CURDATE(), INTERVAL 1 DAY), '14:00:00', '18:00:00', 'Visite guidate'),
(6, DATE_ADD(CURDATE(), INTERVAL 2 DAY), '17:00:00', '21:00:00', 'Laboratori didattici'),
(2, DATE_ADD(CURDATE(), INTERVAL -1 DAY), '09:00:00', '13:00:00', 'Sala espositiva'),
(3, DATE_ADD(CURDATE(), INTERVAL 3 DAY), '10:00:00', '14:00:00', 'Curatela mostre');
