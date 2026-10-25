package it.unicam.cs.mpgc.rpg089027.persistenza;

import it.unicam.cs.mpgc.rpg089027.modelli.Squadra;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Contratto per i servizi responsabili del salvataggio e caricamento
 * dello stato della {@link Squadra} su supporto permanente.
 * <p>
 * Applica il Dependency Inversion Principle separando la logica di gioco
 * dalle specifiche tecnologiche del formato di memorizzazione.
 */
public interface GestoreSalvataggi {

    /**
     * Serializza e salva lo stato della squadra nel percorso specificato.
     *
     * @param squadra  la squadra da salvare (non nulla)
     * @param percorso il percorso del file di destinazione (non nullo)
     * @throws IOException              in caso di errori di I/O durante la scrittura
     * @throws IllegalArgumentException se uno dei parametri è nullo
     */
    void salvaSquadra(Squadra squadra, Path percorso) throws IOException;

    /**
     * Carica e ricostruisce una squadra a partire dal file specificato.
     *
     * @param percorso il percorso del file sorgente da cui leggere (non nullo)
     * @return l'istanza della squadra ripristinata con tutti i suoi eroi
     * @throws IOException              in caso di errori di lettura o file non trovato
     * @throws IllegalArgumentException se il percorso è nullo
     */
    Squadra caricaSquadra(Path percorso) throws IOException;
}