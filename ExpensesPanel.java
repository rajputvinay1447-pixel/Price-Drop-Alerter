package pricedrop.ui;

import pricedrop.data.AppData;
import pricedrop.model.Expense;
import pricedrop.util.Theme;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class ExpensesPanel extends JPanel {

    private JComboBox<String> monthFilter;
    private JComboBox<String> catFilter;
    private JLabel totalLabel;
    private DefaultTableModel tableModel;
    private JPanel summaryCard;
    private JTable table;

    private static final String[] MONTHS = {"2025-03","2025-02","2025-01"};
    private static final String[] MONTH_LABELS = {"March 2025","February 2025","January 2025"};

    public ExpensesPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        add(buildHeader(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildHeader() {
        JPanel wrap = new JPanel(new BorderLayout(0, 10));
        wrap.setOpaque(false);

        JLabel title = UIComponents.makeSectionTitle("Expense Tracker");
        JLabel sub = new JLabel("Log and categorize your spending in ₹");
        sub.setFont(Theme.FONT_BODY(11)); sub.setForeground(Theme.TEXT3);

        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 3));
        titleBox.setOpaque(false);
        titleBox.add(title); titleBox.add(sub);
        wrap.add(titleBox, BorderLayout.NORTH);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        monthFilter = UIComponents.makeCombo(MONTH_LABELS);
        monthFilter.setPreferredSize(new Dimension(140, 34));
        monthFilter.addActionListener(e -> refresh());

        catFilter = UIComponents.makeCombo(new String[]{"All Categories","Purchase","Bill","Food","Transport","Entertainment","Healthcare","Other"});
        catFilter.setPreferredSize(new Dimension(160, 34));
        catFilter.addActionListener(e -> refresh());

        JButton addBtn = UIComponents.makeButton("+ Add Expense", Theme.ORANGE, Theme.ORANGE);
        addBtn.addActionListener(e -> openAddDialog());

        totalLabel = new JLabel("");
        totalLabel.setFont(Theme.FONT_MONO(14)); totalLabel.setForeground(Theme.ORANGE);

        toolbar.add(monthFilter); toolbar.add(catFilter); toolbar.add(addBtn);
        toolbar.add(Box.createHorizontalStrut(20)); toolbar.add(totalLabel);
        wrap.add(toolbar, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildContent() {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);

        // Table
        String[] cols = {"Date","Title","Category","Platform","Payment","Amount",""};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c==6; }
        };
        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) c.setBackground(row%2==0 ? Theme.CARD : Theme.BG3);
                return c;
            }
        };
        styleTable();

        JPanel tableWrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(Theme.BORDER); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.dispose();
            }
        };
        tableWrap.setOpaque(false);
        tableWrap.add(table.getTableHeader(), BorderLayout.NORTH);
        tableWrap.add(UIComponents.scroll(table), BorderLayout.CENTER);

        // Summary card
        summaryCard = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD); g2.fillRoundRect(0,0,getWidth(),getHeight(),14,14);
                g2.setColor(Theme.BORDER); g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,14,14);
                g2.dispose();
            }
        };
        summaryCard.setOpaque(false);
        summaryCard.setLayout(new BoxLayout(summaryCard, BoxLayout.Y_AXIS));
        summaryCard.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        summaryCard.setPreferredSize(new Dimension(280, 100));

        row.add(tableWrap, BorderLayout.CENTER);
        row.add(summaryCard, BorderLayout.EAST);
        return row;
    }

    private void styleTable() {
        table.setBackground(Theme.CARD); table.setForeground(Theme.TEXT2);
        table.setFont(Theme.FONT_BODY(12)); table.setRowHeight(38);
        table.setShowGrid(false); table.setIntercellSpacing(new Dimension(0,0));
        table.setSelectionBackground(new Color(0x00,0xd4,0xff,20));
        table.setSelectionForeground(Theme.TEXT);

        JTableHeader h = table.getTableHeader();
        h.setBackground(new Color(0,0,0,50)); h.setForeground(Theme.TEXT3);
        h.setFont(Theme.FONT_MONO(9)); h.setBorder(BorderFactory.createMatteBorder(0,0,1,0,Theme.BORDER));

        int[] w = {80,200,90,80,95,90,50};
        for (int i=0;i<w.length;i++) table.getColumnModel().getColumn(i).setPreferredWidth(w[i]);

        // Amount column
        table.getColumnModel().getColumn(5).setCellRenderer((t,v,sel,foc,r,c)->{
            DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
            JLabel l = (JLabel)rend.getTableCellRendererComponent(t,v,sel,foc,r,c);
            l.setForeground(Theme.ORANGE); l.setFont(Theme.FONT_MONO(12));
            l.setBackground(sel?new Color(0x00,0xd4,0xff,20):(r%2==0?Theme.CARD:Theme.BG3));
            return l;
        });
        // Category badge
        table.getColumnModel().getColumn(2).setCellRenderer((t,v,sel,foc,r,c)->{
            DefaultTableCellRenderer rend = new DefaultTableCellRenderer();
            JLabel l = (JLabel)rend.getTableCellRendererComponent(t,v,sel,foc,r,c);
            l.setForeground(Theme.PURPLE); l.setFont(Theme.FONT_MONO(10));
            l.setBackground(sel?new Color(0x00,0xd4,0xff,20):(r%2==0?Theme.CARD:Theme.BG3));
            return l;
        });
        // Delete button column
        table.getColumnModel().getColumn(6).setCellRenderer((t,v,sel,foc,r,c)->{
            JButton b = UIComponents.makeButton("🗑", Theme.RED, Theme.RED);
            b.setFont(Theme.FONT_BODY(10));
            b.setBackground(r%2==0 ? Theme.CARD : Theme.BG3);
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER,2,3)); p.setBackground(r%2==0?Theme.CARD:Theme.BG3);
            p.add(b); return p;
        });
        table.getColumnModel().getColumn(6).setCellEditor(new DelEditor());
    }

    public void refresh() {
        String month = MONTHS[monthFilter != null ? monthFilter.getSelectedIndex() : 0];
        String cat   = catFilter != null ? (String)catFilter.getSelectedItem() : "All Categories";

        List<Expense> filtered = AppData.expenses.stream()
                .filter(e -> e.date.startsWith(month))
                .filter(e -> "All Categories".equals(cat) || e.type.equals(cat))
                .collect(Collectors.toList());

        double total = filtered.stream().mapToDouble(e -> e.amount).sum();
        if (totalLabel != null) totalLabel.setText("Total: " + AppData.fmtFull(total));

        tableModel.setRowCount(0);
        for (Expense e : filtered) {
            tableModel.addRow(new Object[]{e.date, e.title, e.type, e.platform, e.payment, AppData.fmtFull(e.amount), e.id});
        }

        // Summary
        if (summaryCard != null) {
            summaryCard.removeAll();
            JLabel bTitle = UIComponents.makeCardTitle("📊 Breakdown");
            bTitle.setAlignmentX(LEFT_ALIGNMENT);
            bTitle.setBorder(BorderFactory.createEmptyBorder(0,0,12,0));
            summaryCard.add(bTitle);

            Map<String,Double> cats = new LinkedHashMap<>();
            filtered.forEach(e -> cats.merge(e.type, e.amount, Double::sum));

            for (Map.Entry<String, Double> entry : cats.entrySet()) {
                String c = entry.getKey(); double amt = entry.getValue();
                int pct = total > 0 ? (int)(amt/total*100) : 0;
                Color col = Theme.getCategoryColor(c);

                JPanel item = new JPanel(new BorderLayout(0,3));
                item.setOpaque(false); item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

                JPanel top = new JPanel(new BorderLayout()); top.setOpaque(false);
                JLabel cLbl = new JLabel(AppData.getCategoryEmoji(c) + " " + c);
                cLbl.setFont(Theme.FONT_BODY(11)); cLbl.setForeground(Theme.TEXT2);
                JLabel aLbl = new JLabel(AppData.fmtFull(amt));
                aLbl.setFont(Theme.FONT_MONO(11)); aLbl.setForeground(Theme.TEXT);
                top.add(cLbl, BorderLayout.WEST); top.add(aLbl, BorderLayout.EAST);

                JPanel prog = UIComponents.makeProgressBar(pct, col);
                prog.setMaximumSize(new Dimension(Integer.MAX_VALUE, 8));
                JLabel pctLbl = new JLabel(pct + "% of total");
                pctLbl.setFont(Theme.FONT_MONO(9)); pctLbl.setForeground(Theme.TEXT3);
                pctLbl.setAlignmentX(RIGHT_ALIGNMENT);

                item.add(top, BorderLayout.NORTH); item.add(prog, BorderLayout.CENTER);
                item.add(pctLbl, BorderLayout.SOUTH);
                summaryCard.add(item); summaryCard.add(Box.createVerticalStrut(8));
            }

            // Total row
            summaryCard.add(UIComponents.makeSep());
            JPanel totRow = new JPanel(new BorderLayout()); totRow.setOpaque(false);
            JLabel totLbl = new JLabel("Month Total");
            totLbl.setFont(Theme.FONT_BODY(11)); totLbl.setForeground(Theme.TEXT3);
            JLabel totVal = new JLabel(AppData.fmtFull(total));
            totVal.setFont(Theme.FONT_MONO(16)); totVal.setForeground(Theme.ORANGE);
            totRow.add(totLbl, BorderLayout.WEST); totRow.add(totVal, BorderLayout.EAST);
            summaryCard.add(Box.createVerticalStrut(10)); summaryCard.add(totRow);
            summaryCard.revalidate(); summaryCard.repaint();
        }
    }

    private void openAddDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner instanceof Frame ? (Frame)owner : null, "➕ Add Expense", true);
        dlg.getContentPane().setBackground(Theme.BG2);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));

        JLabel titleLbl = new JLabel("➕ Add Expense");
        titleLbl.setFont(Theme.FONT_TITLE(16)); titleLbl.setForeground(Theme.TEXT);
        titleLbl.setBorder(BorderFactory.createEmptyBorder(0,0,18,0));
        form.add(titleLbl);

        JTextField fTitle = addField(form, "Title *", "");
        JPanel row1 = new JPanel(new GridLayout(1,2,12,0)); row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JTextField fAmt  = addFieldTo(row1, "Amount (₹) *", "");
        JTextField fDate = addFieldTo(row1, "Date", new java.text.SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        form.add(row1); form.add(Box.createVerticalStrut(10));

        JPanel row2 = new JPanel(new GridLayout(1,2,12,0)); row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JComboBox<String> fCat  = addComboTo(row2,"Category", new String[]{"Purchase","Bill","Food","Transport","Entertainment","Healthcare","Other"});
        JComboBox<String> fPlat = addComboTo(row2,"Platform",  new String[]{"Amazon","Flipkart","Myntra","Other"});
        form.add(row2); form.add(Box.createVerticalStrut(10));
        JComboBox<String> fPay = addCombo(form,"Payment Method", new String[]{"UPI","Card","Net Banking","COD","Cash","Wallet"});

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT,10,0)); actions.setOpaque(false);
        JButton cancel = UIComponents.makeButton("Cancel", Theme.TEXT2, Theme.BG4);
        JButton save   = UIComponents.makeButton("✅ Add Expense", Theme.ORANGE, Theme.ORANGE);
        cancel.addActionListener(e->dlg.dispose());
        save.addActionListener(e->{
            String t=fTitle.getText().trim(); String aStr=fAmt.getText().trim();
            if(t.isEmpty()||aStr.isEmpty()){JOptionPane.showMessageDialog(dlg,"Title and amount required!","Error",JOptionPane.ERROR_MESSAGE);return;}
            double amt; try{amt=Double.parseDouble(aStr);}catch(NumberFormatException ex){JOptionPane.showMessageDialog(dlg,"Invalid amount","Error",JOptionPane.ERROR_MESSAGE);return;}
            AppData.expenses.add(new Expense(AppData.nextExpenseId++,t,amt,(String)fCat.getSelectedItem(),fDate.getText(),(String)fPlat.getSelectedItem(),(String)fPay.getSelectedItem()));
            dlg.dispose(); refresh(); MainWindow.showToast("Expense added!",true);
        });
        actions.add(cancel); actions.add(save);
        form.add(Box.createVerticalStrut(10)); form.add(UIComponents.makeSep());
        form.add(Box.createVerticalStrut(10)); form.add(actions);

        dlg.setContentPane(form);
        dlg.pack(); dlg.setMinimumSize(new Dimension(480,200));
        dlg.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
    }

    private JTextField addField(JPanel p, String lbl, String val) {
        JPanel row=new JPanel(new BorderLayout(0,5)); row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE,70));
        JLabel l=new JLabel(lbl.toUpperCase()); l.setFont(Theme.FONT_MONO(9)); l.setForeground(Theme.TEXT3);
        JTextField tf=UIComponents.makeTextField(""); tf.setText(val); tf.setPreferredSize(new Dimension(400,36));
        row.add(l,BorderLayout.NORTH); row.add(tf,BorderLayout.CENTER);
        p.add(row); p.add(Box.createVerticalStrut(10)); return tf;
    }
    private JTextField addFieldTo(JPanel p, String lbl, String val) {
        JPanel row=new JPanel(new BorderLayout(0,5)); row.setOpaque(false);
        JLabel l=new JLabel(lbl.toUpperCase()); l.setFont(Theme.FONT_MONO(9)); l.setForeground(Theme.TEXT3);
        JTextField tf=UIComponents.makeTextField(""); tf.setText(val);
        row.add(l,BorderLayout.NORTH); row.add(tf,BorderLayout.CENTER); p.add(row); return tf;
    }
    private JComboBox<String> addCombo(JPanel p, String lbl, String[] items) {
        JPanel row=new JPanel(new BorderLayout(0,5)); row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE,70));
        JLabel l=new JLabel(lbl.toUpperCase()); l.setFont(Theme.FONT_MONO(9)); l.setForeground(Theme.TEXT3);
        JComboBox<String> cb=UIComponents.makeCombo(items);
        row.add(l,BorderLayout.NORTH); row.add(cb,BorderLayout.CENTER);
        p.add(row); p.add(Box.createVerticalStrut(10)); return cb;
    }
    private JComboBox<String> addComboTo(JPanel p, String lbl, String[] items) {
        JPanel row=new JPanel(new BorderLayout(0,5)); row.setOpaque(false);
        JLabel l=new JLabel(lbl.toUpperCase()); l.setFont(Theme.FONT_MONO(9)); l.setForeground(Theme.TEXT3);
        JComboBox<String> cb=UIComponents.makeCombo(items);
        row.add(l,BorderLayout.NORTH); row.add(cb,BorderLayout.CENTER); p.add(row); return cb;
    }

    class DelEditor extends DefaultCellEditor {
        private int expId;
        DelEditor() { super(new JCheckBox()); setClickCountToStart(1); }
        @Override public Component getTableCellEditorComponent(JTable t,Object v,boolean sel,int r,int c){
            expId = v instanceof Integer ? (int)v : -1;
            JPanel p=new JPanel(new FlowLayout(FlowLayout.CENTER,2,3)); p.setBackground(Theme.CARD);
            JButton b=UIComponents.makeButton("🗑",Theme.RED,Theme.RED); b.setFont(Theme.FONT_BODY(10));
            b.addActionListener(e->{
                stopCellEditing();
                if(JOptionPane.showConfirmDialog(null,"Delete this expense?","Confirm",JOptionPane.YES_NO_OPTION)==0){
                    AppData.expenses.removeIf(x->x.id==expId); refresh(); MainWindow.showToast("Expense deleted",true);
                }
            });
            p.add(b); return p;
        }
        @Override public Object getCellEditorValue(){return expId;}
    }
}
