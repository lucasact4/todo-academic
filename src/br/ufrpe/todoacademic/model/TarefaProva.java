package br.ufrpe.todoacademic.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Tarefa específica para provas (usa uma regra de prioridade mais rígida)
public class TarefaProva extends Tarefa {

    public TarefaProva() {
        super();
    }

    public TarefaProva(String titulo,
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

        // prova sem data já acende um alerta médio
        if (limite == null) {
            return 3;
        }

        long dias = ChronoUnit.DAYS.between(hoje, limite);

        // quanto mais perto do dia da prova, mais urgente
        if (dias <= 0)  return 5; // hoje ou atrasada
        if (dias <= 3)  return 4; // reta final de revisão
        if (dias <= 7)  return 3;
        if (dias <= 20) return 2;
        return 1;
    }

    @Override
    public String getTipo() {
        return "Prova";
    }
}
