// 📁 habilidades/HabilidadeCritico.java

package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;
import com.baquara.modelo.ValoresHabilidade;
import java.util.Random;

public class HabilidadeCritico implements HabilidadeEspecial {
    private Personagem usuario;
    private String nome;
    private String descricao;
    private Random random;

    public HabilidadeCritico(Personagem usuario) {
        this.usuario = usuario;
        this.random = new Random();
        this.nome = "CHUVA DE FLECHAS";
        atualizarDescricao();
    }

    private void atualizarDescricao() {
        int ataque = usuario.getAtaque();
        int danoEstimado = (int)(ataque * 1.8);
        this.descricao = "Dispara múltiplas flechas causando ~" + danoEstimado +
                " de dano + sangramento.\n" +
                "   🎯 Chance de crítico aumenta com Penetração restante!\n" +
                "   💫 Custo: " + ValoresHabilidade.CUSTO_CACADORA + " de Penetração\n" +
                "   ✨ Recupera " + ValoresHabilidade.RECUPERACAO_CACADORA + " de Penetração por acerto!";
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
        return attr.getValorAtual() >= ValoresHabilidade.CUSTO_CACADORA;
    }

    @Override
    public void recarregarAposAcerto() {
        if (!(usuario instanceof AtributoEspecial)) return;
        AtributoEspecial attr = (AtributoEspecial) usuario;
        attr.recarregar(ValoresHabilidade.RECUPERACAO_CACADORA);
        System.out.println("✨ +" + ValoresHabilidade.RECUPERACAO_CACADORA + " de " + attr.getNomeAtributo() + " recuperado!");
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
        int custo = ValoresHabilidade.CUSTO_CACADORA;

        // Verifica se tem recurso suficiente
        if (!attr.consumir(custo)) {
            if (attr.getValorAtual() > 10) {
                System.out.println("⚠️ " + attr.getNomeAtributo() + " baixo! Usando versão reduzida...");
                custo = attr.getValorAtual();
                attr.consumir(custo);
            } else {
                System.out.println("❌ " + attr.getNomeAtributo() + " insuficiente! Habilidade cancelada.");
                return 0;
            }
        }

        // Cálculos baseados no ATAQUE do personagem
        int ataque = usuario.getAtaque();

        // Dano = 1.8x o ataque
        int dano = (int)(ataque * 1.8);

        // Chance de crítico baseada na penetração restante
        // 30% base + até 25% extra conforme penetração
        double chanceCritico = 0.30 + (attr.getPorcentagem() / 100 * 0.25);
        boolean critico = random.nextDouble() < chanceCritico;

        System.out.println("\n🏹 " + usuario.getNome() + " dispara CHUVA DE FLECHAS!");
        System.out.println("💙 Consumiu " + custo + " de " + attr.getNomeAtributo() +
                " (Restante: " + attr.getValorAtual() + "/" + attr.getValorMaximo() + ")");
        System.out.println("⚔️ Ataque base: " + ataque + " × 1.8 = " + dano);

        if (critico) {
            dano = (int)(dano * 1.5);
            System.out.println("🎯 GOLPE CRÍTICO! (Chance era " + String.format("%.0f", chanceCritico * 100) + "%)");
        }

        System.out.println("💥 Flechas causam " + dano + " de dano!");
        alvo.tomarDano(dano);

        // Dano de sangramento = 15% do ataque
        int sangramento = (int)(ataque * 0.15);
        System.out.println("🩸 Sangramento! +" + sangramento + " de dano adicional!");
        alvo.tomarDano(sangramento);

        // Recupera um pouco de penetração
        attr.recarregar(ValoresHabilidade.RECUPERACAO_CACADORA);
        System.out.println("✨ +" + ValoresHabilidade.RECUPERACAO_CACADORA + " de " + attr.getNomeAtributo() + " recuperado!");

        return dano + sangramento;
    }
}