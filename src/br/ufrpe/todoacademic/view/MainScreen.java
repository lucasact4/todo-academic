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
import javax.swing.table.TableColumn;
import java.awt.*;
import java.util.List;

public class MainScreen extends JFrame {

    private final Color COLOR_HEADER_BG = new Color(0, 153, 102);
    private final Color COLOR_BG_LIGHT = new Color(248, 249, 250);

    private final Color BTN_NEW_BG = new Color(225, 240, 255);
    private final Color BTN_NEW_FG = new Color(0, 80, 180);

    private final Color BTN_VIEW_BG = new Color(240, 240, 245);
    private final Color BTN_VIEW_FG = new Color(70, 70, 90);

    private final Color BTN_EDIT_BG = new Color(255, 248, 220);
    private final Color BTN_EDIT_FG = new Color(180, 90, 0);

    private final Color BTN_DONE_BG = new Color(225, 255, 235);
    private final Color BTN_DONE_FG = new Color(0, 120, 60);

    private final Color BTN_DEL_BG  = new Color(255, 230, 230);
    private final Color BTN_DEL_FG  = new Color(200, 40, 40);

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

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER_BG);
        header.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel titleBlock = new JPanel(new GridLayout(2, 1));
        titleBlock.setOpaque(false);

        JLabel lblTitle = new JLabel("TodoAcademic");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);

        java.net.URL imgUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/tick.png");
        if (imgUrl != null) {
            lblTitle.setIcon(new ImageIcon(imgUrl));
            lblTitle.setIconTextGap(15);
        }

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
        JPanel toolbar = new JPanel();
        toolbar.setLayout(new BoxLayout(toolbar, BoxLayout.LINE_AXIS));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230,230,230)),
            new EmptyBorder(15, 0, 15, 0)
        ));

        JButton btnNova = createButton("Nova Tarefa", "/br/ufrpe/todoacademic/resources/add.png", BTN_NEW_BG, BTN_NEW_FG);
        btnNova.addActionListener(e -> abrirFormularioNovaTarefa());

        JButton btnVisualizar = createButton("Visualizar", "/br/ufrpe/todoacademic/resources/eye.png", BTN_VIEW_BG, BTN_VIEW_FG);
        btnVisualizar.addActionListener(e -> onVisualizarTarefa());

        JButton btnEditar = createButton("Editar", "/br/ufrpe/todoacademic/resources/edit.png", BTN_EDIT_BG, BTN_EDIT_FG);
        btnEditar.addActionListener(e -> onEditarTarefa());

        JButton btnConcluir = createButton("Concluir", "/br/ufrpe/todoacademic/resources/check.png", BTN_DONE_BG, BTN_DONE_FG);
        btnConcluir.addActionListener(e -> onConcluirTarefa());

        JButton btnExcluir = createButton("Excluir", "/br/ufrpe/todoacademic/resources/delete.png", BTN_DEL_BG, BTN_DEL_FG);
        btnExcluir.addActionListener(e -> onExcluirTarefa());

        toolbar.add(Box.createHorizontalGlue());

        toolbar.add(btnNova);
        toolbar.add(Box.createHorizontalStrut(15));

        toolbar.add(btnVisualizar);
        toolbar.add(Box.createHorizontalStrut(15));

        toolbar.add(btnEditar);
        toolbar.add(Box.createHorizontalStrut(15));

        toolbar.add(btnConcluir);
        toolbar.add(Box.createHorizontalStrut(15));

        toolbar.add(btnExcluir);

        toolbar.add(Box.createHorizontalGlue());

        return toolbar;
    }

    private JButton createButton(String text, String iconPath, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);

        try {
            java.net.URL imgUrl = getClass().getResource(iconPath);
            if(imgUrl != null) {
                btn.setIcon(new ImageIcon(imgUrl));
            }
        } catch (Exception e) {}

        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.setBackground(bgColor);
        btn.setForeground(fgColor);

        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);

        btn.setBorder(new EmptyBorder(8, 20, 8, 20));
        btn.setIconTextGap(10);

        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 45));

        return btn;
    }

    private JPanel createContentPanel() {
        panelConteudo = new JPanel(new CardLayout());
        panelConteudo.setOpaque(false);

        tarefaTableModel = new TarefaTableModel();
        tableTarefas = new JTable(tarefaTableModel);
        estilizarTabela(tableTarefas);

        JScrollPane scroll = new JScrollPane(tableTarefas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        panelEmptyList = new JPanel(new GridBagLayout());
        panelEmptyList.setBackground(Color.WHITE);
        panelEmptyList.setBorder(BorderFactory.createLineBorder(new Color(230,230,230), 1));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;

        java.net.URL imgUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/lists.png");
        if (imgUrl != null) {
            JLabel lblImage = new JLabel(new ImageIcon(imgUrl));
            gbc.insets = new Insets(0, 0, 20, 0);
            panelEmptyList.add(lblImage, gbc);
        }

        JLabel lblEmpty = new JLabel("<html><center><h2>Nenhuma tarefa por aqui! :D</h2><br/>Clique em <b>'Nova Tarefa'</b> para começar.</center></html>");
        lblEmpty.setForeground(new Color(150, 150, 150));

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        panelEmptyList.add(lblEmpty, gbc);

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

        if (table.getColumnCount() > 0) {
            TableColumn colId = table.getColumnModel().getColumn(0);
            colId.setMinWidth(50);
            colId.setMaxWidth(50);
            colId.setPreferredWidth(50);
        }

        if (table.getColumnCount() > 0) {
            int lastIndex = table.getColumnModel().getColumnCount() - 1;
            TableColumn colPrioridade = table.getColumnModel().getColumn(lastIndex);
            colPrioridade.setMinWidth(80);
            colPrioridade.setMaxWidth(80);
            colPrioridade.setPreferredWidth(80);
        }

        JTableHeader header = table.getTableHeader();
        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setForeground(new Color(100, 100, 100));
                lbl.setBackground(new Color(248, 249, 250));
                lbl.setBorder(new EmptyBorder(0, 10, 0, 10));

                int lastIdx = table.getColumnModel().getColumnCount() - 1;
                if (column == 0 || column == lastIdx) {
                    lbl.setHorizontalAlignment(JLabel.CENTER);
                } else {
                    lbl.setHorizontalAlignment(JLabel.LEFT);
                }
                return lbl;
            }
        });

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (c instanceof JLabel) {
                    JLabel lbl = (JLabel) c;
                    lbl.setBorder(new EmptyBorder(0, 10, 0, 10));

                    int lastIdx = table.getColumnModel().getColumnCount() - 1;

                    if (column == 0) {
                        lbl.setHorizontalAlignment(JLabel.CENTER);
                    } else if (column == lastIdx) {
                        lbl.setHorizontalAlignment(JLabel.CENTER);
                        try {
                            int prioridade = Integer.parseInt(value.toString());
                            lbl.setFont(lbl.getFont().deriveFont(Font.BOLD));

                            if (prioridade >= 4) {
                                lbl.setForeground(new Color(220, 53, 69));
                            } else if (prioridade == 3) {
                                lbl.setForeground(new Color(255, 193, 7));
                            } else {
                                lbl.setForeground(new Color(40, 167, 69));
                            }
                        } catch (Exception e) {}
                    } else {
                        lbl.setHorizontalAlignment(JLabel.LEFT);
                    }

                    if (isSelected) {
                        lbl.setForeground(Color.BLACK);
                    }
                }

                if (isSelected) {
                    c.setBackground(new Color(220, 245, 230));
                } else {
                    c.setBackground(Color.WHITE);
                }
                return c;
            }
        };

        table.setDefaultRenderer(Object.class, cellRenderer);
        table.setDefaultRenderer(Integer.class, cellRenderer);
        table.setDefaultRenderer(Long.class, cellRenderer);
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

    private void abrirFormularioNovaTarefa() {
        TarefaFormDialog dialog = new TarefaFormDialog(this, tarefaService);
        dialog.setOnTarefaSalva(this::carregarTarefas);
        dialog.setVisible(true);
    }

    private void onVisualizarTarefa() {
        int selectedRow = tableTarefas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa para visualizar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableTarefas.getValueAt(selectedRow, 0);
        try {
            Tarefa t = tarefaService.buscarPorId(id);
            TarefaFormDialog dialog = new TarefaFormDialog(this, tarefaService, t);
            dialog.ativarModoLeitura();
            dialog.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar tarefa: " + e.getMessage());
        }
    }

    private void onEditarTarefa() {
        int selectedRow = tableTarefas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableTarefas.getValueAt(selectedRow, 0);
        try {
             Tarefa t = tarefaService.buscarPorId(id);
             TarefaFormDialog dialog = new TarefaFormDialog(this, tarefaService, t);
             dialog.setOnTarefaSalva(this::carregarTarefas);
             dialog.setVisible(true);
        } catch(Exception e) {
             JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void onConcluirTarefa() {
        int selectedRow = tableTarefas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableTarefas.getValueAt(selectedRow, 0);
        try {
            tarefaService.concluirTarefa(id);
            carregarTarefas();
        } catch (RepositoryException e) {
            JOptionPane.showMessageDialog(this, "Erro ao concluir tarefa: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onExcluirTarefa() {
        int selectedRow = tableTarefas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Selecione uma tarefa na tabela.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int id = (int) tableTarefas.getValueAt(selectedRow, 0);

        Tarefa t = null;
        try { t = tarefaService.buscarPorId(id); } catch(Exception e){}
        String nome = (t != null) ? t.getTitulo() : "Selecionada";

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Tem certeza que deseja excluir a tarefa \"" + nome + "\"?",
                "Confirmar exclusão",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                tarefaService.removerTarefa(id);
                carregarTarefas();
            } catch (RepositoryException e) {
                JOptionPane.showMessageDialog(this, "Erro ao excluir tarefa: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}