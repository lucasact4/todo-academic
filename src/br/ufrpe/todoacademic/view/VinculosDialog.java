package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.model.Tarefa;
import br.ufrpe.todoacademic.model.TipoUsuario;
import br.ufrpe.todoacademic.model.Usuario;
import br.ufrpe.todoacademic.model.VinculoTarefa;
import br.ufrpe.todoacademic.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VinculosDialog extends JDialog {

    private final Tarefa tarefa;
    private final Usuario usuarioLogado; // Necessário para validação
    private final AuthService authService;
    
    private JTable tableVinculos;
    private DefaultTableModel tableModel;
    
    // Botões
    private JButton btnAdd;
    private JButton btnEdit;
    private JButton btnDel;
    private JButton btnClose;

    public VinculosDialog(Window owner, Tarefa tarefa, Usuario usuarioLogado) {
        super(owner, "Estudantes Vinculados", ModalityType.APPLICATION_MODAL);
        this.tarefa = tarefa;
        this.usuarioLogado = usuarioLogado;
        this.authService = new AuthService();
        
        initComponents();
        aplicarRestricoesPerfil(); // Aplica regras de Aluno vs Prof
        carregarTabela();
    }

    private void initComponents() {
        setSize(700, 450);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());

        // --- Header ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 153, 102));
        header.setBorder(new EmptyBorder(15, 15, 15, 15));
        JLabel lblTitle = new JLabel("Gerenciar Vínculos: " + tarefa.getTitulo());
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblTitle.setForeground(Color.WHITE);
        header.add(lblTitle);
        add(header, BorderLayout.NORTH);

        // --- Tabela ---
        String[] cols = {"Aluno", "Prioridade", "Status", "Observações"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int col) { return false; }
        };
        tableVinculos = new JTable(tableModel);
        tableVinculos.setRowHeight(30);
        estilizarTabela();
        
        JScrollPane scroll = new JScrollPane(tableVinculos);
        scroll.setBorder(new EmptyBorder(10, 10, 10, 10));
        add(scroll, BorderLayout.CENTER);

        // --- Botões ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        btnAdd = new JButton("Vincular Aluno");
        btnEdit = new JButton("Editar Detalhes");
        btnDel = new JButton("Remover");
        btnClose = new JButton("Fechar");

        // Cores suaves
        btnAdd.setBackground(new Color(225, 240, 255)); btnAdd.setForeground(new Color(0, 80, 180));
        btnEdit.setBackground(new Color(255, 248, 220)); btnEdit.setForeground(new Color(180, 90, 0));
        btnDel.setBackground(new Color(255, 230, 230)); btnDel.setForeground(new Color(200, 40, 40));

        btnAdd.addActionListener(e -> adicionarVinculo());
        btnEdit.addActionListener(e -> editarVinculo());
        btnDel.addActionListener(e -> removerVinculo());
        btnClose.addActionListener(e -> dispose());

        footer.add(btnAdd);
        footer.add(btnEdit);
        footer.add(btnDel);
        footer.add(Box.createHorizontalStrut(20));
        footer.add(btnClose);
        add(footer, BorderLayout.SOUTH);
    }
    
    private void aplicarRestricoesPerfil() {
        // Se for ALUNO, esconde botões de gestão
        if (usuarioLogado.getTipo() == TipoUsuario.ALUNO) {
            btnAdd.setVisible(false); // Não pode vincular outros
            btnDel.setVisible(false); // Não pode remover
            btnEdit.setText("Meus Detalhes"); // Muda texto para ficar claro
        }
    }

    private void estilizarTabela() {
        tableVinculos.setShowVerticalLines(false);
        tableVinculos.setGridColor(new Color(230, 230, 230));
        tableVinculos.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        tableVinculos.getColumnModel().getColumn(1).setCellRenderer(center);
        tableVinculos.getColumnModel().getColumn(2).setCellRenderer(center);
    }

    private void carregarTabela() {
        tableModel.setRowCount(0);
        
        for (VinculoTarefa vt : tarefa.getVinculos()) {
            // REGRA DE VISUALIZAÇÃO:
            // Se for Professor/Admin -> Vê todos.
            // Se for Aluno -> Só vê a si mesmo na lista.
            boolean isProfOrAdmin = (usuarioLogado.getTipo() != TipoUsuario.ALUNO);
            boolean isMe = vt.getAluno().getLogin().equals(usuarioLogado.getLogin());
            
            if (isProfOrAdmin || isMe) {
                tableModel.addRow(new Object[]{
                    vt.getAluno().getNome(),
                    vt.getPrioridadeManual(),
                    vt.isConcluido() ? "Concluído" : "Pendente",
                    vt.getObservacao()
                });
            }
        }
    }

    private void adicionarVinculo() {
        // Segurança extra
        if (usuarioLogado.getTipo() == TipoUsuario.ALUNO) return;

        List<Usuario> alunos = authService.listarAlunos();
        
        // Remove quem já está vinculado
        alunos.removeIf(a -> tarefa.getVinculos().stream()
                .anyMatch(v -> v.getAluno().getLogin().equals(a.getLogin())));

        if (alunos.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Não há alunos disponíveis para vincular.");
            return;
        }

        Usuario selecionado = (Usuario) JOptionPane.showInputDialog(
                this, "Selecione o aluno:", "Vincular", 
                JOptionPane.QUESTION_MESSAGE, null, 
                alunos.toArray(), alunos.get(0));

        if (selecionado != null) {
            VinculoTarefa novoVinculo = new VinculoTarefa(selecionado);
            tarefa.adicionarVinculo(novoVinculo);
            carregarTabela();
        }
    }

    private void editarVinculo() {
        int row = tableVinculos.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Selecione um vínculo na tabela.");
            return;
        }
        
        // Pega o nome/login da linha selecionada para achar o objeto real na lista
        String nomeAlunoRow = (String) tableVinculos.getValueAt(row, 0);
        
        // Busca o objeto VinculoTarefa correspondente
        VinculoTarefa vinculo = tarefa.getVinculos().stream()
                .filter(v -> v.getAluno().getNome().equals(nomeAlunoRow))
                .findFirst()
                .orElse(null);

        if (vinculo == null) return;

        // Formulário de Edição
        JTextField txtObs = new JTextField(vinculo.getObservacao());
        JComboBox<String> cbPrio = new JComboBox<>(new String[]{"Baixa", "Média", "Alta"});
        cbPrio.setSelectedItem(vinculo.getPrioridadeManual());
        JCheckBox chkConcluido = new JCheckBox("Concluído pelo aluno");
        chkConcluido.setSelected(vinculo.isConcluido());

        Object[] message = {
            "Aluno: " + vinculo.getAluno().getNome(),
            "Prioridade Manual:", cbPrio,
            "Status:", chkConcluido,
            "Observações:", txtObs
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Editar Vínculo", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            vinculo.setPrioridadeManual((String) cbPrio.getSelectedItem());
            vinculo.setConcluido(chkConcluido.isSelected());
            vinculo.setObservacao(txtObs.getText());
            carregarTabela();
        }
    }

    private void removerVinculo() {
        // Segurança extra
        if (usuarioLogado.getTipo() == TipoUsuario.ALUNO) return;

        int row = tableVinculos.getSelectedRow();
        if (row == -1) return;
        
        String nomeAlunoRow = (String) tableVinculos.getValueAt(row, 0);
        VinculoTarefa vinculo = tarefa.getVinculos().stream()
                .filter(v -> v.getAluno().getNome().equals(nomeAlunoRow))
                .findFirst()
                .orElse(null);
                
        if (vinculo != null) {
            int opt = JOptionPane.showConfirmDialog(this, "Remover " + vinculo.getAluno().getNome() + " desta tarefa?");
            if (opt == JOptionPane.YES_OPTION) {
                tarefa.removerVinculo(vinculo);
                carregarTabela();
            }
        }
    }
}