package view.common;

import model.DatPhong;
import model.HoaDon;
import model.KhachHang;
import model.KiemTraPhong;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.view.JasperViewer;

import javax.swing.*;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceReportManager {

    public static void printInvoice(HoaDon hd, KhachHang kh, List<DatPhong> listDatPhong, KiemTraPhong ktp) {
        try {
            // 1. Load Template
            InputStream reportStream = InvoiceReportManager.class
                    .getResourceAsStream("/resources/invoice_template.jrxml");
            if (reportStream == null) {
                JOptionPane.showMessageDialog(null, "Không tìm thấy file template hóa đơn!");
                return;
            }

            // 2. Compile Report
            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

            // 3. Prepare Parameters
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("InvoiceID", hd.getMaHoaDon());
            parameters.put("CreatedDate", sdf.format(hd.getNgayTao()));
            parameters.put("CustomerName", kh.getTenKhachHang());
            parameters.put("CustomerPhone", kh.getSoDienThoai());
            parameters.put("TotalAmount", UIFactory.formatCurrency(hd.getTongTien()));

            BigDecimal surcharge = (ktp != null) ? ktp.getTienBoiThuong() : BigDecimal.ZERO;
            parameters.put("Surcharge", UIFactory.formatCurrency(surcharge));
            parameters.put("StaffName", "Admin"); // Placeholder, ideally fetch from session

            // 4. Prepare Data Source
            List<RoomData> roomDataList = new ArrayList<>();
            for (DatPhong dp : listDatPhong) {
                roomDataList.add(new RoomData(
                        String.valueOf(dp.getMaPhong()),
                        sdf.format(dp.getNgayNhanPhong()),
                        (dp.getNgayTraPhong() != null) ? sdf.format(dp.getNgayTraPhong()) : "",
                        UIFactory.formatCurrency(dp.getTienPhong())));
            }
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(roomDataList);

            // 5. Fill Report
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            // 6. Show Viewer
            JasperViewer.viewReport(jasperPrint, false); // false = close viewer doesn't exit app

        } catch (JRException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Lỗi khi tạo báo cáo: " + e.getMessage());
        }
    }

    // Inner class for Bean Data Source
    public static class RoomData {
        private String roomNo;
        private String checkIn;
        private String checkOut;
        private String roomPrice;

        public RoomData(String roomNo, String checkIn, String checkOut, String roomPrice) {
            this.roomNo = roomNo;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.roomPrice = roomPrice;
        }

        public String getRoomNo() {
            return roomNo;
        }

        public String getCheckIn() {
            return checkIn;
        }

        public String getCheckOut() {
            return checkOut;
        }

        public String getRoomPrice() {
            return roomPrice;
        }
    }
}
