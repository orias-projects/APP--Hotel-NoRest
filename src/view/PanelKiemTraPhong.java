package view;

import controller.KiemTraPhongController;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.KiemTraPhong;
import view.common.UIFactory;
import controller.PhongController;
import model.Phong;

public class PanelKiemTraPhong extends JPanel {

    private KiemTraPhongController controller = new KiemTraPhongController();
    private PhongController phongController = new PhongController();

    private JTable tbl;
    private DefaultTableModel model;

    private JTextField txtID, txtNgayThanhToan, txtTienBoiThuong;
    private JTextField txtMaHoaDon, txtMaPhong;
    private JTextField txtSearch;
    private JComboBox<String> cboFilter;

    private List<KiemTraPhong> dataList;

    public PanelKiemTraPhong() {

        setLayout(new BorderLayout());

        // ==================== TITLE PANEL ====================
        JPanel pnlTitle = UIFactory.createSectionTitle("KIỂM TRA PHÒNG");

        // ==================== FILTER PANEL ====================
        txtSearch = UIFactory.createTextField(25);
        cboFilter = new JComboBox<>(new String[]{"Tất cả", "Mã Hóa Đơn", "Mã Phòng"});
        JPanel pnlFilter = UIFactory.createFilterPanel(txtSearch, cboFilter);

        // ==================== INPUT PANEL ====================
        JPanel pnlInput = UIFactory.createInputPanel(3, 4);

        txtID = UIFactory.createTextField(0);
        txtID.setEditable(false);

        txtMaHoaDon = UIFactory.createTextField(0);
        txtMaPhong = UIFactory.createTextField(0);
        txtNgayThanhToan = UIFactory.createTextField(0);
        txtTienBoiThuong = UIFactory.createTextField(0);

        pnlInput.add(UIFactory.createLabel("ID:"));
        pnlInput.add(txtID);
        pnlInput.add(UIFactory.createLabel("Mã Hóa Đơn:"));
        pnlInput.add(txtMaHoaDon);

        pnlInput.add(UIFactory.createLabel("Mã Phòng:"));
        pnlInput.add(txtMaPhong);
        pnlInput.add(UIFactory.createLabel("Ngày Thanh Toán:"));
        pnlInput.add(txtNgayThanhToan);

        pnlInput.add(UIFactory.createLabel("Tiền Bồi Thường:"));
        pnlInput.add(txtTienBoiThuong);

        // ==================== HEADER PANEL ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.add(pnlTitle, BorderLayout.NORTH);
        pnlHeader.add(pnlFilter, BorderLayout.CENTER);
        pnlHeader.add(pnlInput, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // ==================== TABLE ====================
        model = UIFactory.createTableModel(new String[]{
                "ID", "Mã Hóa Đơn", "Mã Phòng", "Ngày Thanh Toán", "Tiền Bồi Thường"
        });
        tbl = UIFactory.createTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tbl), BorderLayout.CENTER);

        // ==================== TABLE CLICK ====================
        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tbl.getSelectedRow();
                if (row >= 0) {
                    txtID.setText(model.getValueAt(row, 0).toString());
                    txtMaHoaDon.setText(model.getValueAt(row, 1).toString());
                    txtMaPhong.setText(model.getValueAt(row, 2).toString());
                    txtNgayThanhToan.setText(model.getValueAt(row, 3).toString());

                    // ⭐ Lấy tiền format → chuyển về raw để hiển thị đúng trong ô nhập
                    String formatted = model.getValueAt(row, 4).toString();
                    try {
                        txtTienBoiThuong.setText(
                                UIFactory.getRawCurrencyValue(formatted).toString()
                        );
                    } catch (Exception ex) {
                        txtTienBoiThuong.setText("");
                    }
                }
            }
        });

        // ==================== BUTTON PANEL ====================
        JPanel pnlBottom = new JPanel();

        JButton btnAdd = UIFactory.createBtnPrimary("Thêm");
        JButton btnUpdate = UIFactory.createBtnUpdate("Sửa");
        JButton btnDelete = UIFactory.createBtnDelete("Xóa");
        JButton btnRefresh = UIFactory.createBtnRefresh("Làm mới");

        pnlBottom.add(btnAdd);
        pnlBottom.add(btnUpdate);
        pnlBottom.add(btnDelete);
        pnlBottom.add(btnRefresh);

        add(pnlBottom, BorderLayout.SOUTH);

        // ===================================================
        // BUTTON EVENTS
        // ===================================================

        btnAdd.addActionListener(e -> {
            try {
                KiemTraPhong ktp = new KiemTraPhong(
                        0,
                        Integer.parseInt(txtMaHoaDon.getText()),
                        Integer.parseInt(txtMaPhong.getText()),
                        java.sql.Timestamp.valueOf(txtNgayThanhToan.getText()),
                        UIFactory.parseCurrencyToBigDecimal(txtTienBoiThuong.getText())
                );

                if (controller.add(ktp)) {
                    UIFactory.showMsg(this, "Thêm thành công");
                    loadData();
                } else {
                    UIFactory.showError(this, "Thêm thất bại");
                }
            } catch (Exception ex) {
                UIFactory.showError(this, "Lỗi dữ liệu: " + ex.getMessage());
            }
        });

        btnUpdate.addActionListener(e -> {
            if (txtID.getText().isEmpty()) {
                UIFactory.showError(this, "Chọn dòng cần sửa!");
                return;
            }

            try {
                KiemTraPhong ktp = new KiemTraPhong(
                        Integer.parseInt(txtID.getText()),
                        Integer.parseInt(txtMaHoaDon.getText()),
                        Integer.parseInt(txtMaPhong.getText()),
                        java.sql.Timestamp.valueOf(txtNgayThanhToan.getText()),
                        UIFactory.parseCurrencyToBigDecimal(txtTienBoiThuong.getText())
                );

                if (controller.update(ktp)) {
                    UIFactory.showMsg(this, "Sửa thành công");
                    loadData();
                } else {
                    UIFactory.showError(this, "Sửa thất bại");
                }
            } catch (Exception ex) {
                UIFactory.showError(this, "Lỗi dữ liệu: " + ex.getMessage());
            }
        });

        btnDelete.addActionListener(e -> {
            if (txtID.getText().isEmpty()) {
                UIFactory.showError(this, "Chọn dòng cần xóa!");
                return;
            }

            if (UIFactory.showConfirm(this, "Xóa kiểm tra phòng này?")) {
                if (controller.delete(Integer.parseInt(txtID.getText()))) {
                    UIFactory.showMsg(this, "Xóa thành công");
                    loadData();
                } else {
                    UIFactory.showError(this, "Xóa thất bại");
                }
            }
        });

        btnRefresh.addActionListener(e -> {
            clearForm();
            loadData();
        });

        // ==================== SEARCH EVENTS ====================
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterData();
            }
        });

        cboFilter.addActionListener(e -> filterData());

        loadData();
    }

    // ==================== LOAD DATA ====================
    private void loadData() {
        dataList = controller.getAll();
        filterData();
    }

    // ==================== FILTER DATA ====================
    private void filterData() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String type = cboFilter.getSelectedItem().toString();

        model.setRowCount(0);

        for (KiemTraPhong ktp : dataList) {

            String maHoaDon = String.valueOf(ktp.getMaHoaDon());
            Phong p = phongController.getById(ktp.getMaPhong());
            String tenPhong = (p != null) ? p.getSoPhong() : "N/A";

            boolean match = switch (type) {
                case "Mã Hóa Đơn" -> maHoaDon.contains(keyword);
                case "Mã Phòng" -> (tenPhong.toLowerCase().contains(keyword));
                default -> maHoaDon.contains(keyword) || tenPhong.toLowerCase().contains(keyword);
            };

            if (match) {
                model.addRow(new Object[]{
                        ktp.getMaKiemTraPhong(),
                        ktp.getMaHoaDon(),
                        tenPhong,
                        ktp.getNgayThanhToan(),

                        // ⭐ HIỂN THỊ TIỀN CÓ FORMAT
                        UIFactory.formatCurrency(ktp.getTienBoiThuong())
                });
            }
        }
    }

    // ==================== CLEAR FORM ====================
    private void clearForm() {
        txtID.setText("");
        txtMaHoaDon.setText("");
        txtMaPhong.setText("");
        txtNgayThanhToan.setText("");
        txtTienBoiThuong.setText("");
        txtSearch.setText("");
    }
}
