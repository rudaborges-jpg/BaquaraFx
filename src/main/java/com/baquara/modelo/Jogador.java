package com.baquara.modelo;

public class Jogador {
    private String nome;
    private Personagem personagem;
    private int pontuacao;

    public Jogador(String nome) {
        this.nome = nome;
        this.pontuacao = 0;
    }

    public void escolherPersonagem(Personagem personagem) {
        this.personagem = personagem;
        System.out.println(nome + " escolheu " + personagem.getNome() + "!");
    }

    public String getNome() { return nome; }
    public Personagem getPersonagem() { return personagem; }
    public int getPontuacao() { return pontuacao; }
    public void addPontuacao(int pontos) { this.pontuacao += pontos; }
    public boolean vivo() { return personagem != null && personagem.vivo(); }
    public void tomarDano(int dano) { personagem.tomarDano(dano); }

    public void mostrarStatus() {
        System.out.println("\n👤 Jogador: " + nome);
        personagem.mostrarStatus();
        System.out.println("🏆 Pontuação: " + pontuacao);
    }
}