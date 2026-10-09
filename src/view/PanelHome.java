package view;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.Map;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.data.category.DefaultCategoryDataset;

import controller.KhachHangController;
import controller.NhanVienController;
import controller.PhongController;
import controller.ThietBiController;
import controller.ThongKeController;
import model.TaiKhoan;

public class PanelHome extends JPanel {

    private ThongKeController controller = new ThongKeController();
    private KhachHangController khController = new KhachHangController();
    private PhongController phongController = new PhongController();
    private NhanVienController NhanVienController = new NhanVienController();
    private ThietBiController tbController = new ThietBiController();

    private FrmMain frmMain;
    private TaiKhoan userLogin;

    // Font chuẩn
    private final Font fontTitle = new Font("Segoe UI", Font.BOLD, 20);
    private final Font fontCardTitle = new Font("Segoe UI", Font.BOLD, 20);
    private final Font fontCardDesc = new Font("Segoe UI", Font.PLAIN, 16);
    private final Font fontCardIcon = new Font("Segoe UI", Font.PLAIN, 24);

    // Font cho biểu đồ
    private final Font fontChartTitle = new Font("Segoe UI", Font.BOLD, 16);
    private final Font fontAxisLabel = new Font("Segoe UI", Font.BOLD, 13);
    private final Font fontTickLabel = new Font("Segoe UI", Font.PLAIN, 12);
    private final Font fontLegend = new Font("Segoe UI", Font.PLAIN, 12);

    // Màu sắc viền
    private final Color colorBlue = new Color(100, 100, 255);
    private final Color colorRed = new Color(255, 10, 10);
    private final Color colorGreen = new Color(46, 204, 113);
    private final Color colorShadow = new Color(200, 200, 200);

    // =================================================================
    // 1. GRID TỔNG
    // =================================================================
    // UPDATE: Constructor nhận FrmMain để điều hướng
    public PanelHome(FrmMain frmMain, TaiKhoan userLogin) {
        this.frmMain = frmMain;
        this.userLogin = userLogin;

        setLayout(new GridBagLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(30, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);

        // --- HÀNG 1: TIÊU ĐỀ ---
        RoundedPanel pnlTitleGroup = new RoundedPanel(20);
        pnlTitleGroup.setLayout(new BorderLayout());
        pnlTitleGroup.setBackground(Color.WHITE);
        pnlTitleGroup.setBorder(new EmptyBorder(15, 40, 15, 25));

        JLabel lblTitle = new JLabel("DASHBOARD - TỔNG QUAN HỆ THỐNG");
        lblTitle.setFont(fontTitle);
        lblTitle.setForeground(new Color(33, 115, 70));
        lblTitle.setHorizontalAlignment(SwingConstants.LEFT);
        pnlTitleGroup.add(lblTitle, BorderLayout.CENTER);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.05;
        add(pnlTitleGroup, gbc);

        // --- HÀNG 2: NỘI DUNG CHÍNH ---

        // CỘT TRÁI: CHỨA CÁC NÚT
        JPanel pnlLeftColumn = createLeftColumnWithGroups();

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 0.95;
        add(pnlLeftColumn, gbc);

        // CỘT PHẢI: CHỨA BIỂU ĐỒ
        JPanel pnlCharts = createChartGrid();

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.4;
        gbc.weighty = 0.95;
        add(pnlCharts, gbc);

        // --- HÀNG 3: FOOTER ---
        RoundedPanel pnlFooter = new RoundedPanel(20);
        pnlFooter.setBackground(Color.WHITE);
        pnlFooter.setPreferredSize(new Dimension(0, 20));

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.05;
        add(pnlFooter, gbc);
    }

    // ==============================================================
    // TẠO CẤU TRÚC CỘT TRÁI (CHỨA 2 NHÓM NÚT)
    // ==============================================================
    private JPanel createLeftColumnWithGroups() {
        JPanel container = new JPanel(new GridBagLayout());
        container.setOpaque(false);
        container.setPreferredSize(new Dimension(0, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 20, 0);
        gbc.gridx = 0;
        gbc.weightx = 1.0;

        int x = 20;
        // --- NHÓM TRÊN (Group 1): Đặt phòng & Trả phòng ---
        RoundedPanel pnlTopGroup = new RoundedPanel(40);
        pnlTopGroup.setBackground(Color.WHITE);
        pnlTopGroup.setLayout(new GridLayout(1, 2, 40, 40)); // 1 hàng, 2 cột
        pnlTopGroup.setBorder(new EmptyBorder(x, x, x, x));

        // Nút 1: Đặt phòng (Xanh dương)
        pnlTopGroup.add(createDashboardCard(
                "Đặt phòng",
                "Số phòng trống: " + phongController.countPhongTrong(),
                colorBlue));

        // Nút 2: Trả phòng (Đỏ) -> Chuyển đến Hóa đơn
        pnlTopGroup.add(createDashboardCard(
                "Phòng",
                "Đang sử dụng: " + phongController.countPhongDangSuDung(),
                colorRed));

        gbc.gridy = 0;
        gbc.weighty = 0.33;
        container.add(pnlTopGroup, gbc);

        // --- NHÓM DƯỚI (Group 2): 4 Nút còn lại (Xanh lá) ---
        gbc.insets = new Insets(0, 0, 0, 0);
        RoundedPanel pnlBottomGroup = new RoundedPanel(40);
        pnlBottomGroup.setBackground(Color.WHITE);
        pnlBottomGroup.setLayout(new GridLayout(2, 2, 40, 40)); // 2 hàng, 2 cột
        pnlBottomGroup.setBorder(new EmptyBorder(x, x, x, x));

        // Nút 3: Nhân viên
        pnlBottomGroup.add(createDashboardCard(
                "Nhân viên",
                "Tổng nhân viên: " + NhanVienController.countNhanVien(),
                colorGreen));
        // Nút 4: Khách hàng
        pnlBottomGroup.add(createDashboardCard(
                "Khách hàng",
                "Tổng khách hàng: " + khController.countKhachHang(),
                colorGreen));
        // Nút 5: Thiết bị
        pnlBottomGroup.add(createDashboardCard(
                "Thiết bị",
                "Loại thiết bị: " + tbController.countThietBi(),
                colorGreen));
        // Thêm hiển thị doanh thu tháng hiện tại (Yêu cầu mới: Chỉ hiện tháng hiện tại,
        // đẹp mắt)
        // 1. Lấy tháng hiện tại (Key dùng để tra cứu trong Map: yyyy-MM)
        String currentMonthKey = java.time.YearMonth.now().format(
                java.time.format.DateTimeFormatter.ofPattern("yyyy-MM"));

        // String hiển thị cho người dùng: MM/yyyy
        String displayMonth = java.time.YearMonth.now().format(
                java.time.format.DateTimeFormatter.ofPattern("MM/yyyy"));

        // 2. Lấy dữ liệu doanh thu
        Map<String, Double> revenueMap = new repository.ThongKeDao().getDoanhThuTheoThang();
        Double currentRevenue = revenueMap.getOrDefault(currentMonthKey, 0.0);
        String formattedRevenue = new DecimalFormat("#,###").format(currentRevenue);

        // 3. Tạo Card hiển thị (Sử dụng HTML để format đẹp)

        JLabel lblRevenue = new JLabel();
        lblRevenue.setText("<html>"
                + "<div style='text-align: left;'>"
                + "<span style='font-size:14px; color:#7f8c8d;'>Doanh thu tháng " + displayMonth + "</span><br>"
                + "<span style='font-size:16px; color:#27ae60; font-weight:bold;'>" + formattedRevenue
                + " <span style='font-size:14px'>VNĐ</span></span>"
                + "</div>"
                + "</html>");

        // Panel chứa nội dung
        RoundedCard revenueCard = new RoundedCard(20, colorGreen);
        revenueCard.setLayout(new BorderLayout());
        revenueCard.setBorder(new EmptyBorder(15, 20, 15, 20));
        revenueCard.add(lblRevenue, BorderLayout.CENTER);

        // Thêm icon bên phải cho đẹp
        JLabel lblIconRight = new JLabel("");
        revenueCard.add(lblIconRight, BorderLayout.EAST);

        // 4. Thêm vào nhóm dưới cùng
        pnlBottomGroup.add(revenueCard);

        gbc.gridy = 1;
        gbc.weighty = 0.67;
        container.add(pnlBottomGroup, gbc);

        return container;
    }

    // ==============================================================
    // HÀM TẠO NÚT (CARD) MỚI CÓ SỰ KIỆN CLICK
    // ==============================================================
    private JPanel createDashboardCard(String title, String description, Color borderColor) {
        RoundedCard panel = new RoundedCard(20, borderColor);
        panel.setLayout(new BorderLayout());
        // Padding bên trong nút
        panel.setBorder(new EmptyBorder(25, 20, 15, 20));

        // Panel chứa Text (Title + Description)
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(fontCardTitle);
        lblTitle.setForeground(borderColor);
        lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel(description);
        lblDesc.setFont(fontCardDesc);
        lblDesc.setForeground(borderColor.darker());
        lblDesc.setAlignmentX(Component.LEFT_ALIGNMENT);

        textPanel.add(lblTitle);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(lblDesc);

        // Icon bên phải
        JLabel lblIcon = new JLabel("");
        lblIcon.setFont(fontCardIcon);
        lblIcon.setForeground(borderColor);

        panel.add(textPanel, BorderLayout.CENTER);
        panel.add(lblIcon, BorderLayout.EAST);

        // --- XỬ LÝ SỰ KIỆN CLICK VÀ HOVER ---
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent evt) {
                panel.setHover(true);
                panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                panel.repaint();
            }

            @Override
            public void mouseExited(MouseEvent evt) {
                panel.setHover(false);
                panel.repaint();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Logic điều hướng
                if (frmMain != null) {
                    switch (title) {
                        case "Đặt phòng":
                            frmMain.loadPanel(new PanelDatPhong(userLogin));
                            break;
                        case "Trả phòng":
                            frmMain.loadPanel(new PanelKiemTraPhong());
                            break;
                        case "Nhân viên":
                            frmMain.loadPanel(new PanelNhanVien());
                            break;
                        case "Khách hàng":
                            frmMain.loadPanel(new PanelKhachHang());
                            break;
                        case "Thiết bị":
                            frmMain.loadPanel(new PanelThietBi());
                            break;
                        case "Doanh thu":
                            break;
                    }
                }
            }
        });

        return panel;
    }

    // ==============================================================
    // CLASS CUSTOM: ROUNDED CARD (NÚT 2 VIỀN)
    // ==============================================================
    class RoundedCard extends JPanel {
        private int radius;
        private Color innerBorderColor;
        private boolean isHover = false;
        private final Color bgDefault = new Color(255, 255, 255);
        private final Color bgHover = new Color(250, 250, 250);

        public RoundedCard(int radius, Color innerBorderColor) {
            this.radius = radius;
            this.innerBorderColor = innerBorderColor;
            setOpaque(false);
        }

        public void setHover(boolean hover) {
            this.isHover = hover;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();

            // 1. Vẽ nền
            g2.setColor(isHover ? bgHover : bgDefault);
            g2.fillRoundRect(0, 0, width, height, radius, radius);

            // 2. Vẽ viền ngoài (Màu xám - Shadow effect giả lập)
            g2.setStroke(new BasicStroke(1));
            g2.setColor(colorShadow);
            g2.drawRoundRect(0, 0, width - 1, height - 1, radius, radius);

            // 3. Vẽ viền trong (Màu theo loại nút) - Cách lề 1px
            g2.setColor(innerBorderColor);
            g2.drawRoundRect(1, 1, width - 3, height - 3, radius - 1, radius - 1);

            g2.dispose();
        }
    }

    // ==============================================================
    // CLASS CUSTOM: ROUNDED PANEL (CONTAINER)
    // ==============================================================
    class RoundedPanel extends JPanel {
        private int cornerRadius = 15;

        public RoundedPanel(int radius) {
            this.cornerRadius = radius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    // ==============================================================
    // GRID BIỂU ĐỒ
    // ==============================================================
    private JPanel createChartGrid() {
        RoundedPanel panel = new RoundedPanel(20);
        panel.setLayout(new GridLayout(2, 1, 0, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setPreferredSize(new Dimension(0, 0));

        Map<String, Integer> datPhongMap = controller.thongKeDatPhongTheoThang();
        Map<String, Double> doanhThuMap = controller.thongKeDoanhThuTheoThang();

        JFreeChart chartDatPhong = taoBieuDoDatPhong(datPhongMap);
        JFreeChart chartDoanhThu = taoBieuDoDoanhThu(doanhThuMap);

        ChartPanel pChart1 = new ChartPanel(chartDatPhong);
        ChartPanel pChart2 = new ChartPanel(chartDoanhThu);

        pChart1.setBorder(null);
        pChart2.setBorder(null);
        pChart1.setBackground(Color.WHITE);
        pChart2.setBackground(Color.WHITE);
        pChart1.setOpaque(false);
        pChart2.setOpaque(false);

        panel.add(pChart1);
        panel.add(pChart2);

        return panel;
    }

    // ==============================================================
    // LOGIC CHART THEME
    // ==============================================================
    private void applyChartTheme(JFreeChart chart) {
        chart.getTitle().setFont(fontChartTitle);
        chart.setBackgroundPaint(Color.WHITE);

        if (chart.getLegend() != null) {
            chart.getLegend().setItemFont(fontLegend);
            chart.getLegend().setBackgroundPaint(Color.WHITE);
        }

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(new Color(248, 248, 248));
        plot.setOutlineVisible(false);

        plot.getDomainAxis().setLabelFont(fontAxisLabel);
        plot.getRangeAxis().setLabelFont(fontAxisLabel);
        plot.getDomainAxis().setTickLabelFont(fontTickLabel);
        plot.getRangeAxis().setTickLabelFont(fontTickLabel);
    }

    private JFreeChart taoBieuDoDatPhong(Map<String, Integer> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((thang, soLuong) -> {
            dataset.addValue(soLuong, "Lượt đặt", thang);
        });

        JFreeChart chart = ChartFactory.createBarChart(
                "Tần Suất Đặt Phòng", "Tháng", "Số lượng",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.getDomainAxis().setLowerMargin(0.05);
        plot.getDomainAxis().setUpperMargin(0.05);
        plot.getRangeAxis().setUpperMargin(0.15);

        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setBarPainter(new StandardBarPainter());
        renderer.setSeriesPaint(0, new Color(52, 152, 219));
        renderer.setMaximumBarWidth(0.25);

        applyChartTheme(chart);
        return chart;
    }

    private JFreeChart taoBieuDoDoanhThu(Map<String, Double> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((thang, doanhThu) -> {
            dataset.addValue(doanhThu / 1_000_000, "Triệu đồng", thang); // ⭐ chuyển sang triệu
        });

        JFreeChart chart = ChartFactory.createLineChart(
                "Doanh Thu Theo Tháng",
                "Tháng",
                "Triệu đồng (tr)", // ⭐ đổi đơn vị trục Y
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setRangeGridlinePaint(new Color(220, 220, 220));
        plot.getRangeAxis().setUpperMargin(0.15);

        // ⭐ format số trên trục Y cho đẹp
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setNumberFormatOverride(new DecimalFormat("#,###"));

        applyChartTheme(chart);
        return chart;
    }

}