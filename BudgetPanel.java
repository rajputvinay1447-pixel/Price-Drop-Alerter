package pricedrop.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Window;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import pricedrop.data.AppData;
import pricedrop.model.Budget;
import pricedrop.util.Theme;

public class BudgetPanel extends JPanel {

    private JLabel budTotal, budSpent, budOver;
    private JPanel budgetGrid;

    public BudgetPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildHeader() {
        JPanel wrap = new JPanel(new BorderLayout());
        wrap.setOpaque(false);

        JLabel title = UIComponents.makeSectionTitle("Budget Manager");
        JLabel sub = new JLabel("Set monthly category limits · Get alerts before overspending");
        sub.setFont(Theme.FONT_BODY(11)); sub.setForeground(Theme.TEXT3);
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false); titleBox.add(title); titleBox.add(sub);
        wrap.add(titleBox, BorderLayout.WEST);

        JButton addBtn = UIComponents.makeButton("+ Set Budget", Theme.PURPLE, Theme.PURPLE);
        addBtn.addActionListener(e -> openAddDialog());
        wrap.add(addBtn, BorderLayout.EAST);
        return wrap;
    }

    private JPanel buildContent() {
        JPanel outer = new JPanel();
        outer.setOpaque(false);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

        // Stats row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 12, 0));
        statsRow.setOpaque(false); statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        statsRow.setBorder(BorderFactory.createEmptyBorder(14, 0, 14, 0));

        JPanel bTotal = makeStatCard("Total Budget", "₹26K", "this month limit", Theme.CYAN);
        JPanel bSpent = makeStatCard("Total Spent", "₹7.4K", "across categories", Theme.ORANGE);
        JPanel bOver  = makeStatCard("Over Budget", "0", "categories", Theme.RED);
        budTotal = getVal(bTotal); budSpent = getVal(bSpent); budOver = getVal(bOver);

        statsRow.add(bTotal); statsRow.add(bSpent); statsRow.add(bOver);
        outer.add(statsRow);

        // Budget grid
        budgetGrid = new JPanel(new GridLayout(0, 2, 14, 14));
        budgetGrid.setOpaque(false);
        budgetGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        outer.add(budgetGrid);

        return outer;
    }

    private JPanel makeStatCard(String label, String val, String sub, Color accent) {
        JPanel card = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(Theme.BORDER); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                GradientPaint gp=new GradientPaint(0,0,accent,getWidth(),0,accent.darker());
                g2.setPaint(gp); g2.fillRoundRect(0,0,getWidth(),3,3,3);
                g2.setColor(new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),38));
                g2.fillOval(getWidth()-75,getHeight()-75,110,110);
                g2.dispose();
            }
        };
        card.setOpaque(false); card.setPreferredSize(new Dimension(180,100));
        JLabel lbl=new JLabel(label.toUpperCase()); lbl.setFont(Theme.FONT_MONO(9)); lbl.setForeground(Theme.TEXT3); lbl.setBounds(14,12,160,13);
        JLabel vl=new JLabel(val); vl.setFont(Theme.FONT_TITLE(22)); vl.setForeground(accent); vl.setBounds(14,28,160,30); vl.setName("val");
        JLabel sl=new JLabel(sub); sl.setFont(Theme.FONT_BODY(10)); sl.setForeground(Theme.TEXT3); sl.setBounds(14,60,160,14);
        card.add(lbl); card.add(vl); card.add(sl);
        return card;
    }

    private JLabel getVal(JPanel c) {
        for (Component x : c.getComponents())
            if ("val".equals(((JComponent)x).getName())) return (JLabel)x;
        return new JLabel();
    }

    public final void refresh() {
        double tL = AppData.budgets.stream().mapToDouble(b->b.limit).sum();
        double tS = AppData.budgets.stream().mapToDouble(b->b.spent).sum();
        long   ov = AppData.budgets.stream().filter(Budget::isOver).count();

        if (budTotal!=null) budTotal.setText(AppData.fmt(tL));
        if (budSpent!=null) budSpent.setText(AppData.fmt(tS));
        if (budOver !=null) budOver.setText(String.valueOf(ov));

        if (budgetGrid == null) return;
        budgetGrid.removeAll();

        if (AppData.budgets.isEmpty()) {
            JLabel empty = new JLabel("<html><center>📊<br><span style='color:#4a5070'>No budgets set.<br>Click \"+ Set Budget\" to get started.</span></center></html>", SwingConstants.CENTER);
            empty.setFont(Theme.FONT_BODY(12)); empty.setForeground(Theme.TEXT3);
            budgetGrid.add(empty);
        } else {
            for (Budget b : AppData.budgets) budgetGrid.add(makeBudgetCard(b));
        }

        budgetGrid.revalidate(); budgetGrid.repaint();
    }

    private JPanel makeBudgetCard(Budget b) {
        int pct = b.getPercent();
        Color col = pct >= 100 ? Theme.RED : pct >= 80 ? Theme.ORANGE : pct >= 60 ? new Color(0xf1c40f) : Theme.GREEN;

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
        card.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Header row
        JPanel header = new JPanel(new BorderLayout()); header.setOpaque(false);
        JLabel name = new JLabel(AppData.getCategoryEmoji(b.category) + " " + b.category);
        name.setFont(Theme.FONT_BOLD(13)); name.setForeground(Theme.TEXT);
        JLabel pctLbl = new JLabel(pct + "%");
        pctLbl.setFont(Theme.FONT_MONO(14)); pctLbl.setForeground(col);
        header.add(name, BorderLayout.WEST); header.add(pctLbl, BorderLayout.EAST);
        card.add(header); card.add(Box.createVerticalStrut(10));

        // Progress bar
        JPanel prog = UIComponents.makeProgressBar(pct, col);
        prog.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        prog.setMinimumSize(new Dimension(10, 10));
        card.add(prog); card.add(Box.createVerticalStrut(8));

        // Amounts row
        JPanel amounts = new JPanel(new BorderLayout()); amounts.setOpaque(false);
        JLabel spent = new JLabel("Spent: " + AppData.fmtFull(b.spent));
        spent.setFont(Theme.FONT_MONO(11)); spent.setForeground(Theme.TEXT3);
        JLabel limit = new JLabel("Limit: " + AppData.fmtFull(b.limit));
        limit.setFont(Theme.FONT_MONO(11)); limit.setForeground(Theme.TEXT3);
        amounts.add(spent, BorderLayout.WEST); amounts.add(limit, BorderLayout.EAST);
        card.add(amounts); card.add(Box.createVerticalStrut(5));

        // Remaining/over
        JLabel remain;
        if (b.isOver()) {
            remain = new JLabel("⚠️ Over by " + AppData.fmtFull(b.spent - b.limit));
            remain.setForeground(Theme.RED);
        } else {
            remain = new JLabel("✅ Remaining: " + AppData.fmtFull(b.limit - b.spent));
            remain.setForeground(Theme.GREEN);
        }
        remain.setFont(Theme.FONT_BODY(11));
        card.add(remain);

        return card;
    }

    private void openAddDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner instanceof Frame ? (Frame)owner : null, "📊 Set Monthly Budget", true);
        dlg.getContentPane().setBackground(Theme.BG2);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));

        JLabel titleLbl = new JLabel("📊 Set Monthly Budget");
        titleLbl.setFont(Theme.FONT_TITLE(16)); titleLbl.setForeground(Theme.TEXT);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0,0,18,0));
        form.add(titleLbl);

        JComboBox<String> fCat = addCombo(form, "Category",
                new String[]{"Purchase","Food","Transport","Bill","Entertainment","Healthcare","Education","Savings","Other"});
        JTextField fLimit = addField(form, "Monthly Limit (₹) *", "");
        addField(form, "Alert when you reach (%)", "80");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); actions.setOpaque(false);
        JButton cancel = UIComponents.makeButton("Cancel", Theme.TEXT2, Theme.BG4);
        JButton save   = UIComponents.makeButton("✅ Save Budget", Theme.PURPLE, Theme.PURPLE);
        cancel.addActionListener(e->dlg.dispose());
        save.addActionListener(e->{
            String lStr=fLimit.getText().trim();
            if(lStr.isEmpty()){JOptionPane.showMessageDialog(dlg,"Enter a valid limit","Error",JOptionPane.ERROR_MESSAGE);return;}
            double limit; try{limit=Double.parseDouble(lStr);}catch(NumberFormatException ex){JOptionPane.showMessageDialog(dlg,"Invalid limit","Error",JOptionPane.ERROR_MESSAGE);return;}
            if(limit<=0){JOptionPane.showMessageDialog(dlg,"Limit must be > 0","Error",JOptionPane.ERROR_MESSAGE);return;}
            AppData.budgets.add(new Budget(AppData.nextBudgetId++,(String)fCat.getSelectedItem(),limit,0));
            dlg.dispose(); refresh(); MainWindow.showToast("Budget set!",true);
        });
        actions.add(cancel); actions.add(save);
        form.add(Box.createVerticalStrut(10)); form.add(UIComponents.makeSep());
        form.add(Box.createVerticalStrut(10)); form.add(actions);

        dlg.setContentPane(form);
        dlg.pack(); dlg.setMinimumSize(new Dimension(420,200));
        dlg.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
    }

    private JTextField addField(JPanel p, String lbl, String val) {
        JPanel row=new JPanel(new BorderLayout(0,5)); row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE,70));
        JLabel l=new JLabel(lbl.toUpperCase()); l.setFont(Theme.FONT_MONO(9)); l.setForeground(Theme.TEXT3);
        JTextField tf=UIComponents.makeTextField(""); tf.setText(val); tf.setPreferredSize(new Dimension(360,36));
        row.add(l,BorderLayout.NORTH); row.add(tf,BorderLayout.CENTER);
        p.add(row); p.add(Box.createVerticalStrut(10)); return tf;
    }

    private JComboBox<String> addCombo(JPanel p, String lbl, String[] items) {
        JPanel row=new JPanel(new BorderLayout(0,5)); row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE,70));
        JLabel l=new JLabel(lbl.toUpperCase()); l.setFont(Theme.FONT_MONO(9)); l.setForeground(Theme.TEXT3);
        JComboBox<String> cb=UIComponents.makeCombo(items);
        row.add(l,BorderLayout.NORTH); row.add(cb,BorderLayout.CENTER);
        p.add(row); p.add(Box.createVerticalStrut(10)); return cb;
    }
}
