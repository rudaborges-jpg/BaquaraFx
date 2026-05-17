// 📁 modelo/Inimigo.java

package com.baquara.modelo;

import java.util.*;

public class Inimigo implements Entidade {
    private String nome;
    private int nivel;
    private int vida;
    private int vidaMax;
    private int ataqueBase;
    private int ataque;
    private int defesa;
    private int defesaBase;

    // Sistema genérico de efeitos de status
    private Map<Class<? extends EfeitoStatus>, EfeitoStatus> efeitos;

    // Construtor principal - TUDO é calculado baseado no nível
    public Inimigo(String nome, int nivel) {
        this.nome = nome;
        this.nivel = nivel;

        // Fórmulas de progressão baseadas no nível (1 a 10)
        this.vidaMax = 60 + (nivel * 100);
        this.vida = vidaMax;
        this.ataqueBase = 20 + (nivel * 4);
        this.ataque = ataqueBase;
        this.defesaBase = 5 + (nivel * 3);
        this.defesa = defesaBase;

        this.efeitos = new HashMap<>();
    }

    // Construtor para chefões personalizados
    public Inimigo(String nome, int nivel, int vida, int ataque, int defesa) {
        this.nome = nome;
        this.nivel = nivel;
        this.vidaMax = vida;
        this.vida = vida;
        this.ataqueBase = ataque;
        this.ataque = ataque;
        this.defesaBase = defesa;
        this.defesa = defesa;
        this.efeitos = new HashMap<>();
    }

    // ========== GETTERS ==========

    @Override
    public String getNome() {
        return nome;
    }

    // ⭐ ADICIONE ESTE GETTER (é o que está faltando!)
    public int getNivel() {
        return nivel;
    }

    @Override
    public int getAtaque() {
        return ataque;
    }

    @Override
    public int getDefesa() {
        return defesa;
    }

    @Override
    public int getVida() {
        return vida;
    }

    @Override
    public int getVidaMax() {
        return vidaMax;
    }

    public int getAtaqueBase() {
        return ataqueBase;
    }

    public int getDefesaBase() {
        return defesaBase;
    }

    // ========== SETTERS ==========

    @Override
    public void setAtaque(int ataque) {
        this.ataque = ataque;
    }

    @Override
    public void setDefesa(int defesa) {
        this.defesa = defesa;
    }

    @Override
    public void setVida(int vida) {
        this.vida = Math.max(0, Math.min(vida, vidaMax));
    }

    // ========== MÉTODOS DE EFEITOS ==========

    @Override
    public void adicionarEfeito(EfeitoStatus efeito) {
        Class<? extends EfeitoStatus> tipo = efeito.getClass();

        if (efeitos.containsKey(tipo)) {
            EfeitoStatus existente = efeitos.get(tipo);
            existente.renovar();
            System.out.println("🔄 " + nome + ": " + efeito.getNome() + " renovado!");
        } else {
            efeitos.put(tipo, efeito);
            efeito.aplicar(this);
            System.out.println("✨ " + nome + " recebeu efeito: " + efeito.getNome());
        }
    }

    @Override
    public void removerEfeito(Class<? extends EfeitoStatus> tipoEfeito) {
        EfeitoStatus efeito = efeitos.remove(tipoEfeito);
        if (efeito != null) {
            efeito.remover(this);
            System.out.println("⏰ " + nome + ": " + efeito.getNome() + " expirou!");
        }
    }

    @Override
    public boolean temEfeito(Class<? extends EfeitoStatus> tipoEfeito) {
        return efeitos.containsKey(tipoEfeito) &&
                efeitos.get(tipoEfeito).estaAtivo();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EfeitoStatus> T getEfeito(Class<T> tipoEfeito) {
        return (T) efeitos.get(tipoEfeito);
    }

    @Override
    public void atualizarEfeitos() {
        List<Class<? extends EfeitoStatus>> paraRemover = new ArrayList<>();

        for (EfeitoStatus efeito : efeitos.values()) {
            if (efeito.estaAtivo()) {
                if (!efeito.reduzirDuracao()) {
                    paraRemover.add(efeito.getClass());
                }
            } else {
                paraRemover.add(efeito.getClass());
            }
        }

        for (Class<? extends EfeitoStatus> tipo : paraRemover) {
            removerEfeito(tipo);
        }
    }

    // ========== MÉTODOS DE COMBATE ==========

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

    public boolean vivo() {
        return vida > 0;
    }

    public void limparEfeitos() {
        for (EfeitoStatus efeito : efeitos.values()) {
            efeito.remover(this);
        }
        efeitos.clear();
        this.ataque = ataqueBase;
        System.out.println("✨ Todos os efeitos de " + nome + " foram removidos!");
    }

    public void mostrarStatus() {
        double reducao = (double) defesa / (defesa + 50);
        int percentualReducao = (int)(reducao * 100);

        System.out.println("👾 " + nome + " (Nv." + nivel + ")");
        System.out.println("   ❤️ Vida: " + vida + "/" + vidaMax);
        System.out.println("   ⚔️ Ataque: " + ataque);
        System.out.println("   🛡️ Defesa: " + defesa + " (reduz " + percentualReducao + "%)");

        for (EfeitoStatus efeito : efeitos.values()) {
            if (efeito.estaAtivo()) {
                System.out.println("   " + efeito.getDescricao());
            }
        }
    }
}