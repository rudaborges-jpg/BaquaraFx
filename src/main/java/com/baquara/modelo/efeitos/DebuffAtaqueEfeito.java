package com.baquara.modelo.efeitos;

import com.baquara.modelo.efeitos.EfeitoStatus;
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

        System.out.println("\n" + "=".repeat(50));
        System.out.println("🔻🔻🔻 " + alvo.getNome() + " TEVE O ATAQUE REDUZIDO! 🔻🔻🔻");
        System.out.println("   ⚔️ " + ataqueOriginal + " → " + novoAtaque + " (-" + (int)((1 - multiplicador) * 100) + "%)");
        System.out.println("   ⏳ Duração: " + duracaoRestante + " rodada(s)");
        System.out.println("=".repeat(50));
    }

    @Override
    public void remover(Entidade alvo) {
        alvo.setAtaque(ataqueOriginal);
        System.out.println("\n" + "=".repeat(50));
        System.out.println("✨✨✨ Ataque de " + alvo.getNome() + " voltou ao normal! ✨✨✨");
        System.out.println("   ⚔️ " + alvo.getAtaque() + " de ataque restaurado!");
        System.out.println("=".repeat(50));
    }
}