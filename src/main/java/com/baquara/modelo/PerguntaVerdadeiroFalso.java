package com.baquara.modelo;

import java.util.Arrays;
import java.util.List;

public class PerguntaVerdadeiroFalso extends Pergunta {

    // Construtor completo com estagioMaximo
    public PerguntaVerdadeiroFalso(int id, String texto, boolean respostaCorreta,
                                   Dificuldade dificuldade, PerTipo tipoPersonagem,
                                   String categoria, int estagioMinimo, int estagioMaximo) {
        super(id, texto, respostaCorreta ? "V" : "F",
                dificuldade, tipoPersonagem, categoria, estagioMinimo, estagioMaximo);
    }

    // Construtor simplificado (usa estagioMaximo = -1)
    public PerguntaVerdadeiroFalso(int id, String texto, boolean respostaCorreta,
                                   Dificuldade dificuldade, PerTipo tipoPersonagem,
                                   String categoria, int estagioMinimo) {
        this(id, texto, respostaCorreta, dificuldade, tipoPersonagem,
                categoria, estagioMinimo, -1);
    }

    @Override
    public void exibir() {
        System.out.println("\n📖 " + getTexto());
        System.out.println("   V) Verdadeiro");
        System.out.println("   F) Falso");
        System.out.println("[Dificuldade: " + getDificuldade().getNome() + "]");
    }

    @Override
    public List<String> getOpcoes() {
        return Arrays.asList("Verdadeiro", "Falso");
    }
}
