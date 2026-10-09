package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.HoaDonController;
import controller.KhachHangController;
import model.KhachHang;
import model.HoaDon;
import view.common.UIFactory;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal; // Import BigDecimal
import java.text.SimpleDateFormat;
import java.util.List;

public class PanelHoaDon extends JPanel {

    private HoaDonController hoaDonController = new HoaDonController();
    private KhachHangController khachHangController = new KhachHangController();

    private JTable tbl;
    private DefaultTableModel model;

    private JTextField txtID, txtNgayTao, txtNgayThanhToan, txtTongTien, txtTrangThai;
    private JComboBox<KhachHang> cboKhachHang;
    private JTextField txtSearch;
    private JComboBox<String> cboFilter;

    private List<HoaDon> dataList;

    public PanelHoaDon() {

        setLayout(new BorderLayout());

        // ... (phần khởi tạo giao diện không thay đổi) ...

        // ==================== TITLE PANEL ====================
        JPanel pnlTitle = UIFactory.createSectionTitle("HÓA ĐƠN");

        // ==================== FILTER PANEL ====================
        txtSearch = UIFactory.createTextField(25);
        cboFilter = new JComboBox<>(new String[] {
                "Tất cả",
                "Mã hóa đơn",
                "Khách hàng",
                "Ngày tạo",
                "Ngày thanh toán",
                "Tổng tiền ≥",
                "Chưa thanh toán",
                "Đã thanh toán"
        });
        JPanel pnlFilter = UIFactory.createFilterPanel(txtSearch, cboFilter);

        // ==================== INPUT PANEL ====================
        JPanel pnlInput = UIFactory.createInputPanel(3, 4);

        txtID = UIFactory.createTextField(0);
        txtID.setEditable(false);
        txtNgayTao = UIFactory.createTextField(0);
        txtNgayTao.setEditable(false);
        txtNgayThanhToan = UIFactory.createTextField(0);
        txtNgayThanhToan.setEditable(false);
        txtTongTien = UIFactory.createTextField(0);
        txtTongTien.setEditable(false);
        txtTrangThai = UIFactory.createTextField(0);
        txtTrangThai.setEditable(false);

        cboKhachHang = new JComboBox<>();
        loadKhachHang();

        pnlInput.add(UIFactory.createLabel("Mã hóa đơn:"));
        pnlInput.add(txtID);

        pnlInput.add(UIFactory.createLabel("Khách hàng:"));
        pnlInput.add(cboKhachHang);

        pnlInput.add(UIFactory.createLabel("Ngày tạo:"));
        pnlInput.add(txtNgayTao);

        pnlInput.add(UIFactory.createLabel("Ngày thanh toán:"));
        pnlInput.add(txtNgayThanhToan);

        pnlInput.add(UIFactory.createLabel("Tổng tiền:"));
        pnlInput.add(txtTongTien);

        pnlInput.add(UIFactory.createLabel("Trạng thái:"));
        pnlInput.add(txtTrangThai);

        // ==================== HEADER PANEL ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.add(pnlTitle, BorderLayout.NORTH);
        pnlHeader.add(pnlFilter, BorderLayout.CENTER);
        pnlHeader.add(pnlInput, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // ==================== TABLE ====================
        model = UIFactory.createTableModel(new String[] {
                "Mã HD", "Khách hàng", "Ngày tạo",
                "Ngày thanh toán", "Tổng tiền", "Trạng thái"
        });

        tbl = UIFactory.createTable(model);
        tbl.setRowHeight(25);

        add(new JScrollPane(tbl), BorderLayout.CENTER);

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                loadForm();
            }
        });

        JPanel pnlBottom = UIFactory.createPanelBase();

        JButton btnPrint = new JButton("In hóa đơn");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPrint.setBackground(new Color(41, 128, 185));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btnPrint.addActionListener(e -> printInvoice());

        pnlBottom.add(btnPrint);
        add(pnlBottom, BorderLayout.SOUTH);

        // ==================== EVENTS ====================
        KeyAdapter filterEvent = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                loadData(txtSearch.getText(), cboFilter.getSelectedItem().toString());
            }
        };

        txtSearch.addKeyListener(filterEvent);
        cboFilter.addActionListener(e -> loadData(txtSearch.getText(), cboFilter.getSelectedItem().toString()));

        loadData("", "Tất cả");
    }

    // ==================== LOAD COMBOBOX ====================
    private void loadKhachHang() {
        cboKhachHang.removeAllItems();
        for (KhachHang kh : khachHangController.getAllKhachHang())
            cboKhachHang.addItem(kh);
    }

    // ==================== LOAD DATA ====================
    private void loadData(String key, String type) {

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        model.setRowCount(0);
        dataList = hoaDonController.getAll();

        key = key.toLowerCase().trim();

        for (HoaDon hd : dataList) {

            KhachHang kh = khachHangController.getById(hd.getMaKhachHang());
            String tenKH = kh != null ? kh.getTenKhachHang() : "N/A";

            boolean match = true;

            switch (type) {
                case "Mã hóa đơn":
                    match = String.valueOf(hd.getMaHoaDon()).contains(key);
                    break;

                case "Khách hàng":
                    match = tenKH.toLowerCase().contains(key);
                    break;

                case "Ngày tạo":
                    match = hd.getNgayTao() != null &&
                            fmt.format(hd.getNgayTao()).toLowerCase().contains(key);
                    break;

                case "Ngày thanh toán":
                    match = hd.getNgayThanhToan() != null &&
                            fmt.format(hd.getNgayThanhToan()).toLowerCase().contains(key);
                    break;

                case "Tổng tiền ≥":
                    try {
                        BigDecimal min = new BigDecimal(key); // Dùng BigDecimal
                        match = hd.getTongTien().compareTo(min) >= 0; // So sánh bằng compareTo
                    } catch (Exception ignored) {
                    }
                    break;

                case "Chưa thanh toán":
                    match = hd.getTrangThai().equals("Chưa thanh toán");
                    break;

                case "Đã thanh toán":
                    match = hd.getTrangThai().equals("Đã thanh toán");
                    break;

                default:
                    match = true;
            }

            if (!match)
                continue;

            model.addRow(new Object[] {
                    hd.getMaHoaDon(),
                    tenKH,
                    UIFactory.formatTimestamp(hd.getNgayTao()),
                    UIFactory.formatTimestamp(hd.getNgayThanhToan()),
                    UIFactory.formatCurrency(hd.getTongTien()), // Dùng formatCurrency cho BigDecimal
                    hd.getTrangThai()
            });
        }
    }

    // ==================== LOAD FORM ====================
    private void loadForm() {
        int row = tbl.getSelectedRow();
        if (row < 0)
            return;

        txtID.setText(model.getValueAt(row, 0).toString());
        selectKhachHang(model.getValueAt(row, 1).toString());
        txtNgayTao.setText(model.getValueAt(row, 2).toString());
        txtNgayThanhToan.setText(model.getValueAt(row, 3).toString());
        txtTongTien.setText(model.getValueAt(row, 4).toString());
        txtTrangThai.setText(model.getValueAt(row, 5).toString());
    }

    // ==================== SELECT COMBOBOX ====================
    private void selectKhachHang(String ten) {
        for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
            if (cboKhachHang.getItemAt(i).getTenKhachHang().equalsIgnoreCase(ten)) {
                cboKhachHang.setSelectedIndex(i);
                return;
            }
        }
    }

    // ================= PRINT INVOICE =================
    private void printInvoice() {
        // Mở dialog chọn khách hàng và hóa đơn theo luồng mới
        FrmSelectCustomerInvoice dialog = new FrmSelectCustomerInvoice(
                (JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
    }
}