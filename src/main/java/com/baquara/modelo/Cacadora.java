package com.baquara.modelo;

import java.util.Random;

public class Cacadora extends Personagem implements AtributoEspecial {
    private int penetracao;
    private int penetracaoMaxima;
    private int flechasPrecisas;
    private Random random;
    private double chanceCritico;

    public Cacadora() {
        super(PerTipo.CACADORA, "Caçadora", 110, 30, 12);  // Defesa baixa: 12
        this.penetracaoMaxima = 100;
        this.penetracao = 100;
        this.flechasPrecisas = 0;
        this.random = new Random();
        this.chanceCritico = 0.30;
    }

    public int getPenetracao() { return penetracao; }
    public int getPenetracaoMaxima() { return penetracaoMaxima; }
    public int getFlechasPrecisas() { return flechasPrecisas; }

    public boolean consumirPenetracao(int quantidade) {
        if (penetracao >= quantidade) {
            penetracao -= quantidade;
            flechasPrecisas++;
            System.out.println("🎯 Penetração consumida: " + quantidade +
                    " (Restante: " + penetracao + "/" + penetracaoMaxima + ")");
            return true;
        }
        System.out.println("❌ Penetração insuficiente! (" + penetracao + "/" + penetracaoMaxima + ")");
        return false;
    }

    @Override
    public void recarregarPorEstagio(int estagioNumero) {
        int recarga = estagioNumero * 8;
        penetracao = Math.min(penetracaoMaxima, penetracao + recarga);
        System.out.println("🏹 Penetração recarregada: +" + recarga + " (" + penetracao + "/" + penetracaoMaxima + ")");
    }

    @Override
    public void recarregarPorNivel(int novoNivel) {
        penetracaoMaxima += 10;
        penetracao = penetracaoMaxima;
        chanceCritico = Math.min(0.50, 0.30 + (novoNivel * 0.02));
        System.out.println("🎯 Penetração máxima: " + penetracaoMaxima + " | Restaurada!");
        System.out.println("💫 Chance de crítico: " + (int)(chanceCritico * 100) + "%");
    }

    @Override
    public void usarHabilidadeEspecial(Personagem alvo) {
        System.out.println("\n🌿 " + nome + " usa CHUVA DE FLECHAS!");

        int custo = 35;
        if (!consumirPenetracao(custo)) {
            if (penetracao > 10) {
                System.out.println("⚠️ Penetração baixa! Usando versão reduzida...");
                custo = penetracao;
                penetracao = 0;
            } else {
                System.out.println("❌ Penetração insuficiente! Habilidade cancelada.");
                return;
            }
        }

        int dano = 45 + (nivel * 4);
        boolean critico = random.nextDouble() < chanceCritico;

        if (critico) {
            dano = (int)(dano * 1.5);
            System.out.print("🎯 GOLPE CRÍTICO! ");
        }

        System.out.println("💥 Rajada de flechas causa " + dano + " de dano!");
        alvo.tomarDano(dano);

        int sangramento = 10 + (flechasPrecisas * 2);
        System.out.println("🩸 Sangramento! +" + sangramento + " de dano adicional!");
        alvo.tomarDano(sangramento);

        System.out.println("🏹 Flechas precisas: " + flechasPrecisas);
        penetracao = Math.min(penetracaoMaxima, penetracao + 5);
    }

    @Override
    public String getNomeHabilidade() {
        return "CHUVA DE FLECHAS";
    }

    @Override
    public String getDescricaoHabilidade() {
        return "Causa " + (45 + (nivel * 4)) + " de dano + sangramento.\n" +
                "   🎯 " + (int)(chanceCritico * 100) + "% de chance de crítico!";
    }

    @Override
    public void mostrarStatus() {
        super.mostrarStatus();
        System.out.println("   🎯 Penetração: " + penetracao + "/" + penetracaoMaxima);
        System.out.println("   💫 Chance Crítico: " + (int)(chanceCritico * 100) + "%");
        if (flechasPrecisas > 0) {
            System.out.println("   🏹 Flechas Precisas: " + flechasPrecisas);
        }
    }
    // ============ IMPLEMENTAÇÃO DE AtributoEspecial ============

    @Override
    public String getNomeAtributo() {
        return "Penetração";
    }

    @Override
    public int getValorAtual() {
        return penetracao;
    }

    @Override
    public int getValorMaximo() {
        return penetracaoMaxima;
    }

    @Override
    public double getPorcentagem() {
        return (double) penetracao / penetracaoMaxima * 100;
    }

    @Override
    public boolean consumir(int quantidade) {
        return consumirPenetracao(quantidade);
    }

    @Override
    public void recarregar(int quantidade) {
        penetracao = Math.min(penetracaoMaxima, penetracao + quantidade);
    }

    @Override
    public void recarregarCompletamente() {
        penetracao = penetracaoMaxima;
    }
}
