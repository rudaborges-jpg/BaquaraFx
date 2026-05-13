package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;
import java.util.Random;

public class HabilidadeDestruicaoTotal implements HabilidadeEspecial {
    private Personagem usuario;
    private int cooldownMaximo;
    private int cooldownAtual;
    private String nome;
    private String descricao;
    private Random random;
    private String[] elementos;

    public HabilidadeDestruicaoTotal(Personagem usuario, int cooldown) {
        this.usuario = usuario;
        this.cooldownMaximo = cooldown;
        this.cooldownAtual = 0;
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
                "   🎲 Efeito elemental aleatório adicional!";
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
        int custo = 50;

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

        // Recupera um pouco de poder arcano
        attr.recarregar(3);
        System.out.println("✨ +3 de " + attr.getNomeAtributo() + " recuperado!");

        cooldownAtual = cooldownMaximo;
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

    @Override
    public void reduzirCooldown() {
        if (cooldownAtual > 0) cooldownAtual--;
    }

    @Override
    public void resetarCooldown() { cooldownAtual = 0; }
}