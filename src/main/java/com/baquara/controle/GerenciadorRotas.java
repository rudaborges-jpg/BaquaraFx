package com.baquara.controle;

import com.baquara.modelo.*;
import java.util.*;

public class GerenciadorRotas {
    private Map<PerTipo, Rota> rotas;
    private Rota rotaAtual;

    public GerenciadorRotas() {
        this.rotas = new HashMap<>();
        criarTodasRotas();
    }

    private void criarTodasRotas() {
        Rota rotaPaladino = new Rota(PerTipo.PALADINO, "Caminho da Iluminação");

        rotaPaladino.adicionarEstagio(new Estagio(1, "Iniciação da Fé", 1));
        rotaPaladino.adicionarEstagio(new Estagio(2, "Prova da Devoção", 2));
        rotaPaladino.adicionarEstagio(new Estagio(3, "Julgamento Divino", 3));
        rotaPaladino.adicionarEstagio(new Estagio(4, "Catedral das Sombras", 4));
        rotaPaladino.adicionarEstagio(new Estagio(5, "Valquírias", 5));
        rotaPaladino.adicionarEstagio(new Estagio(6, "Purgatório", 6));
        rotaPaladino.adicionarEstagio(new Estagio(7, "Anjos Caídos", 7));
        rotaPaladino.adicionarEstagio(new Estagio(8, "Trono Celestial", 8));
        rotaPaladino.adicionarEstagio(new Estagio(9, "Arcanjo Miguel", 9));

        Inimigo chefaoPaladino = new Inimigo("👑 METATRON - O Anjo Supremo 👑", 1300, 45, 10);
        rotaPaladino.adicionarEstagio(new Estagio(10, "METATRON", 10, chefaoPaladino));

        rotas.put(PerTipo.PALADINO, rotaPaladino);

        Rota rotaCacadora = new Rota(PerTipo.CACADORA, "Caminho da Predadora");

        rotaCacadora.adicionarEstagio(new Estagio(1, "Floresta dos Sussurros", 1));
        rotaCacadora.adicionarEstagio(new Estagio(2, "Lago das Fadas", 2));
        rotaCacadora.adicionarEstagio(new Estagio(3, "Montanha Gelada", 3));
        rotaCacadora.adicionarEstagio(new Estagio(4, "Vulcão Adormecido", 4));
        rotaCacadora.adicionarEstagio(new Estagio(5, "Pântano Maldito", 5));
        rotaCacadora.adicionarEstagio(new Estagio(6, "Caverna dos Ursos", 6));
        rotaCacadora.adicionarEstagio(new Estagio(7, "Templo da Natureza", 7));
        rotaCacadora.adicionarEstagio(new Estagio(8, "Espírito da Floresta", 8));
        rotaCacadora.adicionarEstagio(new Estagio(9, "Guardião dos Animais", 9));

        Inimigo chefaoCacadora = new Inimigo("👑 FENRIR - O Lobo Gigante 👑", 1280, 50, 10);
        rotaCacadora.adicionarEstagio(new Estagio(10, "FENRIR", 10, chefaoCacadora));

        rotas.put(PerTipo.CACADORA, rotaCacadora);

        Rota rotaGuerreiro = new Rota(PerTipo.GUERREIRO, "Caminho da Glória");

        rotaGuerreiro.adicionarEstagio(new Estagio(1, "Campo de Treinamento", 1));
        rotaGuerreiro.adicionarEstagio(new Estagio(2, "Arena dos Gladiadores", 2));
        rotaGuerreiro.adicionarEstagio(new Estagio(3, "Castelo Sombrio", 3));
        rotaGuerreiro.adicionarEstagio(new Estagio(4, "Campo de Batalha", 4));
        rotaGuerreiro.adicionarEstagio(new Estagio(5, "Fortaleza Invencível", 5));
        rotaGuerreiro.adicionarEstagio(new Estagio(6, "Vale dos Reis", 6));
        rotaGuerreiro.adicionarEstagio(new Estagio(7, "Portão do Inferno", 7));
        rotaGuerreiro.adicionarEstagio(new Estagio(8, "Monte dos Gigantes", 8));
        rotaGuerreiro.adicionarEstagio(new Estagio(9, "Exército das Trevas", 9));

        Inimigo chefaoGuerreiro = new Inimigo("👑 ODIN - O Pai de Todos 👑", 1350, 55, 10);
        rotaGuerreiro.adicionarEstagio(new Estagio(10, "ODIN", 10, chefaoGuerreiro));

        rotas.put(PerTipo.GUERREIRO, rotaGuerreiro);

        Rota rotaSabio = new Rota(PerTipo.SABIO, "Caminho do Conhecimento");

        rotaSabio.adicionarEstagio(new Estagio(1, "Biblioteca Antiga", 1));
        rotaSabio.adicionarEstagio(new Estagio(2, "Observatório Estelar", 2));
        rotaSabio.adicionarEstagio(new Estagio(3, "Laboratório Alquímico", 3));
        rotaSabio.adicionarEstagio(new Estagio(4, "Academia dos Sábios", 4));
        rotaSabio.adicionarEstagio(new Estagio(5, "Torre do Saber", 5));
        rotaSabio.adicionarEstagio(new Estagio(6, "Museu Histórico", 6));
        rotaSabio.adicionarEstagio(new Estagio(7, "Planetário", 7));
        rotaSabio.adicionarEstagio(new Estagio(8, "Teatro Grego", 8));
        rotaSabio.adicionarEstagio(new Estagio(9, "Oráculo de Delfos", 9));

        Inimigo chefaoSabio = new Inimigo("👑 ATHENA - Deusa da Sabedoria 👑", 1260, 48, 10);
        rotaSabio.adicionarEstagio(new Estagio(10, "ATHENA", 10, chefaoSabio));

        rotas.put(PerTipo.SABIO, rotaSabio);

        Rota rotaArcanista = new Rota(PerTipo.ARCANISTA, "Caminho Místico");

        rotaArcanista.adicionarEstagio(new Estagio(1, "Torre de Marfim", 1));
        rotaArcanista.adicionarEstagio(new Estagio(2, "Círculo Mágico", 2));
        rotaArcanista.adicionarEstagio(new Estagio(3, "Floresta Encantada", 3));
        rotaArcanista.adicionarEstagio(new Estagio(4, "Labirinto Mítico", 4));
        rotaArcanista.adicionarEstagio(new Estagio(5, "Castelo Flutuante", 5));
        rotaArcanista.adicionarEstagio(new Estagio(6, "Plano Astral", 6));
        rotaArcanista.adicionarEstagio(new Estagio(7, "Reino dos Deuses", 7));
        rotaArcanista.adicionarEstagio(new Estagio(8, "Caverna da Magia", 8));
        rotaArcanista.adicionarEstagio(new Estagio(9, "Portal Dimensional", 9));

        Inimigo chefaoArcanista = new Inimigo("👑 LOKI - O Deus da Trapaça 👑", 1290, 52, 10);
        rotaArcanista.adicionarEstagio(new Estagio(10, "LOKI", 10, chefaoArcanista));

        rotas.put(PerTipo.ARCANISTA, rotaArcanista);

        // Diagnóstico
        System.out.println("\n✅ Rotas criadas com sucesso!");
        for (PerTipo tipo : PerTipo.values()) {
            if (tipo == PerTipo.CAPOEIRISTA) continue;
            System.out.println("   " + tipo.getNome() + ": " + rotas.get(tipo).getTotalEstagios() + " estágios");
        }
    }

    public Rota getRota(PerTipo tipo) {
        return rotas.get(tipo);
    }

    public void setRotaAtual(Rota rota) {
        this.rotaAtual = rota;
    }

    public Rota getRotaAtual() {
        return rotaAtual;
    }
}