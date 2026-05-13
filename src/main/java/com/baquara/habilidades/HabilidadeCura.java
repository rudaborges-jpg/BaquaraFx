package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;

public class HabilidadeCura implements HabilidadeEspecial {
    private Personagem usuario;
    private int cooldownMaximo;
    private int cooldownAtual;
    private String nome;
    private String descricao;

    public HabilidadeCura(Personagem usuario, int cooldown) {
        this.usuario = usuario;
        this.cooldownMaximo = cooldown;
        this.cooldownAtual = 0;
        this.nome = "ESPÍRITO SAGRADO";
        atualizarDescricao();
    }

    private void atualizarDescricao() {
        int ataque = usuario.getAtaque();
        int curaEstimada = (int)(ataque * 1.5);
        int danoEstimado = (int)(ataque * 0.8);
        this.descricao = "Invoca luz divina para curar ~" + curaEstimada +
                " de vida e causar ~" + danoEstimado + " de dano sagrado.\n" +
                "   📿 Cura e dano aumentam com Poder Divino restante!";
    }

    @Override
    public String getNome() { return nome; }

    @Override
    public String getDescricao() {
        atualizarDescricao(); // Atualiza com o ataque atual
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
        int custo = 30;

        // Verifica se tem recurso suficiente
        if (!attr.consumir(custo)) {
            if (attr.getValorAtual() > 15) {
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
        int bonusPoder = attr.getValorAtual() / 4;

        // Cura = 1.5x o ataque + bônus do poder divino
        int curaTotal = (int)(ataque * 1.5) + bonusPoder;

        System.out.println("\n🙏 " + usuario.getNome() + " invoca o ESPÍRITO SAGRADO!");
        System.out.println("💙 Consumiu " + custo + " de " + attr.getNomeAtributo() +
                " (Restante: " + attr.getValorAtual() + "/" + attr.getValorMaximo() + ")");
        System.out.println("⚔️ Ataque base: " + ataque + " → Cura: " + curaTotal);

        usuario.curar(curaTotal);
        System.out.println("💚 Curou " + curaTotal + " de vida!");

        // Dano = 0.8x o ataque + bônus do poder divino
        int dano = (int)(ataque * 0.8) + bonusPoder;
        System.out.println("⚡ Luz divina atinge " + alvo.getNome() + " causando " + dano + " de dano!");
        alvo.tomarDano(dano);

        // Recupera um pouco do poder divino
        attr.recarregar(5);
        System.out.println("✨ +5 de " + attr.getNomeAtributo() + " recuperado!");

        cooldownAtual = cooldownMaximo;
        return dano;
    }

    @Override
    public void reduzirCooldown() {
        if (cooldownAtual > 0) cooldownAtual--;
    }

    @Override
    public void resetarCooldown() { cooldownAtual = 0; }
}