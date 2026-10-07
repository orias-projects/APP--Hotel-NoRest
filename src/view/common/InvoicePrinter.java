package view.common;

import model.DatPhong;
import model.HoaDon;
import model.KhachHang;
import model.KiemTraPhong;

import java.awt.*;
import java.awt.print.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;

public class InvoicePrinter implements Printable {

    private HoaDon hoaDon;
    private KhachHang khachHang;
    private List<DatPhong> listDatPhong;
    // private KiemTraPhong kiemTraPhong;
    private List<CompensationItem> listCompensation;
    private java.util.Map<Integer, String> mapPhong; // Map maPhong -> soPhong
    private String invoiceIdDisplay; // Custom display for invoice ID (e.g. "101, 102")

    public static class CompensationItem {
        public String itemName;
        public int quantity;
        public BigDecimal price;
        public BigDecimal total;

        public CompensationItem(String itemName, int quantity, BigDecimal price, BigDecimal total) {
            this.itemName = itemName;
            this.quantity = quantity;
            this.price = price;
            this.total = total;
        }
    }

    public InvoicePrinter(HoaDon hoaDon, KhachHang khachHang, List<DatPhong> listDatPhong,
            KiemTraPhong kiemTraPhong, List<CompensationItem> listCompensation,
            java.util.Map<Integer, String> mapPhong) {
        this.hoaDon = hoaDon;
        this.khachHang = khachHang;
        this.listDatPhong = listDatPhong;
        // this.kiemTraPhong = kiemTraPhong;
        this.listCompensation = listCompensation;
        this.mapPhong = mapPhong;
        this.invoiceIdDisplay = String.valueOf(hoaDon.getMaHoaDon());
    }

    public void setInvoiceIdDisplay(String invoiceIdDisplay) {
        this.invoiceIdDisplay = invoiceIdDisplay;
    }

    @Override
    public int print(Graphics g, PageFormat pf, int page) throws PrinterException {
        if (page > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());

        // Define fonts
        Font fontTitle = new Font("Serif", Font.BOLD, 24);
        Font fontHeader = new Font("Serif", Font.BOLD, 14);
        Font fontRegular = new Font("Serif", Font.PLAIN, 12);
        Font fontBold = new Font("Serif", Font.BOLD, 12);
        Font fontItalic = new Font("Serif", Font.ITALIC, 12);

        int y = 50;
        int x = 25;
        int width = (int) pf.getImageableWidth() - 100;

        // 1. Header
        g2d.setFont(fontTitle);
        centerString(g2d, "HÓA ĐƠN THANH TOÁN", width, y);
        y += 30;

        g2d.setFont(fontRegular);
        centerString(g2d, "Khách sạn Nhóm 3", width, y);
        y += 40;

        // 2. Invoice Info
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        g2d.setFont(fontRegular);
        g2d.drawString("Mã hóa đơn: " + invoiceIdDisplay, x, y);
        g2d.drawString("Ngày tạo: " + sdf.format(hoaDon.getNgayTao()), x + 250, y);
        y += 20;
        g2d.drawString("Khách hàng: " + khachHang.getTenKhachHang(), x, y);
        g2d.drawString("SĐT: " + khachHang.getSoDienThoai(), x + 250, y);
        y += 30;

        // 3. Room Details Table
        g2d.setFont(fontHeader);
        g2d.drawString("CHI TIẾT PHÒNG", x, y);
        y += 20;

        // Table Header
        int[] colX = { x, x + 40, x + 130, x + 230, x + 330, x + 420};
        String[] headers = { "Phòng", "Nhận", "Trả", "Tiền phòng", "Tiền phạt" };

        g2d.setFont(fontBold);
        g2d.drawLine(x, y - 15, x + width + 50, y - 15);
        for (int i = 0; i < headers.length; i++) {
            g2d.drawString(headers[i], colX[i], y);
        }
        g2d.drawLine(x, y + 5, x + width + 50, y + 5);
        y += 20;

        // Table Content
        g2d.setFont(fontRegular);
        for (DatPhong dp : listDatPhong) {
            String tenPhong = mapPhong != null ? mapPhong.getOrDefault(dp.getMaPhong(), String.valueOf(dp.getMaPhong()))
                    : String.valueOf(dp.getMaPhong());
            g2d.drawString(tenPhong, colX[0], y);
            g2d.drawString(sdf.format(dp.getNgayNhanPhong()), colX[1], y);
            g2d.drawString(dp.getNgayTraPhong() != null ? sdf.format(dp.getNgayTraPhong()) : "", colX[2], y);
            g2d.drawString(UIFactory.formatCurrency(dp.getTienPhong()), colX[3], y);
            g2d.drawString(UIFactory.formatCurrency(dp.getTienPhat()), colX[4], y);
            y += 20;
        }
        y += 10;

        // 4. Compensation Details (if any)
        if (listCompensation != null && !listCompensation.isEmpty()) {
            g2d.setFont(fontHeader);
            g2d.drawString("CHI TIẾT BỒI THƯỜNG", x, y);
            y += 20;

            // Table Header
            int[] compColX = { x, x + 130, x + 230, x + 330 };
            String[] compHeaders = { "Thiết bị / Vật dụng", "SL", "Đơn giá", "Thành tiền" };

            g2d.setFont(fontBold);
            g2d.drawLine(x, y - 15, x + width + 50, y - 15);
            for (int i = 0; i < compHeaders.length; i++) {
                g2d.drawString(compHeaders[i], compColX[i], y);
            }
            g2d.drawLine(x, y + 5, x + width + 50, y + 5);
            y += 20;

            // Table Content
            g2d.setFont(fontRegular);
            for (CompensationItem item : listCompensation) {
                g2d.drawString(item.itemName, compColX[0], y);
                g2d.drawString(String.valueOf(item.quantity), compColX[1], y);
                g2d.drawString(UIFactory.formatCurrency(item.price), compColX[2], y);
                g2d.drawString(UIFactory.formatCurrency(item.total), compColX[3], y);
                y += 20;
            }
            y += 10;
        }

        // 5. Totals
        g2d.drawLine(x, y, x + width + 50, y);
        y += 20;

        int labelX = x;
        int valueX = x + 280;

        g2d.setFont(fontBold);

        // Calculate Subtotals
        BigDecimal subTotalRoom = BigDecimal.ZERO;
        BigDecimal subTotalPenalty = BigDecimal.ZERO;

        for (DatPhong dp : listDatPhong) {
            subTotalRoom = subTotalRoom.add(dp.getTienPhong());
            subTotalPenalty = subTotalPenalty.add(dp.getTienPhat());
        }

        g2d.setFont(new Font("Serif", Font.BOLD, 16));
        g2d.drawString("TIỀN:", labelX, y);
        g2d.drawString(UIFactory.formatCurrency(hoaDon.getTongTien()), valueX, y);
        y += 40;

        // 6. Footer
        g2d.setFont(fontItalic);
        centerString(g2d, "Cảm ơn quý khách và hẹn gặp lại!", width, y);

        return PAGE_EXISTS;
    }

    private void centerString(Graphics2D g2d, String s, int width, int y) {
        FontMetrics fm = g2d.getFontMetrics();
        int x = (width - fm.stringWidth(s)) / 2 + 50; // +50 for left margin
        g2d.drawString(s, x, y);
    }
}
