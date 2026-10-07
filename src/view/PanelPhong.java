package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.LoaiPhongController;
import controller.PhongController;
import model.LoaiPhong;
import model.Phong;
import view.common.UIFactory;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PanelPhong extends JPanel {

    private PhongController controller = new PhongController();
    private LoaiPhongController loaiPhongController = new LoaiPhongController();

    private JTable tbl;
    private DefaultTableModel model;

    private JTextField txtID, txtSoPhong, txtSearch;
    private JComboBox<String> cboLoaiPhong, cboTrangThai, cboFilter;

    private List<Phong> dataList;
    private List<LoaiPhong> loaiPhongList;

    public PanelPhong() {

        setLayout(new BorderLayout());

        // ==================== TITLE PANEL ====================
        JPanel pnlTitle = UIFactory.createSectionTitle("DANH SÁCH PHÒNG");

        // ==================== FILTER PANEL ====================
        txtSearch = UIFactory.createTextField(25);
        cboFilter = new JComboBox<>(new String[]{
                "Tất cả", "Số phòng", "Loại phòng", "Trạng thái"
        });
        JPanel pnlFilter = UIFactory.createFilterPanel(txtSearch, cboFilter);

        // ==================== INPUT PANEL ====================
        JPanel pnlInput = UIFactory.createInputPanel(2, 4);

        txtID = UIFactory.createTextField(0);
        txtID.setEditable(false);

        txtSoPhong = UIFactory.createTextField(0);

        cboLoaiPhong = new JComboBox<>();
        loadLoaiPhongToCombo();

        cboTrangThai = new JComboBox<>(new String[]{
                "Trống", "Đã đặt", "Đang ở", "Bảo trì"
        });

        pnlInput.add(UIFactory.createLabel("ID:"));
        pnlInput.add(txtID);
        pnlInput.add(UIFactory.createLabel("Số phòng:"));
        pnlInput.add(txtSoPhong);

        pnlInput.add(UIFactory.createLabel("Loại phòng:"));
        pnlInput.add(cboLoaiPhong);
        pnlInput.add(UIFactory.createLabel("Trạng thái:"));
        pnlInput.add(cboTrangThai);

        // ==================== HEADER PANEL ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.add(pnlTitle, BorderLayout.NORTH);
        pnlHeader.add(pnlFilter, BorderLayout.CENTER);
        pnlHeader.add(pnlInput, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // ==================== TABLE ====================
        model = UIFactory.createTableModel(new String[]{
                "ID", "Số phòng", "Loại phòng", "Trạng thái"
        });

        tbl = UIFactory.createTable(model);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tbl.getSelectedRow();
                txtID.setText(model.getValueAt(row, 0).toString());
                txtSoPhong.setText(model.getValueAt(row, 1).toString());
                cboLoaiPhong.setSelectedItem(model.getValueAt(row, 2).toString());
                cboTrangThai.setSelectedItem(model.getValueAt(row, 3).toString());
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
    private void loadLoaiPhongToCombo() {
        loaiPhongList = loaiPhongController.getAll();
        cboLoaiPhong.removeAllItems();

        for (LoaiPhong lp : loaiPhongList) {
            cboLoaiPhong.addItem(lp.getTenLoaiPhong());
        }
    }

    // ==================== HELPER ====================
    private int getMaLoaiPhongFromName(String name) {
        for (LoaiPhong lp : loaiPhongList) {
            if (lp.getTenLoaiPhong().equals(name)) return lp.getMaLoaiPhong();
        }
        return -1;
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

        for (Phong p : dataList) {
            String soPhong = p.getSoPhong().toLowerCase();
            String loaiPhong = p.getTenLoaiPhong().toLowerCase();
            String trangThai = p.getTrangThai().toLowerCase();

            boolean match = switch (type) {
                case "Số phòng" -> soPhong.contains(key);
                case "Loại phòng" -> loaiPhong.contains(key);
                case "Trạng thái" -> trangThai.contains(key);
                default -> soPhong.contains(key) || loaiPhong.contains(key) || trangThai.contains(key);
            };

            if (match) {
                model.addRow(new Object[]{
                        p.getMaPhong(),
                        p.getSoPhong(),
                        p.getTenLoaiPhong(),
                        p.getTrangThai()
                });
            }
        }
    }

    // ==================== ADD ====================
    private void doAdd() {
        try {
            String soPhong = txtSoPhong.getText();
            String tenLoaiPhong = cboLoaiPhong.getSelectedItem().toString();
            String trangThai = cboTrangThai.getSelectedItem().toString();
            int maLoaiPhong = getMaLoaiPhongFromName(tenLoaiPhong);

            Phong p = new Phong(0, soPhong, maLoaiPhong, trangThai);

            if (controller.add(p)) {
                UIFactory.showMsg(this, "Thêm thành công");
                loadData();
            }
        } catch (Exception e) {
            UIFactory.showError(this, "Lỗi dữ liệu!");
        }
    }

    // ==================== UPDATE ====================
    private void doUpdate() {
        if (txtID.getText().isEmpty()) return;

        try {
            int id = Integer.parseInt(txtID.getText());
            String soPhong = txtSoPhong.getText();
            String tenLoaiPhong = cboLoaiPhong.getSelectedItem().toString();
            String trangThai = cboTrangThai.getSelectedItem().toString();
            int maLoaiPhong = getMaLoaiPhongFromName(tenLoaiPhong);

            if (controller.update(new Phong(id, soPhong, maLoaiPhong, trangThai))) {
                UIFactory.showMsg(this, "Sửa thành công");
                loadData();
            }
        } catch (Exception e) {
            UIFactory.showError(this, "Lỗi!");
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
        txtSoPhong.setText("");
        txtSearch.setText("");
        cboLoaiPhong.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
    }
}