package com.baquara.gui.controllers;

import com.baquara.controle.GerenciadorRodaCapoeira;
import com.baquara.modelo.Jogador;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class TelaCapoeiraController {

    @FXML private Label lblJogadorNome;
    @FXML private Label lblJogadorVida;
    @FXML private ProgressBar barraJogadorVida;
    @FXML private Label lblGinga;

    @FXML private Label lblInimigoNome;
    @FXML private Label lblInimigoVida;
    @FXML private ProgressBar barraInimigoVida;

    @FXML private TextArea txtDialogo;
    @FXML private Button btnBasico;
    @FXML private Button btnDificil;
    @FXML private Button btnCombinado;
    @FXML private Button btnEsquiva;

    private Jogador jogador;
    private GerenciadorRodaCapoeira gerenciador;

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
        this.gerenciador = new GerenciadorRodaCapoeira(jogador);
        atualizarStatus();
    }

    public void iniciarRoda() {
        // Aqui você pode chamar o gerenciador em uma thread separada
        // ou adaptar o GerenciadorRodaCapoeira para usar GUI em vez de console
        txtDialogo.appendText("🥋 A RODA PROIBIDA COMEÇOU! 🥋\n");
        txtDialogo.appendText("Use os botões abaixo para escolher seus ataques!\n");
    }

    @FXML
    public void initialize() {
        btnBasico.setOnAction(e -> atacar(1));
        btnDificil.setOnAction(e -> atacar(2));
        btnCombinado.setOnAction(e -> atacar(3));
        btnEsquiva.setOnAction(e -> esquivar());
    }

    private void atacar(int tipo) {
        txtDialogo.appendText("⚔️ Ataque " + (tipo == 1 ? "BÁSICO" : tipo == 2 ? "DIFÍCIL" : "COMBINADO") + "!\n");
        // Integrar com a lógica do gerenciador
    }

    private void esquivar() {
        txtDialogo.appendText("🌀 ESQUIVA!\n");
    }

    private void atualizarStatus() {
        // Atualizar UI com os dados do jogador e inimigo atual
    }
}
