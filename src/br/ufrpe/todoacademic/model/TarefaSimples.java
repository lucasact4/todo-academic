package br.ufrpe.todoacademic.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Tarefa "normal", sem regras extras além do prazo.
 * É a implementação mais básica de Tarefa.
 */
public class TarefaSimples extends Tarefa {

    // --------- Construtores ---------

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

    // --------- Implementações abstratas ---------

    /**
     * Exemplo de regra:
     * quanto mais perto do prazo, maior a prioridade.
     *
     * 5 = muito urgente (vencida ou vence hoje)
     * 4 = vence em até 2 dias
     * 3 = vence em até 7 dias
     * 2 = vence em até 14 dias
     * 1 = sem prazo definido ou longe demais
     */
    @Override
    public int calcularPrioridade() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = getDataLimite();

        if (limite == null) {
            return 1;
        }

        long dias = ChronoUnit.DAYS.between(hoje, limite);

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
