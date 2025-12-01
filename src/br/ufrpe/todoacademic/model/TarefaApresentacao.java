package br.ufrpe.todoacademic.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TarefaApresentacao extends Tarefa {

    public TarefaApresentacao() {
        super();
    }

    public TarefaApresentacao(String titulo, String descricao, String disciplina, 
                              String responsavel, String notas, LocalDate dataLimite) {
        super(titulo, descricao, disciplina, responsavel, notas, dataLimite);
    }

    @Override
    public int calcularPrioridade() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = getDataLimite();

        if (limite == null) {
            return 3; 
        }

        long dias = ChronoUnit.DAYS.between(hoje, limite);

        // Lógica de Ensaio:
        if (dias <= 0)  return 5; // Dia da apresentação
        if (dias <= 5)  return 4; // 5 dias antes = Alta (Semana de ensaios)
        if (dias <= 14) return 3; // 2 semanas = Média (Montar slides)
        if (dias <= 30) return 2; 
        return 1;
    }

    @Override
    public String getTipo() {
        return "Apresentação";
    }
}