package br.ufrpe.todoacademic.repository;

import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.exception.RepositoryException;
import java.util.List;

public interface TarefaRepository {

    void adicionar(Tarefa tarefa) throws RepositoryException;

    void atualizar(Tarefa tarefa) throws RepositoryException;

    void remover(int id) throws RepositoryException;

    Tarefa buscarPorId(int id) throws RepositoryException;

    List<Tarefa> listarTodas() throws RepositoryException;

    List<Tarefa> buscarPorResponsavel(String responsavel) throws RepositoryException;
}
