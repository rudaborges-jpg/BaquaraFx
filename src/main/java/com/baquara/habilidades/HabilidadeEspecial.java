package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;

public interface HabilidadeEspecial {
    String getNome();
    String getDescricao();
    int executar(Inimigo alvo);
    boolean podeUsar();
    void recarregarAposAcerto();

}