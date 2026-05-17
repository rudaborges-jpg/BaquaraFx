// PerguntaCompletarLacuna.java
package com.baquara.modelo;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class PerguntaCompletarLacuna extends Pergunta {
    private String dica;
    private List<String> respostasCorretas;  // ⭐ NOVO: lista de respostas aceitas

    // Construtor original (mantém compatibilidade)
    public PerguntaCompletarLacuna(int id, String texto, String respostaCorreta,
                                   Dificuldade dificuldade, PerTipo tipoPersonagem,
                                   String categoria, int estagioMinimo, int estagioMaximo) {
        super(id, texto, respostaCorreta, dificuldade, tipoPersonagem, categoria,
                estagioMinimo, estagioMaximo);
        this.dica = null;
        // ⭐ Converte resposta única em lista
        this.respostasCorretas = new ArrayList<>();
        this.respostasCorretas.add(respostaCorreta.toLowerCase());
    }

    // Construtor com DICA (original)
    public PerguntaCompletarLacuna(int id, String texto, String respostaCorreta, String dica,
                                   Dificuldade dificuldade, PerTipo tipoPersonagem,
                                   String categoria, int estagioMinimo, int estagioMaximo) {
        this(id, texto, respostaCorreta, dificuldade, tipoPersonagem, categoria,
                estagioMinimo, estagioMaximo);
        this.dica = dica;
    }

    // ⭐ NOVO CONSTRUTOR: aceita múltiplas respostas
    public PerguntaCompletarLacuna(int id, String texto, List<String> respostasCorretas,
                                   Dificuldade dificuldade, PerTipo tipoPersonagem,
                                   String categoria, int estagioMinimo, int estagioMaximo) {
        super(id, texto, respostasCorretas.get(0), dificuldade, tipoPersonagem, categoria,
                estagioMinimo, estagioMaximo);
        this.dica = null;
        this.respostasCorretas = new ArrayList<>();
        for (String resp : respostasCorretas) {
            this.respostasCorretas.add(resp.toLowerCase());
        }
    }

    // ⭐ NOVO CONSTRUTOR com dica + múltiplas respostas
    public PerguntaCompletarLacuna(int id, String texto, List<String> respostasCorretas, String dica,
                                   Dificuldade dificuldade, PerTipo tipoPersonagem,
                                   String categoria, int estagioMinimo, int estagioMaximo) {
        this(id, texto, respostasCorretas, dificuldade, tipoPersonagem, categoria,
                estagioMinimo, estagioMaximo);
        this.dica = dica;
    }

    // ⭐ NOVO MÉTODO: verifica se resposta está na lista
    public boolean isRespostaCorreta(String resposta) {
        if (resposta == null) return false;
        String respostaNorm = resposta.trim().toLowerCase();
        for (String correta : respostasCorretas) {
            if (respostaNorm.equals(correta)) return true;
        }
        return false;
    }

    // ⭐ GETTER para a lista de respostas
    public List<String> getRespostasCorretas() {
        return respostasCorretas;
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