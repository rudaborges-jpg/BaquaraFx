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

    public int getDanoTotalCausado() {
        return danoTotalCausado;
    }

    public int getDanoPorRodada() {
        return danoPorRodada;
    }

    @Override
    public void aplicar(Entidade alvo) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🩸🩸🩸 " + alvo.getNome() + " COMEÇOU A SANGRAR! 🩸🩸🩸");
        System.out.println("   💔 Perderá " + danoPorRodada + " de vida por " + duracaoMaxima + " rodada(s)!");
        System.out.println("=".repeat(60));
    }

    @Override
    public void remover(Entidade alvo) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("✨🩸 O sangramento de " + alvo.getNome() + " estancou! 🩸✨");
        System.out.println("   📊 Total de dano causado pelo sangramento: " + danoTotalCausado);
        System.out.println("=".repeat(60));
    }

    @Override
    public void atualizar(Entidade alvo) {
        if (!estaAtivo()) return;

        // Variação de ±20% no dano
        double variacao = 0.8 + (random.nextDouble() * 0.4);
        int danoFinal = (int)(danoPorRodada * variacao);
        if (danoFinal < 1) danoFinal = 1;

        // Cria uma string com emojis baseada no dano
        String simbolos = "🩸";
        if (danoFinal > 20) simbolos = "🩸🩸🩸";
        else if (danoFinal > 10) simbolos = "🩸🩸";

        System.out.println("\n" + "=".repeat(50));
        System.out.println(simbolos + " " + alvo.getNome() + " SOFREU " + danoFinal + " DE DANO POR SANGRAMENTO! " + simbolos);
        System.out.println("   💔 Dano base: " + danoPorRodada + " | Variação: " + String.format("%.0f", (variacao * 100)) + "%");
        System.out.println("   ⏱️ Duração restante: " + (duracaoRestante - 1) + " rodada(s)");
        System.out.println("   ❤️ Vida: " + alvo.getVida() + " → " + (alvo.getVida() - danoFinal));
        System.out.println("=".repeat(50));

        alvo.setVida(alvo.getVida() - danoFinal);
        danoTotalCausado += danoFinal;
    }
}