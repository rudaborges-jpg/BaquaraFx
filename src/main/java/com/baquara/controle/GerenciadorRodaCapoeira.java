package com.baquara.controle;

import com.baquara.modelo.*;
import com.baquara.dados.BancoPerguntasCapoeira;
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
    private BancoPerguntasCapoeira bancoCapoeira;

    private int perguntasCertas;
    private int perguntasErradas;
    private int vitorias;
    private int vezesSemTempo;

    // 🆕 Contador de ataques básicos consecutivos
    private int contadorAtaquesBasicos;

    // Dados dos mestres
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

    // 🆕 Frases dos mestres quando contra-atacam após esquiva
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
            160,  // Estágio 1 - Bimba (60 + 1*100)
            260,  // Estágio 2 - Pastinha
            360,  // Estágio 3 - João Grande
            460,  // Estágio 4 - João Pequeno
            560,  // Estágio 5 - Canjiquinha
            660,  // Estágio 6 - Caiçara
            760,  // Estágio 7 - Suassuna
            860,  // Estágio 8 - Nenel
            960   // Estágio 9 - Moraes
    };

    private int[] ataquesMestres = {
            36,
            42,
            48,
            54,
            60,
            66,
            72,
            78,
            84
    };

    public GerenciadorRodaCapoeira(Jogador jogador) {
        this.jogador = jogador;
        this.inimigos = new Inimigo[9];
        this.besouro = new BesouroManganga();
        this.estagioAtual = 0;
        this.pontuacaoTotal = 0;
        this.scanner = new Scanner(System.in);
        this.random = new Random();
        this.bancoCapoeira = new BancoPerguntasCapoeira();

        this.perguntasCertas = 0;
        this.perguntasErradas = 0;
        this.vitorias = 0;
        this.vezesSemTempo = 0;
        this.contadorAtaquesBasicos = 0; // 🆕 Inicializa contador

        // Cria os inimigos baseados nos mestres
        for (int i = 0; i < 9; i++) {
            inimigos[i] = new Inimigo(
                    nomesMestres[i],
                    vidasMestres[i],
                    ataquesMestres[i],
                    i + 1
            );
        }
    }

    // ============ INÍCIO DA RODA ============

    public void iniciarRodaProibida() {
        System.out.println("\n🥋 A RODA PROIBIDA COMEÇA! 🥋");
        System.out.println("=".repeat(60));
        System.out.println("📋 REGRAS DA RODA:");
        System.out.println("   🎯 Escolha seu tipo de ataque (SEM pressa)");
        System.out.println("   📚 Ataque Básico     → Pergunta FÁCIL  (Dano: " + calcularDanoBase(1) + "-" + calcularDanoBase(1, true) + ")");
        System.out.println("   💫 Ataque Difícil    → Pergunta MÉDIA  (Dano: " + calcularDanoBase(2) + "-" + calcularDanoBase(2, true) + ")");
        System.out.println("   🔥 Combinação Mortal → Pergunta DIFÍCIL (Dano: " + calcularDanoBase(3) + "-" + calcularDanoBase(3, true) + ")");
        System.out.println("   🌀 Esquiva           → SEM pergunta (desvia do ataque)");
        System.out.println("   ⏱️  Sem limite de tempo para responder!");
        System.out.println("   ❌ Se errar, o inimigo contra-ataca!");
        System.out.println("   ⚠️  3 ataques básicos seguidos → Inimigo ESQUIVA (100%)");
        System.out.println("   ⚡ Esquiva do inimigo → 50% de chance de CONTRA-ATAQUE");
        System.out.println("=".repeat(60));

        System.out.print("\n🎵 Pressione ENTER para o berimbau tocar...");
        scanner.nextLine();

        // Enfrenta os 9 mestres
        for (int i = 0; i < inimigos.length; i++) {
            estagioAtual = i + 1;
            boolean venceu = enfrentarMestre(inimigos[i], i);

            if (!venceu) {
                derrotaNaRoda();
                return;
            }

            vitorias++;

            // Evolui o capoeirista após cada vitória
            Capoeirista cap = (Capoeirista) jogador.getPersonagem();
            cap.evoluirTitulo(estagioAtual + 1);

            // Recupera recursos após vitória
            cap.recarregarTotalmente();

            if (i < inimigos.length - 1) {
                System.out.println("\n🎵 O berimbau chama o próximo desafiante...");
                System.out.println("   Prepare-se para o próximo mestre!");
                System.out.print("\nPressione ENTER para continuar...");
                scanner.nextLine();
            }
        }

        // CHEFÃO FINAL
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🦗 O BESOURO MANGANGÁ ENTRA NA RODA! 🦗");
        System.out.println("=".repeat(60));
        System.out.println("\n📜 A LENDA DO BESOURO:");
        System.out.println("Manoel Henrique Pereira (1895-1924), o lendário Besouro Mangangá.");
        System.out.println("Nascido em Santo Amaro da Purificação, Bahia.");
        System.out.println("Diziam que tinha o 'corpo fechado' - balas e facas não o feriam.");
        System.out.println("Filho de Ogum, protegido de Exu, seu nome correu o mundo.");
        System.out.println("\n🦗 'FECHADO! NINGUÉM ME SEGURA!'\n");

        System.out.print("Pressione ENTER para o confronto final...");
        scanner.nextLine();

        contadorAtaquesBasicos = 0; // 🆕 Reseta para o chefão
        boolean venceuBesouro = batalhaDeCapoeira(besouro, true);

        if (venceuBesouro) {
            vitoriaFinal();
        } else {
            derrotaFinal();
        }
    }

    // ============ ENFRENTAR MESTRE ============

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

        contadorAtaquesBasicos = 0; // 🆕 Reseta contador para cada novo mestre
        return batalhaDeCapoeira(mestre, false);
    }

    // ============ VERIFICAÇÃO DE PADRÃO DE ATAQUE ============

    /**
     * 🆕 Verifica se o inimigo vai esquivar do ataque básico
     * Se o jogador usar 3 ataques básicos seguidos, o inimigo esquiva 100%
     * e tem 50% de chance de contra-atacar
     */
    private boolean inimigoVaiEsquivar(int escolhaAtaque, Inimigo inimigo) {
        if (escolhaAtaque == 1) { // Ataque Básico
            contadorAtaquesBasicos++;
        } else {
            contadorAtaquesBasicos = 0; // Reseta se usar outro ataque
            return false;
        }

        // Se usou 3 ou mais ataques básicos seguidos
        if (contadorAtaquesBasicos >= 3) {
            System.out.println("\n🔄 O INIMIGO APRENDEU SEU PADRÃO!");
            System.out.println("   '" + inimigo.getNome() + "': Você só está gingando... Previsível!'");
            return true; // 100% de chance de esquivar
        }

        return false;
    }

    /**
     * 🆕 Executa a esquiva do inimigo com chance de contra-ataque
     */
    private void executarEsquivaInimigo(Inimigo inimigo, boolean ehChefao) {
        System.out.println("\n✅ CORRETO! Mas...");
        System.out.println("🔄 " + inimigo.getNome() + " ESQUIVA DO SEU ATAQUE!");
        System.out.println("   'Já conheço essa ginga... Tente algo diferente!'");

        // 50% de chance de contra-ataque
        if (random.nextDouble() < 0.5) {
            int danoContraAtaque = (int)(calcularDanoInimigo(inimigo, ehChefao) * 0.7);
            System.out.println("⚡ CONTRA-ATAQUE! " + inimigo.getNome() + " te acerta com " + danoContraAtaque + " de dano!");
            jogador.tomarDano(danoContraAtaque);

            // Mensagem temática do inimigo
            System.out.println("   💬 '" + frasesContraAtaque[random.nextInt(frasesContraAtaque.length)] + "'");
        } else {
            System.out.println("   Mas o inimigo apenas desviou sem revidar.");
        }
    }

    // ============ CÁLCULO DE DANO ESCALONADO ============

    /**
     * Calcula o dano base para exibição no menu
     */
    private int calcularDanoBase(int tipoAtaque) {
        Capoeirista cap = (Capoeirista) jogador.getPersonagem();
        int ataque = cap.getAtaque();
        int estagio = Math.max(1, estagioAtual);

        switch (tipoAtaque) {
            case 1: // Ataque Básico
                return (ataque + 5) * (estagio / 3 + 1);
            case 2: // Ataque Difícil
                return (ataque + 15) * (estagio / 3 + 1);
            case 3: // Combinação Mortal
                return (8 * 3) * (estagio / 3 + 1); // Mínimo: 3 golpes de 8
            default:
                return ataque * (estagio / 3 + 1);
        }
    }

    /**
     * Calcula o dano máximo para exibição no menu
     */
    private int calcularDanoBase(int tipoAtaque, boolean maximo) {
        if (!maximo) return calcularDanoBase(tipoAtaque);

        Capoeirista cap = (Capoeirista) jogador.getPersonagem();
        int ataque = cap.getAtaque();
        int estagio = Math.max(1, estagioAtual);

        switch (tipoAtaque) {
            case 1: // Ataque Básico
                return (ataque + 10 + 5) * (estagio / 3 + 1);
            case 2: // Ataque Difícil
                return (ataque + 15 + 20) * (estagio / 3 + 1);
            case 3: // Combinação Mortal
                return (12 * 4) * (estagio / 3 + 1); // Máximo: 4 golpes de 12
            default:
                return (ataque + 20) * (estagio / 3 + 1);
        }
    }

    /**
     * Calcula o dano real baseado no dano base do ataque,
     * na dificuldade da pergunta e no estágio atual
     */
    private int calcularDanoEscalado(int danoBase, Dificuldade dificuldade) {
        if (estagioAtual <= 0) return danoBase;

        // Multiplicador baseado na dificuldade da pergunta
        int multiplicadorDificuldade;
        switch (dificuldade) {
            case FACIL:   multiplicadorDificuldade = 1; break;
            case MEDIO:   multiplicadorDificuldade = 2; break;
            case DIFICIL: multiplicadorDificuldade = 3; break;
            default:      multiplicadorDificuldade = 1;
        }

        // Multiplicador baseado no estágio atual (1-10)
        int multiplicadorEstagio = Math.max(1, estagioAtual / 3 + 1);

        // Dano escalado
        int danoEscalado = danoBase * multiplicadorEstagio;

        // Aplica variação aleatória (85% a 115%)
        double variacao = 0.85 + (random.nextDouble() * 0.3);
        danoEscalado = (int)(danoEscalado * variacao * multiplicadorDificuldade);

        // Limites
        danoEscalado = Math.max(danoBase / 2, danoEscalado); // Mínimo: metade do dano base
        danoEscalado = Math.min(350, danoEscalado); // Cap máximo

        return danoEscalado;
    }

    // ============ LOOP DE BATALHA COM TODAS AS MECÂNICAS ============

    // Dentro do GerenciadorRodaCapoeira.java, substitua o método batalhaDeCapoeira:

    /**
     * Loop principal de batalha da Roda de Capoeira
     * Agora com temporizador integrado!
     */
    private boolean batalhaDeCapoeira(Inimigo inimigo, boolean ehChefao) {
        Capoeirista capoeirista = (Capoeirista) jogador.getPersonagem();
        int rodada = 0;

        while (jogador.vivo() && inimigo.vivo()) {
            rodada++;

            // ===== MOSTRA STATUS =====
            System.out.println("\n" + "=".repeat(60));
            System.out.println("🔄 RODADA " + rodada);
            if (ehChefao) {
                System.out.println("🦗 CONFRONTO FINAL CONTRA O BESOURO!");
            }
            System.out.println("=".repeat(60));

            capoeirista.mostrarStatus();
            System.out.println();

            if (ehChefao) {
                besouro.mostrarStatus();
            } else {
                System.out.println("👥 " + inimigo.getNome());
                inimigo.mostrarStatus();
            }

            // ===== MENU DE ATAQUE (SEM TEMPO - escolha estratégica) =====
            int escolhaAtaque = mostrarMenuAtaqueSemTempo();

            // ===== ESQUIVA NÃO TEM PERGUNTA =====
            if (escolhaAtaque == 4) {
                System.out.println("\n🌀 ESQUIVA DE CAPOEIRA!");
                boolean esquivou = capoeirista.executarEsquiva(inimigo);

                if (esquivou) {
                    System.out.println("   ✨ Você desviou com elegância!");
                    capoeirista.consumirEnergiaGinga(-5);
                    System.out.println("   🌀 +5 de Ginga bônus!");
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

            // Para ataques 1, 2 e 3: determina dificuldade
            Dificuldade dificuldade = getDificuldadePorAtaque(escolhaAtaque);

            // Obtém pergunta de capoeira
            Pergunta pergunta = bancoCapoeira.getPerguntaAleatoria(dificuldade);

            if (pergunta == null) {
                System.out.println("❌ Erro ao carregar pergunta!");
                return false;
            }

            // ===== EXIBE PERGUNTA =====
            System.out.println("\n" + "=".repeat(50));
            System.out.println("📚 PERGUNTA DE CAPOEIRA");

// Determina o tempo baseado na dificuldade
            int tempoMaximo;
            String nomeDificuldade;

            switch (dificuldade) {
                case FACIL:
                    tempoMaximo = TemporizadorResposta.TEMPO_FACIL;
                    nomeDificuldade = "⭐ FÁCIL";
                    break;
                case MEDIO:
                    tempoMaximo = TemporizadorResposta.TEMPO_MEDIO;
                    nomeDificuldade = "⭐⭐ MÉDIO";
                    break;
                case DIFICIL:
                    tempoMaximo = TemporizadorResposta.TEMPO_DIFICIL;
                    nomeDificuldade = "⭐⭐⭐ DIFÍCIL";
                    break;
                default:
                    tempoMaximo = TemporizadorResposta.TEMPO_PADRAO;
                    nomeDificuldade = "⭐ FÁCIL";
            }

// Ajuste para chefão
            if (ehChefao) {
                tempoMaximo = TemporizadorResposta.TEMPO_CHEFAO;
                System.out.println("🦗 CHEFÃO! Tempo reduzido!");
            }

            System.out.println("   Dificuldade: " + nomeDificuldade);
            System.out.println("   Estágio: " + estagioAtual);
            System.out.println("   ⏱️  Tempo: " + tempoMaximo + " segundos");
            System.out.println("=".repeat(50));

            pergunta.exibir();

// Lê a resposta COM temporizador
            String resposta;
            if (pergunta instanceof PerguntaCompletarLacuna) {
                resposta = TemporizadorResposta.lerComTempo(scanner, "\n✏️  Digite sua resposta: ", tempoMaximo);
            } else {
                resposta = TemporizadorResposta.lerComTempo(scanner, "\n✏️  Sua resposta: ", tempoMaximo);
            }

// ===== VERIFICA SE O TEMPO ESGOTOU =====
            boolean tempoEsgotado = resposta.equals("TEMPO_ESGOTADO");

            if (tempoEsgotado) {
                System.out.println("\n⏰ TEMPO ESGOTADO! Você perdeu a chance de atacar!");
                System.out.println("💢 " + inimigo.getNome() + " se aproveita da sua hesitação!");

                // ✅ MOSTRA A RESPOSTA CORRETA MESMO NO TIMEOUT
                System.out.println("📖 Resposta correta: " +
                        AvaliadorRespostas.getRespostaCorretaFormatada(pergunta));

                // O inimigo ataca com dano reduzido
                int danoInimigo = (int)(calcularDanoInimigo(inimigo, ehChefao) * 0.7);
                System.out.println("⚡ " + inimigo.getNome() + " te acerta causando " + danoInimigo + " de dano!");
                jogador.tomarDano(danoInimigo);

                vezesSemTempo++;
                contadorAtaquesBasicos = 0;

                if (!jogador.vivo()) {
                    return false;
                }

                System.out.print("\n⏭️  Pressione ENTER para próxima rodada...");
                scanner.nextLine();
                continue;
            }

// ===== AVALIA RESPOSTA =====
            boolean acertou = AvaliadorRespostas.avaliar(pergunta, resposta);

// ===== PROCESSAR RESULTADO =====
            if (acertou) {
                perguntasCertas++;

                // Verifica se o inimigo vai esquivar (padrão de ataques básicos)
                if (inimigoVaiEsquivar(escolhaAtaque, inimigo)) {
                    executarEsquivaInimigo(inimigo, ehChefao);
                    capoeirista.consumirEnergiaGinga(-5);
                    System.out.println("🌀 +5 de Ginga recuperada (reduzida)");

                } else {
                    System.out.println("\n✅ CORRETO! Execute seu golpe!");

                    int danoBase = executarAtaqueComDano(escolhaAtaque, capoeirista, inimigo);
                    int danoFinal = calcularDanoEscalado(danoBase, dificuldade);

                    System.out.println("💥 DANO: " + danoBase + " → " + danoFinal +
                            " (Estágio " + estagioAtual + ")");
                    inimigo.tomarDano(danoFinal);

                    // Recarga de Ginga
                    int recargaGinga;
                    switch (escolhaAtaque) {
                        case 1: recargaGinga = 15; break;
                        case 2: recargaGinga = 10; break;
                        case 3: recargaGinga = 5;  break;
                        default: recargaGinga = 10;
                    }

                    capoeirista.consumirEnergiaGinga(-recargaGinga);
                    System.out.println("🌀 +" + recargaGinga + " de Ginga recuperada!");
                }

            } else {
                perguntasErradas++;
                contadorAtaquesBasicos = 0;

                System.out.println("\n❌ ERRADO!");

                // ✅ CORRIGIDO: Agora mostra a resposta correta formatada
                System.out.println("   📖 Resposta correta: " +
                        AvaliadorRespostas.getRespostaCorretaFormatada(pergunta));

                // Para perguntas de lacuna, mostra comparação
                if (pergunta instanceof PerguntaCompletarLacuna) {
                    System.out.println("   ✏️  Você digitou: \"" + resposta + "\"");
                }

                // Inimigo contra-ataca
                int danoInimigo = calcularDanoInimigo(inimigo, ehChefao);
                System.out.println("💢 " + inimigo.getNome() + " contra-ataca causando " +
                        danoInimigo + " de dano!");
                jogador.tomarDano(danoInimigo);

                // 20% de chance de golpe especial
                if (random.nextDouble() < 0.2) {
                    int danoExtra = (int)(danoInimigo * 0.5);
                    System.out.println("⚡ " + inimigo.getNome() + " usa GOLPE ESPECIAL! +" +
                            danoExtra + " de dano!");
                    jogador.tomarDano(danoExtra);
                }
            }

            // ===== VERIFICA MORTES =====
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

    // ============ EXECUTAR ATAQUE COM RETORNO DE DANO ============

    /**
     * Executa o ataque e retorna o dano base causado
     */
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
                        "🌪️ ARMADA VIOLENTA!",
                        "🦵 MEIA LUA DE COMPASSO!",
                        "💫 AÚ BATIDO!",
                        "⚡ MARTELO DE NEGATIVA!",
                        "🎯 QUEIXADA!"
                };
                String movimento = movimentos[random.nextInt(movimentos.length)];
                danoBase = cap.getAtaque() + 15 + random.nextInt(20);
                System.out.println(movimento + " 💥 Dano base: " + danoBase);

                // 30% de chance de golpe preciso
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

                int golpes = 3 + random.nextInt(2); // 3 ou 4 golpes
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

    // ============ PROCESSAR VITÓRIA CONTRA INIMIGO ============

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

    // ============ MENU DE ATAQUE ATUALIZADO ============

    private int mostrarMenuAtaqueSemTempo() {
        Capoeirista cap = (Capoeirista) jogador.getPersonagem();
        int escolha = -1;

        // Calcula danos para exibição
        int danoMinBasico = calcularDanoBase(1);
        int danoMaxBasico = calcularDanoBase(1, true);
        int danoMinDificil = calcularDanoBase(2);
        int danoMaxDificil = calcularDanoBase(2, true);
        int danoMinCombinado = calcularDanoBase(3);
        int danoMaxCombinado = calcularDanoBase(3, true);

        while (escolha < 1 || escolha > 4) {
            System.out.println("\n" + "=".repeat(60));
            System.out.println("📋 ESCOLHA SEU TIPO DE ATAQUE:");
            System.out.println("=".repeat(60));

            System.out.println("\n1 🔄 GINGA BÁSICA (Ataque Normal)");
            System.out.println("   📚 Pergunta FÁCIL");
            System.out.println("   ⚔️  Dano estimado: " + danoMinBasico + " ~ " + danoMaxBasico);
            System.out.println("   🌀 Custo: 0 de Ginga | Recupera: +15 de Ginga");
            System.out.println("   ⚠️  Cuidado: 3 ataques básicos seguidos → Inimigo ESQUIVA!");

            System.out.println("\n2 💫 ATAQUE DIFÍCIL (Movimentos Acrobáticos)");
            System.out.println("   📚 Pergunta MÉDIA");
            System.out.println("   ⚔️  Dano estimado: " + danoMinDificil + " ~ " + danoMaxDificil);
            System.out.println("   🌀 Custo: 20 de Ginga | Recupera: +10 de Ginga");
            System.out.println("   🎯 30% de chance de bônus extra");

            System.out.println("\n3 🔥 COMBINAÇÃO MORTAL (Sequência de Golpes)");
            System.out.println("   📚 Pergunta DIFÍCIL");
            System.out.println("   ⚔️  Dano estimado: " + danoMinCombinado + " ~ " + danoMaxCombinado);
            System.out.println("   🌀 Custo: 40 de Ginga | Recupera: +5 de Ginga");
            System.out.println("   💥 Dano máximo possível!");

            System.out.println("\n4 🌀 ESQUIVA DE CAPOEIRA");
            System.out.println("   📚 SEM pergunta (uso livre)");
            System.out.println("   🛡️  Desvia do próximo ataque inimigo");
            System.out.println("   ⚡ 40% de chance de contra-ataque");
            System.out.println("   🔄 Esquivas restantes: " + cap.getEsquivasRestantes() + "/" + cap.getEsquivasMaximas());
            System.out.println("   🌀 Recupera: +10 de Ginga (+5 bônus)");

            System.out.println("\n" + "=".repeat(60));
            System.out.println("🌀 Ginga disponível: " + cap.getEnergiaGinga() + "/" + cap.getEnergiaMaxima());
            System.out.println("📊 Estágio atual: " + estagioAtual + " | Multiplicador de dano: ×" + (estagioAtual / 3 + 1));

            // 🆕 Aviso sobre padrão de ataques
            if (contadorAtaquesBasicos >= 2) {
                System.out.println("⚠️  ATENÇÃO: " + contadorAtaquesBasicos + " ataques básicos consecutivos!");
                System.out.println("   Mais " + (3 - contadorAtaquesBasicos) + " ataque(s) básico(s) → Inimigo ESQUIVA (100%) + 50% contra-ataque!");
            }

            System.out.println("=".repeat(60));

            System.out.print("\n🎯 Sua escolha (1-4): ");

            try {
                String entrada = scanner.nextLine().trim();
                escolha = Integer.parseInt(entrada);

                if (escolha == 2 && cap.getEnergiaGinga() < 20) {
                    System.out.println("\n❌ GINGA INSUFICIENTE! Você precisa de 20 de Ginga.");
                    System.out.println("   Ginga atual: " + cap.getEnergiaGinga());
                    System.out.println("   Escolha outra opção.");
                    escolha = -1;
                    continue;
                }

                if (escolha == 3 && cap.getEnergiaGinga() < 40) {
                    System.out.println("\n❌ GINGA INSUFICIENTE! Você precisa de 40 de Ginga.");
                    System.out.println("   Ginga atual: " + cap.getEnergiaGinga());
                    System.out.println("   Escolha outra opção.");
                    escolha = -1;
                    continue;
                }

                if (escolha == 4 && cap.getEsquivasRestantes() <= 0) {
                    System.out.println("\n❌ SEM ESQUIVAS! Você já usou todas.");
                    System.out.println("   Esquivas serão recuperadas no próximo estágio.");
                    System.out.println("   Escolha outra opção.");
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

    // ============ DIFICULDADE POR ATAQUE ============

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

    // ============ CÁLCULO DE DANO DO INIMIGO ============

    private int calcularDanoInimigo(Inimigo inimigo, boolean ehChefao) {
        int danoBase = inimigo.getAtaque();

        // Chefão na fase 3 causa dano dobrado
        if (ehChefao && besouro.getFaseAtual() == 3) {
            danoBase *= 2;
            System.out.println("⚡ FÚRIA DO BESOURO! Dano dobrado!");
        }

        // Variação de 85% a 115%
        double variacao = 0.85 + (random.nextDouble() * 0.3);
        int danoFinal = (int)(danoBase * variacao);

        return Math.max(5, danoFinal);
    }

    // ============ TELAS DE RESULTADO ============

    private void derrotaNaRoda() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💀 VOCÊ CAIU NA RODA! 💀");
        System.out.println("=".repeat(60));
        System.out.println("   😢 O berimbau silenciou...");
        System.out.println("   💔 Sua ginga foi interrompida...");
        System.out.println("   🌟 Mas a capoeira continua viva!");
        System.out.println("   💪 Treine mais e volte mais forte!\n");
        exibirResultados();
    }

    private void derrotaFinal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🦗 O BESOURO VENCEU! 🦗");
        System.out.println("=".repeat(60));
        System.out.println("   'FECHADO, NINGUÉM ME SEGURA!'");
        System.out.println("   Você lutou bravamente contra a lenda...");
        System.out.println("   O corpo fechado do Besouro prevaleceu...");
        System.out.println("   Mas sua jornada foi lendária!\n");
        exibirResultados();
    }

    private void vitoriaFinal() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("👑 VOCÊ SE TORNOU O NOVO BESOURO! 👑");
        System.out.println("=".repeat(60));
        System.out.println("   🦗 O corpo fechado agora é seu!");
        System.out.println("   🌎 Sua fama correrá o mundo!");
        System.out.println("   📜 Você é a nova lenda da capoeira!");
        System.out.println("   🎵 Seu nome será cantado nas rodas para sempre!");
        System.out.println("   👑 VOCÊ É O HERDEIRO DO BERIMBAU SAGRADO!\n");
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
        System.out.println("   🦗 Enfrentou o Besouro: " + (estagioAtual >= 10 ? "SIM 👑" : "NÃO"));

        System.out.println("\n🎵 O berimbau continuará tocando...");
        System.out.println("   A capoeira nunca morrerá!");
        System.out.println("   Axé! 🙏\n");
    }
}