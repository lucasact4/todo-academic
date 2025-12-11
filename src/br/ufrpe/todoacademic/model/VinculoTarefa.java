package br.ufrpe.todoacademic.model;

import java.io.Serializable;

public class VinculoTarefa implements Serializable {
    private static final long serialVersionUID = 1L;

    private Usuario aluno;
    private String observacao;
    private boolean concluido;
    private String prioridadeManual; // "Baixa", "Média", "Alta"

    public VinculoTarefa(Usuario aluno) {
        this.aluno = aluno;
        this.observacao = "";
        this.concluido = false;
        this.prioridadeManual = "Média";
    }

    // Getters e Setters
    public Usuario getAluno() { return aluno; }
    public void setAluno(Usuario aluno) { this.aluno = aluno; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }

    public boolean isConcluido() { return concluido; }
    public void setConcluido(boolean concluido) { this.concluido = concluido; }

    public String getPrioridadeManual() { return prioridadeManual; }
    public void setPrioridadeManual(String prioridadeManual) { this.prioridadeManual = prioridadeManual; }
    
    @Override
    public String toString() {
        return aluno.getNome();
    }
}