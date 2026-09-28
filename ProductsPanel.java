package pricedrop.ui;

import pricedrop.data.AppData;
import pricedrop.model.Product;
import pricedrop.util.Theme;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class ProductsPanel extends JPanel {

    private JTextField searchField;
    private JComboBox<String> platformFilter;
    private JLabel statusLabel;
    private JTable table;
    private DefaultTableModel tableModel;

    public ProductsPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 14));
        setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        add(buildHeader(), BorderLayout.NORTH);
        add(buildTable(), BorderLayout.CENTER);
        refresh();
    }

    private JPanel buildHeader() {
        JPanel wrap = new JPanel(new BorderLayout(0, 10));
        wrap.setOpaque(false);

        JPanel titleRow = new JPanel(new BorderLayout());
        titleRow.setOpaque(false);
        JLabel title = UIComponents.makeSectionTitle("Product Tracker");
        statusLabel = new JLabel("Loading...");
        statusLabel.setFont(Theme.FONT_BODY(11)); statusLabel.setForeground(Theme.TEXT3);
        titleRow.add(title, BorderLayout.WEST);

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);

        searchField = UIComponents.makeTextField("🔍  Search products...");
        searchField.setPreferredSize(new Dimension(240, 34));
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { refresh(); }
        });

        platformFilter = UIComponents.makeCombo(new String[]{"All Platforms", "Amazon", "Flipkart", "Myntra"});
        platformFilter.setPreferredSize(new Dimension(140, 34));
        platformFilter.addActionListener(e -> refresh());

        JButton addBtn = UIComponents.makeButton("+ Add Product", Theme.GREEN, Theme.GREEN);
        addBtn.addActionListener(e -> openAddDialog(null));

        toolbar.add(searchField); toolbar.add(platformFilter); toolbar.add(addBtn);
        toolbar.add(Box.createHorizontalGlue());
        toolbar.add(statusLabel);

        wrap.add(titleRow, BorderLayout.NORTH);
        wrap.add(toolbar, BorderLayout.CENTER);
        return wrap;
    }

    private JPanel buildTable() {
        String[] cols = {"Platform","Product Name","Current","MRP","Discount","Target","Status","Actions"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return c == 7; }
            @Override public Class<?> getColumnClass(int c) { return c==7 ? JPanel.class : String.class; }
        };
        table = new JTable(tableModel) {
            @Override public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (!isRowSelected(row)) c.setBackground(row%2==0 ? Theme.CARD : Theme.BG3);
                return c;
            }
        };
        styleTable();

        JPanel wrap = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.CARD);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        wrap.setOpaque(false);
        wrap.add(table.getTableHeader(), BorderLayout.NORTH);
        wrap.add(UIComponents.scroll(table), BorderLayout.CENTER);
        return wrap;
    }

    private void styleTable() {
        table.setBackground(Theme.CARD);
        table.setForeground(Theme.TEXT2);
        table.setFont(Theme.FONT_BODY(12));
        table.setRowHeight(40);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(0x00, 0xd4, 0xff, 20));
        table.setSelectionForeground(Theme.TEXT);

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0,0,0, 50));
        header.setForeground(Theme.TEXT3);
        header.setFont(Theme.FONT_MONO(9));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Theme.BORDER));

        // Column widths
        int[] widths = {85,230,90,90,75,85,90,110};
        for (int i=0; i<widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        // Renderers
        table.getColumnModel().getColumn(7).setCellRenderer(new ActionRenderer());
        table.getColumnModel().getColumn(7).setCellEditor(new ActionEditor());
        table.getColumnModel().getColumn(4).setCellRenderer(new BadgeRenderer(Theme.GREEN));
        table.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(0).setCellRenderer(new PlatformRenderer());
        table.getColumnModel().getColumn(2).setCellRenderer(new PriceRenderer(Theme.GREEN));
        table.getColumnModel().getColumn(3).setCellRenderer(new StrikeRenderer());
    }

    public final void refresh() {
        String search = searchField != null ? searchField.getText().toLowerCase() : "";
        String plat   = platformFilter != null
                ? (String) platformFilter.getSelectedItem() : "All Platforms";

        List<Product> filtered = AppData.products.stream()
                .filter(p -> search.isEmpty() || p.name.toLowerCase().contains(search))
                .filter(p -> "All Platforms".equals(plat) || p.platform.equals(plat))
                .collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (Product p : filtered) {
            tableModel.addRow(new Object[]{
                    p.platform,
                    p.name,
                    AppData.fmtFull(p.current),
                    AppData.fmtFull(p.original),
                    p.getDiscount() + "% OFF",
                    p.target > 0 ? AppData.fmtFull(p.target) : "—",
                    p.getStatus(),
                    p.id
            });
        }

        double totalSavings = AppData.products.stream().mapToDouble(p->Math.max(0,p.original-p.current)).sum();
        if (statusLabel != null)
            statusLabel.setText(filtered.size() + " of " + AppData.products.size()
                    + " products · Total savings: " + AppData.fmtFull(totalSavings));
    }

    private void openAddDialog(Product existing) {
        boolean isEdit = existing != null;
        JDialog dlg = createStyledDialog(isEdit ? "✏️ Edit Product" : "➕ Add New Product");

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(8, 0, 8, 0));

        JTextField fName = addField(form, "Product Name *", isEdit ? existing.name : "");
        JTextField fUrl  = addField(form, "Product URL", isEdit ? existing.url : "");

        JPanel row1 = new JPanel(new GridLayout(1, 2, 12, 0));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JComboBox<String> fPlat = addComboField(row1, "Platform", new String[]{"Amazon","Flipkart","Myntra"}, isEdit ? existing.platform : "Amazon");
        JComboBox<String> fCat  = addComboField(row1, "Category", new String[]{"Electronics","Fashion","Home Appliances","Books","Sports","Beauty","Grocery","Other"}, isEdit ? existing.category : "Electronics");
        form.add(row1); form.add(Box.createVerticalStrut(10));

        fUrl.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { autoDetectPlatform(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { autoDetectPlatform(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {}
            void autoDetectPlatform() {
                if (fPlat != null) {
                    String platform = AppData.getPlatformUrl(fUrl.getText());
                    if (platform != null) {
                        fPlat.setSelectedItem(platform);
                    }
                }
            }
        });

        JPanel row2 = new JPanel(new GridLayout(1, 2, 12, 0));
        row2.setOpaque(false); row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JTextField fCur  = addFieldTo(row2, "Current Price (₹) *", isEdit ? String.valueOf((int)existing.current) : "");
        JTextField fOrig = addFieldTo(row2, "Original MRP (₹)", isEdit ? String.valueOf((int)existing.original) : "");
        form.add(row2); form.add(Box.createVerticalStrut(10));

        JTextField fTgt = addField(form, "Target Price (₹)", isEdit && existing.target>0 ? String.valueOf((int)existing.target) : "");

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);
        JButton cancel = UIComponents.makeButton("Cancel", Theme.TEXT2, Theme.BG4);
        JButton save   = UIComponents.makeButton(isEdit ? "💾 Save Changes" : "✅ Add Product", Theme.GREEN, Theme.GREEN);

        cancel.addActionListener(e -> dlg.dispose());
        save.addActionListener(e -> {
            String name = fName.getText().trim();
            String curStr = fCur.getText().trim();
            if (name.isEmpty() || curStr.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Name and current price are required!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            double cur, orig, tgt;
            try { cur = Double.parseDouble(curStr); } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid current price!", "Error", JOptionPane.ERROR_MESSAGE); return;
            }
            try { orig = fOrig.getText().isEmpty() ? cur : Double.parseDouble(fOrig.getText()); } catch (NumberFormatException ex) { orig = cur; }
            try { tgt  = fTgt.getText().isEmpty()  ? 0   : Double.parseDouble(fTgt.getText());  } catch (NumberFormatException ex) { tgt = 0; }

            if (isEdit && existing != null) {
                existing.name = name;
                existing.url = fUrl.getText();
                existing.platform = fPlat.getSelectedItem() instanceof String ? (String)fPlat.getSelectedItem() : "Amazon";
                existing.category = fCat.getSelectedItem() instanceof String ? (String)fCat.getSelectedItem() : "Electronics";
                existing.current = cur;
                existing.original = orig;
                existing.target = tgt;
            } else {
                String platform = fPlat.getSelectedItem() instanceof String ? (String)fPlat.getSelectedItem() : "Amazon";
                String category = fCat.getSelectedItem() instanceof String ? (String)fCat.getSelectedItem() : "Electronics";
                Product np = new Product(AppData.nextProductId++, name, fUrl.getText(),
                        platform, category, cur, orig, tgt);
                AppData.products.add(np);
            }
            dlg.dispose();
            refresh();
            MainWindow.showToast(isEdit ? "Product updated!" : "Product added!", true);
        });

        actions.add(cancel); actions.add(save);
        form.add(Box.createVerticalStrut(10));
        form.add(UIComponents.makeSep());
        form.add(Box.createVerticalStrut(10));
        form.add(actions);

        dlg.add(form);
        dlg.pack();
        dlg.setLocationRelativeTo(SwingUtilities.getWindowAncestor(this));
        dlg.setVisible(true);
    }

    private JDialog createStyledDialog(String title) {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg = new JDialog(owner instanceof Frame ? (Frame)owner : null, title, true);
        dlg.getContentPane().setBackground(Theme.BG2);
        dlg.setBackground(Theme.BG2);
        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(24, 28, 20, 28));
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Theme.FONT_TITLE(16)); titleLabel.setForeground(Theme.TEXT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 18, 0));
        content.add(titleLabel);
        content.setName("form");
        dlg.setContentPane(content);
        dlg.setMinimumSize(new Dimension(480, 200));
        return dlg;
    }

    private JTextField addField(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(0, 5));
        row.setOpaque(false); row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(Theme.FONT_MONO(9)); lbl.setForeground(Theme.TEXT3);
        JTextField tf = UIComponents.makeTextField("");
        tf.setText(value);
        tf.setPreferredSize(new Dimension(400, 36));
        row.add(lbl, BorderLayout.NORTH); row.add(tf, BorderLayout.CENTER);
        parent.add(row); parent.add(Box.createVerticalStrut(10));
        return tf;
    }

    private JTextField addFieldTo(JPanel parent, String label, String value) {
        JPanel row = new JPanel(new BorderLayout(0, 5));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(Theme.FONT_MONO(9)); lbl.setForeground(Theme.TEXT3);
        JTextField tf = UIComponents.makeTextField("");
        tf.setText(value);
        row.add(lbl, BorderLayout.NORTH); row.add(tf, BorderLayout.CENTER);
        parent.add(row);
        return tf;
    }

    private JComboBox<String> addComboField(JPanel parent, String label, String[] items, String selected) {
        JPanel row = new JPanel(new BorderLayout(0, 5));
        row.setOpaque(false);
        JLabel lbl = new JLabel(label.toUpperCase());
        lbl.setFont(Theme.FONT_MONO(9)); lbl.setForeground(Theme.TEXT3);
        JComboBox<String> cb = UIComponents.makeCombo(items);
        cb.setSelectedItem(selected);
        row.add(lbl, BorderLayout.NORTH); row.add(cb, BorderLayout.CENTER);
        parent.add(row);
        return cb;
    }

    // ── Custom cell renderers ───────────────────────────────────────────────

    class PlatformRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            String plat = v == null ? "" : v.toString();
            Color color = switch (plat) {
                case "Amazon" -> Theme.AMAZON; case "Flipkart" -> Theme.FLIPKART; default -> Theme.MYNTRA;
            };
            l.setForeground(color); l.setFont(Theme.FONT_MONO(10));
            l.setBackground(sel ? new Color(0x00, 0xd4, 0xff, 20) : (r%2==0 ? Theme.CARD : Theme.BG3));
            return l;
        }
    }

    class PriceRenderer extends DefaultTableCellRenderer {
        Color color;
        PriceRenderer(Color c) { this.color = c; }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            l.setForeground(color); l.setFont(Theme.FONT_MONO(12));
            l.setBackground(sel ? new Color(0x00,0xd4,0xff,20) : (r%2==0 ? Theme.CARD : Theme.BG3));
            return l;
        }
    }

    class StrikeRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            l.setForeground(Theme.TEXT3); l.setFont(Theme.FONT_MONO(11));
            l.setText("<html><strike>" + (v==null?"":v) + "</strike></html>");
            l.setBackground(sel ? new Color(0x00,0xd4,0xff,20) : (r%2==0 ? Theme.CARD : Theme.BG3));
            return l;
        }
    }

    class BadgeRenderer extends DefaultTableCellRenderer {
        Color color;
        BadgeRenderer(Color c) { this.color = c; }
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            String txt = v == null ? "" : v.toString();
            boolean isOff = txt.endsWith("OFF") && !txt.startsWith("0");
            l.setForeground(isOff ? Theme.RED : Theme.TEXT3);
            l.setFont(Theme.FONT_MONO(10));
            l.setBackground(sel ? new Color(0x00,0xd4,0xff,20) : (r%2==0 ? Theme.CARD : Theme.BG3));
            return l;
        }
    }

    class StatusRenderer extends DefaultTableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, v, sel, foc, r, c);
            String status = v == null ? "" : v.toString();
            l.setForeground(switch (status) {
                case "TARGET HIT" -> Theme.GREEN; case "DROPPED" -> Theme.ORANGE; default -> Theme.CYAN;
            });
            l.setFont(Theme.FONT_MONO(10));
            l.setBackground(sel ? new Color(0x00,0xd4,0xff,20) : (r%2==0 ? Theme.CARD : Theme.BG3));
            return l;
        }
    }

    class ActionRenderer implements TableCellRenderer {
        @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int r, int c) {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            p.setBackground(r%2==0 ? Theme.CARD : Theme.BG3);
            JButton edit = UIComponents.makeButton("✏", Theme.CYAN, Theme.CYAN);
            JButton del  = UIComponents.makeButton("🗑", Theme.RED, Theme.RED);
            edit.setFont(Theme.FONT_BODY(11)); del.setFont(Theme.FONT_BODY(11));
            p.add(edit); p.add(del);
            return p;
        }
    }

    class ActionEditor extends DefaultCellEditor {
        private JPanel panel;
        private int productId;
        ActionEditor() { super(new JCheckBox()); setClickCountToStart(1); }

        @Override public Component getTableCellEditorComponent(JTable t, Object v, boolean sel, int r, int c) {
            productId = v instanceof Integer ? (int)v : -1;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            panel.setBackground(Theme.CARD);

            JButton edit = UIComponents.makeButton("✏", Theme.CYAN, Theme.CYAN);
            JButton del  = UIComponents.makeButton("🗑", Theme.RED, Theme.RED);
            edit.setFont(Theme.FONT_BODY(11)); del.setFont(Theme.FONT_BODY(11));

            edit.addActionListener(e -> {
                stopCellEditing();
                Product p = AppData.products.stream().filter(x->x.id==productId).findFirst().orElse(null);
                if (p != null) openAddDialog(p);
            });
            del.addActionListener(e -> {
                stopCellEditing();
                if (JOptionPane.showConfirmDialog(null,"Delete this product?","Confirm",JOptionPane.YES_NO_OPTION)==0) {
                    AppData.products.removeIf(p -> p.id == productId);
                    refresh();
                    MainWindow.showToast("Product deleted", true);
                }
            });
            panel.add(edit); panel.add(del);
            return panel;
        }
        @Override public Object getCellEditorValue() { return productId; }
    }
}
