package it.unicam.cs.mpgc.rpg089027.persistenza;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import it.unicam.cs.mpgc.rpg089027.modelli.Eroe;
import it.unicam.cs.mpgc.rpg089027.modelli.Squadra;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Implementazione concreta di {@link GestoreSalvataggi} che serializza e deserializza
 * lo stato della {@link Squadra} in formato JSON mediante la libreria Google Gson.
 * <p>
 * Impiega il pattern try-with-resources, codifica UTF-8 garantita e
 * registrazione dell'adattatore polimorfico per la gerarchia di eroi.
 */
public class GestoreSalvataggiJson implements GestoreSalvataggi {

    private final Gson gson;

    /**
     * Inizializza il gestore configurando l'istanza Gson con l'adattatore
     * per la gerarchia di eroi e la formattazione leggibile (pretty printing).
     */
    public GestoreSalvataggiJson() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Eroe.class, new EroeTypeAdapter())
                .setPrettyPrinting()
                .create();
    }

    @Override
    public void salvaSquadra(Squadra squadra, Path percorso) throws IOException {
        if (squadra == null) {
            throw new IllegalArgumentException("La squadra da salvare non può essere nulla.");
        }
        if (percorso == null) {
            throw new IllegalArgumentException("Il percorso del file di destinazione non può essere nullo.");
        }

        // Crea le cartelle genitrici se non esistono
        Path parent = percorso.getParent();
        if (parent != null && !Files.exists(parent)) {
            Files.createDirectories(parent);
        }

        // Try-with-resources per chiusura automatica del writer
        try (BufferedWriter writer = Files.newBufferedWriter(percorso, StandardCharsets.UTF_8)) {
            gson.toJson(squadra, writer);
        } catch (JsonIOException e) {
            throw new IOException("Errore durante la serializzazione JSON della squadra.", e);
        }
    }

    @Override
    public Squadra caricaSquadra(Path percorso) throws IOException {
        if (percorso == null) {
            throw new IllegalArgumentException("Il percorso del file sorgente non può essere nullo.");
        }
        if (!Files.exists(percorso)) {
            throw new IOException("Il file di salvataggio specificato non esiste: " + percorso);
        }

        // Try-with-resources per chiusura automatica del reader
        try (BufferedReader reader = Files.newBufferedReader(percorso, StandardCharsets.UTF_8)) {
            Squadra squadra = gson.fromJson(reader, Squadra.class);
            if (squadra == null) {
                throw new IOException("Il file JSON risulta vuoto o non contiene una squadra valida.");
            }
            return squadra;
        } catch (JsonSyntaxException | JsonIOException e) {
            throw new IOException("Formato del file di salvataggio corrotto o non compatibile.", e);
        }
    }
}