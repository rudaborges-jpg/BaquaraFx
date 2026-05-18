package com.baquara.modelo.efeitos;
import com.baquara.modelo.Entidade;

public abstract class EfeitoStatus {
    protected String nome;
    protected int duracaoRestante;
    protected int duracaoMaxima;
    protected boolean ativo;

    public EfeitoStatus(String nome, int duracaoRodadas) {
        this.nome = nome;
        this.duracaoMaxima = duracaoRodadas;
        this.duracaoRestante = duracaoRodadas;
        this.ativo = true;
    }

    public String getNome() {
        return nome;
    }

    public int getDuracaoRestante() {
        return duracaoRestante;
    }

    public int getDuracaoMaxima() {
        return duracaoMaxima;
    }

    public boolean estaAtivo() {
        return ativo && duracaoRestante > 0;
    }

    public abstract void aplicar(Entidade alvo);
    public abstract void remover(Entidade alvo);

    public void atualizar(Entidade alvo) {
        // Implementação padrão - pode ser sobrescrita pelas classes filhas
    }

    public boolean reduzirDuracao() {
        if (duracaoRestante > 0) {
            duracaoRestante--;
        }
        if (duracaoRestante == 0) {
            ativo = false;
        }
        return ativo;
    }

    public void renovar() {
        this.duracaoRestante = this.duracaoMaxima;
        this.ativo = true;
        System.out.println("🔄 " + nome + " renovado! Duração resetada para " + duracaoMaxima + " rodada(s)");
    }

    public String getDescricao() {
        return nome + ": " + duracaoRestante + " rodada(s) restante(s)";
    }
}
