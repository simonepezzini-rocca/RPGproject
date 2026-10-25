package it.unicam.cs.mpgc.rpg089027.persistenza;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Fornisce costanti e metodi di utilità per la risoluzione e la gestione
 * dei percorsi del file system dedicati ai salvataggi della squadra.
 * <p>
 * Centralizza la cartella di memorizzazione e l'estensione dei file JSON
 * per evitare percorsi cablati all'interno della logica applicativa o della GUI.
 */
public final class ConfigurazioneSalvataggi {

    /**
     * Cartella predefinita per l'archiviazione dei salvataggi di gioco.
     */
    public static final String CARTELLA_SALVATAGGI_PREDEFINITA = "saves";

    /**
     * Estensione standard per i file di salvataggio in formato JSON.
     */
    public static final String ESTENSIONE_FILE_SALVATAGGIO = ".json";

    /**
     * Nome predefinito per il file di salvataggio automatico.
     */
    public static final String NOME_SALVATAGGIO_RAPIDO = "quick_save" + ESTENSIONE_FILE_SALVATAGGIO;

    // Costruttore privato per impedire l'istanziazione di una classe di sole utilità statiche
    private ConfigurazioneSalvataggi() {
        throw new AssertionError("La classe ConfigurazioneSalvataggi non può essere istanziata.");
    }

    /**
     * Restituisce il percorso della cartella predefinita dei salvataggi,
     * creandola sul disco se non risulta già presente.
     *
     * @return il percorso della cartella di salvataggio
     * @throws IOException se si verifica un errore durante la creazione della cartella
     */
    public static Path ottieniCartellaSalvataggi() throws IOException {
        Path cartella = Paths.get(CARTELLA_SALVATAGGI_PREDEFINITA);
        if (!Files.exists(cartella)) {
            Files.createDirectories(cartella);
        }
        return cartella;
    }

    /**
     * Risolve un percorso completo per un salvataggio dato il nome desiderato,
     * assicurando che l'estensione finale sia corretta.
     *
     * @param nomeFile il nome desiderato per il salvataggio
     * @return il percorso normalizzato all'interno della cartella dei salvataggi
     * @throws IOException se non è possibile accedere o creare la cartella
     */
    public static Path risolviPercorsoSalvataggio(String nomeFile) throws IOException {
        if (nomeFile == null || nomeFile.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del file di salvataggio non può essere vuoto o nullo.");
        }

        String nomeSanificato = nomeFile.trim();
        if (!nomeSanificato.toLowerCase().endsWith(ESTENSIONE_FILE_SALVATAGGIO)) {
            nomeSanificato += ESTENSIONE_FILE_SALVATAGGIO;
        }

        return ottieniCartellaSalvataggi().resolve(nomeSanificato);
    }
}
