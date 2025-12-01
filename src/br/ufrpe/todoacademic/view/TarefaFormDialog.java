package br.ufrpe.todoacademic.view;

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
    
    // Cor de fundo da Sidebar (Um cinza bem clarinho para distinguir do formulário branco)
    private final Color COLOR_SIDEBAR_BG = new Color(250, 250, 250);

    private final TarefaService tarefaService;
    private Tarefa tarefaEdicao;
    private Runnable onTarefaSalva;
    
    // --- COMPONENTES GLOBAIS ---
    private JButton btnSalvar;
    private JButton btnCancelar;
    
    private JLabel lblTitle;
    private JLabel lblSub;

    // --- CAMPOS ---
    private JTextField txtTitulo;
    private JTextField txtDisciplina;
    private JTextField txtResponsavel;
    private JTextField txtDataLimite;
    private JTextField txtNotas;
    private JTextArea txtDescricao;
    private JComboBox<String> comboTipo;
    
    // Componente de explicação (agora na sidebar)
    private JTextPane txtInfoPrioridade; 

    public TarefaFormDialog(Frame parent, TarefaService tarefaService) {
        this(parent, tarefaService, null);
    }

    public TarefaFormDialog(Frame parent, TarefaService tarefaService, Tarefa tarefaEdicao) {
        super(parent, true);
        this.tarefaService = tarefaService;
        this.tarefaEdicao = tarefaEdicao;
        initComponents();
        
        // Listener para atualizar a sidebar
        comboTipo.addActionListener(e -> atualizarExplicacaoPrioridade());
        
        carregarDadosSeEdicao();
        
        // Inicializa o texto da sidebar
        if (tarefaEdicao == null) {
            atualizarExplicacaoPrioridade();
        }
    }

    public void setOnTarefaSalva(Runnable onTarefaSalva) {
        this.onTarefaSalva = onTarefaSalva;
    }
    
    public void ativarModoLeitura() {
        setTitle("Visualizar Tarefa"); 
        
        lblTitle.setText("Visualizar Tarefa");
        lblSub.setText("Detalhes completos da tarefa.");

        // Desabilita edição
        txtTitulo.setEditable(false);
        txtDisciplina.setEditable(false);
        txtResponsavel.setEditable(false);
        txtDataLimite.setEditable(false);
        txtNotas.setEditable(false);
        txtDescricao.setEditable(false);
        comboTipo.setEnabled(false);

        // Estilo Leitura
        Color readOnlyColor = new Color(252, 252, 252);
        txtTitulo.setBackground(readOnlyColor);
        txtDisciplina.setBackground(readOnlyColor);
        txtResponsavel.setBackground(readOnlyColor);
        txtDataLimite.setBackground(readOnlyColor);
        txtNotas.setBackground(readOnlyColor);
        txtDescricao.setBackground(readOnlyColor);
        
        // Remove bordas para ficar mais limpo
        txtTitulo.setBorder(null);
        txtDescricao.setBorder(null);

        btnSalvar.setVisible(false); 
        btnCancelar.setText("Fechar");
    }

    private void initComponents() {
        setTitle(tarefaEdicao == null ? "Nova Tarefa" : "Editar Tarefa");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // AUMENTAMOS A LARGURA (WIDTH) PARA ACOMODAR A SIDEBAR
        setMinimumSize(new Dimension(1100, 650));
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(COLOR_BG_FORM);
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        
        // --- DIVISÃO DO CONTEÚDO (ESQUERDA + CENTRO) ---
        JPanel contentContainer = new JPanel(new BorderLayout());
        contentContainer.add(createSidePanel(), BorderLayout.WEST);   // Info Prioridade (Esquerda)
        contentContainer.add(createFormPanel(), BorderLayout.CENTER); // Formulário (Centro)
        
        root.add(contentContainer, BorderLayout.CENTER);
        // -----------------------------------------------
        
        root.add(createButtonPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    // --- 1. HEADER (TOPO) ---
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

    // --- 2. SIDEBAR (PAINEL ESQUERDO DE INFORMAÇÃO) ---
    private JPanel createSidePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(380, 0));
        panel.setBackground(COLOR_SIDEBAR_BG);
        panel.setBorder(new MatteBorder(0, 0, 0, 1, new Color(230, 230, 230))); // Borda direita separadora

        // Título da Sidebar com Ícone
        JLabel lblHeader = new JLabel(" Regra de Prioridade");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(new Color(80, 80, 80));
        lblHeader.setBorder(new EmptyBorder(20, 20, 15, 20));
        
        // Tenta carregar o ícone info.png
        try {
            java.net.URL iconUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/info.png");
            if (iconUrl != null) {
                lblHeader.setIcon(new ImageIcon(iconUrl));
            }
        } catch (Exception e) {}

        // Área de Texto Rico (HTML)
        txtInfoPrioridade = new JTextPane();
        txtInfoPrioridade.setContentType("text/html");
        txtInfoPrioridade.setEditable(false);
        txtInfoPrioridade.setOpaque(false); // Transparente para pegar a cor da sidebar
        txtInfoPrioridade.setBorder(new EmptyBorder(0, 15, 20, 15)); // Margens internas
        
        // JScrollPane invisível (caso o texto seja muito grande em monitores pequenos)
        JScrollPane scroll = new JScrollPane(txtInfoPrioridade);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        panel.add(lblHeader, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // --- 3. FORMULÁRIO (CENTRO) ---
    private JPanel createFormPanel() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_BG_FORM);
        form.setBorder(new EmptyBorder(20, 30, 20, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTHWEST;

        // Inicialização
        txtTitulo = createTextField("Ex: Relatório Final de OO");
        txtDisciplina = createTextField("Ex: Programação II");
        txtResponsavel = createTextField("Ex: Lucas");
        txtDataLimite = createTextField("DD/MM/AAAA");
        txtNotas = createTextField("Ex: Vale nota; Entregar impresso...");
        
        comboTipo = new JComboBox<>(new String[]{"Simples", "Estudo", "Trabalho em Grupo", "Apresentação", "Prova"});
        comboTipo.setBackground(Color.WHITE);
        // Deixa o combo um pouco mais alto
        comboTipo.setPreferredSize(new Dimension(comboTipo.getPreferredSize().width, 35)); 

        txtDescricao = new JTextArea(5, 20);
        txtDescricao.setLineWrap(true);
        txtDescricao.setWrapStyleWord(true);
        txtDescricao.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtDescricao.putClientProperty("JComponent.outline", "gray"); 
        JScrollPane scrollDescricao = new JScrollPane(txtDescricao);
        scrollDescricao.putClientProperty("JComponent.outline", "gray");

        // -- MONTAGEM --

        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.gridwidth = 2; // Ocupa tudo
        gbc.weightx = 1.0;
        
        // ADICIONE ESSA LINHA PARA TIRAR A MARGEM DIREITA
        gbc.insets = new Insets(0, 0, 15, 0); 
        
        form.add(createFieldGroup("Título da Tarefa *", txtTitulo), gbc);

        
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        
        // Coluna 0 (Esquerda): Disciplina
        gbc.gridx = 0;
        // Adiciona 15px na DIREITA para afastar do Responsável
        gbc.insets = new Insets(0, 0, 15, 15); 
        form.add(createFieldGroup("Disciplina *", txtDisciplina), gbc);
        
        // Coluna 1 (Direita): Responsável
        gbc.gridx = 1;
        // Zera a margem DIREITA para alinhar com o Título e Notas
        gbc.insets = new Insets(0, 0, 15, 0); 
        form.add(createFieldGroup("Responsável *", txtResponsavel), gbc);

        // LINHA 2 (Aplique a mesma lógica aqui para ficarem iguais)
        gbc.gridy = 2;
        
        // Coluna 0 (Esquerda): Tipo
        gbc.gridx = 0;
        // Adiciona 15px na DIREITA
        gbc.insets = new Insets(0, 0, 15, 15); 
        form.add(createFieldGroup("Tipo", comboTipo), gbc);
        
        // Coluna 1 (Direita): Data
        gbc.gridx = 1;
        // Zera a margem DIREITA
        gbc.insets = new Insets(0, 0, 15, 0); 
        form.add(createFieldGroup("Data Limite", txtDataLimite), gbc);

        // LINHA 3
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(0, 0, 15, 0);
        form.add(createFieldGroup("Notas Rápidas", txtNotas), gbc);

        // LINHA 4: Descrição (Ocupa o resto vertical)
        gbc.gridy = 4;
        gbc.weighty = 1.0; 
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0); // Sem margem inferior
        form.add(createFieldGroup("Descrição Detalhada", scrollDescricao), gbc);

        // NOTA: Removemos a linha 5 (Info Prioridade) daqui porque foi para a sidebar

        return form;
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
        getRootPane().setDefaultButton(btnSalvar);
        return panel;
    }

    // --- LÓGICA DE DADOS ---

    private void atualizarExplicacaoPrioridade() {
        String tipo = (String) comboTipo.getSelectedItem();
        if (tipo == null) return;

        // Estilo CSS - Fundo transparente e fontes ajustadas para a sidebar
        String style = "<style>"
                + "body { font-family: 'Segoe UI', sans-serif; font-size: 11px; margin: 0; color: #555; }"
                + "h2 { font-size: 11px; margin-bottom: 5px; color: #333; }"
                + "ul { margin-left: 20px; padding: 0; }"
                + "li { margin-bottom: 6px; }" // Mais espaço entre itens
                + ".green { color: #28a745; font-weight: bold; }" 
                + ".blue  { color: #007bff; font-weight: bold; }" 
                + ".yellow{ color: #d39e00; font-weight: bold; }" 
                + ".orange{ color: #fd7e14; font-weight: bold; }" 
                + ".red   { color: #dc3545; font-weight: bold; }" 
                + ".gray  { color: #888; }"
                + "</style>";

        String conteudo = "";

        switch (tipo.toLowerCase()) {
            case "simples":
                conteudo = "<b>Tarefas Simples:</b><br>Dependem apenas do prazo final.<br>"
                        + "<ul>"
                        + "<li>Sem prazo definido &rarr; <span class='green'>Baixa (1)</span></li>"
                        + "<li>Até 14 dias &rarr; <span class='blue'>Normal (2)</span></li>"
                        + "<li>Até 7 dias &rarr; <span class='yellow'>Média (3)</span></li>"
                        + "<li>Até 2 dias &rarr; <span class='orange'>Alta (4)</span></li>"
                        + "<li>Hoje ou Atrasada &rarr; <span class='red'>Urgente (5)</span></li>"
                        + "</ul>";
                break;

            case "estudo":
                conteudo = "<b>Estudo:</b><br>Atividade essencial, urgência escala rápido.<br>"
                        + "<ul>"
                        + "<li>Sem prazo &rarr; <span class='blue'>Normal (2)</span> <span class='gray'>(Estudar é vital!)</span></li>"
                        + "<li>Até 20 dias &rarr; <span class='blue'>Normal (2)</span></li>"
                        + "<li>Até 10 dias &rarr; <span class='yellow'>Média (3)</span></li>"
                        + "<li>Até 3 dias &rarr; <span class='orange'>Alta (4)</span></li>"
                        + "<li>Hoje ou Atrasada &rarr; <span class='red'>Urgente (5)</span></li>"
                        + "</ul>";
                break;

            case "prova":
                conteudo = "<b>Prova:</b><br>Exige atenção máxima e preparação prévia.<br>"
                        + "<ul>"
                        + "<li>Seguem a escala de Estudo, mas com mais rigor.</li>"
                        + "<li><span class='gray'>Nota:</span> A data limite deve ser o dia da prova.</li>"
                        + "<li>3 dias antes &rarr; Já entra em <span class='orange'>Alta Prioridade (4)</span>.</li>"
                        + "<li>No dia &rarr; <span class='red'>Urgente (5)</span>.</li>"
                        + "</ul>";
                break;

            case "trabalho em grupo":
                conteudo = "<b>Trabalho em Grupo:</b><br>Peso maior na nota, exige antecedência.<br>"
                        + "<ul>"
                        + "<li>Sem prazo &rarr; <span class='yellow'>Média (3)</span> <span class='gray'>(Coordenação demora)</span></li>"
                        + "<li>Até 30 dias &rarr; <span class='blue'>Normal (2)</span></li>"
                        + "<li>Até 15 dias &rarr; <span class='yellow'>Média (3)</span></li>"
                        + "<li>Até 5 dias &rarr; <span class='orange'>Alta (4)</span></li>"
                        + "<li>Hoje ou Atrasada &rarr; <span class='red'>Urgente (5)</span></li>"
                        + "</ul>";
                break;

            case "apresentação":
                conteudo = "<b>Apresentação:</b><br>Requer preparação de slides e ensaio.<br>"
                        + "<ul>"
                        + "<li>Inicia com prioridade mínima <span class='yellow'>Média (3)</span>.</li>"
                        + "<li>5 dias antes &rarr; Sobe para <span class='orange'>Alta (4)</span> para ensaios.</li>"
                        + "<li>Atenção aos prazos de entrega de slides vs dia da apresentação.</li>"
                        + "</ul>";
                break;

            default:
                conteudo = "<span class='gray'>Selecione um tipo acima para ver como a prioridade será calculada.</span>";
        }

        txtInfoPrioridade.setText("<html><head>" + style + "</head><body>" + conteudo + "</body></html>");
        txtInfoPrioridade.setCaretPosition(0);
    }

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
            atualizarExplicacaoPrioridade();
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

            if (titulo.isEmpty() || disciplina.isEmpty() || responsavel.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Preencha Título, Disciplina e Responsável.", 
                        "Aviso", JOptionPane.WARNING_MESSAGE);
                 return;
            }

            LocalDate dataLimite = null;
            String strData = txtDataLimite.getText().trim();
            if (!strData.isEmpty()) {
                dataLimite = LocalDate.parse(strData, FORMATTER_DATA);
            }

            if (tarefaEdicao == null) {
                tarefaService.cadastrarTarefa(tipoSelecionado, titulo, descricao, disciplina, responsavel, notas, dataLimite);
            } else {
                tarefaEdicao.setTitulo(titulo);
                tarefaEdicao.setDisciplina(disciplina);
                tarefaEdicao.setResponsavel(responsavel);
                tarefaEdicao.setNotas(notas);
                tarefaEdicao.setDescricao(descricao);
                tarefaEdicao.setDataLimite(dataLimite);
                
                tarefaService.atualizarTarefa(tarefaEdicao, tipoSelecionado);
            }

            if (onTarefaSalva != null) onTarefaSalva.run();
            dispose();

        } catch (DateTimeParseException e) {
            JOptionPane.showMessageDialog(this, "Data inválida. Use DD/MM/AAAA.", "Erro", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Erro: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
}