package it.unicam.cs.mpgc.rpg089027.servizi;

import it.unicam.cs.mpgc.rpg089027.modelli.Combattente;
import it.unicam.cs.mpgc.rpg089027.modelli.Eroe;
import it.unicam.cs.mpgc.rpg089027.modelli.Squadra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Servizio applicativo responsabile della gestione e risoluzione
 * degli scontri a turni tra una {@link Squadra} di eroi e un avversario {@link Combattente}.
 * <p>
 * Mantiene la cronologia degli eventi di battaglia per consentire
 * la rendicontazione sia testuale che grafica delle azioni.
 */
public class GestoreCombattimento {

    private final Squadra squadra;
    private final Combattente avversario;
    private final List<String> registroBattaglia;
    private int numeroTurno;

    /**
     * Inizializza una nuova sessione di combattimento tra la squadra e l'avversario specificato.
     *
     * @param squadra    la squadra di eroi partecipanti (deve contenere almeno un membro vivo)
     * @param avversario l'entità ostile avversaria (deve essere in vita)
     */
    public GestoreCombattimento(Squadra squadra, Combattente avversario) {
        if (squadra == null || !squadra.isAlmenoUnoVivo()) {
            throw new IllegalArgumentException("La squadra deve essere presente e avere almeno un membro in vita.");
        }
        if (avversario == null || !avversario.isVivo()) {
            throw new IllegalArgumentException("L'avversario deve essere presente e in vita.");
        }
        this.squadra = squadra;
        this.avversario = avversario;
        this.registroBattaglia = new ArrayList<>();
        this.numeroTurno = 0;
        registraEvento(String.format("Inizio scontro: Squadra '%s' contro %s!",
                squadra.getNomeSquadra(), avversario.getNome()));
    }

    public Squadra getSquadra() {
        return squadra;
    }

    public Combattente getAvversario() {
        return avversario;
    }

    public int getNumeroTurno() {
        return numeroTurno;
    }

    /**
     * Restituisce una copia non modificabile del registro degli eventi di battaglia.
     *
     * @return lista non modificabile dei messaggi di log
     */
    public List<String> getRegistroBattaglia() {
        return Collections.unmodifiableList(registroBattaglia);
    }

    /**
     * Esegue un singolo turno completo di combattimento:
     * 1. Tutti gli eroi vivi della squadra attaccano o usano abilità speciali contro l'avversario.
     * 2. Se l'avversario sopravvive, contrattacca colpendo un eroe vivo della squadra.
     *
     * @return true se il combattimento è ancora in corso, false se uno dei due contendenti è sconfitto
     */
    public boolean eseguiTurno() {
        if (isCombattimentoTerminato()) {
            return false;
        }

        numeroTurno++;
        registraEvento(String.format("--- INIZIO TURNO %d ---", numeroTurno));

        // Fase 1: Turno degli Eroi
        for (Eroe eroe : squadra.getMembriVivi()) {
            if (!avversario.isVivo()) {
                break; // L'avversario è già caduto durante questo turno
            }

            // Alternanza: usa abilità speciale se il turno è pari, altrimenti attacco base
            if (numeroTurno % 2 == 0) {
                String esitoAbilita = eroe.eseguiAbilitaSpeciale(avversario);
                registraEvento(esitoAbilita);
            } else {
                int danno = eroe.calcolaDannoAttacco();
                avversario.subisciDanno(danno);
                registraEvento(String.format("%s attacca %s infliggendo %d danni fisici! (HP avversario residui: %d)",
                        eroe.getNome(), avversario.getNome(), danno, avversario.getPuntiVita()));
            }

            if (!avversario.isVivo()) {
                registraEvento(String.format("%s è stato sconfitto! La squadra '%s' trionfa!",
                        avversario.getNome(), squadra.getNomeSquadra()));
                return false;
            }
        }

        // Fase 2: Contrattacco dell'Avversario (se ancora in vita)
        if (avversario.isVivo() && squadra.isAlmenoUnoVivo()) {
            List<Eroe> superstiti = squadra.getMembriVivi();
            // Il mostro bersaglia il primo eroe superstite della linea
            Eroe bersaglio = superstiti.get(0);
            int dannoNemico = avversario.calcolaDannoAttacco();
            bersaglio.subisciDanno(dannoNemico);

            registraEvento(String.format("%s contrattacca ferocemente %s infliggendo %d danni! (HP residui: %d)",
                    avversario.getNome(), bersaglio.getNome(), dannoNemico, bersaglio.getPuntiVita()));

            if (!bersaglio.isVivo()) {
                registraEvento(String.format("L'eroe %s è caduto in battaglia!", bersaglio.getNome()));
            }

            if (!squadra.isAlmenoUnoVivo()) {
                registraEvento(String.format("Tutti i membri della squadra '%s' sono stati sconfitti... Game Over!",
                        squadra.getNomeSquadra()));
                return false;
            }
        }

        return !isCombattimentoTerminato();
    }

    /**
     * Risolve l'intero scontro eseguendo turni sequenziali fino alla sconfitta di una delle due parti.
     *
     * @return true se la squadra vince, false se la squadra viene sterminata
     */
    public boolean simulaInteroScontro() {
        while (!isCombattimentoTerminato()) {
            eseguiTurno();
        }
        return isVittoriaSquadra();
    }

    /**
     * Verifica se lo scontro è terminato.
     *
     * @return true se l'avversario è morto o se tutti gli eroi sono caduti
     */
    public boolean isCombattimentoTerminato() {
        return !avversario.isVivo() || !squadra.isAlmenoUnoVivo();
    }

    /**
     * Determina se l'esito finale è una vittoria per gli eroi.
     *
     * @return true se la squadra ha almeno un superstite e l'avversario è sconfitto
     */
    public boolean isVittoriaSquadra() {
        return squadra.isAlmenoUnoVivo() && !avversario.isVivo();
    }

    private void registraEvento(String messaggio) {
        this.registroBattaglia.add(messaggio);
    }
}
