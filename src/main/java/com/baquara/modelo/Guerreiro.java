package com.baquara.modelo;

public class Guerreiro extends Personagem implements AtributoEspecial {
    private int espiritoLuta;
    private int espiritoLutaMaximo;
    private int comboAtual;

    public Guerreiro() {
        super(PerTipo.GUERREIRO, "Guerreiro", 140, 28, 18);  // Defesa média: 18
        this.espiritoLutaMaximo = 100;
        this.espiritoLuta = 100;
        this.comboAtual = 0;
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