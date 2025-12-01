package br.ufrpe.todoacademic.service;

import br.ufrpe.todoacademic.exception.RepositoryException;
import br.ufrpe.todoacademic.exception.TarefaInvalidaException;
import br.ufrpe.todoacademic.model.StatusTarefa;
import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.model.TarefaSimples;
import br.ufrpe.todoacademic.repository.TarefaRepository;
import br.ufrpe.todoacademic.model.TarefaEstudo;
import br.ufrpe.todoacademic.model.TarefaTrabalhoGrupo;
import br.ufrpe.todoacademic.model.TarefaProva;
import br.ufrpe.todoacademic.model.TarefaApresentacao;

import java.time.LocalDate;
import java.util.List;

// Camada de serviço: faz a ponte entre a interface gráfica e o repositório
public class TarefaService {

    private final TarefaRepository repository;

    public TarefaService(TarefaRepository repository) {
        this.repository = repository;
    }

    // ============= CADASTRO =============

    // cria a tarefa do tipo correto (Simples, Estudo, Prova...) e salva no repositório
    public Tarefa cadastrarTarefa(
            String tipoEsperado, String titulo, String descricao,
            String disciplina, String responsavel, String notas, LocalDate dataLimite
    ) throws TarefaInvalidaException, RepositoryException {

        validarDadosBasicos(titulo, disciplina, responsavel, dataLimite);

        Tarefa tarefa = instanciarTarefa(
                tipoEsperado, titulo, descricao, disciplina, responsavel, notas, dataLimite
        );

        repository.adicionar(tarefa);
        return tarefa;
    }

    // escolhe qual implementação de Tarefa criar com base no texto do tipo
    private Tarefa instanciarTarefa(String tipoTexto,
                                    String titulo,
                                    String desc,
                                    String disc,
                                    String resp,
                                    String notas,
                                    LocalDate data) {

        String tipo = tipoTexto != null ? tipoTexto.trim().toLowerCase() : "";

        switch (tipo) {
            case "estudo":
                return new TarefaEstudo(titulo, desc, disc, resp, notas, data);

            case "prova":
                return new TarefaProva(titulo, desc, disc, resp, notas, data);

            case "trabalho em grupo":
            case "trabalho":
                return new TarefaTrabalhoGrupo(titulo, desc, disc, resp, notas, data);

            case "apresentação":
            case "apresentacao":
                return new TarefaApresentacao(titulo, desc, disc, resp, notas, data);

            case "simples":
            default:
                return new TarefaSimples(titulo, desc, disc, resp, notas, data);
        }
    }

    // atalho específico para tarefa simples (usado se não precisar escolher tipo na tela)
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

    // ============= ATUALIZAÇÃO =============

    // atualiza uma tarefa e, se o tipo mudar, troca também a classe concreta
    public void atualizarTarefa(Tarefa tarefaExistente, String novoTipo)
            throws TarefaInvalidaException, RepositoryException {

        if (tarefaExistente == null) {
            throw new TarefaInvalidaException("Tarefa não pode ser nula.");
        }

        // valida os campos que já estão setados na tarefa
        validarDadosBasicos(
                tarefaExistente.getTitulo(),
                tarefaExistente.getDisciplina(),
                tarefaExistente.getResponsavel(),
                tarefaExistente.getDataLimite()
        );

        // se o tipo não mudou, só manda salvar de novo
        if (tarefaExistente.getTipo().equalsIgnoreCase(novoTipo)) {
            repository.atualizar(tarefaExistente);
        } else {
            // se o tipo mudou, criamos uma nova instância da classe correta
            Tarefa novaTarefa = instanciarTarefa(
                    novoTipo,
                    tarefaExistente.getTitulo(),
                    tarefaExistente.getDescricao(),
                    tarefaExistente.getDisciplina(),
                    tarefaExistente.getResponsavel(),
                    tarefaExistente.getNotas(),
                    tarefaExistente.getDataLimite()
            );

            // preserva dados de sistema
            novaTarefa.setId(tarefaExistente.getId());
            novaTarefa.setDataCriacao(tarefaExistente.getDataCriacao());
            novaTarefa.setStatus(tarefaExistente.getStatus());

            repository.atualizar(novaTarefa);
        }
    }

    // ============= STATUS =============

    // usado pelo botão "Concluir" da tela principal
    public void concluirTarefa(int id) throws RepositoryException {
        Tarefa t = repository.buscarPorId(id);
        t.setStatus(StatusTarefa.CONCLUIDA);
        repository.atualizar(t);
    }

    // reabre uma tarefa concluída (volta para pendente)
    public void reabrirTarefa(int id) throws RepositoryException {
        Tarefa t = repository.buscarPorId(id);
        t.setStatus(StatusTarefa.PENDENTE);
        repository.atualizar(t);
    }

    // ============= REMOÇÃO =============

    public void removerTarefa(int id) throws RepositoryException {
        repository.remover(id);
    }

    // ============= CONSULTAS =============

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

    // ============= VALIDAÇÃO =============

    // regras básicas para não deixar cadastrar/atualizar tarefa inválida
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
