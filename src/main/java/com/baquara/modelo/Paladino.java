// 📁 modelo/Paladino.java
package com.baquara.modelo;

public class Paladino extends Personagem implements AtributoEspecial {
    private int poderDivino;
    private int poderDivinoMaximo;
    private int feAbencoada;

    public Paladino() {
        super(PerTipo.PALADINO, "Paladino", 150, 22, 25);  // Defesa alta: 25
        this.poderDivinoMaximo = 100;
        this.poderDivino = 100;
        this.feAbencoada = 0;
    }

    public int getPoderDivino() { return poderDivino; }
    public int getPoderDivinoMaximo() { return poderDivinoMaximo; }
    public int getFeAbencoada() { return feAbencoada; }

    public boolean consumirPoderDivino(int quantidade) {
        if (poderDivino >= quantidade) {
            poderDivino -= quantidade;
            feAbencoada++;
            System.out.println("✨ Poder Divino consumido: " + quantidade +
                    " (Restante: " + poderDivino + "/" + poderDivinoMaximo + ")");
            return true;
        }
        System.out.println("❌ Poder Divino insuficiente! (" + poderDivino + "/" + poderDivinoMaximo + ")");
        return false;
    }

    public void recarregarPoderDivino(int quantidade) {
        if (quantidade <= 0) return;
        poderDivino = Math.min(poderDivinoMaximo, poderDivino + quantidade);
    }

    @Override
    public void recarregarPorEstagio(int estagioNumero) {
        int recarga = estagioNumero * 10;
        recarregarPoderDivino(recarga);
        System.out.println("🙏 Poder Divino recarregado: +" + recarga + " (" + poderDivino + "/" + poderDivinoMaximo + ")");
    }

    @Override
    public void recarregarPorNivel(int novoNivel) {
        poderDivinoMaximo += 10;
        poderDivino = poderDivinoMaximo;
        feAbencoada += 2;
        System.out.println("🌟 Poder Divino máximo: " + poderDivinoMaximo + " | Completamente restaurado!");
    }

    @Override
    public void usarHabilidadeEspecial(Personagem alvo) {
        System.out.println("\n🛡️ " + nome + " invoca o ESPÍRITO SAGRADO!");

        int custo = 30 + (feAbencoada * 2);
        if (custo > 70) custo = 70;

        if (!consumirPoderDivino(custo)) {
            if (poderDivino > 15) {
                System.out.println("⚠️ Poder Divino baixo! Usando versão reduzida...");
                custo = poderDivino;
                poderDivino = 0;
                feAbencoada++;
            } else {
                System.out.println("❌ Poder Divino insuficiente! Habilidade cancelada.");
                return;
            }
        }

        int cura = 40 + (nivel * 5) + (poderDivino / 4);
        curar(cura);
        System.out.println("💚 Curou " + cura + " de vida!");

        int dano = 25 + (nivel * 3) + (feAbencoada * 3);
        System.out.println("⚡ Luz divina atinge " + alvo.getNome() + " causando " + dano + " de dano!");
        alvo.tomarDano(dano);

        System.out.println("📿 Fé Abençoada acumulada: " + feAbencoada);
        recarregarPoderDivino(5);
    }

    @Override
    public String getNomeHabilidade() {
        return "ESPÍRITO SAGRADO";
    }

    @Override
    public String getDescricaoHabilidade() {
        return "Cura " + (40 + (nivel * 5)) + " de vida e causa " +
                (25 + (nivel * 3)) + " de dano sagrado.\n" +
                "   📿 Quanto mais Fé Abençoada, mais forte fica!";
    }

    @Override
    public void mostrarStatus() {
        super.mostrarStatus();
        System.out.println("   🙏 Poder Divino: " + poderDivino + "/" + poderDivinoMaximo);
        System.out.println("   📿 Fé Abençoada: " + feAbencoada + " acúmulos");
    }
    // ============ IMPLEMENTAÇÃO DE AtributoEspecial ============

    @Override
    public String getNomeAtributo() {
        return "Poder Divino";
    }

    @Override
    public int getValorAtual() {
        return poderDivino;
    }

    @Override
    public int getValorMaximo() {
        return poderDivinoMaximo;
    }

    @Override
    public double getPorcentagem() {
        return (double) poderDivino / poderDivinoMaximo * 100;
    }

    @Override
    public boolean consumir(int quantidade) {
        return consumirPoderDivino(quantidade);
    }

    @Override
    public void recarregar(int quantidade) {
        recarregarPoderDivino(quantidade);
    }

    @Override
    public void recarregarCompletamente() {
        poderDivino = poderDivinoMaximo;
    }
}