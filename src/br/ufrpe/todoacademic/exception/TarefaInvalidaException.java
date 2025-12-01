package br.ufrpe.todoacademic.exception;

// Exceção usada para erros de validação de dados da tarefa
public class TarefaInvalidaException extends Exception {

    public TarefaInvalidaException(String message) {
        super(message);
    }

    public TarefaInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
