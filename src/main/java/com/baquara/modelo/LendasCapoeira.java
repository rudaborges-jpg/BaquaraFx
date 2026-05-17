// 📁 modelo/LendasCapoeira.java

package com.baquara.modelo;

import java.util.Random;

public class LendasCapoeira {

    public static class MestreCapoeira extends Inimigo {
        private String titulo;
        private String fraseDerrota;
        private String historia;
        private Random random;
        private String[] gingas = {
                "🔄 GINGA DE ANGOLA - Movimentos baixos e rasteiros",
                "💫 GINGA REGIONAL - Movimentos rápidos e altos",
                "🌀 GINGA CONTEMPORÂNEA - Mistura de estilos"
        };

        public MestreCapoeira(String nome, String titulo, int nivel, int vida, int ataque,
                              String historia, String fraseDerrota) {
            super(nome, nivel, vida, ataque, 5 + (nivel * 3));
            this.titulo = titulo;
            this.historia = historia;
            this.fraseDerrota = fraseDerrota;
            this.random = new Random();
        }

        public String getTitulo() { return titulo; }
        public String getHistoria() { return historia; }
        public String getFraseDerrota() { return fraseDerrota; }

        @Override
        public void mostrarStatus() {
            System.out.println("👥 " + getNome() + " - " + titulo);
            System.out.println("   " + gingas[random.nextInt(gingas.length)]);
            super.mostrarStatus();
        }
    }

    public static MestreCapoeira[] criarTodosMestres() {
        return new MestreCapoeira[] {
                criarEstagio1(),
                criarEstagio2(),
                criarEstagio3(),
                criarEstagio4(),
                criarEstagio5(),
                criarEstagio6(),
                criarEstagio7(),
                criarEstagio8(),
                criarEstagio9()
        };
    }

    public static MestreCapoeira criarEstagio1() {
        return new MestreCapoeira(
                "MESTRE BIMBA",
                "O Pai da Capoeira Regional",
                1, 100, 15,
                "Manoel dos Reis Machado (1900-1974), conhecido como Mestre Bimba,\n" +
                        "criou a Capoeira Regional em 1928. Foi o primeiro a tirar a capoeira\n" +
                        "da marginalidade e transformá-la em arte marcial respeitada.",
                "A capoeira nunca morrerá... Ela vive em cada ginga..."
        );
    }

    public static MestreCapoeira criarEstagio2() {
        return new MestreCapoeira(
                "MESTRE PASTINHA",
                "O Guardião da Capoeira Angola",
                2, 130, 18,
                "Vicente Ferreira Pastinha (1889-1981) foi o grande defensor da\n" +
                        "Capoeira Angola, a forma mais tradicional. Ensinou que capoeira\n" +
                        "é dança, luta, filosofia e resistência cultural.",
                "Capoeira é tudo que a boca come e o coração sente..."
        );
    }

    public static MestreCapoeira criarEstagio3() {
        return new MestreCapoeira(
                "MESTRE JOÃO GRANDE",
                "O Discípulo de Pastinha",
                3, 160, 22,
                "João Oliveira dos Santos (1933-2022), discípulo de Mestre Pastinha,\n" +
                        "levou a Capoeira Angola para o mundo. Ensinou nos EUA e formou\n" +
                        "gerações de capoeiristas. Foi doutor honoris causa.",
                "A capoeira é minha vida... Minha filosofia..."
        );
    }

    public static MestreCapoeira criarEstagio4() {
        return new MestreCapoeira(
                "MESTRE JOÃO PEQUENO",
                "O Filósofo da Capoeira",
                4, 190, 26,
                "João Pereira dos Santos (1917-2011), também discípulo de Pastinha,\n" +
                        "era conhecido por sua técnica impecável e movimentos precisos.\n" +
                        "Ensinou que capoeira é humildade e respeito.",
                "Na capoeira, o importante não é o golpe, é a intenção..."
        );
    }

    public static MestreCapoeira criarEstagio5() {
        return new MestreCapoeira(
                "MESTRE CANJIQUINHA",
                "O Gênio da Malícia",
                5, 220, 30,
                "José Anastácio dos Santos (1937-2003), discípulo de Bimba,\n" +
                        "era mestre da malícia e da mandinga. Suas rodas eram famosas\n" +
                        "pela imprevisibilidade e criatividade dos movimentos.",
                "Malandro não briga, malandro ginga..."
        );
    }

    public static MestreCapoeira criarEstagio6() {
        return new MestreCapoeira(
                "MESTRE CAIÇARA",
                "O Guardião da Tradição Oral",
                6, 250, 34,
                "Antônio Carlos Moraes (1938-2008) foi guardião das tradições orais\n" +
                        "da capoeira. Preservou ladainhas, corridos e histórias ancestrais.\n" +
                        "Sua memória guardava séculos de sabedoria.",
                "Quem não sabe ler a história, está condenado a repeti-la..."
        );
    }

    public static MestreCapoeira criarEstagio7() {
        return new MestreCapoeira(
                "MESTRE SUASSUNA",
                "O Inovador da Capoeira",
                7, 280, 38,
                "Reinaldo Ramos Suassuna (1938-) fundou o Grupo Cordão de Ouro,\n" +
                        "criando um estilo próprio que mistura tradição e inovação.\n" +
                        "Suas rodas são espetáculos de técnica e beleza.",
                "Capoeira é identidade, é raiz, é resistência..."
        );
    }

    public static MestreCapoeira criarEstagio8() {
        return new MestreCapoeira(
                "MESTRE NENEL",
                "O Herdeiro de Bimba",
                8, 320, 42,
                "Manoel Nascimento Machado (1956-), filho de Mestre Bimba,\n" +
                        "é o guardião do legado da Capoeira Regional. Mantém viva\n" +
                        "a sequência de ensino criada por seu pai.",
                "A capoeira regional é a arte de meu pai... Minha herança..."
        );
    }

    public static MestreCapoeira criarEstagio9() {
        return new MestreCapoeira(
                "MESTRE MORAES",
                "O Filósofo da Capoeira Angola",
                9, 380, 46,
                "Pedro Moraes Trindade (1950-), fundador do GCAP, é um dos maiores\n" +
                        "pensadores da capoeira. Sua filosofia une ancestralidade africana,\n" +
                        "resistência cultural e transformação social.",
                "Capoeira é revolução... É a arte de transformar dor em dança..."
        );
    }
}