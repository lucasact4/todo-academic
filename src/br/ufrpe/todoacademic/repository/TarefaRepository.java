package br.ufrpe.todoacademic.repository;

import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.exception.RepositoryException;
import java.util.List;

// Contrato da camada de repositório para trabalhar com Tarefa
public interface TarefaRepository {

    // cadastra uma nova tarefa no repositório
    void adicionar(Tarefa tarefa) throws RepositoryException;

    // atualiza uma tarefa já existente
    void atualizar(Tarefa tarefa) throws RepositoryException;

    // remove pelo id gerado em memória
    void remover(int id) throws RepositoryException;

    // busca uma tarefa específica
    Tarefa buscarPorId(int id) throws RepositoryException;

    // retorna todas as tarefas cadastradas
    List<Tarefa> listarTodas() throws RepositoryException;

    // filtra tarefas por responsável (ex.: Lucas, Guilherme...)
    List<Tarefa> buscarPorResponsavel(String responsavel) throws RepositoryException;
}
