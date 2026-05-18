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

    /**
     * Aplica o efeito ao alvo (chamado quando o efeito é aplicado)
     */
    public abstract void aplicar(Entidade alvo);

    /**
     * Remove o efeito do alvo (chamado quando o efeito expira)
     */
    public abstract void remover(Entidade alvo);

    /**
     * Atualiza o efeito a cada rodada (opcional - pode ser sobrescrito)
     */
    public void atualizar(Entidade alvo) {
        // Implementação padrão - pode ser sobrescrita pelas classes filhas
    }

    /**
     * Reduz a duração e retorna se ainda está ativo
     */
    public boolean reduzirDuracao() {
        if (duracaoRestante > 0) {
            duracaoRestante--;
        }
        if (duracaoRestante == 0) {
            ativo = false;
        }
        return ativo;
    }

    /**
     * Renova o efeito (reseta a duração)
     */
    public void renovar() {
        this.duracaoRestante = this.duracaoMaxima;
        this.ativo = true;
    }

    /**
     * Retorna descrição do efeito para exibição
     */
    public String getDescricao() {
        return nome + ": " + duracaoRestante + " rodada(s) restante(s)";
    }
}
