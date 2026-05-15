package com.baquara.gui.controllers;

import com.baquara.controle.AvaliadorRespostas;
import com.baquara.dados.BancoPerguntasCapoeira;
import com.baquara.modelo.*;
import com.baquara.modelo.Pergunta.Dificuldade;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
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
    @FXML private VBox painelLacuna;
    @FXML private TextField txtRespostaLacuna;
    @FXML private Button btnEnviarLacuna;
    @FXML private Label lblTurno;
    @FXML private Label lblTurnoMsg;
    @FXML private Label lblTemporizador;

    @FXML private Button btnBasico;
    @FXML private Button btnDificil;
    @FXML private Button btnCombinado;
    @FXML private Button btnEsquiva;

    @FXML private VBox painelDialogo;
    @FXML private VBox painelAcoes;
    @FXML private VBox painelPergunta;
    @FXML private Button btnVoltarMenu;

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

    // Timer
    private Thread timerThread;
    private int tempoMaximoAtual = 0;

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
        adicionarDialogo("🥋 " + capoeirista.getNome() + " entrou na RODA DE CAPOEIRA!", false);
        adicionarDialogo("🎵 O berimbau toca... a roda vai começar!", false);
    }

    public void iniciarRoda() {
        proximoMestre();
    }

    private void proximoMestre() {
        if (estagioAtual >= nomesMestres.length) {
            adicionarDialogo("🏆 PARABÉNS! Você derrotou todos os mestres!", false);
            adicionarDialogo("🦗 Agora enfrente o lendário BESOURO MANGANGÁ!", false);
            enfrentarBesouro();
            return;
        }

        String nome = nomesMestres[estagioAtual];
        int vida = vidasMestres[estagioAtual];
        int ataque = ataquesMestres[estagioAtual];

        inimigoAtual = new Inimigo(nome, vida, ataque, estagioAtual + 1);

        atualizarStatusInimigo();
        adicionarDialogo("\n📜 ESTÁGIO " + (estagioAtual + 1) + "/9", false);
        adicionarDialogo("👥 " + nome + " entra na roda!", false);
        adicionarDialogo("⚔️ Prepare-se para o jogo!", false);

        contadorAtaquesBasicos = 0;
        limparPergunta();
    }

    private void enfrentarBesouro() {
        inimigoAtual = new BesouroManganga();
        atualizarStatusInimigo();
        adicionarDialogo("\n🦗 BESOURO MANGANGÁ APARECEU!", false);
        adicionarDialogo("'FECHADO! NINGUÉM ME SEGURA!'", false);
        limparPergunta();
    }

    private void limparPergunta() {
        pararTimer();

        Platform.runLater(() -> {
            painelDialogo.setVisible(true);
            painelDialogo.setManaged(true);
            painelAcoes.setVisible(true);
            painelAcoes.setManaged(true);

            painelPergunta.setVisible(false);
            painelPergunta.setManaged(false);

            txtPergunta.setText("⚡ Clique em um ataque para começar!");
            txtPergunta.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: #FFF8DC; -fx-text-fill: #000; -fx-border-color: #FFD700; -fx-border-width: 3; -fx-border-radius: 8;");

            painelAlternativas.getChildren().clear();
            if (painelLacuna != null) {
                painelLacuna.setVisible(false);
                painelLacuna.setManaged(false);
            }
            lblDificuldade.setText("⭐ Aguardando ação...");
            lblTurnoMsg.setText("🥋 Escolha seu movimento ao lado! 🥋");
            if (lblTemporizador != null) {
                lblTemporizador.setText("");
            }

            aguardandoResposta = false;
            tipoAtaqueSelecionado = 0;
        });
    }

    private void pararTimer() {
        if (timerThread != null && timerThread.isAlive()) {
            timerThread.interrupt();
            timerThread = null;
        }
    }

    private void carregarPergunta(int tipoAtaque) {
        Dificuldade dificuldade;
        String nomeDificuldade = "";
        String nomeAtaque = "";

        switch (tipoAtaque) {
            case 1:
                dificuldade = Dificuldade.FACIL;
                nomeDificuldade = "⭐ FÁCIL";
                nomeAtaque = "GINGA BÁSICA";
                tempoMaximoAtual = 15;
                break;
            case 2:
                dificuldade = Dificuldade.MEDIO;
                nomeDificuldade = "⭐⭐ MÉDIO";
                nomeAtaque = "ATAQUE DIFÍCIL";
                tempoMaximoAtual = 12;
                break;
            default:
                dificuldade = Dificuldade.DIFICIL;
                nomeDificuldade = "⭐⭐⭐ DIFÍCIL";
                nomeAtaque = "COMBINAÇÃO MORTAL";
                tempoMaximoAtual = 10;
                break;
        }

        if (inimigoAtual instanceof BesouroManganga) {
            tempoMaximoAtual = 8;
            nomeDificuldade += " 🔥 CHEFÃO! 🔥";
        }

        perguntaAtual = bancoPerguntas.getPerguntaAleatoria(dificuldade);

        if (perguntaAtual == null) {
            adicionarDialogo("❌ Erro ao carregar pergunta!", false);
            return;
        }

        tipoAtaqueSelecionado = tipoAtaque;
        aguardandoResposta = true;

        final String nomeDificuldadeFinal = nomeDificuldade;
        final String nomeAtaqueFinal = nomeAtaque;
        final int tempoFinal = tempoMaximoAtual;

        Platform.runLater(() -> {
            painelPergunta.setVisible(true);
            painelPergunta.setManaged(true);
            painelDialogo.setVisible(false);
            painelDialogo.setManaged(false);
            painelAcoes.setVisible(false);
            painelAcoes.setManaged(false);

            lblDificuldade.setText(nomeDificuldadeFinal + " - " + nomeAtaqueFinal);
            txtPergunta.setText(perguntaAtual.getTexto());
            txtPergunta.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: #FFF8DC; -fx-text-fill: #000; -fx-border-color: #FFD700; -fx-border-width: 3; -fx-border-radius: 8;");

            if (lblTemporizador != null) {
                lblTemporizador.setText("⏱️ " + tempoFinal + " segundos");
                lblTemporizador.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14px; -fx-font-weight: bold;");
                lblTemporizador.setVisible(true);
            }

            painelAlternativas.getChildren().clear();

            if (painelLacuna != null) {
                painelLacuna.setVisible(false);
                painelLacuna.setManaged(false);
            }

            lblTurnoMsg.setText("📚 Responda a pergunta para executar o golpe!");

            if (perguntaAtual instanceof PerguntaMultiplaEscolha) {
                configurarMultiplaEscolha((PerguntaMultiplaEscolha) perguntaAtual);
            } else if (perguntaAtual instanceof PerguntaVerdadeiroFalso) {
                configurarVerdadeiroFalso();
            } else if (perguntaAtual instanceof PerguntaCompletarLacuna) {
                configurarLacuna();
            }

            iniciarTimer(tempoFinal);
        });
    }

    private void iniciarTimer(int segundos) {
        pararTimer();

        timerThread = new Thread(() -> {
            try {
                for (int i = segundos; i >= 0; i--) {
                    if (Thread.currentThread().isInterrupted()) {
                        return;
                    }

                    final int segundosRestantes = i;
                    Platform.runLater(() -> {
                        if (lblTemporizador != null) {
                            if (segundosRestantes > 0) {
                                lblTemporizador.setText("⏱️ " + segundosRestantes + " segundos");
                                if (segundosRestantes <= 3) {
                                    lblTemporizador.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 16px; -fx-font-weight: bold;");
                                } else {
                                    lblTemporizador.setStyle("-fx-text-fill: #FFD700; -fx-font-size: 14px; -fx-font-weight: bold;");
                                }
                            } else {
                                lblTemporizador.setText("⏰ TEMPO ESGOTADO!");
                                lblTemporizador.setStyle("-fx-text-fill: #ff4444; -fx-font-size: 16px; -fx-font-weight: bold;");
                            }
                        }
                    });

                    if (i == 0) {
                        if (aguardandoResposta) {
                            Platform.runLater(() -> {
                                if (aguardandoResposta) {
                                    avaliarResposta("TEMPO_ESGOTADO");
                                }
                            });
                        }
                        return;
                    }

                    Thread.sleep(1000);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void configurarMultiplaEscolha(PerguntaMultiplaEscolha pergunta) {
        var opcoes = pergunta.getOpcoes();

        for (int i = 0; i < opcoes.size(); i++) {
            char letra = (char) ('A' + i);
            Button btn = new Button(letra + ") " + opcoes.get(i));
            btn.setStyle("-fx-background-color: #306830; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 8px; -fx-background-radius: 8;");
            btn.setMaxWidth(Double.MAX_VALUE);
            final String resposta = String.valueOf(letra);
            btn.setOnAction(e -> avaliarResposta(resposta));
            painelAlternativas.getChildren().add(btn);
        }
    }

    private void configurarVerdadeiroFalso() {
        HBox hboxVF = new HBox(15.0);
        hboxVF.setAlignment(javafx.geometry.Pos.CENTER);

        Button btnV = new Button("✅ VERDADEIRO");
        Button btnF = new Button("❌ FALSO");

        String estilo = "-fx-background-color: #306830; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8px 20px; -fx-background-radius: 8;";
        btnV.setStyle(estilo);
        btnF.setStyle(estilo);

        btnV.setOnAction(e -> avaliarResposta("V"));
        btnF.setOnAction(e -> avaliarResposta("F"));

        hboxVF.getChildren().addAll(btnV, btnF);
        painelAlternativas.getChildren().add(hboxVF);
    }

    private void configurarLacuna() {
        if (painelLacuna == null) return;

        painelLacuna.setVisible(true);
        painelLacuna.setManaged(true);
        txtRespostaLacuna.clear();
        txtRespostaLacuna.requestFocus();

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

    private void avaliarResposta(String resposta) {
        if (!aguardandoResposta) return;

        pararTimer();

        boolean tempoEsgotado = resposta.equals("TEMPO_ESGOTADO");

        if (tempoEsgotado) {
            adicionarDialogo("\n⏰ TEMPO ESGOTADO! Você perdeu a chance de atacar!", true);
            adicionarDialogo("📖 Resposta correta: " +
                    AvaliadorRespostas.getRespostaCorretaFormatada(perguntaAtual), true);

            int danoInimigo = (int)(calcularDanoInimigo() * 0.7);

            if (capoeirista.getEsquivasRestantes() > 0) {
                adicionarDialogo("🌀 VOCÊ USA UMA ESQUIVA PARA DESVIAR DO CONTRA-ATAQUE!", false);
                capoeirista.executarEsquiva(inimigoAtual, danoInimigo);
                atualizarStatusJogador();
            } else {
                adicionarDialogo("💢 " + inimigoAtual.getNome() + " aproveita sua hesitação e causa " + danoInimigo + " de dano!", true);
                jogador.tomarDano(danoInimigo);
                atualizarStatusJogador();
            }

            if (!jogador.vivo()) {
                finalizarJogo(false);
                return;
            }

            contadorAtaquesBasicos = 0;
            limparPergunta();
            return;
        }

        boolean correta = AvaliadorRespostas.avaliar(perguntaAtual, resposta);
        aguardandoResposta = false;

        if (correta) {
            int dano = calcularDano(tipoAtaqueSelecionado);
            inimigoAtual.tomarDano(dano);
            adicionarDialogo("✅ CORRETO! Causou " + dano + " de dano!", false); // Verde

            int recuperacao = tipoAtaqueSelecionado == 1 ? 15 : (tipoAtaqueSelecionado == 2 ? 10 : 5);
            capoeirista.recarregar(recuperacao);
            atualizarStatusJogador();

            if (!inimigoAtual.vivo()) {
                if (inimigoAtual instanceof BesouroManganga) {
                    adicionarDialogo("\n🏆🏆🏆 VOCÊ DERROTOU O BESOURO MANGANGÁ!", false);
                    adicionarDialogo("👑 Você se tornou uma LENDA DA CAPOEIRA!", false);
                    finalizarJogo(true);
                    return;
                } else {
                    adicionarDialogo("\n🎉 VITÓRIA! " + inimigoAtual.getNome() + " foi derrotado!", false);
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
            adicionarDialogo("❌ ERRADO!", true); // Vermelho
            adicionarDialogo("📖 Resposta correta: " +
                    AvaliadorRespostas.getRespostaCorretaFormatada(perguntaAtual), true); // Vermelho

            int danoInimigo = calcularDanoInimigo();

            if (capoeirista.getEsquivasRestantes() > 0) {
                adicionarDialogo("🌀 VOCÊ USA UMA ESQUIVA PARA DESVIAR DO CONTRA-ATAQUE!", false);
                capoeirista.executarEsquiva(inimigoAtual, danoInimigo);
                atualizarStatusJogador();
            } else {
                adicionarDialogo("💢 " + inimigoAtual.getNome() + " contra-ataca causando " + danoInimigo + " de dano!", true);
                jogador.tomarDano(danoInimigo);
                atualizarStatusJogador();
            }

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
        dano = (int) (dano * variacao);

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
        int dano = (int) (danoBase * variacao);

        return Math.max(5, dano);
    }

    /**
     * Adiciona mensagem ao diálogo com cor específica
     * @param msg A mensagem
     * @param isErro true = vermelho, false = verde
     */
    private void adicionarDialogo(String msg, boolean isErro) {
        Platform.runLater(() -> {
            String atual = txtDialogo.getText();
            String cor = isErro ? "#ff6666" : "#88ff88";

            // Adiciona a mensagem com a cor correspondente
            String textoAtual = txtDialogo.getText();
            String novaLinha = msg + "\n";

            // Aplica estilo com a cor correta
            txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #000000; -fx-text-fill: " + cor + "; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
            txtDialogo.setText(novaLinha + textoAtual);

            // Limita o tamanho
            if (txtDialogo.getText().length() > 800) {
                String texto = txtDialogo.getText();
                txtDialogo.setText(texto.substring(0, 600));
            }
        });
    }

    private void atualizarStatusJogador() {
        Platform.runLater(() -> {
            lblJogadorNome.setText(capoeirista.getNome());
            lblJogadorNivel.setText(capoeirista.getNome().equals("INICIANTE NA RODA") ? "Iniciante" : "Mestre");
            lblJogadorVida.setText("Vida: " + capoeirista.getVida() + "/" + capoeirista.getVidaMax());
            barraJogadorVida.setProgress((double) capoeirista.getVida() / capoeirista.getVidaMax());
            lblJogadorGinga.setText("🌀 Ginga: " + capoeirista.getEnergiaGinga() + "/" + capoeirista.getEnergiaMaxima());

            int esquivasRestantes = capoeirista.getEsquivasRestantes();
            btnEsquiva.setText("🌀 ESQUIVA (" + esquivasRestantes + " restante" + (esquivasRestantes != 1 ? "s" : "") + ")");

            if (esquivasRestantes <= 0) {
                btnEsquiva.setStyle("-fx-background-color: #555; -fx-text-fill: #aaa; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 8px 15px; -fx-background-radius: 8;");
                btnEsquiva.setDisable(true);
            } else {
                btnEsquiva.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white; -fx-font-size: 12px; -fx-font-weight: bold; -fx-padding: 8px 15px; -fx-background-radius: 8;");
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
        pararTimer();
        Platform.runLater(() -> {
            if (vitoria) {
                txtDialogo.setText("\n🏆🏆🏆 VOCÊ É UMA LENDA VIVA! 🏆🏆🏆\n👑 O BERIMBAU CANTA SEU NOME!");
                txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #000000; -fx-text-fill: #88ff88; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
            } else {
                txtDialogo.setText("\n💀 VOCÊ CAIU NA RODA...\n🎵 A capoeira continua viva!");
                txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #000000; -fx-text-fill: #ff6666; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
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
                adicionarDialogo("🔄 GINGA BÁSICA!", false);
                contadorAtaquesBasicos++;
                if (contadorAtaquesBasicos >= 3) {
                    adicionarDialogo("⚠️ Inimigo aprendeu seu padrão! Esquiva automática!", true);
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
                    adicionarDialogo("💫 ATAQUE DIFÍCIL!", false);
                    carregarPergunta(2);
                    atualizarStatusJogador();
                } else {
                    adicionarDialogo("❌ Ginga insuficiente! Precisa de 20 de Ginga.", true);
                }
            }
        });

        btnCombinado.setOnAction(e -> {
            if (inimigoAtual != null && inimigoAtual.vivo() && !aguardandoResposta) {
                if (capoeirista.getEnergiaGinga() >= 40) {
                    capoeirista.consumir(40);
                    adicionarDialogo("🔥 COMBINAÇÃO MORTAL!", false);
                    carregarPergunta(3);
                    atualizarStatusJogador();
                } else {
                    adicionarDialogo("❌ Ginga insuficiente! Precisa de 40 de Ginga.", true);
                }
            }
        });

        btnEsquiva.setOnAction(e -> {
            if (inimigoAtual != null && inimigoAtual.vivo() && !aguardandoResposta) {
                if (capoeirista.getEsquivasRestantes() > 0) {
                    adicionarDialogo("🌀 ESQUIVA! Você desvia do próximo ataque!", false);
                    capoeirista.usarEsquiva();
                    atualizarStatusJogador();
                } else {
                    adicionarDialogo("❌ Sem esquivas disponíveis!", true);
                }
            }
        });

        btnVoltarMenu.setOnAction(e -> limparPergunta());
    }
}