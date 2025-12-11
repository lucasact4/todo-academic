package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.model.TipoUsuario;
import br.ufrpe.todoacademic.model.Usuario;
import br.ufrpe.todoacademic.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class UsuarioListScreen extends JFrame {

    private final Usuario usuarioLogado;
    private final AuthService authService;
    private JTable tableUsuarios;
    private DefaultTableModel tableModel;

    // Cores (Mesma paleta da MainScreen)
    private final Color COLOR_HEADER = new Color(0, 153, 102);
    private final Color BTN_ADD_BG = new Color(225, 240, 255);
    private final Color BTN_ADD_FG = new Color(0, 80, 180);
    private final Color BTN_DEL_BG = new Color(255, 230, 230);
    private final Color BTN_DEL_FG = new Color(200, 40, 40);

    public UsuarioListScreen(Usuario usuarioLogado) {
        this.usuarioLogado = usuarioLogado;
        this.authService = new AuthService();
        initComponents();
        carregarUsuarios();
    }

    private void initComponents() {
        setTitle("Gerenciamento de Usuários");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(900, 600); // Aumentei um pouco para ficar mais espaçoso
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_HEADER);
        header.setBorder(new EmptyBorder(25, 30, 25, 30));

        JLabel lblTitle = new JLabel(usuarioLogado.getTipo() == TipoUsuario.ADMIN ? " Gestão de Usuários" : " Meus Alunos");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);
        
        // Ícone do Header
        try {
            java.net.URL imgUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/group.png");
            if (imgUrl != null) lblTitle.setIcon(new ImageIcon(imgUrl));
        } catch (Exception e) {}

        header.add(lblTitle, BorderLayout.CENTER);
        root.add(header, BorderLayout.NORTH);

        // --- Tabela ---
        String[] colunas = {"Nome Completo", "Login de Acesso", "Tipo de Conta"};
        tableModel = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tableUsuarios = new JTable(tableModel);
        estilizarTabela(tableUsuarios); // Aplica o visual bonito e centralizado

        JScrollPane scroll = new JScrollPane(tableUsuarios);
        scroll.setBorder(new EmptyBorder(20, 30, 20, 30)); // Margem ao redor da tabela
        scroll.getViewport().setBackground(Color.WHITE);
        root.add(scroll, BorderLayout.CENTER);

        // --- Botões ---
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 20));
        panelButtons.setBackground(Color.WHITE);
        panelButtons.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JButton btnNovo = createButton("Novo Usuário", "/br/ufrpe/todoacademic/resources/user_add.png", BTN_ADD_BG, BTN_ADD_FG);
        JButton btnExcluir = createButton("Excluir Selecionado", "/br/ufrpe/todoacademic/resources/user_delete.png", BTN_DEL_BG, BTN_DEL_FG);

        btnNovo.addActionListener(e -> abrirFormulario());
        btnExcluir.addActionListener(e -> excluirUsuario());

        panelButtons.add(btnNovo);
        panelButtons.add(btnExcluir);

        root.add(panelButtons, BorderLayout.SOUTH);
    }

    private void estilizarTabela(JTable table) {
        table.setRowHeight(45); // Linhas mais altas
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setFillsViewportHeight(true);

        // Cabeçalho
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(new Color(248, 249, 250));
        header.setForeground(new Color(80, 80, 80));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)));
        
        // --- CENTRALIZAÇÃO ---
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        // Aplica centralização em todas as colunas
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        // Renderizador customizado para o Header também ficar centralizado
        ((DefaultTableCellRenderer)table.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER);
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
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setIconTextGap(10);
        
        // Tamanho mínimo para ficarem bonitos
        btn.setPreferredSize(new Dimension(200, 45));
        
        return btn;
    }

    private void carregarUsuarios() {
        tableModel.setRowCount(0);
        List<Usuario> lista;

        if (usuarioLogado.getTipo() == TipoUsuario.ADMIN) {
            lista = authService.listarTodos();
        } else {
            lista = authService.listarAlunos();
        }

        for (Usuario u : lista) {
            tableModel.addRow(new Object[]{u.getNome(), u.getLogin(), u.getTipo()});
        }
    }

    private void abrirFormulario() {
        UsuarioFormDialog dialog = new UsuarioFormDialog(this, authService, usuarioLogado);
        dialog.setOnSalvo(this::carregarUsuarios);
        dialog.setVisible(true);
    }

    private void excluirUsuario() {
        int row = tableUsuarios.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um usuário na tabela para excluir.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String login = (String) tableUsuarios.getValueAt(row, 1);
        
        if (login.equals(usuarioLogado.getLogin())) {
            JOptionPane.showMessageDialog(this, "Você não pode excluir sua própria conta!", "Ação Negada", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this, 
                "Tem certeza que deseja remover o usuário '" + login + "'?\nEssa ação não pode ser desfeita.", 
                "Confirmar Exclusão", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
        
        if (opt == JOptionPane.YES_OPTION) {
            authService.removerUsuario(login);
            carregarUsuarios();
            JOptionPane.showMessageDialog(this, "Usuário removido com sucesso.");
        }
    }
}