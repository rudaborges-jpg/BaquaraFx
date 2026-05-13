package com.baquara.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class TelaInicialController {

    @FXML private TextField txtNome;
    @FXML private Button btnJogar;
    @FXML private Button btnSair;

    @FXML
    public void initialize() {
        btnJogar.setOnAction(e -> iniciarJogo());
        btnSair.setOnAction(e -> sair());
        txtNome.setPromptText("Digite seu nome");
    }

    private void iniciarJogo() {
        String nome = txtNome.getText().trim();

        if (nome.isEmpty()) {
            txtNome.setStyle("-fx-border-color: red; -fx-border-width: 2px;");
            txtNome.setPromptText("⚠️ Digite seu nome!");
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

    private void sair() {
        Stage stage = (Stage) btnSair.getScene().getWindow();
        stage.close();
    }
}