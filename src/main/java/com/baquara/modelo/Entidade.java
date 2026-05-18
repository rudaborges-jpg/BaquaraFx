// 📁 modelo/Entidade.java
package com.baquara.modelo;

import com.baquara.modelo.efeitos.EfeitoStatus;

public interface Entidade {
    String getNome();
    int getAtaque();
    int getDefesa();
    int getVida();
    int getVidaMax();

    void setAtaque(int ataque);
    void setDefesa(int defesa);
    void setVida(int vida);

    void adicionarEfeito(EfeitoStatus efeito);
    void removerEfeito(Class<? extends EfeitoStatus> tipoEfeito);
    boolean temEfeito(Class<? extends EfeitoStatus> tipoEfeito);
    <T extends EfeitoStatus> T getEfeito(Class<T> tipoEfeito);
    void atualizarEfeitos();
}
