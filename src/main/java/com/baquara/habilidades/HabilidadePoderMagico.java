package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;

public class HabilidadePoderMagico implements HabilidadeEspecial {
    private Personagem usuario;
    private int cooldownMaximo;
    private int cooldownAtual;
    private String nome;
    private String descricao;
    private int conhecimento;

    public HabilidadePoderMagico(Personagem usuario, int cooldown) {
        this.usuario = usuario;
        this.cooldownMaximo = cooldown;
        this.cooldownAtual = 0;
        this.nome = "SABEDORIA ANCESTRAL";
        this.conhecimento = 0;
        atualizarDescricao();
    }

    private void atualizarDescricao() {
        int ataque = usuario.getAtaque();
        int danoEstimado = (int)(ataque * 2.2);
        int curaEstimada = (int)(ataque * 1.2);
        this.descricao = "Conjura sabedoria arcana causando ~" + danoEstimado +
                " de dano e curando ~" + curaEstimada + ".\n" +
                "   📚 Conhecimento acumulado aumenta poder!\n" +
                "   💙 Recupera mana após o uso!";
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
        int custo = 30;

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

        conhecimento++;

        // Cálculos baseados no ATAQUE do personagem
        int ataque = usuario.getAtaque();
        int bonusConhecimento = conhecimento * 3;
        int bonusMana = attr.getValorAtual() / 3;

        // Dano = 2.2x o ataque + bônus
        int dano = (int)(ataque * 2.2) + bonusConhecimento + bonusMana;

        System.out.println("\n📜 " + usuario.getNome() + " conjura SABEDORIA ANCESTRAL!");
        System.out.println("💙 Consumiu " + custo + " de " + attr.getNomeAtributo() +
                " (Restante: " + attr.getValorAtual() + "/" + attr.getValorMaximo() + ")");
        System.out.println("⚔️ Ataque base: " + ataque + " × 2.2 = " + (int)(ataque * 2.2));
        System.out.println("📚 Conhecimento x" + conhecimento + " (+" + bonusConhecimento + " de dano)");
        System.out.println("💙 Bônus de mana: +" + bonusMana + " de dano");
        System.out.println("💥 Explosão arcana causa " + dano + " de dano!");
        alvo.tomarDano(dano);

        // Cura = 1.2x o ataque + bônus de conhecimento
        int cura = (int)(ataque * 1.2) + (conhecimento * 4);
        usuario.curar(cura);
        System.out.println("💚 Curou " + cura + " de vida!");

        // Recupera mana
        int recuperacao = 10 + (conhecimento / 2);
        attr.recarregar(recuperacao);
        System.out.println("🔮 +" + recuperacao + " de " + attr.getNomeAtributo() + " recuperado!");

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