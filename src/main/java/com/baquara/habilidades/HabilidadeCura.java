// 📁 habilidades/HabilidadeCura.java

package com.baquara.habilidades;

import com.baquara.modelo.Inimigo;
import com.baquara.modelo.Personagem;
import com.baquara.modelo.AtributoEspecial;
import com.baquara.modelo.ValoresHabilidade;

public class HabilidadeCura implements HabilidadeEspecial {
    private Personagem usuario;
    private String nome;
    private String descricao;

    public HabilidadeCura(Personagem usuario) {
        this.usuario = usuario;
        this.nome = "ESPÍRITO SAGRADO";
        atualizarDescricao();
    }

    private void atualizarDescricao() {
        int ataque = usuario.getAtaque();
        int curaEstimada = (int)(ataque * 1.5);
        int danoEstimado = (int)(ataque * 0.8);
        this.descricao = "Invoca luz divina para curar ~" + curaEstimada +
                " de vida e causar ~" + danoEstimado + " de dano sagrado.\n" +
                "   📿 Cura e dano aumentam com Poder Divino restante!\n" +
                "   💫 Custo: " + ValoresHabilidade.CUSTO_PALADINO + " de Poder Divino\n" +
                "   ✨ Recupera " + ValoresHabilidade.RECUPERACAO_PALADINO + " de Poder Divino por acerto!";
    }

    @Override
    public String getNome() {
        return nome;
    }

    @Override
    public String getDescricao() {
        atualizarDescricao();
        return descricao;
    }

    @Override
    public boolean podeUsar() {
        if (!(usuario instanceof AtributoEspecial)) return false;
        AtributoEspecial attr = (AtributoEspecial) usuario;
        return attr.getValorAtual() >= ValoresHabilidade.CUSTO_PALADINO;
    }

    @Override
    public void recarregarAposAcerto() {
        if (!(usuario instanceof AtributoEspecial)) return;
        AtributoEspecial attr = (AtributoEspecial) usuario;
        attr.recarregar(ValoresHabilidade.RECUPERACAO_PALADINO);
    }

    @Override
    public int executar(Inimigo alvo) {
        if (!podeUsar()) {
            System.out.println("❌ " + usuario.getNome() + " não tem " +
                    ((AtributoEspecial) usuario).getNomeAtributo() + " suficiente!");
            return 0;
        }

        AtributoEspecial attr = (AtributoEspecial) usuario;
        int custo = ValoresHabilidade.CUSTO_PALADINO;

        // Consome o recurso
        attr.consumir(custo);

        // Cálculos baseados no ATAQUE do personagem
        int ataque = usuario.getAtaque();
        int bonusPoder = attr.getValorAtual() / 4;

        // Cura = 1.5x o ataque + bônus do poder divino
        int curaTotal = (int)(ataque * 1.5) + bonusPoder;

        System.out.println("\n🙏 " + usuario.getNome() + " invoca o ESPÍRITO SAGRADO!");
        System.out.println("💙 Consumiu " + custo + " de " + attr.getNomeAtributo() +
                " (Restante: " + attr.getValorAtual() + "/" + attr.getValorMaximo() + ")");

        usuario.curar(curaTotal);
        System.out.println("💚 Curou " + curaTotal + " de vida!");

        // Dano = 0.8x o ataque + bônus do poder divino
        int dano = (int)(ataque * 0.8) + bonusPoder;
        System.out.println("⚡ Luz divina atinge " + alvo.getNome() + " causando " + dano + " de dano!");
        alvo.tomarDano(dano);

        return dano;
    }
}