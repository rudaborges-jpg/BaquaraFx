package com.baquara.gui.controllers;

import com.baquara.controle.AvaliadorRespostas;
import com.baquara.controle.GerenciadorRotas;
import com.baquara.dados.BancoPerguntas;
import com.baquara.modelo.*;
import com.baquara.modelo.Pergunta.Dificuldade;
import com.baquara.habilidades.HabilidadeEspecial;
import com.baquara.modelo.efeitos.DebuffAtaqueEfeito;
import com.baquara.modelo.efeitos.SangramentoEfeito;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.*;

public class TelaBatalhaController {

    @FXML private Label lblJogadorNome;
    @FXML private Label lblJogadorNivel;
    @FXML private Label lblJogadorVida;
    @FXML private ProgressBar barraJogadorVida;
    @FXML private Label lblJogadorAtributo;
    @FXML private Label lblJogadorSprite;
    @FXML private Label lblJogadorStatus;
    @FXML private Label lblJogadorDefesa;

    @FXML private Label lblInimigoNome;
    @FXML private Label lblInimigoNivel;
    @FXML private Label lblInimigoVida;
    @FXML private ProgressBar barraInimigoVida;
    @FXML private Label lblInimigoSprite;
    @FXML private Label lblInimigoStatus;
    @FXML private Label lblInimigoDefesa;
    @FXML private Label lblInimigoEfeitos;
    @FXML private Label lblSangramentoIcon;

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
    @FXML private VBox painelAcoes;
    @FXML private Button btnVoltarMenu;

    private Jogador jogador;
    private Inimigo inimigoAtual;
    private BancoPerguntas bancoPerguntas;
    private Pergunta perguntaAtual;
    private int estagioAtualNumero = 1;
    private int pontuacaoTotal = 0;
    private Random random = new Random();
    private boolean aguardandoResposta = false;

    private GerenciadorRotas gerenciadorRotas;
    private Rota rotaAtual;
    private int estagioIndex = 0;
    private List<Estagio> estagios;

    private int perguntasCertas = 0;
    private int perguntasErradas = 0;
    private int habilidadesUsadas = 0;
    private int danoTotalCausado = 0;
    private int danoTotalRecebido = 0;
    private int rodadaAtual = 0;

    private Set<Integer> perguntasUsadas = new HashSet<>();
    private Map<Dificuldade, List<Pergunta>> perguntasDisponiveis = new HashMap<>();

    private final Map<PerTipo, String> sprites = new HashMap<>();
    {
        sprites.put(PerTipo.PALADINO, "🛡️");
        sprites.put(PerTipo.GUERREIRO, "⚔️");
        sprites.put(PerTipo.CACADORA, "🏹");
        sprites.put(PerTipo.SABIO, "📚");
        sprites.put(PerTipo.ARCANISTA, "🔮");
        sprites.put(PerTipo.CAPOEIRISTA, "🥋");
    }

    // ==================== PROCESSAMENTO DE EFEITOS ====================

    /**
     * ⭐ PROCESSAMENTO DE INÍCIO DE RODADA
     * Deve ser chamado ANTES do jogador escolher sua ação
     * Aqui que o sangramento e outros efeitos causam dano!
     */
    private void processarInicioRodada() {
        if (inimigoAtual == null || !inimigoAtual.vivo()) return;

        Platform.runLater(() -> {
            adicionarDialogoNormal("\n🔥 INÍCIO DA RODADA - PROCESSANDO EFEITOS...");
        });

        // Verifica se o inimigo tem sangramento ANTES de processar
        SangramentoEfeito sangramento = inimigoAtual.getEfeito(SangramentoEfeito.class);
        boolean tinhaSangramento = (sangramento != null && sangramento.estaAtivo());

        if (tinhaSangramento) {
            int vidaAntes = inimigoAtual.getVida();
            int danoEsperado = sangramento.getDanoPorRodada();

            adicionarDialogoEfeito("🩸 " + inimigoAtual.getNome() + " está sangrando! Perderá " +
                    danoEsperado + " de vida esta rodada!");
            piscarBarraVidaInimigo(Color.RED);
            animarIconeSangramento();
        }

        // ⭐ ATUALIZA OS EFEITOS - É AQUI QUE O DANO É APLICADO!
        inimigoAtual.atualizarEfeitos();

        // Verifica se o inimigo morreu pelo sangramento
        if (!inimigoAtual.vivo()) {
            adicionarDialogoAcerto("\n💀🩸 " + inimigoAtual.getNome() + " MORREU PELO SANGRAMENTO! 🩸💀");
            Platform.runLater(() -> {
                lblInimigoSprite.setStyle("-fx-effect: dropshadow(gaussian, red, 20, 0.7, 0, 0);");
            });

            // Atualiza status e finaliza se necessário
            atualizarStatusInimigoComAnimacao();
            atualizarStatusDetalhadoInimigo();

            // Processa vitória do estágio
            Estagio estagioAtualObj = estagios.get(estagioIndex);
            String nomeEstagio = estagioAtualObj.getNome();

            if (estagioAtualObj.ehChefao()) {
                adicionarDialogoAcerto("\n🎉🎉🎉 CHEFÃO DERROTADO! 🎉🎉🎉");
                adicionarDialogoAcerto("👑 " + inimigoAtual.getNome() + " foi vencido!");
                adicionarDialogoAcerto("🏆 Estágio " + (estagioIndex + 1) + " concluído!");
            } else {
                adicionarDialogoAcerto("\n🎉 VITÓRIA! " + inimigoAtual.getNome() + " foi derrotado!");
                adicionarDialogoAcerto("🏆 Estágio " + (estagioIndex + 1) + ": " + nomeEstagio + " concluído!");
            }

            int bonus = (estagioIndex + 1) * 50;
            pontuacaoTotal += bonus;
            jogador.addPontuacao(bonus);
            adicionarDialogoNormal("🏆 Bônus de estágio: +" + bonus + " pontos!");

            avancarEstagio();
            return;
        }

        // Atualiza a interface com a nova vida
        atualizarStatusInimigo();
        atualizarStatusDetalhadoInimigo();

        // Mostra quanto dano o sangramento causou
        if (tinhaSangramento) {
            SangramentoEfeito sangAtual = inimigoAtual.getEfeito(SangramentoEfeito.class);
            if (sangAtual != null && sangAtual.getDanoTotalCausado() > 0) {
                adicionarDialogoNormal("📊 Dano total do sangramento até agora: " +
                        sangAtual.getDanoTotalCausado());
            }

            // Mostra quantas rodadas faltam
            if (sangAtual != null && sangAtual.estaAtivo()) {
                adicionarDialogoNormal("🩸 Sangramento ativo por mais " +
                        sangAtual.getDuracaoRestante() + " rodada(s)");
            } else if (sangAtual == null || !sangAtual.estaAtivo()) {
                adicionarDialogoNormal("✨ O sangramento de " + inimigoAtual.getNome() + " cessou!");
            }
        }

        // Processa outros efeitos (debuff de ataque, etc)
        DebuffAtaqueEfeito debuff = inimigoAtual.getEfeito(DebuffAtaqueEfeito.class);
        if (debuff != null && debuff.estaAtivo()) {
            adicionarDialogoNormal("🔻 " + inimigoAtual.getNome() + " está com ataque reduzido! (" +
                    debuff.getDuracaoRestante() + " rodadas restantes)");
        }
    }

    // ==================== EFEITOS VISUAIS ====================

    private void piscarBarraVidaInimigo(Color cor) {
        Platform.runLater(() -> {
            String estiloOriginal = barraInimigoVida.getStyle();
            String corHex = String.format("#%02X%02X%02X",
                    (int)(cor.getRed() * 255),
                    (int)(cor.getGreen() * 255),
                    (int)(cor.getBlue() * 255));
            barraInimigoVida.setStyle("-fx-accent: " + corHex + ";");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> barraInimigoVida.setStyle(estiloOriginal));
                }
            }, 300);
        });
    }

    private void piscarDanoSangramento(int dano) {
        Platform.runLater(() -> {
            String estiloOriginal = lblInimigoVida.getStyle();
            lblInimigoVida.setStyle("-fx-text-fill: #ff0000; -fx-font-weight: bold; -fx-font-size: 14px;");
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> lblInimigoVida.setStyle(estiloOriginal));
                }
            }, 500);
        });
    }

    private void atualizarStatusInimigoComAnimacao() {
        Platform.runLater(() -> {
            double progresso = (double) inimigoAtual.getVida() / inimigoAtual.getVidaMax();
            Timeline timeline = new Timeline();
            KeyValue kv = new KeyValue(barraInimigoVida.progressProperty(), progresso);
            KeyFrame kf = new KeyFrame(Duration.millis(300), kv);
            timeline.getKeyFrames().add(kf);
            timeline.play();
            lblInimigoVida.setText("HP: " + inimigoAtual.getVida() + "/" + inimigoAtual.getVidaMax());
        });
    }

    private void animarIconeSangramento() {
        Platform.runLater(() -> {
            if (lblSangramentoIcon != null) {
                lblSangramentoIcon.setText("🩸");
                lblSangramentoIcon.setVisible(true);
                lblSangramentoIcon.setStyle("-fx-effect: dropshadow(gaussian, red, 10, 0.5, 0, 0);");
                FadeTransition ft = new FadeTransition(Duration.millis(1000), lblSangramentoIcon);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                ft.setOnFinished(e -> lblSangramentoIcon.setVisible(false));
                ft.play();
            }
        });
    }

    // ==================== MÉTODOS DE DIÁLOGO COM ESTILOS ====================

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

    private void adicionarDialogoSangramento(String msg) {
        Platform.runLater(() -> {
            String atual = txtDialogo.getText();
            txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #330000; -fx-text-fill: #ff4444; -fx-font-weight: bold; -fx-border-color: #ff0000; -fx-border-width: 2; -fx-border-radius: 5;");
            txtDialogo.setText("🩸 " + msg + " 🩸\n" + (atual.length() > 500 ? atual.substring(0, 500) : atual));
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> {
                        if (txtDialogo.getText().contains(msg)) {
                            txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #FFF8DC; -fx-text-fill: #000000; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
                        }
                    });
                }
            }, 2000);
        });
    }

    private void adicionarDialogoEfeito(String msg) {
        Platform.runLater(() -> {
            String atual = txtDialogo.getText();
            txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #1a0000; -fx-text-fill: #ff6666; -fx-font-weight: bold; -fx-border-color: #ff4444; -fx-border-width: 2; -fx-border-radius: 5;");
            txtDialogo.setText("⚠️ " + msg + " ⚠️\n" + (atual.length() > 500 ? atual.substring(0, 500) : atual));
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

    // ==================== MÉTODOS DE STATUS ====================

    private void atualizarStatusDetalhadoInimigo() {
        Platform.runLater(() -> {
            if (inimigoAtual == null) return;

            double reducao = (double) inimigoAtual.getDefesa() / (inimigoAtual.getDefesa() + 50);
            int percentualReducao = (int)(reducao * 100);

            if (lblInimigoStatus != null) {
                lblInimigoStatus.setText("⚔️ Ataque: " + inimigoAtual.getAtaque());
            }
            if (lblInimigoDefesa != null) {
                lblInimigoDefesa.setText("🛡️ Defesa: " + inimigoAtual.getDefesa() + " (-" + percentualReducao + "% dano)");
            }

            StringBuilder efeitos = new StringBuilder();
            boolean temEfeitoAtivo = false;

            SangramentoEfeito sangramento = inimigoAtual.getEfeito(SangramentoEfeito.class);
            if (sangramento != null && sangramento.estaAtivo()) {
                efeitos.append("🩸💔 SANGRAMENTO (").append(sangramento.getDuracaoRestante()).append(" rodadas) 💔🩸");
                temEfeitoAtivo = true;
                Platform.runLater(() -> lblInimigoSprite.setStyle("-fx-effect: dropshadow(gaussian, red, 15, 0.5, 0, 0);"));
            } else {
                Platform.runLater(() -> lblInimigoSprite.setStyle(""));
            }

            DebuffAtaqueEfeito debuff = inimigoAtual.getEfeito(DebuffAtaqueEfeito.class);
            if (debuff != null && debuff.estaAtivo()) {
                if (temEfeitoAtivo) efeitos.append(" | ");
                efeitos.append("🔻 ATAQUE REDUZIDO (").append(debuff.getDuracaoRestante()).append(" rodadas) 🔻");
                temEfeitoAtivo = true;
            }

            if (lblInimigoEfeitos != null) {
                if (temEfeitoAtivo) {
                    lblInimigoEfeitos.setText(efeitos.toString());
                    lblInimigoEfeitos.setVisible(true);
                    lblInimigoEfeitos.setStyle("-fx-text-fill: #ff4444; -fx-font-weight: bold; -fx-font-size: 11px;");
                } else {
                    lblInimigoEfeitos.setVisible(false);
                    lblInimigoEfeitos.setStyle("");
                }
            }
        });
    }

    private void atualizarStatusDetalhadoJogador() {
        Platform.runLater(() -> {
            Personagem p = jogador.getPersonagem();
            double reducao = (double) p.getDefesa() / (p.getDefesa() + 50);
            int percentualReducao = (int)(reducao * 100);
            if (lblJogadorStatus != null) {
                lblJogadorStatus.setText("⚔️ Ataque: " + p.getAtaque());
            }
            if (lblJogadorDefesa != null) {
                lblJogadorDefesa.setText("🛡️ Defesa: " + p.getDefesa() + " (-" + percentualReducao + "% dano)");
            }
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
            lblInimigoNivel.setText("Nv. " + estagioAtualNumero);
            lblInimigoVida.setText("HP: " + inimigoAtual.getVida() + "/" + inimigoAtual.getVidaMax());
            barraInimigoVida.setProgress((double) inimigoAtual.getVida() / inimigoAtual.getVidaMax());
        });
    }

    // ==================== AVALIAÇÃO DE RESPOSTA ====================

    private void avaliarResposta(String resposta) {
        if (!aguardandoResposta) return;
        aguardandoResposta = false;

        boolean correta = AvaliadorRespostas.avaliar(perguntaAtual, resposta);

        if (correta) {
            perguntasCertas++;
            Personagem p = jogador.getPersonagem();
            HabilidadeEspecial hab = p.getHabilidade();

            if (hab != null) {
                hab.recarregarAposAcerto();
                adicionarDialogoNormal("✨ +" + getRecuperacaoHabilidade(p) + " de " +
                        ((AtributoEspecial) p).getNomeAtributo() + " recuperado!");
                atualizarStatusJogador();
                atualizarBotaoHabilidade();
            }

            int danoBruto = calcularDano(perguntaAtual.getDificuldade());
            int defesaInimigo = inimigoAtual.getDefesa();
            double reducao = (double) defesaInimigo / (defesaInimigo + 50);
            int danoMitigado = (int) (danoBruto * reducao);
            int danoFinal = danoBruto - danoMitigado;
            if (danoFinal < 1) danoFinal = 1;

            int vidaAntes = inimigoAtual.getVida();
            inimigoAtual.tomarDano(danoFinal);
            danoTotalCausado += danoFinal;

            adicionarDialogoAcerto("✅ CORRETO! " + jogador.getPersonagem().getNome() + " ataca!");
            adicionarDialogoAcerto("   💥 Dano bruto: " + danoBruto);
            if (danoMitigado > 0) {
                adicionarDialogoAcerto("   🛡️ Defesa do " + inimigoAtual.getNome() +
                        " (" + defesaInimigo + ") mitigou " + danoMitigado + " de dano");
            }
            adicionarDialogoAcerto("   ❤️ " + inimigoAtual.getNome() + ": " +
                    vidaAntes + " → " + inimigoAtual.getVida() + " (-" + danoFinal + ")");

            pontuacaoTotal += 100;
            jogador.addPontuacao(100);

            int exp = perguntaAtual.getDificuldade().getDanoBase() * 5;
            int nivelAntes = jogador.getPersonagem().getNivel();
            jogador.getPersonagem().addExperiencia(exp);
            adicionarDialogoNormal("📚 +" + exp + " de experiência!");

            if (jogador.getPersonagem().getNivel() > nivelAntes) {
                adicionarDialogoAcerto("🎉 " + jogador.getPersonagem().getNome() + " subiu para o NÍVEL " + jogador.getPersonagem().getNivel() + "!");
                adicionarDialogoNormal("   ❤️ Vida +40 | ⚔️ Ataque +5 | 🛡️ Defesa +3");
            }

            // ⭐ NÃO processa efeitos aqui - será feito no início da próxima rodada
            // processarEfeitosEReduzirDebuffs(); ← REMOVIDO

            if (!inimigoAtual.vivo()) {
                Estagio estagioAtualObj = estagios.get(estagioIndex);
                String nomeEstagio = estagioAtualObj.getNome();

                if (estagioAtualObj.ehChefao()) {
                    adicionarDialogoAcerto("\n🎉🎉🎉 CHEFÃO DERROTADO! 🎉🎉🎉");
                    adicionarDialogoAcerto("👑 " + inimigoAtual.getNome() + " foi vencido!");
                    adicionarDialogoAcerto("🏆 Estágio " + (estagioIndex + 1) + " concluído!");
                } else {
                    adicionarDialogoAcerto("\n🎉 VITÓRIA! " + inimigoAtual.getNome() + " foi derrotado!");
                    adicionarDialogoAcerto("🏆 Estágio " + (estagioIndex + 1) + ": " + nomeEstagio + " concluído!");
                }

                int bonus = (estagioIndex + 1) * 50;
                pontuacaoTotal += bonus;
                jogador.addPontuacao(bonus);
                adicionarDialogoNormal("🏆 Bônus de estágio: +" + bonus + " pontos!");

                avancarEstagio();

                if (estagioIndex >= estagios.size()) {
                    return;
                }
            }

            atualizarStatusJogador();
            atualizarStatusDetalhadoJogador();
            atualizarStatusInimigo();
            atualizarStatusDetalhadoInimigo();

            voltarAoMenu();

        } else {
            perguntasErradas++;
            int danoBruto = calcularDanoInimigo();

            Personagem p = jogador.getPersonagem();
            int defesaJogador = p.getDefesa();
            double reducao = (double) defesaJogador / (defesaJogador + 50);
            int danoMitigado = (int) (danoBruto * reducao);
            int danoFinal = danoBruto - danoMitigado;
            if (danoFinal < 1) danoFinal = 1;

            int vidaAntes = p.getVida();
            jogador.tomarDano(danoFinal);
            danoTotalRecebido += danoFinal;

            adicionarDialogoErro("❌ ERRADO! " + inimigoAtual.getNome() + " contra-ataca!");
            adicionarDialogoErro("   💥 Dano bruto: " + danoBruto);
            if (danoMitigado > 0) {
                adicionarDialogoErro("   🛡️ Sua defesa (" + defesaJogador + ") mitigou " + danoMitigado + " de dano");
            }
            adicionarDialogoErro("   ❤️ " + p.getNome() + ": " + vidaAntes + " → " + p.getVida() + " (-" + danoFinal + ")");
            adicionarDialogoErro("📖 Resposta correta: " + AvaliadorRespostas.getRespostaCorretaFormatada(perguntaAtual));

            atualizarStatusJogador();
            atualizarStatusDetalhadoJogador();

            // ⭐ NÃO processa efeitos aqui - será feito no início da próxima rodada
            // processarEfeitosEReduzirDebuffs(); ← REMOVIDO

            if (!jogador.vivo()) {
                finalizarJogo(false);
                return;
            }

            voltarAoMenu();
        }
    }

    // ==================== OUTROS MÉTODOS ====================

    public void setJogador(Jogador jogador) {
        this.jogador = jogador;
        this.bancoPerguntas = new BancoPerguntas();
        this.gerenciadorRotas = new GerenciadorRotas();
        PerTipo tipo = jogador.getPersonagem().getTipo();
        this.rotaAtual = gerenciadorRotas.getRota(tipo);
        this.estagios = rotaAtual.getEstagios();
        this.estagioIndex = 0;
        this.estagioAtualNumero = 1;

        inicializarHabilidadeDoPersonagem();
        atualizarStatusJogador();
        atualizarStatusDetalhadoJogador();

        String sprite = sprites.getOrDefault(jogador.getPersonagem().getTipo(), "⚔️");
        lblJogadorSprite.setText(sprite);

        adicionarDialogoNormal("✨ " + jogador.getPersonagem().getNome() + " entrou na batalha!");
        adicionarDialogoNormal("🗺️ Rota: " + rotaAtual.getNomeRota());

        Personagem p = jogador.getPersonagem();
        HabilidadeEspecial hab = p.getHabilidade();

        if (hab != null) {
            adicionarDialogoNormal("🌟 Habilidade: " + hab.getNome());
            adicionarDialogoNormal("   " + hab.getDescricao());
            AtributoEspecial attr = (AtributoEspecial) p;
            adicionarDialogoNormal("   💫 Custo: " + getCustoHabilidade(p) + " de " + attr.getNomeAtributo());
            adicionarDialogoNormal("   ✨ Recupera " + getRecuperacaoHabilidade(p) + " de " + attr.getNomeAtributo() + " por acerto!");
        }

        atualizarBotaoHabilidade();
    }

    private void inicializarHabilidadeDoPersonagem() {
        Personagem p = jogador.getPersonagem();
        if (p instanceof Paladino && p.getHabilidade() == null) {
            p.setHabilidade(new com.baquara.habilidades.HabilidadeCura(p));
        } else if (p instanceof Guerreiro && p.getHabilidade() == null) {
            p.setHabilidade(new com.baquara.habilidades.HabilidadeDanoExtra(p));
        } else if (p instanceof Cacadora && p.getHabilidade() == null) {
            p.setHabilidade(new com.baquara.habilidades.HabilidadeCritico(p));
        } else if (p instanceof Sabio && p.getHabilidade() == null) {
            p.setHabilidade(new com.baquara.habilidades.HabilidadePoderMagico(p));
        } else if (p instanceof Arcanista && p.getHabilidade() == null) {
            p.setHabilidade(new com.baquara.habilidades.HabilidadeDestruicaoTotal(p));
        }
    }

    private void atualizarBotaoHabilidade() {
        Platform.runLater(() -> {
            Personagem p = jogador.getPersonagem();
            HabilidadeEspecial hab = p.getHabilidade();
            if (hab == null) {
                btnHabilidade.setDisable(true);
                lblCooldown.setText("❌ Sem habilidade");
                return;
            }
            if (hab.podeUsar()) {
                btnHabilidade.setDisable(false);
                AtributoEspecial attr = (AtributoEspecial) p;
                lblCooldown.setText("✨ PRONTA! (" + attr.getValorAtual() + "/" + attr.getValorMaximo() + " " + attr.getNomeAtributo() + ")");
                lblCooldown.setStyle("-fx-text-fill: #4CAF50;");
            } else {
                btnHabilidade.setDisable(true);
                AtributoEspecial attr = (AtributoEspecial) p;
                lblCooldown.setText("❌ " + attr.getNomeAtributo() + " insuficiente! (Precisa: " + getCustoHabilidade(p) + ")");
                lblCooldown.setStyle("-fx-text-fill: #ff4444;");
            }
        });
    }

    private int getCustoHabilidade(Personagem p) {
        if (p instanceof Paladino) return ValoresHabilidade.CUSTO_PALADINO;
        if (p instanceof Guerreiro) return ValoresHabilidade.CUSTO_GUERREIRO;
        if (p instanceof Cacadora) return ValoresHabilidade.CUSTO_CACADORA;
        if (p instanceof Sabio) return ValoresHabilidade.CUSTO_SABIO;
        if (p instanceof Arcanista) return ValoresHabilidade.CUSTO_ARCANISTA;
        return 30;
    }

    private int getRecuperacaoHabilidade(Personagem p) {
        if (p instanceof Paladino) return ValoresHabilidade.RECUPERACAO_PALADINO;
        if (p instanceof Guerreiro) return ValoresHabilidade.RECUPERACAO_GUERREIRO;
        if (p instanceof Cacadora) return ValoresHabilidade.RECUPERACAO_CACADORA;
        if (p instanceof Sabio) return ValoresHabilidade.RECUPERACAO_SABIO;
        if (p instanceof Arcanista) return ValoresHabilidade.RECUPERACAO_ARCANISTA;
        return 5;
    }

    @FXML
    public void initialize() {
        btnAtacar.setOnAction(e -> mostrarPainelPergunta());
        btnHabilidade.setOnAction(e -> usarHabilidade());
        btnVoltarMenu.setOnAction(e -> voltarAoMenu());

        txtRespostaLacuna.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                String resposta = txtRespostaLacuna.getText().trim();
                if (!resposta.isEmpty() && perguntaAtual instanceof PerguntaCompletarLacuna) {
                    avaliarResposta(resposta);
                }
            }
        });

        txtDialogo.setStyle("-fx-font-size: 11px; -fx-background-color: #FFF8DC; -fx-text-fill: #000000; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
    }

    public void iniciarBatalha() {
        carregarProximoInimigo();
        mostrarMenuPrincipal();
    }

    private void mostrarMenuPrincipal() {
        painelDialogo.setVisible(true);
        painelDialogo.setManaged(true);
        painelAcoes.setVisible(true);
        painelAcoes.setManaged(true);
        painelPergunta.setVisible(false);
        painelPergunta.setManaged(false);
        btnVoltarMenu.setVisible(true);
        btnVoltarMenu.setManaged(true);
        lblTurnoMsg.setText("⚔️ Escolha sua ação! ⚔️");
        aguardandoResposta = false;
        atualizarStatusDetalhadoInimigo();
        atualizarStatusDetalhadoJogador();
    }

    /**
     * ⭐ VOLTAR AO MENU - Processa o início da próxima rodada (sangramento)
     */
    private void voltarAoMenu() {
        aguardandoResposta = false;

        // ⭐⭐ PROCESSAR INÍCIO DA PRÓXIMA RODADA (efeitos como sangramento) ⭐⭐
        if (inimigoAtual != null && inimigoAtual.vivo()) {
            processarInicioRodada();

            // Se o inimigo morreu pelo sangramento, não volta ao menu
            if (!inimigoAtual.vivo()) {
                return;
            }
        }

        mostrarMenuPrincipal();
    }

    private void carregarProximoInimigo() {
        if (estagioIndex >= estagios.size()) {
            finalizarJogo(true);
            return;
        }
        Estagio estagio = estagios.get(estagioIndex);
        int nivel = estagio.getNumero();
        estagioAtualNumero = nivel;
        adicionarDialogoNormal("\n" + "=".repeat(50));
        adicionarDialogoNormal("🏰 ESTÁGIO " + nivel + ": " + estagio.getNome());
        adicionarDialogoNormal("📊 Dificuldade: " + estagio.getDificuldade() + "/10");
        adicionarDialogoNormal("=".repeat(50));
        if (estagio.ehChefao()) {
            this.inimigoAtual = estagio.getChefao();
            adicionarDialogoNormal("\n👑" + "=".repeat(48) + "👑");
            adicionarDialogoNormal("👑         CHEFÃO APARECEU!          👑");
            adicionarDialogoNormal("👑    " + inimigoAtual.getNome() + "    👑");
            adicionarDialogoNormal("👑" + "=".repeat(48) + "👑");
        } else {
            String nomeInimigo = "🛡️ Guardião do " + estagio.getNome() + " 🛡️";
            this.inimigoAtual = new Inimigo(nomeInimigo, nivel);
            adicionarDialogoNormal("\n⚠️ " + inimigoAtual.getNome() + " apareceu!");
            adicionarDialogoNormal("   📖 " + estagio.getNome());
        }
        lblInimigoSprite.setText(getInimigoSprite(nivel));
        double reducao = (double) inimigoAtual.getDefesa() / (inimigoAtual.getDefesa() + 50);
        int percentualReducao = (int)(reducao * 100);
        adicionarDialogoNormal("   🛡️ Defesa: " + inimigoAtual.getDefesa() + " (reduz " + percentualReducao + "% do dano)");
        atualizarStatusInimigo();
        atualizarStatusDetalhadoInimigo();
    }

    private void avancarEstagio() {
        estagioIndex++;
        estagioAtualNumero = estagioIndex + 1;
        if (estagioIndex >= estagios.size()) {
            finalizarJogo(true);
            return;
        }
        jogador.getPersonagem().curar(30);
        adicionarDialogoNormal("\n✨ +30 de vida recuperada pelo avanço de estágio!");
        if (inimigoAtual != null) {
            inimigoAtual.limparEfeitos();
            adicionarDialogoNormal("✨ Efeitos de status (sangramento, debuffs) foram removidos!");
        }
        adicionarDialogoNormal("\n🎵 Prepare-se para o próximo desafio...\n");
        carregarProximoInimigo();
        atualizarBotaoHabilidade();
    }

    private String getInimigoSprite(int estagio) {
        if (estagio == 10) return "👑";
        else if (estagio >= 8) return "🐉";
        else if (estagio >= 6) return "👹";
        else if (estagio >= 4) return "🧙";
        else if (estagio >= 2) return "🐺";
        else return "👾";
    }

    private void mostrarPainelPergunta() {
        painelDialogo.setVisible(false);
        painelDialogo.setManaged(false);
        painelAcoes.setVisible(false);
        painelAcoes.setManaged(false);
        painelPergunta.setVisible(true);
        painelPergunta.setManaged(true);
        btnVoltarMenu.setVisible(false);
        btnVoltarMenu.setManaged(false);
        rodadaAtual++;
        lblTurno.setText("⚔️ RODADA " + rodadaAtual + " ⚔️");
        Dificuldade dificuldade;
        String nomeDificuldade;
        if (estagioAtualNumero <= 3) {
            dificuldade = Dificuldade.FACIL;
            nomeDificuldade = "⭐ FÁCIL";
        } else if (estagioAtualNumero <= 7) {
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
        txtPergunta.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-background-color: #FFF8DC; -fx-text-fill: #000; -fx-border-color: #FFD700; -fx-border-width: 2; -fx-border-radius: 5;");
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
            String estilo = "-fx-background-color: #306830; -fx-text-fill: white; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 8px 20px; -fx-background-radius: 8;";
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

    private Pergunta getPerguntaSemRepeticao(Dificuldade dificuldade) {
        PerTipo tipo = jogador.getPersonagem().getTipo();
        if (!perguntasDisponiveis.containsKey(dificuldade)) {
            List<Pergunta> todas = bancoPerguntas.getPerguntasPorDificuldade(tipo, dificuldade, estagioAtualNumero);
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
        int multiplicadorEstagio = Math.max(1, estagioAtualNumero / 2);
        int dano = danoBase * multiplicador * multiplicadorEstagio;
        double variacao = 0.85 + (random.nextDouble() * 0.3);
        dano = (int)(dano * variacao);
        return Math.max(10, Math.min(80, dano));
    }

    private int calcularDanoInimigo() {
        int danoBase = inimigoAtual.getAtaque();
        int multiplicador = estagioAtualNumero;
        if (estagioAtualNumero % 5 == 0) multiplicador *= 2;
        int dano = danoBase + (multiplicador * 3);
        double variacao = 0.85 + (random.nextDouble() * 0.3);
        dano = (int)(dano * variacao);
        return Math.max(8, dano);
    }

    private void usarHabilidade() {
        Personagem p = jogador.getPersonagem();
        HabilidadeEspecial hab = p.getHabilidade();
        if (hab == null) {
            adicionarDialogoErro("❌ Seu personagem não possui habilidade especial!");
            return;
        }
        if (!hab.podeUsar()) {
            AtributoEspecial attr = (AtributoEspecial) p;
            adicionarDialogoErro("❌ " + attr.getNomeAtributo() + " insuficiente! " +
                    "Você tem " + attr.getValorAtual() + "/" + attr.getValorMaximo() +
                    ". Precisa de " + getCustoHabilidade(p) + "!");
            return;
        }
        adicionarDialogoNormal("🌟 " + p.getNome() + " usa " + hab.getNome() + "!");
        habilidadesUsadas++;
        int dano = hab.executar(inimigoAtual);
        danoTotalCausado += dano;
        adicionarDialogoAcerto("💥 Causou " + dano + " de dano devastador!");
        atualizarStatusInimigo();
        atualizarStatusDetalhadoInimigo();
        atualizarBotaoHabilidade();
        atualizarStatusJogador();

        // ⭐ NÃO processa efeitos aqui - será feito no início da próxima rodada
        // processarEfeitosEReduzirDebuffs(); ← REMOVIDO

        if (!inimigoAtual.vivo()) {
            Estagio estagioAtualObj = estagios.get(estagioIndex);
            if (estagioAtualObj.ehChefao()) {
                adicionarDialogoAcerto("\n🎉🎉🎉 CHEFÃO DERROTADO COM HABILIDADE! 🎉🎉🎉");
                adicionarDialogoAcerto("👑 " + inimigoAtual.getNome() + " foi vencido!");
            } else {
                adicionarDialogoAcerto("\n🎉 VITÓRIA! Estágio " + (estagioIndex + 1) + " concluído!");
            }
            int bonus = (estagioIndex + 1) * 50;
            pontuacaoTotal += bonus;
            jogador.addPontuacao(bonus);
            adicionarDialogoNormal("🏆 Bônus de estágio: +" + bonus + " pontos!");
            avancarEstagio();
            if (estagioIndex >= estagios.size()) {
                return;
            }
        }
        voltarAoMenu();
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
                stats.put("estagiosCompletados", estagioIndex);
                stats.put("modoJogo", "Batalha Normal - " + rotaAtual.getNomeRota());
                javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/fxml/tela-resultado.fxml"));
                javafx.scene.Parent root = loader.load();
                TelaResultadoController controller = loader.getController();
                controller.setDados(jogador, stats, vitoria);
                javafx.stage.Stage stage = (javafx.stage.Stage) btnAtacar.getScene().getWindow();
                stage.setScene(new javafx.scene.Scene(root));
                stage.setTitle("Baquara - Resultado");
            } catch (Exception e) {
                e.printStackTrace();
                if (vitoria) {
                    adicionarDialogoAcerto("\n🏆 PARABÉNS! VOCÊ VENCEU O JOGO! 🏆");
                    adicionarDialogoAcerto("🏆 Rota concluída: " + rotaAtual.getNomeRota());
                } else {
                    adicionarDialogoErro("\n💀 GAME OVER! 💀");
                }
                adicionarDialogoNormal("🎉 Pontuação final: " + pontuacaoTotal);
            }
        });
    }
}