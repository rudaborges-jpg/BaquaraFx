package com.baquara.gui.controllers;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import com.baquara.modelo.*;
import com.baquara.controle.AvaliadorRespostas;
import com.baquara.dados.BancoPerguntas;
import com.baquara.modelo.Pergunta.Dificuldade;
import java.util.List;
import java.util.Random;

public class TelaBatalhaController {

    // Componentes da UI
    @FXML private Label lblJogadorNome;
    @FXML private Label lblJogadorVida;
    @FXML private ProgressBar barraJogadorVida;
    @FXML private Label lblJogadorAtributo;
    @FXML private ImageView imgJogador;

    @FXML private Label lblInimigoNome;
    @FXML private Label lblInimigoVida;
    @FXML private ProgressBar barraInimigoVida;
    @FXML private ImageView imgInimigo;
    @FXML private Label lblInimigoInfo;

    @FXML private Label lblDificuldade;
    @FXML private TextArea txtPergunta;
    @FXML private TextArea txtDialogo;

    @FXML private VBox painelAlternativas;
    @FXML private HBox painelLacuna;
    @FXML private TextField txtRespostaLacuna;
    @FXML private Button btnEnviarLacuna;

    @FXML private Button btnHabilidade;
    @FXML private Label lblCooldown;
    @FXML private Label lblTurno;

    // Dados do jogo
    private Jogador jogador;
    private Inimigo inimigoAtual;
    private BancoPerguntas bancoPerguntas;
    private Pergunta perguntaAtual;
    private int estagioAtual = 1;
    private int pontuacao = 0;
    private Random random = new Random();
    private boolean aguardandoResposta = false;
    private Timeline temporizador;
    private int tempoRestante = 20;

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
        carregarSpriteJogador();
        atualizarStatusJogador();
    }

    private void carregarSpriteJogador() {
        String tipo = jogador.getPersonagem().getTipo().getNome().toLowerCase();
        String caminho = "/images/pokemons/" + tipo + ".png";

        try {
            Image img = new Image(getClass().getResourceAsStream(caminho));
            if (img != null && !img.isError()) {
                imgJogador.setImage(img);
            } else {
                imgJogador.setImage(criarSpritePlaceholder(tipo));
            }
        } catch (Exception e) {
            imgJogador.setImage(criarSpritePlaceholder(tipo));
        }
    }

    private Image criarSpritePlaceholder(String tipo) {
        // Placeholder colorido caso não tenha imagem
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(100, 100);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();

        switch (tipo) {
            case "paladino": gc.setFill(javafx.scene.paint.Color.GOLD); break;
            case "guerreiro": gc.setFill(javafx.scene.paint.Color.RED); break;
            case "cacadora": gc.setFill(javafx.scene.paint.Color.GREEN); break;
            case "sabio": gc.setFill(javafx.scene.paint.Color.BLUE); break;
            default: gc.setFill(javafx.scene.paint.Color.PURPLE);
        }
        gc.fillOval(20, 20, 60, 60);
        gc.setFill(javafx.scene.paint.Color.BLACK);
        gc.fillOval(40, 40, 8, 8);
        gc.fillOval(55, 40, 8, 8);

        javafx.scene.image.WritableImage image = canvas.snapshot(null, null);
        return image;
    }

    @FXML
    public void initialize() {
        bancoPerguntas = new BancoPerguntas();
        btnHabilidade.setOnAction(e -> usarHabilidade());

        // Estilo dos botões de resposta
        painelAlternativas.setStyle("-fx-background-color: transparent;");
    }

    public void iniciarBatalha() {
        animarEntrada();
        criarProximoInimigo();
        proximaPergunta();
    }

    private void animarEntrada() {
        // Animação fade-in do jogador
        FadeTransition fade = new FadeTransition(Duration.seconds(0.5), imgJogador);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        adicionarDialogo("✨ " + jogador.getPersonagem().getNome() + " entrou na arena!");
        adicionarDialogo("⚔️ Prepare-se para a batalha do saber!");
    }

    private void criarProximoInimigo() {
        int vidaBase = 80 + (estagioAtual * 25);
        int ataqueBase = 20 + (estagioAtual * 6);
        String[] nomesInimigos = {
                "Guardião da Floresta", "Guardião das Chamas", "Guardião dos Mares",
                "Guardião dos Ventos", "Guardião das Trevas", "Guardião da Luz"
        };
        String nome = nomesInimigos[random.nextInt(nomesInimigos.length)] + " Nv." + estagioAtual;

        inimigoAtual = new Inimigo(nome, vidaBase, ataqueBase, estagioAtual);
        carregarSpriteInimigo();
        atualizarStatusInimigo();

        // Animação de entrada do inimigo
        TranslateTransition entrada = new TranslateTransition(Duration.seconds(0.5), imgInimigo);
        entrada.setFromX(200);
        entrada.setToX(0);
        entrada.play();

        adicionarDialogo("⚠️ " + inimigoAtual.getNome() + " apareceu!");
    }

    private void carregarSpriteInimigo() {
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/pokemons/inimigo" + (estagioAtual % 5 + 1) + ".png"));
            if (img != null && !img.isError()) {
                imgInimigo.setImage(img);
            } else {
                criarSpriteInimigoPlaceholder();
            }
        } catch (Exception e) {
            criarSpriteInimigoPlaceholder();
        }
    }

    private void criarSpriteInimigoPlaceholder() {
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(120, 120);
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(javafx.scene.paint.Color.rgb(100, 50, 50));
        gc.fillOval(20, 20, 80, 80);
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillOval(45, 45, 10, 10);
        gc.fillOval(65, 45, 10, 10);
        imgInimigo.setImage(canvas.snapshot(null, null));
    }

    private void proximaPergunta() {
        aguardandoResposta = true;
        lblTurno.setText("⚔️ SEU TURNO ⚔️");

        Dificuldade dificuldade;
        if (estagioAtual <= 3) {
            dificuldade = Dificuldade.FACIL;
            lblDificuldade.setText("⭐ Dificuldade: FÁCIL");
        } else if (estagioAtual <= 7) {
            dificuldade = Dificuldade.MEDIO;
            lblDificuldade.setText("⭐⭐ Dificuldade: MÉDIO");
        } else {
            dificuldade = Dificuldade.DIFICIL;
            lblDificuldade.setText("⭐⭐⭐ Dificuldade: DIFÍCIL");
        }

        perguntaAtual = bancoPerguntas.getPerguntaAleatoriaPorDificuldade(
                jogador.getPersonagem().getTipo(), dificuldade, estagioAtual
        );

        if (perguntaAtual == null) {
            txtPergunta.setText("Nenhuma pergunta disponível!");
            return;
        }

        txtPergunta.setText(perguntaAtual.getTexto());
        exibirAlternativas();
        iniciarTemporizador();
    }

    private void exibirAlternativas() {
        painelAlternativas.getChildren().clear();
        painelLacuna.setVisible(false);

        if (perguntaAtual instanceof PerguntaMultiplaEscolha) {
            PerguntaMultiplaEscolha p = (PerguntaMultiplaEscolha) perguntaAtual;
            List<String> opcoes = p.getOpcoes();

            for (int i = 0; i < opcoes.size(); i++) {
                char letra = (char) ('A' + i);
                Button btn = criarBotaoAlternativa(letra + ") " + opcoes.get(i), String.valueOf(letra));
                painelAlternativas.getChildren().add(btn);
            }
            painelAlternativas.setVisible(true);

        } else if (perguntaAtual instanceof PerguntaVerdadeiroFalso) {
            painelAlternativas.getChildren().add(criarBotaoAlternativa("V) VERDADEIRO", "V"));
            painelAlternativas.getChildren().add(criarBotaoAlternativa("F) FALSO", "F"));
            painelAlternativas.setVisible(true);

        } else if (perguntaAtual instanceof PerguntaCompletarLacuna) {
            painelAlternativas.setVisible(false);
            painelLacuna.setVisible(true);
            txtRespostaLacuna.clear();
            btnEnviarLacuna.setOnAction(e -> {
                String resposta = txtRespostaLacuna.getText().trim();
                if (!resposta.isEmpty() && aguardandoResposta) {
                    avaliarResposta(resposta);
                }
            });
        }
    }

    private Button criarBotaoAlternativa(String texto, String valorResposta) {
        Button btn = new Button(texto);
        btn.setStyle("-fx-background-color: #2a2a4a; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8; -fx-background-radius: 8;");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            if (aguardandoResposta) {
                if (temporizador != null) temporizador.stop();
                avaliarResposta(valorResposta);
            }
        });

        // Hover effect
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #4a4a6a; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8; -fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: #2a2a4a; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8; -fx-background-radius: 8;"));

        return btn;
    }

    private void iniciarTemporizador() {
        tempoRestante = 20;
        if (temporizador != null) temporizador.stop();

        temporizador = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            tempoRestante--;
            lblTurno.setText("⏱️ " + tempoRestante + "s restantes");

            if (tempoRestante <= 0) {
                temporizador.stop();
                if (aguardandoResposta) {
                    aguardandoResposta = false;
                    adicionarDialogo("⏰ TEMPO ESGOTADO!");
                    adicionarDialogo("📖 Resposta: " + perguntaAtual.getRespostaCorreta());
                    aplicarPenalidadeTempo();
                    proximaPergunta();
                }
            }
        }));
        temporizador.setCycleCount(20);
        temporizador.play();
    }

    private void aplicarPenalidadeTempo() {
        int dano = 10 + (estagioAtual * 2);
        animarDanoJogador();
        jogador.tomarDano(dano);
        atualizarStatusJogador();
        adicionarDialogo("💔 " + inimigoAtual.getNome() + " causou " + dano + " de dano!");

        if (!jogador.vivo()) {
            finalizarJogo(false);
        }
    }

    private void avaliarResposta(String resposta) {
        aguardandoResposta = false;
        if (temporizador != null) temporizador.stop();

        boolean correta = AvaliadorRespostas.avaliar(perguntaAtual, resposta);

        if (correta) {
            processarAcerto();
        } else {
            processarErro();
        }
    }

    private void processarAcerto() {
        int dano = calcularDano();
        adicionarDialogo("✅ CORRETO!");
        adicionarDialogo("💥 Causou " + dano + " de dano em " + inimigoAtual.getNome() + "!");

        animarAtaque();

        new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                inimigoAtual.tomarDano(dano);
                atualizarStatusInimigo();
                pontuacao += 100;

                if (!inimigoAtual.vivo()) {
                    processarVitoria();
                } else {
                    proximaPergunta();
                }
            });
        }).start();
    }

    private void processarErro() {
        int dano = 15 + (estagioAtual * 2);
        adicionarDialogo("❌ ERRADO!");
        adicionarDialogo("📖 Resposta correta: " + perguntaAtual.getRespostaCorreta());
        adicionarDialogo("💔 " + inimigoAtual.getNome() + " contra-ataca causando " + dano + " de dano!");

        animarDanoJogador();

        new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                jogador.tomarDano(dano);
                atualizarStatusJogador();

                if (!jogador.vivo()) {
                    finalizarJogo(false);
                } else {
                    proximaPergunta();
                }
            });
        }).start();
    }

    private void processarVitoria() {
        adicionarDialogo("🎉 " + inimigoAtual.getNome() + " foi derrotado! 🎉");

        // Animação de explosão/vitória
        ScaleTransition explosao = new ScaleTransition(Duration.seconds(0.3), imgInimigo);
        explosao.setToX(0);
        explosao.setToY(0);
        explosao.play();

        new Thread(() -> {
            try { Thread.sleep(1500); } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                estagioAtual++;

                if (estagioAtual > 10) {
                    finalizarJogo(true);
                } else {
                    adicionarDialogo("✨ Avançando para o Estágio " + estagioAtual + "!");
                    criarProximoInimigo();
                    proximaPergunta();
                }
            });
        }).start();
    }

    private int calcularDano() {
        int base = jogador.getPersonagem().getAtaque();
        int multiplicador = perguntaAtual.getDificuldade().getDanoBase();
        return base * multiplicador + 15 + random.nextInt(20);
    }

    private void usarHabilidade() {
        if (!aguardandoResposta) {
            adicionarDialogo("⏳ Aguarde sua vez para usar habilidade!");
            return;
        }

        Personagem p = jogador.getPersonagem();
        adicionarDialogo("🌟 " + p.getNomeHabilidade() + "!");

        int dano = 60 + (estagioAtual * 8) + random.nextInt(30);
        adicionarDialogo("💥 Causou " + dano + " de dano devastador!");

        btnHabilidade.setDisable(true);
        lblCooldown.setText("⏳ Cooldown: 5");

        animarAtaqueEspecial();

        new Thread(() -> {
            try { Thread.sleep(500); } catch (InterruptedException e) {}
            Platform.runLater(() -> {
                inimigoAtual.tomarDano(dano);
                atualizarStatusInimigo();

                if (!inimigoAtual.vivo()) {
                    processarVitoria();
                } else {
                    // Cooldown
                    new Thread(() -> {
                        for (int i = 5; i > 0; i--) {
                            final int cd = i;
                            Platform.runLater(() -> lblCooldown.setText("⏳ Cooldown: " + cd));
                            try { Thread.sleep(1000); } catch (InterruptedException e) {}
                        }
                        Platform.runLater(() -> {
                            btnHabilidade.setDisable(false);
                            lblCooldown.setText("✨ PRONTA!");
                        });
                    }).start();
                    proximaPergunta();
                }
            });
        }).start();
    }

    private void animarAtaque() {
        // Jogador balança para frente
        TranslateTransition ataque = new TranslateTransition(Duration.seconds(0.2), imgJogador);
        ataque.setByX(-30);
        ataque.setAutoReverse(true);
        ataque.setCycleCount(2);
        ataque.play();

        // Inimigo treme ao levar dano
        TranslateTransition tremer = new TranslateTransition(Duration.millis(50), imgInimigo);
        tremer.setByX(10);
        tremer.setAutoReverse(true);
        tremer.setCycleCount(6);
        tremer.play();
    }

    private void animarAtaqueEspecial() {
        // Efeito especial - inimigo pisca vermelho
        Timeline pisca = new Timeline(
                new KeyFrame(Duration.millis(100), e -> imgInimigo.setStyle("-fx-effect: dropshadow(gaussian, red, 20, 0.8, 0, 0);")),
                new KeyFrame(Duration.millis(200), e -> imgInimigo.setStyle("-fx-effect: null;")),
                new KeyFrame(Duration.millis(300), e -> imgInimigo.setStyle("-fx-effect: dropshadow(gaussian, red, 20, 0.8, 0, 0);")),
                new KeyFrame(Duration.millis(400), e -> imgInimigo.setStyle("-fx-effect: null;"))
        );
        pisca.play();

        ScaleTransition explosao = new ScaleTransition(Duration.seconds(0.3), imgInimigo);
        explosao.setToX(1.3);
        explosao.setToY(1.3);
        explosao.setAutoReverse(true);
        explosao.setCycleCount(2);
        explosao.play();
    }

    private void animarDanoJogador() {
        Timeline pisca = new Timeline(
                new KeyFrame(Duration.millis(100), e -> imgJogador.setStyle("-fx-effect: dropshadow(gaussian, red, 20, 0.8, 0, 0);")),
                new KeyFrame(Duration.millis(300), e -> imgJogador.setStyle("-fx-effect: null;"))
        );
        pisca.play();

        TranslateTransition tremer = new TranslateTransition(Duration.millis(50), imgJogador);
        tremer.setByX(10);
        tremer.setAutoReverse(true);
        tremer.setCycleCount(6);
        tremer.play();
    }

    private void adicionarDialogo(String msg) {
        Platform.runLater(() -> {
            String atual = txtDialogo.getText();
            txtDialogo.setText(msg + "\n" + (atual.length() > 500 ? "" : atual));
        });
    }

    private void atualizarStatusJogador() {
        Platform.runLater(() -> {
            Personagem p = jogador.getPersonagem();
            lblJogadorNome.setText(p.getNome());
            int percentual = (p.getVida() * 100) / p.getVidaMax();
            lblJogadorVida.setText("HP " + p.getVida() + "/" + p.getVidaMax());
            barraJogadorVida.setProgress((double) p.getVida() / p.getVidaMax());

            if (p instanceof AtributoEspecial) {
                AtributoEspecial attr = (AtributoEspecial) p;
                lblJogadorAtributo.setText(attr.getNomeAtributo() + ": " + attr.getValorAtual() + "/" + attr.getValorMaximo());
            }

            if (p.getVida() < p.getVidaMax() / 3) {
                barraJogadorVida.setStyle("-fx-accent: #ff6600;");
            } else if (p.getVida() < p.getVidaMax() / 2) {
                barraJogadorVida.setStyle("-fx-accent: #ffaa00;");
            } else {
                barraJogadorVida.setStyle("-fx-accent: #44ff44;");
            }
        });
    }

    private void atualizarStatusInimigo() {
        Platform.runLater(() -> {
            lblInimigoNome.setText(inimigoAtual.getNome());
            lblInimigoVida.setText("HP " + inimigoAtual.getVida() + "/" + inimigoAtual.getVidaMax());
            barraInimigoVida.setProgress((double) inimigoAtual.getVida() / inimigoAtual.getVidaMax());

            if (inimigoAtual.getVida() < inimigoAtual.getVidaMax() / 3) {
                barraInimigoVida.setStyle("-fx-accent: #ff6600;");
            } else if (inimigoAtual.getVida() < inimigoAtual.getVidaMax() / 2) {
                barraInimigoVida.setStyle("-fx-accent: #ffaa00;");
            } else {
                barraInimigoVida.setStyle("-fx-accent: #ff4444;");
            }
        });
    }

    private void finalizarJogo(boolean vitoria) {
        aguardandoResposta = false;
        if (temporizador != null) temporizador.stop();

        if (vitoria) {
            adicionarDialogo("\n🏆🏆🏆 PARABÉNS! VOCÊ VENCEU O JOGO! 🏆🏆🏆");
            adicionarDialogo("🎉 Pontuação final: " + pontuacao);
            btnHabilidade.setDisable(true);
        } else {
            adicionarDialogo("\n💀 GAME OVER! " + jogador.getPersonagem().getNome() + " foi derrotado! 💀");
            adicionarDialogo("📊 Pontuação final: " + pontuacao);
            btnHabilidade.setDisable(true);
        }
    }
}