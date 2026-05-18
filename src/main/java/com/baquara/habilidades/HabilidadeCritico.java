// 📁 habilidades/HabilidadeCritico.java

package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;
import com.baquara.modelo.ValoresHabilidade;
import com.baquara.modelo.efeitos.SangramentoEfeito;
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
        int sangramentoEstimado = (int)(ataque * 0.15);
        this.descricao = "Dispara múltiplas flechas causando ~" + danoEstimado +
                " de dano + sangramento de ~" + sangramentoEstimado +
                " por 3 rodadas.\n" +
                "   🎯 Chance de crítico: 30% + bônus por Penetração restante!\n" +
                "   💫 Custo: " + ValoresHabilidade.CUSTO_CACADORA + " de Penetração\n" +
                "   🩸 Sangramento: " + sangramentoEstimado + " de dano por rodada durante 3 rodadas\n" +
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
        int nivel = usuario.getNivel();

        // Dano base = 2.0x o ataque (aumentado para ficar mais forte)
        int dano = (int)(ataque * 2.0) + (nivel * 2);

        // Chance de crítico: 30% base + até 25% extra conforme penetração restante
        double chanceCritico = 0.30 + (attr.getPorcentagem() / 100 * 0.25);
        boolean critico = random.nextDouble() < chanceCritico;

        System.out.println("\n🏹 " + usuario.getNome() + " dispara CHUVA DE FLECHAS!");
        System.out.println("💙 Consumiu " + custo + " de " + attr.getNomeAtributo() +
                " (Restante: " + attr.getValorAtual() + "/" + attr.getValorMaximo() + ")");
        System.out.println("⚔️ Ataque base: " + ataque);
        System.out.println("📊 Nível: " + nivel);

        if (critico) {
            int danoAntes = dano;
            dano = (int)(dano * 1.5);
            System.out.println("🎯 GOLPE CRÍTICO! (Chance era " + String.format("%.0f", chanceCritico * 100) + "%)");
            System.out.println("   💥 Dano aumentou de " + danoAntes + " para " + dano);
        }

        System.out.println("💥 Flechas causam " + dano + " de dano!");
        alvo.tomarDano(dano);

        // ========== ⭐ APLICA O EFEITO DE SANGRAMENTO ==========
        // Dano de sangramento = 20% do ataque + bônus de nível
        int danoSangramentoPorRodada = (int)(ataque * 0.20) + (nivel * 2);
        int duracaoSangramento = 3;

        // Bônus adicional se foi crítico
        if (critico) {
            danoSangramentoPorRodada = (int)(danoSangramentoPorRodada * 1.3);
            System.out.println("🩸 SANGRAMENTO POTENCIALIZADO PELO CRÍTICO!");
        }

        System.out.println("\n🩸🩸🩸 SANGRAMENTO APLICADO! 🩸🩸🩸");
        System.out.println("   ⏱️ Duração: " + duracaoSangramento + " rodadas");
        System.out.println("   💔 Dano por rodada: " + danoSangramentoPorRodada);

        // Verifica se já tem sangramento ativo para renovar
        if (alvo.temEfeito(SangramentoEfeito.class)) {
            SangramentoEfeito sangramentoExistente = alvo.getEfeito(SangramentoEfeito.class);
            sangramentoExistente.renovar();
            System.out.println("🔄 Sangramento renovado! Duração resetada.");
        } else {
            SangramentoEfeito sangramento = new SangramentoEfeito(duracaoSangramento, danoSangramentoPorRodada);
            alvo.adicionarEfeito(sangramento);
        }

        // Recupera um pouco de penetração
        int recuperacao = ValoresHabilidade.RECUPERACAO_CACADORA;
        if (critico) {
            recuperacao = (int)(recuperacao * 1.5);
            System.out.println("✨ Bônus de crítico! Recuperação aumentada!");
        }
        attr.recarregar(recuperacao);
        System.out.println("✨ +" + recuperacao + " de " + attr.getNomeAtributo() + " recuperado!");

        // Mostra estatísticas finais
        System.out.println("\n📊 STATUS APÓS HABILIDADE:");
        System.out.println("   💙 " + attr.getNomeAtributo() + " restante: " + attr.getValorAtual() + "/" + attr.getValorMaximo());

        if (alvo.temEfeito(SangramentoEfeito.class)) {
            SangramentoEfeito sang = alvo.getEfeito(SangramentoEfeito.class);
            System.out.println("   🩸 Sangramento ativo por " + sang.getDuracaoRestante() + " rodada(s)");
        }

        return dano;
    }
}