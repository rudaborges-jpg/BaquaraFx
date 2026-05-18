package com.baquara.gui.controllers;

import com.baquara.controle.RankingManager;
import com.baquara.modelo.Jogador;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.util.Map;

public class TelaRankingController {

    @FXML private ListView<String> listViewRanking;
    @FXML private Button btnVoltar;

    private RankingManager rankingManager;
    private String telaOrigem;  // ⭐ NOVO: guarda de onde veio ("inicial" ou "resultado")
    private Jogador jogador;    // ⭐ NOVO: guarda o jogador (para voltar à tela de resultado)
    private Map<String, Object> estatisticas; // ⭐ NOVO: guarda estatísticas
    private boolean venceu;      // ⭐ NOVO: guarda se venceu

    // ⭐ CONSTRUTOR PARA QUANDO VEM DA TELA INICIAL
    public void setRankingManager(RankingManager manager) {
        this.rankingManager = manager;
        this.telaOrigem = "inicial";
        this.jogador = null;
        this.estatisticas = null;
        carregarRanking();
    }

    // ⭐ NOVO: CONSTRUTOR PARA QUANDO VEM DA TELA DE RESULTADO
    public void setDadosERanking(RankingManager manager, Jogador jogador,
                                 Map<String, Object> estatisticas, boolean venceu) {
        this.rankingManager = manager;
        this.telaOrigem = "resultado";
        this.jogador = jogador;
        this.estatisticas = estatisticas;
        this.venceu = venceu;
        carregarRanking();
    }

    @FXML
    public void initialize() {
        btnVoltar.setOnAction(e -> voltar());
    }

    private void carregarRanking() {
        listViewRanking.getItems().clear();

        // Cabeçalho
        listViewRanking.getItems().add(String.format("%-4s %-20s %-15s %-10s %-8s %-15s %-12s",
                "#", "JOGADOR", "PERSONAGEM", "PONTOS", "ESTÁGIOS", "MODO", "DATA"));
        listViewRanking.getItems().add("─".repeat(95));

        // Lista de rankings
        int posicao = 1;
        for (RankingManager.RegistroRanking r : rankingManager.getRankingCompleto()) {
            String medalha = posicao == 1 ? "🥇" : (posicao == 2 ? "🥈" : (posicao == 3 ? "🥉" : "  "));

            // Trunca o modo de jogo se for muito longo
            String modo = r.getModoJogo();
            if (modo == null) modo = "Normal";
            if (modo.length() > 12) modo = modo.substring(0, 10) + "..";

            listViewRanking.getItems().add(String.format("%s %-2d %-18s %-15s %-8d %-6d %-12s %-15s",
                    medalha, posicao,
                    r.getNomeJogador().length() > 16 ? r.getNomeJogador().substring(0, 13) + "..." : r.getNomeJogador(),
                    r.getPersonagem(),
                    r.getPontuacao(),
                    r.getEstagiosCompletados(),
                    modo,
                    r.getData()));
            posicao++;
        }

        if (rankingManager.getRankingCompleto().isEmpty()) {
            listViewRanking.getItems().add("   Nenhuma partida registrada ainda!");
        }
    }

    // ⭐ MÉTODO INTELIGENTE: decide para onde voltar baseado na origem
    private void voltar() {
        if ("resultado".equals(telaOrigem)) {
            voltarParaTelaResultado();
        } else {
            voltarParaTelaInicial();
        }
    }

    // ⭐ VOLTA PARA A TELA DE RESULTADO (com os dados da partida)
    private void voltarParaTelaResultado() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-resultado.fxml"));
            Parent root = loader.load();

            TelaResultadoController controller = loader.getController();
            controller.setDados(jogador, estatisticas, venceu);

            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Baquara - Resultado");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao voltar para tela de resultado: " + e.getMessage());
            // Fallback: volta para a tela inicial em caso de erro
            voltarParaTelaInicial();
        }
    }

    // ⭐ VOLTA PARA A TELA INICIAL
    private void voltarParaTelaInicial() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/tela-inicial.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVoltar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Baquara - Batalha do Saber");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
