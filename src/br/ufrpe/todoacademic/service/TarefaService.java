package br.ufrpe.todoacademic.service;

import br.ufrpe.todoacademic.exception.RepositoryException;
import br.ufrpe.todoacademic.exception.TarefaInvalidaException;
import br.ufrpe.todoacademic.model.*;
import br.ufrpe.todoacademic.repository.TarefaRepository;

import java.time.LocalDate;
import java.util.List;

public class TarefaService {

    private final TarefaRepository repository;

    public TarefaService(TarefaRepository repository) {
        this.repository = repository;
    }

    // ============= CONSULTA COM PERMISSÃO =============

    public List<Tarefa> listarTarefas(Usuario usuario) throws RepositoryException {
        // CORREÇÃO: Agora ADMIN também vê tudo, igual ao PROFESSOR
        if (usuario.getTipo() == TipoUsuario.PROFESSOR || usuario.getTipo() == TipoUsuario.ADMIN) {
            return repository.listarTodas();
        } else {
            return repository.listarPorUsuario(usuario.getLogin());
        }
    }
    
    public List<Tarefa> listarTodas() throws RepositoryException {
        return repository.listarTodas();
    }

    public Tarefa buscarPorId(int id) throws RepositoryException {
        return repository.buscarPorId(id);
    }

    // ============= CADASTRO =============

    public Tarefa cadastrarTarefa(
            Usuario usuarioLogado,
            String tipoEsperado, String titulo, String descricao,
            String disciplina, String responsavel, String notas, LocalDate dataLimite
    ) throws TarefaInvalidaException, RepositoryException {

        validarDadosBasicos(titulo, disciplina, responsavel, dataLimite);

        Tarefa tarefa = instanciarTarefa(tipoEsperado, titulo, descricao, disciplina, responsavel, notas, dataLimite);
        tarefa.setUsuarioLogin(usuarioLogado.getLogin());

        repository.adicionar(tarefa);
        return tarefa;
    }
    
    public TarefaRepository getRepository() {
        return this.repository;
    }
    
    public void concluirTarefa(int id) throws RepositoryException {
        Tarefa t = repository.buscarPorId(id);
        t.setStatus(StatusTarefa.CONCLUIDA);
        repository.atualizar(t);
    }

    // NOVO MÉTODO: Coloca a tarefa "Em Andamento"
    public void iniciarTarefa(int id) throws RepositoryException {
        Tarefa t = repository.buscarPorId(id);
        t.setStatus(StatusTarefa.EM_ANDAMENTO);
        repository.atualizar(t);
    }

    // ============= ATUALIZAÇÃO =============

    public void atualizarTarefa(Usuario usuarioLogado, Tarefa tarefaExistente, String novoTipo)
            throws TarefaInvalidaException, RepositoryException {

        if (tarefaExistente == null) throw new TarefaInvalidaException("Tarefa nula.");

        verificarPermissaoEdicao(usuarioLogado, tarefaExistente);

        validarDadosBasicos(tarefaExistente.getTitulo(), tarefaExistente.getDisciplina(), 
                            tarefaExistente.getResponsavel(), tarefaExistente.getDataLimite());

        if (tarefaExistente.getTipo().equalsIgnoreCase(novoTipo)) {
            repository.atualizar(tarefaExistente);
        } else {
            Tarefa novaTarefa = instanciarTarefa(
                    novoTipo,
                    tarefaExistente.getTitulo(), tarefaExistente.getDescricao(),
                    tarefaExistente.getDisciplina(), tarefaExistente.getResponsavel(),
                    tarefaExistente.getNotas(), tarefaExistente.getDataLimite()
            );
            novaTarefa.setId(tarefaExistente.getId());
            novaTarefa.setDataCriacao(tarefaExistente.getDataCriacao());
            novaTarefa.setStatus(tarefaExistente.getStatus());
            novaTarefa.setUsuarioLogin(tarefaExistente.getUsuarioLogin());

            repository.atualizar(novaTarefa);
        }
    }

    // ============= REMOÇÃO =============

    public void removerTarefa(int id, Usuario usuarioLogado) throws RepositoryException, TarefaInvalidaException {
        Tarefa t = repository.buscarPorId(id);
        verificarPermissaoEdicao(usuarioLogado, t);
        repository.remover(id);
    }
    
    // ============= STATUS =============

    // ============= AUXILIARES =============

    private void verificarPermissaoEdicao(Usuario usuario, Tarefa tarefa) throws TarefaInvalidaException {
        // Professor e Admin podem editar tudo
        if (usuario.getTipo() == TipoUsuario.PROFESSOR || usuario.getTipo() == TipoUsuario.ADMIN) {
            return;
        }
        if (!tarefa.getUsuarioLogin().equals(usuario.getLogin())) {
            throw new TarefaInvalidaException("Você não tem permissão para alterar/excluir tarefas de outro usuário.");
        }
    }

    private Tarefa instanciarTarefa(String tipoTexto, String titulo, String desc,
                                    String disc, String resp, String notas, LocalDate data) {
        String tipo = tipoTexto != null ? tipoTexto.trim().toLowerCase() : "";
        switch (tipo) {
            case "estudo": return new TarefaEstudo(titulo, desc, disc, resp, notas, data);
            case "prova": return new TarefaProva(titulo, desc, disc, resp, notas, data);
            case "trabalho em grupo": return new TarefaTrabalhoGrupo(titulo, desc, disc, resp, notas, data);
            case "simples": default: return new TarefaSimples(titulo, desc, disc, resp, notas, data);
        }
    }

    private void validarDadosBasicos(String titulo, String disciplina, String responsavel, LocalDate dataLimite) throws TarefaInvalidaException {
        if (titulo == null || titulo.isBlank()) throw new TarefaInvalidaException("O título é obrigatório.");
        if (disciplina == null || disciplina.isBlank()) throw new TarefaInvalidaException("A disciplina é obrigatória.");
        if (responsavel == null || responsavel.isBlank()) throw new TarefaInvalidaException("O responsável é obrigatório.");
    }
}