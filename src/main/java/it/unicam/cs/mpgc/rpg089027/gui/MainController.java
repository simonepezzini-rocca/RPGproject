package it.unicam.cs.mpgc.rpg089027.gui;

import it.unicam.cs.mpgc.rpg089027.modelli.Combattente;
import it.unicam.cs.mpgc.rpg089027.modelli.Eroe;
import it.unicam.cs.mpgc.rpg089027.modelli.Guerriero;
import it.unicam.cs.mpgc.rpg089027.modelli.Mago;
import it.unicam.cs.mpgc.rpg089027.modelli.Mostro;
import it.unicam.cs.mpgc.rpg089027.modelli.Squadra;
import it.unicam.cs.mpgc.rpg089027.persistenza.GestoreSalvataggiJson;
import it.unicam.cs.mpgc.rpg089027.servizi.GestoreCombattimento;
import it.unicam.cs.mpgc.rpg089027.servizi.Partita;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

/**
 * Controller principale per l'interfaccia grafica JavaFX (MainView.fxml).
 * Gestisce la visualizzazione della squadra, l'aggiornamento reattivo della tabella,
 * l'arruolamento dei personaggi e l'arena di combattimento a turni con log in tempo reale.
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
    private GestoreCombattimento combattimentoAttivo;
    private Combattente nemicoAttuale;
    private int hpInizialiNemico;
    private int indiceUltimoLogLetto = 0;

    private final ObservableList<Eroe> listaEroiOsservabile = FXCollections.observableArrayList();
    private final Random random = new Random();

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
    private void handleArruolaEroe(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/viste/NuovoEroeDialog.fxml"));
            VBox dialogRoot = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Arruola Nuovo Eroe");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(tabellaEroi.getScene().getWindow());
            dialogStage.setScene(new Scene(dialogRoot));

            NuovoEroeController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            if (controller.isConfermato() && controller.getEroeCreato() != null) {
                partita.getSquadra().aggiungiMembro(controller.getEroeCreato());
                aggiornaVistaSquadra();
                impostaMessaggioStato("Arruolato nuovo eroe: " + controller.getEroeCreato().getNome());
            }
        } catch (IOException e) {
            mostraErrore("Errore Interfaccia", "Impossibile aprire il dialogo: " + e.getMessage());
        } catch (Exception e) {
            mostraErrore("Errore di Dominio", e.getMessage());
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
    private void handleGeneraNemico(ActionEvent event) {
        if (!partita.getSquadra().isAlmenoUnoVivo()) {
            mostraAvviso("Squadra Esausta", "Tutti gli eroi sono esausti. Rianimali o crea una nuova squadra.");
            return;
        }

        String[] nomiMostri = {"Orco delle Montagne", "Drago Antico", "Goblin Sciamano", "Golem di Pietra"};
        String nomeScelto = nomiMostri[random.nextInt(nomiMostri.length)];
        hpInizialiNemico = 70 + random.nextInt(50);
        int potenzaAttacco = 12 + random.nextInt(8);

        nemicoAttuale = new Mostro(nomeScelto, hpInizialiNemico, potenzaAttacco);
        combattimentoAttivo = partita.iniziaCombattimento(nemicoAttuale);
        indiceUltimoLogLetto = 0;

        lblNomeNemico.setText(nemicoAttuale.getNome());
        aggiornaBarraNemico();

        btnAttacca.setDisable(false);
        btnAbilitaSpeciale.setDisable(false);
        btnRisolviScontro.setDisable(false);

        sincronizzaBattleLog();
        impostaMessaggioStato("Scontro avviato contro: " + nemicoAttuale.getNome());
    }

    @FXML
    private void handleAttacca(ActionEvent event) {
        eseguiTurnoCombattimento();
    }

    @FXML
    private void handleAbilitaSpeciale(ActionEvent event) {
        eseguiTurnoCombattimento();
    }

    private void eseguiTurnoCombattimento() {
        if (combattimentoAttivo == null || combattimentoAttivo.isCombattimentoTerminato()) {
            return;
        }

        combattimentoAttivo.eseguiTurno();

        aggiornaBarraNemico();
        aggiornaVistaSquadra();
        sincronizzaBattleLog();

        if (combattimentoAttivo.isCombattimentoTerminato()) {
            gestisciFineCombattimento();
        }
    }

    @FXML
    private void handleRisolviScontro(ActionEvent event) {
        if (combattimentoAttivo == null || combattimentoAttivo.isCombattimentoTerminato()) {
            return;
        }

        partita.concludiCombattimentoCorrente();

        aggiornaBarraNemico();
        aggiornaVistaSquadra();
        sincronizzaBattleLog();
        gestisciFineCombattimento();
    }

    private void gestisciFineCombattimento() {
        disabilitaControlliScontro();
        if (combattimentoAttivo.isVittoriaSquadra()) {
            impostaMessaggioStato("Scontro vinto! Vittorie totali registrate: " + partita.getVittorieTotali());
        } else {
            impostaMessaggioStato("Sconfitta: tutti gli eroi della squadra sono caduti.");
        }
    }

    private void sincronizzaBattleLog() {
        if (combattimentoAttivo == null) {
            return;
        }
        List<String> logCompleto = combattimentoAttivo.getRegistroBattaglia();
        while (indiceUltimoLogLetto < logCompleto.size()) {
            txtBattleLog.appendText(logCompleto.get(indiceUltimoLogLetto) + "\n");
            indiceUltimoLogLetto++;
        }
    }

    private void aggiornaBarraNemico() {
        if (nemicoAttuale != null && hpInizialiNemico > 0) {
            int hp = Math.max(0, nemicoAttuale.getPuntiVita());
            double ratio = (double) hp / hpInizialiNemico;
            barSaluteNemico.setProgress(ratio);
            lblHpNemico.setText(hp + " / " + hpInizialiNemico);
        } else {
            barSaluteNemico.setProgress(0.0);
            lblHpNemico.setText("0 / 0");
        }
    }

    private void disabilitaControlliScontro() {
        btnAttacca.setDisable(true);
        btnAbilitaSpeciale.setDisable(true);
        btnRisolviScontro.setDisable(true);
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