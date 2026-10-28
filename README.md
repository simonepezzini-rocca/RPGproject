# 🛡️ RPG Battle Manager - Gioco di Ruolo in Java

RPG Battle Manager è un'applicazione desktop in Java e JavaFX per la gestione e il combattimento tra eroi e mostri in un gioco di ruolo (RPG).

Il progetto è stato sviluppato per il corso di Metodologie di Programmazione e Modellazione della Conoscenza dell'Università di Camerino (A.A. 2025/2026) ed è realizzato secondo i principi SOLID, Clean Code e i pattern MVC e Facade.

--------------------------------------------------------------------------------

## 📌 Funzionalità Principali

* ⚔️ Gestione della squadra con creazione ed arruolamento di eroi (Guerriero e Mago).
* 🎲 Arena di combattimento a turni contro mostri con calcolo dei danni, gestione risorse (salute e mana) e registro di battaglia (Battle Log).
* 💾 Salvataggio e caricamento della squadra su file JSON tramite la libreria Gson e adapter polimorfici.
* 🖥️ Interfaccia grafica nativa in JavaFX ed FXML con finestre di dialogo e validazione degli input.

--------------------------------------------------------------------------------

## 🚀 Come Compilare ed Eseguire

### 📋 Prerequisiti
* ☕ Java JDK 21 o superiore
* 🛠️ Git e Gradle

### 🔨 Build del progetto
Per compilare il codice e preparare il progetto, eseguire nel terminale: **./gradlew build**
### 🎮 Esecuzione dell'applicazione
Per avviare l'interfaccia grafica: **./gradlew run**

--------------------------------------------------------------------------------

## 🧪 Esecuzione dei Test e Documentazione

### 📊 Test di unità e integrazione (JUnit 5)
Per eseguire tutti i test automatizzati: **./gradlew test**

I report in formato HTML vengono salvati nella cartella: build/reports/tests/test/index.html


### 📚 Generazione Javadoc
Per generare la documentazione HTML del codice: **./gradlew javadoc**

La documentazione viene salvata nella cartella: build/docs/javadoc/index.html

### 🏛️ Struttura dei Package
Tutto il codice è organizzato sotto la radice assegnata: it.unicam.cs.mpgc.rpg089027

* 📦 it.unicam.cs.mpgc.rpg089027.modelli : Classi del dominio (Eroe, Guerriero, Mago, Mostro, Squadra, Combattente)
* 💾 it.unicam.cs.mpgc.rpg089027.persistenza : Salvataggio JSON (GestoreSalvataggiJson, EroeTypeAdapter, ConfigurazioneSalvataggi)
* ⚙️ it.unicam.cs.mpgc.rpg089027.servizi : Logica di gioco (Facade Partita, GestoreCombattimento)
* 🎨 it.unicam.cs.mpgc.rpg089027.gui : Controller dell'interfaccia grafica (MainController, NuovoEroeController)
* ⚠️ it.unicam.cs.mpgc.rpg089027.eccezioni : Eccezioni personalizzate di dominio

--------------------------------------------------------------------------------

## 🤖 Dichiarazione sull'Uso di Strumenti di Intelligenza Artificiale

In conformità con le direttive del corso, si dichiara l'uso dei seguenti strumenti di Intelligenza Artificiale:
* 💡 Gemini Notebook:
Utilizzato per lo studio teorico, la comprensione guidata dei concetti del programma (principi SOLID, pattern MVC e Facade, unit testing con JUnit 5, automazione Gradle e persistenza JSON) e per la pianificazione dell'architettura.
* 🛠️ IntelliJ IDEA (AI Assistant & IDE):
Utilizzato per il debug rapido degli errori di compilazione e degli stack trace, per la risoluzione dei warning e per la generazione dello scheletro iniziale delle classi.

--------------------------------------------------------------------------------

## 📖 Documentazione Aggiuntiva
Per la spiegazione dettagliata delle scelte architetturali, dei principi SOLID e dei pattern utilizzati, consultare la Wiki del repository GitHub.
* 🎓 Autore: **Simone Pezzini** (Matricola 089027)
* 🏫 Corso: Metodologie di Programmazione e Modellazione della Conoscenza
* 🏛️ Università degli Studi di Camerino (UNICAM)