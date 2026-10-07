package view;

import javax.swing.*;
// import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import controller.TaiKhoanController;
import model.TaiKhoan;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class FrmLogin extends JFrame {

    private JTextField txtUser;
    private JPasswordField txtPass;
    private JButton btnLogin;
    private JButton btnExit;
    private TaiKhoanController controller = new TaiKhoanController();
    
    // Màu sắc chủ đạo (Lấy từ FrmMain)
    private final Color mainColor = new Color(33, 115, 70);
    private final Color hoverColor = new Color(28, 97, 59);
    private final Color bgColor = Color.WHITE;
    private final Color grayText = new Color(100, 100, 100);
    
    // Tọa độ để kéo thả cửa sổ
    private int xMouse, yMouse;

    public FrmLogin() {
        setTitle("Đăng nhập hệ thống");
        setSize(750, 450); // Kích thước rộng hơn theo tỉ lệ 16:9
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(1, 2)); // Chia đôi màn hình
        setUndecorated(true); // Bỏ thanh tiêu đề mặc định
        
        // Bo tròn góc cửa sổ (Nếu OS hỗ trợ)
        setShape(new RoundRectangle2D.Double(0, 0, 750, 450, 15, 15));

        // =================================================================
        // 1. PANEL TRÁI (MÀU XANH - BRANDING)
        // =================================================================
        JPanel pnlLeft = new JPanel();
        pnlLeft.setBackground(mainColor);
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        
        JLabel lblIcon = new JLabel("🏨");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        lblIcon.setForeground(Color.WHITE);
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblBrand = new JLabel("HOTEL MANAGER");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel lblSlogan = new JLabel("Quản lý chuyên nghiệp");
        lblSlogan.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblSlogan.setForeground(new Color(200, 230, 210));
        lblSlogan.setAlignmentX(Component.CENTER_ALIGNMENT);

        pnlLeft.add(Box.createVerticalGlue());
        pnlLeft.add(lblIcon);
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(lblBrand);
        pnlLeft.add(lblSlogan);
        pnlLeft.add(Box.createVerticalGlue());

        // =================================================================
        // 2. PANEL PHẢI (TRẮNG - FORM INPUT)
        // =================================================================
        JPanel pnlRight = new JPanel();
        pnlRight.setBackground(bgColor);
        pnlRight.setLayout(null); // Dùng Absolute Layout để dễ chỉnh vị trí chi tiết

        // --- Nút tắt (X) ---
        btnExit = new JButton("X");
        btnExit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnExit.setForeground(mainColor);
        btnExit.setBounds(330, 10, 40, 30);
        btnExit.setBorder(null);
        btnExit.setBackground(Color.WHITE);
        btnExit.setFocusPainted(false);
        btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExit.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { btnExit.setForeground(Color.RED); }
            public void mouseExited(MouseEvent e) { btnExit.setForeground(mainColor); }
        });
        btnExit.addActionListener(e -> System.exit(0));
        pnlRight.add(btnExit);

        // --- Tiêu đề Form ---
        JLabel lblTitle = new JLabel("ĐĂNG NHẬP");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(mainColor);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setBounds(0, 50, 375, 50);
        pnlRight.add(lblTitle);

        // --- Username ---
        JLabel lblUser = new JLabel("Tài khoản");
        lblUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblUser.setForeground(grayText);
        lblUser.setBounds(40, 120, 300, 20);
        pnlRight.add(lblUser);

        txtUser = new JTextField();
        txtUser.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtUser.setBounds(40, 145, 295, 30);
        txtUser.setBorder(new MatteBorder(0, 0, 2, 0, mainColor)); // Gạch chân
        txtUser.setBackground(bgColor);
        pnlRight.add(txtUser);

        // --- Password ---
        JLabel lblPass = new JLabel("Mật khẩu");
        lblPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblPass.setForeground(grayText);
        lblPass.setBounds(40, 200, 300, 20);
        pnlRight.add(lblPass);

        txtPass = new JPasswordField();
        txtPass.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtPass.setBounds(40, 225, 295, 30);
        txtPass.setBorder(new MatteBorder(0, 0, 2, 0, mainColor)); // Gạch chân
        txtPass.setBackground(bgColor);
        pnlRight.add(txtPass);

        // --- Button Login ---
        btnLogin = new RoundedButton("ĐĂNG NHẬP");
        btnLogin.setBounds(40, 290, 295, 45);
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        pnlRight.add(btnLogin);

        // Thêm vào Frame
        add(pnlLeft);
        add(pnlRight);

        // =================================================================
        // 3. EVENTS (LOGIC GIỮ NGUYÊN)
        // =================================================================
        
        // Kéo thả cửa sổ (Vì đã bỏ thanh tiêu đề mặc định)
        pnlLeft.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                xMouse = evt.getX();
                yMouse = evt.getY();
            }
        });
        pnlLeft.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                int x = evt.getXOnScreen();
                int y = evt.getYOnScreen();
                setLocation(x - xMouse, y - yMouse);
            }
        });
        // Cho phép kéo ở cả panel phải (trừ các component)
        pnlRight.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent evt) {
                xMouse = evt.getX() + 375; // Offset chiều rộng panel trái
                yMouse = evt.getY();
            }
        });
        pnlRight.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent evt) {
                int x = evt.getXOnScreen();
                int y = evt.getYOnScreen();
                setLocation(x - xMouse, y - yMouse);
            }
        });

        // Logic đăng nhập
        btnLogin.addActionListener(e -> doLogin());

        txtPass.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    doLogin();
            }
        });
    }

    // Hàm xử lý đăng nhập (Giữ nguyên logic cũ)
    private void doLogin() {
        String user = txtUser.getText().trim();
        String pass = String.valueOf(txtPass.getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        TaiKhoan tk = controller.login(user, pass);

        if (tk == null) {
            JOptionPane.showMessageDialog(this, "Sai tài khoản hoặc mật khẩu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            txtPass.setText("");
            txtPass.requestFocus();
            return;
        }

        // Mở FrmMain
        new FrmMain(tk).setVisible(true);
        this.dispose();
    }

    // =================================================================
    // INNER CLASS: ROUNDED BUTTON
    // =================================================================
    class RoundedButton extends JButton {
        public RoundedButton(String text) {
            super(text);
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setForeground(Color.WHITE);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (getModel().isPressed()) {
                g2.setColor(hoverColor.darker());
            } else if (getModel().isRollover()) {
                g2.setColor(hoverColor);
            } else {
                g2.setColor(mainColor);
            }

            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Bo tròn 30px
            super.paintComponent(g);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        // Set theme hệ thống nếu có
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) { e.printStackTrace(); }
        
        new FrmLogin().setVisible(true);
    }
}