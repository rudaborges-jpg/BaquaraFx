package com.baquara.modelo;

import com.baquara.modelo.efeitos.EfeitoStatus;

import java.util.*;

public class Paladino extends Personagem implements AtributoEspecial, Entidade {
    private int poderDivino;
    private int poderDivinoMaximo;
    private int feAbencoada;

    // ⭐ NOVOS CAMPOS PARA EFEITOS DE STATUS
    private Map<Class<? extends EfeitoStatus>, EfeitoStatus> efeitos;

    public Paladino() {
        super(PerTipo.PALADINO, "Paladino", 150, 24, 22);
        this.poderDivinoMaximo = 100;
        this.poderDivino = 100;
        this.feAbencoada = 0;

        // ⭐ INICIALIZA O MAPA DE EFEITOS
        this.efeitos = new HashMap<>();
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
        System.out.println("\n🛡️ " + nome + " invoca o ESPÍRITO SAGRADO!");

        int custo = 30 + (feAbencoada * 2);
        if (custo > 55) custo = 70;

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

        int cura = 25 + (nivel * 3) + (poderDivino / 4);
        curar(cura);
        System.out.println("💚 Curou " + cura + " de vida!");

        int dano = 25 + (nivel * 3) + (feAbencoada * 4);
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
        int fe = getFeAbencoada();
        int poderAtual = getPoderDivino();
        int poderMaximo = getPoderDivinoMaximo();

        int curaBase = 25 + (nivel * 3);
        int curaBonus = poderAtual / 5;
        int curaTotal = curaBase + curaBonus;

        int danoBase = 25 + (nivel * 3);
        int danoBonus = fe * 3;
        int danoTotal = danoBase + danoBonus;

        int custo = 30 + (fe * 2);
        if (custo > 70) custo = 70;

        return "🛡️ ESPÍRITO SAGRADO\n" +
                "   💚 Cura: " + curaBase + " + " + curaBonus + " = " + curaTotal + " de vida\n" +
                "   ⚡ Dano: " + danoBase + " + " + danoBonus + " = " + danoTotal + " de dano\n" +
                "   📿 Fé Abençoada: " + fe + " acúmulos (+" + (fe * 3) + " de dano)\n" +
                "   💫 Custo: " + custo + " de Poder Divino\n" +
                "   🙏 Poder Divino: " + poderAtual + "/" + poderMaximo;
    }

    @Override
    public void mostrarStatus() {
        super.mostrarStatus();

        // ⭐ USANDO OS GETTERS
        System.out.println("   🙏 Poder Divino: " + getPoderDivino() + "/" + getPoderDivinoMaximo());
        System.out.println("   📿 Fé Abençoada: " + getFeAbencoada() + " acúmulos");

        // Mostra o custo atual da habilidade
        int custo = 30 + (getFeAbencoada() * 2);
        if (custo > 70) custo = 70;
        System.out.println("   💫 Custo da Habilidade: " + custo + " PD");

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

    @Override
    protected void levelUp() {
        nivel++;
        experiencia = 0;


        int aumentoVida = 40;
        vidaMax += aumentoVida;
        vida += aumentoVida;

        ataque += 4;
        defesa += 4;

        System.out.println("\n🎉 " + nome + " subiu para o NÍVEL " + nivel + "!");
        System.out.println("   ❤️ Vida +" + aumentoVida + " | ⚔️ Ataque +5 | 🛡️ Defesa +3 (Total: " + defesa + ")");

        // Recarrega atributos especiais (Poder Divino)
        recarregarPorNivel(nivel);
    }

}