package it.unicam.cs.mpgc.rpg089027.servizi;

import it.unicam.cs.mpgc.rpg089027.modelli.Guerriero;
import it.unicam.cs.mpgc.rpg089027.modelli.Mago;
import it.unicam.cs.mpgc.rpg089027.modelli.Mostro;
import it.unicam.cs.mpgc.rpg089027.modelli.Squadra;
import it.unicam.cs.mpgc.rpg089027.persistenza.GestoreSalvataggiJson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Test di Integrazione - Flusso completo della Partita")
class PartitaIntegrazioneTest {

    @Test
    @DisplayName("Ciclo completo: Creazione squadra, combattimento e persistenza JSON")
    void testFlussoCompletoPartitaEJson(@TempDir Path tempDir) throws IOException {
        // Arrange
        Squadra squadra = new Squadra("Eroi di Camerino");
        squadra.aggiungiMembro(new Guerriero("Thor", 25));
        squadra.aggiungiMembro(new Mago("Gandalf", 20, 60));

        Partita partita = new Partita(squadra, new GestoreSalvataggiJson());
        Mostro nemico = new Mostro("Goblin", 30, 5);

        // Act - 1. Combattimento
        GestoreCombattimento scontro = partita.iniziaCombattimento(nemico);
        boolean vittoria = partita.concludiCombattimentoCorrente();

        // Assert - 1. Esito scontro
        assertTrue(vittoria, "La squadra dovrebbe vincere contro un debole Goblin");
        assertEquals(1, partita.getVittorieTotali());
        assertFalse(nemico.isVivo());

        // Act - 2. Salvataggio su file JSON temporaneo
        Path fileSalvataggio = tempDir.resolve("partita_test.json");
        partita.salvaSquadraSuFile(fileSalvataggio);

        // Act - 3. Caricamento in nuova sessione
        Partita nuovaPartita = new Partita(new Squadra("Provvisoria"), new GestoreSalvataggiJson());
        nuovaPartita.caricaSquadraDaFile(fileSalvataggio);

        // Assert - 3. Verifica integrità dati ripristinati
        assertEquals("Eroi di Camerino", nuovaPartita.getSquadra().getNomeSquadra());
        assertEquals(2, nuovaPartita.getSquadra().getNumeroMembri());
        assertTrue(nuovaPartita.getSquadra().isAlmenoUnoVivo());
    }
}
