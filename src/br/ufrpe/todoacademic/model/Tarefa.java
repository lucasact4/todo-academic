package br.ufrpe.todoacademic.model;

import java.time.LocalDate;

public abstract class Tarefa {

    // id interno usado pelo repositório em memória
    private int id;

    private String titulo;
    private String descricao;
    private String disciplina;   // ex.: Programação II, Didática
    private String responsavel;  // ex.: Lucas, Guilherme
    private String notas;

    private LocalDate dataCriacao;
    private LocalDate dataLimite;
    private StatusTarefa status;

    // Construtor básico: toda tarefa já nasce como pendente na data atual
    protected Tarefa() {
        this.dataCriacao = LocalDate.now();
        this.status = StatusTarefa.PENDENTE;
    }

    // Construtor usado pelos tipos concretos de tarefa
    protected Tarefa(String titulo,
                     String descricao,
                     String disciplina,
                     String responsavel,
                     String notas,
                     LocalDate dataLimite) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.disciplina = disciplina;
        this.responsavel = responsavel;
        this.notas = notas;
        this.dataLimite = dataLimite;
    }

    // Cada subtipo (Simples, Estudo, Prova...) implementa sua própria regra de prioridade
    public abstract int calcularPrioridade();

    // Usado para exibir o tipo da tarefa na interface
    public abstract String getTipo();

    public boolean estaConcluida() {
        return status == StatusTarefa.CONCLUIDA;
    }

    // Marca a tarefa como concluída (usado pelo botão "Concluir" da tela principal)
    public void marcarConcluida() {
        this.status = StatusTarefa.CONCLUIDA;
    }

    public void marcarPendente() {
        this.status = StatusTarefa.PENDENTE;
    }

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

    @Override
    public String toString() {
        return "[" + getTipo() + "] " + titulo +
               " (" + disciplina + " - " + responsavel + ")";
    }
}
