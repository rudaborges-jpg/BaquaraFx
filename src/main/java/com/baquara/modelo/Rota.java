package com.baquara.modelo;

import java.util.ArrayList;
import java.util.List;

public class Rota {
    private PerTipo personagem;
    private String nomeRota;
    private List<Estagio> estagios;

    public Rota(PerTipo personagem, String nomeRota) {
        this.personagem = personagem;
        this.nomeRota = nomeRota;
        this.estagios = new ArrayList<>();
    }

    public void adicionarEstagio(Estagio estagio) {
        estagios.add(estagio);
    }

    public PerTipo getPersonagem() { return personagem; }
    public String getNomeRota() { return nomeRota; }
    public List<Estagio> getEstagios() { return estagios; }
    public int getTotalEstagios() { return estagios.size(); }

    public void mostrarInfo() {
        System.out.println("\n🗺️ ROTA: " + nomeRota);
        System.out.println("👤 Personagem: " + personagem.getNome());
        System.out.println("📚 Total de estágios: " + estagios.size());
        for (Estagio e : estagios) {
            System.out.println("   - Estágio " + e.getNumero() + ": " + e.getNome());
        }
    }
}
