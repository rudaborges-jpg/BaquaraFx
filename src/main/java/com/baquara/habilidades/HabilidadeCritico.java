package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;
import java.util.Random;

public class HabilidadeCritico implements HabilidadeEspecial {
    private Personagem usuario;
    private int cooldownMaximo;
    private int cooldownAtual;
    private String nome;
    private String descricao;
    private Random random;

    public HabilidadeCritico(Personagem usuario, int cooldown) {
        this.usuario = usuario;
        this.cooldownMaximo = cooldown;
        this.cooldownAtual = 0;
        this.random = new Random();
        this.nome = "CHUVA DE FLECHAS";
        atualizarDescricao();
    }

    private void atualizarDescricao() {
        int ataque = usuario.getAtaque();
        int danoEstimado = (int)(ataque * 1.8);
        this.descricao = "Dispara múltiplas flechas causando ~" + danoEstimado +
                " de dano + sangramento.\n" +
                "   🎯 Chance de crítico aumenta com Penetração restante!";
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
        int custo = 35;

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
        attr.recarregar(5);
        System.out.println("✨ +5 de " + attr.getNomeAtributo() + " recuperado!");

        cooldownAtual = cooldownMaximo;
        return dano + sangramento;
    }

    @Override
    public void reduzirCooldown() {
        if (cooldownAtual > 0) cooldownAtual--;
    }

    @Override
    public void resetarCooldown() { cooldownAtual = 0; }
}