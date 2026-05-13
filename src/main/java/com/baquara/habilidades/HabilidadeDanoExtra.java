package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;

public class HabilidadeDanoExtra implements HabilidadeEspecial {
    private Personagem usuario;
    private int cooldownMaximo;
    private int cooldownAtual;
    private String nome;
    private String descricao;

    public HabilidadeDanoExtra(Personagem usuario, int cooldown) {
        this.usuario = usuario;
        this.cooldownMaximo = cooldown;
        this.cooldownAtual = 0;
        this.nome = "FÚRIA DO GUERREIRO";
        atualizarDescricao();
    }

    private void atualizarDescricao() {
        int ataque = usuario.getAtaque();
        int danoEstimado = (int)(ataque * 2.0);
        this.descricao = "Libera fúria acumulada causando ~" + danoEstimado +
                " de dano extra.\n" +
                "   💪 Quanto mais Espírito de Luta restante, mais forte!";
    }

    @Override
    public String getNome() { return nome; }

    @Override
    public String getDescricao() {
        atualizarDescricao();
        return descricao;
    }

    @Override
    public int getCooldown() { return cooldownMaximo; }

    @Override
    public int getCooldownAtual() { return cooldownAtual; }

    @Override
    public boolean estaPronta() { return cooldownAtual == 0; }

    @Override
    public int executar(Inimigo alvo) {
        if (!estaPronta()) {
            System.out.println("⏳ Habilidade em cooldown!");
            return 0;
        }

        // ========== LÓGICA COM ATRIBUTO ESPECIAL ==========
        AtributoEspecial attr = (AtributoEspecial) usuario;
        int custo = 40;

        // Verifica se tem recurso suficiente
        if (!attr.consumir(custo)) {
            if (attr.getValorAtual() > 10) {
                System.out.println("⚠️ " + attr.getNomeAtributo() + " baixo! Usando FÚRIA reduzida...");
                custo = attr.getValorAtual();
                attr.consumir(custo);
            } else {
                System.out.println("❌ " + attr.getNomeAtributo() + " insuficiente! Habilidade cancelada.");
                return 0;
            }
        }

        // Cálculos baseados no ATAQUE do personagem
        int ataque = usuario.getAtaque();

        // Bônus baseado no Espírito de Luta restante (quanto mais, mais forte)
        int bonusEspirito = attr.getValorAtual() / 2;

        // Dano = 2.0x o ataque + bônus de espírito
        int danoTotal = (int)(ataque * 2.0) + bonusEspirito;

        System.out.println("\n💪 " + usuario.getNome() + " libera FÚRIA DO GUERREIRO!");
        System.out.println("💙 Consumiu " + custo + " de " + attr.getNomeAtributo() +
                " (Restante: " + attr.getValorAtual() + "/" + attr.getValorMaximo() + ")");
        System.out.println("⚔️ Ataque base: " + ataque + " × 2.0 = " + (int)(ataque * 2.0));
        System.out.println("💪 Bônus de espírito: +" + bonusEspirito + " de dano");
        System.out.println("💥 DANO TOTAL: " + danoTotal + "!");

        alvo.tomarDano(danoTotal);

        // O ímpeto da batalha recupera um pouco do espírito
        attr.recarregar(8);
        System.out.println("✨ +8 de " + attr.getNomeAtributo() + " recuperado!");

        cooldownAtual = cooldownMaximo;
        return danoTotal;
    }

    @Override
    public void reduzirCooldown() {
        if (cooldownAtual > 0) cooldownAtual--;
    }

    @Override
    public void resetarCooldown() { cooldownAtual = 0; }
}