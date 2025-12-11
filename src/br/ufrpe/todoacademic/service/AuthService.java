package br.ufrpe.todoacademic.service;

import br.ufrpe.todoacademic.model.TipoUsuario;
import br.ufrpe.todoacademic.model.Usuario;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AuthService {
    
    private static final String ARQUIVO_USUARIOS = "usuarios_db.dat";
    private static List<Usuario> usuarios = new ArrayList<>();

    // Bloco estático: Roda assim que o programa inicia
    static {
        carregarUsuarios();
    }

    private static void carregarUsuarios() {
        File file = new File(ARQUIVO_USUARIOS);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                usuarios = (List<Usuario>) ois.readObject();
            } catch (Exception e) {
                usuarios = new ArrayList<>();
            }
        }
        if (usuarios.isEmpty()) {
            restaurarAdminPadrao();
        }
    }

    private static void salvarUsuarios() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_USUARIOS))) {
            oos.writeObject(usuarios);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // --- A CORREÇÃO ESTÁ AQUI: ADICIONADO 'static' ---
    public static void restaurarAdminPadrao() {
        usuarios.clear();
        usuarios.add(new Usuario("Super Admin", "admin", "admin", TipoUsuario.ADMIN));
        salvarUsuarios();
    }

    public Usuario autenticar(String login, String senha) {
        for (Usuario u : usuarios) {
            if (u.getLogin().equalsIgnoreCase(login) && u.getSenha().equals(senha)) {
                return u;
            }
        }
        return null; 
    }
    
    public void cadastrarUsuario(Usuario u) throws Exception {
        for (Usuario existente : usuarios) {
            if (existente.getLogin().equalsIgnoreCase(u.getLogin())) {
                throw new Exception("Login já existe!");
            }
        }
        usuarios.add(u);
        salvarUsuarios();
    }
    
    public void removerUsuario(String login) {
        if (usuarios.removeIf(u -> u.getLogin().equalsIgnoreCase(login))) {
            salvarUsuarios();
        }
    }
    
    public List<Usuario> listarTodos() {
        return new ArrayList<>(usuarios);
    }
    
    public List<Usuario> listarAlunos() {
        return usuarios.stream()
                .filter(u -> u.getTipo() == TipoUsuario.ALUNO)
                .collect(Collectors.toList());
    }
}