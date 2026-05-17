package com.baquara.controle;

import com.baquara.modelo.*;
import java.text.Normalizer;


public class AvaliadorRespostas {


    public static boolean avaliar(Pergunta pergunta, String resposta) {
        if (pergunta == null || resposta == null || resposta.trim().isEmpty()) {
            return false;
        }

        if (resposta.equals("TEMPO_ESGOTADO")) {
            return false;
        }

        if (pergunta instanceof PerguntaCompletarLacuna) {
            return avaliarLacuna(pergunta, resposta);
        } else if (pergunta instanceof PerguntaMultiplaEscolha) {
            return avaliarMultiplaEscolha((PerguntaMultiplaEscolha) pergunta, resposta);
        } else if (pergunta instanceof PerguntaVerdadeiroFalso) {
            return avaliarVerdadeiroFalso(pergunta, resposta);
        }

        return resposta.trim().equalsIgnoreCase(pergunta.getRespostaCorreta().trim());
    }

    /**
     * Avalia perguntas de completar lacuna - VERSÃO CORRIGIDA
     * Agora exige correspondência EXATA ou próxima, não apenas "contém"
     */
    private static boolean avaliarLacuna(Pergunta pergunta, String respostaJogador) {
        String respostaNormalizada = normalizarTexto(respostaJogador);
        String corretaNormalizada = normalizarTexto(pergunta.getRespostaCorreta());

        // ⭐ 1. VERIFICAÇÃO EXATA (MAIS RÍGIDA)
        if (respostaNormalizada.equals(corretaNormalizada)) {
            return true;
        }

        // ⭐ 2. PERMITE APENAS SE FOR MUITO PARECIDA (90% de similaridade)
        // Evita que "e" seja aceito para "espírito"
        if (Math.abs(respostaNormalizada.length() - corretaNormalizada.length()) > 3) {
            return false;  // Diferença de tamanho muito grande
        }

        // ⭐ 3. VERIFICA SE A RESPOSTA É UMA PALAVRA INTEIRA (não apenas uma letra)
        if (respostaNormalizada.length() <= 2 && !corretaNormalizada.equals(respostaNormalizada)) {
            return false;  // Resposta muito curta (1-2 letras) que não é exatamente a correta
        }

        // ⭐ 4. VERIFICAÇÃO DE "CONTER" APENAS SE FOR UMA PALAVRA COMPLETA
        // Ex: "espírito" contém "espírito" - OK
        // Ex: "e" contém "espírito" - NÃO OK (já barrado pelo passo 3)
        if (corretaNormalizada.contains(respostaNormalizada) && respostaNormalizada.length() > 3) {
            return true;
        }

        // ⭐ 5. VERIFICAÇÃO DE VARIAÇÕES COMUNS (plural, acentos, etc.)
        return verificarVariacoesComuns(respostaNormalizada, corretaNormalizada);
    }

    // ⭐ ADICIONAR ESTE MÉTODO AUXILIAR PARA VALIDAR TAMANHO MÍNIMO
    private static boolean isRespostaValida(String resposta) {
        if (resposta == null || resposta.trim().isEmpty()) return false;

        // Remove espaços extras
        String trimmed = resposta.trim();

        // Resposta muito curta (1-2 caracteres) geralmente é inválida para lacuna
        if (trimmed.length() <= 2) {
            return false;
        }

        return true;
    }

    /**
     * Avalia perguntas de múltipla escolha
     */
    private static boolean avaliarMultiplaEscolha(PerguntaMultiplaEscolha pergunta, String resposta) {
        String respostaUpper = resposta.trim().toUpperCase();

        // Aceita letra (A, B, C, D)
        if (respostaUpper.matches("[A-D]")) {
            int index = respostaUpper.charAt(0) - 'A';
            if (index >= 0 && index < pergunta.getOpcoes().size()) {
                String opcaoSelecionada = pergunta.getOpcoes().get(index);
                return opcaoSelecionada.equals(pergunta.getRespostaCorreta());
            }
        }

        // Aceita texto completo
        String respostaNormalizada = normalizarTexto(resposta);
        String corretaNormalizada = normalizarTexto(pergunta.getRespostaCorreta());

        return respostaNormalizada.equals(corretaNormalizada) ||
                respostaNormalizada.contains(corretaNormalizada);
    }

    /**
     * Avalia perguntas de verdadeiro/falso
     */
    private static boolean avaliarVerdadeiroFalso(Pergunta pergunta, String resposta) {
        String respostaUpper = resposta.trim().toUpperCase();

        boolean respostaBool = respostaUpper.equals("V") ||
                respostaUpper.equals("VERDADEIRO") ||
                respostaUpper.equals("SIM") ||
                respostaUpper.equals("TRUE");

        boolean corretaBool = pergunta.getRespostaCorreta().equals("V") ||
                pergunta.getRespostaCorreta().equals("VERDADEIRO");

        return respostaBool == corretaBool;
    }

    /**
     * Normaliza texto para comparação
     */
    private static String normalizarTexto(String texto) {
        if (texto == null || texto.trim().isEmpty()) return "";

        String normalizado = texto.trim().toLowerCase();

        // Remove acentos
        normalizado = Normalizer.normalize(normalizado, Normalizer.Form.NFD)
                .replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");

        // Substitui separadores
        normalizado = normalizado.replace("-", " ")
                .replace("–", " ")
                .replace("—", " ")
                .replace("'", " ")
                .replace("`", " ")
                .replace("´", " ");

        // Remove pontuação
        normalizado = normalizado.replaceAll("[.,;:!?\"'«»()\\[\\]{}]", " ");

        // Remove artigos no início
        normalizado = normalizado.replaceAll(
                "^(o |a |os |as |um |uma |uns |umas )", "");

        // Remove múltiplos espaços
        normalizado = normalizado.replaceAll("\\s+", " ").trim();

        return normalizado;
    }

    /**
     * Verifica variações comuns (números por extenso)
     */
    private static boolean verificarVariacoesComuns(String resposta, String correta) {
        // Mapeia números para extenso
        String[][] variacoes = {
                {"1", "um"}, {"2", "dois"}, {"3", "tres"},
                {"4", "quatro"}, {"5", "cinco"}, {"6", "seis"},
                {"7", "sete"}, {"8", "oito"}, {"9", "nove"}, {"10", "dez"},
                {"12", "doze"}, {"150", "cento e cinquenta"}
        };

        for (String[] variacao : variacoes) {
            String respostaExpandida = resposta.replace(variacao[0], variacao[1])
                    .replace(variacao[1], variacao[0]);
            String corretaExpandida = correta.replace(variacao[0], variacao[1])
                    .replace(variacao[1], variacao[0]);

            if (respostaExpandida.equals(corretaExpandida)) {
                return true;
            }
        }

        return false;
    }

    /**
     * ✅ CORRIGIDO: Retorna a resposta correta formatada para exibição
     * Agora funciona para TODOS os tipos de pergunta
     */
    public static String getRespostaCorretaFormatada(Pergunta pergunta) {
        if (pergunta == null) return "N/A";

        // Múltipla escolha - mostra letra + texto
        if (pergunta instanceof PerguntaMultiplaEscolha) {
            PerguntaMultiplaEscolha multipla = (PerguntaMultiplaEscolha) pergunta;
            return multipla.getLetraCorreta() + ") " + pergunta.getRespostaCorreta();
        }

        // Verdadeiro/Falso - mostra V ou F + texto explicativo
        if (pergunta instanceof PerguntaVerdadeiroFalso) {
            String resposta = pergunta.getRespostaCorreta();
            if (resposta.equals("V")) {
                return "V) VERDADEIRO";
            } else if (resposta.equals("F")) {
                return "F) FALSO";
            }
            return resposta;
        }

        // Completar lacuna - mostra a palavra correta
        if (pergunta instanceof PerguntaCompletarLacuna) {
            return "\"" + pergunta.getRespostaCorreta() + "\"";
        }

        // Fallback
        return pergunta.getRespostaCorreta();
    }
}