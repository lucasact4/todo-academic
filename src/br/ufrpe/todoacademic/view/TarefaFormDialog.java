package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.exception.RepositoryException;
import br.ufrpe.todoacademic.exception.TarefaInvalidaException;
import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.service.TarefaService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TarefaFormDialog extends JDialog {

    private static final DateTimeFormatter FORMATTER_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy"); // <-- dd/MM/aaaa

    private final TarefaService tarefaService;
    private Tarefa tarefaEdicao; // se != null, estamos editando

    private Runnable onTarefaSalva; // callback para atualizar lista na tela principal

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
        super(parent, true); // modal
        this.tarefaService = tarefaService;
        this.tarefaEdicao = tarefaEdicao;
        initComponents();
        carregarTarefaSeEdicao();
    }

    public void setOnTarefaSalva(Runnable onTarefaSalva) {
        this.onTarefaSalva = onTarefaSalva;
    }

    private void initComponents() {
        setTitle(tarefaEdicao == null ? "Nova Tarefa" : "Editar Tarefa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setMinimumSize(new Dimension(520, 480));

        // Painel raiz
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(new EmptyBorder(10, 10, 10, 10));
        root.setBackground(Color.WHITE);

        // ============= HEADER COLORIDO =============
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 153, 102));
        header.setBorder(new EmptyBorder(8, 12, 8, 12));

        JLabel lblIcone = new JLabel();
        // se tiver um ícone tipo add.png ou edit.png, você pode trocar aqui
        lblIcone.setIcon(new ImageIcon(getClass().getResource("/resources/add.png")));
        lblIcone.setBorder(new EmptyBorder(0, 0, 0, 8));

        JLabel lblTituloJanela = new JLabel(
                tarefaEdicao == null ? "Nova tarefa acadêmica" : "Editar tarefa acadêmica"
        );
        lblTituloJanela.setForeground(Color.WHITE);
        lblTituloJanela.setFont(new Font("Segoe UI", Font.BOLD, 18));

        JLabel lblSub = new JLabel("Preencha os dados principais da tarefa do grupo");
        lblSub.setForeground(new Color(230, 255, 241));
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JPanel headerText = new JPanel();
        headerText.setOpaque(false);
        headerText.setLayout(new BoxLayout(headerText, BoxLayout.Y_AXIS));
        headerText.add(lblTituloJanela);
        headerText.add(Box.createVerticalStrut(2));
        headerText.add(lblSub);

        header.add(lblIcone, BorderLayout.WEST);
        header.add(headerText, BorderLayout.CENTER);

        // ============= FORM =============
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        txtTitulo = new JTextField();
        txtDisciplina = new JTextField();
        txtResponsavel = new JTextField();
        txtDataLimite = new JTextField();
        txtNotas = new JTextField();
        txtDescricao = new JTextArea(4, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);

        String[] tipos = {"Simples", "Estudo", "Trabalho em Grupo"};
        comboTipo = new JComboBox<>(tipos);

        int row = 0;

        // ---- TÍTULO (linha inteira) ----
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        form.add(new JLabel("Título *:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        form.add(txtTitulo, gbc);
        row++;

        // ---- DISCIPLINA / RESPONSÁVEL ----
        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Disciplina *:"), gbc);

        gbc.gridx = 1;
        form.add(txtDisciplina, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Responsável *:"), gbc);

        gbc.gridx = 3;
        form.add(txtResponsavel, gbc);
        row++;

        // ---- TIPO / DATA LIMITE ----
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Tipo de tarefa:"), gbc);

        gbc.gridx = 1;
        form.add(comboTipo, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Data limite (dd/MM/aaaa):"), gbc);

        gbc.gridx = 3;
        form.add(txtDataLimite, gbc);
        row++;

        // Dica da data (linha só de texto pequeno)
        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        JLabel lblHintData = new JLabel("Ex.: 30/11/2025 — opcional, deixe em branco se não houver prazo.");
        lblHintData.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        lblHintData.setForeground(new Color(120, 120, 120));
        form.add(lblHintData, gbc);
        row++;

        gbc.gridwidth = 1;

        // ---- NOTAS ----
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Notas (opcional):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        form.add(txtNotas, gbc);
        row++;

        // ---- DESCRIÇÃO ----
        gbc.gridwidth = 1;
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        form.add(new JLabel("Descrição (opcional):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = row;
        gbc.gridwidth = 3;
        gbc.weighty = 1.0;
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);
        scrollDescricao.setPreferredSize(new Dimension(200, 100));
        form.add(scrollDescricao, gbc);
        row++;

        // ============= BOTÕES =============
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelButtons.setBackground(Color.WHITE);

        JButton btnSalvar = new JButton("Salvar");
        JButton btnCancelar = new JButton("Cancelar");

        btnSalvar.setBackground(new Color(0, 153, 102));
        btnSalvar.setForeground(Color.WHITE);
        btnSalvar.setFocusPainted(false);
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 12));

        btnCancelar.setFocusPainted(false);
        btnCancelar.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        btnSalvar.addActionListener(e -> onSalvar());
        btnCancelar.addActionListener(e -> dispose());

        panelButtons.add(btnCancelar);
        panelButtons.add(btnSalvar);

        getRootPane().setDefaultButton(btnSalvar); // Enter = salvar

        // Monta o root
        root.add(header, BorderLayout.NORTH);
        root.add(form, BorderLayout.CENTER);
        root.add(panelButtons, BorderLayout.SOUTH);

        setContentPane(root);
        pack();
        setLocationRelativeTo(getParent());
    }

    private void carregarTarefaSeEdicao() {
        if (tarefaEdicao == null) {
            return;
        }

        txtTitulo.setText(tarefaEdicao.getTitulo());
        txtDisciplina.setText(tarefaEdicao.getDisciplina());
        txtResponsavel.setText(tarefaEdicao.getResponsavel());
        txtNotas.setText(tarefaEdicao.getNotas() != null ? tarefaEdicao.getNotas() : "");
        txtDescricao.setText(tarefaEdicao.getDescricao() != null ? tarefaEdicao.getDescricao() : "");

        if (tarefaEdicao.getDataLimite() != null) {
            // agora exibindo em dd/MM/yyyy
            txtDataLimite.setText(tarefaEdicao.getDataLimite().format(FORMATTER_DATA));
        }

        // Ajustar combo de tipo de acordo com getTipo()
        String tipo = tarefaEdicao.getTipo();
        if (tipo != null) {
            tipo = tipo.toLowerCase();
            if (tipo.contains("estudo")) {
                comboTipo.setSelectedItem("Estudo");
            } else if (tipo.contains("trabalho")) {
                comboTipo.setSelectedItem("Trabalho em Grupo");
            } else {
                comboTipo.setSelectedItem("Simples");
            }
        }
    }

    private void onSalvar() {
        try {
            String titulo = txtTitulo.getText().trim();
            String disciplina = txtDisciplina.getText().trim();
            String responsavel = txtResponsavel.getText().trim();
            String notas = txtNotas.getText().trim();
            String descricao = txtDescricao.getText().trim();
            String tipoSelecionado = (String) comboTipo.getSelectedItem();

            // Validações mínimas de campos obrigatórios (o service também pode validar)
            if (titulo.isEmpty() || disciplina.isEmpty() || responsavel.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Preencha os campos obrigatórios: Título, Disciplina e Responsável.",
                        "Campos obrigatórios",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalDate dataLimite = null;
            String textoData = txtDataLimite.getText().trim();
            if (!textoData.isEmpty()) {
                // agora no formato dd/MM/yyyy
                dataLimite = LocalDate.parse(textoData, FORMATTER_DATA);
            }

            if (tarefaEdicao == null) {
                // NOVA TAREFA
                tarefaService.cadastrarTarefa(
                        tipoSelecionado,
                        titulo,
                        descricao,
                        disciplina,
                        responsavel,
                        notas,
                        dataLimite
                );
            } else {
                // EDIÇÃO
                tarefaEdicao.setTitulo(titulo);
                tarefaEdicao.setDisciplina(disciplina);
                tarefaEdicao.setResponsavel(responsavel);
                tarefaEdicao.setNotas(notas);
                tarefaEdicao.setDescricao(descricao);
                tarefaEdicao.setDataLimite(dataLimite);

                tarefaService.atualizarTarefa(tarefaEdicao);
            }

            if (onTarefaSalva != null) {
                onTarefaSalva.run();
            }

            dispose();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this,
                    "Data em formato inválido. Use o formato dd/MM/aaaa (ex: 30/11/2025).",
                    "Data inválida",
                    JOptionPane.WARNING_MESSAGE);
        } catch (TarefaInvalidaException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE);
        } catch (RepositoryException e) {
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar tarefa: " + e.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
