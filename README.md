# Baquara - Batalha do Saber ⚔️

[![Java Version](https://img.shields.io/badge/Java-21+-orange.svg)](https://www.oracle.com/java/technologies/downloads/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue.svg)](https://openjfx.io/)

**Baquara** é um jogo educacional de batalha por turnos desenvolvido para a disciplina de **Linguagem de Programação Orientada a Objetos (LPOO)**. O jogo combina perguntas e respostas com mecânicas de RPG, onde o conhecimento do jogador se traduz diretamente em poder de combate.

## 📋 Sobre o Projeto

Em um mundo onde o saber é poder, você assume o papel de um herói que deve superar desafios através do conhecimento. Cada rodada apresenta uma pergunta educacional; acertar causa dano ao inimigo, enquanto errar permite que o adversário contra-ataque.

O nome **"Baquara"** é uma homenagem à cultura afro-brasileira e à capoeira, representando a união entre conhecimento, luta e arte.

**Características Principais:**
- 🎮 6 personagens jogáveis com habilidades e atributos únicos
- 📚 1800 perguntas distribuídas em 6 temas educacionais (300 por personagem)
- ⚔️ Sistema de batalha por turnos com cálculo de dano baseado em defesa
- 🩸 Efeitos de status (sangramento, debuff de ataque)
- 🌟 Habilidades especiais com recursos próprios
- 🗺️ Rotas exclusivas (10 estágios por personagem + chefão final)
- 🔮 Rota secreta desbloqueável (modo Capoeira)
- 🏆 Ranking persistente
- 🎨 Interface gráfica completa em JavaFX

## 🎮 Gameplay

**Fluxo de Batalha:**
1. Processa efeitos de status no início da rodada (sangramento causa dano)
2. Exibe menu de ações: ATACAR ou HABILIDADE ESPECIAL
3. Apresenta pergunta baseada na dificuldade do estágio
4. Aguarda resposta do jogador
5. Avalia resposta e aplica efeito: Acerto → causa dano ao inimigo + recupera atributo especial | Erro → inimigo contra-ataca
6. Atualiza status e verifica vitória/derrota

**Tipos de Pergunta:**
- **Múltipla Escolha:** 4 alternativas (A, B, C, D)
- **Verdadeiro/Falso:** afirmação com opções V ou F
- **Completar Lacuna:** texto com espaço para preencher

**Dificuldade e Dano:**
- Estágios 1-3: Fácil (multiplicador 1x)
- Estágios 4-7: Médio (multiplicador 2x)
- Estágios 8-10: Difícil (multiplicador 3x)

**Fórmulas:**
- Dano do jogador = ataque × multiplicadorDificuldade × multiplicadorEstagio × variação (±15%)
- Mitigação de dano = defesa / (defesa + 50) do alvo

## 👥 Personagens e Classes

| Personagem | Emoji | Tema | Atributo | Habilidade |
|------------|-------|------|----------|------------|
| Paladino | 🛡️ | Religiões | Poder Divino | Espírito Sagrado (cura + dano) |
| Guerreiro | ⚔️ | Artes marciais, história | Espírito de Luta | Fúria do Guerreiro (dano + debuff) |
| Caçadora | 🏹 | Animais, natureza | Penetração | Chuva de Flechas (dano + sangramento) |
| Sábio | 📚 | Filosofia, ciência, arte | Mana | Sabedoria Ancestral (dano verdadeiro + cura) |
| Arcanista | 🔮 | Mitologia, magia | Poder Arcano | Cataclismo Arcano (dano elemental) |
| Capoeirista | 🥋 | Capoeira, cultura afro | Energia da Ginga | Jogo de Capoeira (múltiplos golpes + esquiva) |

**Atributos dos Personagens:**
- Paladino: Vida 150, Ataque 22, Defesa 25, Poder Divino (max 100)
- Guerreiro: Vida 140, Ataque 28, Defesa 18, Espírito de Luta (max 100)
- Caçadora: Vida 110, Ataque 30, Defesa 12, Penetração (max 100)
- Sábio: Vida 100, Ataque 25, Defesa 14, Mana (max 100)
- Arcanista: Vida 95, Ataque 32, Defesa 8, Poder Arcano (max 100)
- Capoeirista: Vida 120, Ataque 25, Defesa 15, Energia da Ginga (max 100)

## 🗺️ Rotas e Estágios

Cada personagem possui uma rota única com 10 estágios (9 normais + 1 chefão):

| Personagem | Rota | Chefão Final |
|------------|------|--------------|
| Paladino | Caminho da Iluminação | METATRON |
| Guerreiro | Caminho da Glória | ODIN |
| Caçadora | Caminho da Predadora | FENRIR |
| Sábio | Caminho do Conhecimento | ATHENA |
| Arcanista | Caminho Místico | LOKI |
| Capoeirista | Roda Proibida | BESOURO MANGANGÁ |

**Modo Capoeira (Rota Secreta):** Desbloqueado ao digitar o nome "Rudá" na tela inicial. O jogador enfrenta 9 mestres históricos da capoeira:

| Estágio | Mestre | Vida |
|---------|--------|------|
| 1 | Mestre Bimba | 160 |
| 2 | Mestre Pastinha | 260 |
| 3 | Mestre João Grande | 360 |
| 4 | Mestre João Pequeno | 460 |
| 5 | Mestre Canjiquinha | 560 |
| 6 | Mestre Caiçara | 660 |
| 7 | Mestre Suassuna | 760 |
| 8 | Mestre Nenel | 860 |
| 9 | Mestre Moraes | 960 |
| 10 | Besouro Mangangá | 2000 |

O chefão Besouro Mangangá possui 3 fases de batalha:
- **Fase 1 (100%-66% vida):** Ataques normais
- **Fase 2 (66%-33% vida):** Corpo fechado - imune a ataques com menos de 50 de dano, defesa aumentada
- **Fase 3 (33%-0% vida):** Fúria do Besouro - dano dobrado, mas defesa reduzida

## 🏗️ Arquitetura e Design

**Estrutura de Pacotes:**

com.baquara/
├── BaquaraApplication.java # Ponto de entrada JavaFX
├── controle/ # Camada de controle/lógica
│ ├── AvaliadorRespostas.java # Avaliação de respostas
│ ├── GerenciadorBatalha.java # Gerencia fluxo de batalha
│ ├── GerenciadorRodaCapoeira.java # Modo especial Capoeira
│ ├── GerenciadorRotas.java # Gerencia rotas/estágios
│ ├── RankingManager.java # Ranking persistente
│ └── TemporizadorResposta.java # Timer para perguntas
├── dados/ # Camada de dados
│ └── BancoPerguntas.java # Banco com 1800 perguntas
├── gui/controllers/ # Controladores JavaFX
│ ├── TelaBatalhaController.java
│ ├── TelaCapoeiraController.java
│ ├── TelaInicialController.java
│ ├── TelaPersonagemController.java
│ ├── TelaRankingController.java
│ └── TelaResultadoController.java
├── habilidades/ # Habilidades especiais
│ ├── HabilidadeCritico.java # Caçadora
│ ├── HabilidadeCura.java # Paladino
│ ├── HabilidadeDanoExtra.java # Guerreiro
│ ├── HabilidadeDestruicaoTotal.java # Arcanista
│ ├── HabilidadeEspecial.java # Interface
│ └── HabilidadePoderMagico.java # Sábio
└── modelo/ # Modelos do domínio
├── efeitos/
│ ├── EfeitoStatus.java
│ ├── SangramentoEfeito.java
│ └── DebuffAtaqueEfeito.java
├── Personagem.java (abstract)
├── Paladino.java, Guerreiro.java, Cacadora.java
├── Sabio.java, Arcanista.java, Capoeirista.java
├── Inimigo.java, BesouroManganga.java, Jogador.java
├── Pergunta.java (abstract)
├── PerguntaMultiplaEscolha.java
├── PerguntaVerdadeiroFalso.java
├── PerguntaCompletarLacuna.java
├── Rota.java, Estagio.java
├── PerTipo.java
└── ValoresHabilidade.java


**Diagrama de Classes Principal:**

┌─────────────────────────────────────────────────────────────┐
│ <<interface>> Entidade │
├─────────────────────────────────────────────────────────────┤
│ +getNome(): String, +getAtaque(): int, +getDefesa(): int │
│ +getVida(): int, +adicionarEfeito(), +atualizarEfeitos() │
└─────────────────────────────────────────────────────────────┘
△
│
┌─────────────────────┴─────────────────────┐
│ │
┌───────┴───────┐ ┌───────┴───────┐
│ Personagem │ │ Inimigo │
│ (abstract) │ │ │
├───────────────┤ ├───────────────┤
│ #nome: String │ │ -nivel: int │
│ #vida: int │ │ -ataqueBase │
│ #ataque: int │ └───────────────┘
│ #defesa: int │ △
└───────┬───────┘ │
│ ┌───────┴───────────┐
┌───────┼───────────────────────────────┐ │ BesouroManganga │
│ │ │ │ -faseAtual: int │
▼ ▼ ▼ ▼ └───────────────────┘
┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌────────────┐
│Paladino │ │Guerreiro│ │ Cacadora│ │ Sabio │ │Arcanista│ │Capoeirista │
└─────────┘ └─────────┘ └─────────┘ └─────────┘ └─────────┘ └────────────┘
│ │ │ │ │ │
└───────────┴───────────┴───────────┴───────────┴───────────┘
│
▼
┌───────────────────────────────┐
│ <<interface>> │
│ AtributoEspecial │
├───────────────────────────────┤
│ +getNomeAtributo(): String │
│ +getValorAtual(): int │
│ +consumir(int): boolean │
└───────────────────────────────┘

**Sistema de Habilidades (Strategy Pattern):**

┌─────────────────────────────────────────────────────────────┐
│ <<interface>> HabilidadeEspecial │
├─────────────────────────────────────────────────────────────┤
│ +getNome(): String │
│ +getDescricao(): String │
│ +executar(Inimigo): int │
│ +podeUsar(): boolean │
│ +recarregarAposAcerto(): void │
└─────────────────────────────────────────────────────────────┘
△ △ △ △
│ │ │ │
┌───────┴───────┐ ┌───────┴───────┐ ┌───────┴───────┐ ┌───────┴───────────┐
│HabilidadeCura│ │HabilidadeDano │ │HabilidadeCrit-│ │HabilidadeDestrui- │
│(Paladino) │ │Extra(Guerreiro)│ │ico(Caçadora) │ │caoTotal(Arcanista)│
├───────────────┤ ├───────────────┤ ├───────────────┤ ├───────────────────┤
│Cura: 40-80 │ │Dano: 50-120 │ │Dano: 60-100 │ │Dano: 80-250 │
│Dano: 25-60 │ │Debuff: -20% │ │Sangramento: │ │Efeitos: Fogo, │
│Recurso: │ │ataque por 3 │ │3 rodadas │ │Gelo, Raio, │
│Poder Divino │ │rodadas │ │Recurso: │ │Sombra, Luz │
│ │ │Recurso: │ │Penetração │ │Recurso: │
│ │ │Espírito de │ │ │ │Poder Arcano │
│ │ │Luta │ │ │ │ │
└───────────────┘ └───────────────┘ └───────────────┘ └───────────────────┘


**Sistema de Efeitos (State Pattern):**

┌─────────────────────────────────────────────────────────────┐
│ EfeitoStatus (abstract) │
├─────────────────────────────────────────────────────────────┤
│ #nome: String │
│ #duracaoRestante: int │
│ +aplicar(Entidade): void │
│ +atualizar(Entidade): void │
│ +remover(Entidade): void │
│ +renovar(): void │
│ +reduzirDuracao(): boolean │
└─────────────────────────────────────────────────────────────┘
△ △
│ │
┌───────┴───────────────┐ ┌─────────┴─────────────┐
│ SangramentoEfeito │ │ DebuffAtaqueEfeito │
├───────────────────────┤ ├───────────────────────┤
│ -danoPorRodada: int │ │ -multiplicador: double│
│ -danoTotalCausado: int│ │ -ataqueOriginal: int │
├───────────────────────┤ ├───────────────────────┤
│ +atualizar(): causa │ │ +aplicar(): reduz │
│ dano por rodada │ │ ataque em 20% │
│ com variação de │ │ +remover(): restaura │
│ ±20% │ │ ataque original │
└───────────────────────┘ └───────────────────────┘


## 📚 Banco de Perguntas

**Distribuição por Personagem (total 1800 perguntas, 300 por personagem):**

| Personagem | Total | Fácil | Médio | Difícil |
|------------|-------|-------|-------|---------|
| Paladino | 300 | 150 | 75 | 75 |
| Guerreiro | 300 | 150 | 75 | 75 |
| Caçadora | 300 | 150 | 75 | 75 |
| Sábio | 300 | 150 | 75 | 75 |
| Arcanista | 300 | 150 | 75 | 75 |
| Capoeirista | 300 | 150 | 75 | 75 |
| **TOTAL** | **1800** | **900** | **450** | **450** |

**Temas por Personagem:**

- **Paladino:** Cristianismo (Bíblia, santos, sacramentos, história da Igreja), religiões afro-brasileiras (Candomblé, Umbanda, Orixás), Budismo (Sidarta Gautama, Quatro Nobres Verdades, Caminho Óctuplo), Hinduísmo (Trimurti, Vedas, Bhagavad Gita)

- **Guerreiro:** Artes marciais (Judô, Caratê, Taekwondo, Boxe, Muay Thai, Jiu-Jitsu), estratégia militar (cerco, flanco, blitzkrieg, logística), história das guerras (Guerras Púnicas, Cruzadas, Guerras Mundiais)

- **Caçadora:** Animais (mamíferos, aves, répteis, animais marinhos), natureza (biomas, ecossistemas, fenômenos naturais), sobrevivência (técnicas, purificação de água, abrigo, sinalização), rastreamento (pegadas, fezes, tocas, marcações)

- **Sábio:** Filosofia (Sócrates, Platão, Aristóteles, Nietzsche, Kant, Sartre), ciência (biologia, química, física, astronomia), arte (pintura, escultura, música, arquitetura), matemática (operações, geometria, frações, porcentagem)

- **Arcanista:** Mitologia (grega, nórdica, egípcia, japonesa), magia (grimórios, alquimia, feitiços, wicca), elementos (fogo, água, terra, ar, éter), ocultismo (tarô, astrologia, cabala, runas)

- **Capoeirista:** Instrumentos (berimbau, atabaque, pandeiro, agogô), golpes (ginga, meia-lua, armada, queixada, rasteira, aú), mestres (Bimba, Pastinha, João Grande, Besouro Mangangá), cantigas (ladainha, corrido, chula, sotaque)

## 🎨 Interface Gráfica

**Telas Implementadas:**

| Tela | Descrição |
|------|-----------|
| **Inicial** | Campo para digitar o nome, botões Jogar/Ranking/Sair, suporte à tecla ENTER |
| **Personagem** | 5 portas para personagens normais; rota secreta para "Rudá" (desbloqueia Capoeirista). Cada porta mostra atributos do personagem |
| **Batalha** | Status dos combatentes (vida, ataque, defesa, atributo especial), área de diálogo com feedback colorido, botões ATACAR/HABILIDADE, painel de pergunta dinâmico |
| **Capoeira** | Sprites específicos 🥋, barra de Ginga, botões de golpes (Ginga Básica, Ataque Difícil, Combinação Mortal, Esquiva), timer visual, mostra esquivas restantes |
| **Resultado** | Pontuação total, rodadas jogadas, acertos/erros, aproveitamento percentual, dano causado/recebido, habilidades usadas, estágios completados, status final do personagem |
| **Ranking** | Lista ordenada por pontuação com posição, nome, personagem, pontos, estágios, modo de jogo e data. Medalhas para top 3 (🥇🥈🥉) |

**Feedback Visual:**
- Acerto: texto verde (#008800) em negrito
- Erro: texto vermelho (#CC0000) em negrito
- Sangramento: texto vermelho (#ff4444) com fundo escuro
- Efeitos de status: texto vermelho/laranja em negrito
- Dano normal: texto preto

## 🏆 Sistema de Ranking

- **Persistência:** Arquivo `ranking.dat` via serialização Java
- **Ordenação:** Por pontuação decrescente
- **Anti-duplicação:** Verifica os últimos 5 registros para evitar que a mesma partida seja registrada múltiplas vezes
- **Informações armazenadas:** Nome do jogador, personagem, pontuação, estágios completados, modo de jogo, data e hora

**Estrutura do Registro:**
```java
public static class RegistroRanking implements Serializable {
    private final String nomeJogador;
    private final String personagem;
    private final int pontuacao;
    private final int estagiosCompletados;
    private final boolean venceuJogo;
    private final String modoJogo;
    private final String data;  // dd/MM/yyyy HH:mm:ss
}

**Como Executar**

Pré-requisitos:

Java 21 

JavaFX 21

Maven (opcional, mas recomendado)

# Clone o repositório
git clone https://github.com/seu-usuario/baquara.git

# Entre no diretório
cd baquara

# Execute com Maven
./mvnw javafx:run
# ou
mvn javafx:run

**Estrutura de Diretórios:**

baquara/
├── src/main/java/com/baquara/          # Código fonte
├── src/main/resources/fxml/            # Arquivos FXML
│   ├── tela-inicial.fxml
│   ├── tela-personagem.fxml
│   ├── tela-batalha.fxml
│   ├── tela-capoeira.fxml
│   ├── tela-resultado.fxml
│   └── tela-ranking.fxml
├── ranking.dat                         # Arquivo de ranking (criado automaticamente)
└── pom.xml                             # Dependências Maven

**Dependências Maven (pom.xml)**

<dependencies>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-controls</artifactId>
        <version>21</version>
    </dependency>
    <dependency>
        <groupId>org.openjfx</groupId>
        <artifactId>javafx-fxml</artifactId>
        <version>21</version>
    </dependency>
</dependencies>

## 📐 Justificativas de Design e Arquitetura

### 1. Hierarquia de Personagens com `Entidade` e `AtributoEspecial`

```java
public interface Entidade {
    int getAtaque(); int getDefesa(); int getVida();
    void adicionarEfeito(EfeitoStatus efeito);
    void atualizarEfeitos();
}

public interface AtributoEspecial {
    String getNomeAtributo();
    boolean consumir(int quantidade);
    void recarregarCompletamente();
}
Justificativa: A interface Entidade padroniza o comportamento de todas as entidades que podem
participar de combate (personagens e inimigos), permitindo que o GerenciadorBatalha e o sistema
de efeitos operem de forma polimórfica. A interface AtributoEspecial isola a mecânica de
recursos especiais (Mana, Poder Divino, Penetração, etc.), permitindo que cada personagem
implemente seu próprio sistema de consumo e recarga sem acoplar essa lógica à classe base
Personagem. Isso segue o Princípio da Segregação de Interfaces (ISP).

public abstract class Pergunta {
    protected String texto;
    protected Dificuldade dificuldade;
    public abstract void exibir();
    public abstract List<String> getOpcoes();
}
Justificativa: A hierarquia de perguntas aplica o Template Method Pattern indireto: cada tipo de
pergunta sabe como se exibir e como fornecer suas opções. O BancoPerguntas gerencia coleções
polimórficas de Pergunta, e o AvaliadorRespostas usa instanceof para aplicar regras específicas
de avaliação. Isso permite fácil extensão para novos tipos de pergunta sem modificar as classes
existentes (Open/Closed Principle).

public interface HabilidadeEspecial {
    String getNome();
    int executar(Inimigo alvo);
    boolean podeUsar();
    void recarregarAposAcerto();
}
Justificativa: Em vez de implementar habilidades como métodos dentro de cada classe de
personagem (o que levaria a uma explosão de métodos e baixa coesão), optou-se pelo
Strategy Pattern. Cada habilidade é uma classe separada que recebe o personagem usuário
no construtor. Isso permite: reúso de lógica comum, testabilidade isolada de cada
habilidade, facilidade para adicionar novas habilidades sem modificar classes existentes,
e separação clara entre "o que o personagem é" e "o que o personagem faz".

public abstract class EfeitoStatus {
    protected int duracaoRestante;
    public abstract void aplicar(Entidade alvo);
    public abstract void atualizar(Entidade alvo);
    public abstract void remover(Entidade alvo);
}

Justificativa: O sistema de efeitos (sangramento, debuff de ataque) foi modelado com o State
Pattern. Cada efeito sabe como se aplicar, como atualizar (causar dano por rodada) e como se
remover. A classe Entidade mantém um mapa de efeitos ativos, e o método atualizarEfeitos() é
chamado no início de cada rodada. Isso desacopla completamente a lógica de efeitos das classes
de personagem e inimigo, seguindo o Princípio da Responsabilidade Única (SRP).

public class Rota {
    private PerTipo personagem;
    private List<Estagio> estagios;
}

public class Estagio {
    private int numero;
    private String nome;
    private int dificuldade;
    private Inimigo chefao; // null se não for chefão
}

Justificativa: Cada personagem possui uma rota única com 10 estágios (9 normais + 1 chefão). O
GerenciadorRotas centraliza a criação de todas as rotas, permitindo fácil balanceamento e
extensão. A classe Estagio utiliza composição para incluir um chefão opcional, demonstrando uso
de null como valor semântico para "não é chefão". Isso também permite adicionar novos estágios
ou modificar a ordem sem impactar outras partes do sistema.

public class AvaliadorRespostas {
    public static boolean avaliar(Pergunta pergunta, String resposta);
    private static String normalizarTexto(String texto);
}

Justificativa: A avaliação de respostas é centralizada em uma classe utilitária com métodos
estáticos. Isso permite: normalização de texto (remoção de acentos, lower case, remoção de
pontuação), suporte a múltiplas respostas corretas para perguntas de lacuna (ex: "ginga" e
"ginga básica" serem aceitas), exibição formatada da resposta correta para feedback ao jogador,
e fácil manutenção das regras de avaliação em um único local.

public class RankingManager {
    private static final String ARQUIVO_RANKING = "ranking.dat";
    private List<RegistroRanking> rankings;
    private boolean isPartidaDuplicada(...);
}

Justificativa: O ranking utiliza serialização Java (ObjectOutputStream) para persistência simples e
eficaz. O mecanismo anti-duplicação verifica os últimos 5 registros (mesmo nome, personagem, pontuação,
 estágios e modo de jogo no mesmo minuto) para evitar que uma mesma partida seja registrada múltiplas
vezes (ex: ao voltar da tela de resultado para o ranking e depois retornar). Isso garante a integridade
dos dados do ranking sem necessidade de um banco de dados externo.

public enum PerTipo {
    PALADINO("Paladino", "Religião, fé, justiça"),
    GUERREIRO("Guerreiro", "Combate, estratégia militar");
}

public enum Dificuldade {
    FACIL("Fácil", 10, 1, 4),
    MEDIO("Médio", 10, 4, 7),
    DIFICIL("Difícil", 10, 8, -1);
}

Justificativa: O uso de enum para PerTipo e Dificuldade traz type-safety, evita magic
strings/numbers e permite associar comportamento diretamente aos valores (ex: cada
dificuldade sabe seu dano base e intervalo de estágios). Isso torna o código mais legível
e menos propenso a erros.
