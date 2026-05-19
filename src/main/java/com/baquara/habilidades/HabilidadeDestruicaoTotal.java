// 📁 habilidades/HabilidadeDestruicaoTotal.java
// VERSÃO COM DANO MÍNIMO >= ATAQUE BÁSICO

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

    // Constantes de balanceamento
    private static final double DANO_RAIO_MIN_PERCENT = 0.22;   // Raio: mínimo 22% da vida
    private static final double DANO_RAIO_MAX_PERCENT = 0.32;   // Raio: máximo 32% da vida
    private static final double DANO_OUTROS_MIN_PERCENT = 0.12; // Outros: mínimo 12% da vida
    private static final double DANO_OUTROS_MAX_PERCENT = 0.18; // Outros: máximo 18% da vida
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
        int vidaExemplo = 160 + (estagioAtual - 1) * 100;
        if (estagioAtual == 10) vidaExemplo = 1500;

        int danoOutrosMin = (int)(vidaExemplo * DANO_OUTROS_MIN_PERCENT);
        int danoOutrosMax = (int)(vidaExemplo * DANO_OUTROS_MAX_PERCENT);
        int danoRaioMin = (int)(vidaExemplo * DANO_RAIO_MIN_PERCENT);
        int danoRaioMax = (int)(vidaExemplo * DANO_RAIO_MAX_PERCENT);

        this.descricao = "🌌 Libera poder arcano causando dano ELEVADO!\n\n" +
                "   ⚡⚡⚡ SEMPRE MAIS FORTE QUE O ATAQUE BÁSICO! ⚡⚡⚡\n\n" +
                "   📊 EXEMPLO (Estágio " + estagioAtual + " - Vida: " + vidaExemplo + "):\n" +
                "   ⚡ RAIO: " + danoRaioMin + "-" + danoRaioMax + " de dano (+5% choque)\n" +
                "   🔥❄️🌑✨ OUTROS: " + danoOutrosMin + "-" + danoOutrosMax + " de dano\n\n" +
                "   ⚡ EFEITOS POR ELEMENTO:\n" +
                "   ⚡ RAIO: Dano TOTAL (22-32% vida) + Choque residual (+5%)\n" +
                "   🔥 FOGO: Queimadura (+5% dano) + Sangramento (3% por 2 rodadas)\n" +
                "   ❄️ GELO: Congelamento (-20% ataque por 2 rodadas)\n" +
                "   🌑 SOMBRA: Dreno de vida (cura 40% do dano)\n" +
                "   ✨ ENERGIA PURA: Recupera +20 Poder Arcano\n\n" +
                "   💫 Custo: " + ValoresHabilidade.CUSTO_ARCANISTA + " de Poder Arcano\n" +
                "   ⏰ Cooldown: 3 rodadas";
    }

    /**
     * Calcula dano garantindo que seja maior que o ataque básico
     */
    private int calcularDanoPorVida(Inimigo inimigo, boolean ehRaio) {
        int vidaMax = inimigo.getVidaMax();
        int ataqueBase = usuario.getAtaque();
        int nivel = usuario.getNivel();

        // Calcula dano do ataque básico para referência
        int danoBasicoMax = calcularDanoBasicoMaximo(ataqueBase, nivel);

        int dano;
        double percentual;

        if (ehRaio) {
            // RAIO: entre 22% e 32% da vida
            percentual = DANO_RAIO_MIN_PERCENT + (random.nextDouble() * (DANO_RAIO_MAX_PERCENT - DANO_RAIO_MIN_PERCENT));
            dano = (int)(vidaMax * percentual);
        } else {
            // OUTROS: entre 12% e 18% da vida
            percentual = DANO_OUTROS_MIN_PERCENT + (random.nextDouble() * (DANO_OUTROS_MAX_PERCENT - DANO_OUTROS_MIN_PERCENT));
            dano = (int)(vidaMax * percentual);
        }

        // ⭐ GARANTE QUE O DANO É SEMPRE MAIOR QUE O ATAQUE BÁSICO MÁXIMO
        if (dano <= danoBasicoMax) {
            dano = danoBasicoMax + (int)(danoBasicoMax * 0.2); // 20% a mais que o ataque básico
        }

        // Garante dano mínimo absoluto
        dano = Math.max(ehRaio ? 40 : 25, dano);

        return dano;
    }

    /**
     * Calcula o dano máximo do ataque básico para comparação
     */
    private int calcularDanoBasicoMaximo(int ataqueBase, int nivel) {
        // Considera pergunta DIFÍCIL (multiplicador 3) + estágio atual + variação máxima
        int multiplicadorEstagio = Math.max(1, estagioAtual / 2);
        int dano = ataqueBase * 3 * multiplicadorEstagio;
        dano = (int)(dano * 1.15); // Variação máxima de +15%
        return Math.min(80, dano); // Limite do ataque básico é 80
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

        String elemento = elementos[random.nextInt(elementos.length)];
        ultimoElemento = elemento;
        boolean ehRaio = elemento.startsWith("⚡");

        int vidaMax = alvo.getVidaMax();
        int vidaAtual = alvo.getVida();
        int danoBasicoMax = calcularDanoBasicoMaximo(usuario.getAtaque(), usuario.getNivel());

        // Calcula dano
        int danoBase = calcularDanoPorVida(alvo, ehRaio);
        int danoFinal = danoBase;
        int danoAdicional = 0;

        if (ehRaio) {
            danoAdicional = (int)(vidaMax * CHOQUE_RESIDUAL_PERCENT);
            danoFinal += danoAdicional;
        }

        // Não mata em 1 hit
        if (danoFinal >= vidaAtual) {
            danoFinal = (int)(vidaAtual * 0.85);
            if (danoFinal < 5) danoFinal = vidaAtual - 1;
        }

        double percentualDano = (double) danoFinal / vidaMax * 100;

        System.out.println("\n" + "=".repeat(55));
        System.out.println("🌌 CATACLISMO ARCANO - Estágio " + estagioAtual + " 🌌");
        System.out.println("=".repeat(55));
        System.out.println("💙 Consumiu " + custo + " de Poder Arcano");
        System.out.println("🎲 ELEMENTO: " + elemento);
        System.out.println("📊 Vida do inimigo: " + vidaAtual + "/" + vidaMax);
        System.out.println("⚔️ Ataque básico máximo: " + danoBasicoMax);

        if (ehRaio) {
            System.out.println("\n⚡⚡⚡ RAIO ARCANO! ⚡⚡⚡");
            System.out.println("💥 Dano: " + danoBase + " (+" + danoAdicional + " choque) = " + danoFinal);
        } else {
            System.out.println("\n⚠️ Elemento: " + elemento.split(" ")[0]);
            System.out.println("💥 Dano: " + danoFinal);
        }

        System.out.println(String.format("📊 %.1f%% da vida do inimigo", percentualDano));

        alvo.tomarDano(danoFinal);

        // Aplica efeito elemental
        aplicarEfeitoElemental(elemento, alvo, danoFinal, vidaMax, ehRaio, attr);

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
        System.out.println("⚡ EFEITO: " + elemento.split(" ")[0] + " ⚡");
        System.out.println("=".repeat(40));

        if (elemento.startsWith("🔥")) {
            int danoQueimadura = (int)(vidaMax * 0.05);
            System.out.println("🔥 Queimadura! +" + danoQueimadura);
            alvo.tomarDano(danoQueimadura);

            int danoSangramento = (int)(vidaMax * 0.03);
            System.out.println("🩸 Sangramento: " + danoSangramento + " por 2 rodadas!");
            if (!alvo.temEfeito(SangramentoEfeito.class)) {
                alvo.adicionarEfeito(new SangramentoEfeito(2, danoSangramento));
            } else {
                alvo.getEfeito(SangramentoEfeito.class).renovar();
            }

        } else if (elemento.startsWith("❄️")) {
            System.out.println("❄️ Congelamento! Ataque reduzido em 20% por 2 rodadas!");
            alvo.adicionarEfeito(new DebuffAtaqueEfeito(2, 0.8));

        } else if (elemento.startsWith("⚡")) {
            System.out.println("⚡ Choque residual aplicado!");

        } else if (elemento.startsWith("🌑")) {
            int cura = (int)(danoFinal * 0.40);
            System.out.println("🌑 Dreno de vida! +" + cura);
            usuario.curar(cura);

        } else if (elemento.startsWith("✨")) {
            int recuperacao = 20;
            System.out.println("✨ Energia Pura! +" + recuperacao);
            attr.recarregar(recuperacao);
        }
    }

    public void setEstagioAtual(int estagio) {
        this.estagioAtual = estagio;
    }

    public String getUltimoElemento() {
        return ultimoElemento;
    }
}