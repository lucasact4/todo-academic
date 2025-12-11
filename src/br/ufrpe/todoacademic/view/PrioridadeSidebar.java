package br.ufrpe.todoacademic.view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import java.awt.*;

public class PrioridadeSidebar extends JPanel {

    private final Color COLOR_BG = new Color(250, 250, 250);
    private JTextPane txtInfo;

    public PrioridadeSidebar() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(300, 0)); // Largura fixa da barra lateral
        setBackground(COLOR_BG);
        setBorder(new MatteBorder(0, 0, 0, 1, new Color(230, 230, 230)));

        JLabel lblHeader = new JLabel(" Regra de Prioridade");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblHeader.setForeground(new Color(80, 80, 80));
        lblHeader.setBorder(new EmptyBorder(20, 20, 15, 20));

        // Tenta carregar ícone
        try {
            java.net.URL iconUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/info.png");
            if (iconUrl != null) lblHeader.setIcon(new ImageIcon(iconUrl));
        } catch (Exception e) {}

        txtInfo = new JTextPane();
        txtInfo.setContentType("text/html");
        txtInfo.setEditable(false);
        txtInfo.setOpaque(false);
        txtInfo.setBorder(new EmptyBorder(0, 15, 20, 15));

        JScrollPane scroll = new JScrollPane(txtInfo);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        scroll.setOpaque(false);

        add(lblHeader, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    public void atualizarRegra(String tipo) {
        if (tipo == null) return;

        String style = "<style>"
                + "body { font-family: 'Segoe UI', sans-serif; font-size: 11px; margin: 0; color: #555; }"
                + "ul { margin-left: 20px; padding: 0; }"
                + "li { margin-bottom: 6px; }"
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
                conteudo = "<b>Tarefas Simples:</b><br>Dependem apenas do prazo final.<br><ul>"
                        + "<li>Sem prazo &rarr; <span class='green'>Baixa (1)</span></li>"
                        + "<li>Até 14 dias &rarr; <span class='blue'>Normal (2)</span></li>"
                        + "<li>Até 7 dias &rarr; <span class='yellow'>Média (3)</span></li>"
                        + "<li>Até 2 dias &rarr; <span class='orange'>Alta (4)</span></li>"
                        + "<li>Hoje/Atrasada &rarr; <span class='red'>Urgente (5)</span></li></ul>";
                break;
            case "estudo":
                conteudo = "<b>Estudo:</b><br>Atividade essencial.<br><ul>"
                        + "<li>Sem prazo &rarr; <span class='blue'>Normal (2)</span></li>"
                        + "<li>Até 20 dias &rarr; <span class='blue'>Normal (2)</span></li>"
                        + "<li>Até 10 dias &rarr; <span class='yellow'>Média (3)</span></li>"
                        + "<li>Até 3 dias &rarr; <span class='orange'>Alta (4)</span></li>"
                        + "<li>Hoje &rarr; <span class='red'>Urgente (5)</span></li></ul>";
                break;
            case "prova":
                conteudo = "<b>Prova:</b><br>Atenção máxima.<br><ul>"
                        + "<li><span class='gray'>Nota:</span> Data limite é o dia da prova.</li>"
                        + "<li>3 dias antes &rarr; <span class='orange'>Alta (4)</span>.</li>"
                        + "<li>No dia &rarr; <span class='red'>Urgente (5)</span>.</li></ul>";
                break;
            case "trabalho em grupo":
                conteudo = "<b>Trabalho em Grupo:</b><br>Exige antecedência.<br><ul>"
                        + "<li>Sem prazo &rarr; <span class='yellow'>Média (3)</span></li>"
                        + "<li>Até 30 dias &rarr; <span class='blue'>Normal (2)</span></li>"
                        + "<li>Até 15 dias &rarr; <span class='yellow'>Média (3)</span></li>"
                        + "<li>Até 5 dias &rarr; <span class='orange'>Alta (4)</span></li>"
                        + "<li>Hoje &rarr; <span class='red'>Urgente (5)</span></li></ul>";
                break;
            case "apresentação":
                conteudo = "<b>Apresentação:</b><br><ul>"
                        + "<li>Inicia com <span class='yellow'>Média (3)</span>.</li>"
                        + "<li>5 dias antes &rarr; <span class='orange'>Alta (4)</span> para ensaios.</li></ul>";
                break;
            default:
                conteudo = "<span class='gray'>Selecione um tipo.</span>";
        }

        txtInfo.setText("<html><head>" + style + "</head><body>" + conteudo + "</body></html>");
        txtInfo.setCaretPosition(0);
    }
}