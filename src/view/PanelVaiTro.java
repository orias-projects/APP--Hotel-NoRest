package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import controller.VaiTroController;
import model.VaiTro;
import view.common.UIFactory;

import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PanelVaiTro extends JPanel {

    private VaiTroController controller = new VaiTroController();

    private JTable tbl;
    private DefaultTableModel model;
    private JTextField txtID, txtTenVaiTro, txtSearch;
    private JComboBox<String> cboFilter;

    private List<VaiTro> dataList;

    public PanelVaiTro() {

        setLayout(new BorderLayout());

        // ==================== TITLE PANEL ====================
        JPanel pnlTitle = UIFactory.createSectionTitle("VAI TRÒ");

        // ==================== FILTER PANEL ====================
        txtSearch = UIFactory.createTextField(25);
        cboFilter = new JComboBox<>(new String[]{
                "Tất cả", "Tên vai trò"
        });
        JPanel pnlFilter = UIFactory.createFilterPanel(txtSearch, cboFilter);

        // ==================== INPUT PANEL ====================
        JPanel pnlInput = UIFactory.createInputPanel(1, 4);

        txtID = UIFactory.createTextField(0);
        txtID.setEditable(false);

        txtTenVaiTro = UIFactory.createTextField(0);

        pnlInput.add(UIFactory.createLabel("ID:"));
        pnlInput.add(txtID);
        pnlInput.add(UIFactory.createLabel("Tên vai trò:"));
        pnlInput.add(txtTenVaiTro);

        // ==================== HEADER PANEL ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.add(pnlTitle, BorderLayout.NORTH);
        pnlHeader.add(pnlFilter, BorderLayout.CENTER);
        pnlHeader.add(pnlInput, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // ==================== TABLE ====================
        model = UIFactory.createTableModel(new String[]{"ID", "Tên Vai Trò"});
        tbl = UIFactory.createTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        add(new JScrollPane(tbl), BorderLayout.CENTER);

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tbl.getSelectedRow();
                if (row >= 0) {
                    txtID.setText(model.getValueAt(row, 0).toString());
                    txtTenVaiTro.setText(model.getValueAt(row, 1).toString());
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
        dataList = controller.getAll();
        filterData();
    }

    // ==================== FILTER DATA ====================
    private void filterData() {
        String keyword = txtSearch.getText().trim().toLowerCase();

        model.setRowCount(0);

        for (VaiTro vt : dataList) {

            String tenVaiTro = vt.getTenVaiTro().toLowerCase();

            boolean match = tenVaiTro.contains(keyword);

            if (match) {
                model.addRow(new Object[]{
                        vt.getMaVaiTro(),
                        vt.getTenVaiTro()
                });
            }
        }
    }

    // ==================== ADD ====================
    private void doAdd() {
        VaiTro vt = new VaiTro(0, txtTenVaiTro.getText());

        if (controller.add(vt)) {
            UIFactory.showMsg(this, "Thêm thành công");
            loadData();
        } else {
            UIFactory.showError(this, "Thêm thất bại!");
        }
    }

    // ==================== UPDATE ====================
    private void doUpdate() {
        if (txtID.getText().isEmpty()) {
            UIFactory.showError(this, "Chọn dòng cần sửa!");
            return;
        }

        VaiTro vt = new VaiTro(
                Integer.parseInt(txtID.getText()),
                txtTenVaiTro.getText()
        );

        if (controller.update(vt)) {
            UIFactory.showMsg(this, "Sửa thành công");
            loadData();
        } else {
            UIFactory.showError(this, "Sửa thất bại!");
        }
    }

    // ==================== DELETE ====================
    private void doDelete() {
        if (txtID.getText().isEmpty()) {
            UIFactory.showError(this, "Chọn dòng cần xóa!");
            return;
        }

        if (UIFactory.showConfirm(this, "Xóa vai trò này?")) {
            int id = Integer.parseInt(txtID.getText());

            if (controller.delete(id)) {
                UIFactory.showMsg(this, "Xóa thành công");
                loadData();
            } else {
                UIFactory.showError(this, "Xóa thất bại!");
            }
        }
    }

    // ==================== CLEAR FORM ====================
    private void clearForm() {
        txtID.setText("");
        txtTenVaiTro.setText("");
        txtSearch.setText("");
    }
}