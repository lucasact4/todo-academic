package br.ufrpe.todoacademic.model;

import java.io.Serializable;

public class Usuario implements Serializable {
    private static final long serialVersionUID = 1L; // Versão da classe para serialização

    private String nome;
    private String login;
    private String senha;
    private TipoUsuario tipo;

    public Usuario(String nome, String login, String senha, TipoUsuario tipo) {
        this.nome = nome;
        this.login = login;
        this.senha = senha;
        this.tipo = tipo;
    }

    public String getNome() { return nome; }
    public String getLogin() { return login; }
    public String getSenha() { return senha; }
    public TipoUsuario getTipo() { return tipo; }
    
    @Override
    public String toString() { return nome; }
}