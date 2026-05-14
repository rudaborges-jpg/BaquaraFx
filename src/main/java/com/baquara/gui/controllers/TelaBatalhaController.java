package com.baquara.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.baquara.modelo.*;
import com.baquara.modelo.Pergunta.Dificuldade;
import com.baquara.controle.AvaliadorRespostas;
import com.baquara.dados.BancoPerguntas;
import com.baquara.habilidades.HabilidadeEspecial;
import java.util.*;

public class TelaBatalhaController {

    // Componentes
    @FXML private Label lblJogadorNome;
    @FXML private Label lblJogadorNivel;
    @FXML private Label lblJogadorVida;
    @FXML private ProgressBar barraJogadorVida;
    @FXML private Label lblJogadorAtributo;
    @FXML private Label lblJogadorSprite;

    @FXML private Label lblInimigoNome;
    @FXML private Label lblInimigoNivel;
    @FXML private Label lblInimigoVida;
    @FXML private ProgressBar barraInimigoVida;
    @FXML private Label lblInimigoSprite;

    @FXML private Label lblDificuldade;
    @FXML private TextArea txtPergunta;
    @FXML private TextArea txtDialogo;
    @FXML private TextArea txtDialogoPergunta;
    @FXML private VBox painelAlternativas;
    @FXML private VBox painelLacuna;
    @FXML private VBox painelDialogo;
    @FXML private TextField txtRespostaLacuna;
    @FXML private Button btnEnviarLacuna;
    @FXML private Label lblTurno;
    @FXML private Label lblTurnoMsg;

    @FXML private Button btnAtacar;
    @FXML private Button btnHabilidade;
    @FXML private Label lblCooldown;

    @FXML private VBox painelMenu;
    @FXML private VBox painelPergunta;
    @FXML private VBox painelAcoes;  // <-- NOVO: painel dos botões de ação
    @FXML private Button btnVoltarMenu;

    private Jogador jogador;
    private Inimigo inimigoAtual;
    private BancoPerguntas bancoPerguntas;
    private Pergunta perguntaAtual;
    private int estagioAtual = 1;
    private int pontuacaoTotal = 0;
    private Random random = new Random();
    private boolean aguardandoResposta = false;

    // Estatísticas
    private int perguntasCertas = 0;
    private int perguntasErradas = 0;
    private int habilidadesUsadas = 0;
    private int danoTotalCausado = 0;
    private int danoTotalRecebido = 0;
    private int rodadaAtual = 0;

    // Controle de perguntas
    private Set<Integer> perguntasUsadas = new HashSet<>();
    private Map<Dificuldade, List<Pergunta>> perguntasDisponiveis = new HashMap<>();

    // Sprites
    private final Map<PerTipo, String> sprites = new HashMap<>();
    {
        sprites.put(PerTipo.PALADINO, "🛡️");
        sprites.put(PerTipo.GUERREIRO, "⚔️");
        sprites.put(PerTipo.CACADORA, "🏹");
        sprites.put(PerTipo.SABIO, "📚");
        sprites.put(PerTipo.ARCANISTA, "🔮");
        sprites.put(PerTipo.CAPOEIRISTA, "🥋");
    }

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
        this.bancoPerguntas = new BancoPerguntas();

        inicializarHabilidadeDoPersonagem();
        atualizarStatusJogador();

        String sprite = sprites.getOrDefault(jogador.getPersonagem().getTipo(), "⚔️");
        lblJogadorSprite.setText(sprite);

        adicionarDialogo("✨ " + jogador.getPersonagem().getNome() + " entrou na batalha!");

        Personagem p = jogador.getPersonagem();
        if (p.getHabilidade() != null) {
            adicionarDialogo("🌟 Habilidade: " + p.getHabilidade().getNome());
            adicionarDialogo("   " + p.getHabilidade().getDescricao());
        }

        atualizarInterfaceHabilidade();
    }

    private void inicializarHabilidadeDoPersonagem() {
        Personagem p = jogador.getPersonagem();

        if (p instanceof Paladino && p.getHabilidade() == null) {
            p.setHabilidade(new com.baquara.habilidades.HabilidadeCura(p, 8));
        } else if (p instanceof Guerreiro && p.getHabilidade() == null) {
            p.setHabilidade(new com.baquara.habilidades.HabilidadeDanoExtra(p, 8));
        } else if (p instanceof Cacadora && p.getHabilidade() == null) {
            p.setHabilidade(new com.baquara.habilidades.HabilidadeCritico(p, 8));
        } else if (p instanceof Sabio && p.getHabilidade() == null) {
            p.setHabilidade(new com.baquara.habilidades.HabilidadePoderMagico(p, 8));
        } else if (p instanceof Arcanista && p.getHabilidade() == null) {
            p.setHabilidade(new com.baquara.habilidades.HabilidadeDestruicaoTotal(p, 8));
        }
    }

    private void atualizarInterfaceHabilidade() {
        Platform.runLater(() -> {
            Personagem p = jogador.getPersonagem();
            HabilidadeEspecial hab = p.getHabilidade();

            if (hab == null) {
                btnHabilidade.setDisable(true);
                lblCooldown.setText("❌ Sem habilidade");
                return;
            }

            if (hab.estaPronta()) {
                btnHabilidade.setDisable(false);
                lblCooldown.setText("✨ PRONTA!");
                lblCooldown.setStyle("-fx-text-fill: #4CAF50;");
            } else {
                btnHabilidade.setDisable(true);
                lblCooldown.setText("⏳ Cooldown: " + hab.getCooldownAtual() + " rodada(s)");
                lblCooldown.setStyle("-fx-text-fill: #FF9800;");
            }
        });
    }

    private void reduzirCooldown() {
        Personagem p = jogador.getPersonagem();
        if (p != null && p.getHabilidade() != null) {
            p.reduzirCooldownHabilidade();
            atualizarInterfaceHabilidade();
        }
    }

    @FXML
    public void initialize() {
        btnAtacar.setOnAction(e -> mostrarPainelPergunta());
        btnHabilidade.setOnAction(e -> usarHabilidade());
        btnVoltarMenu.setOnAction(e -> voltarAoMenu());

        // Configura ENTER para responder na lacuna
        txtRespostaLacuna.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String resposta = txtRespostaLacuna.getText().trim();
                if (!resposta.isEmpty() && perguntaAtual instanceof PerguntaCompletarLacuna) {
                    avaliarResposta(resposta);
                }
            }
        });
    }

    public void iniciarBatalha() {
        criarProximoInimigo();
        mostrarMenuPrincipal();
    }

    private void mostrarMenuPrincipal() {
        painelDialogo.setVisible(true);
        painelDialogo.setManaged(true);
        painelAcoes.setVisible(true);
        painelAcoes.setManaged(true);

        // Esconde painel de pergunta
        painelPergunta.setVisible(false);
        painelPergunta.setManaged(false);

        lblTurnoMsg.setText("⚔️ Escolha sua ação! ⚔️");
        aguardandoResposta = false;
    }

    private void voltarAoMenu() {
        aguardandoResposta = false;
        mostrarMenuPrincipal();
    }

    private void mostrarPainelPergunta() {
        painelDialogo.setVisible(false);
        painelDialogo.setManaged(false);
        painelAcoes.setVisible(false);
        painelAcoes.setManaged(false);

        // Mostra painel de pergunta
        painelPergunta.setVisible(true);
        painelPergunta.setManaged(true);

        rodadaAtual++;
        lblTurno.setText("⚔️ RODADA " + rodadaAtual + " ⚔️");

        Dificuldade dificuldade;
        String nomeDificuldade;

        if (estagioAtual <= 3) {
            dificuldade = Dificuldade.FACIL;
            nomeDificuldade = "⭐ FÁCIL";
        } else if (estagioAtual <= 7) {
            dificuldade = Dificuldade.MEDIO;
            nomeDificuldade = "⭐⭐ MÉDIO";
        } else {
            dificuldade = Dificuldade.DIFICIL;
            nomeDificuldade = "⭐⭐⭐ DIFÍCIL";
        }

        lblDificuldade.setText(nomeDificuldade);

        perguntaAtual = getPerguntaSemRepeticao(dificuldade);

        if (perguntaAtual == null) {
            txtPergunta.setText("Nenhuma pergunta disponível!");
            return;
        }

        txtDialogoPergunta.setText("📚 " + jogador.getPersonagem().getNome() + " prepara o ataque!");
        txtPergunta.setText(perguntaAtual.getTexto());
        exibirAlternativas();
    }

    private void exibirAlternativas() {
        painelAlternativas.getChildren().clear();
        painelLacuna.setVisible(false);
        painelLacuna.setManaged(false);
        txtRespostaLacuna.clear();
        aguardandoResposta = true;

        if (perguntaAtual instanceof PerguntaMultiplaEscolha) {
            PerguntaMultiplaEscolha p = (PerguntaMultiplaEscolha) perguntaAtual;
            var opcoes = p.getOpcoes();

            for (int i = 0; i < opcoes.size(); i++) {
                char letra = (char) ('A' + i);
                Button btn = new Button(letra + ") " + opcoes.get(i));
                btn.setStyle("-fx-background-color: #306830; -fx-text-fill: white; -fx-font-size: 12px; -fx-padding: 8px; -fx-background-radius: 8;");
                btn.setMaxWidth(Double.MAX_VALUE);
                final String resposta = String.valueOf(letra);
                btn.setOnAction(e -> avaliarResposta(resposta));
                painelAlternativas.getChildren().add(btn);
            }

        } else if (perguntaAtual instanceof PerguntaVerdadeiroFalso) {
            HBox hboxVF = new HBox(10.0);
            hboxVF.setAlignment(javafx.geometry.Pos.CENTER);

            Button btnV = new Button("✅ VERDADEIRO");
            Button btnF = new Button("❌ FALSO");

            String estilo = "-fx-background-color: #306830; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 10px 20px; -fx-background-radius: 8;";
            btnV.setStyle(estilo);
            btnF.setStyle(estilo);

            btnV.setOnAction(e -> avaliarResposta("V"));
            btnF.setOnAction(e -> avaliarResposta("F"));

            hboxVF.getChildren().addAll(btnV, btnF);
            painelAlternativas.getChildren().add(hboxVF);

        } else if (perguntaAtual instanceof PerguntaCompletarLacuna) {
            painelLacuna.setVisible(true);
            painelLacuna.setManaged(true);
            txtRespostaLacuna.requestFocus();
        }
    }

    private void avaliarResposta(String resposta) {
        if (!aguardandoResposta) return;
        aguardandoResposta = false;

        boolean correta = AvaliadorRespostas.avaliar(perguntaAtual, resposta);

        if (correta) {
            perguntasCertas++;
            int dano = calcularDano(perguntaAtual.getDificuldade());
            inimigoAtual.tomarDano(dano);
            danoTotalCausado += dano;
            adicionarDialogo("✅ CORRETO! Causou " + dano + " de dano!");
            pontuacaoTotal += 100;
            jogador.addPontuacao(100);

            int exp = perguntaAtual.getDificuldade().getDanoBase() * 5;
            jogador.getPersonagem().addExperiencia(exp);

            if (!inimigoAtual.vivo()) {
                adicionarDialogo("🎉 VITÓRIA! Estágio " + estagioAtual + " concluído!");

                int bonus = estagioAtual * 50;
                pontuacaoTotal += bonus;
                jogador.addPontuacao(bonus);

                estagioAtual++;

                jogador.getPersonagem().curar(30);
                adicionarDialogo("✨ +30 de vida recuperada!");

                if (estagioAtual > 10) {
                    finalizarJogo(true);
                    return;
                }
                criarProximoInimigo();
            }
            atualizarStatusJogador();
            atualizarStatusInimigo();

            reduzirCooldown();
            voltarAoMenu();

        } else {
            perguntasErradas++;
            int dano = calcularDanoInimigo();
            jogador.tomarDano(dano);
            danoTotalRecebido += dano;
            adicionarDialogo("❌ ERRADO! " + inimigoAtual.getNome() + " causou " + dano + " de dano!");
            adicionarDialogo("📖 Resposta correta: " + AvaliadorRespostas.getRespostaCorretaFormatada(perguntaAtual));
            atualizarStatusJogador();

            if (!jogador.vivo()) {
                finalizarJogo(false);
                return;
            }

            reduzirCooldown();
            voltarAoMenu();
        }
    }

    private void criarProximoInimigo() {
        int vidaBase = 80 + (estagioAtual * 20);
        int ataqueBase = 20 + estagioAtual * 3;
        inimigoAtual = new Inimigo("Guardião Nv." + estagioAtual, vidaBase, ataqueBase, estagioAtual);
        lblInimigoSprite.setText(getInimigoSprite(estagioAtual));
        atualizarStatusInimigo();
        adicionarDialogo("⚠️ " + inimigoAtual.getNome() + " apareceu!");
    }

    private String getInimigoSprite(int estagio) {
        String[] sprites = {"👾", "🐉", "👹", "🧙", "🦇", "🐺", "🧟", "👑"};
        return sprites[Math.min(estagio - 1, sprites.length - 1)];
    }

    private Pergunta getPerguntaSemRepeticao(Dificuldade dificuldade) {
        PerTipo tipo = jogador.getPersonagem().getTipo();

        if (!perguntasDisponiveis.containsKey(dificuldade)) {
            List<Pergunta> todas = bancoPerguntas.getPerguntasPorDificuldade(tipo, dificuldade, estagioAtual);
            if (todas == null || todas.isEmpty()) return null;
            perguntasDisponiveis.put(dificuldade, new ArrayList<>(todas));
        }

        List<Pergunta> disponiveis = perguntasDisponiveis.get(dificuldade);
        List<Pergunta> naoUsadas = new ArrayList<>();

        for (Pergunta p : disponiveis) {
            if (!perguntasUsadas.contains(p.getId())) {
                naoUsadas.add(p);
            }
        }

        if (naoUsadas.isEmpty()) {
            perguntasUsadas.clear();
            naoUsadas = new ArrayList<>(disponiveis);
        }

        Pergunta escolhida = naoUsadas.get(random.nextInt(naoUsadas.size()));
        perguntasUsadas.add(escolhida.getId());

        return escolhida;
    }

    private int calcularDano(Dificuldade diff) {
        Personagem p = jogador.getPersonagem();
        int danoBase = p.getAtaque();

        int multiplicador;
        switch (diff) {
            case FACIL: multiplicador = 1; break;
            case MEDIO: multiplicador = 2; break;
            case DIFICIL: multiplicador = 3; break;
            default: multiplicador = 1;
        }

        int multiplicadorEstagio = Math.max(1, estagioAtual / 2);
        int dano = danoBase * multiplicador * multiplicadorEstagio;

        double variacao = 0.85 + (random.nextDouble() * 0.3);
        dano = (int)(dano * variacao);

        return Math.max(10, Math.min(60, dano));
    }

    private int calcularDanoInimigo() {
        int danoBase = inimigoAtual.getAtaque();
        int multiplicador = estagioAtual;

        if (estagioAtual % 5 == 0) multiplicador *= 2;

        int dano = danoBase + (multiplicador * 3);
        double variacao = 0.85 + (random.nextDouble() * 0.3);
        dano = (int)(dano * variacao);

        return Math.max(8, dano);
    }

    private void usarHabilidade() {
        Personagem p = jogador.getPersonagem();
        HabilidadeEspecial hab = p.getHabilidade();

        if (hab == null) {
            adicionarDialogo("❌ Seu personagem não possui habilidade especial!");
            return;
        }

        if (!hab.estaPronta()) {
            adicionarDialogo("⏳ Habilidade em cooldown! Aguarde " + hab.getCooldownAtual() + " rodada(s).");
            return;
        }

        adicionarDialogo("🌟 " + p.getNome() + " usa " + hab.getNome() + "!");

        habilidadesUsadas++;
        int dano = hab.executar(inimigoAtual);
        danoTotalCausado += dano;

        adicionarDialogo("💥 Causou " + dano + " de dano devastador!");
        atualizarStatusInimigo();
        atualizarInterfaceHabilidade();

        if (!inimigoAtual.vivo()) {
            adicionarDialogo("🎉 VITÓRIA! Estágio " + estagioAtual + " concluído!");

            int bonus = estagioAtual * 50;
            pontuacaoTotal += bonus;
            jogador.addPontuacao(bonus);

            estagioAtual++;
            jogador.getPersonagem().curar(30);

            if (estagioAtual > 10) {
                finalizarJogo(true);
                return;
            }
            criarProximoInimigo();
        }

        reduzirCooldown();
        voltarAoMenu();
    }

    private void adicionarDialogo(String msg) {
        Platform.runLater(() -> {
            String atual = txtDialogo.getText();
            txtDialogo.setText(msg + "\n" + (atual.length() > 500 ? atual.substring(0, 500) : atual));
        });
    }

    private void atualizarStatusJogador() {
        Platform.runLater(() -> {
            Personagem p = jogador.getPersonagem();
            lblJogadorNome.setText(p.getNome());
            lblJogadorNivel.setText("Nv. " + p.getNivel());
            lblJogadorVida.setText("HP: " + p.getVida() + "/" + p.getVidaMax());
            barraJogadorVida.setProgress((double) p.getVida() / p.getVidaMax());

            if (p instanceof AtributoEspecial attr) {
                lblJogadorAtributo.setText(attr.getNomeAtributo() + ": " + attr.getValorAtual() + "/" + attr.getValorMaximo());
            }
        });
    }

    private void atualizarStatusInimigo() {
        Platform.runLater(() -> {
            lblInimigoNome.setText(inimigoAtual.getNome());
            lblInimigoNivel.setText("Nv. " + inimigoAtual.getNivel());
            lblInimigoVida.setText("HP: " + inimigoAtual.getVida() + "/" + inimigoAtual.getVidaMax());
            barraInimigoVida.setProgress((double) inimigoAtual.getVida() / inimigoAtual.getVidaMax());
        });
    }

    private void finalizarJogo(boolean vitoria) {
        Platform.runLater(() -> {
            try {
                Map<String, Object> stats = new HashMap<>();
                stats.put("pontuacao", pontuacaoTotal);
                stats.put("rodadas", rodadaAtual);
                stats.put("acertos", perguntasCertas);
                stats.put("erros", perguntasErradas);
                stats.put("danoCausado", danoTotalCausado);
                stats.put("danoRecebido", danoTotalRecebido);
                stats.put("habilidadesUsadas", habilidadesUsadas);
                stats.put("estagiosCompletados", estagioAtual - 1);
                stats.put("modoJogo", "Batalha Normal");

                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/fxml/tela-resultado.fxml")
                );
                javafx.scene.Parent root = loader.load();

                TelaResultadoController controller = loader.getController();
                controller.setDados(jogador, stats, vitoria);

                javafx.stage.Stage stage = (javafx.stage.Stage) btnAtacar.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
                stage.setTitle("Baquara - Resultado");

            } catch (Exception e) {
                e.printStackTrace();
                if (vitoria) {
                    adicionarDialogo("\n🏆 PARABÉNS! VOCÊ VENCEU O JOGO! 🏆");
                } else {
                    adicionarDialogo("\n💀 GAME OVER! 💀");
                }
                adicionarDialogo("🎉 Pontuação final: " + pontuacaoTotal);
            }
        });
    }
}