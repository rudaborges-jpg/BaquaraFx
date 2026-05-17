// 📁 modelo/Inimigo.java
package com.baquara.modelo;

public class Inimigo {
    private String nome;
    private int vida;
    private int vidaMax;
    private int ataque;
    private int defesa;
    private int nivel;

    public Inimigo(String nome, int vida, int ataque, int nivel) {
        this.nome = nome;
        this.vida = vida;
        this.vidaMax = vida;
        this.ataque = ataque;
        this.nivel = nivel;
        this.defesa = 5 + (nivel * 3);
    }

    public Inimigo(String nome, int vida, int ataque, int defesa, int nivel) {
        this.nome = nome;
        this.vida = vida;
        this.vidaMax = vida;
        this.ataque = ataque;
        this.defesa = defesa;
        this.nivel = nivel;
    }

    public String getNome() { return nome; }
    public int getVida() { return vida; }
    public int getVidaMax() { return vidaMax; }
    public int getAtaque() { return ataque; }
    public int getDefesa() { return defesa; }
    public int getNivel() { return nivel; }

    public void tomarDano(int danoBruto) {
        double reducao = (double) defesa / (defesa + 50);
        int danoMitigado = (int) (danoBruto * reducao);
        int danoFinal = danoBruto - danoMitigado;

        if (danoFinal < 1) danoFinal = 1;

        this.vida -= danoFinal;
        if (this.vida < 0) this.vida = 0;

        System.out.println("💀 " + nome + " perdeu " + danoFinal + " de vida! (❤️ " + vida + "/" + vidaMax + ")");

        if (danoMitigado > 0) {
            System.out.println("   🛡️ Defesa " + defesa + " de " + nome + " mitigou " + danoMitigado + " de dano");
        }
    }

    public boolean vivo() { return vida > 0; }

    public void mostrarStatus() {
        double reducao = (double) defesa / (defesa + 50);
        int percentualReducao = (int)(reducao * 100);

        System.out.println("👾 " + nome + " | ❤️ " + vida + "/" + vidaMax +
                " | ⚔️ " + ataque + " | 🛡️ " + defesa +
                " (reduz " + percentualReducao + "%)");
    }
}