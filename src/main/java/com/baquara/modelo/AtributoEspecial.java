// 📁 modelo/AtributoEspecial.java
package com.baquara.modelo;

public interface AtributoEspecial {
    String getNomeAtributo();
    int getValorAtual();
    int getValorMaximo();
    double getPorcentagem();  // Para exibir barras

    boolean consumir(int quantidade);
    void recarregar(int quantidade);
    void recarregarCompletamente();

    // Recarga parcial após eventos
    void recarregarPorEstagio(int estagioNumero);
    void recarregarPorNivel(int novoNivel);
}