package com.baquara.gui.controllers;

import com.baquara.controle.AvaliadorRespostas;
import com.baquara.controle.RankingManager;
import com.baquara.dados.BancoPerguntas;
import com.baquara.modelo.*;
import com.baquara.modelo.Pergunta.Dificuldade;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.*;

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
    private BancoPerguntas bancoPerguntas;
    private Pergunta perguntaAtual;
    private Random random;

    private int estagioAtual = 0;
    private int contadorAtaquesBasicos = 0;
    private int tipoAtaqueSelecionado = 0;
    private boolean aguardandoResposta = false;

    // Flag: inimigo aprendeu o padrão de ginga básica
    private boolean inimigoAprendeuPadrao = false;

    private Dificuldade dificuldadePerguntaAtual;

    // Timer
    private Thread timerThread;
    private int tempoMaximoAtual = 0;

    // Estatísticas
    private int perguntasCertas = 0;
    private int perguntasErradas = 0;
    private int danoTotalCausado = 0;
    private int danoTotalRecebido = 0;
    private int rodadaAtual = 0;
    private int pontuacaoTotal = 0;

    // ⭐⭐⭐ SISTEMA DE PERGUNTAS GLOBAL ⭐⭐⭐
    private Set<Integer> perguntasUsadasGlobal = new HashSet<>();
    private Map<Dificuldade, Integer> totalPerguntasPorDificuldade = new HashMap<>();
    private Map<Dificuldade, Integer> resetsPorDificuldade = new HashMap<>();
    private boolean bancoInicializado = false;

    // Flags para evitar duplicatas
    private static boolean rankingCapoeiraJaSalvo = false;
    private boolean jogoCapoeiraFinalizado = false;

    private String[] nomesMestres = {
            "MESTRE BIMBA", "MESTRE PASTINHA", "MESTRE JOÃO GRANDE",
            "MESTRE JOÃO PEQUENO", "MESTRE CANJIQUINHA", "MESTRE CAIÇARA",
            "MESTRE SUASSUNA", "MESTRE NENEL", "MESTRE MORAES"
    };

    private int[] vidasMestres = {160, 260, 360, 460, 560, 660, 760, 860, 960};
    private int[] ataquesMestres = {36, 42, 48, 54, 60, 66, 72, 78, 84};

    // Frases do inimigo
    private String[] frasesAprendeuPadrao = {
            "Já decorei seus passos!",
            "Só sabe fazer isso?",
            "Previsível demais!",
            "Vai repetir isso de novo?",
            "Eu já vi esse golpe antes!",
            "Assim você não me pega!",
            "Cadê a criatividade?",
            "Isso é tudo que você sabe?",
            "Sua ginga não me engana mais!",
            "Você precisa de novos movimentos!"
    };

    private String[] frasesQuebrouPadrao = {
            "O quê? Um golpe diferente?",
            "Isso não estava no script!",
            "Você me surpreendeu!",
            "Inovação? Finalmente!",
            "Interessante...",
            "Você aprendeu algo novo?",
            "Esse golpe é perigoso!",
            "Malandro! Mudou o jogo!"
    };

    // ==================== INICIALIZAÇÃO ====================

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
        this.capoeirista = (Capoeirista) jogador.getPersonagem();
        this.bancoPerguntas = new BancoPerguntas();
        this.random = new Random();

        // ⭐ INICIALIZA O BANCO DE PERGUNTAS GLOBAL (apenas uma vez)
        if (!bancoInicializado) {
            inicializarBancoPerguntasGlobal();
            bancoInicializado = true;
        }

        // Reseta flags de combate (mas NÃO as perguntas!)
        rankingCapoeiraJaSalvo = false;
        jogoCapoeiraFinalizado = false;
        inimigoAprendeuPadrao = false;
        contadorAtaquesBasicos = 0;

        perguntasCertas = 0;
        perguntasErradas = 0;
        danoTotalCausado = 0;
        danoTotalRecebido = 0;
        rodadaAtual = 0;
        pontuacaoTotal = 0;

        atualizarStatusJogador();
        adicionarDialogoNormal("🥋 " + capoeirista.getNome() + " entrou na RODA DE CAPOEIRA!");
        adicionarDialogoNormal("🎵 O berimbau toca... a roda vai começar!");

        mostrarEstatisticasPerguntas();
    }

    public void iniciarRoda() {
        proximoMestre();
    }

    // ==================== SISTEMA DE PERGUNTAS GLOBAL ====================

    private void inicializarBancoPerguntasGlobal() {
        for (Dificuldade diff : Dificuldade.values()) {
            List<Pergunta> perguntas = bancoPerguntas.getPerguntasPorDificuldade(
                    PerTipo.CAPOEIRISTA, diff, 1);

            if (perguntas != null) {
                totalPerguntasPorDificuldade.put(diff, perguntas.size());
            } else {
                totalPerguntasPorDificuldade.put(diff, 0);
            }
            resetsPorDificuldade.put(diff, 0);
        }

        perguntasUsadasGlobal.clear();

        System.out.println("\n📚 RODA DE CAPOEIRA - Banco de perguntas carregado:");
        for (Dificuldade diff : Dificuldade.values()) {
            System.out.println("   " + diff.getNome() + ": " +
                    totalPerguntasPorDificuldade.get(diff) + " perguntas");
        }
        System.out.println("\n✨ O banco reinicia APENAS quando TODAS as perguntas de uma dificuldade forem usadas!\n");
    }

    private void mostrarEstatisticasPerguntas() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n📊 PERGUNTAS DISPONÍVEIS:\n");
        for (Dificuldade diff : Dificuldade.values()) {
            int total = totalPerguntasPorDificuldade.getOrDefault(diff, 0);
            int usadas = 0;
            if (total > 0) {
                List<Pergunta> perguntas = bancoPerguntas.getPerguntasPorDificuldade(
                        PerTipo.CAPOEIRISTA, diff, 1);
                if (perguntas != null) {
                    for (Pergunta p : perguntas) {
                        if (perguntasUsadasGlobal.contains(p.getId())) {
                            usadas++;
                        }
                    }
                }
            }
            int restantes = total - usadas;
            String emoji = restantes > 0 ? "✅" : "🔄";
            sb.append("   " + emoji + " " + diff.getNome() + ": " +
                    restantes + "/" + total + " restantes" +
                    (restantes == 0 && total > 0 ? " (resetará na próxima)" : ""));
            if (resetsPorDificuldade.getOrDefault(diff, 0) > 0) {
                sb.append(" | Resets: " + resetsPorDificuldade.get(diff));
            }
            sb.append("\n");
        }
        adicionarDialogoNormal(sb.toString());
    }

    private Pergunta getPerguntaComResetGlobal(Dificuldade dificuldade) {
        List<Pergunta> todasPerguntas = bancoPerguntas.getPerguntasPorDificuldade(
                PerTipo.CAPOEIRISTA, dificuldade, 1);

        if (todasPerguntas == null || todasPerguntas.isEmpty()) {
            System.out.println("❌ Nenhuma pergunta disponível para " + dificuldade.getNome());
            return null;
        }

        List<Pergunta> naoUsadas = new ArrayList<>();
        for (Pergunta p : todasPerguntas) {
            if (!perguntasUsadasGlobal.contains(p.getId())) {
                naoUsadas.add(p);
            }
        }

        // ⭐ SE ACABARAM TODAS AS PERGUNTAS, RESETA!
        if (naoUsadas.isEmpty()) {
            int resetNumero = resetsPorDificuldade.getOrDefault(dificuldade, 0) + 1;
            resetsPorDificuldade.put(dificuldade, resetNumero);

            String msg = "\n" + "=".repeat(60) + "\n" +
                    "🔄 RESET DO BANCO DE PERGUNTAS! 🔄\n" +
                    "   Dificuldade: " + dificuldade.getNome() + "\n" +
                    "   Você já usou TODAS as " + totalPerguntasPorDificuldade.get(dificuldade) +
                    " perguntas desta dificuldade!\n" +
                    "   Reset #" + resetNumero + " - Reiniciando o ciclo...\n" +
                    "=".repeat(60);
            adicionarDialogoNormal(msg);
            System.out.println(msg);

            for (Pergunta p : todasPerguntas) {
                perguntasUsadasGlobal.remove(p.getId());
            }

            naoUsadas = new ArrayList<>(todasPerguntas);
        }

        Pergunta escolhida = naoUsadas.get(random.nextInt(naoUsadas.size()));
        perguntasUsadasGlobal.add(escolhida.getId());

        int restantes = naoUsadas.size() - 1;
        int total = totalPerguntasPorDificuldade.get(dificuldade);
        int resets = resetsPorDificuldade.get(dificuldade);

        String stats = "\n📊 " + dificuldade.getNome() + ": " +
                restantes + "/" + total + " restantes" +
                (resets > 0 ? " | Resets: " + resets : "");
        adicionarDialogoNormal(stats);

        return escolhida;
    }

    // ==================== MÉTODOS DE COMBATE ====================

    private boolean inimigoVaiDesviarDaGingaBasica() {
        if (inimigoAprendeuPadrao) {
            String frase = frasesAprendeuPadrao[random.nextInt(frasesAprendeuPadrao.length)];
            adicionarDialogoErro("🔄 " + inimigoAtual.getNome() + ": '" + frase + "'");
            return true;
        }
        return false;
    }

    private void resetarPadraoDoInimigo() {
        if (inimigoAprendeuPadrao) {
            inimigoAprendeuPadrao = false;
            contadorAtaquesBasicos = 0;
            String frase = frasesQuebrouPadrao[random.nextInt(frasesQuebrouPadrao.length)];
            adicionarDialogoAcerto("💥 " + inimigoAtual.getNome() + ": '" + frase + "'");
            adicionarDialogoNormal("🌀 O inimigo foi surpreendido! Padrão de ginga resetado!");
            lblInimigoNome.setStyle("-fx-text-fill: #FFFFFF;");
        }
    }

    private void proximoMestre() {
        if (estagioAtual >= nomesMestres.length) {
            adicionarDialogoNormal("🏆 PARABÉNS! Você derrotou todos os mestres!");
            adicionarDialogoNormal("🦗 Agora enfrente o lendário BESOURO MANGANGÁ!");
            enfrentarBesouro();
            return;
        }

        String nome = nomesMestres[estagioAtual];
        int nivel = estagioAtual + 1;
        int vida = vidasMestres[estagioAtual];
        int ataque = ataquesMestres[estagioAtual];
        int defesa = 5 + (nivel * 3);

        inimigoAtual = new Inimigo(nome, nivel, vida, ataque, defesa);

        // Reseta flags de combate (mas NÃO as perguntas!)
        inimigoAprendeuPadrao = false;
        contadorAtaquesBasicos = 0;
        lblInimigoNome.setStyle("-fx-text-fill: #FFFFFF;");

        atualizarStatusInimigo();
        adicionarDialogoNormal("\n📜 ESTÁGIO " + (estagioAtual + 1) + "/9");
        adicionarDialogoNormal("👥 " + nome + " entra na roda!");
        adicionarDialogoNormal("⚔️ Prepare-se para o jogo!");

        mostrarEstatisticasPerguntas();
        limparPergunta();
    }

    private void enfrentarBesouro() {
        inimigoAtual = new BesouroManganga();

        inimigoAprendeuPadrao = false;
        contadorAtaquesBasicos = 0;
        lblInimigoNome.setStyle("-fx-text-fill: #FFFFFF;");

        atualizarStatusInimigo();
        adicionarDialogoNormal("\n🦗 BESOURO MANGANGÁ APARECEU!");
        adicionarDialogoNormal("'FECHADO! NINGUÉM ME SEGURA!'");

        mostrarEstatisticasPerguntas();
        limparPergunta();
    }

    // ==================== MÉTODOS DE INTERFACE ====================

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

        this.dificuldadePerguntaAtual = dificuldade;

        if (inimigoAtual instanceof BesouroManganga) {
            tempoMaximoAtual = 8;
            nomeDificuldade += " 🔥 CHEFÃO! 🔥";
        }

        // ⭐ USA O SISTEMA DE PERGUNTAS GLOBAL
        perguntaAtual = getPerguntaComResetGlobal(dificuldade);

        if (perguntaAtual == null) {
            adicionarDialogoNormal("❌ Erro ao carregar pergunta!");
            return;
        }

        tipoAtaqueSelecionado = tipoAtaque;
        aguardandoResposta = true;
        rodadaAtual++;

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

    // ==================== CONFIGURAÇÃO DE PERGUNTAS ====================

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

    // ==================== AVALIAÇÃO DE RESPOSTA ====================

    private void avaliarResposta(String resposta) {
        if (!aguardandoResposta) return;

        pararTimer();

        boolean tempoEsgotado = resposta.equals("TEMPO_ESGOTADO");

        if (tempoEsgotado) {
            perguntasErradas++;
            adicionarDialogoErro("\n⏰ TEMPO ESGOTADO! Você perdeu a chance de atacar!");
            adicionarDialogoErro("📖 Resposta correta: " +
                    AvaliadorRespostas.getRespostaCorretaFormatada(perguntaAtual));

            int danoInimigo = (int)(calcularDanoInimigo() * 0.7);
            danoTotalRecebido += danoInimigo;

            if (capoeirista.tentarDesviar(inimigoAtual, danoInimigo)) {
                adicionarDialogoNormal("🌀 ESQUIVA BEM-SUCEDIDA! Você desviou do contra-ataque!");
            } else {
                adicionarDialogoErro("💢 " + inimigoAtual.getNome() + " aproveita sua hesitação e causa " + danoInimigo + " de dano!");
                jogador.tomarDano(danoInimigo);
            }

            atualizarStatusJogador();

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

        boolean ehGingaBasica = (tipoAtaqueSelecionado == 1);

        if (correta) {
            perguntasCertas++;

            boolean inimigoDesviou = false;

            if (ehGingaBasica) {
                contadorAtaquesBasicos++;

                if (contadorAtaquesBasicos >= 3) {
                    inimigoAprendeuPadrao = true;
                    String frase = frasesAprendeuPadrao[random.nextInt(frasesAprendeuPadrao.length)];
                    adicionarDialogoErro("🔄 " + inimigoAtual.getNome() + " APRENDEU SEU PADRÃO!");
                    adicionarDialogoErro("   💬 '" + frase + "'");
                    adicionarDialogoErro("⚠️ " + inimigoAtual.getNome() + " vai DESVIAR de TODAS as suas gingas básicas!");
                    adicionarDialogoErro("   🎯 Use um ataque diferente para quebrar o padrão!");
                    lblInimigoNome.setStyle("-fx-text-fill: #FF6B6B; -fx-font-weight: bold;");
                }

                if (inimigoAprendeuPadrao) {
                    inimigoDesviou = true;
                    adicionarDialogoErro("\n🔄 " + inimigoAtual.getNome() + " DESVIOU da sua Ginga Básica!");
                    adicionarDialogoErro("   🎯 Ele já aprendeu seu padrão! Use um golpe diferente!");

                    atualizarStatusJogador();
                    limparPergunta();
                    return;
                }
            } else {
                resetarPadraoDoInimigo();
            }

            if (!inimigoDesviou) {
                int dano = calcularDano(tipoAtaqueSelecionado);
                danoTotalCausado += dano;
                inimigoAtual.tomarDano(dano);
                adicionarDialogoAcerto("✅ CORRETO! Causou " + dano + " de dano!");

                int pontosGanhos = 50 + (tipoAtaqueSelecionado * 25);
                pontuacaoTotal += pontosGanhos;
                jogador.addPontuacao(pontosGanhos);
                adicionarDialogoNormal("🏆 +" + pontosGanhos + " pontos!");

                int recuperacaoGinga;
                switch (tipoAtaqueSelecionado) {
                    case 1: recuperacaoGinga = 15; break;
                    case 2: recuperacaoGinga = 10; break;
                    default: recuperacaoGinga = 5; break;
                }
                capoeirista.recarregar(recuperacaoGinga);
                adicionarDialogoNormal("🌀 +" + recuperacaoGinga + " de Ginga recuperada!");

                atualizarStatusJogador();

                if (!inimigoAtual.vivo()) {
                    if (inimigoAtual instanceof BesouroManganga) {
                        int bonusBesouro = 500;
                        pontuacaoTotal += bonusBesouro;
                        jogador.addPontuacao(bonusBesouro);
                        adicionarDialogoAcerto("👑 BÔNUS POR DERROTAR O BESOURO: +" + bonusBesouro + " pontos!");

                        adicionarDialogoAcerto("\n🏆🏆🏆 VOCÊ DERROTOU O BESOURO MANGANGÁ!");
                        adicionarDialogoAcerto("👑 Você se tornou uma LENDA DA CAPOEIRA!");
                        finalizarJogo(true);
                        return;
                    } else {
                        int bonusMestre = (estagioAtual + 1) * 50;
                        pontuacaoTotal += bonusMestre;
                        jogador.addPontuacao(bonusMestre);
                        adicionarDialogoAcerto("🏆 Bônus por derrotar " + inimigoAtual.getNome() + ": +" + bonusMestre + " pontos!");

                        adicionarDialogoAcerto("\n🎉 VITÓRIA! " + inimigoAtual.getNome() + " foi derrotado!");
                        estagioAtual++;
                        capoeirista.evoluirTitulo(estagioAtual + 1);
                        capoeirista.resetarEsquivas();
                        atualizarStatusJogador();

                        // NÃO RESETA AS PERGUNTAS! Só as flags de combate
                        inimigoAprendeuPadrao = false;
                        contadorAtaquesBasicos = 0;
                        lblInimigoNome.setStyle("-fx-text-fill: #FFFFFF;");

                        if (estagioAtual >= nomesMestres.length) {
                            enfrentarBesouro();
                        } else {
                            proximoMestre();
                        }
                        limparPergunta();
                        return;
                    }
                }
            }

        } else {
            perguntasErradas++;

            int penalidade = 20;
            pontuacaoTotal = Math.max(0, pontuacaoTotal - penalidade);
            adicionarDialogoErro("💔 -" + penalidade + " pontos por erro!");

            adicionarDialogoErro("❌ ERRADO!");
            adicionarDialogoErro("📖 Resposta correta: " +
                    AvaliadorRespostas.getRespostaCorretaFormatada(perguntaAtual));

            if (ehGingaBasica && inimigoAprendeuPadrao) {
                adicionarDialogoErro("🔄 " + inimigoAtual.getNome() + " já conhece seu padrão! Desvio automático!");
            } else {
                int danoInimigo = calcularDanoInimigo();
                danoTotalRecebido += danoInimigo;

                if (capoeirista.tentarDesviar(inimigoAtual, danoInimigo)) {
                    adicionarDialogoNormal("🌀 ESQUIVA BEM-SUCEDIDA! Você desviou do contra-ataque!");
                } else {
                    adicionarDialogoErro("💢 " + inimigoAtual.getNome() + " contra-ataca causando " + danoInimigo + " de dano!");
                    jogador.tomarDano(danoInimigo);
                }
            }

            if (!ehGingaBasica) {
                resetarPadraoDoInimigo();
            }

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

    // ==================== CÁLCULOS ====================

    private int calcularDano(int tipoAtaque) {
        int danoBase;
        int ataque = capoeirista.getAtaque();

        int bonusDificuldade = 0;
        switch (dificuldadePerguntaAtual) {
            case FACIL:
                bonusDificuldade = 5;
                break;
            case MEDIO:
                bonusDificuldade = 15;
                break;
            case DIFICIL:
                bonusDificuldade = 30;
                break;
        }

        switch (tipoAtaque) {
            case 1:
                danoBase = ataque + random.nextInt(10) + 5 + bonusDificuldade;
                break;
            case 2:
                danoBase = ataque + 15 + random.nextInt(20) + bonusDificuldade * 2;
                break;
            default:
                int golpes = 3 + random.nextInt(2);
                danoBase = 0;
                for (int i = 0; i < golpes; i++) {
                    danoBase += 8 + random.nextInt(12) + (bonusDificuldade / 2);
                }
                danoBase += bonusDificuldade * 2;
                break;
        }

        int multiplicador = Math.max(1, (estagioAtual / 3) + 1);
        int dano = danoBase * multiplicador;

        double variacao = 0.85 + (random.nextDouble() * 0.3);
        dano = (int) (dano * variacao);

        return Math.max(10, Math.min(450, dano));
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

    // ==================== DIÁLOGOS ====================

    private void adicionarDialogoNormal(String msg) {
        Platform.runLater(() -> {
            String atual = txtDialogo.getText();
            txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #FFF8DC; -fx-text-fill: #000000; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
            txtDialogo.setText(msg + "\n" + (atual.length() > 500 ? atual.substring(0, 500) : atual));
        });
    }

    private void adicionarDialogoAcerto(String msg) {
        Platform.runLater(() -> {
            String atual = txtDialogo.getText();
            txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #FFF8DC; -fx-text-fill: #008800; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
            txtDialogo.setText(msg + "\n" + (atual.length() > 500 ? atual.substring(0, 500) : atual));

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (txtDialogo.getText().contains(msg)) {
                            txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #FFF8DC; -fx-text-fill: #000000; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
                        }
                    });
                }
            }, 1500);
        });
    }

    private void adicionarDialogoErro(String msg) {
        Platform.runLater(() -> {
            String atual = txtDialogo.getText();
            txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #FFF8DC; -fx-text-fill: #CC0000; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
            txtDialogo.setText(msg + "\n" + (atual.length() > 500 ? atual.substring(0, 500) : atual));

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (txtDialogo.getText().contains(msg)) {
                            txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #FFF8DC; -fx-text-fill: #000000; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
                        }
                    });
                }
            }, 1500);
        });
    }

    // ==================== STATUS ====================

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

    // ==================== FINALIZAR JOGO ====================

    private void finalizarJogo(boolean vitoria) {
        pararTimer();

        if (jogoCapoeiraFinalizado) {
            System.out.println("⚠️ Jogo da Capoeira já finalizado! Ignorando nova chamada.");
            return;
        }
        jogoCapoeiraFinalizado = true;

        // ⭐ SALVA O RANKING APENAS UMA VEZ
        if (!rankingCapoeiraJaSalvo) {
            try {
                RankingManager rankingManager = new RankingManager();

                int estagiosCompletados = estagioAtual;
                if (vitoria && inimigoAtual instanceof BesouroManganga) {
                    estagiosCompletados = 10;
                }

                int pontuacaoFinal = pontuacaoTotal;
                if (pontuacaoFinal <= 0 && estagiosCompletados > 0) {
                    pontuacaoFinal = estagiosCompletados * 50;
                }

                System.out.println("📊 Salvando ranking da Capoeira:");
                System.out.println("   👤 Jogador: " + jogador.getNome());
                System.out.println("   🥋 Personagem: " + capoeirista.getNome());
                System.out.println("   🏆 Pontuação: " + pontuacaoFinal);
                System.out.println("   📌 Estágios: " + estagiosCompletados + "/10");
                System.out.println("   🏅 Vitória: " + vitoria);

                rankingManager.adicionarPontuacao(
                        jogador.getNome(),
                        capoeirista.getNome(),
                        pontuacaoFinal,
                        estagiosCompletados,
                        vitoria,
                        "Roda de Capoeira"
                );
                rankingCapoeiraJaSalvo = true;
                System.out.println("🏆 Ranking da Capoeira salvo com sucesso!");
            } catch (Exception e) {
                System.err.println("Erro ao salvar ranking da Capoeira: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // ⭐ ABRE A TELA DE RESULTADO
        Platform.runLater(() -> {
            try {
                Map<String, Object> stats = new HashMap<>();
                stats.put("pontuacao", pontuacaoTotal);
                stats.put("rodadas", rodadaAtual);
                stats.put("acertos", perguntasCertas);
                stats.put("erros", perguntasErradas);
                stats.put("danoCausado", danoTotalCausado);
                stats.put("danoRecebido", danoTotalRecebido);
                stats.put("habilidadesUsadas", 0);

                int estagiosCompletados = estagioAtual;
                if (vitoria && inimigoAtual instanceof BesouroManganga) {
                    estagiosCompletados = 10;
                }
                stats.put("estagiosCompletados", estagiosCompletados);
                stats.put("modoJogo", "Roda de Capoeira");

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-resultado.fxml"));
                Parent root = loader.load();

                TelaResultadoController controller = loader.getController();
                controller.setDados(jogador, stats, vitoria, new RankingManager());

                Stage stage = (Stage) btnBasico.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Baquara - Resultado");

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Erro ao abrir tela de resultado: " + e.getMessage());
                if (vitoria) {
                    adicionarDialogoAcerto("\n🏆🏆🏆 VOCÊ É UMA LENDA VIVA! 🏆🏆🏆");
                    adicionarDialogoAcerto("👑 O BERIMBAU CANTA SEU NOME!");
                } else {
                    adicionarDialogoErro("\n💀 VOCÊ CAIU NA RODA...");
                    adicionarDialogoErro("🎵 A capoeira continua viva!");
                }
                btnBasico.setDisable(true);
                btnDificil.setDisable(true);
                btnCombinado.setDisable(true);
                btnEsquiva.setDisable(true);
            }
        });
    }

    // ==================== INITIALIZE ====================

    @FXML
    public void initialize() {
        txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #FFF8DC; -fx-text-fill: #000000; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");

        painelDialogo.setVisible(true);
        painelDialogo.setManaged(true);
        painelAcoes.setVisible(true);
        painelAcoes.setManaged(true);
        painelPergunta.setVisible(false);
        painelPergunta.setManaged(false);

        btnBasico.setOnAction(e -> {
            if (inimigoAtual != null && inimigoAtual.vivo() && !aguardandoResposta) {
                adicionarDialogoNormal("🔄 GINGA BÁSICA!");
                carregarPergunta(1);
            }
        });

        btnDificil.setOnAction(e -> {
            if (inimigoAtual != null && inimigoAtual.vivo() && !aguardandoResposta) {
                if (capoeirista.getEnergiaGinga() >= 20) {
                    capoeirista.consumir(20);
                    adicionarDialogoNormal("💫 ATAQUE DIFÍCIL!");
                    carregarPergunta(2);
                    atualizarStatusJogador();
                } else {
                    adicionarDialogoErro("❌ Ginga insuficiente! Precisa de 20 de Ginga.");
                }
            }
        });

        btnCombinado.setOnAction(e -> {
            if (inimigoAtual != null && inimigoAtual.vivo() && !aguardandoResposta) {
                if (capoeirista.getEnergiaGinga() >= 40) {
                    capoeirista.consumir(40);
                    adicionarDialogoNormal("🔥 COMBINAÇÃO MORTAL!");
                    carregarPergunta(3);
                    atualizarStatusJogador();
                } else {
                    adicionarDialogoErro("❌ Ginga insuficiente! Precisa de 40 de Ginga.");
                }
            }
        });

        btnEsquiva.setOnAction(e -> {
            if (inimigoAtual != null && inimigoAtual.vivo() && !aguardandoResposta) {
                if (capoeirista.usarEsquiva()) {
                    adicionarDialogoNormal("🌀 ESQUIVA ATIVADA! Você desviará do PRÓXIMO ataque inimigo!");
                    adicionarDialogoNormal("   ⚡ Um contra-ataque será aplicado automaticamente!");
                    atualizarStatusJogador();
                } else {
                    adicionarDialogoErro("❌ Sem esquivas disponíveis! (" + capoeirista.getEsquivasRestantes() + "/" + capoeirista.getEsquivasMaximas() + ")");
                }
            }
        });

        btnVoltarMenu.setVisible(false);
        btnVoltarMenu.setManaged(false);
    }
}