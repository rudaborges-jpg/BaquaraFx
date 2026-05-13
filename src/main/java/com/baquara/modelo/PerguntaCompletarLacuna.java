package com.baquara.modelo;

import java.util.List;

public class PerguntaCompletarLacuna extends Pergunta {
    private String dica;

    public PerguntaCompletarLacuna(int id, String texto, String respostaCorreta,
                                   Dificuldade dificuldade, PerTipo tipoPersonagem,
                                   String categoria, int estagioMinimo, int estagioMaximo) {
        super(id, texto, respostaCorreta, dificuldade, tipoPersonagem, categoria,
                estagioMinimo, estagioMaximo);
        this.dica = null;
    }

    public PerguntaCompletarLacuna(int id, String texto, String respostaCorreta,
                                   Dificuldade dificuldade, PerTipo tipoPersonagem,
                                   String categoria, int estagioMinimo) {
        this(id, texto, respostaCorreta, dificuldade, tipoPersonagem,
                categoria, estagioMinimo, -1);
    }

    public PerguntaCompletarLacuna(int id, String texto, String respostaCorreta, String dica,
                                   Dificuldade dificuldade, PerTipo tipoPersonagem,
                                   String categoria, int estagioMinimo, int estagioMaximo) {
        super(id, texto, respostaCorreta, dificuldade, tipoPersonagem, categoria,
                estagioMinimo, estagioMaximo);
        this.dica = dica;
    }

    @Override
    public void exibir() {
        System.out.println("\n📖 " + getTexto());
        System.out.println("   ✏️ Digite a palavra/frase que completa a lacuna:");
        if (dica != null) {
            System.out.println("   💡 Dica: " + dica);
        }
        System.out.println("[Dificuldade: " + getDificuldade().getNome() + "]");
    }

    @Override
    public List<String> getOpcoes() {
        return null;
    }

    public String getDica() {
        return dica;
    }
}