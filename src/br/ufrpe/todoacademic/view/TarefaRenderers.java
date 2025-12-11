package br.ufrpe.todoacademic.view;

import br.ufrpe.todoacademic.model.TipoUsuario;
import br.ufrpe.todoacademic.model.Usuario;
import br.ufrpe.todoacademic.service.AuthService;

import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

public class TarefaRenderers {

    public static DefaultTableCellRenderer createHeaderRenderer() {
        return new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
                lbl.setForeground(new Color(120, 120, 120));
                lbl.setBackground(new Color(250, 250, 250));
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));
                lbl.setHorizontalAlignment(JLabel.CENTER);
                return lbl;
            }
        };
    }

    public static DefaultTableCellRenderer createCellRenderer() {
        return new BadgeCellRenderer();
    }

    static class BadgeCellRenderer extends DefaultTableCellRenderer {
        
        private Color badgeColor = null;
        private Color textColor = null;
        private boolean isBadge = false;
        private final AuthService authService = new AuthService(); 

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            String text = (value != null) ? value.toString() : "";
            String colName = table.getColumnName(column);

            // Reseta
            this.badgeColor = null;
            this.isBadge = false;
            this.setToolTipText(null);
            this.setBorder(new EmptyBorder(0, 5, 0, 5));

            if (text.contains("_")) {
                text = text.replace("_", " ");
                this.setText(text);
            }

            // 1. Fundo
            if (isSelected) {
                this.setBackground(new Color(225, 245, 235)); 
                this.setForeground(Color.BLACK);
            } else {
                this.setBackground(Color.WHITE);
                this.setForeground(new Color(50, 50, 50));
            }

            // --- 2. COLUNA RESPONSÁVEL ---
            // Verifica nome ou índice (Agora é 2, pois removemos Disciplina)
            if (colName.toLowerCase().startsWith("respons") || column == 2) {
                setHorizontalAlignment(JLabel.LEFT); 
                
                List<Usuario> usuarios = authService.listarTodos();
                String prefixo = "";
                
                for (Usuario u : usuarios) {
                    String nomeUsuario = u.getNome().trim().toLowerCase();
                    String nomeNaTarefa = text.trim().toLowerCase();

                    // Verifica se contem (mais flexível)
                    if (nomeUsuario.contains(nomeNaTarefa) || nomeNaTarefa.contains(nomeUsuario)) {
                        if (u.getTipo() == TipoUsuario.ALUNO) prefixo = "Aluno:";
                        else if (u.getTipo() == TipoUsuario.PROFESSOR) prefixo = "Prof.:";
                        else if (u.getTipo() == TipoUsuario.ADMIN) prefixo = "Admin:";
                        break;
                    }
                }
                
                if (!prefixo.isEmpty()) {
                    // MUDANÇA: Prefixo em NEGRITO, Nome NORMAL
                    // Ex: <b>Aluno:</b> Lucas
                    this.setText("<html><b>" + prefixo + "</b> " + text + "</html>");
                } else {
                    this.setText(text);
                }
            }

            // 3. Coluna ID
            else if (column == 0 || colName.equalsIgnoreCase("ID")) {
                setHorizontalAlignment(JLabel.CENTER);
            } 
            
            // 4. Coluna STATUS (Badge)
            else if (colName.equalsIgnoreCase("Status")) {
                setHorizontalAlignment(JLabel.CENTER);
                this.isBadge = true;
                this.setFont(new Font("Segoe UI", Font.BOLD, 11));

                if (text.equalsIgnoreCase("CONCLUIDA")) {
                    this.badgeColor = new Color(25, 135, 84); 
                    this.textColor = Color.WHITE;
                } else if (text.equalsIgnoreCase("PENDENTE")) {
                    this.badgeColor = new Color(255, 193, 7); 
                    this.textColor = new Color(50, 50, 50);
                } else if (text.equalsIgnoreCase("EM ANDAMENTO")) {
                    this.badgeColor = new Color(13, 110, 253); 
                    this.textColor = Color.WHITE;
                } else {
                    this.isBadge = false;
                }
                
                if(isBadge) this.setForeground(textColor);
            }

            // 5. Coluna PRIORIDADE
            else if (colName.equalsIgnoreCase("Prioridade") || (column == table.getColumnCount() - 1 && text.matches("\\d+"))) {
                setHorizontalAlignment(JLabel.CENTER);
                this.setFont(new Font("Segoe UI", Font.BOLD, 14));
                try {
                    int p = Integer.parseInt(text);
                    if (p >= 4) setForeground(new Color(220, 53, 69));
                    else if (p == 3) setForeground(new Color(255, 193, 7));
                    else setForeground(new Color(25, 135, 84));

                    String tip = "<html><b>Prioridade " + p + "</b></html>";
                    this.setToolTipText(tip);
                } catch (Exception e) {}
            } 
            
            // 6. Outras
            else {
                setHorizontalAlignment(JLabel.LEFT);
                this.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            }

            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());

            if (isBadge && badgeColor != null) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                FontMetrics fm = g2.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                int paddingX = 16; 
                int paddingY = 4;
                int badgeWidth = textWidth + paddingX;
                int badgeHeight = textHeight + paddingY;
                int x = (getWidth() - badgeWidth) / 2;
                int y = (getHeight() - badgeHeight) / 2;

                g2.setColor(badgeColor);
                g2.fillRoundRect(x, y, badgeWidth, badgeHeight, 15, 15);
                g2.dispose();
            }

            boolean wasOpaque = isOpaque();
            setOpaque(false);
            super.paintComponent(g);
            setOpaque(wasOpaque); 
        }
    }
}