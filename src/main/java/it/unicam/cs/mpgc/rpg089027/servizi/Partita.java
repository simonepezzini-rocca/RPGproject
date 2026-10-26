package it.unicam.cs.mpgc.rpg089027.servizi;

import it.unicam.cs.mpgc.rpg089027.modelli.Combattente;
import it.unicam.cs.mpgc.rpg089027.modelli.Squadra;
import it.unicam.cs.mpgc.rpg089027.persistenza.ConfigurazioneSalvataggi;
import it.unicam.cs.mpgc.rpg089027.persistenza.GestoreSalvataggi;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Controller applicativo di alto livello (Pattern Facade) che coordina
 * lo stato della sessione di gioco, le operazioni di persistenza
 * e l'avvio degli scontri a turni.
 */
public class Partita {

    private Squadra squadra;
    private final GestoreSalvataggi gestoreSalvataggi;
    private GestoreCombattimento combattimentoCorrente;
    private int vittorieTotali;

    /**
     * Inizializza una nuova sessione di gioco con una squadra e un gestore di persistenza.
     *
     * @param squadra           la squadra iniziale di eroi (non nulla)
     * @param gestoreSalvataggi il servizio di salvataggio/caricamento (non nullo)
     */
    public Partita(Squadra squadra, GestoreSalvataggi gestoreSalvataggi) {
        if (squadra == null) {
            throw new IllegalArgumentException("La squadra non può essere nulla.");
        }
        if (gestoreSalvataggi == null) {
            throw new IllegalArgumentException("Il gestore dei salvataggi non può essere nullo.");
        }
        this.squadra = squadra;
        this.gestoreSalvataggi = gestoreSalvataggi;
        this.vittorieTotali = 0;
    }

    public Squadra getSquadra() {
        return squadra;
    }

    public int getVittorieTotali() {
        return vittorieTotali;
    }

    public Optional<GestoreCombattimento> getCombattimentoCorrente() {
        return Optional.ofNullable(combattimentoCorrente);
    }

    /**
     * Avvia un nuovo scontro tra la squadra e un avversario specificato.
     *
     * @param avversario l'entità ostile da affrontare
     * @return l'istanza di GestoreCombattimento creata per lo scontro
     */
    public GestoreCombattimento iniziaCombattimento(Combattente avversario) {
        this.combattimentoCorrente = new GestoreCombattimento(this.squadra, avversario);
        return this.combattimentoCorrente;
    }

    /**
     * Risolve lo scontro corrente e aggiorna il contatore delle vittorie in caso di successo.
     *
     * @return true se la squadra vince la battaglia, false altrimenti
     */
    public boolean concludiCombattimentoCorrente() {
        if (combattimentoCorrente == null) {
            throw new IllegalStateException("Nessun combattimento attualmente in corso.");
        }

        boolean vittoria = combattimentoCorrente.simulaInteroScontro();
        if (vittoria) {
            vittorieTotali++;
        }
        return vittoria;
    }

    /**
     * Salva lo stato attuale della squadra nel percorso specificato.
     *
     * @param percorso il file di destinazione su cui scrivere la squadra
     * @throws IOException in caso di errori di scrittura su file
     */
    public void salvaSquadraSuFile(Path percorso) throws IOException {
        if (percorso == null) {
            throw new IllegalArgumentException("Il percorso di salvataggio non può essere nullo.");
        }
        gestoreSalvataggi.salvaSquadra(this.squadra, percorso);
    }

    /**
     * Esegue il salvataggio rapido dello stato attuale della squadra.
     *
     * @throws IOException in caso di errori di scrittura su file
     */
    public void eseguiSalvataggioRapido() throws IOException {
        Path percorso = ConfigurazioneSalvataggi.risolviPercorsoSalvataggio(
                ConfigurazioneSalvataggi.NOME_SALVATAGGIO_RAPIDO
        );
        salvaSquadraSuFile(percorso);
    }

    /**
     * Carica lo stato della squadra da un file specificato, sostituendo la squadra attuale.
     *
     * @param percorso il file da cui ripristinare la squadra
     * @throws IOException in caso di problemi di I/O o formato non valido
     */
    public void caricaSquadraDaFile(Path percorso) throws IOException {
        this.squadra = gestoreSalvataggi.caricaSquadra(percorso);
        this.combattimentoCorrente = null;
    }
}