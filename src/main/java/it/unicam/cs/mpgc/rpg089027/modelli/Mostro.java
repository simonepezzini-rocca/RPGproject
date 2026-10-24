package it.unicam.cs.mpgc.rpg089027.modelli;

/**
 * Rappresenta un'entità ostile controllata dal sistema di gioco.
 * <p>
 * Implementa {@link Combattente} per consentire l'interazione diretta
 * e polimorfica con gli eroi all'interno delle sessioni di combattimento.
 */
public class Mostro implements Combattente {

    private final String nome;
    private int puntiVita;
    private final int puntiVitaMassimi;
    private final int attaccoBase;

    /**
     * Costruisce un'istanza di Mostro con parametri specificati.
     *
     * @param nome             il nome identificativo del mostro
     * @param puntiVitaMassimi i punti vita massimi iniziali (maggiori di zero)
     * @param attaccoBase      il valore dell'attacco base (maggiore di zero)
     */
    public Mostro(String nome, int puntiVitaMassimi, int attaccoBase) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del mostro non può essere vuoto o nullo.");
        }
        if (puntiVitaMassimi <= 0) {
            throw new IllegalArgumentException("I punti vita massimi devono essere maggiori di zero.");
        }
        if (attaccoBase <= 0) {
            throw new IllegalArgumentException("L'attacco base deve essere maggiore di zero.");
        }
        this.nome = nome.trim();
        this.puntiVitaMassimi = puntiVitaMassimi;
        this.puntiVita = puntiVitaMassimi;
        this.attaccoBase = attaccoBase;
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public int getPuntiVita() {
        return puntiVita;
    }

    public int getPuntiVitaMassimi() {
        return puntiVitaMassimi;
    }

    public int getAttaccoBase() {
        return attaccoBase;
    }

    @Override
    public void subisciDanno(int danno) {
        if (danno < 0) {
            throw new IllegalArgumentException("Il danno non può essere negativo.");
        }
        this.puntiVita = Math.max(0, this.puntiVita - danno);
    }

    @Override
    public boolean isVivo() {
        return this.puntiVita > 0;
    }

    @Override
    public int calcolaDannoAttacco() {
        return this.attaccoBase;
    }

    @Override
    public String toString() {
        return String.format("Mostro '%s' - HP: %d/%d (Attacco: %d)", nome, puntiVita, puntiVitaMassimi, attaccoBase);
    }
}