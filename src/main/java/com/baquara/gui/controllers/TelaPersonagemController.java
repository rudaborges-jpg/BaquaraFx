package com.baquara.gui.controllers;

import com.baquara.controle.GerenciadorBatalha;
import com.baquara.controle.GerenciadorRodaCapoeira;
import com.baquara.dados.BancoPerguntas;
import com.baquara.modelo.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class TelaPersonagemController {

    @FXML private Button btnPaladino;
    @FXML private Button btnGuerreiro;
    @FXML private Button btnCacadora;
    @FXML private Button btnSabio;
    @FXML private Button btnArcanista;
    @FXML private Button btnRotaSecreta;
    @FXML private Button btnVoltar;

    private String nomeJogador;
    private boolean personagemEscolhido = false;
    private boolean rotaSecretaAtivada = false;

    public void setNomeJogador(String nome) {
        this.nomeJogador = nome;
        System.out.println("Jogador: " + nomeJogador);

        // ⭐ VERIFICAÇÃO DA ROTA SECRETA
        if ("Rudá".equalsIgnoreCase(nome.trim())) {
            rotaSecretaAtivada = true;
            mostrarPortaSecreta();
            System.out.println("🔮 ROTA SECRETA DESBLOQUEADA! 🔮");
        }
    }

    private void mostrarPortaSecreta() {
        Platform.runLater(() -> {
            btnRotaSecreta.setVisible(true);
            btnRotaSecreta.setManaged(true);

            // Efeito de brilho na porta secreta
            btnRotaSecreta.setStyle("-fx-effect: dropshadow(gaussian, #9C27B0, 20, 0.5, 0, 0);");

            // Tooltip explicativo
            javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(
                    "⚡ ROTA SECRETA DESBLOQUEADA! ⚡\n" +
                            "Você foi escolhido pelo destino!\n" +
                            "Enfrente a Roda de Capoeira e prove seu valor!"
            );
            javafx.scene.control.Tooltip.install(btnRotaSecreta, tooltip);
        });
    }

    @FXML
    public void initialize() {
        btnPaladino.setOnAction(e -> escolherPersonagem(new Paladino(), false));
        btnGuerreiro.setOnAction(e -> escolherPersonagem(new Guerreiro(), false));
        btnCacadora.setOnAction(e -> escolherPersonagem(new Cacadora(), false));
        btnSabio.setOnAction(e -> escolherPersonagem(new Sabio(), false));
        btnArcanista.setOnAction(e -> escolherPersonagem(new Arcanista(), false));
        btnRotaSecreta.setOnAction(e -> iniciarRotaSecreta());
        btnVoltar.setOnAction(e -> voltar());

        // Inicialmente a porta secreta está invisível
        btnRotaSecreta.setVisible(false);
        btnRotaSecreta.setManaged(false);
    }

    private void escolherPersonagem(Personagem personagem, boolean isRotaSecreta) {
        if (personagemEscolhido) return;
        personagemEscolhido = true;

        System.out.println("Personagem escolhido: " + personagem.getNome());

        Jogador jogador = new Jogador(nomeJogador);
        jogador.escolherPersonagem(personagem);

        if (isRotaSecreta) {
            iniciarRodaCapoeira(jogador);
        } else {
            iniciarBatalhaNormal(jogador);
        }
    }

    private void iniciarRotaSecreta() {
        if (personagemEscolhido) return;
        personagemEscolhido = true;

        System.out.println("🔮 INICIANDO ROTA SECRETA - CAPOEIRA! 🔮");

        // Cria um Capoeirista para a rota secreta
        Capoeirista capoeirista = new Capoeirista();

        Jogador jogador = new Jogador(nomeJogador);
        jogador.escolherPersonagem(capoeirista);

        iniciarRodaCapoeira(jogador);
    }

    private void iniciarBatalhaNormal(Jogador jogador) {
        try {
            // Modo normal - batalha tradicional
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-batalha.fxml"));
            Parent root = loader.load();

            TelaBatalhaController controller = loader.getController();
            controller.setJogador(jogador);

            Stage stage = (Stage) btnPaladino.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Baquara - Batalha");

            controller.iniciarBatalha();

        } catch (Exception e) {
            e.printStackTrace();
            personagemEscolhido = false;
        }
    }

    private void iniciarRodaCapoeira(Jogador jogador) {
        try {
            // Modo capoeira - roda proibida
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-capoeira.fxml"));
            Parent root = loader.load();

            TelaCapoeiraController controller = loader.getController();
            controller.setJogador(jogador);

            Stage stage = (Stage) btnRotaSecreta.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Baquara - Roda de Capoeira");

            controller.iniciarRoda();

        } catch (Exception e) {
            e.printStackTrace();
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