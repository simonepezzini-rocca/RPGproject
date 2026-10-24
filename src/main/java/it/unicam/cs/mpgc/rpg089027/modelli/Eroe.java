package it.unicam.cs.mpgc.rpg089027.modelli;

import java.util.Objects;
import java.util.UUID;

/**
 * Rappresenta la classe base astratta per tutti i personaggi giocabili (Eroi).
 * <p>
 * Implementa l'interfaccia {@link Combattente} per fornire il comportamento comune
 * di combattimento e incapsula l'identità univoca (UUID) e i parametri vitali di base.
 */
public abstract class Eroe implements Combattente {

    private final UUID id;
    private final String nome;
    private int livello;
    private int puntiVita;
    private final int puntiVitaMassimi;

    /**
     * Costruttore protetto destinato all'invocazione da parte delle sottoclassi concrete.
     *
     * @param nome             il nome dell'eroe (non nullo e non vuoto)
     * @param puntiVitaMassimi i punti vita massimi iniziali (devono essere maggiori di zero)
     */
    protected Eroe(String nome, int puntiVitaMassimi) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome dell'eroe non può essere vuoto o nullo.");
        }
        if (puntiVitaMassimi <= 0) {
            throw new IllegalArgumentException("I punti vita massimi devono essere maggiori di zero.");
        }
        this.id = UUID.randomUUID();
        this.nome = nome.trim();
        this.livello = 1;
        this.puntiVitaMassimi = puntiVitaMassimi;
        this.puntiVita = puntiVitaMassimi;
    }

    public UUID getId() {
        return id;
    }

    @Override
    public String getNome() {
        return nome;
    }

    public int getLivello() {
        return livello;
    }

    public void aumentaLivello() {
        this.livello++;
    }

    @Override
    public int getPuntiVita() {
        return puntiVita;
    }

    public int getPuntiVitaMassimi() {
        return puntiVitaMassimi;
    }

    @Override
    public void subisciDanno(int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo.");
        }
        this.puntiVita = Math.max(0, this.puntiVita - danno);
    }

    /**
     * Ripristina un certo ammontare di punti vita, senza superare il massimale.
     *
     * @param cura la quantità di salute da ripristinare
     */
    public void cura(int cura) {
        if (cura < 0) {
            throw new IllegalArgumentException("La quantità di cura non può essere negativa.");
        }
        this.puntiVita = Math.min(this.puntiVitaMassimi, this.puntiVita + cura);
    }

    @Override
    public boolean isVivo() {
        return this.puntiVita > 0;
    }

    /**
     * Esegue l'abilità speciale caratteristica della specifica classe di eroe.
     *
     * @param bersaglio l'avversario contro cui scagliare l'abilità
     * @return una stringa descrittiva dell'esito dell'abilità speciale
     */
    public abstract String eseguiAbilitaSpeciale(Combattente bersaglio);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Eroe eroe)) return false;
        return Objects.equals(id, eroe.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s (Lv.%d) - HP: %d/%d", nome, livello, puntiVita, puntiVitaMassimi);
    }
}