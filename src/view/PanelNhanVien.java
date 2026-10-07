package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.NhanVienController;
import model.NhanVien;
import view.common.UIFactory;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PanelNhanVien extends JPanel {

    private NhanVienController controller = new NhanVienController();
    private JTable tbl;
    private DefaultTableModel model;

    private JTextField txtID, txtTenNhanVien, txtSoDienThoai, txtEmail, txtSearch;
    private JComboBox<String> cboFilter;

    private List<NhanVien> dataList;

    public PanelNhanVien() {

        setLayout(new BorderLayout());

        // ==================== TITLE PANEL ====================
        JPanel pnlTitle = UIFactory.createSectionTitle("NHÂN VIÊN");

        // ==================== FILTER PANEL ====================
        txtSearch = UIFactory.createTextField(25);
        cboFilter = new JComboBox<>(new String[]{"Tất cả", "Tên nhân viên", "Email", "SĐT"});
        JPanel pnlFilter = UIFactory.createFilterPanel(txtSearch, cboFilter);

        // ==================== INPUT PANEL ====================
        JPanel pnlInput = UIFactory.createInputPanel(2, 4);

        txtID = UIFactory.createTextField(0);
        txtID.setEditable(false);
        txtTenNhanVien = UIFactory.createTextField(0);
        txtSoDienThoai = UIFactory.createTextField(0);
        txtEmail = UIFactory.createTextField(0);

        pnlInput.add(UIFactory.createLabel("ID:"));
        pnlInput.add(txtID);
        pnlInput.add(UIFactory.createLabel("Tên nhân viên:"));
        pnlInput.add(txtTenNhanVien);

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
        model = UIFactory.createTableModel(new String[]{"ID", "Tên NV", "SĐT", "Email"});
        tbl = UIFactory.createTable(model);

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tbl.getSelectedRow();
                if (row >= 0) {
                    txtID.setText(model.getValueAt(row, 0) + "");
                    txtTenNhanVien.setText(model.getValueAt(row, 1) + "");
                    txtSoDienThoai.setText(model.getValueAt(row, 2) + "");
                    txtEmail.setText(model.getValueAt(row, 3) + "");
                }
            }
        });

        add(new JScrollPane(tbl), BorderLayout.CENTER);

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
        dataList = controller.getAll();
        filterData();
    }

    // ==================== FILTER DATA ====================
    private void filterData() {
        String key = txtSearch.getText().trim().toLowerCase();
        String type = cboFilter.getSelectedItem().toString();

        model.setRowCount(0);

        for (NhanVien nv : dataList) {
            String tenNhanVien = nv.getTenNhanVien().toLowerCase();
            String email = nv.getEmail().toLowerCase();
            String soDienThoai = nv.getSoDienThoai().toLowerCase();

            boolean match =
                    switch (type) {
                        case "Tên nhân viên" -> tenNhanVien.contains(key);
                        case "Email" -> email.contains(key);
                        case "SĐT" -> soDienThoai.contains(key);
                        default -> tenNhanVien.contains(key) || email.contains(key) || soDienThoai.contains(key);
                    };

            if (match) {
                model.addRow(new Object[]{
                        nv.getMaNhanVien(),
                        nv.getTenNhanVien(),
                        nv.getSoDienThoai(),
                        nv.getEmail()
                });
            }
        }
    }

    // ==================== ADD ====================
    private void doAdd() {
        NhanVien nv = new NhanVien(
                0, txtTenNhanVien.getText(), txtSoDienThoai.getText(), txtEmail.getText()
        );

        if (controller.add(nv)) {
            UIFactory.showMsg(this, "Thêm thành công");
            loadData();
        }
    }

    // ==================== UPDATE ====================
    private void doUpdate() {
        if (txtID.getText().isEmpty()) return;

        NhanVien nv = new NhanVien(
                Integer.parseInt(txtID.getText()),
                txtTenNhanVien.getText(),
                txtSoDienThoai.getText(),
                txtEmail.getText()
        );

        if (controller.update(nv)) {
            UIFactory.showMsg(this, "Sửa thành công");
            loadData();
        }
    }

    // ==================== DELETE ====================
    private void doDelete() {
        if (txtID.getText().isEmpty()) return;

        if (controller.delete(Integer.parseInt(txtID.getText()))) {
            UIFactory.showMsg(this, "Xóa thành công");
            loadData();
        }
    }

    // ==================== CLEAR FORM ====================
    private void clearForm() {
        txtID.setText("");
        txtTenNhanVien.setText("");
        txtSoDienThoai.setText("");
        txtEmail.setText("");
        txtSearch.setText("");
    }
}