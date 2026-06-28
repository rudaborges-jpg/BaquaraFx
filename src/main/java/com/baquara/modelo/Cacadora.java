package com.baquara.modelo;

import com.baquara.modelo.efeitos.EfeitoStatus;
import com.baquara.modelo.efeitos.SangramentoEfeito;
import java.util.*;

public class Cacadora extends Personagem implements AtributoEspecial, Entidade {
    private int penetracao;
    private int penetracaoMaxima;
    private int flechasPrecisas;
    private Random random;
    private double chanceCritico;
    private Map<Class<? extends EfeitoStatus>, EfeitoStatus> efeitos;

    public Cacadora() {
        super(PerTipo.CACADORA, "Caçadora", 120, 28, 12);
        this.penetracaoMaxima = 100;
        this.penetracao = 100;
        this.flechasPrecisas = 0;
        this.random = new Random();
        this.chanceCritico = 0.30;
        this.efeitos = new HashMap<>();
    }

    public int getPenetracao() { return penetracao; }
    public int getPenetracaoMaxima() { return penetracaoMaxima; }
    public int getFlechasPrecisas() { return flechasPrecisas; }

    public boolean consumirPenetracao(int quantidade) {
        if (penetracao >= quantidade) {
            penetracao -= quantidade;
            flechasPrecisas++;

            // ⭐ Usando os getters para mostrar informações mais claras
            System.out.println("🎯 Penetração consumida: " + quantidade);
            System.out.println("   📊 Restante: " + getPenetracao() + "/" + getPenetracaoMaxima());
            System.out.println("   🏹 Flechas Precisas: " + getFlechasPrecisas());
            System.out.println("   🩸 Novo sangramento: " + (12 + (getFlechasPrecisas() * 2)) + "/rodada");
            return true;
        }
        System.out.println("❌ Penetração insuficiente! (" + penetracao + "/" + penetracaoMaxima + ")");
        return false;
    }

    public void recuperarPenetracao(int quantidade) {
        int penetracaoAntes = penetracao;
        penetracao = Math.min(penetracaoMaxima, penetracao + quantidade);
        int recuperado = penetracao - penetracaoAntes;
        if (recuperado > 0) {
            System.out.println("🏹 Penetração recuperada: +" + recuperado +
                    " (" + penetracao + "/" + penetracaoMaxima + ")");
        }
    }

    @Override
    public void recarregarPorEstagio(int estagioNumero) {
        int recarga = estagioNumero * 8;
        penetracao = Math.min(penetracaoMaxima, penetracao + recarga);

        flechasPrecisas = 0;

        System.out.println("🏹 Penetração recarregada após estágio: +" + recarga +
                " (" + getPenetracao() + "/" + getPenetracaoMaxima() + ")");
        System.out.println("🔄 Flechas Precisas resetadas para 0!");
        System.out.println("   💡 Próximo sangramento: " + (12 + (getFlechasPrecisas() * 2)) + " de dano/rodada");
    }

    @Override
    public void recarregarPorNivel(int novoNivel) {
        penetracaoMaxima += 10;
        penetracao = penetracaoMaxima;
        chanceCritico = Math.min(0.50, 0.30 + (novoNivel * 0.02));
        System.out.println("🎯 Penetração máxima: " + penetracaoMaxima + " | Restaurada!");
        System.out.println("💫 Chance de crítico: " + (int)(chanceCritico * 100) + "%");
    }

    // Métodos da interface Entidade
    @Override
    public int getAtaque() { return ataque; }
    @Override
    public int getDefesa() { return defesa; }
    @Override
    public int getVida() { return vida; }
    @Override
    public int getVidaMax() { return vidaMax; }
    @Override
    public void setAtaque(int ataque) { this.ataque = ataque; }
    @Override
    public void setDefesa(int defesa) { this.defesa = defesa; }
    @Override
    public void setVida(int vida) { this.vida = Math.max(0, Math.min(vida, vidaMax)); }

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
        return efeitos.containsKey(tipoEfeito) && efeitos.get(tipoEfeito).estaAtivo();
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

        int danoInicial = 45 + (nivel * 4);
        boolean critico = random.nextDouble() < chanceCritico;

        if (critico) {
            danoInicial = (int)(danoInicial * 1.5);
            System.out.print("🎯 GOLPE CRÍTICO! ");
        }

        System.out.println("💥 Rajada de flechas causa " + danoInicial + " de dano!");
        alvo.tomarDano(danoInicial);

        int duracaoSangramento = 4;
        int danoSangramentoPorRodada = 12 + (flechasPrecisas * 2);

        System.out.println("\n🩸 SANGRAMENTO APLICADO!");
        System.out.println("   ⏱️ Duração: " + duracaoSangramento + " rodadas");
        System.out.println("   💔 Dano por rodada: " + danoSangramentoPorRodada);

        if (alvo.temEfeito(SangramentoEfeito.class)) {
            SangramentoEfeito sangramentoExistente = alvo.getEfeito(SangramentoEfeito.class);
            sangramentoExistente.renovar();
            System.out.println("🔄 Sangramento renovado! Duração resetada.");
        } else {
            SangramentoEfeito sangramento = new SangramentoEfeito(duracaoSangramento, danoSangramentoPorRodada);
            alvo.adicionarEfeito(sangramento);
        }

        System.out.println("🏹 Flechas precisas acumuladas: " + flechasPrecisas);

        int recuperacao = 10;
        if (critico) recuperacao = 15;
        recuperarPenetracao(recuperacao);
        System.out.println("✨ +" + recuperacao + " de Penetração recuperada pelo acerto!");
    }

    @Override
    public String getNomeHabilidade() {
        return "CHUVA DE FLECHAS";
    }

    @Override
    public String getDescricaoHabilidade() {
        int danoBase = 45 + (nivel * 4);
        int flechas = getFlechasPrecisas();  // ⭐ Usando o getter
        int sangramentoBase = 12 + (flechas * 2);
        int duracao = 4;
        int penetracaoAtual = getPenetracao();  // ⭐ Usando o getter
        int penetracaoMax = getPenetracaoMaxima();  // ⭐ Usando o getter

        return "🌿 CHUVA DE FLECHAS\n" +
                "   💥 Dano imediato: " + danoBase + " (" + (int)(chanceCritico * 100) + "% de crítico)\n" +
                "   🩸 Sangramento: " + sangramentoBase + " de dano por " + duracao + " rodadas\n" +
                "   ⏱️ Duração: " + duracao + " rodadas (renovável com novos usos)\n" +
                "   🏹 Flechas Precisas: " + flechas + " (+2 de sangramento cada)\n" +
                "   💫 Custo: 35 de Penetração (" + penetracaoAtual + "/" + penetracaoMax + " disponível)\n" +
                "   🔄 Flechas resetam a cada estágio!";
    }

    @Override
    public int ataqueInimigo(Personagem alvo) {
        int recuperacao = 5;
        recuperarPenetracao(recuperacao);
        int dano = ataque + (nivel * 2);
        System.out.println("🏹 " + nome + " dispara uma flecha causando " + dano + " de dano!");
        alvo.tomarDano(dano);
        return dano;
    }

    @Override
    public void mostrarStatus() {
        super.mostrarStatus();

        // ⭐ Usando os getters em vez de acesso direto
        System.out.println("   🎯 Penetração: " + getPenetracao() + "/" + getPenetracaoMaxima());
        System.out.println("   💫 Chance Crítico: " + (int)(chanceCritico * 100) + "%");

        int flechas = getFlechasPrecisas();
        if (flechas > 0) {
            System.out.println("   🏹 Flechas Precisas: " + flechas + " (+" + (flechas * 2) + " de sangramento)");
        }

        // Mostra o dano do sangramento atual
        int sangramentoBase = 12 + (flechas * 2);
        System.out.println("   🩸 Sangramento atual: " + sangramentoBase + " de dano por rodada");

        for (EfeitoStatus efeito : efeitos.values()) {
            if (efeito.estaAtivo()) {
                System.out.println("   " + efeito.getDescricao());
            }
        }
    }

    // AtributoEspecial
    @Override
    public String getNomeAtributo() { return "Penetração"; }
    @Override
    public int getValorAtual() { return penetracao; }
    @Override
    public int getValorMaximo() { return penetracaoMaxima; }
    @Override
    public double getPorcentagem() { return (double) penetracao / penetracaoMaxima * 100; }
    @Override
    public boolean consumir(int quantidade) { return consumirPenetracao(quantidade); }
    @Override
    public void recarregar(int quantidade) { recuperarPenetracao(quantidade); }
    @Override
    public void recarregarCompletamente() { penetracao = penetracaoMaxima; }
}