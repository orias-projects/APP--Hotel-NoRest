package view;

import controller.HoaDonController;
import controller.KiemTraChiTietController;
import controller.KiemTraPhongController;
import controller.PhongController;
import controller.ThietBiController;
import controller.ThietBiPhongController;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import model.HoaDon;
import model.KiemTraChiTiet;
import model.KiemTraPhong;
import model.Phong;
import model.TaiKhoan;
import model.ThietBi;
import model.ThietBiPhong;
import view.common.UIFactory;

public class PanelKiemTraChiTiet extends JPanel {

    private KiemTraChiTietController ktctController = new KiemTraChiTietController();
    private KiemTraPhongController ktpController = new KiemTraPhongController();
    private PhongController phongController = new PhongController();
    private ThietBiController thietBiController = new ThietBiController();
    private ThietBiPhongController tbPhongController = new ThietBiPhongController();

    private JTable tbl;
    private DefaultTableModel model;

    private JComboBox<KiemTraPhong> cboKiemTra;
    private JComboBox<ThietBi> cboThietBi;
    private JTextField txtSoLuong;

    private JButton btnUpdate;
    private JButton btnDelete;

    private TaiKhoan userLogin;
    private List<KiemTraChiTiet> dataList;

    public PanelKiemTraChiTiet(TaiKhoan user) {
        this.userLogin = user;
        setLayout(new BorderLayout());

        // 1. TẠO COMPONENT
        cboKiemTra = new JComboBox<>();
        cboThietBi = new JComboBox<>();
        txtSoLuong = UIFactory.createTextField(0);

        // INPUT FORM
        JPanel pnlInput = UIFactory.createInputPanel(3, 4);
        pnlInput.add(UIFactory.createLabel("Kiểm tra phòng:"));
        pnlInput.add(cboKiemTra);
        pnlInput.add(UIFactory.createLabel("Thiết bị:"));
        pnlInput.add(cboThietBi);
        pnlInput.add(UIFactory.createLabel("Số lượng hỏng:"));
        pnlInput.add(txtSoLuong);

        add(pnlInput, BorderLayout.NORTH);

        // TABLE
        model = UIFactory.createTableModel(new String[]{
                "ID", "Mã kiểm tra", "Phòng", "Thiết bị", "Số lượng hỏng", "Nhân viên"
        });
        tbl = UIFactory.createTable(model);
        add(new JScrollPane(tbl), BorderLayout.CENTER);

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                int r = tbl.getSelectedRow();
                if (r < 0) return;

                int maKiemTraPhong = (int) model.getValueAt(r, 1);
                int maThietBi = -1;
                Object tbObj = model.getValueAt(r, 3);

                // Lấy maThietBi từ tên thiết bị
                for (int i = 0; i < cboThietBi.getItemCount(); i++) {
                    ThietBi tb = cboThietBi.getItemAt(i);
                    if (tb.getTenThietBi().equals(tbObj.toString())) {
                        maThietBi = tb.getMaThietBi();
                        break;
                    }
                }

                // Set combo và text
                selectKiemTraById(maKiemTraPhong);
                if (maThietBi != -1) selectThietBiById(maThietBi);
                txtSoLuong.setText(model.getValueAt(r, 4).toString());

                // Disable Update/Delete nếu đã thanh toán
                boolean thanhToan = isThanhToan(maKiemTraPhong);
                btnUpdate.setEnabled(!thanhToan);
                btnDelete.setEnabled(!thanhToan);

                if (thanhToan) {
                    UIFactory.showMsg(PanelKiemTraChiTiet.this,
                            "Phòng này đã thanh toán, không thể sửa/xóa!");
                }
            }
        });

        // BUTTONS
        JPanel pnlBottom = new JPanel();
        JButton btnAdd = UIFactory.createBtnPrimary("Thêm");
        btnUpdate = UIFactory.createBtnUpdate("Cập nhật");
        btnDelete = UIFactory.createBtnDelete("Xóa");
        JButton btnRefresh = UIFactory.createBtnRefresh("Làm mới");

        pnlBottom.add(btnAdd);
        pnlBottom.add(btnUpdate);
        pnlBottom.add(btnDelete);
        pnlBottom.add(btnRefresh);
        add(pnlBottom, BorderLayout.SOUTH);

        // EVENTS
        btnAdd.addActionListener(e -> doAdd());
        btnUpdate.addActionListener(e -> doUpdate());
        btnDelete.addActionListener(e -> doDelete());
        btnRefresh.addActionListener(e -> clearForm());

        cboKiemTra.addActionListener(e -> {
            KiemTraPhong ktp = (KiemTraPhong) cboKiemTra.getSelectedItem();
            if (ktp != null) loadThietBiTheoPhong(ktp.getMaPhong());
        });

        // LOAD DỮ LIỆU BAN ĐẦU
        loadKiemTraPhong();
        loadData();
    }

    // Kiểm tra trạng thái thanh toán
    private boolean isThanhToan(int maKiemTraPhong) {
        KiemTraPhong ktp = ktpController.getById(maKiemTraPhong);
        if (ktp == null) return false;
        HoaDon hd = new HoaDonController().getById(ktp.getMaHoaDon());
        return hd != null && "Đã thanh toán".equals(hd.getTrangThai());
    }

    // LOAD KIỂM TRA PHÒNG
    private void loadKiemTraPhong() {
        cboKiemTra.removeAllItems();
        HoaDonController hdController = new HoaDonController();

        for (KiemTraPhong ktp : ktpController.getAll()) {
            HoaDon hd = hdController.getById(ktp.getMaHoaDon());
            if (hd != null && "Chưa thanh toán".equals(hd.getTrangThai())) {
                cboKiemTra.addItem(ktp);
            }
        }

        if (cboKiemTra.getItemCount() > 0) {
            KiemTraPhong ktp = (KiemTraPhong) cboKiemTra.getItemAt(0);
            loadThietBiTheoPhong(ktp.getMaPhong());
        }
    }

    // LOAD THIẾT BỊ THEO PHÒNG
    private void loadThietBiTheoPhong(int maPhong) {
        if (cboThietBi == null) return;
        cboThietBi.removeAllItems();
        List<ThietBiPhong> listTbPhong = tbPhongController.getThietBiTheoPhong(maPhong);
        for (ThietBiPhong tbp : listTbPhong) {
            ThietBi tb = thietBiController.getById(tbp.getMaThietBi());
            if (tb != null) cboThietBi.addItem(tb);
        }
    }

    // CHỌN KIỂM TRA PHÒNG TRONG COMBO THEO ID
    private void selectKiemTraById(int maKiemTra) {
        for (int i = 0; i < cboKiemTra.getItemCount(); i++) {
            if (cboKiemTra.getItemAt(i).getMaKiemTraPhong() == maKiemTra) {
                cboKiemTra.setSelectedIndex(i);
                return;
            }
        }
    }

    // CHỌN THIẾT BỊ TRONG COMBO THEO ID
    private void selectThietBiById(int maThietBi) {
        for (int i = 0; i < cboThietBi.getItemCount(); i++) {
            if (cboThietBi.getItemAt(i).getMaThietBi() == maThietBi) {
                cboThietBi.setSelectedIndex(i);
                return;
            }
        }
    }

    // LOAD DỮ LIỆU
    private void loadData() {
        dataList = ktctController.getAll();
        fillTable();
        // Reset nút
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }

    private void fillTable() {
        model.setRowCount(0);
        for (KiemTraChiTiet ktct : dataList) {
            KiemTraPhong ktp = ktpController.getById(ktct.getMaKiemTraPhong());
            Phong p = phongController.getById(ktp.getMaPhong());
            ThietBi tb = thietBiController.getById(ktct.getMaThietBiPhong());
            model.addRow(new Object[]{
                    ktct.getMaKiemTraChiTiet(),
                    ktct.getMaKiemTraPhong(),
                    p != null ? p.getSoPhong() : "N/A",
                    tb != null ? tb.getTenThietBi() : "N/A",
                    ktct.getSoLuongBiHong(),
                    ktct.getMaNhanVien()
            });
        }
    }

    // THÊM
    private void doAdd() {
        KiemTraPhong ktp = (KiemTraPhong) cboKiemTra.getSelectedItem();
        ThietBi tb = (ThietBi) cboThietBi.getSelectedItem();

        if (ktp == null || tb == null) {
            UIFactory.showError(this, "Vui lòng chọn kiểm tra phòng và thiết bị!");
            return;
        }

        int soLuong;
        try {
            soLuong = Integer.parseInt(txtSoLuong.getText());
            if (soLuong < 0) {
                UIFactory.showError(this, "Số lượng hỏng không được âm!");
                return;
            }
        } catch (NumberFormatException ex) {
            UIFactory.showError(this, "Số lượng hỏng phải là số nguyên!");
            return;
        }

        try {
            KiemTraChiTiet ktct = new KiemTraChiTiet();
            ktct.setMaKiemTraPhong(ktp.getMaKiemTraPhong());
            ktct.setMaThietBiPhong(tb.getMaThietBi());
            ktct.setSoLuongBiHong(soLuong);
            ktct.setMaNhanVien(userLogin.getMaNhanVien());
            ktct.setNgayKiemTra(new java.util.Date());

            if (ktctController.add(ktct)) {
                UIFactory.showMsg(this, "Thêm chi tiết kiểm tra thành công!");
                loadData();
                clearForm();
            } else {
                UIFactory.showError(this, "Thêm chi tiết kiểm tra thất bại!");
            }

        } catch (Exception e) {
            UIFactory.showError(this, "Lỗi khi thêm chi tiết kiểm tra: " + e.getMessage());
        }
    }

    // CẬP NHẬT
    private void doUpdate() {
        int r = tbl.getSelectedRow();
        if (r < 0) {
            UIFactory.showError(this, "Vui lòng chọn dòng cần cập nhật!");
            return;
        }

        int maKiemTraPhong = (int) model.getValueAt(r, 1);
        if (isThanhToan(maKiemTraPhong)) {
            UIFactory.showError(this, "Phòng đã thanh toán, không thể cập nhật!");
            return;
        }

        try {
            int id = (int) model.getValueAt(r, 0);
            KiemTraPhong ktp = (KiemTraPhong) cboKiemTra.getSelectedItem();
            ThietBi tb = (ThietBi) cboThietBi.getSelectedItem();
            int soLuong = Integer.parseInt(txtSoLuong.getText());

            KiemTraChiTiet ktct = ktctController.getById(id);
            ktct.setMaKiemTraPhong(ktp.getMaKiemTraPhong());
            ktct.setMaThietBiPhong(tb.getMaThietBi());
            ktct.setSoLuongBiHong(soLuong);
            ktct.setMaNhanVien(userLogin.getMaNhanVien());
            ktct.setNgayKiemTra(new java.util.Date());

            boolean updated = ktctController.update(ktct);
            if (updated) {
                UIFactory.showMsg(this, "Cập nhật thành công!");
                loadData();
                clearForm();
            } else {
                UIFactory.showError(this, "Cập nhật thất bại!");
            }

        } catch (NumberFormatException ex) {
            UIFactory.showError(this, "Số lượng hỏng phải là số nguyên!");
        } catch (Exception e) {
            UIFactory.showError(this, "Lỗi: " + e.getMessage());
        }
    }

    // XÓA
    private void doDelete() {
        int r = tbl.getSelectedRow();
        if (r < 0) {
            UIFactory.showError(this, "Vui lòng chọn dòng cần xóa!");
            return;
        }

        int maKiemTraPhong = (int) model.getValueAt(r, 1);
        if (isThanhToan(maKiemTraPhong)) {
            UIFactory.showError(this, "Phòng đã thanh toán, không thể xóa!");
            return;
        }

        if (!UIFactory.showConfirm(this, "Xác nhận xóa?")) return;

        int id = (int) model.getValueAt(r, 0);
        try {
            if (ktctController.delete(id)) {
                UIFactory.showMsg(this, "Xóa thành công!");
                loadData();
                clearForm();
            } else {
                UIFactory.showError(this, "Xóa thất bại!");
            }
        } catch (Exception e) {
            UIFactory.showError(this, "Lỗi khi xóa chi tiết kiểm tra: " + e.getMessage());
        }
    }

    // LÀM MỚI FORM
    private void clearForm() {
        if (cboKiemTra.getItemCount() > 0) cboKiemTra.setSelectedIndex(0);
        if (cboThietBi.getItemCount() > 0) cboThietBi.setSelectedIndex(0);
        txtSoLuong.setText("");
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
    }
}
