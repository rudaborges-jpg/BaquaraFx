// 📁 habilidades/HabilidadeDestruicaoTotal.java

package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;
import com.baquara.modelo.ValoresHabilidade;
import java.util.Random;

public class HabilidadeDestruicaoTotal implements HabilidadeEspecial {
    private Personagem usuario;
    private String nome;
    private String descricao;
    private Random random;
    private String[] elementos;

    public HabilidadeDestruicaoTotal(Personagem usuario) {
        this.usuario = usuario;
        this.random = new Random();
        this.nome = "CATACLISMO ARCANO";
        this.elementos = new String[]{"🔥 Fogo", "❄️ Gelo", "⚡ Raio", "🌑 Sombra", "✨ Luz"};
        atualizarDescricao();
    }

    private void atualizarDescricao() {
        int ataque = usuario.getAtaque();
        int danoEstimado = (int)(ataque * 2.5);
        this.descricao = "Libera poder arcano massivo causando ~" + danoEstimado +
                " de dano elemental.\n" +
                "   💥 Dano aumenta com Poder Arcano consumido!\n" +
                "   🎲 Efeito elemental aleatório adicional!\n" +
                "   💫 Custo: " + ValoresHabilidade.CUSTO_ARCANISTA + " de Poder Arcano\n" +
                "   ✨ Recupera " + ValoresHabilidade.RECUPERACAO_ARCANISTA + " de Poder Arcano por acerto!";
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
        System.out.println("✨ +" + ValoresHabilidade.RECUPERACAO_ARCANISTA + " de " + attr.getNomeAtributo() + " recuperado!");
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
        int custo = ValoresHabilidade.CUSTO_ARCANISTA;

        // Verifica se tem recurso suficiente
        if (!attr.consumir(custo)) {
            if (attr.getValorAtual() > 20) {
                System.out.println("⚠️ " + attr.getNomeAtributo() + " baixo! Usando versão reduzida...");
                custo = attr.getValorAtual();
                attr.consumir(custo);
            } else {
                System.out.println("❌ " + attr.getNomeAtributo() + " insuficiente! Habilidade cancelada.");
                return 0;
            }
        }

        String elemento = elementos[random.nextInt(elementos.length)];

        // Cálculos baseados no ATAQUE do personagem
        int ataque = usuario.getAtaque();

        // Dano = 2.5x o ataque + (custo × 2)
        int dano = (int)(ataque * 2.5) + (custo * 2);

        // Multiplicador aleatório
        double multiplicador = 0.85 + (random.nextDouble() * 0.3);
        dano = (int)(dano * multiplicador);

        System.out.println("\n🌌 " + usuario.getNome() + " libera CATACLISMO ARCANO!");
        System.out.println("💙 Consumiu " + custo + " de " + attr.getNomeAtributo() +
                " (Restante: " + attr.getValorAtual() + "/" + attr.getValorMaximo() + ")");
        System.out.println("⚔️ Ataque base: " + ataque + " × 2.5 = " + (int)(ataque * 2.5));
        System.out.println("💫 Bônus de poder: +" + (custo * 2) + " de dano");
        System.out.println("💥 " + elemento + " explode causando " + dano + " de dano catastrófico!");
        alvo.tomarDano(dano);

        // Efeito elemental
        aplicarEfeitoElemental(elemento, alvo);

        return dano;
    }

    private void aplicarEfeitoElemental(String elemento, Inimigo alvo) {
        int ataque = usuario.getAtaque();

        if (elemento.startsWith("🔥")) {
            int danoQueimadura = (int)(ataque * 0.5);
            System.out.println("🔥 Queimadura! +" + danoQueimadura + " de dano!");
            alvo.tomarDano(danoQueimadura);
        } else if (elemento.startsWith("❄️")) {
            System.out.println("❄️ Congelamento! Inimigo tem ataque reduzido!");
        } else if (elemento.startsWith("⚡")) {
            System.out.println("⚡ Atordoamento! Inimigo perde a próxima ação!");
        } else if (elemento.startsWith("🌑")) {
            int danoSombra = (int)(ataque * 0.4);
            System.out.println("🌑 Dreno de vida! +" + danoSombra + " de dano!");
            alvo.tomarDano(danoSombra);
        } else if (elemento.startsWith("✨")) {
            int curaLuz = (int)(ataque * 0.6);
            System.out.println("✨ Purificação! Você recupera " + curaLuz + " de vida!");
            usuario.curar(curaLuz);
        }
    }
}