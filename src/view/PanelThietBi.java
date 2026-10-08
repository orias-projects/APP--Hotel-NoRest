package view;

import controller.ThietBiController;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.ThietBi;
import view.common.UIFactory;

public class PanelThietBi extends JPanel {

    private ThietBiController controller = new ThietBiController();

    private JTable tbl;
    private DefaultTableModel model;

    private JTextField txtID, txtTenThietBi, txtGiaThietBi;
    private JTextField txtSearch;
    private JComboBox<String> cboFilter;

    private List<ThietBi> dataList;

    public PanelThietBi() {
        setLayout(new BorderLayout());

        // ==================== TITLE PANEL ====================
        JPanel pnlTitle = UIFactory.createSectionTitle("THIẾT BỊ");

        // ==================== FILTER PANEL ====================
        txtSearch = UIFactory.createTextField(25);
        cboFilter = new JComboBox<>(new String[]{"Tất cả", "Tên Thiết Bị"});
        JPanel pnlFilter = UIFactory.createFilterPanel(txtSearch, cboFilter);

        // ==================== INPUT PANEL ====================
        JPanel pnlInput = UIFactory.createInputPanel(2, 4);

        txtID = UIFactory.createTextField(0);
        txtID.setEditable(false);

        txtTenThietBi = UIFactory.createTextField(0);
        txtGiaThietBi = UIFactory.createTextField(0);

        pnlInput.add(UIFactory.createLabel("ID:"));
        pnlInput.add(txtID);
        pnlInput.add(UIFactory.createLabel("Tên Thiết Bị:"));
        pnlInput.add(txtTenThietBi);
        pnlInput.add(UIFactory.createLabel("Giá Thiết Bị:"));
        pnlInput.add(txtGiaThietBi);
        pnlInput.add(new JLabel());
        pnlInput.add(new JLabel());

        // ==================== HEADER PANEL ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.add(pnlTitle, BorderLayout.NORTH);
        pnlHeader.add(pnlFilter, BorderLayout.CENTER);
        pnlHeader.add(pnlInput, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // ==================== TABLE ====================
        model = UIFactory.createTableModel(new String[]{"ID", "Tên Thiết Bị", "Giá Thiết Bị"});
        tbl = UIFactory.createTable(model);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // CLICK ĐỂ LẤY DỮ LIỆU CHO FORM
        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tbl.getSelectedRow();
                if (row >= 0) {
                    txtID.setText(model.getValueAt(row, 0).toString());
                    txtTenThietBi.setText(model.getValueAt(row, 1).toString());

                    // LẤY GIÁ THÔ (raw BigDecimal) từ giá đã format
                    String formattedPrice = model.getValueAt(row, 2).toString();
                    try {
                        BigDecimal raw = UIFactory.getRawCurrencyValue(formattedPrice);
                        txtGiaThietBi.setText(raw.toString());
                    } catch (Exception ex) {
                        txtGiaThietBi.setText("");
                    }
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
        String keyword = txtSearch.getText().trim().toLowerCase();
        String type = cboFilter.getSelectedItem().toString();

        model.setRowCount(0);

        for (ThietBi tb : dataList) {
            boolean match = switch (type) {
                case "Tên Thiết Bị" -> 
                    tb.getTenThietBi().toLowerCase().contains(keyword);
                default -> 
                    tb.getTenThietBi().toLowerCase().contains(keyword) ||
                    UIFactory.formatCurrency(tb.getGiaThietBi().stripTrailingZeros()).toLowerCase().contains(keyword);
            };

            if (match) {
                model.addRow(new Object[]{
                    tb.getMaThietBi(),
                    tb.getTenThietBi(),
                    UIFactory.formatCurrency(tb.getGiaThietBi())    // ⭐ HIỂN THỊ GIÁ ĐƯỢC FORMAT
                });
            }
        }
    }

    // ==================== ADD ====================
    private void doAdd() {
        try {
            BigDecimal giaBD = UIFactory.parseCurrencyToBigDecimal(txtGiaThietBi.getText());

            if (giaBD.compareTo(BigDecimal.ZERO) <= 0) {
                UIFactory.showError(this, "Giá phải lớn hơn 0.");
                return;
            }

            ThietBi tb = new ThietBi(0, txtTenThietBi.getText().trim(), giaBD);

            if (controller.add(tb)) {
                UIFactory.showMsg(this, "Thêm thành công");
                loadData();
                clearForm();
            } else {
                UIFactory.showError(this, "Thêm thất bại");
            }
        } catch (Exception ex) {
            UIFactory.showError(this, "Giá không hợp lệ!");
        }
    }

    // ==================== UPDATE ====================
    private void doUpdate() {
        if (txtID.getText().isEmpty()) {
            UIFactory.showError(this, "Chọn dòng cần sửa!");
            return;
        }

        try {
            BigDecimal giaBD = UIFactory.parseCurrencyToBigDecimal(txtGiaThietBi.getText());

            if (giaBD.compareTo(BigDecimal.ZERO) <= 0) {
                UIFactory.showError(this, "Giá phải lớn hơn 0.");
                return;
            }

            ThietBi tb = new ThietBi(
                Integer.parseInt(txtID.getText()),
                txtTenThietBi.getText().trim(),
                giaBD
            );

            if (controller.update(tb)) {
                UIFactory.showMsg(this, "Sửa thành công");
                loadData();
                clearForm();
            } else {
                UIFactory.showError(this, "Sửa thất bại");
            }
        } catch (Exception ex) {
            UIFactory.showError(this, "Dữ liệu không hợp lệ!");
        }
    }

    // ==================== DELETE ====================
    private void doDelete() {
        if (txtID.getText().isEmpty()) {
            UIFactory.showError(this, "Chọn dòng cần xóa!");
            return;
        }

        if (UIFactory.showConfirm(this, "Xóa thiết bị này?")) {
            int id = Integer.parseInt(txtID.getText());
            if (controller.delete(id)) {
                UIFactory.showMsg(this, "Xóa thành công");
                loadData();
                clearForm();
            } else {
                UIFactory.showError(this, "Xóa thất bại");
            }
        }
    }

    // ==================== CLEAR FORM ====================
    private void clearForm() {
        txtID.setText("");
        txtTenThietBi.setText("");
        txtGiaThietBi.setText("");
        txtSearch.setText("");
    }
}
