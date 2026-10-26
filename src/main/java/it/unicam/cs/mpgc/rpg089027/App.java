package it.unicam.cs.mpgc.rpg089027;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Entry point dell'applicazione grafica sviluppata con JavaFX.
 * Inizializza lo stage primario e carica la vista FXML principale.
 */
public class App extends Application {

    private static final String VISTA_PRINCIPALE = "/viste/MainView.fxml";
    private static final String TITOLO_FINESTRA = "RPG Battle Manager - Simulatore di Scontri a Turni";

    @Override
    public void start(Stage primaryStage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(VISTA_PRINCIPALE));
        Parent root = loader.load();

        Scene scene = new Scene(root, 950, 650);
        primaryStage.setTitle(TITOLO_FINESTRA);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(550);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}