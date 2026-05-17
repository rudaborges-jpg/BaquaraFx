package com.baquara.modelo;

import com.baquara.habilidades.HabilidadeEspecial;

public abstract class Personagem {
    protected String nome;
    protected PerTipo tipo;
    protected int vida;
    protected int vidaMax;
    protected int ataque;
    protected int defesa;
    protected int nivel;
    protected int experiencia;

    // ⭐ REMOVIDOS: spatkCooldown e atualCooldown
    // protected int spatkCooldown;
    // protected int atualCooldown;

    protected HabilidadeEspecial habilidade;

    public Personagem(PerTipo tipo, String nome, int vida, int ataque, int defesa) {
        this.tipo = tipo;
        this.nome = nome;
        this.vida = vida;
        this.vidaMax = vida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.nivel = 1;
        this.experiencia = 0;
        // ⭐ REMOVIDO: this.spatkCooldown = 3;
        // ⭐ REMOVIDO: this.atualCooldown = 0;
    }

    // Getters
    public String getNome() { return nome; }
    public PerTipo getTipo() { return tipo; }
    public int getVida() { return vida; }
    public int getVidaMax() { return vidaMax; }
    public int getAtaque() { return ataque; }
    public int getDefesa() { return defesa; }
    public int getNivel() { return nivel; }
    public int getExperiencia() { return experiencia; }

    // ⭐ REMOVIDOS: taProntaHabilidade(), getCooldownAtual()
    // public boolean taProntaHabilidade() { return atualCooldown == 0; }

    public void setHabilidade(HabilidadeEspecial habilidade) {
        this.habilidade = habilidade;
    }

    public HabilidadeEspecial getHabilidade() {
        return habilidade;
    }

    // ⭐ REMOVIDO - não precisa mais verificar cooldown
    // public boolean isHabilidadePronta() {
    //     return habilidade != null && habilidade.estaPronta();
    // }

    // ⭐ REMOVIDOS: getCooldownAtual(), reduzirCooldownHabilidade(), resetarCooldownHabilidade()
    // public int getCooldownAtual() {
    //     return habilidade != null ? habilidade.getCooldownAtual() : 0;
    // }
    //
    // public void reduzirCooldownHabilidade() {
    //     if (habilidade != null) {
    //         habilidade.reduzirCooldown();
    //     }
    // }
    //
    // public void resetarCooldownHabilidade() {
    //     if (habilidade != null) {
    //         habilidade.resetarCooldown();
    //     }
    // }

    public int usarHabilidade(Inimigo alvo) {
        if (habilidade != null && habilidade.podeUsar()) {
            return habilidade.executar(alvo);
        }
        System.out.println("❌ Habilidade não disponível!");
        return 0;
    }

    public void tomarDano(int danoBruto) {
        double reducao = (double) defesa / (defesa + 50);
        int danoMitigado = (int) (danoBruto * reducao);
        int danoFinal = danoBruto - danoMitigado;

        if (danoFinal < 1) danoFinal = 1;

        this.vida -= danoFinal;
        if (this.vida < 0) this.vida = 0;

        System.out.println("⚔️ " + nome + " sofreu " + danoFinal + " de dano! (❤️ " + vida + "/" + vidaMax + ")");

        if (danoMitigado > 0) {
            int percentualReducao = (int)(reducao * 100);
            System.out.println("   🛡️ Defesa " + defesa + " mitigou " + danoMitigado +
                    " de dano (" + percentualReducao + "% de redução)");
        }
    }

    public void curar(int quanto) {
        this.vida += quanto;
        if (this.vida > vidaMax) this.vida = vidaMax;
        System.out.println("💚 " + nome + " recuperou " + quanto + " de vida! (❤️ " + vida + "/" + vidaMax + ")");
    }

    public int ataqueInimigo(Personagem inimigo) {
        int dano = ataque + (nivel * 2);
        System.out.println("🗡️ " + nome + " ataca " + inimigo.getNome() + " causando " + dano + " de dano!");
        inimigo.tomarDano(dano);
        return dano;
    }

    public boolean vivo() { return vida > 0; }

    public abstract void usarHabilidadeEspecial(Personagem alvo);
    public abstract String getNomeHabilidade();
    public abstract String getDescricaoHabilidade();
    public abstract void recarregarPorEstagio(int estagioNumero);
    public abstract void recarregarPorNivel(int novoNivel);

    public void addExperiencia(int exp) {
        this.experiencia += exp;
        System.out.println("📚 " + nome + " ganhou " + exp + " de experiência!");
        int expNecessario = nivel * 100;
        if (experiencia >= expNecessario) {
            levelUp();
        }
    }

    protected void levelUp() {
        nivel++;
        experiencia = 0;

        int aumentoVida = 40;
        vidaMax += aumentoVida;
        vida += aumentoVida;

        ataque += 5;
        defesa += 3;
        System.out.println("\n🎉 " + nome + " subiu para o NÍVEL " + nivel + "!");
        System.out.println("   ❤️ Vida +" + aumentoVida + " | ⚔️ Ataque +5 | 🛡️ Defesa +3 (Total: " + defesa + ")");

        recarregarPorNivel(nivel);
    }

    public void mostrarStatus() {
        double reducao = (double) defesa / (defesa + 50);
        int percentualReducao = (int)(reducao * 100);

        System.out.println("\n👤 " + nome + " (Nv." + nivel + ")");
        System.out.println("   ❤️ Vida: " + vida + "/" + vidaMax);
        System.out.println("   ⚔️ Ataque: " + ataque);
        System.out.println("   🛡️ Defesa: " + defesa + " (reduz " + percentualReducao + "% do dano)");

        if (habilidade != null) {
            AtributoEspecial attr = (AtributoEspecial) this;
            System.out.println("   ✨ " + habilidade.getNome() + " [" + attr.getNomeAtributo() + ": " +
                    attr.getValorAtual() + "/" + attr.getValorMaximo() + "]");
        }
    }

    @Override
    public String toString() {
        return nome + " (Nv." + nivel + " - " + tipo.getNome() + ")";
    }
}