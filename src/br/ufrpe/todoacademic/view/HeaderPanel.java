package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.model.Usuario;
import java.awt.*;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class HeaderPanel extends JPanel {

    public HeaderPanel(Usuario usuario, ActionListener onLogout) {
        setLayout(new BorderLayout());
        setBackground(new Color(0, 153, 102));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        // --- LADO ESQUERDO: Título ---
        JPanel titleBlock = new JPanel(new GridLayout(2, 1));
        titleBlock.setOpaque(false);

        JLabel lblTitle = new JLabel("TodoAcademic");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(Color.WHITE);

        try {
            java.net.URL imgUrl = getClass().getResource("/br/ufrpe/todoacademic/resources/tick.png");
            if (imgUrl != null) lblTitle.setIcon(new ImageIcon(imgUrl));
        } catch (Exception e) {}
        lblTitle.setIconTextGap(15);

        JLabel lblSub = new JLabel("Programação II · UFRPE");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(new Color(255, 255, 255, 200));

        titleBlock.add(lblTitle);
        titleBlock.add(lblSub);

        // --- LADO DIREITO: Usuário e Botão Sair ---
        Box userBox = Box.createHorizontalBox();
        String textoUsuario = "<html><div style='text-align: right;'>Olá, <b>" + usuario.getNome() + "</b><br/>" +
                              "<small>" + usuario.getTipo() + "</small></div></html>";
        
        JLabel lblUser = new JLabel(textoUsuario);
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblUser.setForeground(Color.WHITE);
        lblUser.setHorizontalAlignment(SwingConstants.RIGHT);

        JButton btnLogout = new JButton("Sair");
        btnLogout.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnLogout.setForeground(Color.WHITE);
        btnLogout.setBackground(new Color(255, 255, 255, 0));
        btnLogout.setContentAreaFilled(false);
        btnLogout.setOpaque(false);
        btnLogout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1), new EmptyBorder(5, 15, 5, 15)));
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.setFocusPainted(false);
        btnLogout.addActionListener(onLogout);

        userBox.add(lblUser);
        userBox.add(Box.createHorizontalStrut(15));
        userBox.add(btnLogout);

        add(titleBlock, BorderLayout.WEST);
        add(userBox, BorderLayout.EAST);
    }
}