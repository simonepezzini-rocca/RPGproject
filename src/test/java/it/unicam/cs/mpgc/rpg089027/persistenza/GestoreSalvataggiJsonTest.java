package it.unicam.cs.mpgc.rpg089027.persistenza;

import it.unicam.cs.mpgc.rpg089027.modelli.Eroe;
import it.unicam.cs.mpgc.rpg089027.modelli.Guerriero;
import it.unicam.cs.mpgc.rpg089027.modelli.Mago;
import it.unicam.cs.mpgc.rpg089027.modelli.Squadra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test di unità per il servizio GestoreSalvataggiJson.
 * Verifica la corretta serializzazione e deserializzazione polimorfica su file
 * utilizzando una directory temporanea gestita da JUnit 5 (@TempDir).
 */
class GestoreSalvataggiJsonTest {

    private GestoreSalvataggiJson gestore;
    private Squadra squadraOriginale;
    private Guerriero guerriero;
    private Mago mago;

    @TempDir
    Path directoryTemporanea;

    @BeforeEach
    void setUp() {
        gestore = new GestoreSalvataggiJson();
        squadraOriginale = new Squadra("Eroi di Camerino");
        guerriero = new Guerriero("Gimli", 18);
        mago = new Mago("Saruman", 22, 70);

        squadraOriginale.aggiungiMembro(guerriero);
        squadraOriginale.aggiungiMembro(mago);
    }

    @Test
    @DisplayName("Salvataggio e caricamento mantengono l'integrità dei dati e il polimorfismo")
    void testSalvataggioECaricamentoSuccesso() throws IOException {
        Path fileDestinazione = directoryTemporanea.resolve("squadra_test.json");

        // Act: salvataggio su file
        gestore.salvaSquadra(squadraOriginale, fileDestinazione);
        assertTrue(Files.exists(fileDestinazione), "Il file JSON deve essere stato effettivamente creato su disco.");
        assertTrue(Files.size(fileDestinazione) > 0, "Il file JSON non deve essere vuoto.");

        // Act: caricamento dal file
        Squadra squadraCaricata = gestore.caricaSquadra(fileDestinazione);

        // Assert: verifica dello stato complessivo della squadra
        assertNotNull(squadraCaricata);
        assertEquals(squadraOriginale.getNomeSquadra(), squadraCaricata.getNomeSquadra());
        assertEquals(2, squadraCaricata.getNumeroMembri());

        // Assert: verifica del polimorfismo delle sottoclassi ricostruite
        Eroe primo = squadraCaricata.getMembri().get(0);
        Eroe secondo = squadraCaricata.getMembri().get(1);

        assertInstanceOf(Guerriero.class, primo, "Il primo membro deve essere ricostruito come Guerriero.");
        assertEquals("Gimli", primo.getNome());
        assertEquals(guerriero.getId(), primo.getId());
        assertEquals(18, ((Guerriero) primo).getForzaFisica());

        assertInstanceOf(Mago.class, secondo, "Il secondo membro deve essere ricostruito come Mago.");
        assertEquals("Saruman", secondo.getNome());
        assertEquals(mago.getId(), secondo.getId());
        assertEquals(70, ((Mago) secondo).getMana());
    }

    @Test
    @DisplayName("Tentare di caricare da un file inesistente solleva IOException")
    void testCaricaFileInesistente() {
        Path fileInesistente = directoryTemporanea.resolve("non_esiste.json");
        assertThrows(IOException.class, () -> gestore.caricaSquadra(fileInesistente));
    }

    @Test
    @DisplayName("Validazione degli argomenti nulli nei metodi di salvataggio e caricamento")
    void testPrecondizioniParametriNulli() {
        Path percorsoValido = directoryTemporanea.resolve("test.json");

        assertThrows(IllegalArgumentException.class, () -> gestore.salvaSquadra(null, percorsoValido));
        assertThrows(IllegalArgumentException.class, () -> gestore.salvaSquadra(squadraOriginale, null));
        assertThrows(IllegalArgumentException.class, () -> gestore.caricaSquadra(null));
    }
}