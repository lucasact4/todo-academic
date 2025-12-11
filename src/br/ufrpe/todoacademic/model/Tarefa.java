package br.ufrpe.todoacademic.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class Tarefa implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String usuarioLogin;
    private String titulo;
    private String descricao;
    private String disciplina;
    private String responsavel;
    private String notas;
    private LocalDate dataCriacao;
    private LocalDate dataLimite;
    private StatusTarefa status;
    
    private List<VinculoTarefa> vinculos = new ArrayList<>();

    public List<VinculoTarefa> getVinculos() {
        if (vinculos == null) vinculos = new ArrayList<>();
        return vinculos;
    }

    public void setVinculos(List<VinculoTarefa> vinculos) {
        this.vinculos = vinculos;
    }

    public void adicionarVinculo(VinculoTarefa vinculo) {
        getVinculos().add(vinculo);
    }

    public void removerVinculo(VinculoTarefa vinculo) {
        getVinculos().remove(vinculo);
    }

    protected Tarefa() {
        this.dataCriacao = LocalDate.now();
        this.status = StatusTarefa.PENDENTE;
    }

    protected Tarefa(String titulo, String descricao, String disciplina, 
                     String responsavel, String notas, LocalDate dataLimite) {
        this();
        this.titulo = titulo;
        this.descricao = descricao;
        this.disciplina = disciplina;
        this.responsavel = responsavel;
        this.notas = notas;
        this.dataLimite = dataLimite;
    }

    public abstract int calcularPrioridade();
    public abstract String getTipo();

    public boolean estaConcluida() { return status == StatusTarefa.CONCLUIDA; }
    public void marcarConcluida() { this.status = StatusTarefa.CONCLUIDA; }
    public void marcarPendente() { this.status = StatusTarefa.PENDENTE; }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsuarioLogin() { return usuarioLogin; }
    public void setUsuarioLogin(String usuarioLogin) { this.usuarioLogin = usuarioLogin; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public String getDisciplina() { return disciplina; }
    public void setDisciplina(String disciplina) { this.disciplina = disciplina; }
    public String getResponsavel() { return responsavel; }
    public void setResponsavel(String responsavel) { this.responsavel = responsavel; }
    public String getNotas() { return notas; }
    public void setNotas(String notas) { this.notas = notas; }
    public LocalDate getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDate dataCriacao) { this.dataCriacao = dataCriacao; }
    public LocalDate getDataLimite() { return dataLimite; }
    public void setDataLimite(LocalDate dataLimite) { this.dataLimite = dataLimite; }
    public StatusTarefa getStatus() { return status; }
    public void setStatus(StatusTarefa status) { this.status = status; }

    @Override
    public String toString() {
        return "[" + getTipo() + "] " + titulo + " (" + disciplina + ")";
    }
}