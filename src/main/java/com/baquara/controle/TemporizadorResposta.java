package com.baquara.controle;

import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Temporizador de Respostas - Versão Corrigida DEFINITIVA
 *
 * ✅ Timer NUNCA apaga a digitação do usuário
 * ✅ Timer em linha separada que não interfere com a leitura
 * ✅ Usa threads separadas com AtomicReference
 *
 * @author Grupo Baquara
 * @version 7.0 - Corrigida definitivamente
 */
public class TemporizadorResposta {

    public static final int TEMPO_FACIL = 15;
    public static final int TEMPO_MEDIO = 12;
    public static final int TEMPO_DIFICIL = 10;
    public static final int TEMPO_CHEFAO = 8;
    public static final int TEMPO_PADRAO = 10;

    /**
     * Lê uma resposta do usuário com tempo limite
     * Agora sem apagar a digitação do usuário!
     */
    public static String lerComTempo(Scanner scanner, String mensagem, int tempoMaximo) {
        if (tempoMaximo <= 0) tempoMaximo = TEMPO_PADRAO;
        if (tempoMaximo > 60) tempoMaximo = 60;

        final int tempoMax = tempoMaximo;
        final AtomicReference<String> respostaAtomic = new AtomicReference<>(null);
        final AtomicBoolean tempoEsgotou = new AtomicBoolean(false);

        // Exibe a mensagem
        System.out.print(mensagem);
        System.out.flush();

        // Guarda o início do tempo
        final long tempoInicio = System.currentTimeMillis();
        final long tempoLimite = tempoInicio + (tempoMax * 1000L);

        // Thread que fica monitorando o tempo e exibindo timer em linha separada
        Thread timerThread = new Thread(() -> {
            try {
                int ultimoSegundo = tempoMax;
                int linhaAtual = 0;

                // Posiciona o cursor para a linha do timer (abaixo da linha de digitação)
                System.out.println(); // Linha em branco para o timer
                linhaAtual++;

                while (System.currentTimeMillis() < tempoLimite) {
                    long agora = System.currentTimeMillis();
                    long restanteMs = tempoLimite - agora;
                    int segundos = (int) Math.max(0, Math.ceil(restanteMs / 1000.0));

                    if (segundos != ultimoSegundo) {
                        ultimoSegundo = segundos;

                        // Sobe para a linha do timer e atualiza
                        System.out.print("\033[" + linhaAtual + "A"); // Sobe para a linha do timer
                        System.out.print("\033[2K"); // Limpa a linha do timer

                        String timerMsg = construirBarraProgresso(segundos, tempoMax);
                        System.out.print(timerMsg);
                        System.out.println();

                        // Volta para a linha de digitação
                        System.out.print("\033[" + (linhaAtual + 1) + "A");
                        System.out.flush();
                    }

                    Thread.sleep(200);
                }

                // Tempo esgotado - marca sem apagar a digitação
                tempoEsgotou.set(true);

                // Exibe mensagem de tempo esgotado na linha do timer
                System.out.print("\033[" + linhaAtual + "A");
                System.out.print("\033[2K");
                System.out.println("⏰ TEMPO ESGOTADO! (" + tempoMax + " segundos)");
                System.out.flush();

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        timerThread.setDaemon(true);
        timerThread.start();

        // Thread para leitura da resposta
        Thread leituraThread = new Thread(() -> {
            try {
                String resposta = scanner.nextLine().trim();
                if (!resposta.isEmpty()) {
                    respostaAtomic.set(resposta);
                }
            } catch (Exception e) {
                // Scanner interrompido
            }
        });

        leituraThread.setDaemon(true);
        leituraThread.start();

        // Aguarda resposta ou tempo
        try {
            long tempoRestante = tempoLimite - System.currentTimeMillis();
            while (tempoRestante > 0 && respostaAtomic.get() == null && !tempoEsgotou.get()) {
                Thread.sleep(100);
                tempoRestante = tempoLimite - System.currentTimeMillis();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Interrompe o timer
        timerThread.interrupt();

        // Pequena pausa para o timer terminar
        try { Thread.sleep(100); } catch (InterruptedException e) {}

        // Limpa a linha do timer (opcional)
        System.out.print("\033[1A"); // Sobe uma linha
        System.out.print("\033[2K"); // Limpa
        System.out.print("\033[1A"); // Sobe para a linha original
        System.out.flush();

        // Verifica resultado
        if (tempoEsgotou.get() || respostaAtomic.get() == null) {
            System.out.println(); // Nova linha
            return "TEMPO_ESGOTADO";
        }

        long tempoGasto = (System.currentTimeMillis() - tempoInicio) / 1000;
        System.out.println("✅ Resposta registrada em " + tempoGasto + "s");

        return respostaAtomic.get();
    }

    /**
     * Constrói a barra de progresso visual
     */
    private static String construirBarraProgresso(int segundosRestantes, int tempoTotal) {
        StringBuilder sb = new StringBuilder();
        sb.append("⏱️  ");

        // Alerta visual
        if (segundosRestantes <= 3) {
            sb.append("⚠️⚠️⚠️ ");
        } else if (segundosRestantes <= 5) {
            sb.append("⚠️ ");
        }

        sb.append("[");

        int barraLargura = 15;
        double proporcao = (double) segundosRestantes / tempoTotal;
        int preenchido = (int) (proporcao * barraLargura);

        for (int i = 0; i < barraLargura; i++) {
            if (i < preenchido) {
                sb.append("▓");
            } else {
                sb.append("░");
            }
        }

        sb.append("] ");
        sb.append(segundosRestantes);
        sb.append("s");

        return sb.toString();
    }

    /**
     * Versão com tempo padrão
     */
    public static String lerComTempo(Scanner scanner, String mensagem) {
        return lerComTempo(scanner, mensagem, TEMPO_PADRAO);
    }

    /**
     * Mostra contagem regressiva antes de começar
     */
    public static void mostrarContagemRegressiva() {
        System.out.print("\n⏱️  Preparando... ");
        for (int i = 3; i > 0; i--) {
            try {
                System.out.print(i + "... ");
                System.out.flush();
                Thread.sleep(500);
            } catch (InterruptedException e) {
                break;
            }
        }
        System.out.println("JÁ! 🎯");
    }
}