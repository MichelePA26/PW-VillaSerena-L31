# Museo "Villa Serena" — Piattaforma digitale

Prototipo di piattaforma digitale per la gestione integrata dei processi di
un'impresa culturale (museo), realizzato come Project Work del Corso di
Laurea in Informatica per le Aziende Digitali (L-31) — Università Telematica
Pegaso.

La piattaforma copre, in un unico sistema, la relazione con il visitatore
(catalogo, eventi, prenotazioni, pagamento online, biglietto digitale con
QR code) e la gestione interna del personale (anagrafica con dati cifrati,
ferie/permessi, turni, dashboard con indicatori sintetici).

Per la descrizione completa dell'architettura, delle scelte progettuali e
dei test effettuati, vedi il report tecnico incluso in questo repository
(`Project_Work_Museo_Villa_Serena_5_aggiornato.docx`).

---

## Indice

- [Stack tecnologico](#stack-tecnologico)
- [Prerequisiti](#prerequisiti)
- [Avvio rapido con Docker (consigliato)](#avvio-rapido-con-docker-consigliato)
- [Avvio manuale (senza Docker)](#avvio-manuale-senza-docker)
- [Variabili d'ambiente](#variabili-dambiente)
- [Credenziali PayPal Sandbox](#credenziali-paypal-sandbox-solo-per-testare-i-pagamenti)
- [Primo accesso e ruoli di test](#primo-accesso-e-ruoli-di-test)
- [Documentazione delle API](#documentazione-delle-api)
- [Struttura del repository](#struttura-del-repository)
- [Documentazione aggiuntiva](#documentazione-aggiuntiva)

---

## Stack tecnologico

Livello            | Tecnologia               
---                |---                       
Front-end          | Angular 17 (TypeScript)  
Back-end           | Java 17 + Spring Boot    
Database           | MySQL 8                  
Autenticazione     | Spring Security + JWT    
Pagamenti          | PayPal Checkout REST API 
Containerizzazione | Docker + Docker Compose 

---

## Prerequisiti

Scegli **uno** dei due percorsi più sotto (Docker oppure installazione
manuale) — non servono entrambi.

**Per l'avvio con Docker:**
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) installato e in esecuzione

**Per l'avvio manuale:**
- Java 17 (distribuzione [Eclipse Temurin](https://adoptium.net/) consigliata)
- Maven — è già incluso nel repository come archivio portabile (`apache-maven-3.9.16-bin.zip`, cartella `backend/`), non serve installarlo separatamente
- Node.js 18+ e npm
- MySQL 8

**In entrambi i casi**, se si vuole testare il pagamento online, è necessario
un account [PayPal Developer](https://developer.paypal.com/) gratuito — vedi
la sezione dedicata più sotto.

---

## Avvio rapido con Docker (consigliato)

Il modo più veloce per avere l'intero sistema funzionante (database,
back-end e front-end) con un unico comando.

**1. Clona il repository**
```bash
git clone https://github.com/MichelePA26/PW-VillaSerena-L31.git
cd PW-VillaSerena-L31
```

**2. Crea il file `.env`** nella cartella principale del progetto (allo
stesso livello di `docker-compose.yml`), con questo contenuto:
```
DB_HOST=database
DB_PORT=3306
DB_NAME=museo_villaserena
DB_USER=root
DB_ROOT_PASSWORD=<scegli una password>

BACKEND_PORT=8080
JWT_SECRET=<una stringa segreta a piacere>
ENCRYPTION_KEY=<una stringa segreta a piacere>

PAYPAL_CLIENT_ID=<vedi sezione PayPal Sandbox, opzionale>
PAYPAL_CLIENT_SECRET=<vedi sezione PayPal Sandbox, opzionale>
```
Per un semplice avvio dimostrativo (senza testare il pagamento online), i
valori di `JWT_SECRET` ed `ENCRYPTION_KEY` possono essere una qualsiasi
stringa a piacere; i campi PayPal possono restare vuoti — le sole
funzionalità di pagamento non saranno disponibili, il resto della
piattaforma funziona regolarmente.

**3. Avvia tutto con un comando**
```bash
docker compose up --build
```

**4. Accedi all'applicazione**
- Front-end: [http://localhost:4200](http://localhost:4200)
- Back-end (API): [http://localhost:8080](http://localhost:8080)
- Documentazione API interattiva: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Per fermare il sistema: `Ctrl+C`, poi `docker compose down` (i dati del
database restano salvati; per rimuoverli aggiungere `-v`).

---

## Avvio manuale (senza Docker)

### 1. Database

Assicurarsi che il servizio MySQL sia in esecuzione, quindi creare ed
eseguire lo schema:
```bash
mysql -u root -p < database/schema.sql
```
Questo comando crea il database `museo_villaserena` con tutte le tabelle e
un set di dati di esempio (opere, eventi, dipendenti).

### 2. Back-end

```bash
cd backend
```
Configurare le variabili d'ambiente indicate nella sezione successiva
(tramite il proprio sistema operativo, oppure editando temporaneamente
`src/main/resources/application.properties`), quindi avviare:
```bash
# Windows
"apache-maven-3.9.16-bin\apache-maven-3.9.16\bin\mvn.cmd" spring-boot:run

# macOS / Linux
./apache-maven-3.9.16-bin/apache-maven-3.9.16/bin/mvn spring-boot:run
```
Il back-end sarà disponibile su [http://localhost:8080](http://localhost:8080).

### 3. Front-end

In un secondo terminale:
```bash
cd frontend
npm install
npm start
```
Il front-end sarà disponibile su [http://localhost:4200](http://localhost:4200).

---

## Dipendenze aggiuntive installate

Oltre alle dipendenze incluse di default nei progetti Angular e Spring Boot
generati dai rispettivi strumenti da riga di comando, il progetto utilizza
alcune librerie aggiuntive, elencate qui per completezza (sono già
dichiarate in `frontend/package.json` e `backend/pom.xml`, quindi vengono
installate automaticamente da `npm install` e da Maven — non serve
installarle una per una a mano).

**Front-end (Angular), installate con `npm install <nome-libreria>`:**

Libreria        | Comando di installazione               | A cosa serve                                                 
---             |---                                     |---                                                             
`chart.js`      | `npm install chart.js`                 | Grafico a barre delle prenotazioni per evento, nella dashboard 
`qrcode`        | `npm install qrcode`                   | Generazione del codice QR del biglietto digitale           
`@types/qrcode` | `npm install --save-dev @types/qrcode` | Tipi TypeScript per la libreria `qrcode`               

Se si clona il repository e si esegue semplicemente `npm install` nella
cartella `frontend/`, tutte queste dipendenze vengono scaricate
automaticamente perché già presenti in `package.json`: la tabella sopra è
utile solo per capire a cosa serve ciascuna, non per doverle installare
manualmente una a una.

Back-end (Spring Boot), gestite da Maven tramite `pom.xml`:

Dipendenza                                    | A cosa serve                         
---                                           |---                                  
`spring-boot-starter-web`                     | Esposizione delle API REST              
`spring-boot-starter-data-jpa`                | Persistenza dei dati (Hibernate)               
`spring-boot-starter-security`                | Autenticazione e autorizzazione             
`mysql-connector-j`                           | Driver JDBC per la connessione a MySQL        
`jjwt-api`, `jjwt-impl`, `jjwt-gson` (0.12.6) | Generazione e verifica dei token JWT          
`springdoc-openapi-starter-webmvc-ui` (3.1.0) | Documentazione interattiva delle API (Swagger UI)
`spring-boot-starter-test`                    | Test automatici (JUnit 5 + Mockito)

Anche in questo caso non serve installare nulla manualmente: eseguendo
`mvn spring-boot:run` (o il comando indicato nella sezione precedente),
Maven scarica automaticamente tutte le dipendenze elencate nel `pom.xml`
al primo avvio (potrebbe richiedere qualche minuto in più la prima volta).

**Risorse esterne** (non installate come dipendenze, ma caricate
direttamente dalle pagine dell'applicazione tramite un indirizzo web):

- **PayPal JS SDK** — bottone di pagamento nella pagina degli eventi
- **Font Awesome** — icone dell'interfaccia
- **Google Fonts** (Poppins, Playfair Display) — caratteri tipografici del sito pubblico

Queste ultime richiedono una connessione internet attiva al momento
dell'utilizzo dell'applicazione, anche in esecuzione locale.

---

## Variabili d'ambiente

Variabile                                                      | Descrizione                                                    | Obbligatoria                
---                                                            |---                                                             |---                          
`DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_ROOT_PASSWORD` | Connessione al database                                        | Sì                          
`BACKEND_PORT`                                                 | Porta del back-end (default 8080)                              | No                          
`JWT_SECRET`                                                   | Chiave per la firma dei token di autenticazione                | Sì                          
`ENCRYPTION_KEY`                                               | Chiave per la cifratura di codice fiscale e IBAN del personale | Sì                          
`PAYPAL_CLIENT_ID`, `PAYPAL_CLIENT_SECRET`                     | Credenziali dell'app PayPal (sandbox)                          | Solo per testare i pagamenti

Il file `.env` non va mai incluso nel controllo di versione (è già escluso
tramite `.gitignore`).

---

## Credenziali PayPal Sandbox (solo per testare i pagamenti)

1. Accedere a [developer.paypal.com](https://developer.paypal.com/) con un
   account PayPal personale (gratuito).
2. Sezione **Apps & Credentials**, scheda **Sandbox** → creare una nuova app
   (o usare quella di default) e copiare **Client ID** e **Secret**.
3. Sezione **Testing Tools → Sandbox Accounts** → recuperare le credenziali
   di un account di tipo **Personal**, da usare per simulare un pagamento
   durante i test (mai le proprie credenziali PayPal reali).

---

## Primo accesso e ruoli di test

I dati di esempio caricati da `schema.sql` includono alcuni utenti già
presenti a scopo dimostrativo (per popolare le liste, verificare le
relazioni tra tabelle, ecc.), **ma le loro password non sono utilizzabili
per il login**, in quanto sostituite con un valore segnaposto non reale.

Per accedere all'applicazione:

**1. Registrare un nuovo utente** dalla pagina pubblica `/registrati`. Il
nuovo utente ottiene automaticamente il ruolo **Visitatore**, che permette
di consultare il catalogo, prenotare eventi, lasciare feedback.

**2. Per esplorare anche le aree riservate a Operatore o HR**, promuovere
l'utente appena creato direttamente sul database:

```sql
-- Promuove l'utente a Operatore (gestione opere/eventi/prenotazioni)
UPDATE utente SET ruolo = 'OPERATORE' WHERE email = 'la-tua-email@example.com';
```

```sql
-- Promuove l'utente a HR: richiede DUE passaggi, non solo il ruolo,
-- perché il pannello amministrativo si aspetta anche un profilo
-- dipendente collegato
UPDATE utente SET ruolo = 'HR' WHERE email = 'la-tua-email@example.com';

INSERT INTO dipendente (utente_id, mansione, data_assunzione, stato, tipo_contratto, livello_inquadramento)
VALUES (
  (SELECT id FROM utente WHERE email = 'la-tua-email@example.com'),
  'Responsabile HR', CURDATE(), 'ATTIVO', 'TEMPO_INDETERMINATO', 'Quadro'
);
```

Dopo aver eseguito uno di questi comandi, **effettuare nuovamente il login**
(un token già ottenuto in precedenza non riflette il nuovo ruolo).

Un utente con ruolo HR, dopo il login, viene indirizzato automaticamente al
pannello amministrativo (`/admin`), da cui è possibile assumere altri
utenti tramite l'interfaccia stessa ("Nuova assunzione"), senza dover
ripetere questa procedura via SQL.

---

## Documentazione delle API

- **Swagger UI** (documentazione interattiva, generata automaticamente):
  [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
  con il back-end in esecuzione.
- **Collezione Postman**: nella cartella [`postman/`](./postman) di questo
  repository è disponibile `Museo_VillaSerena.postman_collection.json`,
  importabile direttamente in Postman, con richieste pronte per ogni
  endpoint dell'applicazione, organizzate per area funzionale. La stessa
  cartella contiene alcuni screenshot dei test più significativi.

---

## Struttura del repository

```
PW-VillaSerena-L31/
├── backend/              Applicazione Spring Boot (API REST)
├── frontend/             Applicazione Angular
├── database/
│   └── schema.sql        Schema del database e dati di esempio
├── postman/               Collezione Postman e screenshot dei test
├── docker-compose.yml     Avvio orchestrato di database, back-end, front-end
├── .env.example           Modello delle variabili d'ambiente richieste
├── Readme.md                             Informazioni sull'installazione
├── Guida_Passaggio_Produzione.docx       Guida Teorico per passaggio in Produzione 
├── PRODUCTION_CHECKLIST.md               Guida Tecnica per passaggio in Produzione
└── Report_PW_Museo_Villa_Serena.docx     Report tecnico completo
```

---

## Documentazione aggiuntiva

- **Report tecnico completo** (architettura, modello dati, scelte
  progettuali, test): `Project_Work_Museo_Villa_Serena_5_aggiornato.docx`

- **Guida al passaggio in produzione**: `Guida_Passaggio_Produzione.docx`
  e `PRODUCTION_CHECKLIST.md`

---

**Autore:** Michele — Project Work, Corso di Laurea in Informatica per le
Aziende Digitali (L-31), Università Telematica Pegaso, A.A. 2025/2026.
