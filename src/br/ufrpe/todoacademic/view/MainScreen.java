package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.exception.RepositoryException;
import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.repository.TarefaRepositoryMemoria;
import br.ufrpe.todoacademic.service.TarefaService;
import br.ufrpe.todoacademic.util.TarefaTableModel;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class MainScreen extends JFrame {

    // --- PALETA DE CORES ---
    private final Color COLOR_PRIMARY_GREEN = new Color(0, 207, 138);
    private final Color COLOR_ACTION_BLUE = new Color(0, 123, 255);
    
    // Novas cores solicitadas (Tons pastéis/claros)
    // Amarelo claro para o fundo + Texto escuro para leitura
    private final Color COLOR_EDIT_BG = new Color(255, 240, 160); 
    private final Color COLOR_EDIT_TEXT = new Color(100, 80, 0); 

    // Vermelho claro para o fundo + Vermelho escuro para o texto
    private final Color COLOR_DELETE_BG = new Color(255, 215, 215); 
    private final Color COLOR_DELETE_TEXT = new Color(180, 40, 40);
    
    private final Color COLOR_BG_LIGHT = new Color(248, 249, 250);
    private final Color COLOR_TEXT_DARK = new Color(51, 51, 51);

    private final TarefaService tarefaService;

    private JPanel panelConteudo;
    private JTable tableTarefas;
    private TarefaTableModel tarefaTableModel;
    private JPanel panelEmptyList;

    public MainScreen() {
        this(new TarefaService(new TarefaRepositoryMemoria()));
    }

    public MainScreen(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
        initComponents();
        carregarTarefas();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
            // Define a cor de foco global para o azul (ou verde, conforme preferir)
            UIManager.put("Component.focusColor", new Color(0, 123, 255)); 
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
        } catch (Exception ex) {
            System.err.println("FlatLaf não encontrado. Usando padrão.");
        }

        java.awt.EventQueue.invokeLater(() -> new MainScreen().setVisible(true));
    }

    private void initComponents() {
        setTitle("TodoAcademic");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1000, 700));
        
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG_LIGHT);
        setContentPane(root);

        root.add(createHeaderPanel(), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(COLOR_BG_LIGHT);
        centerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));
        
        centerPanel.add(createToolbarPanel(), BorderLayout.NORTH);
        centerPanel.add(createContentPanel(), BorderLayout.CENTER);
        
        root.add(centerPanel, BorderLayout.CENTER);
        root.add(createStatusBar(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
    }

    // --- PAINÉIS ---

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY_GREEN);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1));
        titleBlock.setOpaque(false);
        
        JLabel lblTitle = new JLabel("TodoAcademic");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);
        
        JLabel lblSub = new JLabel("Programação II · UFRPE");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(255, 255, 255, 200));

        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        JLabel lblGroup = new JLabel("<html>Grupo:<br/><b>Lucas · Guilherme · Sofia · Julia</b></html>");
        lblGroup.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblGroup.setForeground(Color.WHITE);
        lblGroup.setHorizontalAlignment(SwingConstants.RIGHT);

        header.add(titleBlock, BorderLayout.WEST);
        header.add(lblGroup, BorderLayout.EAST);

        return header;
    }

    private JPanel createToolbarPanel() {
        JPanel toolbar = new JPanel(new BorderLayout());
        toolbar.setBackground(COLOR_BG_LIGHT);
        toolbar.setOpaque(false);

        JLabel lblSection = new JLabel("Minhas Tarefas");
        lblSection.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblSection.setForeground(COLOR_TEXT_DARK);

        // FlowLayout com gap maior entre botões (15px)
        JPanel buttonGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonGroup.setOpaque(false);

        // 1. Botão Nova Tarefa (Verde)
        JButton btnNova = createButton("Nova Tarefa", "/resources/add.png", COLOR_PRIMARY_GREEN, Color.WHITE);
        
        // 2. Botão Editar (Agora Amarelo Claro)
        // Usa o fundo amarelo claro e texto marrom/escuro
        JButton btnEditar = createButton("Editar", "/resources/edit.png", COLOR_EDIT_BG, COLOR_EDIT_TEXT);
        
        // 3. Botão Concluir (Azul)
        JButton btnConcluir = createButton("Concluir", "/resources/check.png", COLOR_ACTION_BLUE, Color.WHITE);
        
        // 4. Botão Excluir (Agora Vermelho Claro)
        // Usa fundo vermelho claro e texto vermelho escuro (mantém o alerta de perigo, mas suave)
        JButton btnExcluir = createButton("Excluir", "/resources/delete.png", COLOR_DELETE_BG, COLOR_DELETE_TEXT);

        // Listeners
        btnNova.addActionListener(e -> onNovaTarefa());
        btnEditar.addActionListener(e -> onEditarTarefa());
        btnConcluir.addActionListener(e -> onConcluirTarefa());
        btnExcluir.addActionListener(e -> onExcluirTarefa());

        buttonGroup.add(btnNova);
        buttonGroup.add(btnEditar);
        buttonGroup.add(btnConcluir);
        buttonGroup.add(btnExcluir);

        toolbar.add(lblSection, BorderLayout.WEST);
        toolbar.add(buttonGroup, BorderLayout.EAST);

        return toolbar;
    }

    /**
     * Método auxiliar refatorado para maior controle de cores e alinhamento
     */
    private JButton createButton(String text, String iconPath, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        
        try {
            btn.setIcon(new ImageIcon(getClass().getResource(iconPath)));
        } catch (Exception e) {}

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        
        // Cores personalizadas
        btn.setBackground(bgColor);
        btn.setForeground(fgColor);
        
        // Se o fundo for branco, adiciona uma borda cinza suave para definição
        if (bgColor.equals(Color.WHITE)) {
             btn.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        } else {
             // Remove borda de botões coloridos para visual "Flat"
             btn.setBorderPainted(false);
        }

        // --- MELHORIAS DE TAMANHO E ALINHAMENTO ---
        
        // 1. Tamanho fixo maior
        btn.setPreferredSize(new Dimension(150, 45)); 
        
        // 2. Alinha tudo à esquerda dentro do botão
        btn.setHorizontalAlignment(SwingConstants.LEFT); 
        
        // 3. Margem interna (Padding) para o ícone não colar na borda esquerda
        // Topo, Esquerda, Baixo, Direita
        btn.setMargin(new Insets(0, 15, 0, 0)); 
        
        // 4. Espaço entre o ícone e o texto
        btn.setIconTextGap(12); 

        return btn;
    }

    private JPanel createContentPanel() {
        panelConteudo = new JPanel(new CardLayout());
        panelConteudo.setOpaque(false);

        // Tabela
        tarefaTableModel = new TarefaTableModel();
        tableTarefas = new JTable(tarefaTableModel);
        estilizarTabela(tableTarefas);

        JScrollPane scroll = new JScrollPane(tableTarefas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        // Empty State
        panelEmptyList = new JPanel(new GridBagLayout());
        panelEmptyList.setBackground(Color.WHITE);
        panelEmptyList.setBorder(BorderFactory.createLineBorder(new Color(230,230,230), 1));
        
        JLabel lblEmpty = new JLabel("<html><center><h2>Nenhuma tarefa por aqui! :D</h2><br/>Clique em <b>'Nova Tarefa'</b> para começar.</center></html>");
        lblEmpty.setForeground(new Color(150, 150, 150));
        panelEmptyList.add(lblEmpty);

        panelConteudo.add(scroll, "TABELA");
        panelConteudo.add(panelEmptyList, "EMPTY");

        return panelConteudo;
    }
    
    private void estilizarTabela(JTable table) {
        table.setRowHeight(40);
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Cabeçalho
        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setForeground(new Color(100, 100, 100));
                lbl.setBackground(new Color(248, 249, 250));
                lbl.setBorder(new EmptyBorder(0, 10, 0, 0));
                lbl.setHorizontalAlignment(JLabel.LEFT);
                return lbl;
            }
        });
        
        // Células
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                
                if (isSelected) {
                    c.setBackground(new Color(220, 245, 230));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(Color.WHITE);
                    c.setForeground(COLOR_TEXT_DARK);
                }
                return c;
            }
        });
    }

    private JPanel createStatusBar() {
        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
        status.setBackground(Color.WHITE);
        status.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230,230,230)));
        
        JLabel lblStatus = new JLabel(" Sistema pronto. Gerenciador acadêmico v1.0");
        lblStatus.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStatus.setForeground(Color.GRAY);
        status.add(lblStatus);
        
        return status;
    }

    // --- LÓGICA ---

    private void carregarTarefas() {
        CardLayout cl = (CardLayout) panelConteudo.getLayout();
        try {
            List<Tarefa> tarefas = tarefaService.listarTodas();
            tarefaTableModel.setTarefas(tarefas);

            if (tarefas.isEmpty()) {
                cl.show(panelConteudo, "EMPTY");
            } else {
                cl.show(panelConteudo, "TABELA");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
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