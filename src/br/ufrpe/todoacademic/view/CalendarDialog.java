package br.ufrpe.todoacademic.view;

import java.awt.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class CalendarDialog extends JDialog {

    private YearMonth currentMonth;
    private final Consumer<LocalDate> onDateSelected;
    private JPanel daysPanel;
    private JLabel lblMonthYear;

    public CalendarDialog(Window owner, Consumer<LocalDate> onDateSelected) {
        super(owner, "Selecionar Data", ModalityType.APPLICATION_MODAL);
        this.onDateSelected = onDateSelected;
        this.currentMonth = YearMonth.now();
        
        initComponents();
        atualizarCalendario();
    }

    private void initComponents() {
        // AJUSTE 1: Aumentei um pouco o tamanho para garantir que os números caibam
        setSize(360, 360);
        setLocationRelativeTo(getOwner());
        setLayout(new BorderLayout());
        setResizable(false);

        // --- Header (Navegação) ---
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(0, 153, 102));
        header.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton btnPrev = createNavButton("<");
        btnPrev.addActionListener(e -> {
            currentMonth = currentMonth.minusMonths(1);
            atualizarCalendario();
        });

        JButton btnNext = createNavButton(">");
        btnNext.addActionListener(e -> {
            currentMonth = currentMonth.plusMonths(1);
            atualizarCalendario();
        });

        lblMonthYear = new JLabel("", SwingConstants.CENTER);
        lblMonthYear.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblMonthYear.setForeground(Color.WHITE);

        header.add(btnPrev, BorderLayout.WEST);
        header.add(lblMonthYear, BorderLayout.CENTER);
        header.add(btnNext, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // --- Corpo (Dias) ---
        daysPanel = new JPanel(new GridLayout(0, 7, 5, 5)); // Aumentei o espaçamento entre botões
        daysPanel.setBackground(Color.WHITE);
        daysPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        add(daysPanel, BorderLayout.CENTER);
    }

    private void atualizarCalendario() {
        daysPanel.removeAll();
        
        // Atualiza título
        String mes = currentMonth.getMonth().getDisplayName(TextStyle.FULL, new Locale("pt", "BR"));
        mes = mes.substring(0, 1).toUpperCase() + mes.substring(1);
        lblMonthYear.setText(mes + " " + currentMonth.getYear());

        // Cabeçalho dos dias
        String[] diasSemana = {"D", "S", "T", "Q", "Q", "S", "S"};
        for (String d : diasSemana) {
            JLabel lbl = new JLabel(d, SwingConstants.CENTER);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
            lbl.setForeground(Color.GRAY);
            daysPanel.add(lbl);
        }

        // Lógica de dias
        LocalDate primeiroDia = currentMonth.atDay(1);
        int diaDaSemana = primeiroDia.getDayOfWeek().getValue(); 
        int slotsVazios = (diaDaSemana % 7); 

        for (int i = 0; i < slotsVazios; i++) {
            daysPanel.add(new JLabel(""));
        }

        int diasNoMes = currentMonth.lengthOfMonth();
        LocalDate hoje = LocalDate.now();

        for (int dia = 1; dia <= diasNoMes; dia++) {
            LocalDate data = currentMonth.atDay(dia);
            JButton btnDia = new JButton(String.valueOf(dia));
            
            // AJUSTE 2: Margem pequena para o número não ser cortado (...)
            btnDia.setMargin(new Insets(2, 2, 2, 2));
            
            btnDia.setFocusPainted(false);
            btnDia.setBorderPainted(false); // Flat style
            btnDia.setBackground(Color.WHITE);
            btnDia.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnDia.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            
            // Destaca o dia atual
            if (data.equals(hoje)) {
                btnDia.setForeground(Color.WHITE);
                btnDia.setBackground(new Color(0, 153, 102)); // Fundo verde para o dia atual
                btnDia.setFont(new Font("Segoe UI", Font.BOLD, 12));
                btnDia.setOpaque(true); // Necessário em alguns LookAndFeels para mostrar a cor de fundo
            }

            btnDia.addActionListener(e -> {
                if (onDateSelected != null) onDateSelected.accept(data);
                dispose();
            });
            
            daysPanel.add(btnDia);
        }

        daysPanel.revalidate();
        daysPanel.repaint();
    }

    private JButton createNavButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(255, 255, 255, 50));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}