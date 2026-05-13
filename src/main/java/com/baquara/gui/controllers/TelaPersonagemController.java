package com.baquara.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class TelaPersonagemController {

    @FXML private Button btnPaladino;
    @FXML private Button btnGuerreiro;
    @FXML private Button btnCacadora;
    @FXML private Button btnSabio;
    @FXML private Button btnArcanista;
    @FXML private Button btnVoltar;

    private String nomeJogador;

    public void setNomeJogador(String nome) {
        this.nomeJogador = nome;
        System.out.println("Jogador: " + nomeJogador);
    }

    @FXML
    public void initialize() {
        btnPaladino.setOnAction(e -> escolherPersonagem("PALADINO"));
        btnGuerreiro.setOnAction(e -> escolherPersonagem("GUERREIRO"));
        btnCacadora.setOnAction(e -> escolherPersonagem("CACADORA"));
        btnSabio.setOnAction(e -> escolherPersonagem("SABIO"));
        btnArcanista.setOnAction(e -> escolherPersonagem("ARCANISTA"));
        btnVoltar.setOnAction(e -> voltar());
    }

    private void escolherPersonagem(String personagem) {
        System.out.println("Personagem escolhido: " + personagem);
        System.out.println("Nome do jogador: " + nomeJogador);

        // TODO: Próximo passo - abrir tela de batalha
        // Por enquanto, mostra mensagem no console
    }

    private void voltar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-inicial.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}