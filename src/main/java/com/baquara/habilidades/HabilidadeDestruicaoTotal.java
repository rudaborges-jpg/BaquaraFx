
package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;
import com.baquara.modelo.ValoresHabilidade;
import com.baquara.modelo.efeitos.SangramentoEfeito;
import com.baquara.modelo.efeitos.DebuffAtaqueEfeito;
import java.util.Random;

public class HabilidadeDestruicaoTotal implements HabilidadeEspecial {
    private Personagem usuario;
    private String nome;
    private String descricao;
    private Random random;
    private String[] elementos;
    private String ultimoElemento;
    private int estagioAtual;

    // Constantes de balanceamento baseadas na VIDA do inimigo
    private static final double DANO_OUTROS_MAX_PERCENT = 0.10;  // Outros elementos: máximo 10% da vida
    private static final double DANO_RAIO_MIN_PERCENT = 0.20;   // Raio: mínimo 20% da vida
    private static final double DANO_RAIO_MAX_PERCENT = 0.30;   // Raio: máximo 30% da vida (não mata em 1 hit)
    private static final double CHOQUE_RESIDUAL_PERCENT = 0.05; // +5% da vida de choque residual

    public HabilidadeDestruicaoTotal(Personagem usuario) {
        this.usuario = usuario;
        this.random = new Random();
        this.nome = "CATACLISMO ARCANO";
        this.elementos = new String[]{"🔥 Fogo", "❄️ Gelo", "⚡ Raio", "🌑 Sombra", "✨ Energia Pura"};
        this.ultimoElemento = "";
        this.estagioAtual = 1;
        atualizarDescricao();
    }

    public void setEstagio(int estagio) {
        this.estagioAtual = Math.max(1, Math.min(10, estagio));
        atualizarDescricao();
    }

    private void atualizarDescricao() {
        // Calcula exemplos para mostrar na descrição
        int vidaExemplo = 160 + (estagioAtual - 1) * 100;
        if (estagioAtual == 10) vidaExemplo = 1500;

        int danoOutrosMax = (int)(vidaExemplo * DANO_OUTROS_MAX_PERCENT);
        int danoRaioMin = (int)(vidaExemplo * DANO_RAIO_MIN_PERCENT);
        int danoRaioMax = (int)(vidaExemplo * DANO_RAIO_MAX_PERCENT);
        int danoRaioTotalMax = (int)(vidaExemplo * (DANO_RAIO_MAX_PERCENT + CHOQUE_RESIDUAL_PERCENT));

        this.descricao = "🌌 Libera poder arcano causando dano baseado na % de vida do inimigo!\n\n" +
                "   📊 EXEMPLO (Estágio " + estagioAtual + " - Vida: " + vidaExemplo + "):\n" +
                "   ⚡ RAIO: " + danoRaioMin + "-" + danoRaioMax + " de dano (+" + (int)(vidaExemplo * CHOQUE_RESIDUAL_PERCENT) + " choque)\n" +
                "   🔥❄️🌑✨ OUTROS: até " + danoOutrosMax + " de dano\n\n" +
                "   ⚡⚡⚡ EFEITOS POR ELEMENTO: ⚡⚡⚡\n" +
                "   ⚡ RAIO: Dano TOTAL (20-30% vida) + Choque residual (+5% vida)\n" +
                "   🔥 FOGO: Queimadura (+3% dano) + Sangramento (2% por 2 rodadas)\n" +
                "   ❄️ GELO: Congelamento (-15% ataque por 2 rodadas)\n" +
                "   🌑 SOMBRA: Dreno de vida (cura 30% do dano causado)\n" +
                "   ✨ ENERGIA PURA: Recupera +15 Poder Arcano\n\n" +
                "   💫 Custo: " + ValoresHabilidade.CUSTO_ARCANISTA + " de Poder Arcano\n" +
                "   ⏰ Cooldown: 3 rodadas";
    }

    /**
     * Calcula dano baseado na VIDA MÁXIMA do inimigo
     * @param inimigo Alvo do dano
     * @param ehRaio Se é raio ou outro elemento
     * @return Dano calculado
     */
    private int calcularDanoPorVida(Inimigo inimigo, boolean ehRaio) {
        int vidaMax = inimigo.getVidaMax();
        int dano;

        if (ehRaio) {
            // RAIO: entre 20% e 30% da vida
            double percent = DANO_RAIO_MIN_PERCENT + (random.nextDouble() * (DANO_RAIO_MAX_PERCENT - DANO_RAIO_MIN_PERCENT));
            dano = (int)(vidaMax * percent);
        } else {
            // OUTROS ELEMENTOS: entre 5% e 10% da vida
            double percent = 0.05 + (random.nextDouble() * (DANO_OUTROS_MAX_PERCENT - 0.05));
            dano = (int)(vidaMax * percent);
        }

        // Garante dano mínimo
        dano = Math.max(ehRaio ? 20 : 8, dano);

        return dano;
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
        return attr.getValorAtual() >= ValoresHabilidade.CUSTO_ARCANISTA;
    }

    @Override
    public void recarregarAposAcerto() {
        if (!(usuario instanceof AtributoEspecial)) return;
        AtributoEspecial attr = (AtributoEspecial) usuario;
        attr.recarregar(ValoresHabilidade.RECUPERACAO_ARCANISTA);
    }

    @Override
    public int executar(Inimigo alvo) {
        if (!podeUsar()) {
            System.out.println("❌ " + usuario.getNome() + " não tem Poder Arcano suficiente!");
            return 0;
        }

        AtributoEspecial attr = (AtributoEspecial) usuario;
        int custo = ValoresHabilidade.CUSTO_ARCANISTA;

        if (!attr.consumir(custo)) {
            if (attr.getValorAtual() > 10) {
                System.out.println("⚠️ Poder Arcano baixo! Usando versão reduzida...");
                custo = attr.getValorAtual();
                attr.consumir(custo);
            } else {
                System.out.println("❌ Poder Arcano insuficiente! Habilidade cancelada.");
                return 0;
            }
        }

        // Escolhe elemento aleatório
        String elemento = elementos[random.nextInt(elementos.length)];
        ultimoElemento = elemento;

        boolean ehRaio = elemento.startsWith("⚡");

        int vidaMax = alvo.getVidaMax();
        int vidaAtual = alvo.getVida();

        // ⭐ CALCULA DANO BASEADO NA VIDA DO INIMIGO
        int danoBase = calcularDanoPorVida(alvo, ehRaio);
        int danoFinal = danoBase;

        // ⭐ BÔNUS DO RAIO (choque residual)
        int danoAdicional = 0;
        if (ehRaio) {
            danoAdicional = (int)(vidaMax * CHOQUE_RESIDUAL_PERCENT);
            danoFinal += danoAdicional;
        }

        // ⭐ GARANTE QUE NÃO MATA EM 1 HIT (deixa pelo menos 10% de vida)
        if (danoFinal >= vidaAtual) {
            danoFinal = (int)(vidaAtual * 0.9);
            if (danoFinal < 5) danoFinal = vidaAtual - 1;
        }

        double percentualDano = (double) danoFinal / vidaMax * 100;
        double percentualAdicional = ehRaio ? (double) danoAdicional / vidaMax * 100 : 0;
        double vidaRestantePercentual = (double) (vidaAtual - danoFinal) / vidaMax * 100;

        System.out.println("\n" + "=".repeat(55));
        System.out.println("🌌 CATACLISMO ARCANO - Estágio " + estagioAtual + " 🌌");
        System.out.println("=".repeat(55));
        System.out.println("💙 Consumiu " + custo + " de Poder Arcano");
        System.out.println("🎲 ELEMENTO SORTEADO: " + elemento);
        System.out.println("📊 Vida do inimigo: " + vidaAtual + "/" + vidaMax);

        if (ehRaio) {
            System.out.println("\n⚡⚡⚡ RAIO ARCANO! ⚡⚡⚡");
            System.out.println("💥 Dano do raio: " + danoBase + String.format(" (%.1f%% da vida)", (double)danoBase/vidaMax*100));
            System.out.println("⚡ Choque residual: +" + danoAdicional + String.format(" (%.1f%%)", percentualAdicional));
            System.out.println("📊 DANO TOTAL: " + danoFinal + String.format(" (%.1f%% da vida)", percentualDano));
        } else {
            System.out.println("\n⚠️ Elemento: " + elemento.split(" ")[0]);
            System.out.println("💥 Dano causado: " + danoFinal + String.format(" (%.1f%% da vida)", percentualDano));
        }

        System.out.println("\n❤️ Vida restante: " + (vidaAtual - danoFinal) + String.format(" (%.1f%%)", vidaRestantePercentual));

        alvo.tomarDano(danoFinal);

        // Aplica efeito elemental
        aplicarEfeitoElemental(elemento, alvo, danoFinal, vidaMax, ehRaio, attr);

        // Recupera um pouco de poder arcano
        attr.recarregar(ValoresHabilidade.RECUPERACAO_ARCANISTA);
        System.out.println("\n✨ +" + ValoresHabilidade.RECUPERACAO_ARCANISTA + " de Poder Arcano recuperado!");
        System.out.println("📊 Poder Arcano: " + attr.getValorAtual() + "/" + attr.getValorMaximo());

        return danoFinal;
    }

    private void aplicarEfeitoElemental(String elemento, Inimigo alvo,
                                        int danoFinal, int vidaMax,
                                        boolean ehRaio,
                                        AtributoEspecial attr) {

        System.out.println("\n" + "=".repeat(40));
        System.out.println("⚡ EFEITO ESPECIAL: " + elemento.split(" ")[0] + " ⚡");
        System.out.println("=".repeat(40));

        if (elemento.startsWith("🔥")) {
            // 🔥 FOGO: Queimadura + Sangramento (baseado na vida)
            int danoQueimadura = (int)(vidaMax * 0.03);
            if (danoQueimadura < 3) danoQueimadura = 3;
            System.out.println("🔥🔥🔥 QUEIMADURA!");
            System.out.println("   💥 Dano extra: +" + danoQueimadura);
            alvo.tomarDano(danoQueimadura);

            int danoSangramento = (int)(vidaMax * 0.02);
            if (danoSangramento < 2) danoSangramento = 2;
            System.out.println("🩸🩸🩸 SANGRAMENTO!");
            System.out.println("   💔 " + danoSangramento + " de dano por 2 rodadas!");

            if (!alvo.temEfeito(SangramentoEfeito.class)) {
                alvo.adicionarEfeito(new SangramentoEfeito(2, danoSangramento));
            } else {
                alvo.getEfeito(SangramentoEfeito.class).renovar();
            }

        } else if (elemento.startsWith("❄️")) {
            // ❄️ GELO: Congelamento - reduz ataque
            System.out.println("❄️❄️❄️ CONGELAMENTO!");
            System.out.println("   🛡️ Ataque do inimigo reduzido em 15% por 2 rodadas!");
            alvo.adicionarEfeito(new DebuffAtaqueEfeito(2, 0.85));

        } else if (elemento.startsWith("⚡")) {
            // ⚡ RAIO: Choque residual já aplicado no dano
            System.out.println("⚡⚡⚡ CHOQUE RESIDUAL!");
            System.out.println("   💫 Inimigo eletrocutado!");
            System.out.println("   ⚡ O choque já foi aplicado no dano total!");

        } else if (elemento.startsWith("🌑")) {
            // 🌑 SOMBRA: Dreno de vida (30% do dano causado)
            int cura = (int)(danoFinal * 0.30);
            if (cura < 5) cura = 5;
            System.out.println("🌑🌑🌑 DRENO DE VIDA!");
            System.out.println("   💚 Você drenou " + cura + " de vida do inimigo!");
            System.out.println("   ❤️ +" + cura + " de vida recuperada!");
            usuario.curar(cura);

        } else if (elemento.startsWith("✨")) {
            // ✨ ENERGIA PURA: Recupera Poder Arcano
            int recuperacao = 15;
            System.out.println("✨✨✨ ENERGIA PURA!");
            System.out.println("   ⚡ Você canaliza energia pura do caos!");
            System.out.println("   💙 +" + recuperacao + " de Poder Arcano recuperado!");
            attr.recarregar(recuperacao);
            System.out.println("   📊 Poder Arcano agora: " + attr.getValorAtual() + "/" + attr.getValorMaximo());
        }
    }

    public void setEstagioAtual(int estagio) {
        this.estagioAtual = estagio;
    }

    public String getUltimoElemento() {
        return ultimoElemento;
    }
}