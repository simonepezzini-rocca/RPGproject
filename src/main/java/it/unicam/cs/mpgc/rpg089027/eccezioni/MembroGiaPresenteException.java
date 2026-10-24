package it.unicam.cs.mpgc.rpg089027.eccezioni;

/**
 * Eccezione unchecked lanciata quando si tenta di aggiungere a una squadra
 * un eroe già presente nella formazione.
 */
public class MembroGiaPresenteException extends RuntimeException {

    public MembroGiaPresenteException(String messaggio) {
        super(messaggio);
    }
}
