package view;

import controller.HoaDonController;
import controller.KhachHangController;
import model.HoaDon;
import model.KhachHang;
import view.common.UIFactory;
import view.common.InvoicePrinter;
import view.common.InvoicePreviewDialog;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

public class FrmSelectCustomerInvoice extends JDialog {

    private KhachHangController khachHangController = new KhachHangController();
    private HoaDonController hoaDonController = new HoaDonController();

    private JTextField txtSearchCustomer;
    private JTable tblCustomer;
    private DefaultTableModel modelCustomer;

    private JTable tblInvoice;
    private DefaultTableModel modelInvoice;
    private JButton btnPrint;

    private KhachHang selectedCustomer;
    // private HoaDon selectedInvoice;

    public FrmSelectCustomerInvoice(JFrame parent) {
        super(parent, "Chọn Khách Hàng & Hóa Đơn", true);
        setSize(1000, 600);
        setLocationRelativeTo(parent);
        setLayout(new GridLayout(1, 2, 10, 10));

        // LEFT PANEL: CUSTOMER SELECTION
        JPanel pnlLeft = new JPanel(new BorderLayout());
        pnlLeft.setBorder(BorderFactory.createTitledBorder("1. Chọn Khách Hàng"));

        JPanel pnlSearch = new JPanel(new BorderLayout(5, 5));
        pnlSearch.add(new JLabel("Tìm kiếm:"), BorderLayout.WEST);
        txtSearchCustomer = new JTextField();
        pnlSearch.add(txtSearchCustomer, BorderLayout.CENTER);
        pnlLeft.add(pnlSearch, BorderLayout.NORTH);

        modelCustomer = new DefaultTableModel(new String[] { "Mã", "Tên", "SĐT" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblCustomer = new JTable(modelCustomer);
        tblCustomer.setRowHeight(25);
        pnlLeft.add(new JScrollPane(tblCustomer), BorderLayout.CENTER);

        // RIGHT PANEL: INVOICE SELECTION
        JPanel pnlRight = new JPanel(new BorderLayout());
        pnlRight.setBorder(BorderFactory.createTitledBorder("2. Chọn Hóa Đơn Đã Thanh Toán"));

        modelInvoice = new DefaultTableModel(new String[] { "Mã HD", "Ngày Tạo", "Tổng Tiền" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInvoice = new JTable(modelInvoice);
        tblInvoice.setRowHeight(25);
        pnlRight.add(new JScrollPane(tblInvoice), BorderLayout.CENTER);

        btnPrint = new JButton("In Hóa Đơn");
        btnPrint.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnPrint.setBackground(new Color(41, 128, 185));
        btnPrint.setForeground(Color.WHITE);
        btnPrint.setEnabled(false);
        JPanel pnlBtn = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlBtn.add(btnPrint);
        pnlRight.add(pnlBtn, BorderLayout.SOUTH);

        add(pnlLeft);
        add(pnlRight);

        // EVENTS
        txtSearchCustomer.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                loadCustomers(txtSearchCustomer.getText());
            }
        });

        tblCustomer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblCustomer.getSelectedRow();
                if (row >= 0) {
                    int maKH = (int) tblCustomer.getValueAt(row, 0);
                    selectedCustomer = khachHangController.getById(maKH);
                    loadInvoices(maKH);
                }
            }
        });

        tblInvoice.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Enable print button if at least one row is selected
                btnPrint.setEnabled(tblInvoice.getSelectedRows().length > 0);
            }
        });

        btnPrint.addActionListener(e -> printInvoice());

        loadCustomers("");
    }

    private void loadCustomers(String keyword) {
        modelCustomer.setRowCount(0);
        List<KhachHang> list = khachHangController.search(keyword);
        for (KhachHang kh : list) {
            modelCustomer.addRow(new Object[] { kh.getMaKhachHang(), kh.getTenKhachHang(), kh.getSoDienThoai() });
        }
    }

    private void loadInvoices(int maKH) {
        modelInvoice.setRowCount(0);
        // selectedInvoice = null;
        btnPrint.setEnabled(false);

        List<HoaDon> list = hoaDonController.getByKhachHang(maKH);
        for (HoaDon hd : list) {
            if (hd.getTrangThai().equals("Đã thanh toán")) {
                modelInvoice.addRow(new Object[] {
                        hd.getMaHoaDon(),
                        UIFactory.formatTimestamp(hd.getNgayTao()),
                        UIFactory.formatCurrency(hd.getTongTien())
                });
            }
        }
    }

    private void printInvoice() {
        int[] selectedRows = tblInvoice.getSelectedRows();
        if (selectedRows.length == 0 || selectedCustomer == null)
            return;

        List<model.DatPhong> aggregatedDatPhong = new java.util.ArrayList<>();
        List<view.common.InvoicePrinter.CompensationItem> aggregatedCompensation = new java.util.ArrayList<>();
        BigDecimal totalCompensationAmount = BigDecimal.ZERO;
        BigDecimal totalInvoiceAmount = BigDecimal.ZERO;
        StringBuilder invoiceIds = new StringBuilder();

        // Use the first invoice for basic info (date, etc.)
        int firstMaHD = (int) tblInvoice.getValueAt(selectedRows[0], 0);
        HoaDon firstHoaDon = hoaDonController.getById(firstMaHD);

        repository.DatPhongDao dpDao = new repository.DatPhongDao();
        repository.KiemTraPhongDao ktpDao = new repository.KiemTraPhongDao();
        repository.KiemTraChiTietDao ktctDao = new repository.KiemTraChiTietDao();
        repository.ThietBiPhongDao tbpDao = new repository.ThietBiPhongDao();
        repository.ThietBiDao tbDao = new repository.ThietBiDao();
        repository.PhongDao phongDao = new repository.PhongDao();
        java.util.Map<Integer, String> mapPhong = new java.util.HashMap<>();

        for (int i = 0; i < selectedRows.length; i++) {
            int maHD = (int) tblInvoice.getValueAt(selectedRows[i], 0);
            HoaDon hd = hoaDonController.getById(maHD);

            if (i > 0)
                invoiceIds.append(", ");
            invoiceIds.append(maHD);

            totalInvoiceAmount = totalInvoiceAmount.add(hd.getTongTien());

            // Aggregate DatPhong
            List<model.DatPhong> listDP = dpDao.getAll().stream()
                    .filter(dp -> dp.getMaHoaDon() == maHD)
                    .toList();
            aggregatedDatPhong.addAll(listDP);

            // Map Phong names
            for (model.DatPhong dp : listDP) {
                model.Phong p = phongDao.getById(dp.getMaPhong());
                if (p != null) {
                    mapPhong.put(dp.getMaPhong(), p.getSoPhong());
                }
            }

            // Aggregate Compensation
            model.KiemTraPhong ktp = ktpDao.getByMaHoaDon(maHD);
            if (ktp != null) {
                totalCompensationAmount = totalCompensationAmount.add(ktp.getTienBoiThuong());

                List<model.KiemTraChiTiet> listKtct = ktctDao.getAllByMaKiemTraPhong(ktp.getMaKiemTraPhong());
                boolean hasDetails = false;

                for (model.KiemTraChiTiet item : listKtct) {
                    if (item.getSoLuongBiHong() > 0) {
                        model.ThietBiPhong tbp = tbpDao.getById(item.getMaThietBiPhong());
                        if (tbp != null) {
                            model.ThietBi tb = tbDao.getById(tbp.getMaThietBi());
                            if (tb != null) {
                                BigDecimal price = tb.getGiaThietBi();
                                BigDecimal total = price.multiply(new BigDecimal(item.getSoLuongBiHong()));
                                aggregatedCompensation.add(new view.common.InvoicePrinter.CompensationItem(
                                        tb.getTenThietBi(),
                                        item.getSoLuongBiHong(),
                                        price,
                                        total));
                                hasDetails = true;
                            }
                        }
                    }
                }

                // Fallback for this specific invoice if no details but has amount
                if (!hasDetails && ktp.getTienBoiThuong().compareTo(BigDecimal.ZERO) > 0) {
                    aggregatedCompensation.add(new view.common.InvoicePrinter.CompensationItem(
                            "Phụ thu (HĐ " + maHD + ")",
                            1,
                            ktp.getTienBoiThuong(),
                            ktp.getTienBoiThuong()));
                }
            }
        }

        // Create Dummy Objects for Printer
        HoaDon dummyHoaDon = new HoaDon();
        dummyHoaDon.setMaHoaDon(firstHoaDon.getMaHoaDon()); // Will be overridden by display string
        dummyHoaDon.setNgayTao(firstHoaDon.getNgayTao()); // Use date of first invoice
        dummyHoaDon.setTongTien(totalInvoiceAmount);

        model.KiemTraPhong dummyKtp = new model.KiemTraPhong();
        dummyKtp.setTienBoiThuong(totalCompensationAmount);

        // Show Preview Dialog
        InvoicePrinter printer = new InvoicePrinter(dummyHoaDon, selectedCustomer, aggregatedDatPhong, dummyKtp,
                aggregatedCompensation, mapPhong);
        printer.setInvoiceIdDisplay(invoiceIds.toString());

        InvoicePreviewDialog previewDialog = new InvoicePreviewDialog(
                SwingUtilities.getWindowAncestor(this), printer);
        previewDialog.setVisible(true);
    }
}
