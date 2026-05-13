// 📁 modelo/Arcanista.java (CORRIGIDO)
package com.baquara.modelo;

import java.util.Random;

public class Arcanista extends Personagem implements AtributoEspecial {
    private int poderArcano;
    private int poderMaximo;
    private Random random;
    private String[] elementos;

    public Arcanista() {
        super(PerTipo.ARCANISTA, "Arcanista", 95, 32, 8);  // Defesa muito baixa: 8
        this.poderArcano = 100;
        this.poderMaximo = 100;
        this.random = new Random();
        this.elementos = new String[]{"🔥 Fogo", "❄️ Gelo", "⚡ Raio", "🌑 Sombra", "✨ Luz"};
    }

    public int getPoderArcano() { return poderArcano; }
    public int getPoderMaximo() { return poderMaximo; }

    public boolean consumirPoderArcano(int quantidade) {
        if (poderArcano >= quantidade) {
            poderArcano -= quantidade;
            System.out.println("✨ Poder Arcano consumido: " + quantidade +
                    " (Restante: " + poderArcano + "/" + poderMaximo + ")");
            return true;
        }
        System.out.println("❌ Poder Arcano insuficiente! (" + poderArcano + "/" + poderMaximo + ")");
        return false;
    }

    private void acumularPoder() {
        if (poderArcano < poderMaximo) {
            int acumulo = 3;
            poderArcano = Math.min(poderMaximo, poderArcano + acumulo);
        }
    }

    @Override
    public void recarregarPorEstagio(int estagioNumero) {
        int recarga = estagioNumero * 10;
        poderArcano = Math.min(poderMaximo, poderArcano + recarga);
        System.out.println("🔮 Poder Arcano recarregado: +" + recarga + " (" + poderArcano + "/" + poderMaximo + ")");
    }

    public void recarregarPorNivel(int novoNivel) {
        poderMaximo += 12;
        poderArcano = poderMaximo;
        System.out.println("🌟 Poder Arcano máximo: " + poderMaximo + " | Completamente restaurado!");
    }


    public void atacar(Personagem alvo) {
        String elemento = elementos[random.nextInt(elementos.length)];

        int bonusPoder = 0;
        if (poderArcano >= 5) {
            consumirPoderArcano(5);
            bonusPoder = 15;
        }

        int dano = ataque + (nivel * 2) + bonusPoder;
        System.out.println("🔮 " + nome + " conjura " + elemento + " causando " + dano + " de dano!");
        alvo.tomarDano(dano);

        acumularPoder();
    }

    @Override
    public void usarHabilidadeEspecial(Personagem alvo) {
        System.out.println("\n🌌 " + nome + " libera CATACLISMO ARCANO!");

        int custo = 50;
        if (!consumirPoderArcano(custo)) {
            if (poderArcano > 20) {
                System.out.println("⚠️ Poder Arcano baixo! Usando versão reduzida...");
                custo = poderArcano;
                poderArcano = 0;
            } else {
                System.out.println("❌ Poder Arcano insuficiente! Habilidade cancelada.");
                return;
            }
        }

        String elemento = elementos[random.nextInt(elementos.length)];
        int dano = ataque + (nivel * 3) + (custo * 2);

        System.out.println("💥 " + elemento + " explode causando " + dano + " de dano catastrófico!");
        alvo.tomarDano(dano);

        // Efeito elemental
        aplicarEfeitoElemental(elemento, alvo);

        acumularPoder();
    }

    private void aplicarEfeitoElemental(String elemento, Personagem alvo) {
        switch (elemento.split(" ")[0]) {
            case "🔥":
                int danoQueimadura = 15;
                System.out.println("🔥 Queimadura! +" + danoQueimadura + " de dano adicional!");
                alvo.tomarDano(danoQueimadura);
                break;
            case "❄️":
                System.out.println("❄️ Congelamento! Inimigo tem ataque reduzido!");
                break;
            case "⚡":
                System.out.println("⚡ Atordoamento! Inimigo perde a próxima ação!");
                break;
            case "🌑":
                int danoSombra = 10;
                System.out.println("🌑 Dreno de vida! +" + danoSombra + " de dano!");
                alvo.tomarDano(danoSombra);
                this.curar(danoSombra / 2);
                break;
            case "✨":
                System.out.println("✨ Purificação! Você recupera 20 de vida!");
                this.curar(20);
                break;
        }
    }

    @Override
    public String getNomeHabilidade() {
        return "CATACLISMO ARCANO";
    }

    @Override
    public String getDescricaoHabilidade() {
        return "Libera poder arcano causando dano massivo com efeitos elementais aleatórios.";
    }

    @Override
    public void mostrarStatus() {
        super.mostrarStatus();
        System.out.println("   🔮 Poder Arcano: " + poderArcano + "/" + poderMaximo);
    }
    // ============ IMPLEMENTAÇÃO DE AtributoEspecial ============

    @Override
    public String getNomeAtributo() {
        return "Poder Arcano";
    }

    @Override
    public int getValorAtual() {
        return poderArcano;
    }

    @Override
    public int getValorMaximo() {
        return poderMaximo;
    }

    @Override
    public double getPorcentagem() {
        return (double) poderArcano / poderMaximo * 100;
    }

    @Override
    public boolean consumir(int quantidade) {
        return consumirPoderArcano(quantidade);
    }

    @Override
    public void recarregar(int quantidade) {
        poderArcano = Math.min(poderMaximo, poderArcano + quantidade);
    }

    @Override
    public void recarregarCompletamente() {
        poderArcano = poderMaximo;
    }
}