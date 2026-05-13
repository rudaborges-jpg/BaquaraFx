package com.baquara.modelo;

import java.util.List;

public abstract class Pergunta {
    protected int id;
    protected String texto;
    protected String respostaCorreta;
    protected Dificuldade dificuldade;
    protected PerTipo tipoPersonagem;
    protected String categoria;
    protected int estagioMinimo;
    protected int estagioMaximo;  // ← NOVO: estágio máximo (se -1, vai até o fim)

    public enum Dificuldade {
        FACIL("Fácil", 10, 1, 4),
        MEDIO("Médio", 10, 4, 7),
        DIFICIL("Difícil", 10, 8, -1);

        private String nome;
        private int danoBase;
        private int estagioMinimo;
        private int estagioMaximo;

        Dificuldade(String nome, int danoBase, int estagioMinimo, int estagioMaximo) {
            this.nome = nome;
            this.danoBase = danoBase;
            this.estagioMinimo = estagioMinimo;
            this.estagioMaximo = estagioMaximo;
        }

        public String getNome() { return nome; }
        public int getDanoBase() { return danoBase; }
        public int getEstagioMinimo() { return estagioMinimo; }
        public int getEstagioMaximo() { return estagioMaximo; }
    }

    public Pergunta(int id, String texto, String respostaCorreta, Dificuldade dificuldade,
                    PerTipo tipoPersonagem, String categoria, int estagioMinimo, int estagioMaximo) {
        this.id = id;
        this.texto = texto;
        this.respostaCorreta = respostaCorreta;
        this.dificuldade = dificuldade;
        this.tipoPersonagem = tipoPersonagem;
        this.categoria = categoria;
        this.estagioMinimo = estagioMinimo;
        this.estagioMaximo = estagioMaximo;
    }

    public Pergunta(int id, String texto, String respostaCorreta, Dificuldade dificuldade,
                    PerTipo tipoPersonagem, String categoria, int estagioMinimo) {
        this(id, texto, respostaCorreta, dificuldade, tipoPersonagem, categoria,
                estagioMinimo, -1);
    }

    public int getId() { return id; }
    public String getTexto() { return texto; }
    public String getRespostaCorreta() { return respostaCorreta; }
    public Dificuldade getDificuldade() { return dificuldade; }
    public PerTipo getTipoPersonagem() { return tipoPersonagem; }
    public String getCategoria() { return categoria; }
    public int getEstagioMinimo() { return estagioMinimo; }
    public int getEstagioMaximo() { return estagioMaximo; }

    public boolean isAdequadaParaEstagio(int estagioNumero) {
        if (estagioNumero < estagioMinimo) return false;

        if (estagioMaximo != -1 && estagioNumero > estagioMaximo) return false;

        return true;
    }
    public abstract void exibir();
    public abstract List<String> getOpcoes();
}