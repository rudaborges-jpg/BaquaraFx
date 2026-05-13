package com.baquara.gui.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import com.baquara.modelo.*;
import com.baquara.modelo.Pergunta.Dificuldade;
import com.baquara.controle.AvaliadorRespostas;
import com.baquara.dados.BancoPerguntas;
import com.baquara.habilidades.HabilidadeEspecial;
import java.util.Random;

public class TelaBatalhaController {

    @FXML private Label lblJogadorNome;
    @FXML private Label lblJogadorVida;
    @FXML private ProgressBar barraJogadorVida;
    @FXML private Label lblJogadorAtributo;

    @FXML private Label lblInimigoNome;
    @FXML private Label lblInimigoVida;
    @FXML private ProgressBar barraInimigoVida;

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

    private Jogador jogador;
    private Inimigo inimigoAtual;
    private BancoPerguntas bancoPerguntas;
    private Pergunta perguntaAtual;
    private int estagioAtual = 1;
    private int pontuacao = 0;
    private Random random = new Random();

    // ⭐ ESTATÍSTICAS PARA TELA FINAL
    private int perguntasCertas = 0;
    private int perguntasErradas = 0;
    private int habilidadesUsadas = 0;
    private int danoTotalCausado = 0;
    private int danoTotalRecebido = 0;
    private int rodadaAtual = 0;

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
        atualizarStatusJogador();
        adicionarDialogo("✨ " + jogador.getPersonagem().getNome() + " entrou na batalha!");

        // ⭐ Mostrar informação da habilidade
        Personagem p = jogador.getPersonagem();
        if (p.getHabilidade() != null) {
            adicionarDialogo("🌟 Habilidade: " + p.getHabilidade().getNome());
            adicionarDialogo("   " + p.getHabilidade().getDescricao());
            adicionarDialogo("   ⏱️ Cooldown: " + p.getHabilidade().getCooldown() + " rodadas");
        }

        atualizarInterfaceHabilidade();
    }

    /**
     * ⭐ Atualiza a interface baseado no cooldown REAL do personagem
     */
    private void atualizarInterfaceHabilidade() {
        Platform.runLater(() -> {
            Personagem p = jogador.getPersonagem();
            HabilidadeEspecial hab = p.getHabilidade();

            if (hab == null) {
                btnHabilidade.setDisable(true);
                lblCooldown.setText("❌ Sem habilidade");
                return;
            }

            boolean pronta = hab.estaPronta();
            int cdAtual = hab.getCooldownAtual();

            if (pronta) {
                btnHabilidade.setDisable(false);
                lblCooldown.setText("✨ PRONTA!");
                lblCooldown.setStyle("-fx-text-fill: #4CAF50;");
            } else {
                btnHabilidade.setDisable(true);
                lblCooldown.setText("⏳ Cooldown: " + cdAtual + " rodada(s)");
                lblCooldown.setStyle("-fx-text-fill: #FF9800;");
            }
        });
    }

    /**
     * ⭐ REDUZ COOLDOWN - Chamado ao final de cada rodada
     */
    private void reduzirCooldownHabilidade() {
        Personagem p = jogador.getPersonagem();
        if (p != null) {
            p.reduzirCooldownHabilidade();
            atualizarInterfaceHabilidade();
        }
    }

    /**
     * ⭐ RESETA COOLDOWN (usado entre estágios/chefões)
     */
    private void resetarCooldownHabilidade() {
        Personagem p = jogador.getPersonagem();
        if (p != null) {
            p.resetarCooldownHabilidade();
            atualizarInterfaceHabilidade();
            adicionarDialogo("✨ Cooldown da habilidade resetado!");
        }
    }

    @FXML
    public void initialize() {
        bancoPerguntas = new BancoPerguntas();
        btnHabilidade.setOnAction(e -> usarHabilidade());
    }

    public void iniciarBatalha() {
        criarProximoInimigo();
        proximaPergunta();
    }

    private void criarProximoInimigo() {
        int vidaBase = 80 + (estagioAtual * 20);
        inimigoAtual = new Inimigo("Guardião Nv." + estagioAtual, vidaBase, 20 + estagioAtual * 3, estagioAtual);
        atualizarStatusInimigo();
        adicionarDialogo("⚠️ " + inimigoAtual.getNome() + " apareceu!");
    }

    private void proximaPergunta() {
        rodadaAtual++;
        lblTurno.setText("⚔️ RODADA " + rodadaAtual + " ⚔️");

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
    }

    private void exibirAlternativas() {
        painelAlternativas.getChildren().clear();
        painelLacuna.setVisible(false);

        if (perguntaAtual instanceof PerguntaMultiplaEscolha) {
            PerguntaMultiplaEscolha p = (PerguntaMultiplaEscolha) perguntaAtual;
            var opcoes = p.getOpcoes();

            for (int i = 0; i < opcoes.size(); i++) {
                char letra = (char) ('A' + i);
                Button btn = new Button(letra + ") " + opcoes.get(i));
                btn.setStyle("-fx-background-color: #2a2a4a; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8px;");
                btn.setMaxWidth(Double.MAX_VALUE);
                final String resposta = String.valueOf(letra);
                btn.setOnAction(e -> avaliarResposta(resposta));
                painelAlternativas.getChildren().add(btn);
            }
            painelAlternativas.setVisible(true);

        } else if (perguntaAtual instanceof PerguntaVerdadeiroFalso) {
            Button btnV = new Button("V) VERDADEIRO");
            Button btnF = new Button("F) FALSO");
            btnV.setStyle("-fx-background-color: #2a2a4a; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8px;");
            btnF.setStyle("-fx-background-color: #2a2a4a; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 8px;");
            btnV.setOnAction(e -> avaliarResposta("V"));
            btnF.setOnAction(e -> avaliarResposta("F"));
            painelAlternativas.getChildren().addAll(btnV, btnF);
            painelAlternativas.setVisible(true);

        } else if (perguntaAtual instanceof PerguntaCompletarLacuna) {
            painelAlternativas.setVisible(false);
            painelLacuna.setVisible(true);
            txtRespostaLacuna.clear();
            btnEnviarLacuna.setOnAction(e -> {
                String resposta = txtRespostaLacuna.getText().trim();
                if (!resposta.isEmpty()) avaliarResposta(resposta);
            });
        }
    }

    private void avaliarResposta(String resposta) {
        boolean correta = AvaliadorRespostas.avaliar(perguntaAtual, resposta);

        if (correta) {
            perguntasCertas++;
            int dano = calcularDano(perguntaAtual.getDificuldade());
            inimigoAtual.tomarDano(dano);
            danoTotalCausado += dano;
            adicionarDialogo("✅ CORRETO! Causou " + dano + " de dano!");
            pontuacao += 100;

            // ⭐ GANHA EXPERIÊNCIA
            int exp = perguntaAtual.getDificuldade().getDanoBase() * 5;
            jogador.getPersonagem().addExperiencia(exp);
            adicionarDialogo("📚 +" + exp + " de experiência!");

            if (!inimigoAtual.vivo()) {
                adicionarDialogo("🎉 VITÓRIA! Estágio " + estagioAtual + " concluído!");

                // ⭐ Bônus de estágio
                int bonus = estagioAtual * 50;
                pontuacao += bonus;
                adicionarDialogo("🏆 Bônus de estágio: +" + bonus + " pontos!");

                estagioAtual++;

                // ⭐ Resetar cooldown e curar entre estágios
                jogador.getPersonagem().curar(30);
                resetarCooldownHabilidade();
                adicionarDialogo("✨ +30 de vida recuperada!");

                if (estagioAtual > 10) {
                    finalizarJogo(true);
                    return;
                }
                criarProximoInimigo();
            }
            atualizarStatusJogador();
            atualizarStatusInimigo();

            // ⭐ REDUZ COOLDOWN APÓS RODADA
            reduzirCooldownHabilidade();

            proximaPergunta();

        } else {
            perguntasErradas++;
            int dano = calcularDanoInimigo();
            jogador.tomarDano(dano);
            danoTotalRecebido += dano;
            adicionarDialogo("❌ ERRADO! " + inimigoAtual.getNome() + " causou " + dano + " de dano!");
            adicionarDialogo("📖 Resposta correta: " +
                    AvaliadorRespostas.getRespostaCorretaFormatada(perguntaAtual));
            atualizarStatusJogador();

            if (!jogador.vivo()) {
                finalizarJogo(false);
                return;
            }

            // ⭐ REDUZ COOLDOWN APÓS RODADA (mesmo errando)
            reduzirCooldownHabilidade();

            proximaPergunta();
        }
    }

    /**
     * ⭐ CÁLCULO DE DANO (mesmo do console)
     */
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

        int multiplicadorEstagio = estagioAtual / 2;
        if (multiplicadorEstagio < 1) multiplicadorEstagio = 1;

        int dano = danoBase * multiplicador * multiplicadorEstagio;

        double variacao = 0.85 + (random.nextDouble() * 0.3);
        dano = (int)(dano * variacao);

        dano = Math.max(10, Math.min(60, dano));

        return dano;
    }

    /**
     * ⭐ CÁLCULO DE DANO DO INIMIGO
     */
    private int calcularDanoInimigo() {
        int danoBase = inimigoAtual.getAtaque();
        int multiplicador = estagioAtual;

        if (estagioAtual % 5 == 0) { // Chefões a cada 5 estágios
            multiplicador *= 2;
        }

        int dano = danoBase + (multiplicador * 3);

        double variacao = 0.85 + (random.nextDouble() * 0.3);
        dano = (int)(dano * variacao);

        return Math.max(8, dano);
    }

    /**
     * ⭐ USAR HABILIDADE - Usa o sistema do console
     */
    private void usarHabilidade() {
        Personagem p = jogador.getPersonagem();
        HabilidadeEspecial hab = p.getHabilidade();

        if (hab == null) {
            adicionarDialogo("❌ Seu personagem não possui habilidade especial!");
            return;
        }

        if (!hab.estaPronta()) {
            adicionarDialogo("⏳ Habilidade em cooldown! Aguarde " +
                    hab.getCooldownAtual() + " rodada(s).");
            return;
        }

        adicionarDialogo("🌟 " + p.getNome() + " usa " + hab.getNome() + "!");
        adicionarDialogo("📖 " + hab.getDescricao());

        habilidadesUsadas++;
        int dano = hab.executar(inimigoAtual);
        danoTotalCausado += dano;

        adicionarDialogo("💥 Causou " + dano + " de dano devastador!");
        atualizarStatusInimigo();

        // ⭐ Atualiza interface do cooldown
        atualizarInterfaceHabilidade();

        if (!inimigoAtual.vivo()) {
            adicionarDialogo("🎉 VITÓRIA! Estágio " + estagioAtual + " concluído!");

            int bonus = estagioAtual * 50;
            pontuacao += bonus;
            adicionarDialogo("🏆 Bônus de estágio: +" + bonus + " pontos!");

            estagioAtual++;

            jogador.getPersonagem().curar(30);
            resetarCooldownHabilidade();
            adicionarDialogo("✨ +30 de vida recuperada!");

            if (estagioAtual > 10) {
                finalizarJogo(true);
                return;
            }
            criarProximoInimigo();
        }

        proximaPergunta();
    }

    private void adicionarDialogo(String msg) {
        Platform.runLater(() -> {
            String atual = txtDialogo.getText();
            txtDialogo.setText(msg + "\n" + (atual.length() > 300 ? "" : atual));
        });
    }

    private void atualizarStatusJogador() {
        Platform.runLater(() -> {
            Personagem p = jogador.getPersonagem();
            lblJogadorNome.setText(p.getNome());
            lblJogadorVida.setText("HP: " + p.getVida() + "/" + p.getVidaMax());
            barraJogadorVida.setProgress((double) p.getVida() / p.getVidaMax());

            if (p instanceof AtributoEspecial) {
                AtributoEspecial attr = (AtributoEspecial) p;
                lblJogadorAtributo.setText(attr.getNomeAtributo() + ": " + attr.getValorAtual() + "/" + attr.getValorMaximo());
            }
        });
    }

    private void atualizarStatusInimigo() {
        Platform.runLater(() -> {
            lblInimigoNome.setText(inimigoAtual.getNome());
            lblInimigoVida.setText("HP: " + inimigoAtual.getVida() + "/" + inimigoAtual.getVidaMax());
            barraInimigoVida.setProgress((double) inimigoAtual.getVida() / inimigoAtual.getVidaMax());
        });
    }

    private void finalizarJogo(boolean vitoria) {
        Platform.runLater(() -> {
            try {
                // ⭐ Coleta estatísticas para a tela de resultado
                java.util.Map<String, Object> stats = new java.util.HashMap<>();
                stats.put("pontuacao", pontuacao);
                stats.put("rodadas", rodadaAtual);
                stats.put("acertos", perguntasCertas);
                stats.put("erros", perguntasErradas);
                stats.put("danoCausado", danoTotalCausado);
                stats.put("danoRecebido", danoTotalRecebido);
                stats.put("habilidadesUsadas", habilidadesUsadas);
                stats.put("estagiosCompletados", estagioAtual - 1);
                stats.put("modoJogo", "Batalha Normal");

                // Carrega tela de resultado
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                        getClass().getResource("/fxml/tela-resultado.fxml")
                );
                javafx.scene.Parent root = loader.load();

                TelaResultadoController controller = loader.getController();
                controller.setDados(jogador, stats, vitoria);

                javafx.stage.Stage stage = (javafx.stage.Stage) btnHabilidade.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
                stage.setTitle("Baquara - Resultado");

            } catch (Exception e) {
                e.printStackTrace();
                // Fallback caso a tela de resultado não exista
                if (vitoria) {
                    adicionarDialogo("\n🏆 PARABÉNS! VOCÊ VENCEU O JOGO! 🏆");
                } else {
                    adicionarDialogo("\n💀 GAME OVER! 💀");
                }
                adicionarDialogo("🎉 Pontuação final: " + pontuacao);
                btnHabilidade.setDisable(true);
            }
        });
    }
}