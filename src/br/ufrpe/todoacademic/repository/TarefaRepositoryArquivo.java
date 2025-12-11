package br.ufrpe.todoacademic.repository;

import br.ufrpe.todoacademic.exception.RepositoryException;
import br.ufrpe.todoacademic.model.Tarefa;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TarefaRepositoryArquivo implements TarefaRepository {

    private static final String ARQUIVO_DB = "tarefas_db.dat";
    private List<Tarefa> tarefas;
    private int proximoId = 1;

    public TarefaRepositoryArquivo() {
        this.tarefas = new ArrayList<>();
        carregarDoArquivo();
    }

    private void carregarDoArquivo() {
        File file = new File(ARQUIVO_DB);
        if (!file.exists()) return;

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            this.tarefas = (List<Tarefa>) ois.readObject();
            this.proximoId = tarefas.stream().mapToInt(Tarefa::getId).max().orElse(0) + 1;
        } catch (Exception e) {
            this.tarefas = new ArrayList<>();
        }
    }

    private void salvarNoArquivo() throws RepositoryException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DB))) {
            oos.writeObject(this.tarefas);
        } catch (IOException e) {
            throw new RepositoryException("Erro ao salvar: " + e.getMessage());
        }
    }

    @Override
    public synchronized void adicionar(Tarefa tarefa) throws RepositoryException {
        if (tarefa == null) throw new RepositoryException("Tarefa nula");
        tarefa.setId(proximoId++);
        tarefas.add(tarefa);
        salvarNoArquivo();
    }

    @Override
    public synchronized void atualizar(Tarefa tarefa) throws RepositoryException {
        int idx = encontrarIndicePorId(tarefa.getId());
        if (idx == -1) throw new RepositoryException("Tarefa não encontrada");
        tarefas.set(idx, tarefa);
        salvarNoArquivo();
    }

    @Override
    public synchronized void remover(int id) throws RepositoryException {
        int idx = encontrarIndicePorId(id);
        if (idx == -1) throw new RepositoryException("Tarefa não encontrada");
        tarefas.remove(idx);
        salvarNoArquivo();
    }

    @Override
    public synchronized Tarefa buscarPorId(int id) throws RepositoryException {
        for (Tarefa t : tarefas) if (t.getId() == id) return t;
        throw new RepositoryException("Tarefa não encontrada");
    }

    @Override
    public synchronized List<Tarefa> listarTodas() {
        return new ArrayList<>(tarefas);
    }

    // --- AQUI ESTÁ A CORREÇÃO MÁGICA ---
    @Override
    public synchronized List<Tarefa> listarPorUsuario(String loginUsuario) {
        List<Tarefa> resultado = new ArrayList<>();
        if (loginUsuario == null) return resultado;

        for (Tarefa t : tarefas) {
            // 1. Sou o dono da tarefa? (Quem criou)
            boolean souDono = t.getUsuarioLogin() != null && t.getUsuarioLogin().equalsIgnoreCase(loginUsuario);
            
            // 2. Estou na lista de alunos vinculados?
            boolean estouVinculado = t.getVinculos().stream()
                    .anyMatch(v -> v.getAluno().getLogin().equalsIgnoreCase(loginUsuario));

            // Se for dono OU estiver vinculado, adiciona na lista
            if (souDono || estouVinculado) {
                resultado.add(t);
            }
        }
        return resultado;
    }
    // ------------------------------------

    @Override
    public synchronized List<Tarefa> buscarPorResponsavel(String resp) {
        List<Tarefa> res = new ArrayList<>();
        if (resp == null) return res;
        for (Tarefa t : tarefas) {
            if (t.getResponsavel() != null && t.getResponsavel().equalsIgnoreCase(resp)) res.add(t);
        }
        return res;
    }
    
    @Override
    public synchronized void limparTudo() throws RepositoryException {
        this.tarefas.clear();
        this.proximoId = 1;
        salvarNoArquivo();
    }

    private int encontrarIndicePorId(int id) {
        for (int i = 0; i < tarefas.size(); i++) {
            if (tarefas.get(i).getId() == id) return i;
        }
        return -1;
    }
}