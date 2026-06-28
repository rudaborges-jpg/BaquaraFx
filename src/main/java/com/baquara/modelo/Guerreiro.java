package com.baquara.modelo;

import com.baquara.modelo.efeitos.EfeitoStatus;

import java.util.*;

public class Guerreiro extends Personagem implements AtributoEspecial, Entidade {
    private int espiritoLuta;
    private int espiritoLutaMaximo;
    private int comboAtual;

    // ⭐ NOVOS CAMPOS PARA EFEITOS DE STATUS
    private Map<Class<? extends EfeitoStatus>, EfeitoStatus> efeitos;

    public Guerreiro() {
        super(PerTipo.GUERREIRO, "Guerreiro", 140, 27, 21);
        this.espiritoLutaMaximo = 100;
        this.espiritoLuta = 100;
        this.comboAtual = 0;

        // ⭐ INICIALIZA O MAPA DE EFEITOS
        this.efeitos = new HashMap<>();
    }

    public int getEspiritoLuta() { return espiritoLuta; }
    public int getEspiritoLutaMaximo() { return espiritoLutaMaximo; }
    public int getComboAtual() { return comboAtual; }

    public boolean consumirEspiritoLuta(int quantidade) {
        if (espiritoLuta >= quantidade) {
            espiritoLuta -= quantidade;
            comboAtual++;
            System.out.println("💪 Espírito de Luta consumido: " + quantidade +
                    " (Restante: " + espiritoLuta + "/" + espiritoLutaMaximo + ")");
            if (comboAtual > 1) {
                System.out.println("🔥 Combo x" + comboAtual + "!");
            }
            return true;
        }
        System.out.println("❌ Espírito de Luta insuficiente! (" + espiritoLuta + "/" + espiritoLutaMaximo + ")");
        return false;
    }

    @Override
    public void recarregarPorEstagio(int estagioNumero) {
        int recarga = estagioNumero * 8;
        espiritoLuta = Math.min(espiritoLutaMaximo, espiritoLuta + recarga);
        System.out.println("⚔️ Espírito de Luta recarregado: +" + recarga + " (" + espiritoLuta + "/" + espiritoLutaMaximo + ")");
    }

    @Override
    public void recarregarPorNivel(int novoNivel) {
        espiritoLutaMaximo += 10;
        espiritoLuta = espiritoLutaMaximo;
        comboAtual = 0;
        System.out.println("💥 Espírito de Luta máximo: " + espiritoLutaMaximo + " | Restaurado!");
    }

    // ============ MÉTODOS DA INTERFACE ENTIDADE ============

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
                efeito.atualizar(this);
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

    public void limparEfeitos() {
        for (EfeitoStatus efeito : efeitos.values()) {
            efeito.remover(this);
        }
        efeitos.clear();
        System.out.println("✨ Todos os efeitos de " + nome + " foram removidos!");
    }

    @Override
    public void usarHabilidadeEspecial(Personagem alvo) {
        System.out.println("\n💪 " + nome + " usa FÚRIA DO GUERREIRO!");

        int custo = 40;
        if (!consumirEspiritoLuta(custo)) {
            if (espiritoLuta > 10) {
                System.out.println("⚠️ Espírito de Luta baixo! Usando FÚRIA reduzida...");
                custo = espiritoLuta;
                espiritoLuta = 0;
            } else {
                System.out.println("❌ Espírito de Luta insuficiente! Habilidade cancelada.");
                return;
            }
        }

        int dano = ataque + 15 + (nivel * 2) + (comboAtual * 5);
        if (custo < 40) {
            dano = (int)(dano * 0.6);
            System.out.println("⚠️ Versão reduzida: " + dano + " de dano");
        }

        System.out.println("⚡ Ataque poderoso causa " + dano + " de dano!");
        alvo.tomarDano(dano);

        System.out.println("🔥 Combo atual: x" + comboAtual);
        espiritoLuta = Math.min(espiritoLutaMaximo, espiritoLuta + 8);
    }

    @Override
    public String getNomeHabilidade() {
        return "FÚRIA DO GUERREIRO";
    }

    @Override
    public String getDescricaoHabilidade() {
        return "Causa dano massivo que aumenta com o combo.\n" +
                "   🔥 Cada uso de Espírito de Luta aumenta o combo!";
    }

    @Override
    public void mostrarStatus() {
        super.mostrarStatus();
        System.out.println("   💪 Espírito de Luta: " + espiritoLuta + "/" + espiritoLutaMaximo);
        if (comboAtual > 0) {
            System.out.println("   🔥 Combo: x" + comboAtual);
        }

        // Mostra efeitos ativos
        for (EfeitoStatus efeito : efeitos.values()) {
            if (efeito.estaAtivo()) {
                System.out.println("   " + efeito.getDescricao());
            }
        }
    }

    // ============ IMPLEMENTAÇÃO DE AtributoEspecial ============

    @Override
    public String getNomeAtributo() {
        return "Espírito de Luta";
    }

    @Override
    public int getValorAtual() {
        return espiritoLuta;
    }

    @Override
    public int getValorMaximo() {
        return espiritoLutaMaximo;
    }

    @Override
    public double getPorcentagem() {
        return (double) espiritoLuta / espiritoLutaMaximo * 100;
    }

    @Override
    public boolean consumir(int quantidade) {
        return consumirEspiritoLuta(quantidade);
    }

    @Override
    public void recarregar(int quantidade) {
        espiritoLuta = Math.min(espiritoLutaMaximo, espiritoLuta + quantidade);
    }

    @Override
    public void recarregarCompletamente() {
        espiritoLuta = espiritoLutaMaximo;
    }
}