package br.ufrpe.todoacademic.exception;

public class TarefaInvalidaException extends Exception {

    public TarefaInvalidaException(String message) {
        super(message);
    }

    public TarefaInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
