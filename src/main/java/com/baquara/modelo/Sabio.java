package com.baquara.modelo;

import com.baquara.modelo.efeitos.EfeitoStatus;

import java.util.*;

public class Sabio extends Personagem implements AtributoEspecial, Entidade {
    private int mana;
    private int manaMaxima;
    private int conhecimento;

    // ⭐ NOVOS CAMPOS PARA EFEITOS DE STATUS
    private Map<Class<? extends EfeitoStatus>, EfeitoStatus> efeitos;

    public Sabio() {
        super(PerTipo.SABIO, "Sábio", 110, 25, 14);
        this.manaMaxima = 100;
        this.mana = 100;
        this.conhecimento = 0;

        // ⭐ INICIALIZA O MAPA DE EFEITOS
        this.efeitos = new HashMap<>();
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