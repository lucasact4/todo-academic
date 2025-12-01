package br.ufrpe.todoacademic.exception;

// Exceção genérica para erros na camada de repositório
public class RepositoryException extends Exception {

    public RepositoryException(String message) {
        super(message);
    }

    public RepositoryException(String message, Throwable cause) {
        super(message, cause);
    }
}
