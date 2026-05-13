package com.baquara.modelo;

public enum PerTipo {
    PALADINO("Paladino", "Religião, fé, justiça, sagrado"),
    CACADORA("Caçadora", "Natureza, sobrevivência, animais, rastreamento"),
    GUERREIRO("Guerreiro", "Combate, estratégia militar, história de guerras"),
    SABIO("Sábio", "Conhecimento geral, filosofia, ciência, arte"),
    ARCANISTA("Arcanista", "Magia, mitologia, elementos, ocultismo"),
    CAPOEIRISTA("Capoeirista", "Capoeira, cultura afro-brasileira, música, dança");

    private String nome;
    private String tema;

    PerTipo(String nome, String tema) {
        this.nome = nome;
        this.tema = tema;
    }

    public String getNome() { return nome; }
    public String getTema() { return tema; }
}
