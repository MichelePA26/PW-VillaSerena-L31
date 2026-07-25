
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
  FOREIGN KEY (utente_id) REFERENCES utente(id)
);



-- Esempio di dati

INSERT INTO utente (nome, cognome, email, password_hash, ruolo) VALUES
('Maria', 'Rossi', 'maria.rossi@example.com', '$2a$10$examplehash1', 'HR'),
('Luca', 'Bianchi', 'luca.bianchi@example.com', '$2a$10$examplehash2', 'OPERATORE'),
('Marco', 'Biccari', 'mario.biccari@example.com', '$2e$14$examplehash3', 'OPERATORE');
('Enrico', 'Orsi', 'EnricOrsi23@example.com', '$2rwa$10$examplehash4', 'VISITATORE');
('Giulia', 'Rendi', 'giuliaRendi34@example.com', '$2yr$16$examplehash5', 'VISITATORE');

INSERT INTO dipendente (utente_id, mansione, data_assunzione) VALUES
(1, 'Responsabile HR', '2019-03-01'),
(2, 'Operatore di sala', '2021-06-15');


