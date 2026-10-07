package view.common;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import com.toedter.calendar.JDateChooser;

public class UIFactory {

    public static JPanel createSectionTitle(String title) {
        JLabel lbl = new JLabel(title);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lbl.setForeground(new Color(30, 30, 30)); // màu chữ

        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(25, 25, 5, 15)); // padding
        pnl.add(lbl, BorderLayout.WEST);

        return pnl;
    }

    private static final DecimalFormat CURRENCY_FORMATTER;
    static {
        // Thiết lập chuẩn Việt Nam (Dấu chấm là phân cách hàng nghìn, dấu phẩy là thập
        // phân)
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("vi", "VN"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');

        // Mẫu định dạng: #,### VNĐ
        CURRENCY_FORMATTER = new DecimalFormat("#,### VNĐ", symbols);
    }

    // =========================================================================
    // 1. BẢNG MÀU EXCEL STYLE & FONT
    // =========================================================================
    // Màu xanh đặc trưng của Excel
    public static final Color EXCEL_GREEN = new Color(33, 115, 70); // #217346
    public static final Color EXCEL_LIGHT_GREEN = new Color(230, 242, 234); // Màu nền khi chọn dòng

    // Các màu trạng thái
    public static final Color COLOR_DANGER = new Color(192, 57, 43); // Đỏ trầm (Xóa/Hủy)
    public static final Color COLOR_WARNING = new Color(243, 156, 18); // Cam (Sửa/Cảnh báo)
    public static final Color COLOR_TEXT = new Color(50, 50, 50); // Xám đen (Chữ)
    public static final Color COLOR_BG = new Color(248, 249, 250); // Xám rất nhạt (Nền app)
    public static final Color COLOR_BORDER = new Color(210, 210, 210); // Viền xám nhạt

    // Font chữ chuẩn Windows (Segoe UI)
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);

    // =========================================================================
    // 2. BUTTONS (Nút bấm phong cách Ribbon Excel)
    // =========================================================================

    public static final Font FONT_BTN = new Font("Segoe UI", Font.BOLD, 14);

    public static JButton createButton(String text) {
        return createStyledButton(text, EXCEL_GREEN, Color.WHITE);
    }

    public static JButton createBtnPrimary(String text) {
        return createButton(text);
    }

    public static JButton createBtnDelete(String text) {
        return createStyledButton(text, COLOR_DANGER, Color.WHITE);
    }

    public static JButton createBtnUpdate(String text) {
        return createStyledButton(text, COLOR_WARNING, Color.WHITE);
    }

    public static JButton createBtnRefresh(String text) {
        return createStyledButton(text, new Color(108, 117, 125), Color.WHITE);
    }

    public static void styleButton(JButton btn) {
        formatButton(btn, EXCEL_GREEN, Color.WHITE);
    }

    private static JButton createStyledButton(String text, Color bg, Color fg) {
        // Override paintComponent để vẽ bo tròn
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g2);
                g2.dispose();
            }
        };

        formatButton(btn, bg, fg);
        return btn;
    }

    private static void formatButton(JButton btn, Color bg, Color fg) {
        btn.setFont(FONT_BTN);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);

        btn.setContentAreaFilled(false);

        btn.setBorder(new EmptyBorder(12, 25, 12, 25));

        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(bg.brighter());
                btn.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(bg);
                btn.repaint();
            }
        });
    }

    public static JPanel createPanelBase() {
        JPanel pnl = new JPanel();

        pnl.setOpaque(true);
        pnl.setBackground(Color.WHITE);

        pnl.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(EXCEL_GREEN, 0),
                new EmptyBorder(20, 10, 20, 10)));

        return pnl;
    }

    // =========================================================================
    // 3. TEXT FIELDS & LABELS (Ô nhập liệu sắc nét)
    // =========================================================================

    public static JLabel createLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(FONT_NORMAL);
        lbl.setForeground(COLOR_TEXT);
        return lbl;
    }

    public static JTextField createTextField(int columns) {
        JTextField txt = new JTextField(columns);
        txt.setFont(FONT_NORMAL);
        txt.setForeground(COLOR_TEXT);
        txt.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(5, 5, 5, 5)));
        return txt;
    }

    // =========================================================================
    // 4. TABLES (Bảng dữ liệu giống Excel Sheet)
    // =========================================================================
    public static DefaultTableModel createTableModel(String[] columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static JTable createTable(DefaultTableModel model) {

        // === 1. Tăng cỡ chữ 1.2x ===
        Font fontNormalBig = FONT_NORMAL.deriveFont((float) (FONT_NORMAL.getSize() * 1.2));
        Font fontBoldBig = FONT_BOLD.deriveFont((float) (FONT_BOLD.getSize() * 1.2));

        JTable tbl = new JTable(model);
        tbl.setFont(fontNormalBig);

        // === 2. Chiều cao dòng 1.4x ===
        tbl.setRowHeight((int) (26 * 1.4));

        // === 3. Excel Style ===
        tbl.setGridColor(COLOR_BORDER);
        tbl.setShowGrid(true);
        tbl.setIntercellSpacing(new Dimension(1, 1));
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.setSelectionBackground(EXCEL_LIGHT_GREEN);
        tbl.setSelectionForeground(Color.BLACK);

        // === 4. MARGIN trái/phải = 100, top = 40 ===
        tbl.setBorder(new EmptyBorder(40, 100, 0, 100));

        // ============================================================
        // 5. RENDERER CHO DATA – Padding trái 10px
        // ============================================================
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.translate(10, 0); // padding trái 10px
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        leftRenderer.setOpaque(true);

        for (int i = 0; i < tbl.getColumnCount(); i++) {
            tbl.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }

        // ============================================================
        // 6. HEADER Renderer – Căn trái + Padding trái 10px + Nền xanh
        // ============================================================
        JTableHeader header = tbl.getTableHeader();
        header.setFont(fontBoldBig);
        header.setBackground(EXCEL_GREEN);
        header.setForeground(Color.WHITE);
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer() {
            @Override
            protected void paintComponent(Graphics g) {

                // 1) VẼ NỀN XANH
                g.setColor(EXCEL_GREEN);
                g.fillRect(0, 0, getWidth(), getHeight());

                // 2) DỊCH VỊ TRÍ VẼ TEXT (padding trái 10px)
                Graphics2D g2 = (Graphics2D) g.create();
                g2.translate(10, 0);
                super.paintComponent(g2);
                g2.dispose();
            }
        };
        headerRenderer.setHorizontalAlignment(JLabel.LEFT);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(fontBoldBig);
        headerRenderer.setOpaque(false);

        // === GÁN RENDERER HEADER ===
        header.setDefaultRenderer(headerRenderer);

        // ============================================================
        // 7. TĂNG CHIỀU CAO HEADER (1.5x)
        // ============================================================
        Dimension headerSize = header.getPreferredSize();
        headerSize.height = (int) (headerSize.height * 1.5);
        header.setPreferredSize(headerSize);

        return tbl;
    }

    // =========================================================================
    // 5. PANELS (Layout tổ chức)
    // =========================================================================

    /**
     * Panel chứa ô tìm kiếm và combo lọc
     */
    public static JPanel createFilterPanel(JTextField txtSearch, JComboBox<String> cboFilter) {
        JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COLOR_BORDER),
                new EmptyBorder(15, 10, 15, 10)));

        JLabel lblSearch = createLabel("Tìm kiếm:");
        JLabel lblFilter = createLabel("Lọc theo:");

        // ============================================
        // 1) Viền bo góc xanh – radius thật
        // ============================================
        class RoundedBorder implements Border {
            private final Color color = new Color(0, 153, 0);
            private final Insets pad = new Insets(6, 10, 6, 10);

            @Override
            public Insets getBorderInsets(Component c) {
                return pad;
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(x + 1, y + 1, w - 2, h - 2, 10, 10);
            }
        }

        // ============================================
        // 2) Kích thước cố định cho cả TextField & Combo
        // ============================================
        int FIXED_WIDTH = 180;
        int FIXED_HEIGHT = 36;

        Dimension fixedSize = new Dimension(FIXED_WIDTH, FIXED_HEIGHT);

        txtSearch.setPreferredSize(fixedSize);
        txtSearch.setMinimumSize(fixedSize);
        txtSearch.setMaximumSize(fixedSize);
        txtSearch.setBorder(new RoundedBorder());
        txtSearch.setBackground(Color.WHITE);

        // ComboBox — xử lý tăng height
        cboFilter.setPreferredSize(fixedSize);
        cboFilter.setMinimumSize(fixedSize);
        cboFilter.setMaximumSize(fixedSize);
        cboFilter.setBackground(Color.WHITE);
        cboFilter.setFont(FONT_NORMAL);

        // ============================================
        // 3) Ép ComboBox UI để chiều cao thật bằng txtSearch
        // ============================================
        cboFilter.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton b = new JButton();
                b.setBorder(BorderFactory.createEmptyBorder());
                b.setContentAreaFilled(false);
                b.setFocusable(false);
                b.setPreferredSize(new Dimension(24, FIXED_HEIGHT - 4));
                return b;
            }

            @Override
            public Dimension getPreferredSize(JComponent c) {
                return fixedSize;
            }
        });

        // Border bo góc
        cboFilter.setBorder(new RoundedBorder());

        // ============================================
        // 4) Add component vào panel
        // ============================================
        pnl.add(lblSearch);
        pnl.add(txtSearch);
        pnl.add(Box.createHorizontalStrut(10));
        pnl.add(lblFilter);
        pnl.add(cboFilter);

        return pnl;
    }

    public static JPanel createInputGrid(int rows, int cols) {
        // Chỉ là wrapper gọi hàm mới hoặc tạo grid cơ bản
        JPanel pnl = new JPanel(new GridLayout(rows, cols, 30, 10));
        pnl.setBackground(Color.WHITE);
        pnl.setBorder(new EmptyBorder(20, 50, 20, 50));
        return pnl;
    }

    public static JPanel createInputPanel(int rows, int cols) {

        class RoundedBorder implements Border {
            private final Color color = new Color(0, 153, 0);
            private final Insets pad = new Insets(6, 10, 6, 10);

            @Override
            public Insets getBorderInsets(Component c) {
                return pad;
            }

            @Override
            public boolean isBorderOpaque() {
                return false;
            }

            @Override
            public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(color);
                g2.setStroke(new BasicStroke(1));
                g2.drawRoundRect(x + 1, y + 1, w - 2, h - 2, 10, 10);
            }
        }

        JPanel pnl = new JPanel(new GridBagLayout()) {

            int index = 0;

            // -------------------------
            // Xác định component nhập liệu
            // -------------------------
            private boolean isInputComponent(Component comp) {
                return comp instanceof JTextField ||
                        comp instanceof JComboBox<?> ||
                        comp instanceof JSpinner ||
                        comp instanceof JTextArea ||
                        comp instanceof JFormattedTextField ||
                        comp instanceof JCheckBox ||
                        comp instanceof JRadioButton;
            }

            @Override
            public Component add(Component comp) {

                // ============================
                // 1) LAYOUT: vị trí grid
                // ============================
                GridBagConstraints c = new GridBagConstraints();
                c.gridy = index / cols;
                c.gridx = index % cols;

                c.insets = new Insets(5, 20, 5, 20);

                if (comp instanceof JLabel) {
                    c.weightx = 0;
                    c.fill = GridBagConstraints.NONE;
                    c.anchor = GridBagConstraints.EAST;
                } else {
                    c.weightx = 2.0;
                    c.fill = GridBagConstraints.HORIZONTAL;
                    c.anchor = GridBagConstraints.WEST;
                }

                index++;

                // ============================
                // 2) UI CHO TỪNG LOẠI COMPONENT
                // ============================

                // ----------- LABEL -----------
                if (comp instanceof JLabel lbl) {
                    lbl.setBorder(null);
                    lbl.setOpaque(false);

                    // tăng font nhẹ x1.3
                    Font f = lbl.getFont();
                    if (f != null)
                        lbl.setFont(f.deriveFont((float) (f.getSize() * 1.3)));

                    super.add(lbl, c);
                    return lbl;
                }

                // ----------- INPUT COMPONENT -----------
                if (isInputComponent(comp)) {
                    JComponent jc = (JComponent) comp;

                    // Tăng font x1.2
                    Font f = comp.getFont();
                    if (f != null)
                        comp.setFont(f.deriveFont((float) (f.getSize() * 1.2)));

                    // Tăng chiều cao x1.2
                    Dimension pref = comp.getPreferredSize();
                    pref.height = (int) (pref.height * 1.2);
                    comp.setPreferredSize(pref);

                    jc.setBorder(new RoundedBorder());

                    if (jc instanceof JComboBox<?> cb) {
                        int newHeight = (int) (cb.getPreferredSize().height * 1.2);
                        cb.setUI(new BasicComboBoxUI() {

                            @Override
                            protected JButton createArrowButton() {
                                JButton btn = new JButton();
                                btn.setBorder(BorderFactory.createEmptyBorder());
                                btn.setContentAreaFilled(false);
                                btn.setFocusable(false);
                                btn.setIcon(new ImageIcon("resources/icons/arrow_down_green.png"));

                                Dimension d = btn.getPreferredSize();
                                d.height = newHeight;
                                btn.setPreferredSize(d);
                                return btn;
                            }

                            @Override
                            public Dimension getPreferredSize(JComponent c) {
                                Dimension d = super.getPreferredSize(c);
                                d.height = newHeight;
                                return d;
                            }

                            @Override
                            protected ListCellRenderer<Object> createRenderer() {
                                return new DefaultListCellRenderer() {
                                    @Override
                                    public Component getListCellRendererComponent(
                                            JList<?> list, Object value, int index,
                                            boolean isSelected, boolean cellHasFocus) {

                                        JLabel lbl = (JLabel) super.getListCellRendererComponent(
                                                list, value, index, isSelected, cellHasFocus);

                                        lbl.setPreferredSize(new Dimension(lbl.getPreferredSize().width, newHeight));
                                        Font f = lbl.getFont();
                                        if (f != null)
                                            lbl.setFont(f.deriveFont((float) (f.getSize() * 1.2)));

                                        return lbl;
                                    }
                                };
                            }
                        });
                        Dimension d = cb.getPreferredSize();
                        d.height = newHeight;
                        cb.setPreferredSize(d);
                    }
                    super.add(comp, c);
                    return comp;
                }
                super.add(comp, c);
                return comp;
            }
        };

        // ============================
        // 3) TitledBorder (GroupBox)
        // ============================
        pnl.setBackground(Color.WHITE);

        Border lineBorder = new LineBorder(new Color(0, 153, 0), 1);
        Border titleBorder = BorderFactory.createTitledBorder(
                lineBorder,
                "Dữ liệu chi tiết",
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                new Font("Segoe UI", Font.BOLD, 16),
                new Color(0, 153, 0));

        pnl.setBorder(BorderFactory.createCompoundBorder(
                titleBorder,
                new EmptyBorder(30, 100, 30, 100)));

        return pnl;
    }

    // =========================================================================
    // 6. HELPERS (Giữ nguyên logic, chỉ format)
    // =========================================================================

    public static void showMsg(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void showError(Component parent, String msg) {
        JOptionPane.showMessageDialog(parent, msg, "Lỗi", JOptionPane.ERROR_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String msg) {
        int choice = JOptionPane.showConfirmDialog(parent, msg, "Xác nhận", JOptionPane.YES_NO_OPTION);
        return choice == JOptionPane.YES_OPTION;
    }

    public static String formatCurrency(double amount) {
        // Chuyển sang BigDecimal để dùng formatter thống nhất
        return formatCurrency(BigDecimal.valueOf(amount));
    }

    // 👈 CODE CẦN THÊM: Phương thức quá tải cho BigDecimal
    public static String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 VNĐ";
        }
        return CURRENCY_FORMATTER.format(amount); // ✅ Dùng biến tĩnh đã khai báo
    }
    // 👆 END CODE CẦN THÊM

    public static double parseCurrency(String text) {
        try {
            return Double.parseDouble(text.replace(",", "").replace(" VNĐ", "").trim());
        } catch (Exception e) {
            return 0;
        }
    }

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static Timestamp parseTimestamp(String text) {
        if (text == null || text.trim().isEmpty())
            return null;
        try {
            if (text.length() <= 10)
                text += " 00:00:00";
            return Timestamp.valueOf(text);
        } catch (IllegalArgumentException e) {
            try {
                Date d = DATE_FMT.parse(text);
                return new Timestamp(d.getTime());
            } catch (ParseException ex) {
                return null;
            }
        }
    }

    public static String formatTimestamp(Timestamp ts) {
        if (ts == null)
            return "";
        return DATE_FMT.format(ts);
    }

    public static BigDecimal parseCurrencyToBigDecimal(String text) throws ParseException {
        if (text == null || text.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }

        // Tạo một NumberFormat cục bộ giống như CURRENCY_FORMATTER nhưng cho mục đích
        // parse
        DecimalFormat parseFormatter = (DecimalFormat) NumberFormat.getInstance(Locale.of("vi", "VN"));
        parseFormatter.setGroupingUsed(true); // Cho phép dấu phân cách nhóm
        parseFormatter.setDecimalSeparatorAlwaysShown(false);

        // Loại bỏ ký hiệu VNĐ để parse
        String cleanedText = text.replace(" VNĐ", "").trim();

        try {
            // Parse chuỗi nhập liệu
            Number number = parseFormatter.parse(cleanedText);

            // Chuyển kết quả (có thể là Long hoặc Double) sang BigDecimal
            if (number instanceof BigDecimal) {
                return (BigDecimal) number;
            } else {
                return new BigDecimal(number.toString());
            }
        } catch (ParseException e) {
            throw new ParseException("Giá trị không phải là số hợp lệ: " + cleanedText, 0);
        }
    }

    public static BigDecimal getRawCurrencyValue(String formattedText) throws ParseException {
        // ✅ Dùng CURRENCY_FORMATTER tĩnh để parse chuỗi đã được định dạng
        Number number = CURRENCY_FORMATTER.parse(formattedText);

        // Chuyển Number sang BigDecimal
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        } else {
            // Tạo BigDecimal từ chuỗi của Number để tránh lỗi double/float
            return new BigDecimal(number.toString());
        }
    }

    // =========================================================================
    // 7. DATE PICKER (JDateChooser)
    // =========================================================================

    /**
     * Tạo JDateChooser với định dạng chuẩn Việt Nam
     */
    public static JDateChooser createDateChooser() {
        JDateChooser dateChooser = new JDateChooser();

        // Định dạng ngày: dd/MM/yyyy
        dateChooser.setDateFormatString("dd/MM/yyyy");

        // Locale tiếng Việt
        dateChooser.setLocale(Locale.of("vi", "VN"));

        // Font và style
        dateChooser.setFont(FONT_NORMAL);
        dateChooser.setBackground(Color.WHITE);

        // Border bo góc giống TextField
        dateChooser.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(5, 5, 5, 5)));

        // Tăng chiều cao
        Dimension pref = dateChooser.getPreferredSize();
        pref.height = (int) (pref.height * 1.2);
        dateChooser.setPreferredSize(pref);

        return dateChooser;
    }

    /**
     * Lấy Timestamp từ JDateChooser
     */
    public static Timestamp getDateFromChooser(JDateChooser dateChooser) {
        if (dateChooser == null || dateChooser.getDate() == null) {
            return null;
        }
        return new Timestamp(dateChooser.getDate().getTime());
    }

    /**
     * Set Timestamp vào JDateChooser
     */
    public static void setDateToChooser(JDateChooser dateChooser, Timestamp timestamp) {
        if (dateChooser == null) {
            return;
        }
        if (timestamp == null) {
            dateChooser.setDate(null);
        } else {
            dateChooser.setDate(new Date(timestamp.getTime()));
        }
    }

    // =========================================================================
    // 8. DATE-TIME PICKER (JDateChooser + Time Spinners)
    // =========================================================================

    /**
     * Tạo panel chứa date picker + time spinners (giờ:phút)
     * 
     * @return JPanel chứa [DateChooser] [HH] : [MM]
     */
    public static JPanel createDateTimeChooser() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);

        // Date picker
        JDateChooser dateChooser = createDateChooser();
        dateChooser.setPreferredSize(new Dimension(150, 35));

        // Time spinners
        SpinnerNumberModel hourModel = new SpinnerNumberModel(0, 0, 23, 1);
        SpinnerNumberModel minuteModel = new SpinnerNumberModel(0, 0, 59, 1);

        JSpinner hourSpinner = new JSpinner(hourModel);
        JSpinner minuteSpinner = new JSpinner(minuteModel);

        // Format spinners with leading zeros
        JSpinner.NumberEditor hourEditor = new JSpinner.NumberEditor(hourSpinner, "00");
        JSpinner.NumberEditor minuteEditor = new JSpinner.NumberEditor(minuteSpinner, "00");
        hourSpinner.setEditor(hourEditor);
        minuteSpinner.setEditor(minuteEditor);

        // Style spinners - larger and more visible
        Dimension spinnerSize = new Dimension(60, 35);
        hourSpinner.setPreferredSize(spinnerSize);
        minuteSpinner.setPreferredSize(spinnerSize);

        // Font for spinners
        Font spinnerFont = FONT_NORMAL.deriveFont(14f);
        hourEditor.getTextField().setFont(spinnerFont);
        minuteEditor.getTextField().setFont(spinnerFont);
        hourEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);
        minuteEditor.getTextField().setHorizontalAlignment(JTextField.CENTER);

        // Border for spinners - rounded green border
        Border spinnerBorder = BorderFactory.createCompoundBorder(
                new LineBorder(COLOR_BORDER),
                new EmptyBorder(5, 8, 5, 8));
        hourSpinner.setBorder(spinnerBorder);
        minuteSpinner.setBorder(spinnerBorder);

        hourSpinner.setBackground(Color.WHITE);
        minuteSpinner.setBackground(Color.WHITE);

        // Time label with icon style
        JLabel lblTime = new JLabel("🕐");
        lblTime.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        lblTime.setForeground(EXCEL_GREEN);

        // Colon separator
        JLabel lblColon = new JLabel(":");
        lblColon.setFont(FONT_BOLD.deriveFont(18f));
        lblColon.setForeground(COLOR_TEXT);

        // Add to panel with better spacing
        panel.add(dateChooser);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(lblTime);
        panel.add(hourSpinner);
        panel.add(lblColon);
        panel.add(minuteSpinner);

        // Store references in panel properties for later retrieval
        panel.putClientProperty("dateChooser", dateChooser);
        panel.putClientProperty("hourSpinner", hourSpinner);
        panel.putClientProperty("minuteSpinner", minuteSpinner);

        return panel;
    }

    /**
     * Lấy Timestamp từ DateTimeChooser panel (bao gồm cả giờ phút)
     */
    public static Timestamp getDateTimeFromChooser(JPanel dateTimePanel) {
        if (dateTimePanel == null) {
            return null;
        }

        JDateChooser dateChooser = (JDateChooser) dateTimePanel.getClientProperty("dateChooser");
        JSpinner hourSpinner = (JSpinner) dateTimePanel.getClientProperty("hourSpinner");
        JSpinner minuteSpinner = (JSpinner) dateTimePanel.getClientProperty("minuteSpinner");

        if (dateChooser == null || dateChooser.getDate() == null) {
            return null;
        }

        Date date = dateChooser.getDate();
        int hour = (Integer) hourSpinner.getValue();
        int minute = (Integer) minuteSpinner.getValue();

        // Combine date + time
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
        cal.set(java.util.Calendar.MINUTE, minute);
        cal.set(java.util.Calendar.SECOND, 0);
        cal.set(java.util.Calendar.MILLISECOND, 0);

        return new Timestamp(cal.getTimeInMillis());
    }

    /**
     * Set Timestamp vào DateTimeChooser panel (bao gồm cả giờ phút)
     */
    public static void setDateTimeToChooser(JPanel dateTimePanel, Timestamp timestamp) {
        if (dateTimePanel == null) {
            return;
        }

        JDateChooser dateChooser = (JDateChooser) dateTimePanel.getClientProperty("dateChooser");
        JSpinner hourSpinner = (JSpinner) dateTimePanel.getClientProperty("hourSpinner");
        JSpinner minuteSpinner = (JSpinner) dateTimePanel.getClientProperty("minuteSpinner");

        if (timestamp == null) {
            dateChooser.setDate(null);
            hourSpinner.setValue(0);
            minuteSpinner.setValue(0);
        } else {
            dateChooser.setDate(new Date(timestamp.getTime()));

            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(timestamp);
            hourSpinner.setValue(cal.get(java.util.Calendar.HOUR_OF_DAY));
            minuteSpinner.setValue(cal.get(java.util.Calendar.MINUTE));
        }
    }

}