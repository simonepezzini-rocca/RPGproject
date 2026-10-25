package it.unicam.cs.mpgc.rpg089027.servizi;

import it.unicam.cs.mpgc.rpg089027.modelli.Guerriero;
import it.unicam.cs.mpgc.rpg089027.modelli.Mago;
import it.unicam.cs.mpgc.rpg089027.modelli.Mostro;
import it.unicam.cs.mpgc.rpg089027.modelli.Squadra;
import it.unicam.cs.mpgc.rpg089027.persistenza.GestoreSalvataggiJson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test di unità per la classe coordinatrice Partita.
 * Verifica l'orchestrazione degli scontri, l'aggiornamento delle statistiche
 * e l'integrazione con la persistenza.
 */
class PartitaTest {

    private Partita partita;
    private Squadra squadraIniziale;
    private GestoreSalvataggiJson gestoreSalvataggi;

    @TempDir
    Path directoryTemporanea;

    @BeforeEach
    void setUp() {
        squadraIniziale = new Squadra("Guardiani del Nord");
        squadraIniziale.aggiungiMembro(new Guerriero("Boromir", 16));
        squadraIniziale.aggiungiMembro(new Mago("Radagast", 18, 45));

        gestoreSalvataggi = new GestoreSalvataggiJson();
        partita = new Partita(squadraIniziale, gestoreSalvataggi);
    }

    @Test
    @DisplayName("Inizializzazione corretta dello stato iniziale della partita")
    void testInizializzazionePartita() {
        assertNotNull(partita.getSquadra());
        assertEquals("Guardiani del Nord", partita.getSquadra().getNomeSquadra());
        assertEquals(0, partita.getVittorieTotali());
        assertTrue(partita.getCombattimentoCorrente().isEmpty());
    }

    @Test
    @DisplayName("Avvio di un combattimento e aggiornamento vittorie in caso di successo")
    void testFlussoCombattimentoEVittoria() {
        Mostro nemicoDebole = new Mostro("Fanghiglia", 20, 2);

        GestoreCombattimento combattimento = partita.iniziaCombattimento(nemicoDebole);
        assertNotNull(combattimento);
        assertTrue(partita.getCombattimentoCorrente().isPresent());

        boolean vittoria = partita.concludiCombattimentoCorrente();
        assertTrue(vittoria);
        assertEquals(1, partita.getVittorieTotali());
    }

    @Test
    @DisplayName("Concludere un combattimento quando non ce n'è uno attivo solleva IllegalStateException")
    void testConcludiCombattimentoNonAttivo() {
        assertThrows(IllegalStateException.class, () -> partita.concludiCombattimentoCorrente());
    }

    @Test
    @DisplayName("Caricamento di una nuova squadra da file sostituisce quella corrente")
    void testCaricaSquadraDaFile() throws IOException {
        // Prepariamo una seconda squadra salvata su file temporaneo
        Squadra secondaSquadra = new Squadra("Nuova Alleanza");
        secondaSquadra.aggiungiMembro(new Guerriero("Gimli", 20));
        Path fileSalvataggio = directoryTemporanea.resolve("alleanza.json");
        gestoreSalvataggi.salvaSquadra(secondaSquadra, fileSalvataggio);

        // Act: carichiamo la nuova squadra nella partita
        partita.caricaSquadraDaFile(fileSalvataggio);

        // Assert: la squadra attiva della partita è stata sostituita
        assertEquals("Nuova Alleanza", partita.getSquadra().getNomeSquadra());
        assertEquals(1, partita.getSquadra().getNumeroMembri());
        assertEquals("Gimli", partita.getSquadra().getMembri().get(0).getNome());
    }
}