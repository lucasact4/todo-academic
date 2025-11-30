package br.ufrpe.todoacademic.app;

import br.ufrpe.todoacademic.repository.TarefaRepository;
import br.ufrpe.todoacademic.repository.TarefaRepositoryMemoria;
import br.ufrpe.todoacademic.service.TarefaService;
import br.ufrpe.todoacademic.view.MainScreen;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

public class TodoAcademicApp {

    public static void main(String[] args) {

        // (opcional) configura o LookAndFeel parecido com o do projeto antigo
        try {
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) { // ou "Java Swing", se você preferir
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // se der erro, segue com o padrão
            e.printStackTrace();
        }

        // cria repositório em memória e o service
        TarefaRepository repository = new TarefaRepositoryMemoria();
        TarefaService tarefaService = new TarefaService(repository);

        // tudo de GUI deve rodar na EventQueue
        java.awt.EventQueue.invokeLater(() -> {
            MainScreen frame = new MainScreen(tarefaService); // nossa "MainScreen" nova
            frame.setLocationRelativeTo(null);              // centraliza na tela
            frame.setVisible(true);
        });
    }
}
