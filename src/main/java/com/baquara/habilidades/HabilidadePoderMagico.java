// 📁 habilidades/HabilidadePoderMagico.java
// VERSÃO COM DANO VERDADEIRO - SEMPRE > ATAQUE BÁSICO

package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;
import com.baquara.modelo.ValoresHabilidade;
import java.util.Random;

public class HabilidadePoderMagico implements HabilidadeEspecial {
    private Personagem usuario;
    private String nome;
    private String descricao;
    private Random random;
    private int conhecimento;
    private int estagioAtual;

    // Constantes de balanceamento
    private static final double DANO_MIN_PERCENT = 0.14;      // Dano mínimo: 14% da vida
    private static final double DANO_MAX_PERCENT = 0.22;      // Dano máximo: 22% da vida
    private static final double BONUS_CONHECIMENTO = 0.01;    // +1% por ponto (máx +10%)
    private static final double CURA_PERCENT = 0.12;          // Cura: 12% da vida do Sábio

    public HabilidadePoderMagico(Personagem usuario) {
        this.usuario = usuario;
        this.random = new Random();
        this.nome = "SABEDORIA ANCESTRAL";
        this.conhecimento = 0;
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

        int danoMin = (int)(vidaExemplo * DANO_MIN_PERCENT);
        int danoMax = (int)(vidaExemplo * DANO_MAX_PERCENT);

        double danoAtualBonus = DANO_MIN_PERCENT + (conhecimento * BONUS_CONHECIMENTO);
        if (danoAtualBonus > 0.32) danoAtualBonus = 0.32;
        int danoAtual = (int)(vidaExemplo * danoAtualBonus);

        this.descricao = "📜 Conjura sabedoria ancestral com DANO VERDADEIRO!\n\n" +
                "   ⚡⚡⚡ SEMPRE MAIS FORTE QUE O ATAQUE BÁSICO! ⚡⚡⚡\n\n" +
                "   📊 DANO VERDADEIRO (ignora defesa):\n" +
                "   💥 Dano base: " + danoMin + "-" + danoMax + " de dano\n" +
                "   📈 Com " + conhecimento + " conhecimento: ~" + danoAtual + " de dano\n" +
                "   💚 Cura: " + String.format("%.0f", CURA_PERCENT * 100) + "% da sua vida\n\n" +
                "   ✨ O DANO AUMENTA COM O CONHECIMENTO ACUMULADO!\n" +
                "   🛡️ A DEFESA DO INIMIGO É TOTALMENTE IGNORADA!\n\n" +
                "   💫 Custo: " + ValoresHabilidade.CUSTO_SABIO + " de Mana\n" +
                "   ✨ Recupera " + ValoresHabilidade.RECUPERACAO_SABIO + " de Mana por acerto!";
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
        return attr.getValorAtual() >= ValoresHabilidade.CUSTO_SABIO;
    }

    @Override
    public void recarregarAposAcerto() {
        if (!(usuario instanceof AtributoEspecial)) return;
        AtributoEspecial attr = (AtributoEspecial) usuario;
        attr.recarregar(ValoresHabilidade.RECUPERACAO_SABIO);
        System.out.println("✨ +" + ValoresHabilidade.RECUPERACAO_SABIO + " de Mana recuperado!");
    }

    /**
     * Calcula dano verdadeiro garantindo que seja > ataque básico
     */
    private int calcularDanoVerdadeiro(Inimigo alvo) {
        int vidaMax = alvo.getVidaMax();
        int ataqueBase = usuario.getAtaque();
        int nivel = usuario.getNivel();

        // Calcula dano do ataque básico para referência
        int danoBasicoMax = calcularDanoBasicoMaximo(ataqueBase, nivel);

        // Calcula percentual baseado no conhecimento
        double percentualBase = DANO_MIN_PERCENT + (conhecimento * BONUS_CONHECIMENTO);

        // Variação aleatória
        double variacao = 0.85 + (random.nextDouble() * 0.3);
        double percentualFinal = percentualBase * variacao;

        // Limita entre mínimo e máximo
        percentualFinal = Math.max(DANO_MIN_PERCENT, Math.min(DANO_MAX_PERCENT + (conhecimento * BONUS_CONHECIMENTO * 0.5), percentualFinal));
        percentualFinal = Math.min(0.35, percentualFinal);

        int dano = (int)(vidaMax * percentualFinal);

        // ⭐ GARANTE QUE O DANO É SEMPRE MAIOR QUE O ATAQUE BÁSICO MÁXIMO
        if (dano <= danoBasicoMax) {
            dano = danoBasicoMax + (int)(danoBasicoMax * 0.25); // 25% a mais que o ataque básico
        }

        // Garante dano mínimo absoluto
        dano = Math.max(30, dano);

        return dano;
    }

    /**
     * Calcula o dano máximo do ataque básico para comparação
     */
    private int calcularDanoBasicoMaximo(int ataqueBase, int nivel) {
        int multiplicadorEstagio = Math.max(1, estagioAtual / 2);
        int dano = ataqueBase * 3 * multiplicadorEstagio;
        dano = (int)(dano * 1.15);
        return Math.min(80, dano);
    }

    private int calcularCura() {
        int vidaMax = usuario.getVidaMax();
        int cura = (int)(vidaMax * CURA_PERCENT);
        cura += conhecimento * 2;
        return Math.max(20, Math.min(150, cura));
    }

    @Override
    public int executar(Inimigo alvo) {
        if (!podeUsar()) {
            System.out.println("❌ " + usuario.getNome() + " não tem Mana suficiente!");
            return 0;
        }

        AtributoEspecial attr = (AtributoEspecial) usuario;
        int custo = ValoresHabilidade.CUSTO_SABIO;

        if (!attr.consumir(custo)) {
            if (attr.getValorAtual() > 10) {
                System.out.println("⚠️ Mana baixa! Usando versão reduzida...");
                custo = attr.getValorAtual();
                attr.consumir(custo);
            } else {
                System.out.println("❌ Mana insuficiente! Habilidade cancelada.");
                return 0;
            }
        }

        conhecimento++;

        int vidaMaxInimigo = alvo.getVidaMax();
        int vidaAtualInimigo = alvo.getVida();
        int defesaInimigo = alvo.getDefesa();
        int vidaMaxUsuario = usuario.getVidaMax();
        int vidaAtualUsuario = usuario.getVida();
        int danoBasicoMax = calcularDanoBasicoMaximo(usuario.getAtaque(), usuario.getNivel());

        int dano = calcularDanoVerdadeiro(alvo);
        int cura = calcularCura();

        // Não mata em 1 hit
        if (dano >= vidaAtualInimigo) {
            dano = (int)(vidaAtualInimigo * 0.85);
            if (dano < 5) dano = vidaAtualInimigo - 1;
        }

        if (cura + vidaAtualUsuario > vidaMaxUsuario) {
            cura = vidaMaxUsuario - vidaAtualUsuario;
        }

        double percentualDano = (double) dano / vidaMaxInimigo * 100;

        System.out.println("\n" + "=".repeat(60));
        System.out.println("📜📜📜 SABEDORIA ANCESTRAL 📜📜📜");
        System.out.println("=".repeat(60));
        System.out.println("💙 Consumiu " + custo + " de Mana");
        System.out.println("📚 Conhecimento acumulado: " + conhecimento);
        System.out.println("⚔️ Ataque básico máximo: " + danoBasicoMax);

        System.out.println("\n⚡⚡⚡ DANO VERDADEIRO! ⚡⚡⚡");
        System.out.println("   🛡️ Defesa do inimigo: " + defesaInimigo);
        System.out.println("   💥 DEFESA IGNORADA! Dano: " + dano);
        System.out.println("   📊 " + String.format("%.1f", percentualDano) + "% da vida do inimigo");

        System.out.println("\n💚 CURA SÁBIA:");
        System.out.println("   ❤️ Vida antes: " + vidaAtualUsuario + "/" + vidaMaxUsuario);
        System.out.println("   💚 Cura: +" + cura);

        alvo.tomarDano(dano);
        usuario.curar(cura);

        System.out.println("\n📊 STATUS FINAL:");
        System.out.println("   👤 " + usuario.getNome() + ": " + usuario.getVida() + "/" + vidaMaxUsuario + " ❤️");
        System.out.println("   👾 " + alvo.getNome() + ": " + alvo.getVida() + "/" + vidaMaxInimigo + " ❤️");

        recarregarAposAcerto();
        System.out.println("\n✨ +" + ValoresHabilidade.RECUPERACAO_SABIO + " de Mana recuperado!");
        System.out.println("📊 Mana restante: " + attr.getValorAtual() + "/" + attr.getValorMaximo());
        System.out.println("📚 Conhecimento: " + conhecimento);
        System.out.println("=".repeat(60));

        return dano;
    }

    public void setEstagioAtual(int estagio) {
        this.estagioAtual = estagio;
    }

    public int getConhecimento() {
        return conhecimento;
    }
}