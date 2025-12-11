package br.ufrpe.todoacademic.app;

import br.ufrpe.todoacademic.repository.TarefaRepository;
import br.ufrpe.todoacademic.repository.TarefaRepositoryArquivo; // USAR O DE ARQUIVO
import br.ufrpe.todoacademic.service.TarefaService;
import br.ufrpe.todoacademic.view.LoginScreen;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

public class TodoAcademicApp {

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Component.focusColor", new java.awt.Color(0, 123, 255));
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ALTERAÇÃO AQUI: Usando Repositório de Arquivo
        TarefaRepository repository = new TarefaRepositoryArquivo();
        
        TarefaService tarefaService = new TarefaService(repository);

        java.awt.EventQueue.invokeLater(() -> {
            LoginScreen loginScreen = new LoginScreen(tarefaService);
            loginScreen.setVisible(true);
        });
    }
}