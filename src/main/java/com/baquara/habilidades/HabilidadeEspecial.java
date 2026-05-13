package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;

public interface HabilidadeEspecial {
    String getNome();
    String getDescricao();
    int getCooldown();
    int getCooldownAtual();      // ← retorna quantas rodadas faltam
    boolean estaPronta();         // ← verifica se cooldownAtual == 0
    int executar(Inimigo alvo);
    void reduzirCooldown();       // ← diminui 1 a cada rodada
    void resetarCooldown();       // ← zera o cooldown
}