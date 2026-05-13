package com.baquara.gui.controllers;

import com.baquara.controle.RankingManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class TelaRankingController {

    @FXML private ListView<String> listViewRanking;
    @FXML private Button btnVoltar;

    private RankingManager rankingManager;

    public void setRankingManager(RankingManager manager) {
        this.rankingManager = manager;
        carregarRanking();
    }

    @FXML
    public void initialize() {
        btnVoltar.setOnAction(e -> voltar());
    }

    private void carregarRanking() {
        listViewRanking.getItems().clear();

        // Cabeçalho
        listViewRanking.getItems().add(String.format("%-4s %-20s %-15s %-10s %-8s %-15s",
                "#", "JOGADOR", "PERSONAGEM", "PONTOS", "ESTÁGIOS", "DATA"));
        listViewRanking.getItems().add("─".repeat(80));

        // Lista de rankings
        int posicao = 1;
        for (RankingManager.RegistroRanking r : rankingManager.getRankingCompleto()) {
            String medalha = posicao == 1 ? "🥇" : (posicao == 2 ? "🥈" : (posicao == 3 ? "🥉" : "  "));
            String status = r.isVenceuJogo() ? "🏆" : "💀";

            listViewRanking.getItems().add(String.format("%s %-2d %-18s %-15s %-8d %-6d %-15s",
                    medalha, posicao,
                    r.getNomeJogador().length() > 16 ? r.getNomeJogador().substring(0, 13) + "..." : r.getNomeJogador(),
                    r.getPersonagem(),
                    r.getPontuacao(),
                    r.getEstagiosCompletados(),
                    r.getData()));
            posicao++;
        }

        if (rankingManager.getRankingCompleto().isEmpty()) {
            listViewRanking.getItems().add("   Nenhuma partida registrada ainda!");
        }
    }

    private void voltar() {
        Stage stage = (Stage) btnVoltar.getScene().getWindow();
        stage.close();
    }
}
