package view;

import controller.LoaiPhongController;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.LoaiPhong;
import view.common.UIFactory;

public class PanelLoaiPhong extends JPanel {

    private LoaiPhongController controller = new LoaiPhongController();

    private JTable tbl;
    private DefaultTableModel model;

    private JTextField txtID, txtTenLoaiPhong, txtGiaCoBan, txtSearch;
    private JComboBox<String> cboFilter;

    private List<LoaiPhong> dataList;

    public PanelLoaiPhong() {

        setLayout(new BorderLayout());

        // ==================== TITLE PANEL ====================
        JPanel pnlTitle = UIFactory.createSectionTitle("LOẠI PHÒNG");

        // ==================== FILTER PANEL ====================
        txtSearch = UIFactory.createTextField(25);
        cboFilter = new JComboBox<>(new String[]{
                "Tất cả", "Tên loại phòng", "Giá trong khoảng"
        });
        JPanel pnlFilter = UIFactory.createFilterPanel(txtSearch, cboFilter);

        // ==================== INPUT PANEL ====================
        JPanel pnlInput = UIFactory.createInputPanel(2, 4);

        txtID = UIFactory.createTextField(0);
        txtID.setEditable(false);
        txtTenLoaiPhong = UIFactory.createTextField(0);
        txtGiaCoBan = UIFactory.createTextField(0);

        pnlInput.add(UIFactory.createLabel("ID:"));
        pnlInput.add(txtID);
        pnlInput.add(UIFactory.createLabel("Tên loại phòng:"));
        pnlInput.add(txtTenLoaiPhong);

        pnlInput.add(UIFactory.createLabel("Giá cơ bản:"));
        pnlInput.add(txtGiaCoBan);
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
        model = UIFactory.createTableModel(new String[]{"ID", "Tên Loại Phòng", "Giá Cơ Bản"});
        tbl = UIFactory.createTable(model);

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tbl.getSelectedRow();
                if (row >= 0) {
                    txtID.setText(model.getValueAt(row, 0) + "");
                    txtTenLoaiPhong.setText(model.getValueAt(row, 1) + "");
                    
                    // 💡 SỬA ĐIỂM 1: Khi click, hiển thị giá thô (không định dạng) cho phép người dùng chỉnh sửa dễ hơn
                    // Bạn cần một phương thức mới trong UIFactory để lấy giá trị thô từ chuỗi định dạng
                    // Giả định: UIFactory.getRawCurrencyValue(String formattedText)
                    String formattedPrice = model.getValueAt(row, 2).toString();
                    try {
                        BigDecimal rawValue = UIFactory.getRawCurrencyValue(formattedPrice);
                        txtGiaCoBan.setText(rawValue.toString());
                    } catch (Exception ex) {
                        txtGiaCoBan.setText(""); // Nếu lỗi, xóa trường giá
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
        KeyAdapter filterKey = new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterData();
            }
        };

        txtSearch.addKeyListener(filterKey);
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

        for (LoaiPhong lp : dataList) {
            String tenLoaiPhong = lp.getTenLoaiPhong().toLowerCase();
            BigDecimal giaCoBan = lp.getGiaCoBan();

            boolean match = false;

            switch (type) {

                case "Tên loại phòng":
                    match = tenLoaiPhong.contains(key);
                    break;
                    
                // 💡 CHƯA SỬA LỌC THEO GIÁ: Cần triển khai logic lọc theo khoảng giá
                case "Giá trong khoảng":
                    // Hiện tại chỉ lọc theo Tên và ID nên tạm thời gán luôn match = false/true
                    // Cần parse key thành 2 giá trị min/max để lọc.
                    match = false; // Tạm thời tắt lọc theo giá
                    break;


                default: // Tất cả
                    match = tenLoaiPhong.contains(key) ||
                            // SỬA ĐIỂM 2: Dùng giá trị đã định dạng để so sánh với key
                            UIFactory.formatCurrency(giaCoBan).toLowerCase().contains(key);
            }

            if (match || type.equals("Tất cả")) { // Luôn hiển thị nếu là Tất cả hoặc tìm thấy
                model.addRow(new Object[]{
                        lp.getMaLoaiPhong(),
                        lp.getTenLoaiPhong(),
                        UIFactory.formatCurrency(lp.getGiaCoBan()) // Hiển thị ĐỊNH DẠNG
                });
            }
        }
    }

    // ==================== ADD ====================
    private void doAdd() {
        try {
            // ✅ SỬA ĐIỂM 3: Xử lý trực tiếp String -> BigDecimal từ input
            BigDecimal giaCoBanBd = UIFactory.parseCurrencyToBigDecimal(txtGiaCoBan.getText());
            
            // Thêm logic kiểm tra (nếu cần)
            if (giaCoBanBd.compareTo(BigDecimal.ZERO) <= 0) {
                 UIFactory.showError(this, "Giá cơ bản phải lớn hơn 0.");
                 return;
            }

            LoaiPhong lp = new LoaiPhong(0, txtTenLoaiPhong.getText(), giaCoBanBd);

            if (controller.add(lp)) {
                UIFactory.showMsg(this, "Thêm thành công");
                loadData();
                clearForm();
            } else {
                 UIFactory.showError(this, "Thêm không thành công! (Kiểm tra Controller/Service)");
            }
        } catch (Exception ex) {
            // Bắt lỗi nếu người dùng nhập ký tự không phải số hoặc sai định dạng
            UIFactory.showError(this, "Giá không hợp lệ! Vui lòng nhập số.");
        }
    }

    // ==================== UPDATE ====================
    private void doUpdate() {
        if (txtID.getText().isEmpty()) return;

        try {
            // ✅ SỬA ĐIỂM 4: Xử lý trực tiếp String -> BigDecimal từ input
            BigDecimal giaCoBanBd = UIFactory.parseCurrencyToBigDecimal(txtGiaCoBan.getText());

            // Thêm logic kiểm tra (nếu cần)
            if (giaCoBanBd.compareTo(BigDecimal.ZERO) <= 0) {
                 UIFactory.showError(this, "Giá cơ bản phải lớn hơn 0.");
                 return;
            }
            
            LoaiPhong lp = new LoaiPhong(
                    Integer.parseInt(txtID.getText()),
                    txtTenLoaiPhong.getText(),
                    giaCoBanBd
            );

            if (controller.update(lp)) {
                UIFactory.showMsg(this, "Sửa thành công");
                loadData();
                clearForm();
            } else {
                 UIFactory.showError(this, "Sửa không thành công! (Kiểm tra Controller/Service)");
            }
        } catch (Exception ex) {
            UIFactory.showError(this, "Dữ liệu không hợp lệ! Kiểm tra ID và Giá.");
        }
    }

    // ==================== DELETE ====================
    private void doDelete() {
        if (txtID.getText().isEmpty()) return;

        if (UIFactory.showConfirm(this, "Bạn có chắc chắn muốn xóa loại phòng này?")) {
            if (controller.delete(Integer.parseInt(txtID.getText()))) {
                UIFactory.showMsg(this, "Xóa thành công");
                loadData();
                clearForm();
            } else {
                UIFactory.showError(this, "Xóa không thành công! Có thể phòng đang được sử dụng.");
            }
        }
    }

    // ==================== CLEAR FORM ====================
    private void clearForm() {
        txtID.setText("");
        txtTenLoaiPhong.setText("");
        txtGiaCoBan.setText("");
        txtSearch.setText("");
    }
}