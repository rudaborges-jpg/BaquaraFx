// 📁 habilidades/HabilidadeDanoExtra.java

package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;
import com.baquara.modelo.ValoresHabilidade;
import com.baquara.modelo.efeitos.DebuffAtaqueEfeito;

public class HabilidadeDanoExtra implements HabilidadeEspecial {
    private Personagem usuario;
    private String nome;
    private String descricao;
    private static final double MULTIPLICADOR_DEBUFF = 0.8;  // Reduz 20% do ataque
    private static final int DURACAO_DEBUFF = 3;             // Dura 2 rodadas

    public HabilidadeDanoExtra(Personagem usuario) {
        this.usuario = usuario;
        this.nome = "FÚRIA DO GUERREIRO";
        atualizarDescricao();
    }

    private void atualizarDescricao() {
        int ataque = usuario.getAtaque();
        int danoEstimado = (int)(ataque * 2.0);
        this.descricao = "Libera fúria acumulada causando ~" + danoEstimado +
                " de dano extra.\n" +
                "   💪 Quanto mais Espírito de Luta restante, mais forte!\n" +
                "   💫 Custo: " + ValoresHabilidade.CUSTO_GUERREIRO + " de Espírito de Luta\n" +
                "   🛡️ Aplica debuff de -20% de ataque no inimigo por " + DURACAO_DEBUFF + " rodadas\n" +
                "   🔄 Efeito resetável (usar novamente renova a duração)";
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public String getDescricao() {
        atualizarDescricao();
        return descricao;
    }

    @Override
    public boolean podeUsar() {
        if (!(usuario instanceof AtributoEspecial)) return false;
        AtributoEspecial attr = (AtributoEspecial) usuario;
        return attr.getValorAtual() >= ValoresHabilidade.CUSTO_GUERREIRO;
    }

    @Override
    public void recarregarAposAcerto() {
        if (!(usuario instanceof AtributoEspecial)) return;
        AtributoEspecial attr = (AtributoEspecial) usuario;
        attr.recarregar(ValoresHabilidade.RECUPERACAO_GUERREIRO);
        System.out.println("✨ +" + ValoresHabilidade.RECUPERACAO_GUERREIRO + " de " + attr.getNomeAtributo() + " recuperado!");
    }

    @Override
    public int executar(Inimigo alvo) {
        if (!podeUsar()) {
            System.out.println("❌ " + usuario.getNome() + " não tem " +
                    ((AtributoEspecial) usuario).getNomeAtributo() + " suficiente!");
            return 0;
        }

        // ========== LÓGICA COM ATRIBUTO ESPECIAL ==========
        AtributoEspecial attr = (AtributoEspecial) usuario;
        int custo = ValoresHabilidade.CUSTO_GUERREIRO;

        // Consome o recurso
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

        // ⭐ APLICA O DEBUFF USANDO O SISTEMA DE EFEITOS
        DebuffAtaqueEfeito debuff = new DebuffAtaqueEfeito(DURACAO_DEBUFF, MULTIPLICADOR_DEBUFF);
        alvo.adicionarEfeito(debuff);

        return danoTotal;
    }
}