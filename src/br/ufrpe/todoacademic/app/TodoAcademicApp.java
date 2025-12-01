package br.ufrpe.todoacademic.app;

import br.ufrpe.todoacademic.repository.TarefaRepository;
import br.ufrpe.todoacademic.repository.TarefaRepositoryMemoria;
import br.ufrpe.todoacademic.service.TarefaService;
import br.ufrpe.todoacademic.view.MainScreen;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class TodoAcademicApp {

    public static void main(String[] args) {

        // tenta usar o tema Nimbus para deixar a interface mais agradável
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
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
