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

    // ⭐ MÉTODO MODIFICADO: verifica se é uma partida duplicada
    public void adicionarPontuacao(String nomeJogador, String personagem,
                                   int pontuacao, int estagiosCompletados,
                                   boolean venceuJogo, String modoJogo) {

        // ⭐⭐ VERIFICA SE É UMA PARTIDA DUPLICADA ⭐⭐
        if (isPartidaDuplicada(nomeJogador, personagem, pontuacao, estagiosCompletados, modoJogo)) {
            System.out.println("⚠️ Partida duplicada detectada! Não foi adicionada ao ranking.");
            return;
        }

        RegistroRanking novo = new RegistroRanking(
                nomeJogador, personagem, pontuacao,
                estagiosCompletados, venceuJogo, modoJogo
        );
        rankings.add(novo);
        ordenarRanking();
        salvarRanking();
        System.out.println("✅ Nova partida adicionada ao ranking!");
    }

    // ⭐ NOVO MÉTODO: detecta partidas duplicadas (últimos 5 segundos)
    private boolean isPartidaDuplicada(String nomeJogador, String personagem,
                                       int pontuacao, int estagiosCompletados, String modoJogo) {
        if (rankings.isEmpty()) return false;

        // Pega os últimos 5 registros (ou todos se tiver menos)
        int startIndex = Math.max(0, rankings.size() - 5);

        for (int i = startIndex; i < rankings.size(); i++) {
            RegistroRanking r = rankings.get(i);

            // Verifica se tem os mesmos dados
            if (r.getNomeJogador().equals(nomeJogador) &&
                    r.getPersonagem().equals(personagem) &&
                    r.getPontuacao() == pontuacao &&
                    r.getEstagiosCompletados() == estagiosCompletados &&
                    r.getModoJogo().equals(modoJogo)) {

                // Verifica se foi nos últimos 5 segundos
                try {
                    String dataRegistro = r.getData();
                    // Formato: dd/MM/yyyy HH:mm
                    String dataAtual = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

                    if (dataRegistro.substring(0, 16).equals(dataAtual.substring(0, 16))) {
                        return true;  // Mesmo minuto = provavelmente duplicata
                    }
                } catch (Exception e) {
                    // Se não conseguir comparar a data, assume que não é duplicata
                }
            }
        }

        return false;
    }

    // ⭐ MÉTODO PARA RESETAR RANKING
    public void resetarRanking() {
        rankings.clear();
        salvarRanking();
        System.out.println("✅ Ranking resetado com sucesso!");
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
            this.modoJogo = modoJogo != null ? modoJogo : "Normal";
            this.data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        }

        public String getNomeJogador() { return nomeJogador; }
        public String getPersonagem() { return personagem; }
        public int getPontuacao() { return pontuacao; }
        public int getEstagiosCompletados() { return estagiosCompletados; }
        public boolean isVenceuJogo() { return venceuJogo; }
        public String getModoJogo() { return modoJogo; }
        public String getData() { return data; }
    }
}