package it.unicam.cs.mpgc.rpg089027.servizi;

import it.unicam.cs.mpgc.rpg089027.modelli.Guerriero;
import it.unicam.cs.mpgc.rpg089027.modelli.Mago;
import it.unicam.cs.mpgc.rpg089027.modelli.Mostro;
import it.unicam.cs.mpgc.rpg089027.modelli.Squadra;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test di unità per il servizio GestoreCombattimento.
 * Verifica la logica dei turni, le condizioni di vittoria/sconfitta,
 * la registrazione degli eventi e le precondizioni del costruttore.
 */
class GestoreCombattimentoTest {

    private Squadra squadra;
    private Guerriero guerriero;
    private Mago mago;
    private Mostro mostroDebole;
    private Mostro bossLetale;

    @BeforeEach
    void setUp() {
        squadra = new Squadra("Compagnia dell'Anello");
        guerriero = new Guerriero("Aragorn", 20);
        mago = new Mago("Gandalf", 25, 60);

        squadra.aggiungiMembro(guerriero);
        squadra.aggiungiMembro(mago);

        mostroDebole = new Mostro("Goblin", 30, 5);
        bossLetale = new Mostro("Drago Antico", 500, 150);
    }

    @Test
    @DisplayName("Il costruttore valida le precondizioni su squadra e avversario")
    void testPrecondizioniCostruttore() {
        Squadra squadraVuota = new Squadra("Vuota");
        assertThrows(IllegalArgumentException.class, () -> new GestoreCombattimento(squadraVuota, mostroDebole));
        assertThrows(IllegalArgumentException.class, () -> new GestoreCombattimento(null, mostroDebole));
        assertThrows(IllegalArgumentException.class, () -> new GestoreCombattimento(squadra, null));

        Mostro mostroMorto = new Mostro("Spettro", 10, 5);
        mostroMorto.subisciDanno(10);
        assertThrows(IllegalArgumentException.class, () -> new GestoreCombattimento(squadra, mostroMorto));
    }

    @Test
    @DisplayName("Esecuzione di un singolo turno e avanzamento contatore turni")
    void testEseguiTurnoSingolo() {
        GestoreCombattimento gestore = new GestoreCombattimento(squadra, mostroDebole);
        assertEquals(0, gestore.getNumeroTurno());

        boolean inCorso = gestore.eseguiTurno();
        assertEquals(1, gestore.getNumeroTurno());
        assertTrue(gestore.getRegistroBattaglia().size() > 1);
    }

    @Test
    @DisplayName("Simulazione completa: vittoria della squadra contro avversario debole")
    void testVittoriaSquadra() {
        GestoreCombattimento gestore = new GestoreCombattimento(squadra, mostroDebole);

        boolean vittoria = gestore.simulaInteroScontro();

        assertTrue(vittoria);
        assertTrue(gestore.isVittoriaSquadra());
        assertTrue(gestore.isCombattimentoTerminato());
        assertFalse(mostroDebole.isVivo());
        assertTrue(squadra.isAlmenoUnoVivo());
    }

    @Test
    @DisplayName("Simulazione completa: sconfitta della squadra contro un boss letale")
    void testSconfittaSquadra() {
        GestoreCombattimento gestore = new GestoreCombattimento(squadra, bossLetale);

        boolean vittoria = gestore.simulaInteroScontro();

        assertFalse(vittoria);
        assertFalse(gestore.isVittoriaSquadra());
        assertTrue(gestore.isCombattimentoTerminato());
        assertTrue(bossLetale.isVivo());
        assertFalse(squadra.isAlmenoUnoVivo());
    }

    @Test
    @DisplayName("Il registro di battaglia è non modificabile dall'esterno")
    void testIncapsulamentoRegistroBattaglia() {
        GestoreCombattimento gestore = new GestoreCombattimento(squadra, mostroDebole);
        List<String> log = gestore.getRegistroBattaglia();

        assertThrows(UnsupportedOperationException.class, () -> log.add("Evento non autorizzato"));
    }
}