package br.ufrpe.todoacademic.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Tarefa de Trabalho em Grupo.
 * Em geral tem peso maior na nota, então vamos priorizar mais forte.
 */
public class TarefaTrabalhoGrupo extends Tarefa {

    public TarefaTrabalhoGrupo() {
        super();
    }

    public TarefaTrabalhoGrupo(String titulo,
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

        if (limite == null) {
            return 3; // trabalho em grupo sem prazo -> já é importante
        }

        long dias = ChronoUnit.DAYS.between(hoje, limite);

        // Vamos pesar mais forte ainda para trabalhos em grupo:
        if (dias <= 0)  return 5; // vencida/hoje
        if (dias <= 5)  return 4;
        if (dias <= 15) return 3;
        if (dias <= 30) return 2;
        return 1;
    }

    @Override
    public String getTipo() {
        return "Trabalho em Grupo";
    }
}
