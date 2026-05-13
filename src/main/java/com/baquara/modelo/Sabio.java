// 📁 modelo/Sabio.java
package com.baquara.modelo;

public class Sabio extends Personagem implements AtributoEspecial {
    private int mana;
    private int manaMaxima;
    private int conhecimento;

    public Sabio() {
        super(PerTipo.SABIO, "Sábio", 100, 25, 14);
        this.manaMaxima = 100;
        this.mana = 100;
        this.conhecimento = 0;
    }

    public int getMana() { return mana; }
    public int getManaMaxima() { return manaMaxima; }
    public int getConhecimento() { return conhecimento; }

    public boolean usarMana(int quantidade) {
        if (mana >= quantidade) {
            mana -= quantidade;
            conhecimento++;
            System.out.println("💙 Mana consumida: " + quantidade +
                    " (Restante: " + mana + "/" + manaMaxima + ")");
            return true;
        }
        System.out.println("❌ Mana insuficiente! (" + mana + "/" + manaMaxima + ")");
        return false;
    }

    public void recuperarMana(int quantidade) {
        mana = Math.min(manaMaxima, mana + quantidade);
        if (quantidade > 0) {
            System.out.println("💙 Mana recuperada: +" + quantidade + " (Agora: " + mana + "/" + manaMaxima + ")");
        }
    }

    @Override
    public void recarregarPorEstagio(int estagioNumero) {
        int recarga = estagioNumero * 10;
        recuperarMana(recarga);
        System.out.println("📚 Conhecimento acumulado: " + conhecimento);
    }

    @Override
    public void recarregarPorNivel(int novoNivel) {
        manaMaxima += 10;
        mana = manaMaxima;
        System.out.println("🧠 Mana máxima: " + manaMaxima + " | Completamente restaurada!");
    }

    @Override
    public int ataqueInimigo(Personagem alvo) {
        if (mana >= 10) {
            usarMana(10);
            int bonusConhecimento = conhecimento / 2;
            int dano = ataque + (nivel * 2) + 10 + bonusConhecimento;
            System.out.println("🔮 " + nome + " lança MISSIL MÁGICO causando " + dano + " de dano!");
            if (bonusConhecimento > 0) {
                System.out.println("   📚 +" + bonusConhecimento + " bônus de conhecimento");
            }
            alvo.tomarDano(dano);
            recuperarMana(5);
            return dano;
        } else {
            int dano = ataque + (nivel * 2);
            System.out.println("🗡️ " + nome + " ataca sem mana causando " + dano + " de dano!");
            alvo.tomarDano(dano);
            return dano;
        }
    }

    @Override
    public void usarHabilidadeEspecial(Personagem alvo) {
        System.out.println("\n📜 " + nome + " conjura SABEDORIA ANCESTRAL!");

        int custo = 30;
        if (!usarMana(custo)) {
            if (mana > 10) {
                System.out.println("⚠️ Mana insuficiente! Usando versão reduzida...");
                custo = mana;
                mana = 0;
            } else {
                System.out.println("❌ Mana insuficiente! Habilidade cancelada.");
                return;
            }
        }

        int bonusConhecimento = conhecimento * 2;
        int dano = 40 + (nivel * 3) + bonusConhecimento;
        System.out.println("💥 Explosão arcana causa " + dano + " de dano!");
        alvo.tomarDano(dano);

        int cura = 30 + (nivel * 2) + (conhecimento);
        curar(cura);
        System.out.println("💚 Curou " + cura + " de vida!");

        recuperarMana(10 + (conhecimento / 3));
        System.out.println("📚 Conhecimento acumulado: " + conhecimento);
    }

    @Override
    public String getNomeHabilidade() {
        return "SABEDORIA ANCESTRAL";
    }

    @Override
    public String getDescricaoHabilidade() {
        return "Causa " + (40 + (nivel * 3)) + " de dano, cura " + (30 + (nivel * 2)) +
                " de vida e recupera mana.\n   📚 Quanto mais conhecimento, mais forte!";
    }

    @Override
    public void mostrarStatus() {
        super.mostrarStatus();
        System.out.println("   💙 Mana: " + mana + "/" + manaMaxima);
        if (conhecimento > 0) {
            System.out.println("   📚 Conhecimento: " + conhecimento);
        }
    }
    // ============ IMPLEMENTAÇÃO DE AtributoEspecial ============

    @Override
    public String getNomeAtributo() {
        return "Mana";
    }

    @Override
    public int getValorAtual() {
        return mana;
    }

    @Override
    public int getValorMaximo() {
        return manaMaxima;
    }

    @Override
    public double getPorcentagem() {
        return (double) mana / manaMaxima * 100;
    }

    @Override
    public boolean consumir(int quantidade) {
        return usarMana(quantidade);
    }

    @Override
    public void recarregar(int quantidade) {
        recuperarMana(quantidade);
    }

    @Override
    public void recarregarCompletamente() {
        mana = manaMaxima;
    }
}