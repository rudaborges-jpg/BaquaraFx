package com.baquara.controle;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RankingManager {
    private static final String ARQUIVO_RANKING = "ranking.dat";
    private List<RegistroRanking> rankings;

    public RankingManager() {
        rankings = new ArrayList<>();
        carregarRanking();
    }

    public void adicionarPontuacao(String nomeJogador, String personagem,
                                   int pontuacao, int estagiosCompletados,
                                   boolean venceuJogo, String modoJogo) {
        RegistroRanking novo = new RegistroRanking(
                nomeJogador, personagem, pontuacao,
                estagiosCompletados, venceuJogo, modoJogo
        );
        rankings.add(novo);
        ordenarRanking();
        salvarRanking();
    }

    private void ordenarRanking() {
        rankings.sort((a, b) -> Integer.compare(b.getPontuacao(), a.getPontuacao()));
    }

    public List<RegistroRanking> getTop10() {
        return rankings.size() > 10 ? rankings.subList(0, 10) : new ArrayList<>(rankings);
    }

    public List<RegistroRanking> getRankingCompleto() {
        return new ArrayList<>(rankings);
    }

    private void salvarRanking() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_RANKING))) {
            oos.writeObject(rankings);
        } catch (IOException e) {
            System.err.println("Erro ao salvar ranking: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarRanking() {
        File arquivo = new File(ARQUIVO_RANKING);
        if (!arquivo.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            rankings = (List<RegistroRanking>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Erro ao carregar ranking: " + e.getMessage());
            rankings = new ArrayList<>();
        }
    }

    public static class RegistroRanking implements Serializable {
        private static final long serialVersionUID = 1L;
        private final String nomeJogador;
        private final String personagem;
        private final int pontuacao;
        private final int estagiosCompletados;
        private final boolean venceuJogo;
        private final String modoJogo;
        private final String data;

        public RegistroRanking(String nomeJogador, String personagem, int pontuacao,
                               int estagiosCompletados, boolean venceuJogo, String modoJogo) {
            this.nomeJogador = nomeJogador;
            this.personagem = personagem;
            this.pontuacao = pontuacao;
            this.estagiosCompletados = estagiosCompletados;
            this.venceuJogo = venceuJogo;
            this.modoJogo = modoJogo;
            this.data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        // Getters
        public String getNomeJogador() { return nomeJogador; }
        public String getPersonagem() { return personagem; }
        public int getPontuacao() { return pontuacao; }
        public int getEstagiosCompletados() { return estagiosCompletados; }
        public boolean isVenceuJogo() { return venceuJogo; }
        public String getModoJogo() { return modoJogo; }
        public String getData() { return data; }
    }
}