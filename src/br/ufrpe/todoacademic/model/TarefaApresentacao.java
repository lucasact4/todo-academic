package br.ufrpe.todoacademic.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Tarefa voltada para apresentações (slides, seminários, etc.)
public class TarefaApresentacao extends Tarefa {

    public TarefaApresentacao() {
        super();
    }

    public TarefaApresentacao(String titulo,
                              String descricao,
                              String disciplina,
                              String responsavel,
                              String notas,
                              LocalDate dataLimite) {
        super(titulo, descricao, disciplina, responsavel, notas, dataLimite);
    }

    @Override
    public int calcularPrioridade() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = getDataLimite();

        // apresentação sem data já entra com prioridade média
        if (limite == null) {
            return 3;
        }

        long dias = ChronoUnit.DAYS.between(hoje, limite);

        // regra pensada para fase de preparação e ensaio
        if (dias <= 0)  return 5; // dia da apresentação (ou atrasada)
        if (dias <= 5)  return 4; // semana de ensaios
        if (dias <= 14) return 3; // período para criar conteúdo/slide/etc
        if (dias <= 30) return 2;
        return 1;
    }

    @Override
    public String getTipo() {
        return "Apresentação";
    }
}
