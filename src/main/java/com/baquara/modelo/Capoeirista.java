package com.baquara.modelo;

import java.util.Random;

public class Capoeirista extends Personagem implements AtributoEspecial {
    private int energiaGinga;
    private int energiaMaxima;
    private int esquivasRestantes;
    private int esquivasMaximas;
    private Random random;

    private String[] titulos = {
            "INICIANTE NA RODA",        // Estágio 1
            "APRENDIZ DO BERIMBAU",      // Estágio 2
            "DISCÍPULO DA GINGA",        // Estágio 3
            "GUERREIRO DA CAPOEIRA",     // Estágio 4
            "MESTRE DO JOGO",            // Estágio 5
            "GUARDIÃO DA TRADIÇÃO",      // Estágio 6
            "LENDA VIVA",                // Estágio 7
            "ESPÍRITO ANCESTRAL",        // Estágio 8
            "HERDEIRO DE ZUMBI",         // Estágio 9
            "BESOURO DO BERIMBAU"        // Estágio 10
    };

    public Capoeirista() {
        super(PerTipo.CAPOEIRISTA, "INICIANTE NA RODA", 120, 25, 15);
        this.energiaMaxima = 100;
        this.energiaGinga = 100;
        this.esquivasMaximas = 3;  // Aumentado para 3 esquivas
        this.esquivasRestantes = 3;
        this.random = new Random();
    }

    // ============ GETTERS ============

    public int getEnergiaGinga() { return energiaGinga; }
    public int getEnergiaMaxima() { return energiaMaxima; }
    public int getEsquivasRestantes() { return esquivasRestantes; }
    public int getEsquivasMaximas() { return esquivasMaximas; }

    // ============ MÉTODOS DE GINGA ============

    public boolean consumirEnergiaGinga(int quantidade) {
        if (quantidade < 0) {
            energiaGinga = Math.min(energiaMaxima, energiaGinga - quantidade);
            return true;
        }
        if (energiaGinga >= quantidade) {
            energiaGinga -= quantidade;
            return true;
        }
        return false;
    }

    public boolean usarEsquiva() {
        if (esquivasRestantes > 0) {
            esquivasRestantes--;
            return true;
        }
        return false;
    }

    public void recarregarEsquivas() {
        esquivasRestantes = esquivasMaximas;
    }

    public void recarregarTotalmente() {
        energiaGinga = energiaMaxima;
        recarregarEsquivas();
        System.out.println("🌀 Ginga e esquivas totalmente restauradas!");
    }

    /**
     * Executa a esquiva - BLOQUEIA o próximo dano e causa contra-ataque
     * @param inimigo O inimigo que atacaria
     * @param danoQueSofreria O dano que seria causado (se houver)
     * @return true se esquivou com sucesso
     */
    public boolean executarEsquiva(Inimigo inimigo, int danoQueSofreria) {
        if (!usarEsquiva()) {
            System.out.println("❌ Sem esquivas disponíveis! (" + esquivasRestantes + "/" + esquivasMaximas + ")");
            return false;
        }

        System.out.println("\n🌀 ESQUIVA DE CAPOEIRA! 🌀");
        System.out.println("   Você desviou do golpe inimigo com maestria!");

        if (danoQueSofreria > 0) {
            System.out.println("   🛡️ Dano evitado: " + danoQueSofreria + " de dano!");
        }

        // Contra-ataque (60% de chance)
        if (random.nextDouble() < 0.6) {
            int contraAtaque = (ataque / 2) + random.nextInt(15) + 5;
            System.out.println("⚡ CONTRA-ATAQUE RÁPIDO! Causou " + contraAtaque + " de dano!");
            inimigo.tomarDano(contraAtaque);
        } else {
            System.out.println("   O inimigo recuou, você não conseguiu contra-atacar.");
        }

        // Recupera Ginga
        int gingaRecuperada = 15;
        energiaGinga = Math.min(energiaMaxima, energiaGinga + gingaRecuperada);
        System.out.println("🌀 +" + gingaRecuperada + " de Ginga recuperada!");

        return true;
    }

    /**
     * Versão simples da esquiva (sem dano para bloquear)
     */
    public boolean executarEsquiva(Inimigo inimigo) {
        return executarEsquiva(inimigo, 0);
    }

    // ============ EVOLUÇÃO ============

    public void evoluirTitulo(int estagio) {
        if (estagio < 1 || estagio > 10) return;

        this.nome = titulos[estagio - 1];

        int aumentoVida = 35;
        vidaMax += aumentoVida;
        vida = vidaMax;
        ataque += 5;
        defesa += 3;
        energiaMaxima += 15;
        energiaGinga = energiaMaxima;
        esquivasMaximas++;
        esquivasRestantes = esquivasMaximas;

        System.out.println("\n🥋 VOCÊ EVOLUIU! 🥋");
        System.out.println("   🏅 Novo título: " + nome);
        System.out.println("   ❤️ Vida: " + vidaMax + " | ⚔️ Ataque: " + ataque + " | 🛡️ Defesa: " + defesa);
        System.out.println("   🌀 Ginga Máxima: " + energiaMaxima + " | 🔄 Esquivas: " + esquivasMaximas);
    }

    @Override
    public void recarregarPorEstagio(int estagioNumero) {
        int recarga = 15;
        energiaGinga = Math.min(energiaMaxima, energiaGinga + recarga);
        recarregarEsquivas();
        System.out.println("🌀 Ginga recuperada: +" + recarga + " (" + energiaGinga + "/" + energiaMaxima + ")");
        System.out.println("🔄 Esquivas restauradas: " + esquivasRestantes + "/" + esquivasMaximas);
    }

    @Override
    public void recarregarPorNivel(int novoNivel) {
        energiaMaxima += 20;
        energiaGinga = energiaMaxima;
    }

    // ============ ATAQUES ============

    public int ataqueNormal(Inimigo alvo) {
        int dano = ataque + random.nextInt(10) + 5;
        System.out.println("🔄 GINGA BÁSICA! " + dano + " de dano!");
        alvo.tomarDano(dano);
        energiaGinga = Math.min(energiaMaxima, energiaGinga + 5);
        return dano;
    }

    public int ataqueDificil(Inimigo alvo) {
        if (!consumirEnergiaGinga(20)) {
            System.out.println("❌ Ginga insuficiente! (Precisa de 20, tem " + energiaGinga + ")");
            System.out.println("   Realizando ataque normal...");
            return ataqueNormal(alvo);
        }

        String[] movimentos = {
                "🌪️ ARMADA VIOLENTA!",
                "🦵 MEIA LUA DE COMPASSO!",
                "💫 AÚ BATIDO!",
                "⚡ MARTELO DE NEGATIVA!",
                "🎯 QUEIXADA!"
        };

        String movimento = movimentos[random.nextInt(movimentos.length)];
        int dano = ataque + 15 + random.nextInt(20);

        System.out.println(movimento + " 💥 " + dano + " de dano!");
        alvo.tomarDano(dano);

        if (random.nextDouble() < 0.3) {
            int bonus = random.nextInt(10) + 5;
            System.out.println("🎯 GOLPE PRECISO! +" + bonus + " de dano extra!");
            alvo.tomarDano(bonus);
            dano += bonus;
        }

        return dano;
    }

    public int ataqueCombinado(Inimigo alvo) {
        if (!consumirEnergiaGinga(40)) {
            System.out.println("❌ Ginga insuficiente! (Precisa de 40, tem " + energiaGinga + ")");
            System.out.println("   Realizando ataque normal...");
            return ataqueNormal(alvo);
        }

        System.out.println("🔥 SEQUÊNCIA COMBINADA! 🔥");

        String[] combinacoes = {
                "💢 GINGA → ARMADA → MEIA LUA DE COMPASSO!",
                "⚔️ NEGATIVA → ROLÊ → MARTELO → QUEIXADA!",
                "🌀 AÚ → BANANEIRA → CHAPÉU DE COURO → RASTEIRA!",
                "🎵 BERIMBAU → ESQUIVA → CABEÇADA → VOADORA!"
        };

        System.out.println("   " + combinacoes[random.nextInt(combinacoes.length)]);

        int danoTotal = 0;
        int golpes = 3 + random.nextInt(2);

        for (int i = 0; i < golpes; i++) {
            int danoGolpe = 8 + random.nextInt(12);
            System.out.println("   💥 Golpe " + (i+1) + ": " + danoGolpe + " de dano!");
            alvo.tomarDano(danoGolpe);
            danoTotal += danoGolpe;
        }

        int danoFinal = ataque + danoTotal;
        System.out.println("🎯 TOTAL COMBINADO: " + danoFinal + " de dano!");

        return danoFinal;
    }

    // ============ HABILIDADE ESPECIAL ============

    @Override
    public void usarHabilidadeEspecial(Personagem alvo) {
        System.out.println("🥋 " + nome + " usa JOGO DE CAPOEIRA!");
    }

    @Override
    public String getNomeHabilidade() {
        return "JOGO DE CAPOEIRA";
    }

    @Override
    public String getDescricaoHabilidade() {
        return "Estilos de luta: Normal, Difícil, Combinado e Esquiva";
    }

    // ============ ATRIBUTO ESPECIAL ============

    @Override
    public String getNomeAtributo() {
        return "Energia da Ginga";
    }

    @Override
    public int getValorAtual() {
        return energiaGinga;
    }

    @Override
    public int getValorMaximo() {
        return energiaMaxima;
    }

    @Override
    public double getPorcentagem() {
        return (double) energiaGinga / energiaMaxima * 100;
    }

    @Override
    public boolean consumir(int quantidade) {
        return consumirEnergiaGinga(quantidade);
    }

    @Override
    public void recarregar(int quantidade) {
        energiaGinga = Math.min(energiaMaxima, energiaGinga + quantidade);
    }

    @Override
    public void recarregarCompletamente() {
        energiaGinga = energiaMaxima;
        recarregarEsquivas();
    }

    // ============ MOSTRAR STATUS ============

    @Override
    public void mostrarStatus() {
        double reducao = (double) defesa / (defesa + 50);
        int percentualReducao = (int)(reducao * 100);

        System.out.println("\n👤 " + nome + " (Nv." + nivel + ")");
        System.out.println("   ❤️ Vida: " + vida + "/" + vidaMax);
        System.out.println("   ⚔️ Ataque: " + ataque);
        System.out.println("   🛡️ Defesa: " + defesa + " (reduz " + percentualReducao + "% do dano)");
        System.out.println("   🌀 Energia da Ginga: " + energiaGinga + "/" + energiaMaxima);
        System.out.println("   🔄 Esquivas: " + esquivasRestantes + "/" + esquivasMaximas);
    }
}