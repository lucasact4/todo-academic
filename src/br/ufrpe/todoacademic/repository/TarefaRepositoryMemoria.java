package br.ufrpe.todoacademic.repository;

import br.ufrpe.todoacademic.exception.RepositoryException;
import br.ufrpe.todoacademic.model.Tarefa;
import java.util.ArrayList;
import java.util.List;

public class TarefaRepositoryMemoria implements TarefaRepository {

    private final List<Tarefa> tarefas = new ArrayList<>();
    private int proximoId = 1;

    @Override
    public synchronized void adicionar(Tarefa tarefa) throws RepositoryException {
        if (tarefa == null) throw new RepositoryException("Tarefa não pode ser nula");
        tarefa.setId(proximoId++);
        tarefas.add(tarefa);
    }

    @Override
    public synchronized void atualizar(Tarefa tarefa) throws RepositoryException {
        if (tarefa == null) throw new RepositoryException("Tarefa não pode ser nula");
        int indice = encontrarIndicePorId(tarefa.getId());
        if (indice == -1) throw new RepositoryException("Tarefa com id " + tarefa.getId() + " não encontrada");
        tarefas.set(indice, tarefa);
    }

    @Override
    public synchronized void remover(int id) throws RepositoryException {
        int indice = encontrarIndicePorId(id);
        if (indice == -1) throw new RepositoryException("Tarefa com id " + id + " não encontrada");
        tarefas.remove(indice);
    }

    @Override
    public synchronized Tarefa buscarPorId(int id) throws RepositoryException {
        for (Tarefa t : tarefas) {
            if (t.getId() == id) return t;
        }
        throw new RepositoryException("Tarefa com id " + id + " não encontrada");
    }

    @Override
    public synchronized List<Tarefa> listarTodas() {
        return new ArrayList<>(tarefas);
    }

    @Override
    public synchronized List<Tarefa> listarPorUsuario(String loginUsuario) {
        List<Tarefa> resultado = new ArrayList<>();
        if (loginUsuario == null) return resultado;
        for (Tarefa t : tarefas) {
            if (t.getUsuarioLogin() != null && t.getUsuarioLogin().equalsIgnoreCase(loginUsuario)) {
                resultado.add(t);
            }
        }
        return resultado;
    }

    @Override
    public synchronized List<Tarefa> buscarPorResponsavel(String responsavel) {
        List<Tarefa> resultado = new ArrayList<>();
        if (responsavel == null) return resultado;
        for (Tarefa t : tarefas) {
            if (t.getResponsavel() != null && t.getResponsavel().equalsIgnoreCase(responsavel)) {
                resultado.add(t);
            }
        }
        return resultado;
    }
    
    // --- A CORREÇÃO ESTÁ AQUI: IMPLEMENTAÇÃO DO MÉTODO NOVO ---
    @Override
    public synchronized void limparTudo() {
        this.tarefas.clear();
        this.proximoId = 1;
    }

    private int encontrarIndicePorId(int id) {
        for (int i = 0; i < tarefas.size(); i++) {
            if (tarefas.get(i).getId() == id) return i;
        }
        return -1;
    }
}