package br.ufrpe.todoacademic.model;

import java.time.LocalDate;

/**
 * Classe base abstrata para qualquer tipo de tarefa do sistema.
 *
 * Subclasses exemplos:
 *  - TarefaSimples
 *  - TarefaEstudo
 *  - TarefaTrabalhoGrupo
 */
public abstract class Tarefa {

    private int id;                    // gerenciado pelo repositório em memória
    private String titulo;             // equivalente ao "name" do Task antigo
    private String descricao;
    private String disciplina;         // ex.: Programação II, Didática...
    private String responsavel;        // ex.: Lucas, Guilherme...
    private String notas;              // observações adicionais

    private LocalDate dataCriacao;
    private LocalDate dataLimite;
    private StatusTarefa status;

    // ------------- Construtores -------------

    /**
     * Construtor padrão: define dataCriacao como hoje e status PENDENTE.
     */
    protected Tarefa() {
        this.dataCriacao = LocalDate.now();
        this.status = StatusTarefa.PENDENTE;
    }

    /**
     * Construtor completo (sem id; o id será definido pelo repositório).
     */
    protected Tarefa(String titulo,
                     String descricao,
                     String disciplina,
                     String responsavel,
                     String notas,
                     LocalDate dataLimite) {
        this(); // chama o construtor padrão
        this.titulo = titulo;
        this.descricao = descricao;
        this.disciplina = disciplina;
        this.responsavel = responsavel;
        this.notas = notas;
        this.dataLimite = dataLimite;
    }

    // ------------- Métodos abstratos (polimorfismo) -------------

    /**
     * Cada subtipo calcula a prioridade de forma diferente.
     * Ex.: tarefas de estudo podem considerar dificuldade, trabalhos em grupo peso da nota etc.
     */
    public abstract int calcularPrioridade();

    /**
     * Retorna o "tipo" da tarefa (Simples, Estudo, Trabalho em Grupo, ...).
     */
    public abstract String getTipo();

    // ------------- Métodos de apoio -------------

    public boolean estaConcluida() {
        return status == StatusTarefa.CONCLUIDA;
    }

    public void marcarConcluida() {
        this.status = StatusTarefa.CONCLUIDA;
    }

    public void marcarPendente() {
        this.status = StatusTarefa.PENDENTE;
    }

    // ------------- Getters / Setters -------------

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDisciplina() {
        return disciplina;
    }

    public void setDisciplina(String disciplina) {
        this.disciplina = disciplina;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }

    public String getNotas() {
        return notas;
    }

    public void setNotas(String notas) {
        this.notas = notas;
    }

    public LocalDate getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDate dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDate getDataLimite() {
        return dataLimite;
    }

    public void setDataLimite(LocalDate dataLimite) {
        this.dataLimite = dataLimite;
    }

    public StatusTarefa getStatus() {
        return status;
    }

    public void setStatus(StatusTarefa status) {
        this.status = status;
    }

    // ------------- Representação textual (útil em debug / listas simples) -------------

    @Override
    public String toString() {
        return "[" + getTipo() + "] " + titulo +
               " (" + disciplina + " - " + responsavel + ")";
    }
}
