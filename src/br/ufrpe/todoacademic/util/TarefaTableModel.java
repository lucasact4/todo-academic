package br.ufrpe.todoacademic.util;

import br.ufrpe.todoacademic.model.Tarefa;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TarefaTableModel extends AbstractTableModel {

    private final String[] colunas = {
            "ID",
            "Título",
            "Tipo",
            "Disciplina",
            "Responsável",
            "Prazo",
            "Status",
            "Prioridade"
    };

    private List<Tarefa> tarefas = new ArrayList<>();

    private final DateTimeFormatter dateFormatter =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

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
            case 3 -> t.getDisciplina();
            case 4 -> t.getResponsavel();
            case 5 -> t.getDataLimite() != null
                    ? t.getDataLimite().format(dateFormatter)
                    : "";
            case 6 -> t.getStatus() != null ? t.getStatus().name() : "";
            case 7 -> t.calcularPrioridade();
            default -> "";
        };
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (tarefas.isEmpty()) {
            return Object.class;
        }
        return getValueAt(0, columnIndex).getClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        // todas as colunas só leitura por enquanto
        return false;
    }

    // --------- Métodos de apoio ---------

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    public void setTarefas(List<Tarefa> tarefas) {
        this.tarefas = tarefas != null ? tarefas : new ArrayList<>();
        fireTableDataChanged();
    }

    public Tarefa getTarefa(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= tarefas.size()) {
            return null;
        }
        return tarefas.get(rowIndex);
    }
}
