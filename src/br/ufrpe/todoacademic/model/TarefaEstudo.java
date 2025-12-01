package br.ufrpe.todoacademic.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// Versão de tarefa voltada para estudo (revisão, leitura, etc.)
public class TarefaEstudo extends Tarefa {

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

        // estudo sem prazo ainda assim tem um peso maior que uma tarefa simples
        if (limite == null) {
            return 2;
        }

        long dias = ChronoUnit.DAYS.between(hoje, limite);

        // para estudo eu considero quase tudo mais urgente que o normal
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
