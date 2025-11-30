package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.repository.TarefaRepositoryMemoria;
import br.ufrpe.todoacademic.exception.RepositoryException;
import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.service.TarefaService;
import br.ufrpe.todoacademic.util.TarefaTableModel;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class MainScreen extends JFrame {

    private final TarefaService tarefaService;

    private JPanel panelConteudo;
    private JPanel panelEmptyList;
    private JScrollPane scrollPaneTarefas;
    private JTable tableTarefas;
    private TarefaTableModel tarefaTableModel;

    private JLabel labelEmptyTitle;
    private JLabel labelEmptySubtitle;

    // construtor sem args (usado pelo main / NetBeans)
    public MainScreen() {
        this(new TarefaService(new TarefaRepositoryMemoria()));
    }

    // construtor principal com injeção do service
    public MainScreen(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
        initComponents();
        carregarTarefas();
    }

    public static void main(String[] args) {
        // Tenta usar Nimbus para ficar mais moderno
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ignored) {}

        java.awt.EventQueue.invokeLater(() -> new MainScreen().setVisible(true));
    }

    private void initComponents() {
        setTitle("TodoAcademic - Gerenciador de Tarefas");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 600));

        // Painel raiz com margem e fundo cinza-claro
        JPanel root = new JPanel();
        root.setBackground(new Color(245, 245, 245));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);

        layout.setHorizontalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(root, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(root, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        // ================== TOOLBAR SUPERIOR ==================
        JPanel panelToolbar = new JPanel();
        panelToolbar.setBackground(new Color(0, 153, 102));
        panelToolbar.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel labelLogo = new JLabel();
        // logo tipo tick.png
        labelLogo.setIcon(new ImageIcon(getClass().getResource("/resources/tick.png")));
        labelLogo.setBorder(new EmptyBorder(0, 0, 0, 10));

        JLabel labelTitulo = new JLabel("TodoAcademic");
        labelTitulo.setForeground(Color.WHITE);
        labelTitulo.setFont(new Font("Segoe UI", Font.BOLD, 30));

        JLabel labelSubTitulo = new JLabel("Tarefas acadêmicas do grupo em um só lugar");
        labelSubTitulo.setForeground(new Color(230, 255, 241));
        labelSubTitulo.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JLabel labelInfoDisciplina = new JLabel("Programação II · UFRPE");
        labelInfoDisciplina.setForeground(new Color(230, 255, 241));
        labelInfoDisciplina.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel panelTitulo = new JPanel();
        panelTitulo.setOpaque(false);
        GroupLayout glTitulo = new GroupLayout(panelTitulo);
        panelTitulo.setLayout(glTitulo);
        glTitulo.setHorizontalGroup(
                glTitulo.createParallelGroup(Alignment.LEADING)
                        .addComponent(labelTitulo)
                        .addComponent(labelSubTitulo)
                        .addComponent(labelInfoDisciplina)
        );
        glTitulo.setVerticalGroup(
                glTitulo.createSequentialGroup()
                        .addComponent(labelTitulo)
                        .addGap(3)
                        .addComponent(labelSubTitulo)
                        .addGap(2)
                        .addComponent(labelInfoDisciplina)
        );

        JLabel labelGrupo = new JLabel("Grupo: Lucas · Guilherme · Sofia · Julia");
        labelGrupo.setForeground(Color.WHITE);
        labelGrupo.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        GroupLayout glToolbar = new GroupLayout(panelToolbar);
        panelToolbar.setLayout(glToolbar);
        glToolbar.setHorizontalGroup(
                glToolbar.createSequentialGroup()
                        .addComponent(labelLogo)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(panelTitulo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(labelGrupo)
        );
        glToolbar.setVerticalGroup(
                glToolbar.createParallelGroup(Alignment.CENTER)
                        .addComponent(labelLogo)
                        .addComponent(panelTitulo, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelGrupo)
        );

        // ================== HEADER "TAREFAS" + BOTÕES ==================
        JPanel panelHeaderTarefas = new JPanel();
        panelHeaderTarefas.setBackground(Color.WHITE);
        panelHeaderTarefas.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(8, 12, 8, 12)
        ));

        JLabel labelTarefas = new JLabel("Tarefas");
        labelTarefas.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelTarefas.setForeground(new Color(0, 153, 102));

        JButton btnNova = new JButton(" Nova tarefa");
        JButton btnEditar = new JButton(" Editar");
        JButton btnConcluir = new JButton(" Concluir");
        JButton btnExcluir = new JButton(" Excluir");

        // Ícones nos botões
        btnNova.setIcon(new ImageIcon(getClass().getResource("/resources/add.png")));
        btnEditar.setIcon(new ImageIcon(getClass().getResource("/resources/edit.png")));
        btnConcluir.setIcon(new ImageIcon(getClass().getResource("/resources/check.png")));
        btnExcluir.setIcon(new ImageIcon(getClass().getResource("/resources/delete.png")));

        JButton[] allButtons = {btnNova, btnEditar, btnConcluir, btnExcluir};
        for (JButton b : allButtons) {
            b.setFocusPainted(false);
            b.setBackground(Color.WHITE);
            b.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
            b.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        btnNova.setBackground(new Color(0, 153, 102));
        btnNova.setForeground(Color.WHITE);
        btnNova.setBorder(BorderFactory.createLineBorder(new Color(0, 130, 88)));

        btnNova.addActionListener(e -> onNovaTarefa());
        btnEditar.addActionListener(e -> onEditarTarefa());
        btnConcluir.addActionListener(e -> onConcluirTarefa());
        btnExcluir.addActionListener(e -> onExcluirTarefa());

        GroupLayout glHeader = new GroupLayout(panelHeaderTarefas);
        panelHeaderTarefas.setLayout(glHeader);
        glHeader.setHorizontalGroup(
                glHeader.createSequentialGroup()
                        .addComponent(labelTarefas)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnNova)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEditar)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnConcluir)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnExcluir)
        );
        glHeader.setVerticalGroup(
                glHeader.createParallelGroup(Alignment.CENTER)
                        .addComponent(labelTarefas)
                        .addComponent(btnNova)
                        .addComponent(btnEditar)
                        .addComponent(btnConcluir)
                        .addComponent(btnExcluir)
        );

        // ================== PAINEL CENTRAL ==================
        panelConteudo = new JPanel();
        panelConteudo.setBackground(Color.WHITE);
        panelConteudo.setBorder(new EmptyBorder(10, 0, 0, 0));
        panelConteudo.setLayout(new BorderLayout());

        // Tabela
        tarefaTableModel = new TarefaTableModel();
        tableTarefas = new JTable(tarefaTableModel);
        tableTarefas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableTarefas.setRowHeight(30);
        tableTarefas.setShowVerticalLines(false);
        tableTarefas.setGridColor(Color.WHITE);
        tableTarefas.setSelectionBackground(new Color(204, 255, 204));
        tableTarefas.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        tableTarefas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tableTarefas.getTableHeader().setOpaque(false);
        tableTarefas.getTableHeader().setBackground(new Color(0, 153, 102));
        tableTarefas.getTableHeader().setForeground(Color.WHITE);

        scrollPaneTarefas = new JScrollPane(tableTarefas);
        scrollPaneTarefas.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));

        // Painel "lista vazia" com ícone
        panelEmptyList = new JPanel();
        panelEmptyList.setBackground(Color.WHITE);
        panelEmptyList.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        panelEmptyList.setLayout(new GridBagLayout());

        JLabel labelEmptyIcon = new JLabel();
        labelEmptyIcon.setIcon(new ImageIcon(getClass().getResource("/resources/lists.png")));

        labelEmptyTitle = new JLabel("Nenhuma tarefa por aqui! :D");
        labelEmptyTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        labelEmptyTitle.setForeground(new Color(0, 153, 102));
        labelEmptyTitle.setHorizontalAlignment(SwingConstants.CENTER);

        labelEmptySubtitle = new JLabel("Clique em \"Nova tarefa\" para começar a organizar o grupo.");
        labelEmptySubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelEmptySubtitle.setForeground(new Color(130, 130, 130));
        labelEmptySubtitle.setHorizontalAlignment(SwingConstants.CENTER);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 20, 5, 20);

        gbc.gridy = 0;
        panelEmptyList.add(labelEmptyIcon, gbc);
        gbc.gridy = 1;
        panelEmptyList.add(labelEmptyTitle, gbc);
        gbc.gridy = 2;
        panelEmptyList.add(labelEmptySubtitle, gbc);

        panelConteudo.add(panelEmptyList, BorderLayout.CENTER);

        // ================== STATUS BAR ==================
        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBackground(Color.WHITE);
        statusBar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JLabel statusLeft = new JLabel("Projeto acadêmico · Programação II · NetBeans + Swing");
        statusLeft.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLeft.setBorder(new EmptyBorder(2, 8, 2, 8));
        statusBar.add(statusLeft, BorderLayout.WEST);

        // ================== MONTANDO ROOT ==================
        GroupLayout glRoot = new GroupLayout(root);
        root.setLayout(glRoot);
        glRoot.setHorizontalGroup(
                glRoot.createParallelGroup(Alignment.LEADING)
                        .addComponent(panelToolbar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelHeaderTarefas, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelConteudo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(statusBar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        glRoot.setVerticalGroup(
                glRoot.createSequentialGroup()
                        .addComponent(panelToolbar, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelHeaderTarefas, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(panelConteudo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(statusBar, GroupLayout.PREFERRED_SIZE, 24, GroupLayout.PREFERRED_SIZE)
        );

        pack();
        setLocationRelativeTo(null);
    }

    // ================== AÇÕES ==================

    private void carregarTarefas() {
        try {
            List<Tarefa> tarefas = tarefaService.listarTodas();
            tarefaTableModel.setTarefas(tarefas);
            mostrarTabela(!tarefas.isEmpty());
        } catch (RepositoryException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao carregar tarefas: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarTabela(boolean temTarefas) {
        panelConteudo.removeAll();
        if (temTarefas) {
            panelConteudo.add(scrollPaneTarefas, BorderLayout.CENTER);
        } else {
            panelConteudo.add(panelEmptyList, BorderLayout.CENTER);
        }
        panelConteudo.revalidate();
        panelConteudo.repaint();
    }

    private void onNovaTarefa() {
        TarefaFormDialog dialog = new TarefaFormDialog(this, tarefaService);
        dialog.setOnTarefaSalva(this::carregarTarefas);
        dialog.setVisible(true);
    }

    private void onEditarTarefa() {
        int selectedRow = tableTarefas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma tarefa na tabela.",
                    "Nenhuma tarefa selecionada",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Tarefa t = tarefaTableModel.getTarefa(selectedRow);
        if (t == null) {
            JOptionPane.showMessageDialog(this,
                    "Não foi possível obter a tarefa selecionada.",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        TarefaFormDialog dialog = new TarefaFormDialog(this, tarefaService, t);
        dialog.setOnTarefaSalva(this::carregarTarefas);
        dialog.setVisible(true);
    }

    private void onConcluirTarefa() {
        int selectedRow = tableTarefas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma tarefa na tabela.",
                    "Nenhuma tarefa selecionada",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Tarefa t = tarefaTableModel.getTarefa(selectedRow);
        try {
            tarefaService.concluirTarefa(t.getId());
            carregarTarefas();
        } catch (RepositoryException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao concluir tarefa: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onExcluirTarefa() {
        int selectedRow = tableTarefas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Selecione uma tarefa na tabela.",
                    "Nenhuma tarefa selecionada",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Tarefa t = tarefaTableModel.getTarefa(selectedRow);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir a tarefa \"" + t.getTitulo() + "\"?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                tarefaService.removerTarefa(t.getId());
                carregarTarefas();
            } catch (RepositoryException e) {
                JOptionPane.showMessageDialog(this,
                        "Erro ao excluir tarefa: " + e.getMessage(),
                        "Erro",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
