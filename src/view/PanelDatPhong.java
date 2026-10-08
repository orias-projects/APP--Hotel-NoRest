package view;

import controller.DatPhongController;
import controller.HoaDonController;
import controller.KhachHangController;
import controller.PhongController;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.DatPhong;
import model.HoaDon;
import model.KhachHang;
import model.KiemTraPhong;
import model.LoaiPhong;
import model.Phong;
import model.TaiKhoan;
import service.KiemTraPhongService;
import util.DatPhongUtils;
import view.common.UIFactory;

public class PanelDatPhong extends JPanel {

    private DatPhongController datPhongController = new DatPhongController();
    private KhachHangController khachHangController = new KhachHangController();
    private PhongController phongController = new PhongController();
    private HoaDonController hoaDonController = new HoaDonController();

    private JTable tbl;
    private DefaultTableModel model;

    private JTextField txtID, txtTienPhong, txtTienPhat;
    private JPanel dateTimeNgayNhan, dateTimeNgayHenTra; // Date-time pickers
    private JComboBox<KhachHang> cboKhachHang;
    private JComboBox<Phong> cboPhong;

    private JTextField txtSearch;
    private JComboBox<String> cboFilter;

    private TaiKhoan userLogin;
    private List<DatPhong> dataList;

    public PanelDatPhong(TaiKhoan user) {
        this.userLogin = user;
        setLayout(new BorderLayout());

        // ==================== TITLE PANEL ====================
        JPanel pnlTitle = UIFactory.createSectionTitle("ĐẶT PHÒNG");

        // ==================== FILTER PANEL ====================
        txtSearch = UIFactory.createTextField(25);
        cboFilter = new JComboBox<>(new String[] {
                "Tất cả", "Khách hàng", "Phòng"
        });
        JPanel pnlFilter = UIFactory.createFilterPanel(txtSearch, cboFilter);

        // ==================== INPUT PANEL ====================
        JPanel pnlInput = UIFactory.createInputPanel(4, 4);

        txtID = UIFactory.createTextField(0);
        txtID.setEditable(false);

        cboKhachHang = new JComboBox<>();
        loadKhachHang();

        cboPhong = new JComboBox<>();
        loadPhong();

        dateTimeNgayNhan = UIFactory.createDateTimeChooser();
        dateTimeNgayHenTra = UIFactory.createDateTimeChooser();

        txtTienPhong = UIFactory.createTextField(0);
        txtTienPhong.setEditable(false);

        txtTienPhat = UIFactory.createTextField(0);
        txtTienPhat.setEditable(false);

        pnlInput.add(UIFactory.createLabel("Mã đặt phòng:"));
        pnlInput.add(txtID);
        pnlInput.add(UIFactory.createLabel("Khách hàng:"));
        pnlInput.add(cboKhachHang);
        pnlInput.add(UIFactory.createLabel("Phòng:"));
        pnlInput.add(cboPhong);
        pnlInput.add(UIFactory.createLabel("Ngày nhận:"));
        pnlInput.add(dateTimeNgayNhan);
        pnlInput.add(UIFactory.createLabel("Ngày hẹn trả:"));
        pnlInput.add(dateTimeNgayHenTra);
        pnlInput.add(UIFactory.createLabel("Tiền phòng:"));
        pnlInput.add(txtTienPhong);
        pnlInput.add(UIFactory.createLabel("Tiền phạt:"));
        pnlInput.add(txtTienPhat);

        // ==================== HEADER PANEL ====================
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.add(pnlTitle, BorderLayout.NORTH);
        pnlHeader.add(pnlFilter, BorderLayout.CENTER);
        pnlHeader.add(pnlInput, BorderLayout.SOUTH);

        add(pnlHeader, BorderLayout.NORTH);

        // ==================== TABLE ====================
        model = UIFactory.createTableModel(new String[] {
                "ID", "Khách", "Phòng", "Nhận", "Hẹn trả", "Ngày trả",
                "NV lập", "Tiền phòng", "Tiền phạt"
        });

        tbl = UIFactory.createTable(model);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int row = tbl.getSelectedRow();
                if (row < 0)
                    return;

                txtID.setText(model.getValueAt(row, 0).toString());
                selectKhachHang(model.getValueAt(row, 1).toString());
                selectPhong(model.getValueAt(row, 2).toString());

                // Set dates to date-time pickers
                Timestamp ngayNhan = UIFactory.parseTimestamp(model.getValueAt(row, 3).toString());
                Timestamp ngayHenTra = UIFactory.parseTimestamp(model.getValueAt(row, 4).toString());
                UIFactory.setDateTimeToChooser(dateTimeNgayNhan, ngayNhan);
                UIFactory.setDateTimeToChooser(dateTimeNgayHenTra, ngayHenTra);

                txtTienPhong.setText(model.getValueAt(row, 7).toString());
                txtTienPhat.setText(model.getValueAt(row, 8).toString());
            }
        });

        // ==================== BUTTON PANEL ====================
        JPanel pnlBottom = UIFactory.createPanelBase();
        JButton btnAdd = UIFactory.createBtnPrimary("Đặt phòng");
        JButton btnTraPhong = UIFactory.createBtnUpdate("Trả phòng");
        JButton btnDelete = UIFactory.createBtnDelete("Hủy đặt");
        JButton btnRefresh = UIFactory.createBtnRefresh("Làm mới");

        pnlBottom.add(btnAdd);
        pnlBottom.add(btnTraPhong);
        pnlBottom.add(btnDelete);
        pnlBottom.add(btnRefresh);

        add(pnlBottom, BorderLayout.SOUTH);

        // ==================== EVENTS ====================
        txtSearch.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterData(txtSearch.getText(), cboFilter.getSelectedItem().toString());
            }
        });

        cboFilter.addActionListener(e -> filterData(txtSearch.getText(), cboFilter.getSelectedItem().toString()));

        btnAdd.addActionListener(e -> doAdd());
        btnDelete.addActionListener(e -> doDelete());
        btnRefresh.addActionListener(e -> clearForm());
        btnTraPhong.addActionListener(e -> doTraPhong());

        loadData();
    }

    // ==================== LOAD COMBOBOX ====================
    private void loadKhachHang() {
        for (KhachHang kh : khachHangController.getAllKhachHang())
            cboKhachHang.addItem(kh);
    }

    private void loadPhong() {
        cboPhong.removeAllItems();
        for (Phong p : phongController.getAll()) {
            if (p.getTrangThai().equalsIgnoreCase("Trống")) {
                cboPhong.addItem(p);
            }
        }
    }

    // ==================== SELECT COMBOBOX ====================
    private void selectKhachHang(String name) {
        for (int i = 0; i < cboKhachHang.getItemCount(); i++) {
            if (cboKhachHang.getItemAt(i).getTenKhachHang().equals(name)) {
                cboKhachHang.setSelectedIndex(i);
                return;
            }
        }
    }

    private void selectPhong(String soPhong) {
        for (int i = 0; i < cboPhong.getItemCount(); i++) {
            if (cboPhong.getItemAt(i).getSoPhong().equals(soPhong)) {
                cboPhong.setSelectedIndex(i);
                return;
            }
        }
    }

    // ==================== LOAD DATA ====================
    private void loadData() {
        dataList = datPhongController.getAll();
        model.setRowCount(0);
        for (DatPhong d : dataList) {
            HoaDon hd = hoaDonController.getById(d.getMaHoaDon());
            if (hd == null)
                continue;

            KhachHang kh = khachHangController.getById(hd.getMaKhachHang());
            Phong p = phongController.getById(d.getMaPhong());

            model.addRow(new Object[] {
                    d.getMaDatPhong(),
                    kh.getTenKhachHang(),
                    p.getSoPhong(),
                    UIFactory.formatTimestamp(d.getNgayNhanPhong()),
                    UIFactory.formatTimestamp(d.getNgayHenTra()),
                    UIFactory.formatTimestamp(d.getNgayTraPhong()),
                    d.getMaNhanVien(),
                    UIFactory.formatCurrency(d.getTienPhong()),
                    UIFactory.formatCurrency(d.getTienPhat())
            });
        }
    }

    // ==================== FILTER DATA ====================
    private void filterData(String key, String type) {
        key = key.toLowerCase();
        model.setRowCount(0);

        for (DatPhong d : dataList) {

            HoaDon hd = hoaDonController.getById(d.getMaHoaDon());
            if (hd == null)
                continue;

            KhachHang kh = khachHangController.getById(hd.getMaKhachHang());
            Phong p = phongController.getById(d.getMaPhong());

            boolean match;

            if (type.equals("Khách hàng"))
                match = kh.getTenKhachHang().toLowerCase().contains(key);
            else if (type.equals("Phòng"))
                match = p.getSoPhong().toLowerCase().contains(key);
            else
                match = kh.getTenKhachHang().toLowerCase().contains(key)
                        || p.getSoPhong().toLowerCase().contains(key);

            if (match) {
                model.addRow(new Object[] {
                        d.getMaDatPhong(),
                        kh.getTenKhachHang(),
                        p.getSoPhong(),
                        UIFactory.formatTimestamp(d.getNgayNhanPhong()),
                        UIFactory.formatTimestamp(d.getNgayHenTra()),
                        UIFactory.formatTimestamp(d.getNgayTraPhong()),
                        d.getMaNhanVien(),
                        UIFactory.formatCurrency(d.getTienPhong()),
                        UIFactory.formatCurrency(d.getTienPhat())
                });
            }
        }
    }

    // ==================== ADD ====================
    private void doAdd() {
        try {
            KhachHang kh = (KhachHang) cboKhachHang.getSelectedItem();
            Phong p = (Phong) cboPhong.getSelectedItem();

            // Get dates from date-time pickers
            Timestamp ngayNhan = UIFactory.getDateTimeFromChooser(dateTimeNgayNhan);
            Timestamp ngayHenTra = UIFactory.getDateTimeFromChooser(dateTimeNgayHenTra);

            if (ngayNhan == null || ngayHenTra == null) {
                UIFactory.showError(this, "Vui lòng chọn ngày nhận và ngày hẹn trả!");
                return;
            }

            LoaiPhong loaiPhong = phongController.getLoaiPhongByPhong(p.getMaPhong());
            BigDecimal giaNgay = loaiPhong.getGiaCoBan();

            BigDecimal tienPhong = DatPhongUtils.tinhTienPhong(ngayNhan, ngayHenTra, giaNgay);

            // Tạo hóa đơn mới
            HoaDon hd = new HoaDon(0, userLogin.getMaNhanVien(), kh.getMaKhachHang(),
                    new Timestamp(System.currentTimeMillis()), null, BigDecimal.ZERO, "Chưa thanh toán");

            int maHoaDon = hoaDonController.createWithKiemTraPhong(hd, p.getMaPhong());
            if (maHoaDon <= 0) {
                UIFactory.showError(this, "Lỗi tạo hóa đơn!");
                return;
            }

            DatPhong d = new DatPhong(
                    0, maHoaDon, userLogin.getMaNhanVien(), p.getMaPhong(),
                    new Timestamp(System.currentTimeMillis()),
                    ngayNhan, ngayHenTra,
                    null, null,
                    tienPhong,
                    BigDecimal.ZERO);

            if (datPhongController.add(d)) {

                phongController.updateStatus(p.getMaPhong(), "Đang ở");

                UIFactory.showMsg(this, "Đặt phòng thành công!");
                loadData();
                loadPhong();
            }

        } catch (Exception e) {
            UIFactory.showError(this, "Lỗi dữ liệu: " + e.getMessage());
        }
    }

    // ==================== TRA PHONG ====================
    private void doTraPhong() {
        if (txtID.getText().isEmpty()) {
            UIFactory.showError(this, "Vui lòng chọn đặt phòng cần trả!");
            return;
        }

        if (!UIFactory.showConfirm(this, "Xác nhận trả phòng?"))
            return;

        try {
            int id = Integer.parseInt(txtID.getText());
            DatPhong dp = datPhongController.getById(id);

            HoaDon hd = hoaDonController.getById(dp.getMaHoaDon());
            if (hd.getTrangThai().equals("Đã thanh toán")) {
                UIFactory.showError(this, "Hóa đơn này đã thanh toán!\nKhông thể trả phòng thêm.");
                return;
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            Phong p = phongController.getById(dp.getMaPhong());
            LoaiPhong loaiPhong = phongController.getLoaiPhongByPhong(p.getMaPhong());
            BigDecimal giaNgay = loaiPhong.getGiaCoBan();

            BigDecimal tienPhong = dp.getTienPhong() != null ? dp.getTienPhong() : BigDecimal.ZERO;
            BigDecimal tienPhat = DatPhongUtils.tinhPhatTheoGio(dp.getNgayHenTra(), now, giaNgay);

            // Tiền bồi thường lấy trực tiếp từ KiemTraPhong
            KiemTraPhongService ktPhongService = new KiemTraPhongService();
            KiemTraPhong kt = ktPhongService.getByMaHoaDon(dp.getMaHoaDon());
            BigDecimal tienBoiThuong = kt != null && kt.getTienBoiThuong() != null
                    ? kt.getTienBoiThuong()
                    : BigDecimal.ZERO;

            // Cập nhật trả phòng
            boolean success = datPhongController.traPhong(id, now, tienPhong, tienPhat);

            if (success) {
                BigDecimal tongTien = tienPhong.add(tienPhat).add(tienBoiThuong);

                // Cập nhật hóa đơn
                hd.setTongTien(tongTien);
                hd.setTrangThai("Đã thanh toán");
                hd.setNgayThanhToan(now);
                hoaDonController.update(hd);

                // Đồng bộ ngày thanh toán cho KiemTraPhong
                hoaDonController.updateNgayThanhToan(dp.getMaHoaDon(), now);

                // Cập nhật trạng thái phòng
                phongController.updateStatus(dp.getMaPhong(), "Trống");

                UIFactory.showMsg(this,
                        "Trả phòng thành công!\n" +
                                "Tiền phòng: " + UIFactory.formatCurrency(tienPhong) + "\n" +
                                "Tiền phạt: " + UIFactory.formatCurrency(tienPhat) + "\n" +
                                "Tiền bồi thường: " + UIFactory.formatCurrency(tienBoiThuong) + "\n" +
                                "Tổng hóa đơn: " + UIFactory.formatCurrency(tongTien));

                loadData();
                loadPhong();
                clearForm();
            }

        } catch (Exception ex) {
            UIFactory.showError(this, "Lỗi trả phòng: " + ex.getMessage());
        }
    }

    // ==================== DELETE ====================
    private void doDelete() {
        if (txtID.getText().isEmpty())
            return;

        if (!UIFactory.showConfirm(this, "Hủy đặt phòng?"))
            return;

        int id = Integer.parseInt(txtID.getText());
        DatPhong dp = datPhongController.getById(id);

        HoaDon hd = hoaDonController.getById(dp.getMaHoaDon());
        if (hd.getTrangThai().equals("Đã thanh toán")) {
            UIFactory.showError(this,
                    "Hóa đơn đã thanh toán!\nKhông thể hủy đặt phòng.");
            return;
        }

        phongController.updateStatus(dp.getMaPhong(), "Trống");

        datPhongController.delete(id);

        hoaDonController.delete(dp.getMaHoaDon());

        loadData();
        loadPhong();
        clearForm();
    }

    // ==================== CLEAR FORM ====================
    private void clearForm() {
        txtID.setText("");
        UIFactory.setDateTimeToChooser(dateTimeNgayNhan, null);
        UIFactory.setDateTimeToChooser(dateTimeNgayHenTra, null);
        txtTienPhong.setText("");
        txtTienPhat.setText("");

        loadPhong();
        if (cboKhachHang.getItemCount() > 0)
            cboKhachHang.setSelectedIndex(0);
    }
}