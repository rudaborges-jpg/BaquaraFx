package com.baquara.modelo;

import java.util.Random;

public class BesouroManganga extends Inimigo {
    private int faseAtual;
    private boolean invulneravel;
    private int defesaBase;
    private Random random;
    private String[] frases;
    private String[] movimentosPorFase;

    public BesouroManganga() {
        super(
                "BESOURO MANGANGÁ",
                2000,
                70,
                10
        );
        this.faseAtual = 1;
        this.invulneravel = false;
        this.defesaBase = 20; // Defesa inicial
        this.random = new Random();

        this.frases = new String[] {
                "FECHADO, FECHA O CORPO! 🦗",
                "NINGUÉM SEGURA O BESOURO!",
                "SOU INVENCÍVEL COMO O VENTO!",
                "MINHA FAMA CORRE O MUNDO!",
                "CORDA NO PESCOÇO, FACA NO PEITO, NADA ME ACONTECE!",
                "SOU FILHO DE OGUM, PROTEGIDO DE EXU!"
        };

        this.movimentosPorFase = new String[] {
                "💨 VOOS RASANTES - Ataques rápidos como o inseto",
                "🛡️ CORPO FECHADO - Invulnerável a ataques normais e defesa aumentada",
                "⚡ VINGANÇA DO BESOURO - Dobro de dano, metade da defesa"
        };
    }

    public int getFaseAtual() { return faseAtual; }
    public String getFrase() { return frases[random.nextInt(frases.length)]; }

    @Override
    public int getDefesa() {
        return defesaBase;
    }

    public void avancarFase() {
        faseAtual++;
        System.out.println("\n🦗 " + getNome() + " AVANÇA PARA FASE " + faseAtual + "!");
        System.out.println("   " + getFrase());
        System.out.println("   " + movimentosPorFase[faseAtual - 1]);

        switch (faseAtual) {
            case 2:
                invulneravel = true;
                defesaBase = 65;
                System.out.println("   🛡️ CORPO FECHADO! Imune a ataques normais!");
                System.out.println("   🛡️ Defesa aumentada para " + defesaBase + "!");
                break;
            case 3:
                invulneravel = false;
                defesaBase = 10;
                System.out.println("   ⚡ FÚRIA DO BESOURO! Dano dobrado, mas defesa reduzida para " + defesaBase + "!");
                break;
        }
    }

    @Override
    public void tomarDano(int danoBruto) {
        if (invulneravel && danoBruto < 50) {
            System.out.println("🛡️ CORPO FECHADO! O ataque não surtiu efeito!");
            return;
        }

        if (faseAtual == 3) {
            danoBruto = (int)(danoBruto * 1.5); // Toma mais dano na fase 3
            System.out.println("💢 CORPO ABERTO! Toma 50% mais dano!");
        }

        // Aplica a defesa normalmente (já está implementada na classe Inimigo)
        super.tomarDano(danoBruto);

        double porcentagemVida = (double) getVida() / getVidaMax();

        if (porcentagemVida <= 0.66 && faseAtual == 1) {
            avancarFase();
        } else if (porcentagemVida <= 0.33 && faseAtual == 2) {
            avancarFase();
        }
    }

    @Override
    public void mostrarStatus() {
        double reducao = (double) getDefesa() / (getDefesa() + 50);
        int percentualReducao = (int)(reducao * 100);

        System.out.println("🦗 " + getNome() + " - O LENDÁRIO CAPOEIRISTA 🦗");
        System.out.println("   Fase " + faseAtual + "/3: " + movimentosPorFase[faseAtual - 1]);
        System.out.println("   ❤️ Vida: " + getVida() + "/" + getVidaMax());
        System.out.println("   ⚔️ Ataque: " + getAtaque());
        System.out.println("   🛡️ Defesa: " + getDefesa() + " (reduz " + percentualReducao + "% do dano)");
        if (invulneravel) {
            System.out.println("   🔮 CORPO FECHADO ATIVO! Imune a ataques com menos de 50 de dano!");
        }
    }
}