package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.TaiKhoanController;
import model.NhanVien;
import model.TaiKhoan;
import model.VaiTro;
import view.common.UIFactory;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PanelTaiKhoan extends JPanel {

    private TaiKhoanController controller = new TaiKhoanController();

    private JTable tbl;
    private DefaultTableModel model;

    private JTextField txtID, txtTaiKhoan, txtMatKhau, txtSearch;
    private JComboBox<NhanVien> cboNhanVien;
    private JComboBox<VaiTro> cboVaiTro;
    private JComboBox<String> cboFilter;

    private List<TaiKhoan> dataList;

    public PanelTaiKhoan() {

        setLayout(new BorderLayout());

        // ==================== TITLE PANEL ====================
        JPanel pnlTitle = UIFactory.createSectionTitle("TÀI KHOẢN");

        // ==================== FILTER PANEL ====================
        txtSearch = UIFactory.createTextField(25);
        cboFilter = new JComboBox<>(new String[]{
                "Tất cả", "Tài khoản", "Nhân viên", "Vai trò"
        });
        JPanel pnlFilter = UIFactory.createFilterPanel(txtSearch, cboFilter);

        // ==================== INPUT PANEL ====================
        JPanel pnlInput = UIFactory.createInputPanel(3, 4);

        txtID = UIFactory.createTextField(0);
        txtID.setEditable(false);

        txtTaiKhoan = UIFactory.createTextField(0);
        txtMatKhau = UIFactory.createTextField(0);

        cboNhanVien = new JComboBox<>();
        cboVaiTro = new JComboBox<>();

        loadComboNhanVien();
        loadComboVaiTro();

        pnlInput.add(UIFactory.createLabel("ID:"));
        pnlInput.add(txtID);

        pnlInput.add(UIFactory.createLabel("Tài khoản:"));
        pnlInput.add(txtTaiKhoan);

        pnlInput.add(UIFactory.createLabel("Vai trò:"));
        pnlInput.add(cboVaiTro);

        pnlInput.add(UIFactory.createLabel("Mật khẩu:"));
        pnlInput.add(txtMatKhau);

        pnlInput.add(UIFactory.createLabel("Nhân viên:"));
        pnlInput.add(cboNhanVien);

        // ==================== HEADER PANEL ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.add(pnlTitle, BorderLayout.NORTH);
        pnlHeader.add(pnlFilter, BorderLayout.CENTER);
        pnlHeader.add(pnlInput, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // ==================== TABLE ====================
        model = UIFactory.createTableModel(new String[]{
                "ID", "Tài khoản", "Mật khẩu", "Nhân viên", "Vai trò"
        });

        tbl = UIFactory.createTable(model);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tbl.getSelectedRow();
                txtID.setText(model.getValueAt(row, 0) + "");
                txtTaiKhoan.setText(model.getValueAt(row, 1) + "");
                txtMatKhau.setText(model.getValueAt(row, 2) + "");

                selectNhanVien((String) model.getValueAt(row, 3));
                selectVaiTro((String) model.getValueAt(row, 4));
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

    // ==================== LOAD COMBOBOX ====================
    private void loadComboNhanVien() {
        cboNhanVien.removeAllItems();
        for (NhanVien nv : controller.loadNhanVien()) {
            cboNhanVien.addItem(nv);
        }
    }

    private void loadComboVaiTro() {
        cboVaiTro.removeAllItems();
        for (VaiTro vt : controller.loadVaiTro()) {
            cboVaiTro.addItem(vt);
        }
    }

    // ==================== SELECT COMBOBOX ====================
    private void selectNhanVien(String name) {
        for (int i = 0; i < cboNhanVien.getItemCount(); i++) {
            if (cboNhanVien.getItemAt(i).getTenNhanVien().equals(name)) {
                cboNhanVien.setSelectedIndex(i);
                break;
            }
        }
    }

    private void selectVaiTro(String name) {
        for (int i = 0; i < cboVaiTro.getItemCount(); i++) {
            if (cboVaiTro.getItemAt(i).getTenVaiTro().equals(name)) {
                cboVaiTro.setSelectedIndex(i);
                break;
            }
        }
    }

    // ==================== LOAD DATA ====================
    private void loadData() {
        dataList = controller.getAll();
        filterData();
    }

    // ==================== FILTER DATA ====================
    private void filterData() {
        String key = txtSearch.getText().toLowerCase();
        String type = cboFilter.getSelectedItem().toString();

        model.setRowCount(0);

        for (TaiKhoan tk : dataList) {

            String nvName = controller.loadNhanVien().stream()
                    .filter(x -> x.getMaNhanVien() == tk.getMaNhanVien())
                    .findFirst().map(NhanVien::getTenNhanVien).orElse("NULL");

            String vtName = controller.loadVaiTro().stream()
                    .filter(x -> x.getMaVaiTro() == tk.getMaVaiTro())
                    .findFirst().map(VaiTro::getTenVaiTro).orElse("NULL");

            boolean match = switch (type) {
                case "Tài khoản" -> tk.getTaiKhoan().toLowerCase().contains(key);
                case "Nhân viên" -> nvName.toLowerCase().contains(key);
                case "Vai trò" -> vtName.toLowerCase().contains(key);
                default -> tk.getTaiKhoan().toLowerCase().contains(key)
                        || nvName.toLowerCase().contains(key)
                        || vtName.toLowerCase().contains(key);
            };

            if (match) {
                model.addRow(new Object[]{
                        tk.getId(),
                        tk.getTaiKhoan(),
                        tk.getMatKhau(),
                        nvName,
                        vtName
                });
            }
        }
    }

    // ==================== ADD ====================
    private void doAdd() {
        TaiKhoan tk = getFormData(0);
        if (controller.add(tk)) {
            UIFactory.showMsg(this, "Thêm thành công");
            loadData();
        }
    }

    // ==================== UPDATE ====================
    private void doUpdate() {
        if (txtID.getText().isEmpty()) {
            UIFactory.showError(this, "Chọn dòng để sửa!");
            return;
        }

        TaiKhoan tk = getFormData(Integer.parseInt(txtID.getText()));

        if (controller.update(tk)) {
            UIFactory.showMsg(this, "Sửa thành công");
            loadData();
        }
    }

    // ==================== DELETE ====================
    private void doDelete() {
        if (txtID.getText().isEmpty()) {
            UIFactory.showError(this, "Chọn dòng để xóa!");
            return;
        }

        int id = Integer.parseInt(txtID.getText());

        if (controller.delete(id)) {
            UIFactory.showMsg(this, "Xóa thành công");
            loadData();
        }
    }

    // ==================== GET FORM DATA ====================
    private TaiKhoan getFormData(int id) {
        NhanVien nv = (NhanVien) cboNhanVien.getSelectedItem();
        VaiTro vt = (VaiTro) cboVaiTro.getSelectedItem();

        return new TaiKhoan(
                id,
                txtTaiKhoan.getText(),
                txtMatKhau.getText(),
                nv.getMaNhanVien(),
                vt.getMaVaiTro()
        );
    }

    // ==================== CLEAR FORM ====================
    private void clearForm() {
        txtID.setText("");
        txtTaiKhoan.setText("");
        txtMatKhau.setText("");
        txtSearch.setText("");
    }
}