package it.unicam.cs.mpgc.rpg089027.gui;

import it.unicam.cs.mpgc.rpg089027.modelli.Eroe;
import it.unicam.cs.mpgc.rpg089027.modelli.Guerriero;
import it.unicam.cs.mpgc.rpg089027.modelli.Mago;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controller per la finestra di dialogo modale di creazione di un nuovo eroe.
 * Gestisce l'adattamento dinamico dei campi in base alla classe selezionata
 * e la validazione dei dati immessi prima dell'istanziazione.
 */
public class NuovoEroeController {

    private static final String CLASSE_GUERRIERO = "Guerriero";
    private static final String CLASSE_MAGO = "Mago";

    @FXML private TextField txtNome;
    @FXML private ComboBox<String> cmbClasse;
    @FXML private Label lblAttributo;
    @FXML private Spinner<Integer> spnAttributo;

    private Stage dialogStage;
    private Eroe eroeCreato;
    private boolean confermato = false;

    @FXML
    public void initialize() {
        cmbClasse.setItems(FXCollections.observableArrayList(CLASSE_GUERRIERO, CLASSE_MAGO));
        cmbClasse.setValue(CLASSE_GUERRIERO);

        spnAttributo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 15));
        lblAttributo.setText("Forza:");

        cmbClasse.getSelectionModel().selectedItemProperty().addListener((obs, vecchio, nuovo) -> {
            if (CLASSE_MAGO.equals(nuovo)) {
                lblAttributo.setText("Punti Mana:");
                spnAttributo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 150, 40));
            } else {
                lblAttributo.setText("Forza:");
                spnAttributo.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 15));
            }
        });
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean isConfermato() {
        return confermato;
    }

    public Eroe getEroeCreato() {
        return eroeCreato;
    }

    @FXML
    private void handleConferma(ActionEvent event) {
        String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
        if (nome.isEmpty()) {
            mostraErrore("Dato Mancante", "Il nome dell'eroe non può essere vuoto.");
            return;
        }

        int valoreAttributo = spnAttributo.getValue();
        String classeSelezionata = cmbClasse.getValue();

        try {
            if (CLASSE_MAGO.equals(classeSelezionata)) {
                eroeCreato = new Mago(nome, 16, valoreAttributo);
            } else {
                eroeCreato = new Guerriero(nome, valoreAttributo);
            }
            confermato = true;
            dialogStage.close();
        } catch (IllegalArgumentException e) {
            mostraErrore("Parametro non valido", e.getMessage());
        }
    }

    @FXML
    private void handleAnnulla(ActionEvent event) {
        confermato = false;
        dialogStage.close();
    }

    private void mostraErrore(String titolo, String messaggio) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.initOwner(dialogStage);
        alert.setTitle(titolo);
        alert.setHeaderText(null);
        alert.setContentText(messaggio);
        alert.showAndWait();
    }
}