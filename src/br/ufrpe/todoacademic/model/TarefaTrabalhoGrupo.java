package br.ufrpe.todoacademic.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Tarefa específica para trabalhos em grupo (normalmente valem mais na nota)
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

        // trabalho em grupo sem prazo já começa com prioridade média
        if (limite == null) {
            return 3;
        }

        long dias = ChronoUnit.DAYS.between(hoje, limite);

        // quanto mais perto da entrega, mais urgente (bem pesado para grupo)
        if (dias <= 0)  return 5; // hoje ou atrasada
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
