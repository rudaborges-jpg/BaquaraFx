package com.baquara.gui.controllers;

import com.baquara.controle.RankingManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

public class TelaInicialController {

    @FXML private TextField txtNome;
    @FXML private Button btnJogar;
    @FXML private Button btnRanking;  // ⭐ NOVO
    @FXML private Button btnSair;

    @FXML
    public void initialize() {
        btnJogar.setOnAction(e -> iniciarJogo());
        btnRanking.setOnAction(e -> mostrarRanking());  // ⭐ NOVO
        btnSair.setOnAction(e -> sair());
        txtNome.setPromptText("Digite seu nome");

        // CONFIGURA O ENTER PARA INICIAR O JOGO
        txtNome.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                iniciarJogo();
            }
        });

        // Faz o txtNome ganhar foco automaticamente ao abrir a tela
        txtNome.requestFocus();
    }

    private void iniciarJogo() {
        String nome = txtNome.getText().trim();

        if (nome.isEmpty()) {
            txtNome.setStyle("-fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 5;");
            txtNome.setPromptText("⚠️ Digite seu nome!");

            // Pisca o campo para chamar atenção
            new Thread(() -> {
                try {
                    Thread.sleep(200);
                    javafx.application.Platform.runLater(() -> {
                        txtNome.setStyle("");
                        txtNome.setPromptText("Digite seu nome");
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
            return;
        }

        txtNome.setStyle("");

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-personagem.fxml"));
            Parent root = loader.load();

            TelaPersonagemController controller = loader.getController();
            controller.setNomeJogador(nome);

            Stage stage = (Stage) btnJogar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Baquara - Escolha seu Personagem");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar tela de personagem: " + e.getMessage());
        }
    }

    // ⭐ NOVO MÉTODO PARA MOSTRAR O RANKING ⭐
    private void mostrarRanking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-ranking.fxml"));
            Parent root = loader.load();

            TelaRankingController controller = loader.getController();
            controller.setRankingManager(new RankingManager());

            Stage stage = (Stage) btnRanking.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Baquara - Ranking de Jogadores");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar tela de ranking: " + e.getMessage());
        }
    }

    private void sair() {
        Stage stage = (Stage) btnSair.getScene().getWindow();
        stage.close();
    }
}