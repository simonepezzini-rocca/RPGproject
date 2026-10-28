# 🛡️ Wiki Ufficiale - RPG Battle Manager
**Documentazione Architetturale e Progettuale**  
*Corso di Metodologie di Programmazione e Modellazione della Conoscenza (UNICAM A.A. 2025/2026)*  
**Autore**: Simone Pezzini (Matricola `089027`)

---

## 🏛️ 1. Motivazione delle Scelte Progettuali ed Architettura

L'architettura del sistema è stata progettata per garantire una netta separazione delle responsabilità tra il dominio di gioco, la logica di business, la persistenza e l'interfaccia utente.

### 1.1 Perché l'interfaccia `Combattente`?
Nella prima stesura del sistema, il motore di combattimento interagiva direttamente con le classi degli eroi. Per superare questa rigidità e consentire scontri polimorfici:
* È stata introdotta l'interfaccia `Combattente`, che definisce il contratto minimo indispensabile per partecipare ad uno scontro (`subisciDanno()`, `isVivo()`, `getSalute()`, `getNome()`).
* **Motivazione**: In questo modo, il motore di combattimento (`GestoreCombattimento`) non ha alcuna conoscenza concreta di cosa sia un `Eroe` o un `Mostro`. Tratta entrambi semplicemente come entità `Combattente`, rendendo possibile qualsiasi tipo di scontro (Eroe vs Mostro, Eroe vs Eroe, o futuri alleati) senza modificare una singola riga del motore di gioco.

### 1.2 Perché la classe astratta `Eroe` e le sottoclassi `Guerriero` e `Mago`?
* **Gerarchia di Dominio**: La classe astratta `Eroe` implementa l'interfaccia `Combattente` e fattorizza lo stato e il comportamento comune a tutti i personaggi giocabili (identificativo univoco `UUID`, nome, salute massima, salute corrente, livello e stato di vita).
* **Specializzazione**: Le classi concrete `Guerriero` e `Mago` estendono `Eroe` per definire risorse fisiche o magiche e meccaniche speciali differenti:
    * `Guerriero`: Utilizza la `forzaFisica` e implementa l'abilità speciale *ColpoDevastante* che calcola un danno scalato sulla forza.
    * `Mago`: Gestisce la risorsa `mana` e implementa l'abilità speciale *PallaDiFuoco*, che consuma mana per infliggere danni elevati e prevede una ricarica d'emergenza in caso di esaurimento energia.

### 1.3 Perché il Pattern Facade (`Partita`)?
Senza un punto di coordinamento centrale, i Controller dell'interfaccia grafica JavaFX avrebbero dovuto istanziare e gestire direttamente le istanze di `Squadra`, `GestoreCombattimento` e `GestoreSalvataggiJson`.
* **Soluzione**: La classe `Partita` applica il pattern **Facade**, esponendo all'esterno un'API ad alto livello per tutte le operazioni di gioco (`iniziaCombattimento()`, `eseguiTurno()`, `salvaSquadraSuFile()`, `caricaSquadraDaFile()`).
* **Vantaggio**: Isola completamente la GUI dalla complessità sottostante del dominio e della persistenza, riducendo l'accoppiamento e facilitando i test automatici.

### 1.4 Architettura Model-View-Controller (MVC)
L'interfaccia grafica è realizzata in JavaFX seguendo il pattern **MVC**:
* **Model**: Le classi del package `modelli` (`Squadra`, `Eroe`, `Mostro`).
* **View**: I file dichiarativi FXML (`MainView.fxml` e `NuovoEroeDialog.fxml`).
* **Controller**: Le classi `MainController` e `NuovoEroeController` che intercettano gli eventi utente, convalidano i dati e dialogano esclusivamente con il Facade `Partita`.

---

## 📋 2. Mappatura Dettagliata delle Responsabilità (Classi e Interfacce)

### Pacchetto `modelli`
* **`Combattente` (Interfaccia)**: Definisce il contratto astratto per qualsiasi entità in grado di combattere nell'arena.
* **`Eroe` (Classe Astratta)**: Rappresenta l'entità di base del personaggio giocabile con ID univoco, gestione della salute e contratto dell'abilità speciale.
* **`Guerriero` (Classe Concreta)**: Estende `Eroe`. Gestisce attributi e abilità fisiche.
* **`Mago` (Classe Concreta)**: Estende `Eroe`. Gestisce la risorsa mana e le abilità magiche.
* **`Mostro` (Classe Concreta)**: Implementa `Combattente`. Rappresenta l'avversario gestito dal sistema.
* **`Squadra` (Classe Concreta)**: Incapsula la collezione degli eroi della partita, offrendo metodi per l'arruolamento, la rimozione, la ricerca per ID e l'estrazione dei membri in vita via Java Stream.

### Pacchetto `servizi`
* **`GestoreCombattimento`**: Gestisce lo svolgimento dell'arena a turni, il calcolo del danno, l'avanzamento dei turni e la generazione del *Battle Log* immutabile.
* **`Partita`**: Facade principale che coordina lo stato globale della sessione corrente.

### Pacchetto `persistenza`
* **`GestoreSalvataggi` (Interfaccia)**: Contratto astratto per le operazioni di salvataggio/caricamento della squadra.
* **`GestoreSalvataggiJson`**: Implementazione concreta basata su Google Gson per la serializzazione/deserializzazione su file JSON.
* **`EroeTypeAdapter`**: Serializzatore/deserializzatore custom Gson per la gestione del polimorfismo delle sottoclassi di `Eroe`.
* **`ConfigurazioneSalvataggi`**: Gestisce i percorsi predefiniti dei file di salvataggio.

### Pacchetto `gui`
* **`MainController`**: Controller della finestra principale (gestione tabella squadra, barre di stato, avvio arena e log).
* **`NuovoEroeController`**: Controller della finestra modale di dialogo per la creazione ed arruolamento dei nuovi eroi.

### Pacchetto `eccezioni`
* **`MembroGiaPresenteException`**: Sollevata in caso di tentativo di inserimento di un eroe duplicato.
* **`MembroNonTrovatoException`**: Sollevata quando si richiede un'operazione su un ID non presente in squadra.

---

## 📐 3. Analisi dei Principi SOLID

### 1. Single Responsibility Principle (SRP)
Ogni classe nel sistema ha un'unica ed esplicita ragione di modifica:
* `Squadra` si occupa unicamente della coerenza della lista dei membri, delegando il salvataggio a `GestoreSalvataggiJson` e lo scontro a `GestoreCombattimento`.
* `GestoreSalvataggiJson` si occupa solo della conversione tra oggetti Java e formato JSON su disco.
* `MainController` si occupa soltanto della risposta agli eventi dell'interfaccia grafica e dell'aggiornamento dei componenti FXML.

### 2. Open/Closed Principle (OCP)
Il sistema è **aperto all'estensione ma chiuso alla modifica**:
* **Aggiunta Eroi**: Per introdurre una nuova classe giocabile (es. `Paladino`), è sufficiente creare una classe che estenda `Eroe` e registrarla in `EroeTypeAdapter`. Nessuna classe esistente (`Squadra`, `GestoreCombattimento`, `Partita`) necessita di alcuna modifica.
* **Aggiunta Salvataggi**: Per salvare in formato XML invece di JSON, basta creare `GestoreSalvataggiXml implements GestoreSalvataggi` e passarlo al Facade `Partita`.

### 3. Liskov Substitution Principle (LSP)
Tutte le sottoclassi rispettano integralmente il contratto formale dei tipi padre:
* `Guerriero` e `Mago` possono sostituire un `Eroe` o un `Combattente` in qualsiasi punto dell'applicazione senza alterare il funzionamento del programma o sollevare eccezioni inattese.

### 4. Interface Segregation Principle (ISP)
Le interfacce definite nel progetto sono sottili e mirate:
* `Combattente` non include metodi di gestione squadra o di salvataggio, ma espone solo ed esclusivamente i metodi utili al motore di scontro.

### 5. Dependency Inversion Principle (DIP)
Le componenti di alto livello dipendono dalle astrazioni e non dai dettagli concreti:
* La classe `Partita` e il `GestoreCombattimento` interagiscono con l'interfaccia `Combattente` e con l'interfaccia `GestoreSalvataggi`, consentendo l'iniezione flessibile delle dipendenze.

---

## 💾 4. Organizzazione dei Dati e Persistenza JSON

### Gestione del Polimorfismo con `EroeTypeAdapter`
La libreria Gson, durante la deserializzazione di una lista di oggetti astratti `Eroe`, non è in grado di determinare autonomamente se istanziare un `Guerriero` o un `Mago`.
*Per risolvere questo problema*:
* È stata creata la classe custom `EroeTypeAdapter` che implementa `JsonSerializer<Eroe>` e `JsonDeserializer<Eroe>`.
* In fase di scrittura, scrive un attributo speciale `"tipoClasse"` nel JSON (`"Guerriero"` o `"Mago"`).
* In fase di lettura, analizza la proprietà `"tipoClasse"` e delega la ricostruzione alla classe concreta corretta, preservando tutti gli attributi specifici (come il `mana` per i Maghi e la `forzaFisica` per i Guerrieri).

---

## 🔮 5. Limiti del Sistema ed Estendibilità Futura

### Limiti Individuati
1. **Modalità di Combattimento 1v1**: L'arena gestisce attualmente duelli tra un singolo eroe prescelto e un mostro avversario.
2. **Archiviazione Locale**: I salvataggi sono gestiti su file JSON locali nella directory utente.

### Meccanismi di Estensione Futura
1. **Scontri Squadra vs Squadra**: Grazie alla struttura dell'interfaccia `Combattente`, è possibile estendere `GestoreCombattimento` per gestire battaglie a gruppi senza dover riprogettare le entità di base.
2. **Persistenza su Database**: Sostituendo la classe di persistenza con un'implementazione JPA/SQL che rispetti `GestoreSalvataggi`, il sistema potrà salvare su database relazionali.

---

## 🤖 6. Dichiarazione sull'Uso di Strumenti di AI

In conformità con la specifica di progetto, si dichiara l'impiego dei seguenti strumenti di Intelligenza Artificiale:

1. **Gemini Notebook**:
    * Utilizzato come supporto allo studio teorico per l'assimilazione guidata dei principi SOLID, dei pattern MVC e Facade, dell'uso di JUnit 5 e per la pianificazione dell'architettura dei package.
2. **IntelliJ IDEA AI Assistant**:
    * Utilizzato per l'analisi rapida degli errori di compilazione/warning e la generazione dello scheletro iniziale del codice.

*Supervisione e Responsabilità*: Ogni componente di codice è stato personalmente analizzato, compreso, modificato, integrato e testato dallo studente.