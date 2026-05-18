package com.baquara.gui.controllers;

import com.baquara.controle.RankingManager;
import com.baquara.modelo.Jogador;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.util.Map;

public class TelaResultadoController {

    @FXML private Label lblTitulo;
    @FXML private Label lblPontuacao;
    @FXML private Label lblRodadas;
    @FXML private Label lblAcertos;
    @FXML private Label lblErros;
    @FXML private Label lblAproveitamento;
    @FXML private Label lblDanoCausado;
    @FXML private Label lblDanoRecebido;
    @FXML private Label lblHabilidadesUsadas;
    @FXML private Label lblEstagios;
    @FXML private Label lblPersonagemNome;
    @FXML private Label lblPersonagemNivel;
    @FXML private Label lblPersonagemVida;
    @FXML private Label lblPersonagemAtaque;
    @FXML private Label lblPersonagemDefesa;
    @FXML private Label lblPersonagemAtributo;

    @FXML private Button btnJogarNovamente;
    @FXML private Button btnVerRanking;
    @FXML private Button btnSair;

    private Jogador jogador;
    private Map<String, Object> estatisticas;
    private RankingManager rankingManager;
    private boolean venceu;

    public void setDados(Jogador jogador, Map<String, Object> estatisticas, boolean venceu) {
        this.jogador = jogador;
        this.estatisticas = estatisticas;
        this.venceu = venceu;
        this.rankingManager = new RankingManager();

        // Adicionar ao ranking
        rankingManager.adicionarPontuacao(
                jogador.getNome(),
                jogador.getPersonagem().getNome(),
                (int) estatisticas.get("pontuacao"),
                (int) estatisticas.get("estagiosCompletados"),
                venceu,
                (String) estatisticas.get("modoJogo")
        );

        atualizarInterface();
    }

    @FXML
    public void initialize() {
        btnJogarNovamente.setOnAction(e -> jogarNovamente());
        btnVerRanking.setOnAction(e -> mostrarRanking());
        btnSair.setOnAction(e -> sair());
    }

    private void atualizarInterface() {
        if (venceu) {
            lblTitulo.setText("🏆 VITÓRIA TOTAL! 🏆");
            lblTitulo.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");
        } else {
            lblTitulo.setText("💀 GAME OVER 💀");
            lblTitulo.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: #f44336;");
        }

        lblPontuacao.setText("🏆 Pontuação: " + estatisticas.get("pontuacao"));
        lblRodadas.setText("📊 Rodadas: " + estatisticas.get("rodadas"));
        lblAcertos.setText("✅ Acertos: " + estatisticas.get("acertos"));
        lblErros.setText("❌ Erros: " + estatisticas.get("erros"));

        double aproveitamento = (double) (int) estatisticas.get("acertos") /
                ((int) estatisticas.get("acertos") + (int) estatisticas.get("erros")) * 100;
        lblAproveitamento.setText(String.format("📈 Aproveitamento: %.1f%%", aproveitamento));

        lblDanoCausado.setText("⚔️ Dano Causado: " + estatisticas.get("danoCausado"));
        lblDanoRecebido.setText("💔 Dano Recebido: " + estatisticas.get("danoRecebido"));
        lblHabilidadesUsadas.setText("🌟 Habilidades Usadas: " + estatisticas.get("habilidadesUsadas"));
        lblEstagios.setText("📌 Estágios Completados: " + estatisticas.get("estagiosCompletados") + "/10");

        Personagem p = jogador.getPersonagem();
        lblPersonagemNome.setText("👤 Personagem: " + p.getNome());
        lblPersonagemNivel.setText("⭐ Nível: " + p.getNivel());
        lblPersonagemVida.setText("❤️ Vida: " + p.getVida() + "/" + p.getVidaMax());
        lblPersonagemAtaque.setText("⚔️ Ataque: " + p.getAtaque());
        lblPersonagemDefesa.setText("🛡️ Defesa: " + p.getDefesa());

        if (p instanceof AtributoEspecial) {
            AtributoEspecial attr = (AtributoEspecial) p;
            lblPersonagemAtributo.setText("✨ " + attr.getNomeAtributo() + ": " +
                    attr.getValorAtual() + "/" + attr.getValorMaximo());
        } else {
            lblPersonagemAtributo.setText("✨ Atributo Especial: -");
        }
    }

    private void jogarNovamente() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-inicial.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnJogarNovamente.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Baquara - Batalha do Saber");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ⭐ MODIFICADO: Mostra ranking VINDO DA TELA DE RESULTADO
    private void mostrarRanking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-ranking.fxml"));
            Parent root = loader.load();

            TelaRankingController controller = loader.getController();
            // Usa o método específico para quando vem da tela de resultado
            controller.setDadosERanking(rankingManager, jogador, estatisticas, venceu);

            Stage stage = (Stage) btnVerRanking.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Baquara - Ranking de Jogadores");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sair() {
        Stage stage = (Stage) btnSair.getScene().getWindow();
        stage.close();
    }
}