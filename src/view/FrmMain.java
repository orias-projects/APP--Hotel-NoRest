package view;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import model.TaiKhoan;

public class FrmMain extends JFrame {

    private JPanel contentPanel;
    private TaiKhoan userLogin;
    private List<JButton> menuButtons = new ArrayList<>();
    private JButton activeButton;
    
    // Màu sắc chủ đạo
    private Color defaultBackground = Color.WHITE;
    private Color activeBackground = new Color(33, 115, 70);
    private Color defaultForeground = new Color(33, 115, 70);
    private Color activeForeground = Color.WHITE;
    private Color hoverBackground = new Color(236, 240, 241);
    private Color logoutForeground = Color.RED;

    public FrmMain(TaiKhoan tk) {
        // Reset UI để tránh xung đột màu sắc
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.userLogin = tk;

        setTitle("Hệ Thống Quản Lý Khách Sạn");
        setSize(1240, 720);
        setMinimumSize(new Dimension(1240, 720));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== SIDEBAR =====
        JPanel menu = new JPanel();
        menu.setBackground(defaultBackground);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setPreferredSize(new Dimension(200, 700));
        menu.setMinimumSize(new Dimension(200, 0));
        menu.setMaximumSize(new Dimension(200, Integer.MAX_VALUE));
        
        menu.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 2, defaultForeground));

        JLabel lblUser = new JLabel("👤 " + tk.getTaiKhoan(), SwingConstants.LEFT);
        lblUser.setForeground(defaultForeground);
        lblUser.setFont(new Font("SansSerif", Font.BOLD, 17));
        lblUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblUser.getPreferredSize().height + 10));
        lblUser.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

        menu.add(Box.createVerticalStrut(10));
        menu.add(lblUser);
        menu.add(Box.createVerticalStrut(15));

        // ===== TẠO BUTTON =====
        JButton btnHome = menuButton("Trang chủ");
        JButton btnDatPhong = menuButton("Đặt phòng");
        JButton btnHoaDon = menuButton("Hóa đơn");
        JButton btnPhong = menuButton("Phòng");
        JButton btnLoaiPhong = menuButton("Loại phòng");
        JButton btnThietBi = menuButton("Thiết bị");
        JButton btnThietBiPhong = menuButton("Thiết bị phòng");
        JButton btnKiemTraPhong = menuButton("Kiểm tra phòng");
        JButton btnKiemTraChiTiet = menuButton("Chi tiết kiểm tra");
        JButton btnKhachHang = menuButton("Khách hàng");
        JButton btnNhanVien = menuButton("Nhân viên");
        JButton btnTaiKhoan = menuButton("Tài khoản");
        JButton btnVaiTro = menuButton("Vai trò");
        JButton btnThoat = menuButton("Đăng xuất");

        // ===== PHÂN QUYỀN =====
        if (tk.getMaVaiTro() == 1) {
            addToMenu(menu, btnHome);
            addToMenu(menu, btnDatPhong);
            addToMenu(menu, btnHoaDon);
            addToMenu(menu, btnPhong);
            addToMenu(menu, btnLoaiPhong);
            addToMenu(menu, btnThietBi);
            addToMenu(menu, btnThietBiPhong);
            addToMenu(menu, btnKiemTraPhong);
            addToMenu(menu, btnKiemTraChiTiet);
            addToMenu(menu, btnKhachHang);
            addToMenu(menu, btnNhanVien);
            addToMenu(menu, btnTaiKhoan);
            addToMenu(menu, btnVaiTro);
        } else if(tk.getMaVaiTro() == 3) {
            addToMenu(menu, btnDatPhong);
            addToMenu(menu, btnHoaDon);
            addToMenu(menu, btnPhong);
            addToMenu(menu, btnLoaiPhong);
            addToMenu(menu, btnThietBi);
            addToMenu(menu, btnThietBiPhong);
            addToMenu(menu, btnKiemTraPhong);
            addToMenu(menu, btnKiemTraChiTiet);
            addToMenu(menu, btnKhachHang);
        }else{
            addToMenu(menu, btnKiemTraPhong);
            addToMenu(menu, btnKiemTraChiTiet);
             addToMenu(menu, btnThietBi);
            addToMenu(menu, btnThietBiPhong);
        }

        menu.add(Box.createVerticalGlue());
        addToMenu(menu, btnThoat);

        add(menu, BorderLayout.WEST);

        // ===== CONTENT =====
        contentPanel = new JPanel(new BorderLayout());
        add(contentPanel, BorderLayout.CENTER);

        // ===== EVENTS =====
        btnHome.addActionListener(e -> {
            setActiveButton(btnHome);
            loadPanel(new PanelHome(this, userLogin));
        });
        // ... Các event khác giữ nguyên logic load panel ...
        btnKhachHang.addActionListener(e -> { setActiveButton(btnKhachHang); loadPanel(new PanelKhachHang()); });
        btnNhanVien.addActionListener(e -> { setActiveButton(btnNhanVien); loadPanel(new PanelNhanVien()); });
        btnLoaiPhong.addActionListener(e -> { setActiveButton(btnLoaiPhong); loadPanel(new PanelLoaiPhong()); });
        btnPhong.addActionListener(e -> { setActiveButton(btnPhong); loadPanel(new PanelPhong()); });
        btnTaiKhoan.addActionListener(e -> { setActiveButton(btnTaiKhoan); loadPanel(new PanelTaiKhoan()); });
        btnVaiTro.addActionListener(e -> { setActiveButton(btnVaiTro); loadPanel(new PanelVaiTro()); });
        btnDatPhong.addActionListener(e -> { setActiveButton(btnDatPhong); loadPanel(new PanelDatPhong(userLogin)); });
        btnKiemTraPhong.addActionListener(e -> { setActiveButton(btnKiemTraPhong); loadPanel(new PanelKiemTraPhong()); });
        btnThietBi.addActionListener(e -> { setActiveButton(btnThietBi); loadPanel(new PanelThietBi()); });
        btnThietBiPhong.addActionListener(e -> { setActiveButton(btnThietBiPhong); loadPanel(new PanelThietBiPhong()); });
        btnKiemTraChiTiet.addActionListener(e -> { setActiveButton(btnKiemTraChiTiet); loadPanel(new PanelKiemTraChiTiet(userLogin)); });
        btnHoaDon.addActionListener(e -> { setActiveButton(btnHoaDon); loadPanel(new PanelHoaDon()); });
        
        // XỬ LÝ ĐĂNG XUẤT VỚI DIALOG CUSTOM
        btnThoat.addActionListener(e -> confirmLogout());

        // Sau khi add menu & phân quyền
    if (tk.getMaVaiTro() == 1) {
        setActiveButton(btnHome);
        loadPanel(new PanelHome(this, userLogin));
    } 
    else if (tk.getMaVaiTro() == 3) {
        setActiveButton(btnDatPhong);
        loadPanel(new PanelDatPhong(userLogin));
    } 
    else { // Vai trò nhỏ nhất
        setActiveButton(btnKiemTraPhong);
        loadPanel(new PanelKiemTraPhong());
    }

    }

    // ==========================
    // CUSTOM LOGOUT DIALOG
    // ==========================
    private void confirmLogout() {
        // Sử dụng Custom Dialog thay vì JOptionPane
        CustomConfirmDialog dialog = new CustomConfirmDialog(
            this, 
            "Xác nhận đăng xuất", 
            "Bạn có chắc chắn muốn đăng xuất khỏi hệ thống?"
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            this.dispose();
            new FrmLogin().setVisible(true);
        }
    }

    // ==========================
    // HELPERS
    // ==========================
    public void loadPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
        String panelName = panel.getClass().getSimpleName();
        if (panel instanceof PanelHome) panelName = "Trang chủ";
        setTitle("Hệ Thống Quản Lý Khách Sạn - " + panelName);
    }

    private void addToMenu(JPanel menu, JButton btn) {
        menuButtons.add(btn);
        menu.add(btn);
    }

    private void setActiveButton(JButton btn) {
        for (JButton button : menuButtons) {
            button.setBackground(defaultBackground);
            button.setForeground(button.getText().equals("Đăng xuất") ? logoutForeground : defaultForeground);
        }
        btn.setBackground(activeBackground);
        btn.setForeground(activeForeground);
        activeButton = btn;
    }

    private JButton menuButton(String title) {
        JButton btn = new JButton(title);
        btn.setFocusPainted(false);
        btn.setBackground(defaultBackground);
        btn.setForeground(defaultForeground);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15)); // Update Font
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 10));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (btn != activeButton) btn.setBackground(hoverBackground);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (btn != activeButton) btn.setBackground(defaultBackground);
            }
        });
        if (title.equals("Đăng xuất")) {
            btn.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 10));
            btn.setForeground(logoutForeground);
        }
        return btn;
    }

    // =================================================================
    // INNER CLASS: CUSTOM CONFIRM DIALOG (MODERN UI)
    // =================================================================
    class CustomConfirmDialog extends JDialog {
        private boolean confirmed = false;
        private int xMouse, yMouse;

        public CustomConfirmDialog(JFrame parent, String title, String message) {
            super(parent, true); // Modal = true
            setUndecorated(true);
            setSize(400, 200);
            setLocationRelativeTo(parent);
            
            // Bo góc cho dialog
            setShape(new RoundRectangle2D.Double(0, 0, 400, 200, 0, 0));
            
            JPanel content = new JPanel(new BorderLayout());
            content.setBackground(Color.WHITE);
            // Viền xanh bao quanh
            content.setBorder(new LineBorder(activeBackground, 1)); 
            setContentPane(content);

            // --- HEADER (Tiêu đề + Nút đóng) ---
            JPanel pnlHeader = new JPanel(new BorderLayout());
            pnlHeader.setBackground(Color.WHITE);
            pnlHeader.setBorder(new EmptyBorder(10, 20, 10, 10));

            JLabel lblTitle = new JLabel(title);
            lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
            lblTitle.setForeground(activeBackground);

            // Kéo thả dialog
            pnlHeader.addMouseListener(new MouseAdapter() {
                public void mousePressed(MouseEvent evt) { xMouse = evt.getX(); yMouse = evt.getY(); }
            });
            pnlHeader.addMouseMotionListener(new MouseAdapter() {
                public void mouseDragged(MouseEvent evt) {
                    int x = evt.getXOnScreen();
                    int y = evt.getYOnScreen();
                    setLocation(x - xMouse, y - yMouse);
                }
            });

            pnlHeader.add(lblTitle, BorderLayout.CENTER);
            content.add(pnlHeader, BorderLayout.NORTH);

            // --- CENTER (Icon + Message) ---
            JPanel pnlCenter = new JPanel(new BorderLayout());
            pnlCenter.setBackground(Color.WHITE);
            pnlCenter.setBorder(new EmptyBorder(10, 20, 10, 20));

            // Icon dấu hỏi (?)
            JLabel lblIcon = new JLabel("?");
            lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 40));
            lblIcon.setForeground(activeBackground);
            lblIcon.setBorder(new EmptyBorder(0, 0, 0, 20));

            // Nội dung tin nhắn
            JTextArea txtMsg = new JTextArea(message);
            txtMsg.setFont(new Font("Segoe UI", Font.PLAIN, 15));
            txtMsg.setForeground(Color.DARK_GRAY);
            txtMsg.setWrapStyleWord(true);
            txtMsg.setLineWrap(true);
            txtMsg.setEditable(false);
            txtMsg.setFocusable(false);
            txtMsg.setBackground(Color.WHITE);

            pnlCenter.add(lblIcon, BorderLayout.WEST);
            pnlCenter.add(txtMsg, BorderLayout.CENTER);
            content.add(pnlCenter, BorderLayout.CENTER);

            // --- FOOTER (Buttons) ---
            JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 15));
            pnlButtons.setBackground(new Color(250, 250, 250));

            JButton btnCancel = createStyledButton("Hủy bỏ", new Color(200, 200, 200), Color.BLACK);
            JButton btnOk = createStyledButton("Đồng ý", activeBackground, Color.WHITE);

            btnCancel.addActionListener(e -> {
                confirmed = false;
                dispose();
            });
            btnOk.addActionListener(e -> {
                confirmed = true;
                dispose();
            });

            pnlButtons.add(btnCancel);
            pnlButtons.add(btnOk);
            content.add(pnlButtons, BorderLayout.SOUTH);
        }

        public boolean isConfirmed() {
            return confirmed;
        }

        private JButton createStyledButton(String text, Color bg, Color fg) {
            JButton btn = new JButton(text);
            btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btn.setForeground(fg);
            btn.setBackground(bg);
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setPreferredSize(new Dimension(100, 35));
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            return btn;
        }
    }
}