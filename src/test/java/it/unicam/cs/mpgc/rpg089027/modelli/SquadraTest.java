package it.unicam.cs.mpgc.rpg089027.modelli;

import it.unicam.cs.mpgc.rpg089027.eccezioni.MembroGiaPresenteException;
import it.unicam.cs.mpgc.rpg089027.eccezioni.MembroNonTrovatoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test di unità per la classe aggregata Squadra.
 * Verifica la gestione dei membri, il rifiuto dei duplicati, l'immutabilità della vista
 * e l'interrogazione dello stato vitale tramite Stream.
 */
class SquadraTest {

    private Squadra squadra;
    private Guerriero guerriero;
    private Mago mago;

    @BeforeEach
    void setUp() {
        squadra = new Squadra("Custodi del Regno");
        guerriero = new Guerriero("Thorin", 15);
        mago = new Mago("Gandalf", 20, 50);
    }

    @Test
    @DisplayName("La squadra viene inizializzata con il nome corretto ed è inizialmente vuota")
    void testInizializzazioneSquadra() {
        assertEquals("Custodi del Regno", squadra.getNomeSquadra());
        assertEquals(0, squadra.getNumeroMembri());
        assertFalse(squadra.isAlmenoUnoVivo());
    }

    @Test
    @DisplayName("Aggiunta corretta di membri alla squadra")
    void testAggiungiMembroSuccesso() {
        squadra.aggiungiMembro(guerriero);
        squadra.aggiungiMembro(mago);

        assertEquals(2, squadra.getNumeroMembri());
        assertTrue(squadra.isAlmenoUnoVivo());
    }

    @Test
    @DisplayName("L'inserimento di un membro duplicato solleva MembroGiaPresenteException")
    void testAggiungiMembroDuplicato() {
        squadra.aggiungiMembro(guerriero);

        assertThrows(MembroGiaPresenteException.class, () -> squadra.aggiungiMembro(guerriero));
    }

    @Test
    @DisplayName("La lista esposta da getMembri() è non modificabile")
    void testIncapsulamentoListaMembri() {
        squadra.aggiungiMembro(guerriero);
        List<Eroe> membri = squadra.getMembri();

        assertThrows(UnsupportedOperationException.class, () -> membri.add(mago));
    }

    @Test
    @DisplayName("Rimozione di un membro esistente tramite ID univoco")
    void testRimuoviMembroEsistente() {
        squadra.aggiungiMembro(guerriero);
        UUID idGuerriero = guerriero.getId();

        squadra.rimuoviMembro(idGuerriero);
        assertEquals(0, squadra.getNumeroMembri());
    }

    @Test
    @DisplayName("Tentativo di rimozione con ID inesistente solleva MembroNonTrovatoException")
    void testRimuoviMembroNonPresente() {
        squadra.aggiungiMembro(guerriero);
        UUID idCasuale = UUID.randomUUID();

        assertThrows(MembroNonTrovatoException.class, () -> squadra.rimuoviMembro(idCasuale));
    }

    @Test
    @DisplayName("Ricerca per ID tramite Stream restituisce l'Optional corretto")
    void testTrovaEroePerId() {
        squadra.aggiungiMembro(guerriero);

        Optional<Eroe> trovato = squadra.trovaEroePerId(guerriero.getId());
        assertTrue(trovato.isPresent());
        assertEquals("Thorin", trovato.get().getNome());

        Optional<Eroe> nonTrovato = squadra.trovaEroePerId(UUID.randomUUID());
        assertTrue(nonTrovato.isEmpty());
    }

    @Test
    @DisplayName("Il filtraggio dei membri vivi gestisce correttamente la salute della squadra")
    void testGetMembriViviEStatoSalute() {
        squadra.aggiungiMembro(guerriero);
        squadra.aggiungiMembro(mago);

        // Inizialmente entrambi sono vivi
        assertEquals(2, squadra.getMembriVivi().size());
        assertTrue(squadra.isAlmenoUnoVivo());

        // Il guerriero subisce danno fatale
        guerriero.subisciDanno(150);
        assertFalse(guerriero.isVivo());

        assertEquals(1, squadra.getMembriVivi().size());
        assertEquals("Gandalf", squadra.getMembriVivi().get(0).getNome());
        assertTrue(squadra.isAlmenoUnoVivo());

        // Anche il mago subisce danno fatale
        mago.subisciDanno(100);
        assertFalse(mago.isVivo());

        assertEquals(0, squadra.getMembriVivi().size());
        assertFalse(squadra.isAlmenoUnoVivo());
    }
}