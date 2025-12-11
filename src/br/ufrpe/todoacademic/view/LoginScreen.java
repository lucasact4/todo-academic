package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.model.Usuario;
import br.ufrpe.todoacademic.service.AuthService;
import br.ufrpe.todoacademic.service.TarefaService;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class LoginScreen extends JFrame {

    private final AuthService authService;
    private final TarefaService tarefaService;
    
    private JTextField txtLogin;
    private JPasswordField txtSenha;

    // Cores
    private final Color COLOR_PRIMARY = new Color(0, 153, 102);
    private final Color COLOR_BG_LEFT = new Color(0, 153, 102);
    private final Color COLOR_BG_RIGHT = Color.WHITE;

    public LoginScreen(TarefaService tarefaService) {
        this.tarefaService = tarefaService;
        this.authService = new AuthService();
        initComponents();
    }

    private void initComponents() {
        setTitle("Login - TodoAcademic");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setSize(850, 550); // Tamanho um pouco maior e widescreen
        
        JPanel panelPrincipal = new JPanel(new GridLayout(1, 2)); // Divide a tela em 2 metades iguais
        setContentPane(panelPrincipal);

        // --- LADO ESQUERDO (Verde / Branding) ---
        JPanel panelLeft = new JPanel(new GridBagLayout());
        panelLeft.setBackground(COLOR_BG_LEFT);
        
        JPanel brandBlock = new JPanel(new BorderLayout());
        brandBlock.setOpaque(false);
        
        JLabel lblLogo = new JLabel();
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Tenta carregar ícone grande
        try {
            java.net.URL imgUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/tick.png");
            if (imgUrl != null) {
                // Redimensiona se necessário (opcional, aqui assume que o icone é bom)
                ImageIcon icon = new ImageIcon(imgUrl);
                // Escala simples se for muito grande
                if(icon.getIconWidth() > 100) {
                    Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                    icon = new ImageIcon(img);
                }
                lblLogo.setIcon(icon);
            }
        } catch (Exception e) {}

        JLabel lblTitle = new JLabel("TodoAcademic", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(new EmptyBorder(15, 0, 0, 0));

        JLabel lblSub = new JLabel("Gerenciador Acadêmico", SwingConstants.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(255, 255, 255, 200));

        brandBlock.add(lblLogo, BorderLayout.CENTER);
        
        JPanel textBlock = new JPanel(new GridLayout(2, 1));
        textBlock.setOpaque(false);
        textBlock.add(lblTitle);
        textBlock.add(lblSub);
        
        brandBlock.add(textBlock, BorderLayout.SOUTH);
        
        panelLeft.add(brandBlock); // Centraliza o bloco na tela verde

        // --- LADO DIREITO (Branco / Form) ---
        JPanel panelRight = new JPanel(new GridBagLayout());
        panelRight.setBackground(COLOR_BG_RIGHT);
        
        // Container do formulário para manter largura controlada
        JPanel formContainer = new JPanel(new GridBagLayout());
        formContainer.setOpaque(false);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        // 1. Título "Bem-vindo"
        JLabel lblBemVindo = new JLabel("Bem-vindo(a)!");
        lblBemVindo.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblBemVindo.setForeground(new Color(50, 50, 50));
        formContainer.add(lblBemVindo, gbc);
        
        // 2. Subtítulo
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 25, 0); // Espaço maior abaixo
        JLabel lblDesc = new JLabel("Insira suas credenciais para continuar.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setForeground(Color.GRAY);
        formContainer.add(lblDesc, gbc);

        // 3. Campo Usuário
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        formContainer.add(new JLabel("Usuário"), gbc);
        
        gbc.gridy++;
        txtLogin = createTextField();
        txtLogin.putClientProperty("JTextField.placeholderText", "Digite seu login");
        try {
            java.net.URL iconUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/user.png");
            if(iconUrl != null) txtLogin.putClientProperty("JTextField.leadingIcon", new ImageIcon(iconUrl));
        } catch(Exception e){}
        formContainer.add(txtLogin, gbc);

        // 4. Campo Senha
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 5, 0); // Espaço extra antes do label senha
        formContainer.add(new JLabel("Senha"), gbc);
        
        gbc.gridy++;
        gbc.insets = new Insets(5, 0, 5, 0);
        txtSenha = createPasswordField();
        txtSenha.putClientProperty("JTextField.placeholderText", "••••••");
        try {
            java.net.URL iconUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/lock.png");
            if(iconUrl != null) txtSenha.putClientProperty("JTextField.leadingIcon", new ImageIcon(iconUrl));
        } catch(Exception e){}
        formContainer.add(txtSenha, gbc);

        // 5. Botão Entrar
        gbc.gridy++;
        gbc.insets = new Insets(30, 0, 0, 0); // Espaço grande antes do botão
        JButton btnEntrar = new JButton("ENTRAR");
        btnEntrar.setBackground(COLOR_PRIMARY);
        btnEntrar.setForeground(Color.WHITE);
        btnEntrar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnEntrar.setFocusPainted(false);
        btnEntrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntrar.setPreferredSize(new Dimension(280, 45)); // Botão largo e alto
        
        try {
            java.net.URL iconUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/login_icon.png");
            if(iconUrl != null) btnEntrar.setIcon(new ImageIcon(iconUrl));
        } catch(Exception e){}
        
        btnEntrar.addActionListener(e -> fazerLogin());
        formContainer.add(btnEntrar, gbc);

        // Adiciona o formContainer ao painel direito (centralizado)
        panelRight.add(formContainer);

        // Adiciona os dois lados ao painel principal
        panelPrincipal.add(panelLeft);
        panelPrincipal.add(panelRight);
        
        getRootPane().setDefaultButton(btnEntrar);
        setLocationRelativeTo(null);
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField(20);
        txt.setPreferredSize(new Dimension(100, 40)); // Altura 40px
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return txt;
    }
    
    private JPasswordField createPasswordField() {
        JPasswordField txt = new JPasswordField(20);
        txt.setPreferredSize(new Dimension(100, 40)); // Altura 40px
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        return txt;
    }

    private void fazerLogin() {
        String login = txtLogin.getText();
        String senha = new String(txtSenha.getPassword());

        Usuario usuario = authService.autenticar(login, senha);

        if (usuario != null) {
            abrirMainScreen(usuario);
        } else {
            JOptionPane.showMessageDialog(this, "Usuário ou senha inválidos.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirMainScreen(Usuario usuario) {
        this.dispose();
        EventQueue.invokeLater(() -> {
            MainScreen main = new MainScreen(tarefaService, usuario);
            main.setVisible(true);
        });
    }
}