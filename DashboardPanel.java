package pricedrop.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.util.Comparator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import pricedrop.data.AppData;
import pricedrop.model.Alert;
import pricedrop.model.Product;
import pricedrop.util.Theme;

public class DashboardPanel extends JPanel {

    private JLabel statProducts, statSavings, statMonth, statAlerts;
    private JLabel amzCount, fkCount, mynCount;
    private JLabel amzSavings, fkSavings, mynSavings;
    private JPanel dealsContainer, alertsContainer;

    public DashboardPanel() {
        setOpaque(false);
        setLayout(new BorderLayout());
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(4, 0, 20, 0));

        content.add(buildStatRow());
        content.add(Box.createVerticalStrut(14));
        content.add(buildPlatformRow());
        content.add(Box.createVerticalStrut(14));
        content.add(buildTwoCol());

        add(UIComponents.scroll(content), BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildStatRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

        JPanel bp = makeStatCard("Tracked Products", "6", "Amazon · Flipkart · Myntra", Theme.CYAN);
        JPanel bs = makeStatCard("Total Savings", "₹61K", "vs original prices", Theme.GREEN);
        JPanel bm = makeStatCard("This Month", "₹7.7K", "total expenses", Theme.ORANGE);
        JPanel ba = makeStatCard("Unread Alerts", "3", "price drops & warnings", Theme.RED);

        statProducts = getValLabel(bp);
        statSavings  = getValLabel(bs);
        statMonth    = getValLabel(bm);
        statAlerts   = getValLabel(ba);

        row.add(bp); row.add(bs); row.add(bm); row.add(ba);
        return row;
    }

    private JPanel makeStatCard(String label, String val, String sub, Color accent) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                GradientPaint gp = new GradientPaint(0, 0, accent, getWidth(), 0, accent.darker());
                g2.setPaint(gp); g2.fillRoundRect(0, 0, getWidth(), 3, 3, 3);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 38));
                g2.fillOval(getWidth()-75, getHeight()-75, 110, 110);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setPreferredSize(new Dimension(180, 100));

        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(Theme.FONT_MONO(9));  lbl.setForeground(Theme.TEXT3);  lbl.setBounds(14,12,160,13);
        JLabel vl = new JLabel(val);
        vl.setFont(Theme.FONT_TITLE(22)); vl.setForeground(accent);       vl.setBounds(14,28,160,30);
        vl.setName("statValue");
        JLabel sl = new JLabel(sub);
        sl.setFont(Theme.FONT_BODY(10));  sl.setForeground(Theme.TEXT3);  sl.setBounds(14,60,160,14);

        card.add(lbl); card.add(vl); card.add(sl);
        return card;
    }

    private JLabel getValLabel(JPanel card) {
        for (Component c : card.getComponents())
            if ("statValue".equals(((JComponent)c).getName())) return (JLabel)c;
        return null;
    }

    private JPanel buildPlatformRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 12, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel amz = makePlatformCard("🛒", "Amazon India", Theme.AMAZON, "amz");
        JPanel fk  = makePlatformCard("🛍️", "Flipkart",     Theme.FLIPKART, "fk");
        JPanel myn = makePlatformCard("👗", "Myntra",        Theme.MYNTRA,  "myn");

        amzCount   = findLabel(amz, "count");   fkCount  = findLabel(fk, "count");  mynCount  = findLabel(myn, "count");
        amzSavings = findLabel(amz, "savings");  fkSavings = findLabel(fk,"savings"); mynSavings = findLabel(myn,"savings");

        row.add(amz); row.add(fk); row.add(myn);
        return row;
    }

    private JPanel makePlatformCard(String icon, String name, Color accent, String key) {
        JPanel card = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 16)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 40));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
                g2.dispose();
            }
        };
        card.setName(key);
        card.setOpaque(false);

        JLabel ico = new JLabel(icon);
        ico.setFont(new Font("Dialog", Font.PLAIN, 26));

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 2));
        info.setOpaque(false);
        JLabel nameL = new JLabel(name);
        nameL.setFont(Theme.FONT_BOLD(13)); nameL.setForeground(accent);
        JLabel countL = new JLabel("0 products");
        countL.setFont(Theme.FONT_BODY(11)); countL.setForeground(Theme.TEXT3);
        countL.setName("count");
        info.add(nameL); info.add(countL);

        JLabel savingsL = new JLabel("");
        savingsL.setFont(Theme.FONT_MONO(15)); savingsL.setForeground(accent);
        savingsL.setName("savings");

        card.add(ico); card.add(info); card.add(savingsL);
        return card;
    }

    private JLabel findLabel(JPanel p, String name) {
        for (Component c : p.getComponents()) {
            if (c instanceof JPanel) {
                JPanel inner = (JPanel) c;
                for (Component cc : inner.getComponents()) {
                    if (cc instanceof JLabel && name.equals(((JComponent) cc).getName())) return (JLabel) cc;
                }
            }
            if (c instanceof JLabel && name.equals(((JComponent) c).getName())) return (JLabel) c;
        }
        return new JLabel();
    }

    private JPanel buildTwoCol() {
        JPanel row = new JPanel(new GridLayout(1, 2, 14, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        // Best Deals card
        JPanel dealsCard = makeCard("🔥 Best Deals Now");
        dealsContainer = getContentPane(dealsCard);
        JPanel alertsCard = makeCard("🔔 Recent Alerts");
        alertsContainer = getContentPane(alertsCard);

        row.add(dealsCard); row.add(alertsCard);
        return row;
    }

    private JPanel makeCard(String title) {
        JPanel outer = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        outer.setOpaque(false);
        outer.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JLabel ttl = UIComponents.makeCardTitle(title);
        ttl.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
        outer.add(ttl, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setName("content");
        outer.add(content, BorderLayout.CENTER);
        return outer;
    }

    private JPanel getContentPane(JPanel card) {
        for (Component c : card.getComponents())
            if ("content".equals(((JComponent)c).getName())) return (JPanel)c;
        return new JPanel();
    }

    public final void refresh() {
        List<Product> prods = AppData.products;
        List<Alert>   alts  = AppData.alerts;

        // Stats
        if (statProducts != null) statProducts.setText(String.valueOf(prods.size()));
        double savings = AppData.getTotalSavings();
        if (statSavings  != null) statSavings.setText(AppData.fmt(savings));
        double monthly = AppData.getMonthExpenses("2025-03");
        if (statMonth    != null) statMonth.setText(AppData.fmt(monthly));
        int unread = AppData.getUnreadAlertCount();
        if (statAlerts   != null) statAlerts.setText(String.valueOf(unread));

        // Platform counts
        long amzC = prods.stream().filter(p -> "Amazon".equals(p.platform)).count();
        long fkC  = prods.stream().filter(p -> "Flipkart".equals(p.platform)).count();
        long mynC = prods.stream().filter(p -> "Myntra".equals(p.platform)).count();
        if (amzCount != null) amzCount.setText(amzC + " product" + (amzC!=1?"s":"") + " tracked");
        if (fkCount  != null) fkCount.setText(fkC  + " product" + (fkC!=1?"s":"")  + " tracked");
        if (mynCount != null) mynCount.setText(mynC + " product" + (mynC!=1?"s":"") + " tracked");

        double amzS = prods.stream().filter(p->"Amazon".equals(p.platform)).mapToDouble(p->Math.max(0,p.original-p.current)).sum();
        double fkS  = prods.stream().filter(p->"Flipkart".equals(p.platform)).mapToDouble(p->Math.max(0,p.original-p.current)).sum();
        double mynS = prods.stream().filter(p->"Myntra".equals(p.platform)).mapToDouble(p->Math.max(0,p.original-p.current)).sum();
        if (amzSavings != null) amzSavings.setText(amzS>0 ? AppData.fmt(amzS) : "");
        if (fkSavings  != null) fkSavings.setText(fkS>0  ? AppData.fmt(fkS)  : "");
        if (mynSavings != null) mynSavings.setText(mynS>0 ? AppData.fmt(mynS) : "");

        // Deals
        if (dealsContainer != null) {
            dealsContainer.removeAll();
            List<Product> sorted = prods.stream()
                    .sorted(Comparator.comparingInt(Product::getDiscount).reversed())
                    .limit(6).toList();
            if (sorted.isEmpty()) {
                dealsContainer.add(makeEmpty("🏷️", "No products yet"));
            } else {
                for (Product p : sorted) dealsContainer.add(makeDealRow(p));
            }
            dealsContainer.revalidate(); dealsContainer.repaint();
        }

        // Alerts
        if (alertsContainer != null) {
            alertsContainer.removeAll();
            if (alts.isEmpty()) {
                alertsContainer.add(makeEmpty("🔔", "No alerts yet"));
            } else {
                alts.stream().limit(5).forEach(a -> alertsContainer.add(makeAlertRow(a)));
            }
            alertsContainer.revalidate(); alertsContainer.repaint();
        }
    }

    private JPanel makeDealRow(Product p) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));

        Color pc;
        switch (p.platform) {
            case "Amazon":
                pc = Theme.AMAZON;
                break;
            case "Flipkart":
                pc = Theme.FLIPKART;
                break;
            default:
                pc = Theme.MYNTRA;
        }
        row.add(UIComponents.makeBadge(p.platform, pc, pc));

        JLabel name = new JLabel(truncate(p.name, 30));
        name.setFont(Theme.FONT_BODY(11)); name.setForeground(Theme.TEXT2);
        row.add(name);

        JLabel price = new JLabel(AppData.fmtFull(p.current));
        price.setFont(Theme.FONT_MONO(12)); price.setForeground(Theme.GREEN);
        row.add(price);

        if (p.getDiscount() > 0) {
            row.add(UIComponents.makeBadge(p.getDiscount()+"% OFF", Theme.RED, Theme.RED));
        }
        return row;
    }

    private JPanel makeAlertRow(Alert a) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JLabel ico = new JLabel(a.getEmoji());
        ico.setFont(new Font("Dialog", Font.PLAIN, 18));
        row.add(ico, BorderLayout.WEST);

        JPanel info = new JPanel(new GridLayout(2, 1, 0, 1));
        info.setOpaque(false);
        JLabel title = new JLabel(truncate(a.title, 35));
        title.setFont(Theme.FONT_BOLD(11));
        title.setForeground(a.read ? Theme.TEXT2 : Theme.TEXT);
        JLabel time = new JLabel(a.time);
        time.setFont(Theme.FONT_MONO(10)); time.setForeground(Theme.TEXT3);
        info.add(title); info.add(time);
        row.add(info, BorderLayout.CENTER);

        if (!a.read) {
            JLabel dot = new JLabel("●");
            dot.setForeground(Theme.BLUE); dot.setFont(Theme.FONT_BODY(8));
            row.add(dot, BorderLayout.EAST);
        }
        return row;
    }

    private JPanel makeEmpty(String icon, String msg) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        JLabel l = new JLabel("<html><center>" + icon + "<br><span style='color:#4a5070'>" + msg + "</span></center></html>", SwingConstants.CENTER);
        l.setFont(Theme.FONT_BODY(12)); l.setForeground(Theme.TEXT3);
        p.add(l, BorderLayout.CENTER);
        return p;
    }

    private String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }
}
