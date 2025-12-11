package br.ufrpe.todoacademic.util;

import br.ufrpe.todoacademic.model.Tarefa;
import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TarefaTableModel extends AbstractTableModel {

    // REMOVIDO "Disciplina"
    private final String[] colunas = {
            "ID",
            "Título",
            "Tipo",
            "Responsável",
            "Prazo",
            "Status",
            "Prioridade"
    };

    private List<Tarefa> tarefas = new ArrayList<>();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas != null ? tarefas : new ArrayList<>();
        fireTableDataChanged();
    }

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public Tarefa getTarefa(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= tarefas.size()) return null;
        return tarefas.get(rowIndex);
    }

    @Override
    public int getRowCount() {
        return tarefas.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.length;
    }

    @Override
    public String getColumnName(int column) {
        return colunas[column];
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Tarefa t = tarefas.get(rowIndex);

        return switch (columnIndex) {
            case 0 -> t.getId();
            case 1 -> t.getTitulo();
            case 2 -> t.getTipo();
            // Case 3 (Disciplina) foi removido, Responsável assume o lugar
            case 3 -> t.getResponsavel();
            case 4 -> t.getDataLimite() != null ? t.getDataLimite().format(dateFormatter) : "";
            case 5 -> t.getStatus() != null ? t.getStatus().name() : "PENDENTE";
            case 6 -> t.calcularPrioridade();
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        // Define que ID (0) e Prioridade (6) são números para ordenação correta
        if (columnIndex == 0 || columnIndex == 6) {
            return Integer.class;
        }
        return Object.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}