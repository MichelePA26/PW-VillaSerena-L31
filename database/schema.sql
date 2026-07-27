
-- Museo "Villa Serena"  Schema del database (MySQL 8)


DROP DATABASE IF EXISTS museo_villaserena;
CREATE DATABASE museo_villaserena CHARACTER SET utf8mb4;
USE museo_villaserena;

CREATE TABLE utente (
  id INT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(100) NOT NULL,
  cognome VARCHAR(100) NOT NULL,
  email VARCHAR(150) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  ruolo ENUM('VISITATORE','OPERATORE','HR') NOT NULL DEFAULT 'VISITATORE',
  data_registrazione DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE dipendente (
  id INT AUTO_INCREMENT PRIMARY KEY,
  utente_id INT NOT NULL UNIQUE,
  mansione VARCHAR(100),
  data_assunzione DATE NOT NULL,
  data_cessazione DATE NULL,
  stato ENUM('ATTIVO','CESSATO') DEFAULT 'ATTIVO',
  codice_fiscale VARCHAR(255), -- cifrato
  data_nascita DATE,
  telefono VARCHAR(30),
  indirizzo VARCHAR(255),
  tipo_contratto ENUM('TEMPO_DETERMINATO','TEMPO_INDETERMINATO','PART_TIME','STAGIONALE'),
  livello_inquadramento VARCHAR(50),
  iban VARCHAR(255), -- cifrato
  FOREIGN KEY (utente_id) REFERENCES utente(id)
);

CREATE TABLE collezione (
  id INT AUTO_INCREMENT PRIMARY KEY, 
  nome VARCHAR(150) NOT NULL,
  descrizione TEXT  
);

CREATE TABLE opera (
  id INT AUTO_INCREMENT PRIMARY KEY,
  collezione_id INT,
  titolo VARCHAR(200) NOT NULL,
  autore VARCHAR(150),
  anno INT,
  tecnica VARCHAR(150),
  descrizione TEXT,
  immagine_url VARCHAR(255),
  FOREIGN KEY (collezione_id) REFERENCES collezione(id)
);

CREATE TABLE evento (
  id INT AUTO_INCREMENT PRIMARY KEY,
  titolo VARCHAR (200) NOT NULL,
  descrizione TEXT,
  tipo ENUM('VISITA_GUIDATA','MOSTRA','LABORATORIO') NOT NULL,
  data_inizio DATETIME NOT NULL,
  data_fine DATETIME NOT NULL,
  capienza_max INT NOT NULL
);

CREATE TABLE prenotazione (
  id INT AUTO_INCREMENT PRIMARY KEY,
  utente_id int NOT NULL,
  evento_id int NOT NULL,
  numero_posti INT NOT NULL DEFAULT 1,
  data_prenotazione DATETIME DEFAULT CURRENT_TIMESTAMP,
  stato ENUM('CONFERMATA','ANNULLATA') DEFAULT 'CONFERMATA',
  FOREIGN KEY (utente_id) REFERENCES utente(id),
  FOREIGN KEY (evento_id) REFERENCES evento(id)
);

CREATE TABLE feedback (
  id INT AUTO_INCREMENT PRIMARY KEY,
  utente_id INT NOT NULL,
  prenotazione_id INT NOT NULL,
  voto INT NOT NULL CHECK (voto BETWEEN 1 AND 5),
  commento TEXT,
  data DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (utente_id) REFERENCES utente(id),
  FOREIGN KEY (prenotazione_id) REFERENCES prenotazione(id)
);

CREATE TABLE richiesta_ferie (
  id INT AUTO_INCREMENT PRIMARY KEY,
  dipendente_id INT NOT NULL,
  tipo ENUM('FERIE','PERMESSO') NOT NULL,
  data_inizio DATE NOT NULL,
  data_fine DATE NOT NULL,
  stato ENUM('IN_ATTESA','APPROVATA','RIFIUTATA') DEFAULT 'IN_ATTESA',
  approvata_da INT NULL,
  FOREIGN KEY (dipendente_id) REFERENCES dipendente(id),
  FOREIGN KEY (approvata_da) REFERENCES dipendente(id)
);

CREATE TABLE turno (
  id INT AUTO_INCREMENT PRIMARY KEY,
  dipependente_id INT NOT NULL,
  data DATE NOT NULL,
  ora_inizio TIME NOT NULL,
  ora_fine TIME NOT NULL,
  reparto varchar(100),
  FOREIGN KEY (dipependente_id) REFERENCES dipendente(id)
);

CREATE TABLE pagamento (
  id INT AUTO_INCREMENT PRIMARY KEY,
  prenotazione_id INT NOT NULL,
  importo DECIMAL(10,2) NOT NULL,
  valuta VARCHAR(10) NOT NULL DEFAULT 'EUR',
  stato ENUM('IN_ATTESA','COMPLETATO','FALLITO') DEFAULT 'IN_ATTESA',
  id_ordine_paypal VARCHAR(50),
  data DATETIME DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (prenotazione_id) REFERENCES prenotazione(id)
);


-- Esempio di dati

INSERT INTO utente (nome, cognome, email, password_hash, ruolo) VALUES
('Maria', 'Rossi', 'maria.rossi@example.com', '$2a$10$examplehash1', 'HR'),
('Luca', 'Bianchi', 'luca.bianchi@example.com', '$2a$10$examplehash2', 'OPERATORE'),
('Marco', 'Biccari', 'mario.biccari@example.com', '$2e$14$examplehash3', 'OPERATORE'),
('Enrico', 'Orsi', 'EnricOrsi23@example.com', '$2rwa$10$examplehash4', 'VISITATORE'),
('Giulia', 'Rendi', 'giuliaRendi34@example.com', '$2yr$16$examplehash5', 'VISITATORE'),
('Davide', 'Ferrari', 'davide.ferrari@example.com', '$2a$10$examplehash5', 'OPERATORE'),
('Chiara', 'Romano', 'chiara.romano@example.com', '$2a$10$examplehash6', 'OPERATORE'),
('Marco', 'Greco', 'marco.greco@example.com', '$2a$10$examplehash7', 'OPERATORE');

INSERT INTO dipendente (utente_id, mansione, data_assunzione, tipo_contratto, livello_inquadramento ) VALUES
(1, 'Responsabile HR', '2019-03-01', 'TEMPO_INDETERMINATO', 'Quadro'),
(2, 'Operatore di sala', '2021-06-15', 'TEMPO_INDETERMINATO', 'Livello 3'),
(4, 'Curatrice collezioni', '2020-01-10', 'TEMPO_INDETERMINATO', 'Livello 4'),
(5, 'Addetto biglietteria', '2022-09-01', 'PART_TIME', 'Livello 2'),
(6, 'Guida turistica', '2023-04-15', 'TEMPO_DETERMINATO', 'Livello 2'),
(7, 'Addetto laboratori didattici', '2024-05-20', 'STAGIONALE', 'Livello 1');

INSERT INTO collezione (nome, descrizione) VALUES
('Arte del Novecento', 'Collezione permanente di pittura e scultura del XX secolo'),
('Contemporanea', 'Opere di artisti contemporanei italiani e internazionali'),
('Fotografia moderna', 'Percorso fotografico dagli anni Sessanta a oggi'),
('Arte digitale e new media', 'Installazioni interattive e opere generative');

INSERT INTO opera (collezione_id, titolo, autore, anno, tecnica, descrizione) VALUES
(1, 'Composizione n.3', 'A. Ferretti', 1965, 'Olio su tela', 'Composizione astratta a colori caldi, esempio della fase matura dell''artista.'),
(1, 'Ritratto senza volto', 'E. Marchetti', 1978, 'Tecnica mista su tavola', 'Ritratto che elude i tratti del volto, riflessione sull''identità e l''anonimato.'),
(1, 'Studio per una piazza', 'G. Bruno', 1958, 'Olio su tela', 'Studio preparatorio per un dipinto di più ampio respiro dedicato agli spazi urbani.'),
(2, 'Frammenti urbani', 'S. Conti', 2018, 'Installazione mista', 'Installazione che assembla materiali di recupero raccolti in contesti metropolitani.'),
(2, 'Silenzio metropolitano', 'L. De Angelis', 2021, 'Acrilico su tela', 'Paesaggio urbano notturno, giocato su toni freddi e atmosfere sospese.'),
(2, 'Geometrie sospese', 'F. Rinaldi', 2019, 'Scultura in acciaio', 'Scultura modulare in acciaio che esplora l''equilibrio tra vuoto e pieno.'),
(3, 'Volti della città', 'P. Moretti', 1985, 'Stampa fotografica b/n', 'Reportage fotografico sulla vita quotidiana nei quartieri storici.'),
(3, 'Istanti', 'R. Galli', 2002, 'Stampa fotografica a colori', 'Serie di scatti che catturano momenti fugaci della vita urbana contemporanea.'),
(4, 'Flusso #1', 'Collettivo Nimbus', 2023, 'Installazione video generativa', 'Installazione generativa che rielabora in tempo reale i dati di affluenza del museo.');

INSERT INTO evento( titolo, descrizione, tipo, data_inizio, data_fine, capienza_max) VALUES
('Visita guidata collezione permanente', 'Percorso guidato tra le opere del Novecento', 'VISITA_GUIDATA', '2026-09-05 10:00:00', '2026-09-05 11:30:00', 20),
('Laboratorio per famiglie', 'Attività creativa dedicata a bambini e famiglie', 'LABORATORIO', '2026-09-12 15:00:00', '2026-09-12 17:00:00', 15),
('Mostra: Fotografia moderna', 'Apertura della mostra temporanea di fotografia', 'MOSTRA', '2026-09-20 18:00:00', '2026-09-20 21:00:00', 60),
('Visita guidata serale', 'Apertura straordinaria serale con visita guidata', 'VISITA_GUIDATA', '2026-10-03 19:00:00', '2026-10-03 20:30:00', 25),
('Laboratorio arte digitale', 'Introduzione alle installazioni interattive per ragazzi', 'LABORATORIO', '2026-10-10 16:00:00', '2026-10-10 18:00:00', 12),
('Mostra: Arte contemporanea', 'Nuovo allestimento della collezione contemporanea', 'MOSTRA', '2026-10-18 17:00:00', '2026-10-18 20:00:00', 50);

INSERT INTO turno(dipependente_id, data, ora_inizio, ora_fine, reparto) VALUES
(2, '2026-09-05', '09:00:00', '13:00:00', 'Sala espositiva'),
(2, '2026-09-05', '13:00:00', '17:00:00', 'Biglietteria'),
(3, '2026-09-05', '09:00:00', '17:00:00', 'Curatela mostre'),
(4, '2026-09-12', '09:00:00', '13:00:00', 'Biglietteria'),
(5, '2026-09-12', '14:00:00', '18:00:00', 'Visite guidate'),
(6, '2026-09-20', '17:00:00', '21:00:00', 'Laboratori didattici');
