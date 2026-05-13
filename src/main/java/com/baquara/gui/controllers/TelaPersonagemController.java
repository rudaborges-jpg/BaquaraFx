package com.baquara.gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import com.baquara.modelo.*;
import com.baquara.dados.BancoPerguntas;

public class TelaPersonagemController {

    @FXML private Button btnPaladino;
    @FXML private Button btnGuerreiro;
    @FXML private Button btnCacadora;
    @FXML private Button btnSabio;
    @FXML private Button btnArcanista;
    @FXML private Button btnVoltar;

    private String nomeJogador;
    private boolean personagemEscolhido = false;

    public void setNomeJogador(String nome) {
        this.nomeJogador = nome;
        System.out.println("Jogador: " + nomeJogador);
    }

    @FXML
    public void initialize() {
        btnPaladino.setOnAction(e -> escolherPersonagem(new Paladino()));
        btnGuerreiro.setOnAction(e -> escolherPersonagem(new Guerreiro()));
        btnCacadora.setOnAction(e -> escolherPersonagem(new Cacadora()));
        btnSabio.setOnAction(e -> escolherPersonagem(new Sabio()));
        btnArcanista.setOnAction(e -> escolherPersonagem(new Arcanista()));
        btnVoltar.setOnAction(e -> voltar());
    }

    private void escolherPersonagem(Personagem personagem) {
        if (personagemEscolhido) return; // Evita múltiplos cliques
        personagemEscolhido = true;

        System.out.println("Personagem escolhido: " + personagem.getNome());

        // Criar o jogador
        Jogador jogador = new Jogador(nomeJogador);
        jogador.escolherPersonagem(personagem);

        try {
            // Carregar a tela de batalha
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-batalha.fxml"));
            Parent root = loader.load();

            // Passar o jogador para a tela de batalha
            TelaBatalhaController controller = loader.getController();
            controller.setJogador(jogador);

            // Trocar a tela
            Stage stage = (Stage) btnPaladino.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Baquara - Batalha");

            // Iniciar a batalha
            controller.iniciarBatalha();

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar tela de batalha: " + e.getMessage());
            personagemEscolhido = false;
        }
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