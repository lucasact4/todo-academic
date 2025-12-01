package br.ufrpe.todoacademic.util;

import br.ufrpe.todoacademic.model.Tarefa;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// TableModel usado pela JTable da tela principal para exibir a lista de tarefas
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

    // fonte de dados que a tabela vai renderizar
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
            case 7 -> t.calcularPrioridade(); // usa a regra de cada subtipo de Tarefa
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
        // tabela toda só leitura (edição acontece no formulário próprio)
        return false;
    }

    // --------- Métodos de apoio usados pela tela ---------

    public List<Tarefa> getTarefas() {
        return tarefas;
    }

    // atualiza a lista e avisa a JTable para se redesenhar
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
