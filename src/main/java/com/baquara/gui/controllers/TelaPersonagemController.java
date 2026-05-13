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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TelaPersonagemController {

    @FXML private Button btnPaladino;
    @FXML private Button btnGuerreiro;
    @FXML private Button btnCacadora;
    @FXML private Button btnSabio;
    @FXML private Button btnArcanista;
    @FXML private Button btnRotaSecreta;
    @FXML private Button btnVoltar;

    @FXML private GridPane gridNormais;
    @FXML private VBox painelSecreto;
    @FXML private Label lblSubtitulo;

    private String nomeJogador;
    private boolean personagemEscolhido = false;

    public void setNomeJogador(String nome) {
        this.nomeJogador = nome;
        System.out.println("Jogador: " + nomeJogador);

        // ⭐ SE FOR "Rudá", ESCONDE AS PORTAS NORMAIS E MOSTRA SÓ A SECRETA
        if ("Rudá".equalsIgnoreCase(nome.trim())) {
            mostrarApenasPortaSecreta();
            System.out.println("🔮 ROTA SECRETA DESBLOQUEADA! 🔮");
            System.out.println("🥋 A PORTA DA CAPOEIRA APARECEU! 🥋");
        }
    }

    private void mostrarApenasPortaSecreta() {
        Platform.runLater(() -> {
            // ESCONDE as portas normais
            gridNormais.setVisible(false);
            gridNormais.setManaged(false);

            // MUDA o subtítulo
            lblSubtitulo.setText("✨ Uma porta misteriosa apareceu para você, " + nomeJogador + "! ✨");
            lblSubtitulo.setStyle("-fx-text-fill: #9C27B0; -fx-font-size: 18px; -fx-font-weight: bold;");

            // MOSTRA o painel secreto
            painelSecreto.setVisible(true);
            painelSecreto.setManaged(true);

            // Efeito de brilho pulsante
            new Thread(() -> {
                try {
                    for (int i = 0; i < 8; i++) {
                        Thread.sleep(400);
                        Platform.runLater(() -> {
                            if (btnRotaSecreta.isVisible()) {
                                btnRotaSecreta.setStyle("-fx-effect: dropshadow(gaussian, #FFD700, 30, 0.7, 0, 0);");
                            }
                        });
                        Thread.sleep(400);
                        Platform.runLater(() -> {
                            if (btnRotaSecreta.isVisible()) {
                                btnRotaSecreta.setStyle("-fx-effect: dropshadow(gaussian, #9C27B0, 30, 0.7, 0, 0);");
                            }
                        });
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }).start();
        });
    }

    @FXML
    public void initialize() {
        btnPaladino.setOnAction(e -> escolherPersonagem(new Paladino()));
        btnGuerreiro.setOnAction(e -> escolherPersonagem(new Guerreiro()));
        btnCacadora.setOnAction(e -> escolherPersonagem(new Cacadora()));
        btnSabio.setOnAction(e -> escolherPersonagem(new Sabio()));
        btnArcanista.setOnAction(e -> escolherPersonagem(new Arcanista()));
        btnRotaSecreta.setOnAction(e -> iniciarRotaSecreta());
        btnVoltar.setOnAction(e -> voltar());
    }

    private void escolherPersonagem(Personagem personagem) {
        if (personagemEscolhido) return;
        personagemEscolhido = true;

        System.out.println("Personagem escolhido: " + personagem.getNome());

        Jogador jogador = new Jogador(nomeJogador);
        jogador.escolherPersonagem(personagem);

        iniciarBatalhaNormal(jogador);
    }

    private void iniciarRotaSecreta() {
        if (personagemEscolhido) return;
        personagemEscolhido = true;

        System.out.println("🔮 INICIANDO ROTA SECRETA - CAPOEIRA! 🔮");
        System.out.println("🥋 Bem-vindo à Roda Proibida, " + nomeJogador + "!");

        // Cria um Capoeirista para a rota secreta
        Capoeirista capoeirista = new Capoeirista();

        Jogador jogador = new Jogador(nomeJogador);
        jogador.escolherPersonagem(capoeirista);

        iniciarRodaCapoeira(jogador);
    }

    private void iniciarRodaCapoeira(Jogador jogador) {
        try {
            System.out.println("Carregando tela de capoeira...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-capoeira.fxml"));
            Parent root = loader.load();

            TelaCapoeiraController controller = loader.getController();
            controller.setJogador(jogador);

            Stage stage = (Stage) btnRotaSecreta.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Baquara - Roda de Capoeira");
            stage.setResizable(false);

            controller.iniciarRoda();

            System.out.println("Tela de capoeira carregada com sucesso!");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao carregar tela de capoeira: " + e.getMessage());
            personagemEscolhido = false;

            // Fallback: console
            System.out.println("\n⚠️ Erro na interface gráfica. Usando modo texto...\n");
            GerenciadorRodaCapoeira gerenciador = new GerenciadorRodaCapoeira(jogador);
            gerenciador.iniciarRodaProibida();
        }
    }

    private void iniciarBatalhaNormal(Jogador jogador) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-batalha.fxml"));
            Parent root = loader.load();

            TelaBatalhaController controller = loader.getController();
            controller.setJogador(jogador);

            Stage stage = (Stage) btnPaladino.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Baquara - Batalha");
            stage.setResizable(false);

            controller.iniciarBatalha();

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
            stage.setTitle("Baquara - Batalha do Saber");
            stage.setResizable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}