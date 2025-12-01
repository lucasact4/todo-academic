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
            String tipoEsperado, String titulo, String descricao,
            String disciplina, String responsavel, String notas, LocalDate dataLimite
    ) throws TarefaInvalidaException, RepositoryException {

        validarDadosBasicos(titulo, disciplina, responsavel, dataLimite);
        
        Tarefa tarefa = instanciarTarefa(tipoEsperado, titulo, descricao, disciplina, responsavel, notas, dataLimite);
        
        repository.adicionar(tarefa);
        return tarefa;
    }
    
    private Tarefa instanciarTarefa(String tipoTexto, String titulo, String desc, String disc, String resp, String notas, LocalDate data) {
        String tipo = tipoTexto != null ? tipoTexto.trim().toLowerCase() : "";

        switch (tipo) {
            // --- TIPO: ESTUDO ---
            case "estudo":
                return new TarefaEstudo(titulo, desc, disc, resp, notas, data);

            // --- TIPO: PROVA (Agora tem classe própria) ---
            case "prova":
                return new TarefaProva(titulo, desc, disc, resp, notas, data);

            // --- TIPO: TRABALHO EM GRUPO ---
            case "trabalho em grupo":
            case "trabalho":
                return new TarefaTrabalhoGrupo(titulo, desc, disc, resp, notas, data);

            // --- TIPO: APRESENTAÇÃO (Agora tem classe própria) ---
            case "apresentação":
            case "apresentacao":
                return new TarefaApresentacao(titulo, desc, disc, resp, notas, data);

            // --- DEFAULT: SIMPLES ---
            case "simples":
            default:
                return new TarefaSimples(titulo, desc, disc, resp, notas, data);
        }
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

    public void atualizarTarefa(Tarefa tarefaExistente, String novoTipo)
            throws TarefaInvalidaException, RepositoryException {

        if (tarefaExistente == null) {
            throw new TarefaInvalidaException("Tarefa não pode ser nula.");
        }

        // Valida os dados que já foram setados no objeto
        validarDadosBasicos(
                tarefaExistente.getTitulo(),
                tarefaExistente.getDisciplina(),
                tarefaExistente.getResponsavel(),
                tarefaExistente.getDataLimite()
        );

        // Verifica se o tipo mudou
        // (Ignora case para comparar "Prova" com "prova")
        if (tarefaExistente.getTipo().equalsIgnoreCase(novoTipo)) {
            // Se o tipo é o mesmo, apenas atualiza os dados normais
            repository.atualizar(tarefaExistente);
        } else {
            // SE O TIPO MUDOU: Precisamos criar um objeto NOVO da classe certa
            
            // 1. Cria a nova instância com os dados atualizados
            Tarefa novaTarefa = instanciarTarefa(
                    novoTipo,
                    tarefaExistente.getTitulo(),
                    tarefaExistente.getDescricao(),
                    tarefaExistente.getDisciplina(),
                    tarefaExistente.getResponsavel(),
                    tarefaExistente.getNotas(),
                    tarefaExistente.getDataLimite()
            );

            // 2. IMPORTANTE: Copiar os dados de sistema (ID, Criação, Status) do antigo para o novo
            novaTarefa.setId(tarefaExistente.getId()); 
            novaTarefa.setDataCriacao(tarefaExistente.getDataCriacao());
            novaTarefa.setStatus(tarefaExistente.getStatus());

            // 3. Manda o repositório atualizar (ele vai substituir o objeto antigo pelo novo baseando-se no ID)
            repository.atualizar(novaTarefa);
        }
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
