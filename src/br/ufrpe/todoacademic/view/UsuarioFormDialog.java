package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.model.TipoUsuario;
import br.ufrpe.todoacademic.model.Usuario;
import br.ufrpe.todoacademic.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class UsuarioFormDialog extends JDialog {

    private final AuthService authService;
    private final Usuario usuarioCriador;
    private Runnable onSalvo;

    // Componentes
    private JTextField txtNome;
    private JTextField txtLogin;
    private JPasswordField txtSenha;
    private JComboBox<TipoUsuario> cbTipo;

    // Cores
    private final Color COLOR_PRIMARY = new Color(0, 153, 102);
    private final Color COLOR_BG_FORM = Color.WHITE;

    public UsuarioFormDialog(Window owner, AuthService authService, Usuario usuarioCriador) {
        super(owner, "Novo Usuário", ModalityType.APPLICATION_MODAL);
        this.authService = authService;
        this.usuarioCriador = usuarioCriador;
        initComponents();
    }

    public void setOnSalvo(Runnable onSalvo) {
        this.onSalvo = onSalvo;
    }

    private void initComponents() {
        setSize(450, 580); // Um pouco mais alto para acomodar o layout espaçado
        setLocationRelativeTo(getOwner());
        setResizable(false);
        setLayout(new BorderLayout());

        add(createHeader(), BorderLayout.NORTH);
        add(createFormPanel(), BorderLayout.CENTER);
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_PRIMARY);
        header.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Novo Usuário");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(Color.WHITE);

        // Ícone do Header
        try {
            java.net.URL imgUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/user_add.png");
            if (imgUrl != null) lblTitle.setIcon(new ImageIcon(imgUrl));
            lblTitle.setIconTextGap(15);
        } catch (Exception e) {}

        String desc = (usuarioCriador.getTipo() == TipoUsuario.PROFESSOR)
                ? "Cadastrar um novo Aluno."
                : "Cadastrar Admin, Professor ou Aluno.";

        JLabel lblSub = new JLabel(desc);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(new Color(255, 255, 255, 200));

        textPanel.add(lblTitle);
        textPanel.add(lblSub);

        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_BG_FORM);
        form.setBorder(new EmptyBorder(30, 40, 30, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0); // Espaçamento entre campos

        // 1. Campo Nome
        txtNome = createTextField("Ex: João Silva");
        setFieldIcon(txtNome, "user.png");
        form.add(createFieldGroup("Nome Completo", txtNome), gbc);

        // 2. Campo Login
        gbc.gridy++;
        txtLogin = createTextField("Ex: joao.silva");
        setFieldIcon(txtLogin, "user.png");
        form.add(createFieldGroup("Login de Acesso", txtLogin), gbc);

        // 3. Campo Senha
        gbc.gridy++;
        txtSenha = createPasswordField();
        setFieldIcon(txtSenha, "lock.png");
        form.add(createFieldGroup("Senha", txtSenha), gbc);

        // 4. Combo Tipo
        gbc.gridy++;
        if (usuarioCriador.getTipo() == TipoUsuario.PROFESSOR) {
            cbTipo = new JComboBox<>(new TipoUsuario[]{TipoUsuario.ALUNO});
            cbTipo.setEnabled(false);
        } else {
            cbTipo = new JComboBox<>(TipoUsuario.values());
        }
        cbTipo.setPreferredSize(new Dimension(100, 40));
        cbTipo.setBackground(Color.WHITE);
        form.add(createFieldGroup("Tipo de Conta", cbTipo), gbc);

        return form;
    }

    // Cria um painel com Label em cima e Campo embaixo (Visual Moderno)
    private JPanel createFieldGroup(String label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(COLOR_BG_FORM);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(80, 80, 80));

        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createTextField(String placeholder) {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(100, 40));
        txt.putClientProperty("JTextField.placeholderText", placeholder);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return txt;
    }

    private JPasswordField createPasswordField() {
        JPasswordField txt = new JPasswordField();
        txt.setPreferredSize(new Dimension(100, 40));
        txt.putClientProperty("JTextField.placeholderText", "******");
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return txt;
    }

    private void setFieldIcon(JTextField field, String iconName) {
        try {
            java.net.URL url = getClass().getResource("/br/ufrpe/todoacademic/resources/" + iconName);
            if (url != null) field.putClientProperty("JTextField.leadingIcon", new ImageIcon(url));
        } catch (Exception e) {}
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(110, 40));
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.addActionListener(e -> dispose());

        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setPreferredSize(new Dimension(110, 40));
        btnSalvar.setBackground(COLOR_PRIMARY);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvar.setFocusPainted(false);
        btnSalvar.addActionListener(e -> salvar());

        try {
            java.net.URL url = getClass().getResource("/br/ufrpe/todoacademic/resources/save.png");
            if (url != null) btnSalvar.setIcon(new ImageIcon(url));
        } catch(Exception e){}

        panel.add(btnCancelar);
        panel.add(btnSalvar);
        getRootPane().setDefaultButton(btnSalvar);

        return panel;
    }

    private void salvar() {
        String nome = txtNome.getText().trim();
        String login = txtLogin.getText().trim();
        String senha = new String(txtSenha.getPassword());
        TipoUsuario tipo = (TipoUsuario) cbTipo.getSelectedItem();

        if (nome.isEmpty() || login.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            Usuario novoUsuario = new Usuario(nome, login, senha, tipo);
            authService.cadastrarUsuario(novoUsuario);

            JOptionPane.showMessageDialog(this, "Usuário cadastrado com sucesso!");
            if (onSalvo != null) onSalvo.run();
            dispose();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}