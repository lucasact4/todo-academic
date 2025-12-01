package br.ufrpe.todoacademic.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Tarefa mais básica: só leva em conta o prazo para definir a prioridade
public class TarefaSimples extends Tarefa {

    public TarefaSimples() {
        super();
    }

    public TarefaSimples(String titulo,
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

        // se não tiver prazo, fica como prioridade baixa
        if (limite == null) {
            return 1;
        }

        long dias = ChronoUnit.DAYS.between(hoje, limite);

        // quanto mais perto do prazo, maior o número (mais urgente)
        if (dias <= 0)  return 5; // hoje ou atrasada
        if (dias <= 2)  return 4;
        if (dias <= 7)  return 3;
        if (dias <= 14) return 2;
        return 1;
    }

    @Override
    public String getTipo() {
        return "Tarefa Simples";
    }
}
