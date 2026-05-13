// src/main/java/com/baquara/BaquaraApplication.java

package com.baquara;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class BaquaraApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Carrega a tela inicial (vamos criar em breve)
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-inicial.fxml"));
        Parent root = loader.load();

        primaryStage.setTitle("Baquara - Batalha do Saber");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}