package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.exception.RepositoryException;
import br.ufrpe.todoacademic.exception.TarefaInvalidaException;
import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.model.Usuario;
import br.ufrpe.todoacademic.model.TipoUsuario;
import br.ufrpe.todoacademic.repository.TarefaRepositoryArquivo;
import br.ufrpe.todoacademic.service.AuthService; // Importante
import br.ufrpe.todoacademic.service.TarefaService;
import br.ufrpe.todoacademic.util.TarefaTableModel;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class MainScreen extends JFrame {

    private final Color COLOR_BG_LIGHT = new Color(248, 249, 250);

    // Cores dos Botões
    private final Color BTN_NEW_BG = new Color(225, 240, 255); private final Color BTN_NEW_FG = new Color(0, 80, 180);
    private final Color BTN_USERS_BG = new Color(240, 230, 255); private final Color BTN_USERS_FG = new Color(100, 50, 180);
    private final Color BTN_DEMO_BG = new Color(255, 245, 200); private final Color BTN_DEMO_FG = new Color(180, 100, 0);
    private final Color BTN_VIEW_BG = new Color(240, 240, 245); private final Color BTN_VIEW_FG = new Color(70, 70, 90);
    private final Color BTN_EDIT_BG = new Color(255, 248, 220); private final Color BTN_EDIT_FG = new Color(180, 90, 0);
    private final Color BTN_DONE_BG = new Color(225, 255, 235); private final Color BTN_DONE_FG = new Color(0, 120, 60);
    private final Color BTN_DEL_BG  = new Color(255, 230, 230); private final Color BTN_DEL_FG  = new Color(200, 40, 40);
    private final Color BTN_DISABLED_BG = new Color(230, 230, 230); private final Color BTN_DISABLED_FG = new Color(150, 150, 150);

    private final TarefaService tarefaService;
    private final Usuario usuarioLogado;
    private final AuthService authService; // Necessário para verificar o tipo do criador

    private JPanel panelConteudo;
    private JTable tableTarefas;
    private TarefaTableModel tarefaTableModel;
    private JPanel panelEmptyList;
    
    // Botões de Ação
    private JButton btnVisualizar, btnEditar, btnConcluir, btnExcluir;

    public MainScreen(TarefaService tarefaService, Usuario usuarioLogado) {
        this.tarefaService = tarefaService;
        this.usuarioLogado = usuarioLogado;
        this.authService = new AuthService(); // Inicializa auth
        initComponents();
        carregarTarefas();
        atualizarEstadoBotoes(); 
    }

    public MainScreen() {
        this(new TarefaService(new TarefaRepositoryArquivo()), 
             new Usuario("Teste Admin", "admin", "admin", TipoUsuario.ADMIN));
    }

    public static void main(String[] args) {
        try { UIManager.setLookAndFeel(new FlatLightLaf()); } catch (Exception ex) {}
        java.awt.EventQueue.invokeLater(() -> new MainScreen().setVisible(true));
    }

    private void initComponents() {
        setTitle("TodoAcademic - Logado como " + usuarioLogado.getNome());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1300, 800));

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG_LIGHT);
        setContentPane(root);

        root.add(new HeaderPanel(usuarioLogado, e -> logout()), BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(COLOR_BG_LIGHT);
        centerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        centerPanel.add(createTopToolbar(), BorderLayout.NORTH);
        centerPanel.add(createContentPanel(), BorderLayout.CENTER);
        centerPanel.add(createBottomActionsPanel(), BorderLayout.SOUTH);

        root.add(centerPanel, BorderLayout.CENTER);
        
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230,230,230)));
        JLabel lblStatus = new JLabel(" Sistema pronto. Gerenciador acadêmico v2.0");
        lblStatus.setForeground(Color.GRAY);
        footer.add(lblStatus);
        root.add(footer, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        configurarLimpezaSelecao(root, centerPanel);
    }

    // --- PAINÉIS DE BOTÕES ---

    private JPanel createTopToolbar() {
        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        toolbar.setBackground(Color.WHITE);
        toolbar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230,230,230)), new EmptyBorder(0, 0, 10, 0)));

        JButton btnNova = createButton("Nova Tarefa", "/br/ufrpe/todoacademic/resources/add.png", BTN_NEW_BG, BTN_NEW_FG);
        btnNova.addActionListener(e -> abrirFormularioNovaTarefa());
        toolbar.add(btnNova);
        
        if (usuarioLogado.getTipo() != TipoUsuario.ALUNO) {
            String labelBtn = (usuarioLogado.getTipo() == TipoUsuario.ADMIN) ? "Usuários" : "Meus Alunos";
            JButton btnUsers = createButton(labelBtn, "/br/ufrpe/todoacademic/resources/users.png", BTN_USERS_BG, BTN_USERS_FG);
            btnUsers.addActionListener(e -> new UsuarioListScreen(usuarioLogado).setVisible(true));
            toolbar.add(btnUsers);
        }

        if (usuarioLogado.getTipo() == TipoUsuario.ADMIN) {
            JButton btnDemo = createButton("Dados Demo", "/br/ufrpe/todoacademic/resources/info.png", BTN_DEMO_BG, BTN_DEMO_FG);
            btnDemo.addActionListener(e -> executarDemo());
            toolbar.add(btnDemo);
        }
        return toolbar;
    }

    private JPanel createBottomActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        panel.setBackground(COLOR_BG_LIGHT);
        
        btnVisualizar = createButton("Visualizar", "/br/ufrpe/todoacademic/resources/eye.png", BTN_VIEW_BG, BTN_VIEW_FG);
        btnVisualizar.addActionListener(e -> onVisualizarTarefa());

        btnEditar = createButton("Editar", "/br/ufrpe/todoacademic/resources/edit.png", BTN_EDIT_BG, BTN_EDIT_FG);
        btnEditar.addActionListener(e -> onEditarTarefa());

        btnConcluir = createButton("Concluir", "/br/ufrpe/todoacademic/resources/check.png", BTN_DONE_BG, BTN_DONE_FG);
        btnConcluir.addActionListener(e -> onConcluirTarefa());

        btnExcluir = createButton("Excluir", "/br/ufrpe/todoacademic/resources/delete.png", BTN_DEL_BG, BTN_DEL_FG);
        btnExcluir.addActionListener(e -> onExcluirTarefa());

        panel.add(btnVisualizar);
        panel.add(btnEditar);
        panel.add(btnConcluir);
        panel.add(btnExcluir);
        return panel;
    }

    // --- CONTEÚDO E TABELA ---

    private JPanel createContentPanel() {
        panelConteudo = new JPanel(new CardLayout());
        panelConteudo.setOpaque(false);
        
        tarefaTableModel = new TarefaTableModel();
        tableTarefas = new JTable(tarefaTableModel);
        configurarTabela();

        JScrollPane scroll = new JScrollPane(tableTarefas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);
        scroll.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                tableTarefas.clearSelection();
                atualizarEstadoBotoes();
            }
        });

        panelEmptyList = new JPanel(new GridBagLayout());
        panelEmptyList.setBackground(Color.WHITE);
        panelEmptyList.setBorder(BorderFactory.createLineBorder(new Color(230,230,230), 1));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        try{
            java.net.URL imgUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/lists.png");
            if (imgUrl != null) panelEmptyList.add(new JLabel(new ImageIcon(imgUrl)), gbc);
        }catch(Exception e){}
        JLabel lblEmpty = new JLabel("<html><center><h2>Nenhuma tarefa por aqui! :D</h2><br/>Clique em <b>'Nova Tarefa'</b> para começar.</center></html>");
        lblEmpty.setForeground(new Color(150, 150, 150));
        gbc.gridy = 1; gbc.insets = new Insets(20, 0, 0, 0);
        panelEmptyList.add(lblEmpty, gbc);

        panelConteudo.add(scroll, "TABELA");
        panelConteudo.add(panelEmptyList, "EMPTY");
        
        configurarLimpezaSelecao(scroll, panelEmptyList);
        return panelConteudo;
    }

    private void configurarTabela() {
        tableTarefas.setRowHeight(45);
        tableTarefas.setShowVerticalLines(false);
        tableTarefas.setGridColor(new Color(235, 235, 235));
        tableTarefas.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tableTarefas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableTarefas.setIntercellSpacing(new Dimension(0, 0));

        tableTarefas.getTableHeader().setDefaultRenderer(TarefaRenderers.createHeaderRenderer());
        tableTarefas.setDefaultRenderer(Object.class, TarefaRenderers.createCellRenderer());
        tableTarefas.setDefaultRenderer(Integer.class, TarefaRenderers.createCellRenderer());
        tableTarefas.setDefaultRenderer(Long.class, TarefaRenderers.createCellRenderer());

        tableTarefas.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) atualizarEstadoBotoes();
        });
    }

    // --- LÓGICA DE AÇÕES ---

    private void carregarTarefas() {
        CardLayout cl = (CardLayout) panelConteudo.getLayout();
        try {
            List<Tarefa> tarefas = tarefaService.listarTarefas(usuarioLogado);
            tarefaTableModel.setTarefas(tarefas);
            if (tarefas.isEmpty()) {
                cl.show(panelConteudo, "EMPTY");
                tableTarefas.clearSelection(); 
                atualizarEstadoBotoes();
            } else {
                cl.show(panelConteudo, "TABELA");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage());
        }
    }

    private void abrirFormularioNovaTarefa() {
        TarefaFormDialog dialog = new TarefaFormDialog(this, tarefaService, usuarioLogado);
        dialog.setOnTarefaSalva(this::carregarTarefas);
        dialog.setVisible(true);
    }

    private boolean podeExecutarAcao() {
        return tableTarefas.getSelectedRow() != -1;
    }

    private void onVisualizarTarefa() {
        if(!podeExecutarAcao()) return;
        int id = (int) tableTarefas.getValueAt(tableTarefas.getSelectedRow(), 0);
        try {
            Tarefa t = tarefaService.buscarPorId(id);
            TarefaFormDialog dialog = new TarefaFormDialog(this, tarefaService, usuarioLogado, t);
            dialog.ativarModoLeitura();
            dialog.setVisible(true);
        } catch (Exception e) {}
    }

    private void onEditarTarefa() {
        if(!verificarPermissaoEdicao()) return; // Validação
        int id = (int) tableTarefas.getValueAt(tableTarefas.getSelectedRow(), 0);
        try {
             Tarefa t = tarefaService.buscarPorId(id);
             TarefaFormDialog dialog = new TarefaFormDialog(this, tarefaService, usuarioLogado, t);
             dialog.setOnTarefaSalva(this::carregarTarefas);
             dialog.setVisible(true);
        } catch(Exception e) {}
    }

    private void onConcluirTarefa() {
        if(!verificarPermissaoEdicao()) return; // Validação
        int id = (int) tableTarefas.getValueAt(tableTarefas.getSelectedRow(), 0);
        try {
            tarefaService.concluirTarefa(id);
            carregarTarefas();
        } catch (RepositoryException e) {}
    }

    private void onExcluirTarefa() {
        if(!verificarPermissaoEdicao()) return; // Validação
        int id = (int) tableTarefas.getValueAt(tableTarefas.getSelectedRow(), 0);
        try {
            Tarefa t = tarefaService.buscarPorId(id);
            String nome = (t != null) ? t.getTitulo() : "Selecionada";
            int confirm = JOptionPane.showConfirmDialog(this, "Excluir tarefa \"" + nome + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                tarefaService.removerTarefa(id, usuarioLogado);
                carregarTarefas();
            }
        } catch (TarefaInvalidaException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Acesso Negado", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {}
    }

    private void executarDemo() {
        String[] options = {"Gerar Dados de Teste", "Limpar Tudo (Reset)", "Cancelar"};
        int escolha = JOptionPane.showOptionDialog(this, "Gerenciar Banco de Dados:", "Modo Demo", 
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        if (escolha == 0 || escolha == 1) {
            try {
                var demoService = new br.ufrpe.todoacademic.service.DemoDataService(tarefaService.getRepository());
                if (escolha == 0) {
                    demoService.gerarDadosDeTeste();
                    JOptionPane.showMessageDialog(this, "Dados gerados!");
                } else {
                    demoService.limparDados();
                    JOptionPane.showMessageDialog(this, "Sistema resetado!");
                }
                logout();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage());
            }
        }
    }

    // --- NOVA LÓGICA DE PERMISSÃO RIGOROSA ---

    // Método central que decide se o usuário tem permissão sobre a tarefa selecionada
    private boolean temPermissaoSobreTarefa(Tarefa t) {
        if (t == null) return false;

        // 1. Sou Admin? Pode tudo.
        if (usuarioLogado.getTipo() == TipoUsuario.ADMIN) return true;

        // 2. Sou o Criador da tarefa? Pode tudo.
        if (t.getResponsavel().equalsIgnoreCase(usuarioLogado.getNome())) return true;

        // 3. Sou Professor?
        if (usuarioLogado.getTipo() == TipoUsuario.PROFESSOR) {
            // Professor só pode editar tarefas criadas por ALUNOS.
            // Preciso descobrir quem criou essa tarefa.
            for (Usuario u : authService.listarTodos()) {
                if (u.getNome().equalsIgnoreCase(t.getResponsavel())) {
                    // Se o criador for ALUNO, o professor pode editar.
                    if (u.getTipo() == TipoUsuario.ALUNO) return true;
                    // Se for ADMIN ou OUTRO PROFESSOR, não pode.
                    else return false;
                }
            }
        }

        // Se não for Admin, não for o dono, e não cair na regra do prof -> Não pode.
        return false;
    }

    private void atualizarEstadoBotoes() {
        int selectedRow = tableTarefas.getSelectedRow();
        boolean temSelecao = selectedRow != -1;
        boolean temPermissao = false;

        if (temSelecao) {
            int modelRow = tableTarefas.convertRowIndexToModel(selectedRow);
            Tarefa t = tarefaTableModel.getTarefa(modelRow);
            temPermissao = temPermissaoSobreTarefa(t);
        }

        // Visualizar sempre permitido se tiver seleção
        configurarBotao(btnVisualizar, temSelecao, BTN_VIEW_BG, BTN_VIEW_FG, "Selecione uma tarefa.");
        
        // Botões de ação dependem da permissão
        String msgErro = temSelecao ? "Você não tem permissão para alterar esta tarefa." : "Selecione uma tarefa.";
        configurarBotao(btnEditar, temPermissao, BTN_EDIT_BG, BTN_EDIT_FG, msgErro);
        configurarBotao(btnConcluir, temPermissao, BTN_DONE_BG, BTN_DONE_FG, msgErro);
        configurarBotao(btnExcluir, temPermissao, BTN_DEL_BG, BTN_DEL_FG, msgErro);
    }

    // Validação extra caso o usuário tente burlar o botão desabilitado
    private boolean verificarPermissaoEdicao() {
        if (!podeExecutarAcao()) return false;
        int selectedRow = tableTarefas.getSelectedRow();
        Tarefa t = tarefaTableModel.getTarefa(tableTarefas.convertRowIndexToModel(selectedRow));
        
        if (!temPermissaoSobreTarefa(t)) {
            JOptionPane.showMessageDialog(this, "Você não tem permissão para alterar/excluir tarefas deste usuário.", 
                    "Acesso Negado", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void configurarBotao(JButton btn, boolean ativo, Color bg, Color fg, String tooltip) {
        if (ativo) {
            btn.setBackground(bg); btn.setForeground(fg); 
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR)); 
            btn.setToolTipText(null);
        } else {
            btn.setBackground(BTN_DISABLED_BG); btn.setForeground(BTN_DISABLED_FG); 
            btn.setCursor(new Cursor(Cursor.DEFAULT_CURSOR)); 
            btn.setToolTipText(tooltip);
        }
    }

    private JButton createButton(String text, String iconPath, Color bgColor, Color fgColor) {
        JButton btn = new JButton(text);
        try {
            java.net.URL imgUrl = getClass().getResource(iconPath);
            if(imgUrl != null) btn.setIcon(new ImageIcon(imgUrl));
        } catch (Exception e) {}
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBackground(bgColor); btn.setForeground(fgColor);
        btn.setFocusPainted(false); btn.setBorderPainted(false); btn.setOpaque(true);
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        btn.setIconTextGap(10);
        btn.setPreferredSize(new Dimension(170, 45));
        return btn;
    }

    private void configurarLimpezaSelecao(JComponent... componentes) {
        MouseAdapter limpador = new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (tableTarefas != null) {
                    tableTarefas.clearSelection();
                    atualizarEstadoBotoes();
                    rootPane.requestFocus();
                }
            }
        };
        for (JComponent c : componentes) c.addMouseListener(limpador);
    }

    private void logout() {
        this.dispose();
        java.awt.EventQueue.invokeLater(() -> new LoginScreen(this.tarefaService).setVisible(true));
    }
}