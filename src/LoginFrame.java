// File: src/LoginFrame.java
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.sql.*;

public class LoginFrame extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    // Color Palette - Library Theme
    static final Color BG_DARK       = new Color(18, 32, 47);
    static final Color BG_CARD       = new Color(26, 46, 68);
    static final Color ACCENT_GOLD   = new Color(212, 175, 55);
    static final Color ACCENT_LIGHT  = new Color(100, 160, 220);
    static final Color TEXT_WHITE    = new Color(240, 240, 245);
    static final Color TEXT_GRAY     = new Color(160, 175, 190);
    static final Color BTN_HOVER     = new Color(180, 145, 30);

    public LoginFrame() {
        setTitle("Library Management System");
        setSize(480, 580);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        initUI();
        setExtendedState(JFrame.MAXIMIZED_BOTH);
    }

    private void initUI() {
        // Main panel with dark background
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, BG_DARK, getWidth(), getHeight(), new Color(10, 20, 35));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        // ---- Book Icon / Logo Area ----
        JLabel logoLabel = new JLabel("📚", SwingConstants.CENTER);
        logoLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 60));
        logoLabel.setBounds(0, 30, 480, 80);
        mainPanel.add(logoLabel);

        JLabel titleLabel = new JLabel("Library Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Georgia", Font.BOLD, 24));
        titleLabel.setForeground(ACCENT_GOLD);
        titleLabel.setBounds(0, 110, 480, 35);
        mainPanel.add(titleLabel);

        JLabel subTitle = new JLabel("Sign in to continue", SwingConstants.CENTER);
        subTitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subTitle.setForeground(TEXT_GRAY);
        subTitle.setBounds(0, 148, 480, 25);
        mainPanel.add(subTitle);

        // ---- Card Panel ----
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(BG_CARD);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setOpaque(false);
        card.setLayout(null);
        card.setBounds(50, 195, 380, 310);
        mainPanel.add(card);

        // Username label
        JLabel userLabel = new JLabel("Username");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        userLabel.setForeground(ACCENT_LIGHT);
        userLabel.setBounds(30, 25, 200, 20);
        card.add(userLabel);

        // Username field
        usernameField = new JTextField();
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        usernameField.setForeground(TEXT_WHITE);
        usernameField.setBackground(new Color(35, 60, 85));
        usernameField.setCaretColor(ACCENT_GOLD);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 90, 120), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        usernameField.setBounds(30, 48, 320, 42);
        card.add(usernameField);

        // Password label
        JLabel passLabel = new JLabel("Password");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        passLabel.setForeground(ACCENT_LIGHT);
        passLabel.setBounds(30, 105, 200, 20);
        card.add(passLabel);

        // Password field
        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        passwordField.setForeground(TEXT_WHITE);
        passwordField.setBackground(new Color(35, 60, 85));
        passwordField.setCaretColor(ACCENT_GOLD);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(60, 90, 120), 1, true),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        passwordField.setBounds(30, 128, 320, 42);
        card.add(passwordField);

        // Login Button
        loginButton = new JButton("LOGIN") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(BTN_HOVER);
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(230, 195, 75));
                } else {
                    g2d.setColor(ACCENT_GOLD);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.setColor(BG_DARK);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g2d.drawString(getText(), x, y);
            }
        };
        loginButton.setContentAreaFilled(false);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.setBounds(30, 195, 320, 45);
        card.add(loginButton);

        // Status label
        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        statusLabel.setForeground(new Color(220, 80, 80));
        statusLabel.setBounds(30, 248, 320, 25);
        card.add(statusLabel);

        // Footer
        JLabel footer = new JLabel("© 2024 Library Management System", SwingConstants.CENTER);
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        footer.setForeground(TEXT_GRAY);
        footer.setBounds(0, 520, 480, 25);
        mainPanel.add(footer);

        // ---- Hint Panel ----
        JLabel hintLabel = new JLabel("<html><center><font color='#a0afbe'>Demo: admin/admin123 | librarian/lib123 | student1/stu123</font></center></html>", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        hintLabel.setBounds(20, 510, 440, 30);
        mainPanel.add(hintLabel);

        add(mainPanel);

        // Enter key login
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) loginButton.doClick();
            }
        });

        loginButton.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter username and password!");
            return;
        }

        loginButton.setEnabled(false);
        statusLabel.setForeground(ACCENT_LIGHT);
        statusLabel.setText("Authenticating...");

        SwingWorker<String[], Void> worker = new SwingWorker<>() {
            protected String[] doInBackground() {
                return authenticateUser(username, password);
            }
            protected void done() {
                try {
                    String[] result = get();
                    if (result != null) {
                        statusLabel.setForeground(new Color(80, 200, 120));
                        statusLabel.setText("Login successful! Opening dashboard...");
                        Timer timer = new Timer(800, ev -> {
                            openDashboard(result[0], result[1], username);
                            dispose();
                        });
                        timer.setRepeats(false);
                        timer.start();
                    } else {
                        statusLabel.setForeground(new Color(220, 80, 80));
                        statusLabel.setText("Invalid username or password!");
                        loginButton.setEnabled(true);
                    }
                } catch (Exception ex) {
                    statusLabel.setText("Connection error. Check XAMPP MySQL.");
                    loginButton.setEnabled(true);
                }
            }
        };
        worker.execute();
    }

    private String[] authenticateUser(String username, String password) {
        try (Connection conn = DBConnection.getConnection()) {
            if (conn == null) return null;
            String sql = "SELECT role, full_name, member_type FROM users WHERE username=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String role = rs.getString("role");
                String name = rs.getString("full_name");
                String memberType = rs.getString("member_type");
                return new String[]{role, name, memberType != null ? memberType : ""};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openDashboard(String role, String name, String username) {
        switch (role) {
            case "admin":
    new AdminDashboard(name).setVisible(true);
    break;
case "librarian":
    new LibrarianDashboard(name).setVisible(true);
    break;
case "member":
    new MemberDashboard(name, username).setVisible(true);
    break;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}