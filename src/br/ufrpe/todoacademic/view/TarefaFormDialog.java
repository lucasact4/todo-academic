package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.exception.TarefaInvalidaException;
import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.model.TipoUsuario;
import br.ufrpe.todoacademic.model.Usuario;
import br.ufrpe.todoacademic.service.AuthService;
import br.ufrpe.todoacademic.service.TarefaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TarefaFormDialog extends JDialog {

    private static final DateTimeFormatter FORMATTER_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // Cores
    private final Color COLOR_PRIMARY = new Color(0, 153, 102);
    private final Color COLOR_BG_HEADER = new Color(0, 153, 102);
    private final Color COLOR_BG_FORM = Color.WHITE;
    private final Color COLOR_SIDEBAR_BG = new Color(250, 250, 250);

    // Serviços e Modelos
    private final TarefaService tarefaService;
    private final Usuario usuarioLogado;
    private final AuthService authService;
    private Tarefa tarefaEdicao;
    private Runnable onTarefaSalva;

    // Componentes de UI
    private JButton btnSalvar;
    private JButton btnCancelar;
    private JButton btnCalendar;
    private JButton btnVinculos;

    private JLabel lblTitle;
    private JLabel lblSub;

    private JTextField txtTitulo;
    private JTextField txtDisciplina;
    private JComboBox<String> cbResponsavel; // Combo box para o Criador
    private JFormattedTextField txtDataLimite;
    private JTextField txtNotas;
    private JTextArea txtDescricao;
    private JComboBox<String> comboTipo;
    
    // OTIMIZAÇÃO: Componente separado
    private PrioridadeSidebar sidebarPrioridade; 

    // --- CONSTRUTORES ---

    public TarefaFormDialog(Frame parent, TarefaService tarefaService, Usuario usuarioLogado) {
        this(parent, tarefaService, usuarioLogado, null);
    }

    public TarefaFormDialog(Frame parent, TarefaService tarefaService, Usuario usuarioLogado, Tarefa tarefaEdicao) {
        super(parent, true);
        this.tarefaService = tarefaService;
        this.usuarioLogado = usuarioLogado;
        this.tarefaEdicao = tarefaEdicao;
        this.authService = new AuthService();
        
        initComponents();

        // OTIMIZAÇÃO: Chama o método do componente separado
        comboTipo.addActionListener(e -> {
            String tipo = (String) comboTipo.getSelectedItem();
            sidebarPrioridade.atualizarRegra(tipo);
        });
        
        carregarDadosSeEdicao();

        if (tarefaEdicao == null) {
            // Atualiza sidebar inicial
            sidebarPrioridade.atualizarRegra((String) comboTipo.getSelectedItem());
            
            // SELEÇÃO AUTOMÁTICA DO CRIADOR
            if(usuarioLogado != null) {
                cbResponsavel.setSelectedItem(usuarioLogado.getNome());
            }
        }
    }

    public void setOnTarefaSalva(Runnable onTarefaSalva) {
        this.onTarefaSalva = onTarefaSalva;
    }

    // --- MODO LEITURA ---
    public void ativarModoLeitura() {
        setTitle("Visualizar Tarefa");
        lblTitle.setText("Visualizar Tarefa");
        lblSub.setText("Detalhes completos da tarefa.");

        txtTitulo.setEditable(false);
        txtDisciplina.setEditable(false);
        cbResponsavel.setEnabled(false);
        txtDataLimite.setEditable(false);
        txtNotas.setEditable(false);
        txtDescricao.setEditable(false);
        comboTipo.setEnabled(false);
        if(btnCalendar != null) btnCalendar.setEnabled(false);

        if(btnVinculos != null) btnVinculos.setText("Ver Alunos");

        Color readOnlyColor = new Color(252, 252, 252);
        txtTitulo.setBackground(readOnlyColor);
        txtDisciplina.setBackground(readOnlyColor);
        txtDataLimite.setBackground(readOnlyColor);
        txtNotas.setBackground(readOnlyColor);
        txtDescricao.setBackground(readOnlyColor);

        txtTitulo.setBorder(null);
        txtDescricao.setBorder(null);
        txtDataLimite.setBorder(null);

        btnSalvar.setVisible(false);
        btnCancelar.setText("Fechar");
    }

    // --- INICIALIZAÇÃO DE COMPONENTES ---

    private void initComponents() {
        setTitle(tarefaEdicao == null ? "Nova Tarefa" : "Editar Tarefa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 650));
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG_FORM);
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);

        JPanel contentContainer = new JPanel(new BorderLayout());
        
        // OTIMIZAÇÃO: Instancia o componente separado aqui
        sidebarPrioridade = new PrioridadeSidebar();
        contentContainer.add(sidebarPrioridade, BorderLayout.WEST); 
        
        contentContainer.add(createFormPanel(), BorderLayout.CENTER);

        root.add(contentContainer, BorderLayout.CENTER);
        root.add(createButtonPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BG_HEADER);
        header.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        lblTitle = new JLabel(tarefaEdicao == null ? "Nova Tarefa" : "Editar Tarefa");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        String subText = tarefaEdicao == null ?
                "Preencha os detalhes para criar a nova tarefa." :
                "Altere os dados abaixo para atualizar a tarefa.";

        lblSub = new JLabel(subText);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(255, 255, 255, 200));

        textPanel.add(lblTitle);
        textPanel.add(lblSub);
        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    // --- FORMULÁRIO ---
    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_BG_FORM);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        txtTitulo = createTextField("Ex: Relatório Final de OO");
        txtDisciplina = createTextField("Ex: Programação II");
        
        // --- CAMPO RESP. CRIADOR (COMBOBOX) ---
        cbResponsavel = new JComboBox<>();
        cbResponsavel.setBackground(Color.WHITE);
        cbResponsavel.setPreferredSize(new Dimension(100, 40));
        
        java.util.List<Usuario> listaUsuarios = authService.listarTodos();
        for (Usuario u : listaUsuarios) {
            cbResponsavel.addItem(u.getNome());
        }
        
        // REGRA DE NEGÓCIO: Travamento do Campo Criador
        // Se for ALUNO ou PROFESSOR, trava no nome dele. Só ADMIN pode mudar.
        if (usuarioLogado.getTipo() == TipoUsuario.ALUNO || usuarioLogado.getTipo() == TipoUsuario.PROFESSOR) {
            cbResponsavel.setSelectedItem(usuarioLogado.getNome());
            cbResponsavel.setEnabled(false); // Travado
        }

        txtDataLimite = createFormattedDateField();
        txtNotas = createTextField("Ex: Vale nota; Entregar impresso...");

        comboTipo = new JComboBox<>(new String[]{"Simples", "Estudo", "Trabalho em Grupo", "Apresentação", "Prova"});
        comboTipo.setBackground(Color.WHITE);
        comboTipo.setPreferredSize(new Dimension(comboTipo.getPreferredSize().width, 35));

        txtDescricao = new JTextArea(5, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescricao.putClientProperty("JComponent.outline", "gray");
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);
        scrollDescricao.putClientProperty("JComponent.outline", "gray");

        // -- Layout Grid --
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0);
        form.add(createFieldGroup("Título da Tarefa *", txtTitulo), gbc);

        gbc.gridy = 1; gbc.gridwidth = 1; gbc.weightx = 0.5; gbc.insets = new Insets(0, 0, 15, 15);
        form.add(createFieldGroup("Disciplina *", txtDisciplina), gbc);

        gbc.gridx = 1; gbc.insets = new Insets(0, 0, 15, 0);
        // MUDANÇA NO TÍTULO DO CAMPO
        form.add(createFieldGroup("Resp. Criador *", cbResponsavel), gbc);

        gbc.gridy = 2; gbc.gridx = 0; gbc.insets = new Insets(0, 0, 15, 15);
        form.add(createFieldGroup("Tipo", comboTipo), gbc);

        // Data com calendário
        gbc.gridx = 1; gbc.insets = new Insets(0, 0, 15, 0);
        JPanel pnlData = new JPanel(new BorderLayout(5, 0));
        pnlData.setBackground(COLOR_BG_FORM);
        btnCalendar = new JButton("📅");
        btnCalendar.setPreferredSize(new Dimension(40, 40));
        btnCalendar.setFocusPainted(false);
        btnCalendar.setBackground(new Color(240, 240, 240));
        btnCalendar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCalendar.addActionListener(e -> abrirCalendario());
        pnlData.add(txtDataLimite, BorderLayout.CENTER);
        pnlData.add(btnCalendar, BorderLayout.EAST);
        form.add(createFieldGroup("Data Limite", pnlData), gbc);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; gbc.weightx = 1.0; gbc.insets = new Insets(0, 0, 15, 0);
        form.add(createFieldGroup("Notas Rápidas", txtNotas), gbc);

        gbc.gridy = 4; gbc.weighty = 1.0; gbc.insets = new Insets(0, 0, 0, 0);
        form.add(createFieldGroup("Descrição Detalhada", scrollDescricao), gbc);

        return form;
    }

    // --- MÉTODOS AUXILIARES ---

    private JFormattedTextField createFormattedDateField() {
        JFormattedTextField field;
        try {
            MaskFormatter mask = new MaskFormatter("##/##/####");
            mask.setPlaceholderCharacter('_');
            field = new JFormattedTextField(mask);
            field.setPreferredSize(new Dimension(100, 40));
            field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        } catch (Exception e) { field = new JFormattedTextField(); }
        return field;
    }

    private void abrirCalendario() {
        CalendarDialog cal = new CalendarDialog(this, dataSelecionada -> {
            txtDataLimite.setValue(dataSelecionada.format(FORMATTER_DATA));
        });
        cal.setVisible(true);
    }

    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBackground(COLOR_BG_FORM);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(new Color(80, 80, 80));
        panel.add(lbl, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }

    private JTextField createTextField(String placeholder) {
        JTextField txt = new JTextField();
        txt.setPreferredSize(new Dimension(10, 40));
        txt.putClientProperty("JTextField.placeholderText", placeholder);
        txt.putClientProperty("JTextField.showClearButton", true);
        return txt;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        if (tarefaEdicao != null) {
            btnVinculos = new JButton("Vínculos");
            btnVinculos.setPreferredSize(new Dimension(160, 35));
            btnVinculos.setBackground(new Color(230, 240, 255));
            btnVinculos.setForeground(new Color(0, 80, 180));
            btnVinculos.setFocusPainted(false);
            btnVinculos.setFont(new Font("Segoe UI", Font.BOLD, 13));
            try {
                java.net.URL imgUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/group.png");
                if(imgUrl != null) btnVinculos.setIcon(new ImageIcon(imgUrl));
            } catch(Exception e){}

            btnVinculos.addActionListener(e -> {
                VinculosDialog dialog = new VinculosDialog(this, tarefaEdicao, usuarioLogado);
                dialog.setVisible(true);
            });
            panel.add(btnVinculos);
        }

        btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setFocusPainted(false);

        btnSalvar = new JButton("Salvar");
        btnSalvar.setPreferredSize(new Dimension(100, 35));
        btnSalvar.setBackground(COLOR_PRIMARY);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvar.setFocusPainted(false);

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> onSalvar());

        panel.add(btnCancelar);
        panel.add(btnSalvar);
        
        if (btnSalvar.isVisible()) {
            getRootPane().setDefaultButton(btnSalvar);
        }
        return panel;
    }

    private void carregarDadosSeEdicao() {
        if (tarefaEdicao == null) return;

        txtTitulo.setText(tarefaEdicao.getTitulo());
        txtDisciplina.setText(tarefaEdicao.getDisciplina());
        // Define o item selecionado no combo
        cbResponsavel.setSelectedItem(tarefaEdicao.getResponsavel());
        txtNotas.setText(tarefaEdicao.getNotas());
        txtDescricao.setText(tarefaEdicao.getDescricao());

        if (tarefaEdicao.getDataLimite() != null) {
            txtDataLimite.setValue(tarefaEdicao.getDataLimite().format(FORMATTER_DATA));
        }

        if (tarefaEdicao.getTipo() != null) {
            comboTipo.setSelectedItem(tarefaEdicao.getTipo());
            // Atualiza sidebar
            sidebarPrioridade.atualizarRegra(tarefaEdicao.getTipo());
        }
    }

    private void onSalvar() {
        try {
            String titulo = txtTitulo.getText().trim();
            String disciplina = txtDisciplina.getText().trim();
            String responsavel = (String) cbResponsavel.getSelectedItem();
            String notas = txtNotas.getText().trim();
            String descricao = txtDescricao.getText().trim();
            String tipoSelecionado = (String) comboTipo.getSelectedItem();

            if (titulo.isEmpty() || disciplina.isEmpty() || responsavel == null) {
                JOptionPane.showMessageDialog(this, "Preencha Título, Disciplina e Resp. Criador.", "Aviso", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate dataLimite = null;
            String strData = txtDataLimite.getText();
            if (!strData.equals("__/__/____") && !strData.trim().isEmpty()) {
                dataLimite = LocalDate.parse(strData, FORMATTER_DATA);
            }

            if (tarefaEdicao == null) {
                tarefaService.cadastrarTarefa(usuarioLogado, tipoSelecionado, titulo, descricao, disciplina, responsavel, notas, dataLimite);
            } else {
                tarefaEdicao.setTitulo(titulo);
                tarefaEdicao.setDisciplina(disciplina);
                tarefaEdicao.setResponsavel(responsavel);
                tarefaEdicao.setNotas(notas);
                tarefaEdicao.setDescricao(descricao);
                tarefaEdicao.setDataLimite(dataLimite);
                tarefaService.atualizarTarefa(usuarioLogado, tarefaEdicao, tipoSelecionado);
            }

            if (onTarefaSalva != null) onTarefaSalva.run();
            dispose();

        } catch (TarefaInvalidaException ex) {
             JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro de Permissão", JOptionPane.ERROR_MESSAGE);
        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use DD/MM/AAAA.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }
}