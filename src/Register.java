import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class Register extends JFrame {
    private String user_name;
    private String user_email;
    private String user_pass;
    private int phoneNumber;

    private JTextField nameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JLabel logoLabel;
    private JPanel formPanel;
    private JButton registerBtn, backBtn;

    public Register(JTextField nameField, JTextField emailField, JPasswordField passwordField, JTextField phoneField) {
        this.user_name = nameField.getText();
        this.user_email = emailField.getText();
        this.user_pass = new String(passwordField.getPassword());
        this.phoneNumber = Integer.parseInt(phoneField.getText().trim());

    }

    public Register() {
        setTitle("SIPARKIR");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        BackgroundPanel background = new BackgroundPanel("image/desktop1.jpg");
        background.setLayout(null);
        setContentPane(background);

        ImageIcon rawLogo = new ImageIcon("image/logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        logoLabel = new JLabel(new ImageIcon(scaledLogo));
        background.add(logoLabel);

        formPanel = new JPanel(null);
        formPanel.setBackground(new Color(255, 255, 255, 200));
        background.add(formPanel);

        JLabel nameLabel = new JLabel("Name");
        nameField = new JTextField();
        JLabel emailLabel = new JLabel("Email");
        emailField = new JTextField();
        JLabel passLabel = new JLabel("Password");
        passwordField = new JPasswordField();
        JLabel phoneLabel = new JLabel("Phone Number");
        phoneField = new JTextField();

        registerBtn = new JButton("Register");
        backBtn = new JButton("Back to Login");

        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);
        formPanel.add(phoneLabel);
        formPanel.add(phoneField);
        formPanel.add(registerBtn);
        formPanel.add(backBtn);

        registerBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());
            String phoneStr = phoneField.getText().trim();

            ImageIcon warningIcon = new ImageIcon("image/warning.png");
            ImageIcon successIcon = new ImageIcon("image/berhasil_Register.png");
            ImageIcon errorIcon = new ImageIcon("image/eror.png");
            ImageIcon errorRegisterIcon = new ImageIcon("image/eror_diisi.png");

            if (name.isEmpty() || email.isEmpty() || password.isEmpty() || phoneStr.isEmpty()) {
                popup_window(warningIcon, 300, 90);
                return;
            }
            if (!email.contains("@")) {
                popup_window(errorIcon, 300, 70);
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                phoneField.setText("");
                return;
            }
            if (getUser(email, name)) {
                popup_window(errorRegisterIcon, 400, 100);
                emailField.setText("");
                nameField.setText("");
                emailField.requestFocus();
                return;
            }

            if (!phoneStr.matches("08\\d{8,11}")) {
                popup_window(errorIcon, 300, 70);
                phoneField.requestFocus();
                phoneField.setText("");
                return;
            }

            boolean success = createUser(name, email, password, phoneStr);
            if (success) {
                popup_window(successIcon, 300, 70);
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                phoneField.setText("");

                int width = getWidth();
                int height = getHeight();
                int x = getX();
                int y = getY();
                boolean isMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;

                Timer delay = new Timer(2000, evt -> {
                    ((Timer) evt.getSource()).stop();
                    dispose();
                    SwingUtilities.invokeLater(() -> {
                        Login loginApp = new Login();
                        loginApp.setSize(width, height);
                        loginApp.setLocation(x, y);
                        if (isMaximized)
                            loginApp.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        loginApp.setVisible(true);
                    });
                });
                delay.setRepeats(false);
                delay.start();
            } else {
                popup_window(warningIcon, 250, 70);
                nameField.setText("");
                emailField.setText("");
                passwordField.setText("");
                phoneField.setText("");
            }
        });

        backBtn.addActionListener(e -> {
            int width = getWidth();
            int height = getHeight();
            int x = getX();
            int y = getY();
            boolean isMaximized = (getExtendedState() & JFrame.MAXIMIZED_BOTH) == JFrame.MAXIMIZED_BOTH;

            dispose();
            SwingUtilities.invokeLater(() -> {
                Login login = new Login();
                login.setSize(width, height);
                login.setLocation(x, y);
                if (isMaximized)
                    login.setExtendedState(JFrame.MAXIMIZED_BOTH);
                login.setVisible(true);
            });
        });

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                logoLabel.setBounds(w / 2 - 50, 20, 100, 100);
                formPanel.setBounds(w / 2 - 150, 140, 300, 280);

                nameLabel.setBounds(30, 10, 240, 20);
                nameField.setBounds(30, 30, 240, 25);
                emailLabel.setBounds(30, 60, 240, 20);
                emailField.setBounds(30, 80, 240, 25);
                passLabel.setBounds(30, 110, 240, 20);
                passwordField.setBounds(30, 130, 240, 25);
                phoneLabel.setBounds(30, 160, 240, 20);
                phoneField.setBounds(30, 180, 240, 25);
                registerBtn.setBounds(75, 215, 150, 25);
                backBtn.setBounds(75, 250, 150, 25);
            }
        });

        dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
        setVisible(true);
    }

    private void popup_window(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImg);
        JWindow popup = new JWindow();
        JLabel imageLabel = new JLabel(scaledIcon);
        popup.getContentPane().add(imageLabel);
        popup.pack();

        int parentX = getX();
        int parentY = getY();
        int parentWidth = getWidth();
        int popupWidth = popup.getWidth();
        int popupHeight = popup.getHeight();
        int targetX = parentX + (parentWidth - popupWidth) / 2;
        int startY = parentY - popupHeight;
        int targetY = parentY + 30;

        popup.setLocation(targetX, startY);
        popup.setVisible(true);

        Timer slideTimer = new Timer(10, null);
        slideTimer.addActionListener(new ActionListener() {
            int currentY = startY;

            public void actionPerformed(ActionEvent e) {
                if (currentY < targetY) {
                    currentY += 4;
                    popup.setLocation(targetX, currentY);
                } else {
                    popup.setLocation(targetX, targetY);
                    slideTimer.stop();
                    new Timer(2000, ev -> {
                        popup.setVisible(false);
                        popup.dispose();
                    }).start();
                }
            }
        });
        slideTimer.start();
    }

    private boolean getUser(String email, String name) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database/App.db")) {
            Statement pragmaStmt = conn.createStatement();
            pragmaStmt.execute("PRAGMA foreign_keys = ON;");
            String sql = "SELECT COUNT(*) FROM user WHERE email = ? OR fullname = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, name);
            ResultSet rs = stmt.executeQuery();
            boolean exists = rs.next() && rs.getInt(1) > 0;
            rs.close();
            stmt.close();
            pragmaStmt.close();
            return exists;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean createUser(String name, String email, String password, String phone) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database/App.db")) {
            Statement pragmaStmt = conn.createStatement();
            pragmaStmt.execute("PRAGMA foreign_keys = ON;");
            String createTableSQL = "CREATE TABLE IF NOT EXISTS user (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "fullname TEXT NOT NULL," +
                    "email TEXT NOT NULL UNIQUE," +
                    "user_pw TEXT NOT NULL," +
                    "phoneNumber TEXT NOT NULL);";
            Statement stmt = conn.createStatement();
            stmt.execute(createTableSQL);
            String sql = "INSERT INTO user (fullname, email, user_pw, phoneNumber) VALUES (?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setString(4, phone);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE") || e.getMessage().contains("PRIMARY")) {
                JOptionPane.showMessageDialog(this, "Email sudah terdaftar!", "Gagal Registrasi",
                        JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Kesalahan database: " + e.getMessage(), "Gagal Registrasi",
                        JOptionPane.ERROR_MESSAGE);
            }
            return false;
        }
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.out.println("Gagal memuat gambar latar: " + imagePath);
            }
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Register::new);
    }
}
