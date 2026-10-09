package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.KhachHangController;
import model.KhachHang;
import view.common.UIFactory;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PanelKhachHang extends JPanel {

    private KhachHangController controller = new KhachHangController();

    private JTable tbl;
    private DefaultTableModel model;
    private JTextField txtTenKhachHang, txtSoDienThoai, txtEmail, txtID;
    private JTextField txtSearch;
    private JComboBox<String> cboFilter;

    private List<KhachHang> dataList;

    public PanelKhachHang() {

        setLayout(new BorderLayout());

        // ==================== TITLE PANEL ====================
        JPanel pnlTitle = UIFactory.createSectionTitle("KHÁCH HÀNG");

        // ==================== FILTER PANEL ====================
        txtSearch = UIFactory.createTextField(25);
        cboFilter = new JComboBox<>(new String[]{
                "Tất cả", "Tên khách", "Số điện thoại", "Email"
        });
        JPanel pnlFilter = UIFactory.createFilterPanel(txtSearch, cboFilter);

        // ==================== INPUT PANEL ====================
        JPanel pnlInput = UIFactory.createInputPanel(2, 4);

        txtID = UIFactory.createTextField(0);
        txtID.setEditable(false);

        txtTenKhachHang = UIFactory.createTextField(0);
        txtSoDienThoai = UIFactory.createTextField(0);
        txtEmail = UIFactory.createTextField(0);

        pnlInput.add(UIFactory.createLabel("ID:"));
        pnlInput.add(txtID);
        pnlInput.add(UIFactory.createLabel("Tên khách:"));
        pnlInput.add(txtTenKhachHang);

        pnlInput.add(UIFactory.createLabel("SĐT:"));
        pnlInput.add(txtSoDienThoai);
        pnlInput.add(UIFactory.createLabel("Email:"));
        pnlInput.add(txtEmail);

        // ==================== HEADER PANEL ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.add(pnlTitle, BorderLayout.NORTH);
        pnlHeader.add(pnlFilter, BorderLayout.CENTER);
        pnlHeader.add(pnlInput, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // ==================== TABLE ====================
        model = UIFactory.createTableModel(new String[]{"ID", "Tên Khách", "SĐT", "Email"});
        tbl = UIFactory.createTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tbl), BorderLayout.CENTER);

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tbl.getSelectedRow();
                if (row >= 0) {
                    txtID.setText(model.getValueAt(row, 0).toString());
                    txtTenKhachHang.setText(model.getValueAt(row, 1).toString());
                    txtSoDienThoai.setText(model.getValueAt(row, 2).toString());
                    txtEmail.setText(model.getValueAt(row, 3).toString());
                }
            }
        });

        // ==================== BUTTON PANEL ====================
        JPanel pnlBottom = UIFactory.createPanelBase();
        JButton btnAdd = UIFactory.createBtnPrimary("Thêm");
        JButton btnUpdate = UIFactory.createBtnUpdate("Sửa");
        JButton btnDelete = UIFactory.createBtnDelete("Xóa");
        JButton btnRefresh = UIFactory.createBtnRefresh("Làm mới");

        pnlBottom.add(btnAdd);
        pnlBottom.add(btnUpdate);
        pnlBottom.add(btnDelete);
        pnlBottom.add(btnRefresh);

        add(pnlBottom, BorderLayout.SOUTH);

        // ==================== EVENTS ====================
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterData();
            }
        });

        cboFilter.addActionListener(e -> filterData());

        btnAdd.addActionListener(e -> doAdd());

        btnUpdate.addActionListener(e -> doUpdate());

        btnDelete.addActionListener(e -> doDelete());

        btnRefresh.addActionListener(e -> clearForm());

        loadData();
    }

    // ==================== LOAD DATA ====================
    private void loadData() {
        dataList = controller.getAllKhachHang();
        filterData();
    }

    // ==================== FILTER DATA ====================
    private void filterData() {
        String keyword = txtSearch.getText().trim().toLowerCase();
        String type = cboFilter.getSelectedItem().toString();

        model.setRowCount(0);

        for (KhachHang kh : dataList) {

            String tenKhachHang = kh.getTenKhachHang().toLowerCase();
            String soDienThoai = kh.getSoDienThoai().toLowerCase();
            String email = kh.getEmail().toLowerCase();

            boolean match = switch (type) {
                case "Tên khách" -> tenKhachHang.contains(keyword);
                case "Số điện thoại" -> soDienThoai.contains(keyword);
                case "Email" -> email.contains(keyword);
                default -> tenKhachHang.contains(keyword) || soDienThoai.contains(keyword) || email.contains(keyword);
            };

            if (match) {
                model.addRow(new Object[]{
                        kh.getMaKhachHang(),
                        kh.getTenKhachHang(),
                        kh.getSoDienThoai(),
                        kh.getEmail()
                });
            }
        }
    }

    // ==================== ADD ====================
    private void doAdd() {
        KhachHang kh = new KhachHang(
                0,
                txtTenKhachHang.getText(),
                txtSoDienThoai.getText(),
                txtEmail.getText()
        );

        if (controller.addKhachHang(kh)) {
            UIFactory.showMsg(this, "Thêm thành công");
            loadData();
        } else {
            UIFactory.showError(this, "Thêm thất bại");
        }
    }

    // ==================== UPDATE ====================
    private void doUpdate() {
        if (txtID.getText().isEmpty()) {
            UIFactory.showError(this, "Chọn dòng cần sửa!");
            return;
        }

        KhachHang kh = new KhachHang(
                Integer.parseInt(txtID.getText()),
                txtTenKhachHang.getText(),
                txtSoDienThoai.getText(),
                txtEmail.getText()
        );

        if (controller.updateKhachHang(kh)) {
            UIFactory.showMsg(this, "Sửa thành công");
            loadData();
        } else {
            UIFactory.showError(this, "Sửa thất bại");
        }
    }

    // ==================== DELETE ====================
    private void doDelete() {
        if (txtID.getText().isEmpty()) {
            UIFactory.showError(this, "Chọn dòng cần xóa!");
            return;
        }

        if (UIFactory.showConfirm(this, "Xóa khách hàng này?")) {
            if (controller.deleteKhachHang(Integer.parseInt(txtID.getText()))) {
                UIFactory.showMsg(this, "Xóa thành công");
                loadData();
            } else {
                UIFactory.showError(this, "Xóa thất bại");
            }
        }
    }

    // ==================== CLEAR FORM ====================
    private void clearForm() {
        txtID.setText("");
        txtTenKhachHang.setText("");
        txtSoDienThoai.setText("");
        txtEmail.setText("");
        txtSearch.setText("");
    }
}