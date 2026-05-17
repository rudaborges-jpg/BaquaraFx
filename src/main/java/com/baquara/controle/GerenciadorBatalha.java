package com.baquara.controle;

import com.baquara.modelo.*;
import com.baquara.modelo.Pergunta.Dificuldade;
import com.baquara.dados.BancoPerguntas;
import com.baquara.habilidades.*;
import java.util.*;

public class GerenciadorBatalha {
    private Jogador jogador;
    private Inimigo inimigoAtual;
    private BancoPerguntas bancoPerguntas;
    private GerenciadorRotas gerenciadorRotas;
    private Rota rotaAtual;
    private int estagioIndex;
    private int rodadaAtual;
    private int pontuacaoTotal;
    private Scanner scanner;
    private Random random;

    private Set<Integer> perguntasUsadas;
    private Map<Dificuldade, Integer> resetsPorDificuldade;

    private int perguntasCertas;
    private int perguntasErradas;
    private int habilidadesUsadas;
    private int danoTotalCausado;
    private int danoTotalRecebido;

    public GerenciadorBatalha(Jogador jogador, BancoPerguntas bancoPerguntas) {
        this.jogador = jogador;
        this.bancoPerguntas = bancoPerguntas;
        this.gerenciadorRotas = new GerenciadorRotas();
        this.rodadaAtual = 0;
        this.pontuacaoTotal = 0;
        this.scanner = new Scanner(System.in);
        this.random = new Random();

        this.perguntasUsadas = new HashSet<>();
        this.resetsPorDificuldade = new HashMap<>();
        for (Dificuldade diff : Dificuldade.values()) {
            resetsPorDificuldade.put(diff, 0);
        }

        // Inicializa estatísticas
        this.perguntasCertas = 0;
        this.perguntasErradas = 0;
        this.habilidadesUsadas = 0;
        this.danoTotalCausado = 0;
        this.danoTotalRecebido = 0;

        inicializarHabilidades();
    }

    // ========= INICIALIZAÇÃO DE HABILIDADES (SEM COOLDOWN) =========

    private void inicializarHabilidades() {
        Personagem p = jogador.getPersonagem();

        if (p instanceof Paladino) {
            p.setHabilidade(new HabilidadeCura(p));
        } else if (p instanceof Guerreiro) {
            p.setHabilidade(new HabilidadeDanoExtra(p));
        } else if (p instanceof Cacadora) {
            p.setHabilidade(new HabilidadeCritico(p));
        } else if (p instanceof Sabio) {
            p.setHabilidade(new HabilidadePoderMagico(p));
        } else if (p instanceof Arcanista) {
            p.setHabilidade(new HabilidadeDestruicaoTotal(p));
        }

        if (p.getHabilidade() != null) {
            AtributoEspecial attr = (AtributoEspecial) p;
            System.out.println("\n🌟 " + p.getNome() + " possui a habilidade: " +
                    p.getHabilidade().getNome());
            System.out.println("   " + p.getHabilidade().getDescricao());
            System.out.println("   💫 Custo: " + getCustoHabilidade(p) + " de " + attr.getNomeAtributo());
            System.out.println("   ✨ Recupera " + getRecuperacaoHabilidade(p) + " de " + attr.getNomeAtributo() + " por acerto!");
        }
    }

    // Métodos auxiliares para custo e recuperação
    private int getCustoHabilidade(Personagem p) {
        if (p instanceof Paladino) return 30;
        if (p instanceof Guerreiro) return 40;
        if (p instanceof Cacadora) return 35;
        if (p instanceof Sabio) return 30;
        if (p instanceof Arcanista) return 50;
        return 30;
    }

    private int getRecuperacaoHabilidade(Personagem p) {
        if (p instanceof Paladino) return 5;
        if (p instanceof Guerreiro) return 8;
        if (p instanceof Cacadora) return 5;
        if (p instanceof Sabio) return 10;
        if (p instanceof Arcanista) return 3;
        return 5;
    }

    // ========= INÍCIO DO JOGO =========

    public void iniciarRota() {
        PerTipo tipoPersonagem = jogador.getPersonagem().getTipo();
        rotaAtual = gerenciadorRotas.getRota(tipoPersonagem);
        gerenciadorRotas.setRotaAtual(rotaAtual);
        estagioIndex = 0;

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🗺️ VOCÊ ESCOLHEU A ROTA: " + rotaAtual.getNomeRota());
        System.out.println("=".repeat(60));
        rotaAtual.mostrarInfo();

        System.out.print("\nPressione ENTER para começar sua jornada...");
        scanner.nextLine();

        iniciarJogo();
    }

    private void iniciarJogo() {
        while (estagioIndex < rotaAtual.getTotalEstagios() && jogador.vivo()) {
            Estagio estagio = rotaAtual.getEstagios().get(estagioIndex);
            estagio.mostrarInfo();

            // Cria inimigo para o estágio
            if (estagio.ehChefao()) {
                inimigoAtual = estagio.getChefao();
                System.out.println("\n⚠️ UM CHEFÃO APARECEU! ⚠️");
                System.out.println("👑 " + inimigoAtual.getNome() + " - Vida: " + inimigoAtual.getVida());
            } else {
                criarInimigoParaEstagio(estagio);
            }

            boolean completo = realizarBatalha(estagio);

            if (!completo) {
                exibirTelaDerrota();
                return;
            }

            // Recarrega atributos do personagem após estágio
            Personagem p = jogador.getPersonagem();
            p.recarregarPorEstagio(estagio.getNumero());

            // ⭐ RECUPERA UM POUCO DO ATRIBUTO AO VENCER ESTÁGIO
            if (p.getHabilidade() != null) {
                p.getHabilidade().recarregarAposAcerto();
                p.getHabilidade().recarregarAposAcerto(); // Recupera dobrado
            }

            // Cura parcial após estágio
            p.curar(30);

            // ⭐ REMOVIDO: resetarCooldownHabilidade()

            estagioIndex++;

            if (estagioIndex < rotaAtual.getTotalEstagios() && jogador.vivo()) {
                System.out.println("\n✨ Você avança para o próximo estágio! +30 de vida! ✨");
                System.out.println("✨ +" + getRecuperacaoHabilidade(p) * 2 + " de " +
                        ((AtributoEspecial) p).getNomeAtributo() + " recuperado!");
                System.out.print("\nPressione ENTER para continuar...");
                scanner.nextLine();
            }
        }

        // Verifica se completou a rota
        if (estagioIndex >= rotaAtual.getTotalEstagios() && jogador.vivo()) {
            exibirTelaVitoria();
        }
    }

    // ========= CRIAÇÃO DE INIMIGOS =========

    private void criarInimigoParaEstagio(Estagio estagio) {
        int vidaBase = 60 + (estagio.getNumero() * 100);
        int ataqueBase = 25 + (estagio.getNumero() * 5);
        int defesaBase = 5 + (estagio.getNumero() * 3);
        String nome = "🗡️ Guardião do " + estagio.getNome() + " 🗡️";

        this.inimigoAtual = new Inimigo(nome, vidaBase, ataqueBase, defesaBase, estagio.getNumero());
    }

    // ========= GERENCIAMENTO DE PERGUNTAS =========

    private void resetarPerguntasPorDificuldade(Dificuldade dificuldade) {
        perguntasUsadas.clear();
        int totalResets = resetsPorDificuldade.get(dificuldade) + 1;
        resetsPorDificuldade.put(dificuldade, totalResets);
        System.out.println("🔄 Banco de perguntas reiniciado para dificuldade " +
                dificuldade.getNome() + " (reset #" + totalResets + ")");
    }

    private Pergunta getPerguntaSemRepeticao(PerTipo tipo, Dificuldade dificuldade, int estagioNumero) {
        List<Pergunta> todasPerguntas = bancoPerguntas.getPerguntasPorDificuldade(tipo, dificuldade, estagioNumero);

        if (todasPerguntas == null || todasPerguntas.isEmpty()) {
            System.out.println("⚠️ Nenhuma pergunta disponível para " + dificuldade.getNome());
            return null;
        }

        // Filtra perguntas não usadas
        List<Pergunta> perguntasNaoUsadas = new ArrayList<>();
        for (Pergunta p : todasPerguntas) {
            if (!perguntasUsadas.contains(p.getId())) {
                perguntasNaoUsadas.add(p);
            }
        }

        // Se todas foram usadas, reinicia o ciclo
        if (perguntasNaoUsadas.isEmpty()) {
            System.out.println("\n🔄 Todas as " + todasPerguntas.size() +
                    " perguntas de dificuldade " + dificuldade.getNome() +
                    " já foram usadas na jornada!");
            System.out.println("   Reiniciando ciclo para esta dificuldade...");
            resetarPerguntasPorDificuldade(dificuldade);
            perguntasNaoUsadas = todasPerguntas;
        }

        // Escolhe pergunta aleatória
        Pergunta escolhida = perguntasNaoUsadas.get(random.nextInt(perguntasNaoUsadas.size()));
        perguntasUsadas.add(escolhida.getId());

        // Mostra estatísticas de uso
        int usadasNestaDificuldade = 0;
        for (Pergunta p : todasPerguntas) {
            if (perguntasUsadas.contains(p.getId())) {
                usadasNestaDificuldade++;
            }
        }
        int restantes = todasPerguntas.size() - usadasNestaDificuldade;
        System.out.println("📚 " + dificuldade.getNome() + ": " + usadasNestaDificuldade + "/" +
                todasPerguntas.size() + " usadas | Restantes: " + restantes);

        return escolhida;
    }

    // ========= LOOP DE BATALHA =========

    private boolean realizarBatalha(Estagio estagio) {
        while (jogador.vivo() && inimigoAtual.vivo()) {
            rodadaAtual++;
            System.out.println("\n" + "=".repeat(60));
            System.out.println("⚔️ RODADA " + rodadaAtual);
            if (estagio.ehChefao()) {
                System.out.println("👑 BATALHA CONTRA O CHEFÃO! 👑");
            }
            System.out.println("=".repeat(60));

            // Mostra status
            jogador.mostrarStatus();
            inimigoAtual.mostrarStatus();

            // Mostra status da habilidade (sem cooldown)
            Personagem personagem = jogador.getPersonagem();
            if (personagem.getHabilidade() != null) {
                AtributoEspecial attr = (AtributoEspecial) personagem;
                if (personagem.getHabilidade().podeUsar()) {
                    System.out.println("✨ HABILIDADE ESPECIAL PRONTA! ✨");
                    System.out.println("   💫 Custo: " + getCustoHabilidade(personagem) + " de " + attr.getNomeAtributo());
                    System.out.println("   📊 " + attr.getNomeAtributo() + ": " + attr.getValorAtual() + "/" + attr.getValorMaximo());
                } else {
                    System.out.println("❌ " + attr.getNomeAtributo() + " insuficiente! (Precisa: " +
                            getCustoHabilidade(personagem) + ")");
                    System.out.println("   📊 " + attr.getNomeAtributo() + ": " + attr.getValorAtual() + "/" + attr.getValorMaximo());
                }
            }

            // Menu de ações
            int escolha = mostrarMenuAcao();

            switch (escolha) {
                case 1:
                    realizarRodadaPergunta(estagio);
                    break;
                case 2:
                    if (personagem.getHabilidade() != null && personagem.getHabilidade().podeUsar()) {
                        realizarRodadaHabilidade(estagio);
                    } else if (personagem.getHabilidade() == null) {
                        System.out.println("\n❌ Seu personagem não possui habilidade especial!");
                        System.out.println("   Voltando ao ataque normal...");
                        realizarRodadaPergunta(estagio);
                    } else {
                        System.out.println("\n❌ " + ((AtributoEspecial) personagem).getNomeAtributo() +
                                " insuficiente! Precisa de " + getCustoHabilidade(personagem) + ".");
                        System.out.println("   Voltando ao ataque normal...");
                        realizarRodadaPergunta(estagio);
                    }
                    break;
            }

            // ⭐ REMOVIDO: Atualiza cooldown da habilidade (não existe mais)
            // personagem.reduzirCooldownHabilidade();

            // Verifica vitória
            if (!inimigoAtual.vivo()) {
                System.out.println("\n🎉 VITÓRIA! Estágio " + estagio.getNumero() + " concluído! 🎉");

                int experienciaInimigo = calcularExperienciaPorInimigo(estagio);
                jogador.getPersonagem().addExperiencia(experienciaInimigo);
                System.out.println("📚 +" + experienciaInimigo + " EXP por derrotar o inimigo!");

                int bonus = estagio.getNumero() * 10;
                pontuacaoTotal += bonus;
                System.out.println("🏆 Bônus de estágio: +" + bonus + " pontos!");
                return true;
            }

            // Verifica derrota
            if (!jogador.vivo()) {
                System.out.println("\n💀 Você foi derrotado! 💀");
                return false;
            }
        }
        return false;
    }

    // ========= MENU DE AÇÕES =========

    private int mostrarMenuAcao() {
        Personagem personagem = jogador.getPersonagem();
        int escolha = -1;

        while (escolha != 1 && escolha != 2) {
            System.out.println("\n📋 O QUE VOCÊ DESEJA FAZER?");
            System.out.println("=".repeat(40));
            System.out.println("1 🗡️ Responder Pergunta (Ataque Normal)");

            if (personagem.getHabilidade() != null) {
                AtributoEspecial attr = (AtributoEspecial) personagem;
                String status = personagem.getHabilidade().podeUsar() ?
                        "✅ PRONTA (" + attr.getValorAtual() + "/" + attr.getValorMaximo() + ")" :
                        "❌ RECURSO INSUFICIENTE";
                System.out.println("2 🌟 Usar Habilidade Especial - " +
                        personagem.getHabilidade().getNome() + " [" + status + "]");
                System.out.println("   💫 Custo: " + getCustoHabilidade(personagem) + " de " + attr.getNomeAtributo());
            } else {
                System.out.println("2 ❌ Sem habilidade especial");
            }
            System.out.println("=".repeat(40));
            System.out.print("Sua escolha (1 ou 2): ");

            try {
                escolha = scanner.nextInt();
                scanner.nextLine();

                if (escolha != 1 && escolha != 2) {
                    System.out.println("\n❌ Opção inválida! Digite 1 ou 2.");
                }
            } catch (InputMismatchException e) {
                System.out.println("\n❌ Entrada inválida! Digite um número (1 ou 2).");
                scanner.nextLine();
                escolha = -1;
            }
        }
        return escolha;
    }

    // ========= RODADA DE PERGUNTA =========

    private void realizarRodadaPergunta(Estagio estagio) {
        // Determina dificuldade baseada no estágio
        Dificuldade dificuldade;
        if (estagio.getDificuldade() <= 3) {
            dificuldade = Dificuldade.FACIL;
        } else if (estagio.getDificuldade() <= 7) {
            dificuldade = Dificuldade.MEDIO;
        } else {
            dificuldade = Dificuldade.DIFICIL;
        }

        // Obtém pergunta
        Pergunta pergunta = getPerguntaSemRepeticao(
                jogador.getPersonagem().getTipo(), dificuldade, estagio.getNumero());

        if (pergunta == null) {
            pergunta = bancoPerguntas.getPerguntaAleatoriaParaPersonagem(
                    jogador.getPersonagem().getTipo(), estagio.getNumero());
        }

        if (pergunta == null) {
            System.out.println("\n❌ ERRO: Nenhuma pergunta disponível!");
            return;
        }

        // Exibe pergunta
        pergunta.exibir();

        // Obtém resposta
        String resposta;
        if (pergunta instanceof PerguntaCompletarLacuna) {
            System.out.print("\nDigite sua resposta: ");
            resposta = scanner.nextLine().trim();
        } else {
            System.out.print("\nSua resposta: ");
            resposta = scanner.nextLine().toUpperCase();
        }

        // Avalia resposta
        boolean correta = AvaliadorRespostas.avaliar(pergunta, resposta);

        if (correta) {
            perguntasCertas++;
            System.out.println("\n✅ CORRETO!");

            // ⭐ RECUPERA ATRIBUTO ESPECIAL AO ACERTAR PERGUNTA
            Personagem p = jogador.getPersonagem();
            if (p.getHabilidade() != null) {
                p.getHabilidade().recarregarAposAcerto();
                System.out.println("✨ +" + getRecuperacaoHabilidade(p) + " de " +
                        ((AtributoEspecial) p).getNomeAtributo() + " recuperado!");
            }

            // Calcula e aplica dano
            int dano = calcularDano(pergunta.getDificuldade(), estagio);
            inimigoAtual.tomarDano(dano);
            danoTotalCausado += dano;

            // Ganha experiência
            int experienciaGanha = (estagio.getDificuldade() * 5);
            jogador.getPersonagem().addExperiencia(experienciaGanha);
            System.out.println("📚 +" + experienciaGanha + " de experiência!");

            // Ganha pontos
            int pontos = calcularPontos(pergunta.getDificuldade(), estagio);
            pontuacaoTotal += pontos;
            jogador.addPontuacao(pontos);
            System.out.println("🏆 +" + pontos + " pontos! Total: " + pontuacaoTotal);

        } else {
            perguntasErradas++;
            System.out.println("\n❌ ERRADO!");
            System.out.println("Resposta correta: " + pergunta.getRespostaCorreta());

            // Inimigo contra-ataca
            int danoInimigo = calcularDanoInimigo();
            jogador.tomarDano(danoInimigo);
            danoTotalRecebido += danoInimigo;
            System.out.println("⚠️ " + inimigoAtual.getNome() + " contra-ataca causando " + danoInimigo + " de dano!");
        }
    }

    // ========= RODADA DE HABILIDADE =========

    private void realizarRodadaHabilidade(Estagio estagio) {
        Personagem personagem = jogador.getPersonagem();

        System.out.println("\n🌟 USANDO HABILIDADE ESPECIAL! 🌟");
        System.out.println("💪 " + personagem.getHabilidade().getNome());
        System.out.println("📖 " + personagem.getHabilidade().getDescricao());

        System.out.print("\nDeseja realmente usar a habilidade? (S/N): ");
        String confirmacao = scanner.nextLine().toUpperCase();

        if (!confirmacao.equals("S")) {
            System.out.println("\n❌ Uso cancelado! Voltando ao combate normal.");
            realizarRodadaPergunta(estagio);
            return;
        }

        AtributoEspecial attr = (AtributoEspecial) personagem;
        System.out.println("\n💫 Custo: " + getCustoHabilidade(personagem) + " de " + attr.getNomeAtributo());
        System.out.println("📊 " + attr.getNomeAtributo() + " atual: " + attr.getValorAtual() + "/" + attr.getValorMaximo());

        habilidadesUsadas++;
        int dano = personagem.usarHabilidade(inimigoAtual);
        danoTotalCausado += dano;

        if (dano > 0) {
            System.out.println("\n💥 " + personagem.getNome() + " causa " + dano +
                    " de dano com sua habilidade especial!");
            System.out.println("📊 " + attr.getNomeAtributo() + " restante: " + attr.getValorAtual() + "/" + attr.getValorMaximo());
        }
    }

    private int calcularDano(Dificuldade diff, Estagio estagio) {
        Personagem personagem = jogador.getPersonagem();

        int danoBase = personagem.getAtaque();

        int multiplicadorDificuldade;
        switch (diff) {
            case FACIL:
                multiplicadorDificuldade = 1;
                break;
            case MEDIO:
                multiplicadorDificuldade = 2;
                break;
            case DIFICIL:
                multiplicadorDificuldade = 3;
                break;
            default:
                multiplicadorDificuldade = 1;
        }

        int multiplicadorEstagio = estagio.getDificuldade() / 2;
        if (multiplicadorEstagio < 1) multiplicadorEstagio = 1;

        int dano = danoBase * multiplicadorDificuldade * multiplicadorEstagio;

        double variacao = 0.85 + (random.nextDouble() * 0.3);
        dano = (int)(dano * variacao);

        dano = Math.max(10, dano);
        dano = Math.min(60, dano);

        return dano;
    }

    private int calcularDanoInimigo() {
        int danoBase = inimigoAtual.getAtaque();

        int multiplicador = (estagioIndex + 1);

        if (rotaAtual.getEstagios().get(estagioIndex).ehChefao()) {
            multiplicador *= 2;
        }

        int dano = danoBase + (multiplicador * 3);

        double variacao = 0.85 + (random.nextDouble() * 0.3);
        dano = (int)(dano * variacao);

        return Math.max(8, dano);
    }

    private int calcularPontos(Dificuldade diff, Estagio estagio) {
        return diff.getDanoBase() * 5 * estagio.getDificuldade();
    }

    private int calcularExperienciaPorInimigo(Estagio estagio) {
        int experienciaBase = 50;
        int multiplicador = estagio.getNumero();

        if (estagio.ehChefao()) {
            multiplicador *= 2;
        }

        int experiencia = experienciaBase * multiplicador;

        double variacao = 0.9 + (random.nextDouble() * 0.2);
        experiencia = (int)(experiencia * variacao);

        return Math.max(20, experiencia);
    }

    private void exibirTelaDerrota() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("💀💀💀 GAME OVER 💀💀💀");
        System.out.println("=".repeat(60));
        exibirEstatisticasFinais();
    }

    private void exibirTelaVitoria() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("🏆🏆🏆 PARABÉNS! VITÓRIA TOTAL! 🏆🏆🏆");
        System.out.println("=".repeat(60));
        System.out.println("\n🎉 Você completou a rota: " + rotaAtual.getNomeRota());
        System.out.println("👤 Personagem: " + jogador.getPersonagem().getNome());
        System.out.println("⭐ Nível final: " + jogador.getPersonagem().getNivel());
        exibirEstatisticasFinais();
    }

    private void exibirEstatisticasFinais() {
        System.out.println("\n📊 ESTATÍSTICAS DA PARTIDA");
        System.out.println("=".repeat(40));
        System.out.println("🏆 Pontuação Total: " + pontuacaoTotal);
        System.out.println("📊 Rodadas Jogadas: " + rodadaAtual);
        System.out.println("✅ Perguntas Certas: " + perguntasCertas);
        System.out.println("❌ Perguntas Erradas: " + perguntasErradas);

        if (perguntasCertas + perguntasErradas > 0) {
            double aproveitamento = (double) perguntasCertas / (perguntasCertas + perguntasErradas) * 100;
            System.out.println("📈 Aproveitamento: " + String.format("%.1f", aproveitamento) + "%");
        }

        System.out.println("🌟 Habilidades Usadas: " + habilidadesUsadas);
        System.out.println("⚔️ Dano Total Causado: " + danoTotalCausado);
        System.out.println("💔 Dano Total Recebido: " + danoTotalRecebido);

        Personagem p = jogador.getPersonagem();
        System.out.println("\n👤 PERSONAGEM FINAL");
        System.out.println("=".repeat(40));
        p.mostrarStatus();

        System.out.println("\n📚 PERGUNTAS ÚNICAS USADAS: " + perguntasUsadas.size());
        for (Dificuldade diff : Dificuldade.values()) {
            int resets = resetsPorDificuldade.get(diff);
            if (resets > 0) {
                System.out.println("   🔄 " + diff.getNome() + ": " + resets + " reset(s)");
            }
        }

        System.out.println("\n" + "=".repeat(60));
        System.out.println("🙏 Obrigado por jogar BAQUARA: Batalha do Saber!");
        System.out.println("=".repeat(60));
    }
}