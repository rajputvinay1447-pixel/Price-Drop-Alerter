package pricedrop.ui;

import pricedrop.data.AppData;
import pricedrop.model.Alert;
import pricedrop.util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AlertsPanel extends JPanel {

    private JLabel countLabel;
    private JPanel alertsList;

    public AlertsPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildList(), BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildHeader() {
        JPanel wrap = new JPanel(new BorderLayout(12, 0));
        wrap.setOpaque(false);

        JPanel titleBox = new JPanel(new GridLayout(2,1,0,3));
        titleBox.setOpaque(false);
        titleBox.add(UIComponents.makeSectionTitle("Alerts Center"));
        countLabel = new JLabel("");
        countLabel.setFont(Theme.FONT_BODY(11)); countLabel.setForeground(Theme.TEXT3);
        titleBox.add(countLabel);
        wrap.add(titleBox, BorderLayout.WEST);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btns.setOpaque(false);
        JButton markAll = UIComponents.makeButton("✓ Mark All Read", Theme.TEXT2, Theme.BG4);
        JButton clear   = UIComponents.makeButton("🗑 Clear All", Theme.RED, Theme.RED);
        markAll.addActionListener(e -> {
            AppData.alerts.forEach(a -> a.read = true);
            refresh(); MainWindow.updateAlertBadge();
            MainWindow.showToast("All alerts marked as read", true);
        });
        clear.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null, "Clear all alerts?", "Confirm", JOptionPane.YES_NO_OPTION) == 0) {
                AppData.alerts.clear(); refresh(); MainWindow.updateAlertBadge();
                MainWindow.showToast("Alerts cleared", true);
            }
        });
        btns.add(markAll); btns.add(clear);
        wrap.add(btns, BorderLayout.EAST);
        return wrap;
    }

    private JScrollPane buildList() {
        alertsList = new JPanel();
        alertsList.setOpaque(false);
        alertsList.setLayout(new BoxLayout(alertsList, BoxLayout.Y_AXIS));
        return UIComponents.scroll(alertsList);
    }

    public void refresh() {
        int total = AppData.alerts.size();
        int unread = AppData.getUnreadAlertCount();
        if (countLabel != null)
            countLabel.setText(total + " total · " + unread + " unread");

        if (alertsList == null) return;
        alertsList.removeAll();

        if (AppData.alerts.isEmpty()) {
            JLabel empty = new JLabel("<html><center>🔔<br><br><span style='color:#4a5070'>No alerts yet.<br>Price drop alerts will appear automatically.</span></center></html>", SwingConstants.CENTER);
            empty.setFont(Theme.FONT_BODY(12)); empty.setForeground(Theme.TEXT3);
            empty.setAlignmentX(CENTER_ALIGNMENT);
            JPanel ep = new JPanel(new BorderLayout()); ep.setOpaque(false);
            ep.add(empty, BorderLayout.CENTER); alertsList.add(ep);
        } else {
            for (Alert a : AppData.alerts) alertsList.add(makeAlertCard(a));
        }

        alertsList.revalidate(); alertsList.repaint();
    }

    private JPanel makeAlertCard(Alert a) {
        JPanel card = new JPanel(new BorderLayout(12, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = a.read ? Theme.CARD : new Color(0x4f,0x9e,0xff, 10);
                g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                Color border = a.read ? Theme.BORDER : new Color(0x4f,0x9e,0xff, 55);
                g2.setColor(border); g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                // unread left bar
                if (!a.read) {
                    g2.setColor(Theme.CYAN); g2.setStroke(new BasicStroke(3));
                    g2.drawLine(1,12,1,getHeight()-12);
                }
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                a.read = true; refresh(); MainWindow.updateAlertBadge();
            }
            public void mouseEntered(MouseEvent e) { card.setBorder(BorderFactory.createEmptyBorder(12,17,12,14)); card.repaint(); }
            public void mouseExited(MouseEvent e)  { card.setBorder(BorderFactory.createEmptyBorder(12,14,12,14)); card.repaint(); }
        });

        JLabel ico = new JLabel(a.getEmoji());
        ico.setFont(new Font("Dialog", Font.PLAIN, 22));
        ico.setPreferredSize(new Dimension(36, 36));
        card.add(ico, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(a.savings>0?3:2, 1, 0, 3));
        info.setOpaque(false);
        JLabel title = new JLabel(a.title);
        title.setFont(Theme.FONT_BOLD(12)); title.setForeground(a.read ? Theme.TEXT2 : Theme.TEXT);
        JLabel msg = new JLabel("<html>" + a.message + "</html>");
        msg.setFont(Theme.FONT_BODY(11)); msg.setForeground(Theme.TEXT3);
        info.add(title); info.add(msg);
        if (a.savings > 0) {
            JLabel save = new JLabel("💰 Save " + AppData.fmtFull(a.savings));
            save.setFont(Theme.FONT_BOLD(10)); save.setForeground(Theme.GREEN);
            info.add(save);
        }
        card.add(info, BorderLayout.CENTER);

        JPanel right = new JPanel(new GridLayout(2,1,0,4)); right.setOpaque(false);
        JLabel time = new JLabel(a.time, SwingConstants.RIGHT);
        time.setFont(Theme.FONT_MONO(10)); time.setForeground(Theme.TEXT3);
        right.add(time);
        if (!a.read) {
            JLabel dot = new JLabel("●", SwingConstants.RIGHT);
            dot.setForeground(Theme.BLUE); dot.setFont(Theme.FONT_BODY(10));
            right.add(dot);
        }
        card.add(right, BorderLayout.EAST);

        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);
        wrap.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        wrap.add(card, BorderLayout.CENTER);
        return wrap;
    }
}
