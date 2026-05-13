package com.baquara.dados;

import com.baquara.modelo.*;
import com.baquara.modelo.Pergunta.Dificuldade;
import java.util.*;

public class BancoPerguntasCapoeira {
    private Map<Dificuldade, List<Pergunta>> perguntasPorDificuldade;
    private Random random;
    private int proximoId;
    private Set<Integer> idsUsados;

    public BancoPerguntasCapoeira() {
        this.perguntasPorDificuldade = new HashMap<>();
        this.random = new Random();
        this.proximoId = 1;
        this.idsUsados = new HashSet<>();

        for (Dificuldade diff : Dificuldade.values()) {
            perguntasPorDificuldade.put(diff, new ArrayList<>());
        }

        carregarPerguntasFaceis();    // 150 perguntas
        carregarPerguntasMedias();    // 75 perguntas
        carregarPerguntasDificeis();  // 75 perguntas

        System.out.println("🥋 Banco de Capoeira carregado: " + getTotalPerguntas() + " perguntas!");
        System.out.println("   📗 Fáceis: " + perguntasPorDificuldade.get(Dificuldade.FACIL).size());
        System.out.println("   📙 Médias: " + perguntasPorDificuldade.get(Dificuldade.MEDIO).size());
        System.out.println("   📕 Difíceis: " + perguntasPorDificuldade.get(Dificuldade.DIFICIL).size());
    }

    // ==================== 150 PERGUNTAS FÁCEIS ====================
    // 50 Múltipla Escolha + 50 Verdadeiro/Falso + 50 Lacuna

    private void carregarPerguntasFaceis() {
        // ===== MÚLTIPLA ESCOLHA FÁCEIS (50) =====

        // 1-10: Instrumentos musicais básicos
        adicionarMultipla("Qual é o principal instrumento musical da capoeira?",
                Arrays.asList("Violão", "Berimbau", "Tambor", "Flauta"), 1, Dificuldade.FACIL);
        adicionarMultipla("Qual instrumento de percussão é usado na capoeira?",
                Arrays.asList("Piano", "Atabaque", "Violino", "Guitarra"), 1, Dificuldade.FACIL);
        adicionarMultipla("O pandeiro na capoeira serve para:",
                Arrays.asList("Marcar o ritmo", "Enfeitar a roda", "Chamar os jogadores", "Finalizar o jogo"), 0, Dificuldade.FACIL);
        adicionarMultipla("Qual instrumento é tocado com uma vaqueta de madeira?",
                Arrays.asList("Berimbau", "Atabaque", "Agogô", "Pandeiro"), 1, Dificuldade.FACIL);
        adicionarMultipla("O que é o caxixi na capoeira?",
                Arrays.asList("Um cesto de palha com sementes", "Um tipo de tambor", "Uma dança", "Uma comida"), 0, Dificuldade.FACIL);
        adicionarMultipla("Qual instrumento é feito com duas campânulas de metal?",
                Arrays.asList("Berimbau", "Atabaque", "Agogô", "Caxixi"), 2, Dificuldade.FACIL);
        adicionarMultipla("O reco-reco é feito de:",
                Arrays.asList("Couro", "Madeira ou metal com ranhuras", "Cabaça", "Bambu"), 1, Dificuldade.FACIL);
        adicionarMultipla("Qual desses NÃO é um instrumento da capoeira?",
                Arrays.asList("Berimbau", "Pandeiro", "Violão", "Atabaque"), 2, Dificuldade.FACIL);
        adicionarMultipla("O berimbau é composto por:",
                Arrays.asList("Verga, arame e cabaça", "Madeira e couro", "Metal e plástico", "Bambu e corda"), 0, Dificuldade.FACIL);
        adicionarMultipla("A cabaça do berimbau serve como:",
                Arrays.asList("Enfeite", "Caixa de ressonância", "Arma", "Banco"), 1, Dificuldade.FACIL);

        // 11-20: Golpes básicos
        adicionarMultipla("Qual é o movimento fundamental da capoeira?",
                Arrays.asList("Salto", "Ginga", "Corrida", "Dança"), 1, Dificuldade.FACIL);
        adicionarMultipla("O que é a 'meia-lua' na capoeira?",
                Arrays.asList("Um instrumento", "Um chute circular", "Uma dança", "Uma música"), 1, Dificuldade.FACIL);
        adicionarMultipla("Como se chama o movimento de estrela na capoeira?",
                Arrays.asList("Ginga", "Aú", "Negativa", "Rolê"), 1, Dificuldade.FACIL);
        adicionarMultipla("A 'queixada' é um golpe que atinge:",
                Arrays.asList("O peito", "O queixo/rosto", "As pernas", "As costas"), 1, Dificuldade.FACIL);
        adicionarMultipla("O que é a 'negativa' na capoeira?",
                Arrays.asList("Um ataque", "Uma posição de defesa/esquiva", "Um salto", "Uma música"), 1, Dificuldade.FACIL);
        adicionarMultipla("Qual golpe é dado com a perna esticada em movimento circular?",
                Arrays.asList("Armada", "Cabeçada", "Rasteira", "Benção"), 0, Dificuldade.FACIL);
        adicionarMultipla("A 'cabeçada' é um golpe dado com:",
                Arrays.asList("Os pés", "As mãos", "A cabeça", "Os joelhos"), 2, Dificuldade.FACIL);
        adicionarMultipla("O que é a 'benção' na capoeira?",
                Arrays.asList("Uma oração", "Um chute frontal empurrando", "Uma esquiva", "Um salto"), 1, Dificuldade.FACIL);
        adicionarMultipla("A 'rasteira' visa:",
                Arrays.asList("Saltar sobre o oponente", "Derrubar o oponente puxando seus pés", "Atacar o rosto", "Defender-se de chutes"), 1, Dificuldade.FACIL);
        adicionarMultipla("Qual movimento imita um animal?",
                Arrays.asList("Aú", "Macaco", "Ginga", "Armada"), 1, Dificuldade.FACIL);

        // 21-30: Mestres famosos
        adicionarMultipla("Quem criou a Capoeira Regional?",
                Arrays.asList("Mestre Pastinha", "Mestre Bimba", "Mestre João Grande", "Besouro"), 1, Dificuldade.FACIL);
        adicionarMultipla("Quem foi o grande defensor da Capoeira Angola?",
                Arrays.asList("Mestre Bimba", "Mestre Pastinha", "Mestre Suassuna", "Mestre Nenel"), 1, Dificuldade.FACIL);
        adicionarMultipla("Mestre Bimba se chamava:",
                Arrays.asList("João Pereira", "Vicente Pastinha", "Manoel dos Reis Machado", "José Anastácio"), 2, Dificuldade.FACIL);
        adicionarMultipla("Mestre Pastinha se chamava:",
                Arrays.asList("Manoel Machado", "Vicente Ferreira Pastinha", "João Grande", "Antônio Carlos"), 1, Dificuldade.FACIL);
        adicionarMultipla("Quem foi o discípulo mais famoso de Mestre Bimba?",
                Arrays.asList("João Grande", "João Pequeno", "Nenel (seu filho)", "Pastinha"), 2, Dificuldade.FACIL);
        adicionarMultipla("Quem fundou o Grupo Cordão de Ouro?",
                Arrays.asList("Mestre Bimba", "Mestre Pastinha", "Mestre Suassuna", "Mestre Caíçara"), 2, Dificuldade.FACIL);
        adicionarMultipla("Qual mestre era conhecido como 'o filósofo da capoeira'?",
                Arrays.asList("Mestre Bimba", "Mestre João Pequeno", "Mestre Pastinha", "Mestre Suassuna"), 1, Dificuldade.FACIL);
        adicionarMultipla("Quem foi o lendário Besouro Mangangá?",
                Arrays.asList("Um mestre de capoeira baiano", "Um jogador de futebol", "Um político", "Um cantor"), 0, Dificuldade.FACIL);
        adicionarMultipla("Mestre João Grande e João Pequeno foram discípulos de:",
                Arrays.asList("Mestre Bimba", "Mestre Pastinha", "Mestre Suassuna", "Mestre Caíçara"), 1, Dificuldade.FACIL);
        adicionarMultipla("Onde nasceu a capoeira como conhecemos hoje?",
                Arrays.asList("São Paulo", "Rio de Janeiro", "Bahia", "Pernambuco"), 2, Dificuldade.FACIL);

        // 31-40: Cantigas e tradições
        adicionarMultipla("Como se chama o canto que inicia a roda de capoeira?",
                Arrays.asList("Cantiga", "Ladainha", "Oração", "Hino"), 1, Dificuldade.FACIL);
        adicionarMultipla("O que é um 'corrido' na capoeira?",
                Arrays.asList("Movimento rápido", "Canto de resposta coletiva", "Luta intensa", "Fuga"), 1, Dificuldade.FACIL);
        adicionarMultipla("Quem canta na roda de capoeira?",
                Arrays.asList("Apenas o mestre", "Todos podem cantar, mas o mestre lidera", "Só mulheres", "Só crianças"), 1, Dificuldade.FACIL);
        adicionarMultipla("O que significa 'IÊ' na capoeira?",
                Arrays.asList("Adeus", "Um grito de alerta ou resposta", "Silêncio", "Fim do jogo"), 1, Dificuldade.FACIL);
        adicionarMultipla("A palma na capoeira serve para:",
                Arrays.asList("Aplaudir apenas", "Marcar o ritmo e incentivar", "Chamar atenção", "Pedir silêncio"), 1, Dificuldade.FACIL);
        adicionarMultipla("O que é uma 'volta ao mundo' na capoeira?",
                Arrays.asList("Viagem", "Pausa caminhando em círculo", "Golpe giratório", "Fim da roda"), 1, Dificuldade.FACIL);
        adicionarMultipla("A 'chula' na capoeira é:",
                Arrays.asList("Um golpe", "Um canto de entrada na roda", "Um instrumento", "Uma dança"), 1, Dificuldade.FACIL);
        adicionarMultipla("O que significa 'camará' nas cantigas?",
                Arrays.asList("Inimigo", "Companheiro/amigo", "Mestre", "Estranho"), 1, Dificuldade.FACIL);
        adicionarMultipla("Como se chama a saudação da capoeira antes do jogo?",
                Arrays.asList("Aperto de mão", "Salve ou cumprimento ao pé do berimbau", "Abraço", "Grito"), 1, Dificuldade.FACIL);
        adicionarMultipla("O que é o 'jogo de dentro' na capoeira?",
                Arrays.asList("Luta em espaço fechado", "Jogo mais próximo e baixo, com malícia", "Jogo de equipe", "Treino individual"), 1, Dificuldade.FACIL);

        // 41-50: Cultura e história
        adicionarMultipla("A capoeira foi criada por:",
                Arrays.asList("Portugueses", "Índios", "Escravos africanos no Brasil", "Europeus"), 2, Dificuldade.FACIL);
        adicionarMultipla("A capoeira é considerada:",
                Arrays.asList("Apenas luta", "Apenas dança", "Luta, dança, jogo e cultura", "Apenas esporte"), 2, Dificuldade.FACIL);
        adicionarMultipla("Qual a cor da primeira corda do iniciante?",
                Arrays.asList("Azul", "Vermelha", "Crua (cor natural)", "Amarela"), 2, Dificuldade.FACIL);
        adicionarMultipla("Como se chama o professor de capoeira?",
                Arrays.asList("Sensei", "Mestre", "Coach", "Instrutor"), 1, Dificuldade.FACIL);
        adicionarMultipla("A capoeira foi reconhecida como patrimônio cultural pela UNESCO em:",
                Arrays.asList("2004", "2010", "2014", "2020"), 2, Dificuldade.FACIL);
        adicionarMultipla("Durante a escravidão, a capoeira era:",
                Arrays.asList("Incentivada", "Proibida e perseguida", "Ignorada", "Ensinada nas escolas"), 1, Dificuldade.FACIL);
        adicionarMultipla("O que significa 'capoeira' em tupi-guarani?",
                Arrays.asList("Dança da guerra", "Mato que foi cortado/ralo", "Luta de escravos", "Arte marcial"), 1, Dificuldade.FACIL);
        adicionarMultipla("Qual destes é um estilo de capoeira?",
                Arrays.asList("Karate", "Angola", "Judô", "Boxe"), 1, Dificuldade.FACIL);
        adicionarMultipla("A Capoeira Regional foi criada em que década?",
                Arrays.asList("1890", "1920-1930", "1950", "1970"), 1, Dificuldade.FACIL);
        adicionarMultipla("O que é um 'batizado' na capoeira?",
                Arrays.asList("Batismo religioso", "Cerimônia de troca de corda/graduação", "Primeira luta", "Festa"), 1, Dificuldade.FACIL);

        // ===== VERDADEIRO OU FALSO FÁCEIS (50) =====

        String[] vfFaceis = {
                "A capoeira foi criada por escravos africanos no Brasil.",
                "O berimbau é o principal instrumento da capoeira.",
                "A capoeira sempre foi legalizada no Brasil.",
                "Mestre Bimba criou a Capoeira Regional.",
                "A ginga é o movimento básico da capoeira.",
                "O atabaque é um instrumento de corda.",
                "A capoeira mistura luta, dança e música.",
                "Mestre Pastinha defendia a Capoeira Angola.",
                "O pandeiro é usado na roda de capoeira.",
                "A capoeira é proibida atualmente no Brasil.",
                "O 'aú' é um movimento acrobático semelhante à estrela.",
                "A ladainha é cantada no início da roda.",
                "A 'queixada' é um golpe com a cabeça.",
                "Mestre Bimba foi discípulo de Pastinha.",
                "O berimbau é feito com madeira, arame e cabaça.",
                "A capoeira é considerada patrimônio cultural da humanidade.",
                "O 'rolê' é um movimento de rolamento no chão.",
                "A 'armada' é um chute giratório.",
                "Mestre João Grande foi discípulo de Bimba.",
                "A capoeira Angola é mais lenta que a Regional.",
                "O caxixi acompanha o berimbau.",
                "A 'negativa' é um ataque poderoso.",
                "Besouro Mangangá é uma lenda da capoeira.",
                "A 'benção' é um chute frontal.",
                "O agogô é um instrumento de percussão.",
                "A capoeira surgiu na Bahia.",
                "Existem cordas/graduações na capoeira.",
                "O 'macaco' é um movimento que imita o animal.",
                "A 'rasteira' derruba o oponente pelos pés.",
                "Mestre Suassuna fundou o Cordão de Ouro.",
                "A 'volta ao mundo' é uma pausa na roda.",
                "O 'corrido' é um canto de resposta coletiva.",
                "A palma da mão marca o ritmo na capoeira.",
                "O mestre lidera o canto na roda.",
                "A 'cabeçada' usa a cabeça para atacar.",
                "A capoeira era praticada nos quilombos.",
                "O 'jogo de dentro' é mais próximo e baixo.",
                "O 'jogo de fora' é mais afastado e acrobático.",
                "A 'meia-lua' é um chute circular.",
                "Mestre Nenel era filho de Mestre Bimba.",
                "O berimbau determina o ritmo do jogo.",
                "A 'chamada' interrompe momentaneamente o jogo.",
                "A capoeira é praticada em mais de 150 países.",
                "O 'salve' é a saudação ao pé do berimbau.",
                "A 'mandinga' é a malícia/malandragem na capoeira.",
                "Mestre Caíçara era guardião das tradições orais.",
                "O 'passo' é sinônimo de ginga.",
                "A 'voadora' é um golpe saltando.",
                "O 'martelo' é um chute com o peito do pé.",
                "A capoeira foi reconhecida pela UNESCO em 2014."
        };

        boolean[] respostasVF = {
                true, true, false, true, true, false, true, true, true, false,
                true, true, false, false, true, true, true, true, false, true,
                true, false, true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true, true, true,
                true, true, true, true, true, true, true, true, true, true
        };

        for (int i = 0; i < vfFaceis.length; i++) {
            adicionarVF(vfFaceis[i], respostasVF[i], Dificuldade.FACIL);
        }

        // ===== COMPLETAR LACUNA FÁCEIS (50) =====

        String[][] lacunasFaceis = {
                {"O principal instrumento musical da capoeira é o __________.", "berimbau"},
                {"O movimento básico da capoeira é a __________.", "ginga"},
                {"A capoeira foi criada por __________ africanos no Brasil.", "escravos"},
                {"Mestre __________ criou a Capoeira Regional.", "Bimba"},
                {"Mestre __________ foi o guardião da Capoeira Angola.", "Pastinha"},
                {"O chute circular na capoeira é chamado de meia-__________.", "lua"},
                {"O movimento acrobático similar à estrela é o __________.", "aú"},
                {"A __________ é cantada no início da roda de capoeira.", "ladainha"},
                {"O __________ é um tambor usado na capoeira.", "atabaque"},
                {"A __________ de capoeira é o círculo onde se joga.", "roda"},
                {"O praticante de capoeira é chamado de __________.", "capoeirista"},
                {"Mestre Pastinha defendia a Capoeira __________.", "Angola"},
                {"O chute frontal na capoeira se chama __________.", "benção"},
                {"A __________ é uma posição de defesa e esquiva.", "negativa"},
                {"O golpe que derruba o oponente pelos pés é a __________.", "rasteira"},
                {"__________ Mangangá é uma lenda da capoeira baiana.", "Besouro"},
                {"O berimbau é feito de madeira, arame e __________.", "cabaça"},
                {"A capoeira foi reconhecida pela __________ como patrimônio cultural.", "UNESCO"},
                {"O __________ é um cesto de palha com sementes.", "caxixi"},
                {"A __________ é um golpe giratório com a perna estendida.", "armada"},
                {"O movimento que imita um animal é chamado de __________.", "macaco"},
                {"A __________ é a malícia e malandragem na capoeira.", "mandinga"},
                {"O __________ lidera a roda e o canto.", "mestre"},
                {"A palma serve para __________ o ritmo na roda.", "marcar"},
                {"O __________ é o canto de resposta coletiva.", "corrido"},
                {"A __________ ao mundo é uma pausa caminhando em círculo.", "volta"},
                {"Mestre __________ fundou o Grupo Cordão de Ouro.", "Suassuna"},
                {"A capoeira nasceu no estado da __________.", "Bahia"},
                {"O __________ é composto por duas campânulas de metal.", "agogô"},
                {"A __________ de compasso é um chute giratório baixo.", "meia-lua"},
                {"O __________ é um rolamento no chão para se esquivar.", "rolê"},
                {"O 'jogo de __________' é mais afastado e acrobático.", "fora"},
                {"O 'jogo de __________' é mais próximo e baixo.", "dentro"},
                {"A 'chamada' __________ o jogo momentaneamente.", "interrompe"},
                {"O __________ é um golpe dado com a cabeça.", "cabeçada"},
                {"O filho de Mestre Bimba é Mestre __________.", "Nenel"},
                {"Mestre __________ era discípulo de Pastinha.", "João Grande"},
                {"A __________ é um golpe saltando com as duas pernas.", "voadora"},
                {"O __________ é um golpe com o peito do pé.", "martelo"},
                {"A primeira corda do iniciante é de cor __________.", "crua"},
                {"O __________ é a cerimônia de troca de corda.", "batizado"},
                {"A capoeira mistura luta, __________, música e jogo.", "dança"},
                {"O __________ determina o ritmo do jogo na roda.", "berimbau"},
                {"A __________ é a saudação ao pé do berimbau.", "salve"},
                {"Mestre __________ era guardião das tradições orais.", "Caíçara"},
                {"O __________ é tocado com uma vaqueta de madeira.", "atabaque"},
                {"A '__________' é um grito de alerta na capoeira.", "IÊ"},
                {"O termo '__________' significa companheiro nas cantigas.", "camará"},
                {"O '__________' é um chute com a perna esticada lateralmente.", "queixada"},
                {"A capoeira é hoje praticada em mais de __________ países.", "150"}
        };

        for (String[] lacuna : lacunasFaceis) {
            adicionarLacuna(lacuna[0], lacuna[1], Dificuldade.FACIL);
        }
    }

    // ==================== 75 PERGUNTAS MÉDIAS ====================
    // 25 Múltipla Escolha + 25 Verdadeiro/Falso + 25 Lacuna

    private void carregarPerguntasMedias() {
        // ===== MÚLTIPLA ESCOLHA MÉDIAS (25) =====

        // 1-10: Técnicas e golpes avançados
        adicionarMultipla("Qual a diferença entre 'armada' e 'queixada'?",
                Arrays.asList("São o mesmo golpe", "Armada é giratória, queixada é lateral",
                        "Queixada é mais forte", "Armada é com a cabeça"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O que é 'tesoura' na capoeira?",
                Arrays.asList("Cortar o ar", "Movimento de pernas que prende o oponente",
                        "Um instrumento", "Uma música"), 1, Dificuldade.MEDIO);
        adicionarMultipla("Qual golpe é característico da Capoeira Angola?",
                Arrays.asList("Armada", "Meia-lua de compasso", "Voadora", "Martelo"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O 'S-dobrado' é um movimento de:",
                Arrays.asList("Ataque", "Esquiva com rolamento", "Salto", "Canto"), 1, Dificuldade.MEDIO);
        adicionarMultipla("Qual a sequência correta da 'sequência de Bimba'?",
                Arrays.asList("Armada→Aú→Ginga", "Ginga→Armada→Aú→Benção",
                        "Ginga→Negativa→Rolê", "Aú→Ginga→Queixada"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O que diferencia a 'meia-lua de frente' da 'meia-lua de compasso'?",
                Arrays.asList("A força", "A base de apoio e o giro do corpo",
                        "A velocidade", "A altura"), 1, Dificuldade.MEDIO);
        adicionarMultipla("A 'chapa' é um golpe dado com:",
                Arrays.asList("A mão aberta", "A sola do pé", "O joelho", "O cotovelo"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O que é 'balão' na capoeira?",
                Arrays.asList("Festa", "Movimento que desequilibra e derruba o oponente",
                        "Instrumento", "Canto"), 1, Dificuldade.MEDIO);
        adicionarMultipla("Qual a função do 'floreio' na capoeira?",
                Arrays.asList("Atacar", "Demonstrar habilidade e enfeitar o jogo",
                        "Defender", "Cantar"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O 'aú batido' difere do 'aú' normal por:",
                Arrays.asList("Ser mais lento", "Ter um impacto/parada no final",
                        "Ser executado sem as mãos", "Ser feito de costas"), 1, Dificuldade.MEDIO);

        // 11-17: História e mestres
        adicionarMultipla("Em que ano Mestre Bimba fundou sua academia?",
                Arrays.asList("1920", "1932", "1945", "1958"), 1, Dificuldade.MEDIO);
        adicionarMultipla("Qual era a profissão de Mestre Pastinha antes da capoeira?",
                Arrays.asList("Agricultor", "Pintor e marinheiro", "Professor", "Comerciante"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O que era o 'Centro Esportivo de Capoeira Angola'?",
                Arrays.asList("Um estádio", "A academia de Mestre Pastinha",
                        "Uma federação", "Um time de futebol"), 1, Dificuldade.MEDIO);
        adicionarMultipla("Qual presidente brasileiro legalizou a capoeira?",
                Arrays.asList("Juscelino Kubitschek", "Getúlio Vargas",
                        "João Goulart", "Jânio Quadros"), 1, Dificuldade.MEDIO);
        adicionarMultipla("A capoeira era disfarçada de quê durante a proibição?",
                Arrays.asList("Reza", "Dança folclórica", "Teatro", "Ritual religioso"), 1, Dificuldade.MEDIO);
        adicionarMultipla("Quem foi 'Siri de Mangue' na capoeira?",
                Arrays.asList("Um mestre famoso", "Um personagem de cantiga",
                        "Um instrumento", "Um golpe"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O que representou a 'Academia de Bimba' para a capoeira?",
                Arrays.asList("Nada importante", "A primeira academia legalizada de capoeira",
                        "Um ponto de encontro de malandros", "Uma escola de samba"), 1, Dificuldade.MEDIO);

        // 18-25: Ritmos e toques
        adicionarMultipla("Qual toque do berimbau é usado para jogo mais lento?",
                Arrays.asList("São Bento Grande", "Angola", "Cavalaria", "Iúna"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O toque de 'Cavalaria' era usado para:",
                Arrays.asList("Iniciar a roda", "Alertar sobre a chegada da polícia",
                        "Jogo rápido", "Finalizar o jogo"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O que é o toque 'Iúna'?",
                Arrays.asList("Jogo de iniciantes", "Toque para jogo de mestres/formados",
                        "Alerta de perigo", "Toque de encerramento"), 1, Dificuldade.MEDIO);
        adicionarMultipla("Quantos berimbaus geralmente compõem uma bateria completa?",
                Arrays.asList("1", "3 (gunga, médio e viola)", "5", "2"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O que diferencia o berimbau 'gunga' do 'viola'?",
                Arrays.asList("A cor", "O tamanho e o som (grave e agudo)",
                        "O material", "O formato"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O que significa 'dobrão' no contexto do berimbau?",
                Arrays.asList("Dinheiro", "Moeda usada para pressionar o arame",
                        "Um tipo de berimbau", "Uma música"), 1, Dificuldade.MEDIO);
        adicionarMultipla("Qual toque é característico para jogo acrobático?",
                Arrays.asList("Angola", "São Bento Grande", "Benguela", "Cavalaria"), 1, Dificuldade.MEDIO);
        adicionarMultipla("O toque de 'Benguela' serve para:",
                Arrays.asList("Luta violenta", "Jogo cadenciado e malicioso",
                        "Encerramento", "Saudação"), 1, Dificuldade.MEDIO);

        // ===== VERDADEIRO OU FALSO MÉDIAS (25) =====
        String[] vfMedias = {
                "A Capoeira Regional foi criada na década de 1930.",
                "Mestre Pastinha aprendeu capoeira com um africano chamado Benedito.",
                "O toque 'Cavalaria' avisava sobre a chegada da polícia.",
                "A 'meia-lua de compasso' é um golpe característico da Capoeira Angola.",
                "Mestre Bimba era também lutador de boxe.",
                "O berimbau 'gunga' tem som mais grave que o 'viola'.",
                "A 'sequência de Bimba' tem 8 movimentos.",
                "A capoeira foi legalizada durante o governo Vargas.",
                "Mestre João Pequeno era mais alto que Mestre João Grande.",
                "O 'S-dobrado' é um movimento de esquiva.",
                "A 'tesoura' derruba o oponente com as pernas.",
                "Mestre Caíçara foi discípulo de Mestre Bimba.",
                "O toque 'Iúna' é exclusivo para jogos de mestres.",
                "Besouro Mangangá era conhecido por ter o 'corpo fechado'.",
                "A ladainha pode ser improvisada pelo cantador.",
                "Mestre Suassuna introduziu o atabaque na capoeira.",
                "O 'floreio' não tem função prática no jogo.",
                "A Capoeira Angola é caracterizada por movimentos mais baixos.",
                "O 'balão' arremessa o oponente para cima.",
                "Mestre Nenel herdou a academia do pai.",
                "A 'chapa' é um chute lateral com a sola do pé.",
                "O 'aú batido' termina com um movimento de ataque.",
                "O pandeiro só foi introduzido na capoeira após 1950.",
                "A mandinga é ensinada verbalmente, não tecnicamente.",
                "O toque 'São Bento Grande' é para jogo rápido e agressivo."
        };

        boolean[] respostasVFMedias = {
                true, true, true, true, true, true, true, true, false, true,
                true, false, true, true, true, false, false, true, true, true,
                true, true, false, true, true
        };

        for (int i = 0; i < vfMedias.length; i++) {
            adicionarVF(vfMedias[i], respostasVFMedias[i], Dificuldade.MEDIO);
        }

        // ===== COMPLETAR LACUNA MÉDIAS (25) =====
        String[][] lacunasMedias = {
                {"O toque '__________' avisava sobre a aproximação da polícia.", "Cavalaria"},
                {"Mestre Bimba fundou sua academia em __________.", "1932"},
                {"O berimbau __________ tem o som mais grave da bateria.", "gunga"},
                {"O berimbau __________ tem o som mais agudo.", "viola"},
                {"A __________ de Bimba consiste em 8 movimentos básicos.", "sequência"},
                {"O toque '__________' é usado para jogo de mestres formados.", "Iúna"},
                {"A moeda usada para pressionar o arame do berimbau é o __________.", "dobrão"},
                {"O '__________' é um movimento de esquiva com rolamento.", "S-dobrado"},
                {"A '__________' prende o oponente com as pernas.", "tesoura"},
                {"O toque '__________' é para jogo cadenciado e malicioso.", "Benguela"},
                {"Mestre Pastinha aprendeu capoeira com o africano __________.", "Benedito"},
                {"O '__________' demonstra habilidade acrobática sem intenção de ataque.", "floreio"},
                {"A '__________' é um chute lateral com a sola do pé.", "chapa"},
                {"O '__________' arremessa o oponente para cima.", "balão"},
                {"O toque 'São Bento __________' é para jogo rápido.", "Grande"},
                {"A 'meia-lua de __________' é característica da Angola.", "compasso"},
                {"Mestre __________ era conhecido por sua malícia e mandinga.", "Canjiquinha"},
                {"A capoeira foi disfarçada de __________ durante a proibição.", "dança folclórica"},
                {"O 'aú __________' termina com um movimento de ataque.", "batido"},
                {"A __________ é uma das bases filosóficas da capoeira Angola.", "mandinga"},
                {"Mestre __________ herdou a academia de Mestre Bimba.", "Nenel"},
                {"O toque '__________' inicia a maioria das rodas de capoeira.", "Angola"},
                {"A cerimônia de __________ marca a troca de graduação.", "batizado"},
                {"O 'jogo de __________' é caracterizado por movimentos baixos.", "dentro"},
                {"Mestre __________ introduziu inovações na capoeira contemporânea.", "Suassuna"}
        };

        for (String[] lacuna : lacunasMedias) {
            adicionarLacuna(lacuna[0], lacuna[1], Dificuldade.MEDIO);
        }
    }

    // ==================== 75 PERGUNTAS DIFÍCEIS ====================
    // 25 Múltipla Escolha + 25 Verdadeiro/Falso + 25 Lacuna

    private void carregarPerguntasDificeis() {
        // ===== MÚLTIPLA ESCOLHA DIFÍCEIS (25) =====

        adicionarMultipla("Qual a origem etimológica mais aceita da palavra 'capoeira'?",
                Arrays.asList("Do tupi 'ka'a-puera' (mato ralo)", "Do quimbundo 'kapwila' (luta)",
                        "Do português 'capão' (galo de briga)", "Do iorubá 'kápó' (dança)"), 0, Dificuldade.DIFICIL);
        adicionarMultipla("Qual era o nome verdadeiro de Besouro Mangangá?",
                Arrays.asList("Manoel dos Reis Machado", "Manoel Henrique Pereira",
                        "Vicente Ferreira", "José Anastácio"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Em que cidade baiana Mestre Bimba nasceu?",
                Arrays.asList("Salvador", "Santo Amaro da Purificação",
                        "Cachoeira", "Feira de Santana"), 0, Dificuldade.DIFICIL);
        adicionarMultipla("Qual discípulo de Bimba criou a 'Capoeira Contemporânea'?",
                Arrays.asList("Mestre Nenel", "Mestre Camisa",
                        "Mestre Suassuna", "Mestre Acordeon"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("O que é 'Moringa' no contexto da capoeira?",
                Arrays.asList("Uma planta", "Um grupo de capoeira fundado por Mestre Índio",
                        "Um golpe", "Um instrumento musical"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Quem foi o 'Almirante' no mundo da capoeira?",
                Arrays.asList("Marinheiro famoso", "Mestre Almir das Areias, pesquisador",
                        "Um personagem de cantiga", "Um político"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Em que ano a capoeira foi descriminalizada oficialmente?",
                Arrays.asList("1937", "1940", "1953", "1988"), 0, Dificuldade.DIFICIL);
        adicionarMultipla("Qual é a diferença fundamental entre a Capoeira Angola e a Regional segundo os historiadores?",
                Arrays.asList("A cor da roupa", "A filosofia: Angola preserva tradições ancestrais, Regional moderniza e sistematiza",
                        "O local de prática", "O número de instrumentos"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("O que foi o 'II Congresso Internacional de Capoeira' em 1968?",
                Arrays.asList("Uma festa", "Evento que discutiu e padronizou aspectos da capoeira",
                        "Um torneio de luta", "Uma reunião de mestres para proibir a capoeira"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Qual a relação entre capoeira e candomblé?",
                Arrays.asList("Nenhuma", "Ambas são manifestações de resistência afro-brasileira com raízes comuns",
                        "São a mesma coisa", "O candomblé proíbe a capoeira"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Quem foi 'Mestre Gato' na história da capoeira?",
                Arrays.asList("Um mestre famoso da Regional", "Líder de maltas no Rio de Janeiro imperial",
                        "Um discípulo de Pastinha", "Criador de um golpe específico"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("O que eram as 'maltas' de capoeira no século XIX?",
                Arrays.asList("Grupos de dança", "Gangues organizadas de capoeiristas que controlavam territórios",
                        "Escolas de capoeira", "Festas religiosas"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Qual presidente criou o 'Batalhão de Capoeiras' na Guerra do Paraguai?",
                Arrays.asList("Dom Pedro II", "Caxias", "Deodoro da Fonseca", "Floriano Peixoto"), 0, Dificuldade.DIFICIL);
        adicionarMultipla("O que significa 'Makulelê' e sua relação com a capoeira?",
                Arrays.asList("Não tem relação", "Dança de bastões frequentemente apresentada junto à capoeira",
                        "Um tipo de berimbau", "Um mestre antigo"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Quem foi o mestre responsável por difundir a capoeira nos Estados Unidos nos anos 1970?",
                Arrays.asList("Mestre Bimba", "Mestre Jelon Vieira",
                        "Mestre Pastinha", "Mestre Suassuna"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Em que momento histórico a capoeira passou de 'crime' a 'esporte nacional'?",
                Arrays.asList("Independência do Brasil", "Era Vargas (1930-1945)",
                        "Ditadura militar", "Redemocratização"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("O que é a 'puxada de rede' na capoeira?",
                Arrays.asList("Ritual de pesca", "Cantiga que narra a vida dos pescadores, comum em rodas",
                        "Um golpe", "Uma esquiva"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Qual a função do 'corpo fechado' atribuído a Besouro?",
                Arrays.asList("Habilidade de luta", "Proteção espiritual/religiosa contra armas e morte",
                        "Técnica de defesa", "Título honorífico"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Quem foi 'Madame Satã' na história da capoeira carioca?",
                Arrays.asList("Uma cantora", "João Francisco dos Santos, malandro e capoeirista lendário",
                        "Uma mestra famosa", "Personagem de ficção"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("O que caracteriza a 'Capoeira de Rua' ou 'Capoeira de Vadiação'?",
                Arrays.asList("Luta sem regras", "Prática informal e espontânea nos espaços públicos",
                        "Competição oficial", "Treinamento militar"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Qual a importância de Mestre Decânio para a capoeira?",
                Arrays.asList("Nenhuma", "Médico e discípulo de Bimba, documentou cientificamente a capoeira",
                        "Criador de golpes", "Líder religioso"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("O que são as 'cantigas de sotaque' na capoeira?",
                Arrays.asList("Músicas estrangeiras", "Cantigas de desafio/duelo verbal entre cantadores",
                        "Cantigas religiosas", "Músicas de encerramento"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("Em que ano ocorreu a primeira apresentação pública de capoeira de Mestre Bimba?",
                Arrays.asList("1910", "1928", "1937", "1953"), 2, Dificuldade.DIFICIL);
        adicionarMultipla("Qual o significado do 'chamamento' ou 'chamada' durante o jogo?",
                Arrays.asList("Pedido de socorro", "Interrupção estratégica que testa a malícia do parceiro",
                        "Fim da roda", "Saudação ao público"), 1, Dificuldade.DIFICIL);
        adicionarMultipla("O que diferencia a 'ginga de Angola' da 'ginga Regional'?",
                Arrays.asList("A velocidade", "Angola: ginga mais baixa, corpo inclinado. Regional: ginga mais alta e ereta",
                        "A cor da roupa", "Os instrumentos usados"), 1, Dificuldade.DIFICIL);

        // ===== VERDADEIRO OU FALSO DIFÍCEIS (25) =====
        String[] vfDificeis = {
                "Besouro Mangangá foi morto por arma de fogo apesar do 'corpo fechado'.",
                "Mestre Bimba nasceu em Santo Amaro da Purificação.",
                "A capoeira foi descriminalizada em 1937 durante o Estado Novo.",
                "O termo 'capoeira' vem do tupi 'ka'a-puera'.",
                "Mestre Pastinha foi marinheiro antes de se dedicar à capoeira.",
                "As 'maltas' do Rio de Janeiro eram gangues de capoeiristas no século XIX.",
                "Mestre Jelon Vieira foi pioneiro da capoeira nos Estados Unidos.",
                "Dom Pedro II criou o 'Batalhão de Capoeiras' para a Guerra do Paraguai.",
                "Mestre Decânio era médico e documentou a capoeira cientificamente.",
                "'Madame Satã' era um capoeirista lendário do Rio de Janeiro.",
                "A 'chamada' na capoeira Angola interrompe o jogo momentaneamente.",
                "Mestre Camisa fundou o grupo Abadá na década de 1980.",
                "O 'II Congresso Internacional de Capoeira' foi realizado em 1968.",
                "A 'puxada de rede' é uma cantiga sobre a vida dos pescadores.",
                "Mestre Acordeon foi discípulo direto de Mestre Pastinha.",
                "O 'corpo fechado' de Besouro era proteção do candomblé.",
                "A Capoeira de Rua é caracterizada pela espontaneidade.",
                "Existem registros de capoeira em quilombos no século XVII.",
                "O 'sotaque' na capoeira é um duelo verbal entre cantadores.",
                "A primeira academia legalizada de capoeira foi a de Mestre Bimba.",
                "Mestre Índio fundou o grupo Moringa de capoeira.",
                "A capoeira é praticada em todos os continentes atualmente.",
                "O código penal de 1890 criminalizava especificamente a capoeira.",
                "Mestre João Pequeno recebeu título de Doutor Honoris Causa.",
                "A 'ginga de Angola' é mais alta que a 'ginga Regional'."
        };

        boolean[] respostasVFDificeis = {
                true, false, true, true, true, true, true, true, true, true,
                true, true, true, true, false, true, true, true, true, true,
                true, true, true, true, false
        };

        for (int i = 0; i < vfDificeis.length; i++) {
            adicionarVF(vfDificeis[i], respostasVFDificeis[i], Dificuldade.DIFICIL);
        }

        // ===== COMPLETAR LACUNA DIFÍCEIS (25) =====
        String[][] lacunasDificeis = {
                {"O nome verdadeiro de Besouro Mangangá era Manoel Henrique __________.", "Pereira"},
                {"A capoeira foi descriminalizada durante a Era __________.", "Vargas"},
                {"Mestre __________ foi o primeiro a difundir a capoeira nos EUA.", "Jelon Vieira"},
                {"As __________ eram gangues de capoeiristas no Rio de Janeiro imperial.", "maltas"},
                {"O termo 'capoeira' vem do tupi '__________' (mato ralo).", "ka'a-puera"},
                {"Mestre __________ fundou o grupo Moringa de capoeira.", "Índio"},
                {"Mestre __________ documentou cientificamente a capoeira como médico.", "Decânio"},
                {"O 'II Congresso __________ de Capoeira' foi em 1968.", "Internacional"},
                {"A '__________ de rede' canta a vida dos pescadores.", "puxada"},
                {"__________ Satã foi um lendário malandro capoeirista carioca.", "Madame"},
                {"O 'corpo __________' protegia Besouro segundo a crença popular.", "fechado"},
                {"Mestre __________ fundou o grupo Abadá Capoeira.", "Camisa"},
                {"O código penal de __________ criminalizava a capoeira.", "1890"},
                {"A '__________' é um duelo verbal entre cantadores de capoeira.", "sotaque"},
                {"Mestre __________ foi discípulo de Bimba e difundiu a capoeira na Europa.", "Acordeon"},
                {"A ginga de Angola é mais __________ que a ginga Regional.", "baixa"},
                {"O '__________ de Capoeiras' lutou na Guerra do Paraguai.", "Batalhão"},
                {"Mestre __________ estudou a relação entre capoeira e educação.", "Decânio"},
                {"A primeira apresentação pública de Bimba foi em __________.", "1928"},
                {"A capoeira de __________ é espontânea nos espaços públicos.", "rua"},
                {"O toque de '__________' era usado como alerta policial.", "Cavalaria"},
                {"Mestre __________ nasceu em Santo Amaro da Purificação.", "Caíçara"},
                {"A '__________' testa a malícia durante o jogo de Angola.", "chamada"},
                {"O berimbau __________ é o maior e mais grave da bateria.", "gunga"},
                {"A dança do __________ é frequentemente apresentada com capoeira.", "Makulelê"}
        };

        for (String[] lacuna : lacunasDificeis) {
            adicionarLacuna(lacuna[0], lacuna[1], Dificuldade.DIFICIL);
        }
    }


    private void adicionarMultipla(String texto, List<String> opcoes, int indiceCorreta, Dificuldade diff) {
        String letraCorreta = String.valueOf((char)('A' + indiceCorreta));
        String respostaCorreta = opcoes.get(indiceCorreta);

        Pergunta p = new PerguntaMultiplaEscolha(
                proximoId, texto, opcoes, letraCorreta, diff,
                PerTipo.CAPOEIRISTA, "Capoeira", 1
        );
        proximoId++;

        perguntasPorDificuldade.get(diff).add(p);
    }

    private void adicionarVF(String texto, boolean resposta, Dificuldade diff) {
        Pergunta p = new PerguntaVerdadeiroFalso(
                proximoId, texto, resposta, diff,
                PerTipo.CAPOEIRISTA, "Capoeira", 1
        );
        proximoId++;

        perguntasPorDificuldade.get(diff).add(p);
    }

    private void adicionarLacuna(String texto, String resposta, Dificuldade diff) {
        Pergunta p = new PerguntaCompletarLacuna(
                proximoId, texto, resposta, diff,
                PerTipo.CAPOEIRISTA, "Capoeira", 1
        );
        proximoId++;

        perguntasPorDificuldade.get(diff).add(p);
    }


    public Pergunta getPerguntaAleatoria(Dificuldade dificuldade) {
        List<Pergunta> perguntas = perguntasPorDificuldade.get(dificuldade);

        if (perguntas == null || perguntas.isEmpty()) {
            System.out.println("⚠️ Nenhuma pergunta disponível para dificuldade " + dificuldade);
            return null;
        }

        List<Pergunta> disponiveis = new ArrayList<>();
        for (Pergunta p : perguntas) {
            if (!idsUsados.contains(p.getId())) {
                disponiveis.add(p);
            }
        }

        if (disponiveis.isEmpty()) {
            System.out.println("🔄 Todas as perguntas desta dificuldade foram usadas. Reiniciando ciclo...");
            idsUsados.clear();
            disponiveis = new ArrayList<>(perguntas);
        }

        Pergunta escolhida = disponiveis.get(random.nextInt(disponiveis.size()));
        idsUsados.add(escolhida.getId());

        int total = perguntas.size();
        int usadas = 0;
        for (Pergunta p : perguntas) {
            if (idsUsados.contains(p.getId())) {
                usadas++;
            }
        }
        System.out.println("📚 " + usadas + "/" + total + " perguntas usadas nesta dificuldade");

        return escolhida;
    }


    public Pergunta getPerguntaAleatoria() {
        List<Pergunta> todas = new ArrayList<>();
        for (Dificuldade diff : Dificuldade.values()) {
            todas.addAll(perguntasPorDificuldade.get(diff));
        }

        if (todas.isEmpty()) {
            return null;
        }

        List<Pergunta> disponiveis = new ArrayList<>();
        for (Pergunta p : todas) {
            if (!idsUsados.contains(p.getId())) {
                disponiveis.add(p);
            }
        }

        if (disponiveis.isEmpty()) {
            idsUsados.clear();
            disponiveis = todas;
        }

        Pergunta escolhida = disponiveis.get(random.nextInt(disponiveis.size()));
        idsUsados.add(escolhida.getId());

        return escolhida;
    }


    public void reiniciarCiclo() {
        idsUsados.clear();
        System.out.println("🔄 Ciclo de perguntas reiniciado!");
    }

    public int getTotalPerguntas() {
        int total = 0;
        for (Dificuldade diff : Dificuldade.values()) {
            List<Pergunta> lista = perguntasPorDificuldade.get(diff);
            if (lista != null) {
                total += lista.size();
            }
        }
        return total;
    }
}