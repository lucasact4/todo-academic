package br.ufrpe.todoacademic.app;

import br.ufrpe.todoacademic.repository.TarefaRepository;
import br.ufrpe.todoacademic.repository.TarefaRepositoryMemoria;
import br.ufrpe.todoacademic.service.TarefaService;
import br.ufrpe.todoacademic.view.MainScreen;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;

public class TodoAcademicApp {

    public static void main(String[] args) {

        // aplica o tema FlatLaf antes de criar qualquer componente Swing
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            UIManager.put("Component.focusColor", new java.awt.Color(0, 123, 255));
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
        } catch (Exception e) {
            // se não conseguir aplicar o tema, segue com o padrão da JVM
            e.printStackTrace();
        }

        // camada de dados em memória (simula um "banco" de tarefas)
        TarefaRepository repository = new TarefaRepositoryMemoria();

        // camada de regras de negócio
        TarefaService tarefaService = new TarefaService(repository);

        // inicializa a interface gráfica na Event Dispatch Thread
        java.awt.EventQueue.invokeLater(() -> {
            MainScreen frame = new MainScreen(tarefaService);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
