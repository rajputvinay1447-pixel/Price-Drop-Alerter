package pricedrop.ui;

import pricedrop.util.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UIComponents {

    // ── Dark rounded panel ──────────────────────────────────────────────────
    public static class DarkPanel extends JPanel {
        private final Color bg;
        private final int arc;
        public DarkPanel(Color bg, int arc) {
            this.bg = bg; this.arc = arc;
            setOpaque(false);
        }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bg);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
            g2.dispose();
        }
    }

    // ── Card with top-border accent ─────────────────────────────────────────
    public static class Card extends JPanel {
        private Color accentColor;
        public Card() {
            setOpaque(false);
            setLayout(new BorderLayout());
        }
        public void setAccent(Color c) { this.accentColor = c; }
        @Override protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Theme.CARD);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
            // border
            g2.setColor(Theme.BORDER2);
            g2.setStroke(new BasicStroke(1));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
            // top accent line
            if (accentColor != null) {
                g2.setColor(accentColor);
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(14, 1, getWidth()-14, 1);
            }
            g2.dispose();
        }
    }

    // ── Stat card ───────────────────────────────────────────────────────────
    public static JPanel makeStatCard(String label, String valueId, String sub, Color accent) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                // Top accent bar
                GradientPaint gp = new GradientPaint(0, 0, accent, getWidth(), 0, accent.darker());
                g2.setPaint(gp);
                g2.fillRoundRect(0, 0, getWidth(), 3, 3, 3);
                // Glow orb bottom-right
                Color glow = new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40);
                g2.setColor(glow);
                g2.fillOval(getWidth()-80, getHeight()-80, 110, 110);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(160, 100));

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(Theme.FONT_MONO(9));
        lbl.setForeground(Theme.TEXT3);
        lbl.setBounds(16, 14, 200, 14);

        JLabel val = new JLabel(valueId);
        val.setFont(Theme.FONT_TITLE(22));
        val.setForeground(accent);
        val.setName("statValue");
        val.setBounds(16, 30, 200, 30);

        JLabel subLbl = new JLabel(sub);
        subLbl.setFont(Theme.FONT_BODY(10));
        subLbl.setForeground(Theme.TEXT3);
        subLbl.setBounds(16, 63, 200, 14);

        card.add(lbl); card.add(val); card.add(subLbl);
        return card;
    }

    // ── Styled button ───────────────────────────────────────────────────────
    public static JButton makeButton(String text, Color fg, Color bgBase) {
        JButton btn = new JButton(text) {
            private boolean hover = false;
            { addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) { hover=true; repaint(); }
                @Override
                public void mouseExited(MouseEvent e) { hover=false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hover
                        ? new Color(bgBase.getRed(), bgBase.getGreen(), bgBase.getBlue(), 80)
                        : new Color(bgBase.getRed(), bgBase.getGreen(), bgBase.getBlue(), 40);
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 80));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 9, 9);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setForeground(fg);
        btn.setFont(Theme.FONT_BOLD(12));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(7, 16, 7, 16));
        return btn;
    }

    // ── Styled text field ───────────────────────────────────────────────────
    public static JTextField makeTextField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.INPUT);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 9, 9);
                g2.setColor(isFocusOwner()
                    ? new Color(0x00, 0xd4, 0xff, 100)
                    : Theme.BORDER2);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 9, 9);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        tf.setOpaque(false);
        tf.setForeground(Theme.TEXT);
        tf.setCaretColor(Theme.CYAN);
        tf.setFont(Theme.FONT_BODY(12));
        tf.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        tf.putClientProperty("placeholder", placeholder);
        return tf;
    }

    public static JComboBox<String> makeCombo(String[] items) {
        JComboBox<String> cb = new JComboBox<>(items);
        cb.setBackground(Theme.INPUT);
        cb.setForeground(Theme.TEXT2);
        cb.setFont(Theme.FONT_BODY(12));
        cb.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.BORDER2, 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
        ((JLabel)cb.getRenderer()).setBackground(Theme.BG3);
        return cb;
    }

    // ── Badge label ─────────────────────────────────────────────────────────
    public static JLabel makeBadge(String text, Color fg, Color bg) {
        JLabel lbl = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 40));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 80));
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        lbl.setForeground(fg);
        lbl.setFont(Theme.FONT_MONO(10));
        lbl.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
        lbl.setOpaque(false);
        return lbl;
    }

    // ── Progress bar ────────────────────────────────────────────────────────
    public static JPanel makeProgressBar(int percent, Color color) {
        JPanel wrap = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG4);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                int w = (int)(getWidth() * (Math.min(percent,100) / 100.0));
                if (w > 0) { g2.setColor(color); g2.fillRoundRect(0, 0, w, getHeight(), 4, 4); }
                g2.dispose();
            }
        };
        wrap.setOpaque(false);
        wrap.setPreferredSize(new Dimension(200, 8));
        return wrap;
    }

    // ── Section title ───────────────────────────────────────────────────────
    public static JLabel makeSectionTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_TITLE(18));
        l.setForeground(Theme.TEXT);
        return l;
    }

    // ── Card title ──────────────────────────────────────────────────────────
    public static JLabel makeCardTitle(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_TITLE(13));
        l.setForeground(Theme.TEXT);
        return l;
    }

    // ── Separator ───────────────────────────────────────────────────────────
    public static JPanel makeSep() {
        JPanel sep = new JPanel();
        sep.setBackground(Theme.BORDER);
        sep.setPreferredSize(new Dimension(10000, 1));
        sep.setMaximumSize(new Dimension(10000, 1));
        return sep;
    }

    // ── Scrollpane ──────────────────────────────────────────────────────────
    public static JScrollPane scroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.setBorder(null);
        sp.getVerticalScrollBar().setUnitIncrement(16);
        sp.getVerticalScrollBar().setBackground(Theme.BG2);
        sp.getHorizontalScrollBar().setBackground(Theme.BG2);
        return sp;
    }
}
