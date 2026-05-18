// 📁 modelo/efeitos/DebuffAtaqueEfeito.java

package com.baquara.modelo.efeitos;

import com.baquara.modelo.Entidade;

public class DebuffAtaqueEfeito extends EfeitoStatus {
    private double multiplicador;
    private int ataqueOriginal;

    public DebuffAtaqueEfeito(int duracaoRodadas, double multiplicador) {
        super("🔻 Ataque Reduzido", duracaoRodadas);
        this.multiplicador = multiplicador;
    }

    @Override
    public void aplicar(Entidade alvo) {
        this.ataqueOriginal = alvo.getAtaque();
        int novoAtaque = (int)(ataqueOriginal * multiplicador);
        alvo.setAtaque(novoAtaque);
        System.out.println("💢 Ataque de " + alvo.getNome() + " reduzido em " +
                (int)((1 - multiplicador) * 100) + "%! (" + ataqueOriginal + " → " + novoAtaque + ")");
        System.out.println("⏳ Duração restante: " + duracaoRestante + " pergunta(s)");
    }

    @Override
    public void remover(Entidade alvo) {
        alvo.setAtaque(ataqueOriginal);
        System.out.println("✨ Ataque de " + alvo.getNome() + " voltou ao normal! (" + ataqueOriginal + ")");
    }
}