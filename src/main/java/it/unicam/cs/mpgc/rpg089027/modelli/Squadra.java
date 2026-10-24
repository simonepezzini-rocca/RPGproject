package it.unicam.cs.mpgc.rpg089027.modelli;

import it.unicam.cs.mpgc.rpg089027.eccezioni.MembroGiaPresenteException;
import it.unicam.cs.mpgc.rpg089027.eccezioni.MembroNonTrovatoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Gestisce l'aggregazione di un gruppo di eroi che partecipano insieme all'avventura.
 * <p>
 * Fornisce operazioni per l'arruolamento, la rimozione e il monitoraggio
 * dello stato dei componenti, incapsulando la lista interna e impiegando
 * l'API Stream di Java per le interrogazioni.
 */
public class Squadra {

    private final String nomeSquadra;
    private final List<Eroe> membri;

    /**
     * Costruisce una nuova squadra vuota con un nome assegnato.
     *
     * @param nomeSquadra il nome della squadra (non nullo e non vuoto)
     */
    public Squadra(String nomeSquadra) {
        if (nomeSquadra == null || nomeSquadra.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome della squadra non può essere vuoto o nullo.");
        }
        this.nomeSquadra = nomeSquadra.trim();
        this.membri = new ArrayList<>();
    }

    public String getNomeSquadra() {
        return nomeSquadra;
    }

    /**
     * Restituisce una vista non modificabile dei membri della squadra.
     *
     * @return lista non modificabile degli eroi
     */
    public List<Eroe> getMembri() {
        return Collections.unmodifiableList(membri);
    }

    /**
     * Aggiunge un eroe alla squadra.
     *
     * @param eroe l'eroe da aggiungere
     * @throws MembroGiaPresenteException se l'eroe fa già parte della squadra
     */
    public void aggiungiMembro(Eroe eroe) {
        if (eroe == null) {
            throw new IllegalArgumentException("L'eroe da aggiungere non può essere nullo.");
        }
        if (membri.contains(eroe)) {
            throw new MembroGiaPresenteException("L'eroe '" + eroe.getNome() + "' fa già parte della squadra.");
        }
        membri.add(eroe);
    }

    /**
     * Rimuove un eroe dalla squadra dato il suo identificativo univoco.
     *
     * @param id identificativo univoco dell'eroe
     * @throws MembroNonTrovatoException se nessun eroe corrisponde all'identificativo
     */
    public void rimuoviMembro(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID non può essere nullo.");
        }
        boolean rimosso = membri.removeIf(eroe -> eroe.getId().equals(id));
        if (!rimosso) {
            throw new MembroNonTrovatoException("Nessun membro trovato con ID: " + id);
        }
    }

    /**
     * Cerca un eroe all'interno della squadra dato il suo ID.
     *
     * @param id identificativo univoco dell'eroe
     * @return Optional contenente l'eroe se presente, vuoto altrimenti
     */
    public Optional<Eroe> trovaEroePerId(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return membri.stream()
                .filter(e -> e.getId().equals(id))
                .findFirst();
    }

    /**
     * Restituisce la lista di eroi attualmente in vita tramite Stream.
     *
     * @return lista di eroi con punti vita superiori a zero
     */
    public List<Eroe> getMembriVivi() {
        return membri.stream()
                .filter(Eroe::isVivo)
                .toList();
    }

    /**
     * Verifica se almeno un componente della squadra è ancora in vita.
     *
     * @return true se c'è almeno un sopravvissuto, false altrimenti
     */
    public boolean isAlmenoUnoVivo() {
        return membri.stream().anyMatch(Eroe::isVivo);
    }

    /**
     * Restituisce la dimensione attuale della squadra.
     *
     * @return numero di membri presenti
     */
    public int getNumeroMembri() {
        return membri.size();
    }

    @Override
    public String toString() {
        return String.format("Squadra '%s' (%d membri)", nomeSquadra, membri.size());
    }
}