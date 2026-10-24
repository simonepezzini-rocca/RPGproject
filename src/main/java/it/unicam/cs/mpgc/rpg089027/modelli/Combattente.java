package it.unicam.cs.mpgc.rpg089027.modelli;

/**
 * Contratto comportamentale per qualsiasi entità in grado di partecipare
 * a un combattimento all'interno del gioco.
 * <p>
 * Definisce le operazioni fondamentali per la gestione dello stato vitale
 * e l'erogazione dei danni in modo polimorfico.
 */
public interface Combattente {

    /**
     * Restituisce il nome identificativo del combattente.
     *
     * @return il nome del combattente
     */
    String getNome();

    /**
     * Restituisce i punti vita attuali del combattente.
     *
     * @return punti vita correnti
     */
    int getPuntiVita();

    /**
     * Applica un quantitativo di danno ai punti vita del combattente.
     *
     * @param danno la quantità di danno subito (deve essere non negativa)
     */
    void subisciDanno(int danno);

    /**
     * Verifica se il combattente è ancora in grado di combattere.
     *
     * @return true se i punti vita sono maggiori di zero, false altrimenti
     */
    boolean isVivo();

    /**
     * Calcola il valore di attacco base generato dal combattente durante il proprio turno.
     *
     * @return il valore intero del danno inflitto
     */
    int calcolaDannoAttacco();
}