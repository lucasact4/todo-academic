package br.ufrpe.todoacademic.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Tarefa de Estudo.
 * Poderia representar horas de estudo, revisão para prova etc.
 */
public class TarefaEstudo extends Tarefa {

    // Poderia ter campos adicionais (ex.: horasPrevistas), mas para simplificar
    // vamos só mudar a forma de calcular a prioridade.

    public TarefaEstudo() {
        super();
    }

    public TarefaEstudo(String titulo,
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
            return 2; // estudo sem prazo definido ainda tem alguma importância
        }

        long dias = ChronoUnit.DAYS.between(hoje, limite);

        // Para estudo, vamos considerar tudo "mais urgente":
        if (dias <= 0)  return 5; // hoje ou atrasada
        if (dias <= 3)  return 4;
        if (dias <= 10) return 3;
        if (dias <= 20) return 2;
        return 1;
    }

    @Override
    public String getTipo() {
        return "Estudo";
    }
}
