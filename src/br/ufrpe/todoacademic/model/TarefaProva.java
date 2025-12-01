package br.ufrpe.todoacademic.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class TarefaProva extends Tarefa {

    public TarefaProva() {
        super();
    }

    public TarefaProva(String titulo, String descricao, String disciplina, 
                       String responsavel, String notas, LocalDate dataLimite) {
        super(titulo, descricao, disciplina, responsavel, notas, dataLimite);
    }

    @Override
    public int calcularPrioridade() {
        LocalDate hoje = LocalDate.now();
        LocalDate limite = getDataLimite();

        if (limite == null) {
            // Prova sem data é estranho, mas vamos tratar como alerta médio
            return 3; 
        }

        long dias = ChronoUnit.DAYS.between(hoje, limite);

        // Lógica "Tensa" de Prova:
        if (dias <= 0)  return 5; // É HOJE! (ou passou)
        if (dias <= 3)  return 4; // 3 dias antes = Alta (Revisão final)
        if (dias <= 7)  return 3; // 1 semana antes = Média
        if (dias <= 20) return 2; // Até 20 dias = Normal
        return 1; // Mais que 20 dias = Baixa
    }

    @Override
    public String getTipo() {
        return "Prova";
    }
}