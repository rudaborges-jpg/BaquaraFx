package com.baquara.controle;

import com.baquara.dados.BancoPerguntas;
import com.baquara.modelo.*;
import com.baquara.modelo.Pergunta.Dificuldade;
import java.util.*;

public class GerenciadorRodaCapoeira {
    private Jogador jogador;
    private Inimigo[] inimigos;
    private BesouroManganga besouro;
    private int estagioAtual;
    private int pontuacaoTotal;
    private Scanner scanner;
    private Random random;
    private BancoPerguntas  bancoPerguntas;

    private int perguntasCertas;
    private int perguntasErradas;
    private int vitorias;
    private int vezesSemTempo;
    private int contadorAtaquesBasicos;

    private String[] nomesMestres = {
            "MESTRE BIMBA",
            "MESTRE PASTINHA",
            "MESTRE JOÃO GRANDE",
            "MESTRE JOÃO PEQUENO",
            "MESTRE CANJIQUINHA",
            "MESTRE CAIÇARA",
            "MESTRE SUASSUNA",
            "MESTRE NENEL",
            "MESTRE MORAES"
    };

    private String[] titulosMestres = {
            "O Pai da Capoeira Regional",
            "O Guardião da Capoeira Angola",
            "O Discípulo de Pastinha",
            "O Filósofo da Capoeira",
            "O Gênio da Malícia",
            "O Guardião da Tradição Oral",
            "O Inovador da Capoeira",
            "O Herdeiro de Bimba",
            "O Filósofo da Capoeira Angola"
    };

    private String[] historiasMestres = {
            "Manoel dos Reis Machado (1900-1974). Criou a Capoeira Regional em 1928.\n" +
                    "Primeiro a tirar a capoeira da marginalidade e transformá-la em arte marcial\n" +
                    "respeitada. Criou a 'sequência de Bimba' com 8 movimentos básicos.",
            "Vicente Ferreira Pastinha (1889-1981). Grande defensor da Capoeira Angola,\n" +
                    "a forma mais tradicional. Ensinou que capoeira é dança, luta, filosofia e\n" +
                    "resistência cultural. 'Capoeira é tudo que a boca come.'",
            "João Oliveira dos Santos (1933-2022). Discípulo de Mestre Pastinha, levou a\n" +
                    "Capoeira Angola para o mundo. Ensinou nos EUA e formou gerações de\n" +
                    "capoeiristas. Foi Doutor Honoris Causa.",
            "João Pereira dos Santos (1917-2011). Também discípulo de Pastinha, era\n" +
                    "conhecido por sua técnica impecável e movimentos precisos. 'Na capoeira,\n" +
                    "o importante não é o golpe, é a intenção.'",
            "José Anastácio dos Santos (1937-2003). Discípulo de Bimba, era mestre da\n" +
                    "malícia e da mandinga. Suas rodas eram famosas pela imprevisibilidade e\n" +
                    "criatividade. 'Malandro não briga, malandro ginga.'",
            "Antônio Carlos Moraes (1938-2008). Guardião das tradições orais da capoeira.\n" +
                    "Preservou ladainhas, corridos e histórias ancestrais. Sua memória guardava\n" +
                    "séculos de sabedoria da capoeira.",
            "Reinaldo Ramos Suassuna (1938-). Fundador do Grupo Cordão de Ouro, criou um\n" +
                    "estilo próprio que mistura tradição e inovação. Suas rodas são espetáculos\n" +
                    "de técnica e beleza.",
            "Manoel Nascimento Machado (1956-). Filho de Mestre Bimba, é o guardião do\n" +
                    "legado da Capoeira Regional. Mantém viva a sequência de ensino criada por\n" +
                    "seu pai.",
            "Pedro Moraes Trindade (1950-). Fundador do GCAP, um dos maiores pensadores\n" +
                    "da capoeira. Sua filosofia une ancestralidade africana, resistência cultural\n" +
                    "e transformação social. 'Capoeira é revolução.'"
    };

    private String[] frasesDerrota = {
            "A capoeira nunca morrerá... Ela vive em cada ginga...",
            "Capoeira é tudo que a boca come e o coração sente...",
            "A capoeira é minha vida... Minha filosofia...",
            "Na capoeira, o importante não é o golpe, é a intenção...",
            "Malandro não briga, malandro ginga...",
            "Quem não sabe ler a história, está condenado a repeti-la...",
            "Capoeira é identidade, é raiz, é resistência...",
            "A capoeira regional é a arte de meu pai... Minha herança...",
            "Capoeira é revolução... É a arte de transformar dor em dança..."
    };

    private String[] frasesContraAtaque = {
            "Aprenda a ser imprevisível!",
            "Capoeira é mandinga, não repetição!",
            "Assim você não vai longe...",
            "Está muito óbvio, capoeirista!",
            "Use a criatividade, não só a ginga!",
            "Já decorei seus passos!",
            "A roda não perdoa o previsível!",
            "Isso é tudo que você sabe?",
            "Malandro que é malandro não repete golpe!"
    };

    private int[] vidasMestres = {
            160, 260, 360, 460, 560, 660, 760, 860, 960
    };

    private int[] ataquesMestres = {
            36, 42, 48, 54, 60, 66, 72, 78, 84
    };

    public GerenciadorRodaCapoeira(Jogador jogador) {
        this.jogador = jogador;
        this.inimigos = new Inimigo[9];
        this.besouro = new BesouroManganga();
        this.estagioAtual = 0;
        this.pontuacaoTotal = 0;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.bancoPerguntas = new BancoPerguntas();

        this.perguntasCertas = 0;
        this.perguntasErradas = 0;
        this.vitorias = 0;
        this.vezesSemTempo = 0;
        this.contadorAtaquesBasicos = 0;

        // Cria os inimigos baseados nos mestres usando o NOVO CONSTRUTOR
        for (int i = 0; i < 9; i++) {
            int nivel = i + 1;
            int defesa = 5 + (nivel * 3);
            // ⭐ CONSTRUTOR ATUALIZADO: nome, nivel, vida, ataque, defesa
            inimigos[i] = new Inimigo(nomesMestres[i], nivel, vidasMestres[i], ataquesMestres[i], defesa);
        }
    }

    public void iniciarRodaProibida() {
        System.out.println("\n🥋 A RODA PROIBIDA COMEÇA! 🥋");
        System.out.println("=".repeat(60));
        System.out.println("📋 REGRAS DA RODA:");
        System.out.println("   🎯 Escolha seu tipo de ataque (SEM pressa)");
        System.out.println("   📚 Ataque Básico     → Pergunta FÁCIL");
        System.out.println("   💫 Ataque Difícil    → Pergunta MÉDIA");
        System.out.println("   🔥 Combinação Mortal → Pergunta DIFÍCIL");
        System.out.println("   🌀 Esquiva           → SEM pergunta (desvia do ataque)");
        System.out.println("   ⚠️  3 ataques básicos seguidos → Inimigo ESQUIVA (100%)");
        System.out.println("=".repeat(60));

        System.out.print("\n🎵 Pressione ENTER para o berimbau tocar...");
        scanner.nextLine();

        for (int i = 0; i < inimigos.length; i++) {
            estagioAtual = i + 1;
            boolean venceu = enfrentarMestre(inimigos[i], i);

            if (!venceu) {
                derrotaNaRoda();
                return;
            }

            vitorias++;

            Capoeirista cap = (Capoeirista) jogador.getPersonagem();
            cap.evoluirTitulo(estagioAtual + 1);
            cap.recarregarTotalmente();

            if (i < inimigos.length - 1) {
                System.out.println("\n🎵 O berimbau chama o próximo desafiante...");
                System.out.print("\nPressione ENTER para continuar...");
                scanner.nextLine();
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🦗 O BESOURO MANGANGÁ ENTRA NA RODA! 🦗");
        System.out.println("=".repeat(60));
        System.out.println("\n📜 A LENDA DO BESOURO:");
        System.out.println("Manoel Henrique Pereira (1895-1924), o lendário Besouro Mangangá.");
        System.out.println("Nascido em Santo Amaro da Purificação, Bahia.");
        System.out.println("Diziam que tinha o 'corpo fechado' - balas e facas não o feriam.");
        System.out.println("\n🦗 'FECHADO! NINGUÉM ME SEGURA!'\n");

        System.out.print("Pressione ENTER para o confronto final...");
        scanner.nextLine();

        contadorAtaquesBasicos = 0;
        boolean venceuBesouro = batalhaDeCapoeira(besouro, true);

        if (venceuBesouro) {
            vitoriaFinal();
        } else {
            derrotaFinal();
        }
    }

    private boolean enfrentarMestre(Inimigo mestre, int index) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🥋 ESTÁGIO " + (index + 1) + "/10");
        System.out.println("=".repeat(60));

        System.out.println("\n📜 HISTÓRIA DO MESTRE:");
        System.out.println(historiasMestres[index]);

        System.out.println("\n👥 DESAFIANTE: " + nomesMestres[index]);
        System.out.println("   🏅 " + titulosMestres[index]);
        System.out.println("   ❤️ Vida: " + vidasMestres[index] + " | ⚔️ Ataque: " + ataquesMestres[index]);

        System.out.print("\nPressione ENTER para começar o jogo...");
        scanner.nextLine();

        contadorAtaquesBasicos = 0;
        return batalhaDeCapoeira(mestre, false);
    }

    private boolean inimigoVaiEsquivar(int escolhaAtaque, Inimigo inimigo) {
        if (escolhaAtaque == 1) {
            contadorAtaquesBasicos++;
        } else {
            contadorAtaquesBasicos = 0;
            return false;
        }

        if (contadorAtaquesBasicos >= 3) {
            System.out.println("\n🔄 O INIMIGO APRENDEU SEU PADRÃO!");
            System.out.println("   '" + inimigo.getNome() + "': Você só está gingando... Previsível!'");
            return true;
        }

        return false;
    }

    private void executarEsquivaInimigo(Inimigo inimigo, boolean ehChefao) {
        System.out.println("\n✅ CORRETO! Mas...");
        System.out.println("🔄 " + inimigo.getNome() + " ESQUIVA DO SEU ATAQUE!");

        if (random.nextDouble() < 0.5) {
            int danoContraAtaque = (int)(calcularDanoInimigo(inimigo, ehChefao) * 0.7);
            System.out.println("⚡ CONTRA-ATAQUE! " + inimigo.getNome() + " te acerta com " + danoContraAtaque + " de dano!");
            jogador.tomarDano(danoContraAtaque);
            System.out.println("   💬 '" + frasesContraAtaque[random.nextInt(frasesContraAtaque.length)] + "'");
        } else {
            System.out.println("   Mas o inimigo apenas desviou sem revidar.");
        }
    }

    private int calcularDanoBase(int tipoAtaque) {
        Capoeirista cap = (Capoeirista) jogador.getPersonagem();
        int ataque = cap.getAtaque();
        int estagio = Math.max(1, estagioAtual);

        switch (tipoAtaque) {
            case 1: return (ataque + 5) * (estagio / 3 + 1);
            case 2: return (ataque + 15) * (estagio / 3 + 1);
            case 3: return (8 * 3) * (estagio / 3 + 1);
            default: return ataque * (estagio / 3 + 1);
        }
    }

    private int calcularDanoEscalado(int danoBase, Dificuldade dificuldade) {
        if (estagioAtual <= 0) return danoBase;

        int multiplicadorDificuldade;
        switch (dificuldade) {
            case FACIL: multiplicadorDificuldade = 1; break;
            case MEDIO: multiplicadorDificuldade = 2; break;
            case DIFICIL: multiplicadorDificuldade = 3; break;
            default: multiplicadorDificuldade = 1;
        }

        int multiplicadorEstagio = Math.max(1, estagioAtual / 3 + 1);
        int danoEscalado = danoBase * multiplicadorEstagio;
        double variacao = 0.85 + (random.nextDouble() * 0.3);
        danoEscalado = (int)(danoEscalado * variacao * multiplicadorDificuldade);
        danoEscalado = Math.max(danoBase / 2, danoEscalado);
        danoEscalado = Math.min(350, danoEscalado);

        return danoEscalado;
    }

    private boolean batalhaDeCapoeira(Inimigo inimigo, boolean ehChefao) {
        Capoeirista capoeirista = (Capoeirista) jogador.getPersonagem();
        int rodada = 0;

        while (jogador.vivo() && inimigo.vivo()) {
            rodada++;
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🔄 RODADA " + rodada);
            if (ehChefao) {
                System.out.println("🦗 CONFRONTO FINAL CONTRA O BESOURO!");
            }
            System.out.println("=".repeat(60));

            capoeirista.mostrarStatus();
            System.out.println();
            inimigo.mostrarStatus();

            int escolhaAtaque = mostrarMenuAtaqueSemTempo();

            if (escolhaAtaque == 4) {
                System.out.println("\n🌀 ESQUIVA DE CAPOEIRA!");
                boolean esquivou = capoeirista.executarEsquiva(inimigo);
                if (esquivou) {
                    capoeirista.consumirEnergiaGinga(-5);
                }
                contadorAtaquesBasicos = 0;

                if (!inimigo.vivo()) {
                    System.out.println("\n💀 " + inimigo.getNome() + " FOI DERROTADO!");
                    processarVitoriaInimigo(ehChefao);
                    return true;
                }

                if (inimigo.vivo() && jogador.vivo()) {
                    System.out.print("\n⏭️  Pressione ENTER para próxima rodada...");
                    scanner.nextLine();
                }
                continue;
            }

            Dificuldade dificuldade = getDificuldadePorAtaque(escolhaAtaque);
            Pergunta pergunta = bancoPerguntas.getPerguntaAleatoriaPorDificuldade(
                    PerTipo.CAPOEIRISTA,
                    dificuldade,
                    estagioAtual  // estágio atual (1-10)
            );
            if (pergunta == null) {
                System.out.println("❌ Erro ao carregar pergunta!");
                return false;
            }

            System.out.println("\n" + "=".repeat(50));
            System.out.println("📚 PERGUNTA DE CAPOEIRA");
            System.out.println("   Dificuldade: " + getNomeDificuldade(dificuldade));
            System.out.println("=".repeat(50));
            pergunta.exibir();

            System.out.print("\n✏️  Sua resposta: ");
            String resposta = scanner.nextLine().trim().toUpperCase();

            boolean acertou = AvaliadorRespostas.avaliar(pergunta, resposta);

            if (acertou) {
                perguntasCertas++;

                if (inimigoVaiEsquivar(escolhaAtaque, inimigo)) {
                    executarEsquivaInimigo(inimigo, ehChefao);
                    capoeirista.consumirEnergiaGinga(-5);
                } else {
                    System.out.println("\n✅ CORRETO! Execute seu golpe!");
                    int danoBase = executarAtaqueComDano(escolhaAtaque, capoeirista, inimigo);
                    int danoFinal = calcularDanoEscalado(danoBase, dificuldade);
                    System.out.println("💥 DANO: " + danoFinal);
                    inimigo.tomarDano(danoFinal);

                    int recargaGinga;
                    switch (escolhaAtaque) {
                        case 1: recargaGinga = 15; break;
                        case 2: recargaGinga = 10; break;
                        case 3: recargaGinga = 5; break;
                        default: recargaGinga = 10;
                    }
                    capoeirista.consumirEnergiaGinga(-recargaGinga);
                }
            } else {
                perguntasErradas++;
                contadorAtaquesBasicos = 0;
                System.out.println("\n❌ ERRADO!");
                System.out.println("   📖 Resposta correta: " + pergunta.getRespostaCorreta());

                int danoInimigo = calcularDanoInimigo(inimigo, ehChefao);
                System.out.println("💢 " + inimigo.getNome() + " contra-ataca causando " + danoInimigo + " de dano!");
                jogador.tomarDano(danoInimigo);
            }

            if (!inimigo.vivo()) {
                System.out.println("\n💀 " + inimigo.getNome() + " FOI DERROTADO!");
                processarVitoriaInimigo(ehChefao);
                return true;
            }

            if (!jogador.vivo()) {
                return false;
            }

            if (inimigo.vivo() && jogador.vivo()) {
                System.out.print("\n⏭️  Pressione ENTER para próxima rodada...");
                scanner.nextLine();
            }
        }

        return jogador.vivo();
    }

    private int executarAtaqueComDano(int escolha, Capoeirista cap, Inimigo inimigo) {
        System.out.print("\n💨 Preparando golpe");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(300);
                System.out.print(".");
                System.out.flush();
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println();

        int danoBase = 0;

        switch (escolha) {
            case 1:
                System.out.println("\n🔄 GINGA BÁSICA!");
                danoBase = cap.getAtaque() + random.nextInt(10) + 5;
                System.out.println("   Dano base: " + danoBase);
                break;
            case 2:
                System.out.println("\n💫 ATAQUE DIFÍCIL!");
                String[] movimentos = {
                        "🌪️ ARMADA VIOLENTA!", "🦵 MEIA LUA DE COMPASSO!",
                        "💫 AÚ BATIDO!", "⚡ MARTELO DE NEGATIVA!", "🎯 QUEIXADA!"
                };
                String movimento = movimentos[random.nextInt(movimentos.length)];
                danoBase = cap.getAtaque() + 15 + random.nextInt(20);
                System.out.println(movimento + " 💥 Dano base: " + danoBase);

                if (random.nextDouble() < 0.3) {
                    int bonus = random.nextInt(10) + 5;
                    danoBase += bonus;
                    System.out.println("🎯 GOLPE PRECISO! +" + bonus + " de dano extra!");
                }
                break;
            case 3:
                System.out.println("\n🔥 COMBINAÇÃO MORTAL!");
                String[] combinacoes = {
                        "💢 GINGA → ARMADA → MEIA LUA DE COMPASSO!",
                        "⚔️ NEGATIVA → ROLÊ → MARTELO → QUEIXADA!",
                        "🌀 AÚ → BANANEIRA → CHAPÉU DE COURO → RASTEIRA!",
                        "🎵 BERIMBAU → ESQUIVA → CABEÇADA → VOADORA!"
                };
                System.out.println("   " + combinacoes[random.nextInt(combinacoes.length)]);

                int golpes = 3 + random.nextInt(2);
                danoBase = 0;
                for (int i = 0; i < golpes; i++) {
                    int danoGolpe = 8 + random.nextInt(12);
                    System.out.println("   💥 Golpe " + (i+1) + ": " + danoGolpe + " de dano!");
                    danoBase += danoGolpe;
                }
                System.out.println("   🎯 Dano base total: " + danoBase);
                break;
        }

        return danoBase;
    }

    private void processarVitoriaInimigo(boolean ehChefao) {
        if (!ehChefao) {
            int index = estagioAtual - 1;
            System.out.println("   Últimas palavras: \"" + frasesDerrota[index] + "\"");
        } else {
            System.out.println("   🦗 O BESOURO CAIU! IMPOSSÍVEL!");
        }

        int bonus = estagioAtual * 35;
        pontuacaoTotal += bonus;
        jogador.addPontuacao(bonus);
        System.out.println("🏆 +" + bonus + " pontos! Total: " + pontuacaoTotal);
    }

    private int mostrarMenuAtaqueSemTempo() {
        Capoeirista cap = (Capoeirista) jogador.getPersonagem();
        int escolha = -1;

        while (escolha < 1 || escolha > 4) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📋 ESCOLHA SEU TIPO DE ATAQUE:");
            System.out.println("=".repeat(60));
            System.out.println("\n1 🔄 GINGA BÁSICA (Ataque Normal)");
            System.out.println("2 💫 ATAQUE DIFÍCIL (Movimentos Acrobáticos)");
            System.out.println("3 🔥 COMBINAÇÃO MORTAL (Sequência de Golpes)");
            System.out.println("4 🌀 ESQUIVA DE CAPOEIRA");
            System.out.println("\n🌀 Ginga disponível: " + cap.getEnergiaGinga() + "/" + cap.getEnergiaMaxima());
            System.out.println("=".repeat(60));
            System.out.print("\n🎯 Sua escolha (1-4): ");

            try {
                String entrada = scanner.nextLine().trim();
                escolha = Integer.parseInt(entrada);

                if (escolha == 2 && cap.getEnergiaGinga() < 20) {
                    System.out.println("\n❌ GINGA INSUFICIENTE! Você precisa de 20 de Ginga.");
                    escolha = -1;
                    continue;
                }
                if (escolha == 3 && cap.getEnergiaGinga() < 40) {
                    System.out.println("\n❌ GINGA INSUFICIENTE! Você precisa de 40 de Ginga.");
                    escolha = -1;
                    continue;
                }
                if (escolha == 4 && cap.getEsquivasRestantes() <= 0) {
                    System.out.println("\n❌ SEM ESQUIVAS! Você já usou todas.");
                    escolha = -1;
                    continue;
                }
                if (escolha < 1 || escolha > 4) {
                    System.out.println("\n❌ Opção inválida! Escolha entre 1 e 4.");
                    escolha = -1;
                }
            } catch (NumberFormatException e) {
                System.out.println("\n❌ Entrada inválida! Digite um número de 1 a 4.");
                escolha = -1;
            }
        }
        return escolha;
    }

    private Dificuldade getDificuldadePorAtaque(int escolhaAtaque) {
        switch (escolhaAtaque) {
            case 1: return Dificuldade.FACIL;
            case 2: return Dificuldade.MEDIO;
            case 3: return Dificuldade.DIFICIL;
            default: return Dificuldade.FACIL;
        }
    }

    private String getNomeDificuldade(Dificuldade dificuldade) {
        switch (dificuldade) {
            case FACIL: return "⭐ FÁCIL";
            case MEDIO: return "⭐⭐ MÉDIO";
            case DIFICIL: return "⭐⭐⭐ DIFÍCIL";
            default: return "⭐ FÁCIL";
        }
    }

    private int calcularDanoInimigo(Inimigo inimigo, boolean ehChefao) {
        int danoBase = inimigo.getAtaque();

        if (ehChefao && besouro.getFaseAtual() == 3) {
            danoBase *= 2;
            System.out.println("⚡ FÚRIA DO BESOURO! Dano dobrado!");
        }

        double variacao = 0.85 + (random.nextDouble() * 0.3);
        int danoFinal = (int)(danoBase * variacao);
        return Math.max(5, danoFinal);
    }

    private void derrotaNaRoda() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💀 VOCÊ CAIU NA RODA! 💀");
        System.out.println("=".repeat(60));
        exibirResultados();
    }

    private void derrotaFinal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🦗 O BESOURO VENCEU! 🦗");
        System.out.println("=".repeat(60));
        exibirResultados();
    }

    private void vitoriaFinal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("👑 VOCÊ SE TORNOU O NOVO BESOURO! 👑");
        System.out.println("=".repeat(60));
        exibirResultados();
    }

    private void exibirResultados() {
        System.out.println("\n📊 RESULTADOS DA RODA PROIBIDA");
        System.out.println("=".repeat(50));
        System.out.println("🏆 Pontuação Total: " + pontuacaoTotal);
        System.out.println("📊 Perguntas Respondidas: " + (perguntasCertas + perguntasErradas));
        System.out.println("✅ Acertos: " + perguntasCertas);
        System.out.println("❌ Erros: " + perguntasErradas);

        if (perguntasCertas + perguntasErradas > 0) {
            double aproveitamento = (double) perguntasCertas / (perguntasCertas + perguntasErradas) * 100;
            System.out.println("📈 Aproveitamento: " + String.format("%.1f", aproveitamento) + "%");
        }

        System.out.println("🥋 Mestres Derrotados: " + vitorias + "/9");

        Capoeirista c = (Capoeirista) jogador.getPersonagem();
        System.out.println("\n👤 SEU CAPOEIRISTA:");
        System.out.println("   🏅 Título: " + c.getNome());
        System.out.println("   ❤️ Vida Final: " + c.getVida() + "/" + c.getVidaMax());
        System.out.println("   ⚔️ Ataque: " + c.getAtaque());
        System.out.println("   🛡️ Defesa: " + c.getDefesa());
        System.out.println("   🌀 Ginga Máxima: " + c.getEnergiaMaxima());
        System.out.println("\n🎵 O berimbau continuará tocando...");
        System.out.println("   A capoeira nunca morrerá!");
        System.out.println("   Axé! 🙏\n");
    }
}