package it.unicam.cs.mpgc.rpg089027.servizi;

import it.unicam.cs.mpgc.rpg089027.modelli.Guerriero;
import it.unicam.cs.mpgc.rpg089027.modelli.Mostro;
import it.unicam.cs.mpgc.rpg089027.modelli.Squadra;
import it.unicam.cs.mpgc.rpg089027.persistenza.GestoreSalvataggiJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test Controlli di Flusso ed Eccezioni del Facade Partita")
class PartitaFlussoTest {

    private Partita partita;

    @BeforeEach
    void setUp() {
        Squadra squadra = new Squadra("Guardiani");
        squadra.aggiungiMembro(new Guerriero("Gimli", 20));
        partita = new Partita(squadra, new GestoreSalvataggiJson());
    }

    @Test
    @DisplayName("Inizializzazione con parametri null lancia IllegalArgumentException")
    void testInizializzazioneNull() {
        assertThrows(IllegalArgumentException.class, () -> new Partita(null, new GestoreSalvataggiJson()));
        assertThrows(IllegalArgumentException.class, () -> new Partita(new Squadra("Test"), null));
    }

    @Test
    @DisplayName("Conclusione scontro senza prima averlo iniziato lancia IllegalStateException")
    void testConcludiScontroMaiIniziato() {
        assertThrows(IllegalStateException.class, () -> partita.concludiCombattimentoCorrente());
    }

    @Test
    @DisplayName("Salvataggio con percorso nullo lancia IllegalArgumentException")
    void testSalvataggioPercorsoNull() {
        assertThrows(IllegalArgumentException.class, () -> partita.salvaSquadraSuFile(null));
    }

    @Test
    @DisplayName("Combattimento corrente restituisce Optional coerente")
    void testOptionalCombattimentoCorrente() {
        assertTrue(partita.getCombattimentoCorrente().isEmpty());
        partita.iniziaCombattimento(new Mostro("Troll", 50, 10));
        assertTrue(partita.getCombattimentoCorrente().isPresent());
    }
}