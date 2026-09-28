package pricedrop.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import pricedrop.data.AppData;
import pricedrop.model.Alert;
import pricedrop.model.Product;
import pricedrop.util.Theme;

public class MainWindow extends JFrame {

    private static MainWindow instance;

    private JPanel contentArea;
    private final CardLayout cardLayout = new CardLayout();
    private JLabel topbarTitle;
    private JButton topAddBtn;
    private JLabel alertBadge;

    // Panels
    private DashboardPanel dashPanel;
    private ProductsPanel productsPanel;
    private ExpensesPanel expensesPanel;
    private BudgetPanel budgetPanel;
    private AlertsPanel alertsPanel;

    // Nav buttons
    private final List<JButton> navBtns = new ArrayList<>();

    public MainWindow() {
        instance = this;
        setTitle("PriceDrop — Smart Price & Expense Tracker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 760);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null);

        // Main layout
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.BG);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        root.setOpaque(true);
        root.setBackground(Theme.BG);

        root.add(buildSidebar(), BorderLayout.WEST);
        root.add(buildMain(), BorderLayout.CENTER);

        setContentPane(root);
        setVisible(true);

        // Show dashboard by default
        showPanel("dashboard", navBtns.get(0));
    }

    // ── Sidebar ──────────────────────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(Theme.SIDEBAR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Right glow line
                g2.setColor(new Color(0x00, 0xd4, 0xff, 30));
                g2.setStroke(new BasicStroke(1));
                g2.drawLine(getWidth()-1, 0, getWidth()-1, getHeight());
                g2.dispose();
            }
        };
        sidebar.setOpaque(false);
        sidebar.setPreferredSize(new Dimension(230, 0));

        // Logo
        JPanel logo = new JPanel(null);
        logo.setOpaque(false);
        logo.setPreferredSize(new Dimension(230, 78));
        logo.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        JLabel logoIcon = new JLabel("💰");
        logoIcon.setFont(new Font("Dialog", Font.PLAIN, 28));
        logoIcon.setBounds(18, 14, 36, 36);

        JLabel logoText = new JLabel("PriceDrop");
        logoText.setFont(Theme.FONT_TITLE(21));
        logoText.setForeground(Theme.CYAN);
        logoText.setBounds(56, 14, 160, 28);

        JLabel logoSub = new JLabel("TRACK · SAVE · SPEND SMART");
        logoSub.setFont(Theme.FONT_MONO(8));
        logoSub.setForeground(Theme.TEXT3);
        logoSub.setBounds(56, 44, 160, 14);

        logo.add(logoIcon); logo.add(logoText); logo.add(logoSub);
        sidebar.add(logo, BorderLayout.NORTH);

        // Nav
        JPanel nav = new JPanel();
        nav.setOpaque(false);
        nav.setLayout(new BoxLayout(nav, BoxLayout.Y_AXIS));
        nav.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));

        addNavSection(nav, "MAIN");
        addNavBtn(nav, "⊞", "Dashboard", "dashboard");
        addNavBtn(nav, "🏷️", "Products", "products");
        addNavSection(nav, "FINANCE");
        addNavBtn(nav, "₹", "Expenses", "expenses");
        addNavBtn(nav, "📊", "Budget", "budget");
        addNavSection(nav, "SYSTEM");

        // Alerts button with badge
        JButton alertBtn = addNavBtn(nav, "🔔", "Alerts", "alerts");
        // Badge
        alertBadge = new JLabel("3") {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.RED); g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                g2.dispose(); super.paintComponent(g);
            }
        };
        alertBadge.setForeground(Color.WHITE);
        alertBadge.setFont(Theme.FONT_MONO(10));
        alertBadge.setHorizontalAlignment(SwingConstants.CENTER);
        alertBadge.setOpaque(false);
        alertBadge.setPreferredSize(new Dimension(22, 16));
        // Inject badge into alert button
        alertBtn.setLayout(new BorderLayout());
        alertBtn.add(alertBadge, BorderLayout.EAST);

        addNavBtn(nav, "⚙️", "Settings", "settings");

        JScrollPane navScroll = new JScrollPane(nav);
        navScroll.setBorder(null); navScroll.setOpaque(false);
        navScroll.getViewport().setOpaque(false);
        sidebar.add(navScroll, BorderLayout.CENTER);

        // Footer
        JPanel footer = buildSidebarFooter();
        sidebar.add(footer, BorderLayout.SOUTH);

        return sidebar;
    }

    private void addNavSection(JPanel nav, String label) {
        JLabel sec = new JLabel(label);
        sec.setFont(Theme.FONT_MONO(9));
        sec.setForeground(Theme.TEXT3);
        sec.setBorder(BorderFactory.createEmptyBorder(10, 14, 5, 0));
        sec.setAlignmentX(LEFT_ALIGNMENT);
        nav.add(sec);
    }

    private JButton addNavBtn(JPanel nav, String icon, String label, String panelName) {
        JButton btn = new JButton() {
            private boolean hovered = false;
            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered=true; repaint(); }
                    @Override public void mouseExited(MouseEvent e) { hovered=false; repaint(); }
                });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                boolean active = "active".equals(getClientProperty("state"));
                if (active) {
                    g2.setColor(Theme.cyan10());
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                    g2.setColor(new Color(0x00,0xd4,0xff,50));
                    g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,10,10);
                    g2.setColor(Theme.CYAN);
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine(0,8,0,getHeight()-8);
                } else if (hovered) {
                    g2.setColor(Theme.BORDER);
                    g2.fillRoundRect(0,0,getWidth(),getHeight(),10,10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.putClientProperty("panelName", panelName);
        btn.setForeground(Theme.TEXT2);
        btn.setFont(Theme.FONT_BODY(12));
        btn.setText("  " + icon + "  " + label);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(e -> showPanel(panelName, btn));
        navBtns.add(btn);
        nav.add(btn);
        nav.add(Box.createVerticalStrut(3));
        return btn;
    }

    private JPanel buildSidebarFooter() {
        JPanel footer = new JPanel(new BorderLayout(8,0)) {
            @Override protected void paintComponent(Graphics g) {
                g.setColor(Theme.SIDEBAR); g.fillRect(0,0,getWidth(),getHeight());
                super.paintComponent(g);
            }
        };
        footer.setOpaque(false);
        footer.setPreferredSize(new Dimension(230, 50));
        footer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1,0,0,0, Theme.BORDER),
            BorderFactory.createEmptyBorder(10,16,10,16)
        ));

        JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0,230,118,20)); g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                g2.setColor(new Color(0,230,118,50)); g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,20,20);
                g2.dispose(); super.paintComponent(g);
            }
        };
        status.setOpaque(false);

        JLabel dot = new JLabel("●");
        dot.setForeground(Theme.GREEN); dot.setFont(Theme.FONT_BODY(8));
        JLabel text = new JLabel("Monitoring");
        text.setForeground(Theme.GREEN); text.setFont(Theme.FONT_BOLD(10));
        status.add(dot); status.add(text);
        footer.add(status, BorderLayout.CENTER);

        return footer;
    }

    // ── Main area ────────────────────────────────────────────────────────────
    private JPanel buildMain() {
        JPanel main = new JPanel(new BorderLayout());
        main.setOpaque(false);

        main.add(buildTopbar(), BorderLayout.NORTH);

        contentArea = new JPanel(cardLayout);
        contentArea.setOpaque(false);
        contentArea.setBorder(BorderFactory.createEmptyBorder(22, 24, 0, 24));

        dashPanel     = new DashboardPanel();
        productsPanel = new ProductsPanel();
        expensesPanel = new ExpensesPanel();
        budgetPanel   = new BudgetPanel();
        alertsPanel   = new AlertsPanel();
        SettingsPanel settingsPanel = new SettingsPanel();

        contentArea.add(dashPanel, "dashboard");
        contentArea.add(productsPanel, "products");
        contentArea.add(expensesPanel, "expenses");
        contentArea.add(budgetPanel, "budget");
        contentArea.add(alertsPanel, "alerts");
        contentArea.add(settingsPanel, "settings");

        main.add(contentArea, BorderLayout.CENTER);
        return main;
    }

    private JPanel buildTopbar() {
        JPanel topbar = new JPanel(new BorderLayout(10, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setColor(Theme.TOPBAR); g2.fillRect(0,0,getWidth(),getHeight());
                // bottom accent
                g2.setColor(Theme.BORDER); g2.fillRect(0,getHeight()-1,getWidth(),1);
                GradientPaint gp = new GradientPaint(0,0,new Color(0,0,0,0), getWidth()/2,0, new Color(0x00,0xd4,0xff,25));
                g2.setPaint(gp); g2.fillRect(0,getHeight()-1,getWidth()/2,1);
                g2.dispose();
            }
        };
        topbar.setOpaque(false);
        topbar.setPreferredSize(new Dimension(0, 54));
        topbar.setBorder(BorderFactory.createEmptyBorder(0, 22, 0, 22));

        topbarTitle = new JLabel("Dashboard");
        topbarTitle.setFont(Theme.FONT_TITLE(15));
        topbarTitle.setForeground(Theme.TEXT);
        topbar.add(topbarTitle, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        actions.setOpaque(false);

        JButton checkBtn = UIComponents.makeButton("↻ Check Prices", Theme.CYAN, Theme.CYAN);
        checkBtn.addActionListener(e -> simulatePriceCheck());

        topAddBtn = UIComponents.makeButton("+ Add", Theme.CYAN, Theme.BG4);
        topAddBtn.setVisible(false);

        actions.add(checkBtn);
        actions.add(topAddBtn);
        topbar.add(actions, BorderLayout.EAST);
        return topbar;
    }

    // ── Navigation ───────────────────────────────────────────────────────────
    private void showPanel(String name, JButton activeBtn) {
        cardLayout.show(contentArea, name);
        navBtns.forEach(b -> {
            b.putClientProperty("state", "");
            b.setForeground(Theme.TEXT2);
        });
        if (activeBtn != null) {
            activeBtn.putClientProperty("state", "active");
            activeBtn.setForeground(Theme.CYAN);
        }
        if (topbarTitle != null) {
            String title;
            switch (name) {
                case "dashboard":
                    title = "Dashboard";
                    break;
                case "products":
                    title = "Product Tracker";
                    break;
                case "expenses":
                    title = "Expense Tracker";
                    break;
                case "budget":
                    title = "Budget Manager";
                    break;
                case "alerts":
                    title = "Alerts Center";
                    break;
                default:
                    title = "Settings";
                    break;
            }
            topbarTitle.setText(title);
        }
        if (topAddBtn != null) {
            boolean showAdd = "products".equals(name) || "expenses".equals(name);
            topAddBtn.setVisible(showAdd);
            topAddBtn.setText("+ " + (name.equals("products") ? "Add Product" : "Add Expense"));
        }

        // Refresh active panel
        switch (name) {
            case "dashboard":
                dashPanel.refresh();
                break;
            case "products":
                productsPanel.refresh();
                break;
            case "expenses":
                expensesPanel.refresh();
                break;
            case "budget":
                budgetPanel.refresh();
                break;
            case "alerts":
                alertsPanel.refresh();
                break;
        }
        navBtns.forEach(JComponent::repaint);
    }

    // ── Price Check Simulation ───────────────────────────────────────────────
    public static void simulatePriceCheck() {
        showToast("Checking prices...", true);
        Timer timer = new Timer(1800, e -> {
            int drops = 0;
            Random rng = new Random();
            for (Product p : AppData.products) {
                if (rng.nextDouble() < 0.35) {
                    double drop = Math.round(p.current * (0.01 + rng.nextDouble() * 0.04));
                    double old  = p.current;
                    p.current = Math.max(p.current - drop, p.original * 0.45);
                    p.lowest  = Math.min(p.lowest, p.current);
                    AppData.alerts.add(0, new Alert(AppData.nextAlertId++, "PRICE_DROP",
                            "Price Drop: " + truncate(p.name, 35),
                            "Dropped by " + AppData.fmtFull(drop) + " on " + p.platform + " — now " + AppData.fmtFull(p.current),
                            drop, "Just now", false));
                    if (p.target > 0 && p.current <= p.target) {
                        AppData.alerts.add(0, new Alert(AppData.nextAlertId++, "TARGET_REACHED",
                                "🎯 Target Reached!", truncate(p.name, 40) + " is at your target price!",
                                old - p.current, "Just now", false));
                    }
                    drops++;
                }
            }
            updateAlertBadge();
            String msg = drops > 0
                    ? drops + " price drop" + (drops > 1 ? "s" : "") + " found! Check Alerts."
                    : "All prices checked — no changes.";
            showToast(msg, true);
            if (instance != null) {
                instance.dashPanel.refresh();
                instance.alertsPanel.refresh();
            }
        });
        timer.setRepeats(false);
        timer.start();
    }

    // ── Alert badge ──────────────────────────────────────────────────────────
    public static void updateAlertBadge() {
        if (instance == null || instance.alertBadge == null) return;
        int n = AppData.getUnreadAlertCount();
        instance.alertBadge.setText(String.valueOf(n));
        instance.alertBadge.setVisible(n > 0);
        instance.navBtns.forEach(JComponent::repaint);
    }

    // ── Toast ────────────────────────────────────────────────────────────────
    public static void showToast(String message, boolean success) {
        if (instance == null) return;
        JWindow toast = new JWindow(instance);
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(17,20,34,230)); g2.fillRoundRect(0,0,getWidth(),getHeight(),12,12);
                Color brd = success ? new Color(0,230,118,75) : new Color(255,75,106,75);
                g2.setColor(brd); g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,12,12);
                g2.dispose(); super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        JLabel icon = new JLabel(success ? "✅" : "❌");
        icon.setFont(new Font("Dialog", Font.PLAIN, 14));
        JLabel msg = new JLabel(message);
        msg.setForeground(success ? Theme.GREEN : Theme.RED);
        msg.setFont(Theme.FONT_BODY(12));
        panel.add(icon); panel.add(msg);
        toast.setContentPane(panel);
        toast.pack();

        // Position bottom-right
        Point loc = instance.getLocation();
        Dimension win = instance.getSize();
        toast.setLocation(loc.x + win.width - toast.getWidth() - 26, loc.y + win.height - toast.getHeight() - 26);
        toast.setVisible(true);

        Timer hide = new Timer(3000, e2 -> toast.dispose());
        hide.setRepeats(false); hide.start();
    }

    // ── Reset ────────────────────────────────────────────────────────────────
    public static void resetData() {
        AppData.products.clear();
        AppData.expenses.clear();
        AppData.budgets.clear();
        AppData.alerts.clear();
        // Re-add defaults
        AppData.products.addAll(List.of(
            new Product(1,"Samsung Galaxy S24 Ultra 12GB","https://amazon.in/dp/B0CRDHKBCQ","Amazon","Electronics",109999,134999,99999),
            new Product(2,"Apple iPhone 15 128GB Blue","https://flipkart.com/apple-iphone-15","Flipkart","Electronics",69999,79900,65000),
            new Product(3,"Sony WH-1000XM5 Headphones","https://amazon.in/dp/B09XS7JWHH","Amazon","Electronics",24990,34990,22000),
            new Product(4,"Nike Air Max 270 Running Shoes","https://myntra.com/nike/air-max-270","Myntra","Fashion",7495,11995,6000),
            new Product(5,"LG 43\" 4K Smart TV","https://flipkart.com/lg-43up7500","Flipkart","Electronics",32999,42990,30000),
            new Product(6,"boAt Airdopes 141 TWS Earbuds","https://amazon.in/dp/B09TDLNQ9T","Amazon","Electronics",1299,2990,999)
        ));
        if (instance != null) {
            instance.dashPanel.refresh();
            instance.productsPanel.refresh();
            instance.expensesPanel.refresh();
            instance.budgetPanel.refresh();
            instance.alertsPanel.refresh();
            updateAlertBadge();
        }
    }

    private static String truncate(String s, int max) {
        return s.length() > max ? s.substring(0, max) + "…" : s;
    }
}
