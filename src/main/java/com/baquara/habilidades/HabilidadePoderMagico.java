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

    // Constantes de balanceamento
    private static final double DANO_BASE_PERCENT = 0.12;      // Dano base: 12% da vida
    private static final double DANO_MIN_PERCENT = 0.08;       // Dano mínimo: 8% da vida
    private static final double DANO_MAX_PERCENT = 0.18;       // Dano máximo: 18% da vida
    private static final double BONUS_CONHECIMENTO = 0.01;     // +1% por ponto de conhecimento (máx +10%)
    private static final double CURA_PERCENT = 0.10;           // Cura: 10% da vida do Sábio

    public HabilidadePoderMagico(Personagem usuario) {
        this.usuario = usuario;
        this.random = new Random();
        this.nome = "SABEDORIA ANCESTRAL";
        this.conhecimento = 0;
        atualizarDescricao();
    }

    private void atualizarDescricao() {
        double danoAtual = DANO_BASE_PERCENT + (conhecimento * BONUS_CONHECIMENTO);
        if (danoAtual > 0.28) danoAtual = 0.28;

        this.descricao = "📜 Conjura sabedoria ancestral com DANO VERDADEIRO!\n\n" +
                "   ⚡⚡⚡ CARACTERÍSTICAS ÚNICAS: ⚡⚡⚡\n" +
                "   💥 DANO VERDADEIRO: Ignora COMPLETAMENTE a defesa do inimigo!\n" +
                "   📊 Dano base: " + String.format("%.0f", DANO_BASE_PERCENT * 100) + "% da vida do inimigo\n" +
                "   📈 Bônus por conhecimento: +" + String.format("%.0f", BONUS_CONHECIMENTO * 100) + "% por ponto\n" +
                "   💚 Cura: " + String.format("%.0f", CURA_PERCENT * 100) + "% da sua vida máxima\n\n" +
                "   ✨ Quanto mais conhecimento acumulado, mais forte fica!\n" +
                "   🛡️ A DEFESA DO INIMIGO NÃO FUNCIONA CONTRA ESTA MAGIA!\n\n" +
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
     * Calcula dano verdadeiro baseado na vida do inimigo (ignora defesa)
     */
    private int calcularDanoVerdadeiro(Inimigo alvo) {
        int vidaMax = alvo.getVidaMax();

        // Calcula percentual de dano baseado no conhecimento
        double percentualBase = DANO_BASE_PERCENT + (conhecimento * BONUS_CONHECIMENTO);

        // Aplica variação aleatória (±20%)
        double variacao = 0.8 + (random.nextDouble() * 0.4);
        double percentualFinal = percentualBase * variacao;

        // Limita entre mínimo e máximo
        percentualFinal = Math.max(DANO_MIN_PERCENT, Math.min(DANO_MAX_PERCENT + (conhecimento * BONUS_CONHECIMENTO * 0.5), percentualFinal));
        percentualFinal = Math.min(0.35, percentualFinal); // Máximo absoluto 35%

        int dano = (int)(vidaMax * percentualFinal);

        // Garante dano mínimo
        dano = Math.max(15, dano);

        return dano;
    }

    /**
     * Calcula cura baseada na vida do Sábio
     */
    private int calcularCura() {
        int vidaMax = usuario.getVidaMax();
        int cura = (int)(vidaMax * CURA_PERCENT);

        // Bônus de conhecimento na cura
        cura += conhecimento * 2;

        // Garante cura mínima e máxima
        cura = Math.max(15, Math.min(120, cura));

        return cura;
    }

    @Override
    public int executar(Inimigo alvo) {
        if (!podeUsar()) {
            System.out.println("❌ " + usuario.getNome() + " não tem Mana suficiente!");
            return 0;
        }

        AtributoEspecial attr = (AtributoEspecial) usuario;
        int custo = ValoresHabilidade.CUSTO_SABIO;

        // Consome Mana
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

        // Aumenta conhecimento (acumula para sempre)
        conhecimento++;

        int vidaMaxInimigo = alvo.getVidaMax();
        int vidaAtualInimigo = alvo.getVida();
        int defesaInimigo = alvo.getDefesa();
        int vidaMaxUsuario = usuario.getVidaMax();
        int vidaAtualUsuario = usuario.getVida();

        // Calcula dano verdadeiro (ignora defesa)
        int dano = calcularDanoVerdadeiro(alvo);
        int cura = calcularCura();

        // Garante que não mata em 1 hit (deixa pelo menos 10% de vida)
        if (dano >= vidaAtualInimigo) {
            dano = (int)(vidaAtualInimigo * 0.9);
            if (dano < 5) dano = vidaAtualInimigo - 1;
        }

        // Garante que não cura acima do máximo
        if (cura + vidaAtualUsuario > vidaMaxUsuario) {
            cura = vidaMaxUsuario - vidaAtualUsuario;
        }

        double percentualDano = (double) dano / vidaMaxInimigo * 100;

        System.out.println("\n" + "=".repeat(60));
        System.out.println("📜📜📜 SABEDORIA ANCESTRAL 📜📜📜");
        System.out.println("=".repeat(60));
        System.out.println("💙 Consumiu " + custo + " de Mana");
        System.out.println("📚 Conhecimento acumulado: " + conhecimento);

        System.out.println("\n⚡⚡⚡ DANO VERDADEIRO! ⚡⚡⚡");
        System.out.println("   🛡️ Defesa do inimigo: " + defesaInimigo);
        System.out.println("   💥 DEFESA IGNORADA! Dano causado: " + dano);
        System.out.println("   📊 " + String.format("%.1f", percentualDano) + "% da vida do inimigo");

        System.out.println("\n💚 CURA SÁBIA:");
        System.out.println("   ❤️ Vida antes: " + vidaAtualUsuario + "/" + vidaMaxUsuario);
        System.out.println("   💚 Cura: +" + cura);

        // Aplica dano (sem redução de defesa!)
        System.out.println("\n💥 APLICANDO DANO VERDADEIRO...");
        alvo.tomarDano(dano);

        // Aplica cura
        usuario.curar(cura);

        // Mostra estatísticas finais
        System.out.println("\n📊 STATUS FINAL:");
        System.out.println("   👤 " + usuario.getNome() + ": " + usuario.getVida() + "/" + vidaMaxUsuario + " ❤️");
        System.out.println("   👾 " + alvo.getNome() + ": " + alvo.getVida() + "/" + vidaMaxInimigo + " ❤️");

        // Recupera Mana
        recarregarAposAcerto();
        System.out.println("\n✨ +" + ValoresHabilidade.RECUPERACAO_SABIO + " de Mana recuperado!");
        System.out.println("📊 Mana restante: " + attr.getValorAtual() + "/" + attr.getValorMaximo());
        System.out.println("📚 Conhecimento: " + conhecimento);
        System.out.println("=".repeat(60));

        return dano;
    }

    public int getConhecimento() {
        return conhecimento;
    }

    public void resetarConhecimento() {
        this.conhecimento = 0;
        System.out.println("📚 Conhecimento ancestral foi resetado!");
    }
}