package it.unicam.cs.mpgc.rpg089027.modelli;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite di test di unità per la gerarchia di eroi (Guerriero e Mago).
 * Verifica le precondizioni dei costruttori, la meccanica di danno e le abilità speciali.
 */
class EroiTest {

    private Guerriero guerriero;
    private Mago mago;
    private Mostro bersaglio;

    @BeforeEach
    void setUp() {
        guerriero = new Guerriero("Thorin", 15);
        mago = new Mago("Gandalf", 20, 50);
        bersaglio = new Mostro("Orco", 100, 10);
    }

    @Test
    @DisplayName("Il Guerriero viene inizializzato con parametri corretti")
    void testInizializzazioneGuerriero() {
        assertEquals("Thorin", guerriero.getNome());
        assertEquals(120, guerriero.getPuntiVita());
        assertEquals(120, guerriero.getPuntiVitaMassimi());
        assertEquals(1, guerriero.getLivello());
        assertEquals(15, guerriero.getForzaFisica());
        assertTrue(guerriero.isVivo());
    }

    @Test
    @DisplayName("Il costruttore del Guerriero rifiuta parametri non validi")
    void testPrecondizioniGuerriero() {
        assertThrows(IllegalArgumentException.class, () -> new Guerriero("", 10));
        assertThrows(IllegalArgumentException.class, () -> new Guerriero(null, 10));
        assertThrows(IllegalArgumentException.class, () -> new Guerriero("Conan", 0));
        assertThrows(IllegalArgumentException.class, () -> new Guerriero("Conan", -5));
    }

    @Test
    @DisplayName("Subire danno scala correttamente i punti vita senza scendere sotto zero")
    void testSubisciDanno() {
        guerriero.subisciDanno(40);
        assertEquals(80, guerriero.getPuntiVita());
        assertTrue(guerriero.isVivo());

        // Danno superiore ai punti vita residui
        guerriero.subisciDanno(200);
        assertEquals(0, guerriero.getPuntiVita());
        assertFalse(guerriero.isVivo());
    }

    @Test
    @DisplayName("L'abilità speciale del Guerriero infligge danni e riduce la vita del bersaglio")
    void testAbilitaSpecialeGuerriero() {
        int hpIniziali = bersaglio.getPuntiVita();
        String esito = guerriero.eseguiAbilitaSpeciale(bersaglio);

        assertNotNull(esito);
        assertTrue(bersaglio.getPuntiVita() < hpIniziali);
    }

    @Test
    @DisplayName("Il Mago consuma mana per lanciare la Palla di Fuoco")
    void testAbilitaSpecialeMagoConMana() {
        int manaIniziale = mago.getMana();
        mago.eseguiAbilitaSpeciale(bersaglio);

        assertEquals(manaIniziale - 20, mago.getMana());
    }

    @Test
    @DisplayName("Il Mago a corto di mana recupera energia con attacco d'emergenza")
    void testAbilitaSpecialeMagoSenzaMana() {
        // Scarichiamo il mana del mago eseguendo più attacchi speciali
        mago.eseguiAbilitaSpeciale(bersaglio); // 50 - 20 = 30
        mago.eseguiAbilitaSpeciale(bersaglio); // 30 - 20 = 10

        // Con 10 mana non può fare Palla di Fuoco (costo 20): recupera 10 mana
        mago.eseguiAbilitaSpeciale(bersaglio);
        assertEquals(20, mago.getMana());
    }
}
