package br.ufrpe.todoacademic.service;

import br.ufrpe.todoacademic.exception.RepositoryException;
import br.ufrpe.todoacademic.exception.TarefaInvalidaException;
import br.ufrpe.todoacademic.model.StatusTarefa;
import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.model.TarefaSimples;
import br.ufrpe.todoacademic.repository.TarefaRepository;
import br.ufrpe.todoacademic.model.TarefaEstudo;
import br.ufrpe.todoacademic.model.TarefaTrabalhoGrupo;

import java.time.LocalDate;
import java.util.List;

public class TarefaService {

    private final TarefaRepository repository;

    public TarefaService(TarefaRepository repository) {
        this.repository = repository;
    }

    // ================== CADASTRO ==================

    /**
     * Cria e cadastra uma TarefaSimples após validar os dados.
     */
    public Tarefa cadastrarTarefa(
            String tipoEsperado,
            String titulo,
            String descricao,
            String disciplina,
            String responsavel,
            String notas,
            LocalDate dataLimite
    ) throws TarefaInvalidaException, RepositoryException {

        validarDadosBasicos(titulo, disciplina, responsavel, dataLimite);

        Tarefa tarefa;

        String tipo = tipoEsperado != null ? tipoEsperado.trim().toLowerCase() : "";

        switch (tipo) {
            case "estudo":
                tarefa = new TarefaEstudo(titulo, descricao, disciplina, responsavel, notas, dataLimite);
                break;
            case "trabalho em grupo":
            case "trabalho em grupo ":
            case "trabalho":
                tarefa = new TarefaTrabalhoGrupo(titulo, descricao, disciplina, responsavel, notas, dataLimite);
                break;
            case "simples":
            default:
                tarefa = new TarefaSimples(titulo, descricao, disciplina, responsavel, notas, dataLimite);
                break;
        }

        repository.adicionar(tarefa);
        return tarefa;
    }
    public Tarefa cadastrarTarefaSimples(
            String titulo,
            String descricao,
            String disciplina,
            String responsavel,
            String notas,
            LocalDate dataLimite
    ) throws TarefaInvalidaException, RepositoryException {

        validarDadosBasicos(titulo, disciplina, responsavel, dataLimite);

        TarefaSimples tarefa = new TarefaSimples(
                titulo,
                descricao,
                disciplina,
                responsavel,
                notas,
                dataLimite
        );

        repository.adicionar(tarefa);
        return tarefa;
    }

    // ================== ATUALIZAÇÃO ==================

    /**
     * Atualiza uma tarefa já existente (assumindo que a tarefa já tem id válido).
     */
    public void atualizarTarefa(Tarefa tarefa)
            throws TarefaInvalidaException, RepositoryException {

        if (tarefa == null) {
            throw new TarefaInvalidaException("Tarefa não pode ser nula.");
        }

        validarDadosBasicos(
                tarefa.getTitulo(),
                tarefa.getDisciplina(),
                tarefa.getResponsavel(),
                tarefa.getDataLimite()
        );

        repository.atualizar(tarefa);
    }

    /**
     * Marca uma tarefa como CONCLUÍDA.
     */
    public void concluirTarefa(int id)
            throws RepositoryException {

        Tarefa t = repository.buscarPorId(id);
        t.setStatus(StatusTarefa.CONCLUIDA);
        repository.atualizar(t);
    }

    /**
     * Define status PENDENTE novamente.
     */
    public void reabrirTarefa(int id)
            throws RepositoryException {

        Tarefa t = repository.buscarPorId(id);
        t.setStatus(StatusTarefa.PENDENTE);
        repository.atualizar(t);
    }

    // ================== REMOÇÃO ==================

    public void removerTarefa(int id) throws RepositoryException {
        repository.remover(id);
    }

    // ================== CONSULTAS ==================

    public List<Tarefa> listarTodas() throws RepositoryException {
        return repository.listarTodas();
    }

    public List<Tarefa> listarPorResponsavel(String responsavel)
            throws RepositoryException {
        return repository.buscarPorResponsavel(responsavel);
    }

    public Tarefa buscarPorId(int id) throws RepositoryException {
        return repository.buscarPorId(id);
    }

    // ================== VALIDAÇÃO ==================

    private void validarDadosBasicos(
            String titulo,
            String disciplina,
            String responsavel,
            LocalDate dataLimite
    ) throws TarefaInvalidaException {

        if (titulo == null || titulo.isBlank()) {
            throw new TarefaInvalidaException("O título da tarefa é obrigatório.");
        }

        if (disciplina == null || disciplina.isBlank()) {
            throw new TarefaInvalidaException("A disciplina é obrigatória.");
        }

        if (responsavel == null || responsavel.isBlank()) {
            throw new TarefaInvalidaException("O responsável é obrigatório.");
        }

        if (dataLimite != null && dataLimite.isBefore(LocalDate.now())) {
            throw new TarefaInvalidaException("A data limite não pode ser no passado.");
        }
    }

}
