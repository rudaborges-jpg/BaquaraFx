// 📁 modelo/efeitos/SangramentoEfeito.java

package com.baquara.modelo.efeitos;

import com.baquara.modelo.efeitos.EfeitoStatus;
import com.baquara.modelo.Entidade;
import java.util.Random;

public class SangramentoEfeito extends EfeitoStatus {
    private int danoPorRodada;
    private Random random;
    private int danoTotalCausado;

    public SangramentoEfeito(int duracaoRodadas, int danoPorRodada) {
        super("🩸 Sangramento", duracaoRodadas);
        this.danoPorRodada = danoPorRodada;
        this.random = new Random();
        this.danoTotalCausado = 0;
    }

    @Override
    public void aplicar(Entidade alvo) {
        System.out.println("🩸 " + alvo.getNome() + " começou a sangrar! Perderá " +
                danoPorRodada + " de vida por " + duracaoMaxima + " rodada(s)!");
    }

    @Override
    public void remover(Entidade alvo) {
        System.out.println("✨ O sangramento de " + alvo.getNome() + " estancou! " +
                "Total de dano causado: " + danoTotalCausado);
    }

    @Override
    public void atualizar(Entidade alvo) {
        if (!estaAtivo()) return;

        // Variação de ±20% no dano
        double variacao = 0.8 + (random.nextDouble() * 0.4);
        int danoFinal = (int)(danoPorRodada * variacao);

        if (danoFinal < 1) danoFinal = 1;

        System.out.println("🩸 " + alvo.getNome() + " sofreu " + danoFinal +
                " de dano por sangramento! (Duração restante: " + (duracaoRestante - 1) + ")");

        alvo.setVida(alvo.getVida() - danoFinal);
        danoTotalCausado += danoFinal;

        System.out.println("   ❤️ Vida restante: " + alvo.getVida() + "/" + alvo.getVidaMax());
    }
}