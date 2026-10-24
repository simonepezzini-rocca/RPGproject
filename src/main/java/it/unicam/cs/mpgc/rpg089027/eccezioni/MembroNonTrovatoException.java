package it.unicam.cs.mpgc.rpg089027.eccezioni;

/**
 * Eccezione unchecked lanciata quando l'eroe cercato o richiesto per un'operazione
 * non risulta presente all'interno della squadra.
 */
public class MembroNonTrovatoException extends RuntimeException {

    public MembroNonTrovatoException(String messaggio) {
        super(messaggio);
    }
}