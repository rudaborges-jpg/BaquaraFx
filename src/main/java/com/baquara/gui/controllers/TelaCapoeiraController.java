package com.baquara.gui.controllers;

import com.baquara.controle.AvaliadorRespostas;
import com.baquara.controle.TemporizadorResposta;
import com.baquara.dados.BancoPerguntasCapoeira;
import com.baquara.modelo.*;
import com.baquara.modelo.Pergunta.Dificuldade;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Random;

public class TelaCapoeiraController {

    @FXML private Label lblJogadorNome;
    @FXML private Label lblJogadorNivel;
    @FXML private Label lblJogadorVida;
    @FXML private ProgressBar barraJogadorVida;
    @FXML private Label lblJogadorGinga;
    @FXML private Label lblEsquivas;
    @FXML private Label lblJogadorSprite;

    @FXML private Label lblInimigoNome;
    @FXML private Label lblInimigoNivel;
    @FXML private Label lblInimigoVida;
    @FXML private ProgressBar barraInimigoVida;
    @FXML private Label lblInimigoSprite;

    @FXML private Label lblDificuldade;
    @FXML private TextArea txtPergunta;
    @FXML private TextArea txtDialogo;
    @FXML private VBox painelAlternativas;
    @FXML private VBox painelLacuna;  // ⭐ AGORA É VBox (corrigido)
    @FXML private TextField txtRespostaLacuna;
    @FXML private Button btnEnviarLacuna;
    @FXML private Label lblTurno;
    @FXML private Label lblTurnoMsg;

    @FXML private Button btnBasico;
    @FXML private Button btnDificil;
    @FXML private Button btnCombinado;
    @FXML private Button btnEsquiva;
    @FXML private Label lblTemporizador;


    private TemporizadorResposta temporizador;
    private int tempoRestante = 0;
    private Timeline timelineTemporizador;



    private Jogador jogador;
    private Capoeirista capoeirista;
    private Inimigo inimigoAtual;
    private BancoPerguntasCapoeira bancoPerguntas;
    private Pergunta perguntaAtual;
    private Random random;

    private int estagioAtual = 0;
    private int contadorAtaquesBasicos = 0;
    private int tipoAtaqueSelecionado = 0;
    private boolean aguardandoResposta = false;

    private String[] nomesMestres = {
            "MESTRE BIMBA", "MESTRE PASTINHA", "MESTRE JOÃO GRANDE",
            "MESTRE JOÃO PEQUENO", "MESTRE CANJIQUINHA", "MESTRE CAIÇARA",
            "MESTRE SUASSUNA", "MESTRE NENEL", "MESTRE MORAES"
    };

    private int[] vidasMestres = {160, 260, 360, 460, 560, 660, 760, 860, 960};
    private int[] ataquesMestres = {36, 42, 48, 54, 60, 66, 72, 78, 84};

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
        this.capoeirista = (Capoeirista) jogador.getPersonagem();
        this.bancoPerguntas = new BancoPerguntasCapoeira();
        this.random = new Random();

        atualizarStatusJogador();
        adicionarDialogo("🥋 " + capoeirista.getNome() + " entrou na RODA DE CAPOEIRA!");
        adicionarDialogo("🎵 O berimbau toca... a roda vai começar!");
    }

    public void iniciarRoda() {
        proximoMestre();
    }

    private void proximoMestre() {
        if (estagioAtual >= nomesMestres.length) {
            adicionarDialogo("🏆 PARABÉNS! Você derrotou todos os mestres!");
            adicionarDialogo("🦗 Agora enfrente o lendário BESOURO MANGANGÁ!");
            enfrentarBesouro();
            return;
        }

        String nome = nomesMestres[estagioAtual];
        int vida = vidasMestres[estagioAtual];
        int ataque = ataquesMestres[estagioAtual];

        inimigoAtual = new Inimigo(nome, vida, ataque, estagioAtual + 1);

        atualizarStatusInimigo();
        adicionarDialogo("\n📜 ESTÁGIO " + (estagioAtual + 1) + "/9");
        adicionarDialogo("👥 " + nome + " entra na roda!");
        adicionarDialogo("⚔️ Prepare-se para o jogo!");

        contadorAtaquesBasicos = 0;
        limparPergunta();
    }

    private void enfrentarBesouro() {
        inimigoAtual = new BesouroManganga();
        atualizarStatusInimigo();
        adicionarDialogo("\n🦗 BESOURO MANGANGÁ APARECEU!");
        adicionarDialogo("'FECHADO! NINGUÉM ME SEGURA!'");
        limparPergunta();
    }

    private void iniciarTemporizador(int segundos) {
        tempoRestante = segundos;
        atualizarLabelTemporizador();

        if (timelineTemporizador != null) {
            timelineTemporizador.stop();
        }

        timelineTemporizador = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    tempoRestante--;
                    atualizarLabelTemporizador();

                    if (tempoRestante <= 0) {
                        timelineTemporizador.stop();
                        tempoEsgotado();
                    }
                })
        );
        timelineTemporizador.setCycleCount(segundos);
        timelineTemporizador.play();
    }

    private void atualizarLabelTemporizador() {
        Platform.runLater(() -> {
            if (tempoRestante > 0) {
                lblTemporizador.setText("⏱️ Tempo: " + tempoRestante + "s");
                if (tempoRestante <= 3) {
                    lblTemporizador.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 14px; -fx-font-weight: bold;");
                } else {
                    lblTemporizador.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 12px; -fx-font-weight: bold;");
                }
            } else {
                lblTemporizador.setText("");
            }
        });
    }

    private void tempoEsgotado() {
        aguardandoResposta = false;
        adicionarDialogo("⏰ TEMPO ESGOTADO! Você perdeu a chance de atacar!");

        // Inimigo contra-ataca
        int danoInimigo = calcularDanoInimigo();
        jogador.tomarDano(danoInimigo);
        adicionarDialogo("💢 " + inimigoAtual.getNome() + " aproveita sua hesitação e causa " + danoInimigo + " de dano!");
        atualizarStatusJogador();

        if (!jogador.vivo()) {
            finalizarJogo(false);
            return;
        }

        limparPergunta();
    }

    private void limparPergunta() {
        Platform.runLater(() -> {
            txtPergunta.setText("⚡ Clique em um ataque para começar!");
            painelAlternativas.getChildren().clear();
            if (painelLacuna != null) {
                painelLacuna.setVisible(false);
                painelLacuna.setManaged(false);
            }
            lblDificuldade.setText("⭐ Aguardando ação...");
            aguardandoResposta = false;
            tipoAtaqueSelecionado = 0;
            lblTurnoMsg.setText("🥋 Escolha seu movimento ao lado! 🥋");
            if (timelineTemporizador != null) {
                timelineTemporizador.stop();
            }
            lblTemporizador.setText("");
            tempoRestante = 0;
        });
    }

    private void carregarPergunta(int tipoAtaque) {
        Dificuldade dificuldade;
        String nomeDificuldade;
        String nomeAtaque;

        switch (tipoAtaque) {
            case 1:
                dificuldade = Dificuldade.FACIL;
                nomeDificuldade = "⭐ FÁCIL";
                nomeAtaque = "GINGA BÁSICA";
                break;
            case 2:
                dificuldade = Dificuldade.MEDIO;
                nomeDificuldade = "⭐⭐ MÉDIO";
                nomeAtaque = "ATAQUE DIFÍCIL";
                break;
            default:
                dificuldade = Dificuldade.DIFICIL;
                nomeDificuldade = "⭐⭐⭐ DIFÍCIL";
                nomeAtaque = "COMBINAÇÃO MORTAL";
                break;
        }

        perguntaAtual = bancoPerguntas.getPerguntaAleatoria(dificuldade);

        if (perguntaAtual == null) {
            adicionarDialogo("❌ Erro ao carregar pergunta!");
            return;
        }
        int tempoSegundos;
        switch (tipoAtaque) {
            case 1: tempoSegundos = 15; break;  // Fácil
            case 2: tempoSegundos = 12; break;  // Médio
            default: tempoSegundos = 10; break;  // Difícil
        }

        iniciarTemporizador(tempoSegundos);

        tipoAtaqueSelecionado = tipoAtaque;
        aguardandoResposta = true;

        Platform.runLater(() -> {
            lblDificuldade.setText(nomeDificuldade + " - " + nomeAtaque);
            txtPergunta.setText(perguntaAtual.getTexto());
            txtPergunta.setStyle("-fx-background-color: #FFF8DC; -fx-text-fill: #000; -fx-font-size: 14px; -fx-font-weight: bold; -fx-border-color: #FFD700; -fx-border-width: 3; -fx-border-radius: 8;");
            painelAlternativas.getChildren().clear();

            if (painelLacuna != null) {
                painelLacuna.setVisible(false);
                painelLacuna.setManaged(false);
            }

            if (perguntaAtual instanceof PerguntaMultiplaEscolha) {
                PerguntaMultiplaEscolha p = (PerguntaMultiplaEscolha) perguntaAtual;
                var opcoes = p.getOpcoes();

                for (int i = 0; i < opcoes.size(); i++) {
                    char letra = (char) ('A' + i);
                    Button btn = new Button(letra + ") " + opcoes.get(i));
                    btn.setStyle("-fx-background-color: #306830; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 10px; -fx-background-radius: 8;");
                    btn.setMaxWidth(Double.MAX_VALUE);
                    final String resposta = String.valueOf(letra);
                    btn.setOnAction(e -> avaliarResposta(resposta));
                    painelAlternativas.getChildren().add(btn);
                }

            } else if (perguntaAtual instanceof PerguntaVerdadeiroFalso) {
                HBox hboxVF = new HBox(15.0);
                hboxVF.setAlignment(javafx.geometry.Pos.CENTER);

                Button btnV = new Button("✅ VERDADEIRO");
                Button btnF = new Button("❌ FALSO");

                String estilo = "-fx-background-color: #306830; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 25px; -fx-background-radius: 8;";
                btnV.setStyle(estilo);
                btnF.setStyle(estilo);

                btnV.setOnAction(e -> avaliarResposta("V"));
                btnF.setOnAction(e -> avaliarResposta("F"));

                hboxVF.getChildren().addAll(btnV, btnF);
                painelAlternativas.getChildren().add(hboxVF);

            } else if (perguntaAtual instanceof PerguntaCompletarLacuna) {
                if (painelLacuna != null) {
                    painelLacuna.setVisible(true);
                    painelLacuna.setManaged(true);
                    txtRespostaLacuna.clear();
                    txtRespostaLacuna.requestFocus();

                    // ⭐ ENTER PARA RESPONDER
                    txtRespostaLacuna.setOnKeyPressed(event -> {
                        if (event.getCode() == KeyCode.ENTER) {
                            String resposta = txtRespostaLacuna.getText().trim();
                            if (!resposta.isEmpty()) {
                                avaliarResposta(resposta);
                            }
                        }
                    });

                    btnEnviarLacuna.setOnAction(e -> {
                        String resposta = txtRespostaLacuna.getText().trim();
                        if (!resposta.isEmpty()) {
                            avaliarResposta(resposta);
                        }
                    });
                }
            }

            lblTurnoMsg.setText("📚 Responda a pergunta para executar o golpe!");
        });
    }

    private void avaliarResposta(String resposta) {
        if (!aguardandoResposta) return;

        // ⭐ PARA O TEMPORIZADOR
        if (timelineTemporizador != null) {
            timelineTemporizador.stop();
        }
        lblTemporizador.setText("");

        boolean correta = AvaliadorRespostas.avaliar(perguntaAtual, resposta);
        aguardandoResposta = false;

        if (correta) {
            int dano = calcularDano(tipoAtaqueSelecionado);
            inimigoAtual.tomarDano(dano);
            adicionarDialogo("✅ CORRETO! Causou " + dano + " de dano!");

            int recuperacao = tipoAtaqueSelecionado == 1 ? 15 : (tipoAtaqueSelecionado == 2 ? 10 : 5);
            capoeirista.recarregar(recuperacao);
            atualizarStatusJogador();

            if (!inimigoAtual.vivo()) {
                if (inimigoAtual instanceof BesouroManganga) {
                    adicionarDialogo("\n🏆🏆🏆 VOCÊ DERROTOU O BESOURO MANGANGÁ!");
                    adicionarDialogo("👑 Você se tornou uma LENDA DA CAPOEIRA!");
                    finalizarJogo(true);
                    return;
                } else {
                    adicionarDialogo("\n🎉 VITÓRIA! " + inimigoAtual.getNome() + " foi derrotado!");
                    estagioAtual++;
                    capoeirista.evoluirTitulo(estagioAtual + 1);
                    capoeirista.recarregarCompletamente();
                    atualizarStatusJogador();

                    if (estagioAtual >= nomesMestres.length) {
                        enfrentarBesouro();
                    } else {
                        proximoMestre();
                    }
                    limparPergunta();
                    return;
                }
            }

            if (tipoAtaqueSelecionado != 1) {
                contadorAtaquesBasicos = 0;
            }

        } else {
            adicionarDialogo("❌ ERRADO!");
            adicionarDialogo("📖 Resposta correta: " +
                    AvaliadorRespostas.getRespostaCorretaFormatada(perguntaAtual));

            int danoInimigo = calcularDanoInimigo();
            jogador.tomarDano(danoInimigo);
            adicionarDialogo("💢 " + inimigoAtual.getNome() + " contra-ataca causando " + danoInimigo + " de dano!");
            atualizarStatusJogador();

            if (!jogador.vivo()) {
                finalizarJogo(false);
                return;
            }

            contadorAtaquesBasicos = 0;
        }

        atualizarStatusInimigo();
        limparPergunta();
    }

    private int calcularDano(int tipoAtaque) {
        int danoBase;

        switch (tipoAtaque) {
            case 1:
                danoBase = capoeirista.getAtaque() + random.nextInt(10) + 5;
                break;
            case 2:
                danoBase = capoeirista.getAtaque() + 15 + random.nextInt(20);
                break;
            default:
                int golpes = 3 + random.nextInt(2);
                danoBase = 0;
                for (int i = 0; i < golpes; i++) {
                    danoBase += 8 + random.nextInt(12);
                }
                break;
        }

        int multiplicador = (estagioAtual / 3) + 1;
        int dano = danoBase * multiplicador;

        double variacao = 0.85 + (random.nextDouble() * 0.3);
        dano = (int)(dano * variacao);

        return Math.max(10, Math.min(350, dano));
    }

    private int calcularDanoInimigo() {
        int danoBase = inimigoAtual.getAtaque();

        if (inimigoAtual instanceof BesouroManganga) {
            BesouroManganga besouro = (BesouroManganga) inimigoAtual;
            if (besouro.getFaseAtual() == 3) {
                danoBase *= 2;
            }
        }

        double variacao = 0.85 + (random.nextDouble() * 0.3);
        int dano = (int)(danoBase * variacao);

        return Math.max(5, dano);
    }

    private void adicionarDialogo(String msg) {
        Platform.runLater(() -> {
            String atual = txtDialogo.getText();
            txtDialogo.setText(msg + "\n" + (atual.length() > 500 ? atual.substring(0, 500) : atual));
        });
    }

    private void atualizarStatusJogador() {
        Platform.runLater(() -> {
            lblJogadorNome.setText(capoeirista.getNome());
            lblJogadorNivel.setText(capoeirista.getNome().equals("INICIANTE NA RODA") ? "Iniciante" : "Mestre");
            lblJogadorVida.setText("Vida: " + capoeirista.getVida() + "/" + capoeirista.getVidaMax());
            barraJogadorVida.setProgress((double) capoeirista.getVida() / capoeirista.getVidaMax());
            lblJogadorGinga.setText("🌀 Ginga: " + capoeirista.getEnergiaGinga() + "/" + capoeirista.getEnergiaMaxima());

            // Atualiza botão esquiva
            int esquivasRestantes = capoeirista.getEsquivasRestantes();
            btnEsquiva.setText("🌀 ESQUIVA (" + esquivasRestantes + " restante" + (esquivasRestantes != 1 ? "s" : "") + ")");

            if (esquivasRestantes <= 0) {
                btnEsquiva.setStyle("-fx-background-color: #555; -fx-text-fill: #aaa; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8px 15px; -fx-background-radius: 8;");
                btnEsquiva.setDisable(true);
            } else {
                btnEsquiva.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8px 15px; -fx-background-radius: 8;");
                btnEsquiva.setDisable(false);
            }
        });
    }

    private void atualizarStatusInimigo() {
        Platform.runLater(() -> {
            lblInimigoNome.setText(inimigoAtual.getNome());
            lblInimigoVida.setText("Vida: " + inimigoAtual.getVida() + "/" + inimigoAtual.getVidaMax());
            barraInimigoVida.setProgress((double) inimigoAtual.getVida() / inimigoAtual.getVidaMax());

            if (inimigoAtual instanceof BesouroManganga) {
                BesouroManganga b = (BesouroManganga) inimigoAtual;
                lblInimigoNivel.setText("Fase " + b.getFaseAtual() + "/3 ⚡ LENDA ⚡");
                lblInimigoSprite.setText("🦗");
            } else {
                lblInimigoNivel.setText("Estágio " + (estagioAtual + 1) + "/9");
                lblInimigoSprite.setText("🥋");
            }
        });
    }

    private void finalizarJogo(boolean vitoria) {
        Platform.runLater(() -> {
            if (vitoria) {
                txtDialogo.setText("\n🏆🏆🏆 VOCÊ É UMA LENDA VIVA! 🏆🏆🏆\n👑 O BERIMBAU CANTA SEU NOME!");
            } else {
                txtDialogo.setText("\n💀 VOCÊ CAIU NA RODA...\n🎵 A capoeira continua viva!");
            }
            btnBasico.setDisable(true);
            btnDificil.setDisable(true);
            btnCombinado.setDisable(true);
            btnEsquiva.setDisable(true);
        });
    }

    @FXML
    public void initialize() {
        btnBasico.setOnAction(e -> {
            if (inimigoAtual != null && inimigoAtual.vivo() && !aguardandoResposta) {
                adicionarDialogo("🔄 GINGA BÁSICA!");
                contadorAtaquesBasicos++;
                if (contadorAtaquesBasicos >= 3) {
                    adicionarDialogo("⚠️ Inimigo aprendeu seu padrão! Esquiva automática!");
                    contadorAtaquesBasicos = 0;
                } else {
                    carregarPergunta(1);
                }
            }
        });

        btnDificil.setOnAction(e -> {
            if (inimigoAtual != null && inimigoAtual.vivo() && !aguardandoResposta) {
                if (capoeirista.getEnergiaGinga() >= 20) {
                    capoeirista.consumir(20);
                    adicionarDialogo("💫 ATAQUE DIFÍCIL!");
                    carregarPergunta(2);
                    atualizarStatusJogador();
                } else {
                    adicionarDialogo("❌ Ginga insuficiente! Precisa de 20 de Ginga.");
                }
            }
        });

        btnCombinado.setOnAction(e -> {
            if (inimigoAtual != null && inimigoAtual.vivo() && !aguardandoResposta) {
                if (capoeirista.getEnergiaGinga() >= 40) {
                    capoeirista.consumir(40);
                    adicionarDialogo("🔥 COMBINAÇÃO MORTAL!");
                    carregarPergunta(3);
                    atualizarStatusJogador();
                } else {
                    adicionarDialogo("❌ Ginga insuficiente! Precisa de 40 de Ginga.");
                }
            }
        });

        btnEsquiva.setOnAction(e -> {
            if (inimigoAtual != null && inimigoAtual.vivo() && !aguardandoResposta) {
                if (capoeirista.getEsquivasRestantes() > 0) {
                    adicionarDialogo("🌀 ESQUIVA! Você desvia do próximo ataque!");
                    capoeirista.usarEsquiva();
                    atualizarStatusJogador();
                } else {
                    adicionarDialogo("❌ Sem esquivas disponíveis!");
                }
            }
        });
    }
}