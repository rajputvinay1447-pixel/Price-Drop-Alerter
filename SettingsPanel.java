package pricedrop.ui;

import pricedrop.data.AppData;
import pricedrop.model.*;
import pricedrop.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class SettingsPanel extends JPanel {

    public SettingsPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        content.add(UIComponents.makeSectionTitle("Settings"));
        content.add(Box.createVerticalStrut(18));

        content.add(makeSection("🔔 Price Monitoring", new JPanel[]{
            makeRow("Check Interval", UIComponents.makeCombo(new String[]{"30 minutes","15 minutes","1 hour","2 hours"})),
            makeRow("Desktop Notifications", makeCheckBox(true)),
            makeRow("Currency", makeValLabel("₹ Indian Rupees (INR)")),
            makeActionRow(makeSimCheckBtn())
        }));

        content.add(makeSection("🌓 Appearance", new JPanel[]{
            makeRow("Theme", makeThemeBtns())
        }));

        content.add(makeSection("🛒 Supported Platforms", new JPanel[]{
            makeRow2("Amazon India",   Theme.AMAZON,   "amazon.in/dp/ASIN · Auto-detect ✅"),
            makeRow2("Flipkart",       Theme.FLIPKART, "flipkart.com/p/PID · Auto-detect ✅"),
            makeRow2("Myntra",         Theme.MYNTRA,   "myntra.com/ID/buy · Auto-detect ✅")
        }));

        content.add(makeSection("💾 Data Management", new JPanel[]{
            makeRow("Storage", makeValLabel("In-Memory (session only)")),
            makeActionRow(makeDataBtns())
        }));

        content.add(makeSection("ℹ️ About PriceDrop", new JPanel[]{
            makeRow("Version",   makeValLabel("2.0 Java Edition")),
            makeRow("Built with",makeValLabel("Java · Swing · AWT")),
            makeRow("Runs on",   makeValLabel("Windows · macOS · Linux"))
        }));

        add(UIComponents.scroll(content), BorderLayout.CENTER);
    }

    private JPanel makeSection(String title, JPanel[] rows) {
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(Theme.BORDER); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18,20,18,20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(Theme.FONT_BOLD(13)); titleLbl.setForeground(Theme.TEXT);
        titleLbl.setAlignmentX(LEFT_ALIGNMENT);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
        card.add(titleLbl);
        card.add(UIComponents.makeSep());
        for (JPanel row : rows) {
            card.add(Box.createVerticalStrut(10));
            row.setAlignmentX(LEFT_ALIGNMENT);
            card.add(row);
        }

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createEmptyBorder(0,0,14,0));
        wrap.add(card, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel makeRow(String key, Component val) {
        JPanel row = new JPanel(new BorderLayout()); row.setOpaque(false);
        JLabel k = new JLabel(key); k.setFont(Theme.FONT_BODY(12)); k.setForeground(Theme.TEXT2);
        row.add(k, BorderLayout.WEST); row.add(val, BorderLayout.EAST);
        return row;
    }

    private JPanel makeRow2(String key, Color color, String val) {
        JPanel row = new JPanel(new BorderLayout()); row.setOpaque(false);
        JLabel k = new JLabel(key); k.setFont(Theme.FONT_BOLD(12)); k.setForeground(color);
        JLabel v = new JLabel(val); v.setFont(Theme.FONT_MONO(11)); v.setForeground(Theme.TEXT3);
        row.add(k, BorderLayout.WEST); row.add(v, BorderLayout.EAST);
        return row;
    }

    private JPanel makeActionRow(Component comp) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT,0,0)); row.setOpaque(false);
        row.add(comp); return row;
    }

    private JLabel makeValLabel(String text) {
        JLabel l = new JLabel(text); l.setFont(Theme.FONT_MONO(11)); l.setForeground(Theme.TEXT3); return l;
    }

    private JCheckBox makeCheckBox(boolean checked) {
        JCheckBox cb = new JCheckBox(); cb.setSelected(checked);
        cb.setBackground(Theme.CARD); cb.setForeground(Theme.TEXT2); return cb;
    }

    private JPanel makeThemeBtns() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); p.setOpaque(false);
        JButton dark  = UIComponents.makeButton("🌙 Dark", Theme.CYAN, Theme.CYAN);
        JButton light = UIComponents.makeButton("☀️ Light", Theme.TEXT2, Theme.BG4);
        dark.addActionListener(e -> MainWindow.showToast("Dark theme active (default)", true));
        light.addActionListener(e -> MainWindow.showToast("Light theme coming soon!", false));
        p.add(dark); p.add(light); return p;
    }

    private JButton makeSimCheckBtn() {
        JButton btn = UIComponents.makeButton("↻ Check All Prices Now", Theme.CYAN, Theme.CYAN);
        btn.addActionListener(e -> MainWindow.simulatePriceCheck());
        return btn;
    }

    private JPanel makeDataBtns() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0)); p.setOpaque(false);
        JButton exp = UIComponents.makeButton("📥 Export CSV", Theme.TEXT2, Theme.BG4);
        JButton rst = UIComponents.makeButton("⚠ Reset Data", Theme.RED, Theme.RED);
        exp.addActionListener(e -> exportCSV());
        rst.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null,"Reset all data to defaults?","Confirm",JOptionPane.YES_NO_OPTION)==0) {
                MainWindow.resetData(); MainWindow.showToast("Data reset!",true);
            }
        });
        p.add(exp); p.add(rst); return p;
    }

    private void exportCSV() {
        StringBuilder sb = new StringBuilder();
        sb.append("PRODUCTS\nName,Platform,Current,Original,Discount%,Target\n");
        for (Product p : AppData.products)
            sb.append(String.format("\"%s\",%s,%.0f,%.0f,%d%%,%.0f\n",
                    p.name, p.platform, p.current, p.original, p.getDiscount(), p.target));
        sb.append("\nEXPENSES\nTitle,Amount,Category,Date,Platform,Payment\n");
        for (Expense e : AppData.expenses)
            sb.append(String.format("\"%s\",%.0f,%s,%s,%s,%s\n",
                    e.title, e.amount, e.type, e.date, e.platform, e.payment));

        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("PriceDrop_Export.csv"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (PrintWriter pw = new PrintWriter(fc.getSelectedFile())) {
                pw.print(sb);
                MainWindow.showToast("Exported to CSV!", true);
            } catch (IOException ex) {
                MainWindow.showToast("Export failed: " + ex.getMessage(), false);
            }
        }
    }
}
