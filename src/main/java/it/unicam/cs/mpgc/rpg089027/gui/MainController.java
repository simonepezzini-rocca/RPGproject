package it.unicam.cs.mpgc.rpg089027.gui;

import it.unicam.cs.mpgc.rpg089027.modelli.Eroe;
import it.unicam.cs.mpgc.rpg089027.modelli.Guerriero;
import it.unicam.cs.mpgc.rpg089027.modelli.Mago;
import it.unicam.cs.mpgc.rpg089027.modelli.Squadra;
import it.unicam.cs.mpgc.rpg089027.persistenza.GestoreSalvataggiJson;
import it.unicam.cs.mpgc.rpg089027.servizi.Partita;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;

/**
 * Controller principale per l'interfaccia grafica JavaFX (MainView.fxml).
 * Gestisce la visualizzazione della squadra, l'aggiornamento reattivo della tabella
 * e l'invocazione delle operazioni coordinate dal Facade Partita.
 */
public class MainController {

    @FXML private Label lblNomeSquadra;
    @FXML private TableView<Eroe> tabellaEroi;
    @FXML private TableColumn<Eroe, String> colNome;
    @FXML private TableColumn<Eroe, String> colClasse;
    @FXML private TableColumn<Eroe, String> colHp;
    @FXML private TableColumn<Eroe, String> colStato;

    @FXML private Label lblNomeNemico;
    @FXML private ProgressBar barSaluteNemico;
    @FXML private Label lblHpNemico;

    @FXML private Button btnGeneraNemico;
    @FXML private Button btnAttacca;
    @FXML private Button btnAbilitaSpeciale;
    @FXML private Button btnRisolviScontro;

    @FXML private TextArea txtBattleLog;
    @FXML private Label lblBarraStato;

    private Partita partita;
    private final ObservableList<Eroe> listaEroiOsservabile = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        Squadra squadraIniziale = new Squadra("Compagnia di Camerino");
        squadraIniziale.aggiungiMembro(new Guerriero("Aragorn", 18));
        squadraIniziale.aggiungiMembro(new Mago("Gandalf", 20, 50));

        partita = new Partita(squadraIniziale, new GestoreSalvataggiJson());

        configuraTabella();
        aggiornaVistaSquadra();
        impostaMessaggioStato("Applicazione pronta. Squadra iniziale caricata.");
    }

    private void configuraTabella() {
        colNome.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getNome()));

        colClasse.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getClass().getSimpleName()));

        colHp.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getPuntiVita() + " / " + cellData.getValue().getPuntiVitaMassimi()));

        colStato.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().isVivo() ? "Vivo" : "Esausto"));

        tabellaEroi.setItems(listaEroiOsservabile);
    }

    public void aggiornaVistaSquadra() {
        lblNomeSquadra.setText(partita.getSquadra().getNomeSquadra());
        listaEroiOsservabile.setAll(partita.getSquadra().getMembri());
        tabellaEroi.refresh();
    }

    public void impostaMessaggioStato(String messaggio) {
        lblBarraStato.setText(messaggio);
    }

    @FXML
    private void handleNuovaSquadra(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog("Nuova Alleanza");
        dialog.setTitle("Nuova Squadra");
        dialog.setHeaderText("Crea una nuova squadra");
        dialog.setContentText("Inserisci il nome della squadra:");

        dialog.showAndWait().ifPresent(nome -> {
            if (!nome.trim().isEmpty()) {
                partita.getSquadra().setNomeSquadra(nome.trim());
                aggiornaVistaSquadra();
                impostaMessaggioStato("Creata nuova squadra: " + nome.trim());
            }
        });
    }

    @FXML
    private void handleCaricaSquadra(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Carica Squadra da file JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON (*.json)", "*.json"));
        File file = fileChooser.showOpenDialog(tabellaEroi.getScene().getWindow());

        if (file != null) {
            try {
                partita.caricaSquadraDaFile(file.toPath());
                aggiornaVistaSquadra();
                impostaMessaggioStato("Squadra caricata con successo da: " + file.getName());
            } catch (IOException e) {
                mostraErrore("Errore di Caricamento", "Impossibile caricare il file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleSalvaSquadra(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva Squadra su file JSON");
        fileChooser.setInitialFileName("squadra_" + partita.getSquadra().getNomeSquadra().toLowerCase().replace(" ", "_") + ".json");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON (*.json)", "*.json"));
        File file = fileChooser.showSaveDialog(tabellaEroi.getScene().getWindow());

        if (file != null) {
            try {
                partita.salvaSquadraSuFile(file.toPath());
                impostaMessaggioStato("Squadra salvata con successo in: " + file.getName());
            } catch (IOException e) {
                mostraErrore("Errore di Salvataggio", "Impossibile salvare il file: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleRimuoviEroe(ActionEvent event) {
        Eroe selezionato = tabellaEroi.getSelectionModel().getSelectedItem();
        if (selezionato == null) {
            mostraAvviso("Nessuna Selezione", "Seleziona prima un eroe dalla tabella da rimuovere.");
            return;
        }

        try {
            partita.getSquadra().rimuoviMembro(selezionato);
            aggiornaVistaSquadra();
            impostaMessaggioStato("Rimosso eroe: " + selezionato.getNome());
        } catch (Exception e) {
            mostraErrore("Errore di Rimozione", e.getMessage());
        }
    }

    @FXML
    private void handleEsci(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    private void handleMostraInfo(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Informazioni sul Progetto");
        alert.setHeaderText("RPG Battle Manager v1.0.0");
        alert.setContentText("Progetto di Metodologie di Programmazione e Guida al Collaudo.\n" +
                "Architettura: JavaFX (MVC), JUnit 5, Gson e Principi SOLID.");
        alert.showAndWait();
    }

    @FXML private void handleArruolaEroe(ActionEvent event) {}
    @FXML private void handleGeneraNemico(ActionEvent event) {}
    @FXML private void handleAttacca(ActionEvent event) {}
    @FXML private void handleAbilitaSpeciale(ActionEvent event) {}
    @FXML private void handleRisolviScontro(ActionEvent event) {}

    public void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    public void mostraAvviso(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }

    public Partita getPartita() {
        return partita;
    }
}