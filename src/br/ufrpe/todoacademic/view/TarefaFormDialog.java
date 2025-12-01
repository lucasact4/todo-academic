package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.exception.RepositoryException;
import br.ufrpe.todoacademic.exception.TarefaInvalidaException;
import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.service.TarefaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TarefaFormDialog extends JDialog {

    // --- CONFIGURAÇÕES ---
    private static final DateTimeFormatter FORMATTER_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final Color COLOR_PRIMARY = new Color(0, 153, 102);
    private final Color COLOR_BG_HEADER = new Color(0, 153, 102);
    private final Color COLOR_BG_FORM = Color.WHITE;

    private final TarefaService tarefaService;
    private Tarefa tarefaEdicao;
    private Runnable onTarefaSalva;

    // --- COMPONENTES ---
    private JTextField txtTitulo;
    private JTextField txtDisciplina;
    private JTextField txtResponsavel;
    private JTextField txtDataLimite;
    private JTextField txtNotas;
    private JTextArea txtDescricao;
    private JComboBox<String> comboTipo;

    public TarefaFormDialog(Frame parent, TarefaService tarefaService) {
        this(parent, tarefaService, null);
    }

    public TarefaFormDialog(Frame parent, TarefaService tarefaService, Tarefa tarefaEdicao) {
        super(parent, true);
        this.tarefaService = tarefaService;
        this.tarefaEdicao = tarefaEdicao;
        initComponents();
        carregarDadosSeEdicao();
    }

    public void setOnTarefaSalva(Runnable onTarefaSalva) {
        this.onTarefaSalva = onTarefaSalva;
    }

    private void initComponents() {
        setTitle(tarefaEdicao == null ? "Nova Tarefa" : "Editar Tarefa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(650, 600)); // Tamanho confortável
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG_FORM);
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createFormPanel(), BorderLayout.CENTER);
        root.add(createButtonPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    // --- 1. HEADER ---
    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(COLOR_BG_HEADER);
        header.setBorder(new EmptyBorder(25, 30, 25, 30));

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(tarefaEdicao == null ? "Nova Tarefa" : "Editar Tarefa");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(Color.WHITE);

        JLabel lblSub = new JLabel("Preencha os detalhes para organizar seu grupo.");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(255, 255, 255, 200));

        textPanel.add(lblTitle);
        textPanel.add(lblSub);
        
        header.add(textPanel, BorderLayout.CENTER);
        return header;
    }

    // --- 2. FORMULÁRIO (A MÁGICA ACONTECE AQUI) ---
    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_BG_FORM);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 15); // Margem inferior e direita entre os blocos
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // --- INICIALIZAÇÃO DOS COMPONENTES ---
        txtTitulo = createTextField("Ex: Relatório Final de OO");
        txtDisciplina = createTextField("Ex: Programação II");
        txtResponsavel = createTextField("Ex: Lucas");
        txtDataLimite = createTextField("dd/MM/aaaa");
        txtNotas = createTextField("Ex: Vale nota; Entregar impresso...");
        
        comboTipo = new JComboBox<>(new String[]{"Simples", "Estudo", "Trabalho em Grupo", "Apresentação", "Prova"});
        comboTipo.setBackground(Color.WHITE);

        txtDescricao = new JTextArea(5, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        // Truque para o TextArea parecer um TextField do FlatLaf
        txtDescricao.putClientProperty("JComponent.outline", "gray"); 
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);
        scrollDescricao.putClientProperty("JComponent.outline", "gray"); // Borda suave

        // --- MONTAGEM DO GRID ---

        // LINHA 0: Título (Ocupa 2 colunas = 100%)
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa tudo
        gbc.weightx = 1.0;
        form.add(createFieldGroup("Título da Tarefa *", txtTitulo), gbc);

        // LINHA 1: Disciplina (50%) e Responsável (50%)
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reseta para 1 coluna
        gbc.weightx = 0.5; // Divide o peso
        
        gbc.gridx = 0;
        form.add(createFieldGroup("Disciplina *", txtDisciplina), gbc);
        
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 15, 0); // Remove margem direita do último item da linha
        form.add(createFieldGroup("Responsável *", txtResponsavel), gbc);

        // LINHA 2: Tipo (50%) e Data (50%)
        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 15, 15); // Restaura margem direita
        
        gbc.gridx = 0;
        form.add(createFieldGroup("Tipo", comboTipo), gbc);
        
        gbc.gridx = 1;
        gbc.insets = new Insets(0, 0, 15, 0); // Remove margem direita
        form.add(createFieldGroup("Data Limite", txtDataLimite), gbc);

        // LINHA 3: Notas (100%)
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2; // Ocupa tudo
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0);
        form.add(createFieldGroup("Notas Rápidas", txtNotas), gbc);

        // LINHA 4: Descrição (100% e Cresce Verticalmente)
        gbc.gridy = 4;
        gbc.weighty = 1.0; // Puxa o espaço vertical sobrando
        gbc.fill = GridBagConstraints.BOTH;
        form.add(createFieldGroup("Descrição Detalhada", scrollDescricao), gbc);

        return form;
    }

    // --- MÉTODO AUXILIAR QUE CRIA O BLOCO "LABEL + CAMPO" ---
    private JPanel createFieldGroup(String labelText, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(0, 5)); // Gap vertical de 5px entre label e campo
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
        txt.setPreferredSize(new Dimension(10, 40)); // Altura 40px (mais moderno/touch)
        txt.putClientProperty("JTextField.placeholderText", placeholder);
        txt.putClientProperty("JTextField.showClearButton", true);
        return txt;
    }

    // --- 3. BOTÕES ---
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new MatteBorder(1, 0, 0, 0, new Color(230, 230, 230)));

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setPreferredSize(new Dimension(100, 35));
        btnCancelar.setBackground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        
        JButton btnSalvar = new JButton("Salvar");
        btnSalvar.setPreferredSize(new Dimension(100, 35));
        btnSalvar.setBackground(COLOR_PRIMARY);
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvar.setFocusPainted(false);

        btnCancelar.addActionListener(e -> dispose());
        btnSalvar.addActionListener(e -> onSalvar());

        panel.add(btnCancelar);
        panel.add(btnSalvar);
        getRootPane().setDefaultButton(btnSalvar);
        return panel;
    }

    // --- LÓGICA DE DADOS ---
    private void carregarDadosSeEdicao() {
        if (tarefaEdicao == null) return;

        txtTitulo.setText(tarefaEdicao.getTitulo());
        txtDisciplina.setText(tarefaEdicao.getDisciplina());
        txtResponsavel.setText(tarefaEdicao.getResponsavel());
        txtNotas.setText(tarefaEdicao.getNotas());
        txtDescricao.setText(tarefaEdicao.getDescricao());

        if (tarefaEdicao.getDataLimite() != null) {
            txtDataLimite.setText(tarefaEdicao.getDataLimite().format(FORMATTER_DATA));
        }
        if (tarefaEdicao.getTipo() != null) {
            comboTipo.setSelectedItem(tarefaEdicao.getTipo());
        }
    }

    private void onSalvar() {
        try {
            // ... (parte da captura dos dados continua igual: titulo, disciplina, etc) ...
            
            String titulo = txtTitulo.getText().trim();
            String disciplina = txtDisciplina.getText().trim();
            String responsavel = txtResponsavel.getText().trim();
            String notas = txtNotas.getText().trim();
            String descricao = txtDescricao.getText().trim();
            // Pega o tipo selecionado no Combo Box
            String tipoSelecionado = (String) comboTipo.getSelectedItem(); 

            // ... (validações continuam iguais) ...

            LocalDate dataLimite = null;
            String strData = txtDataLimite.getText().trim();
            if (!strData.isEmpty()) {
                dataLimite = LocalDate.parse(strData, FORMATTER_DATA);
            }

            if (tarefaEdicao == null) {
                // Cadastro novo (passa o tipoSelecionado)
                tarefaService.cadastrarTarefa(tipoSelecionado, titulo, descricao, disciplina, responsavel, notas, dataLimite);
            } else {
                // EDIÇÃO
                
                // 1. Atualizamos os dados no objeto (como você já fazia)
                tarefaEdicao.setTitulo(titulo);
                tarefaEdicao.setDisciplina(disciplina);
                tarefaEdicao.setResponsavel(responsavel);
                tarefaEdicao.setNotas(notas);
                tarefaEdicao.setDescricao(descricao);
                tarefaEdicao.setDataLimite(dataLimite);
                
                // 2. CHAMAMOS O NOVO MÉTODO DO SERVICE PASSANDO O TIPO
                // O service vai decidir se mantém o objeto ou cria um novo
                tarefaService.atualizarTarefa(tarefaEdicao, tipoSelecionado);
            }

            if (onTarefaSalva != null) onTarefaSalva.run();
            dispose();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use dd/MM/aaaa.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); // Bom para debug no console
        }
    }
}