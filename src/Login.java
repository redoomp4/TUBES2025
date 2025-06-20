import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;

public class Login extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JLabel logoLabel;
    private JPanel formPanel;
    private int loggedInUserId = -1;

    protected String user_name;
    protected String user_email;
    protected String user_pass; 

    public Login(JTextField emailField, JPasswordField passwordField) {
        this.emailField = emailField;
        this.passwordField = passwordField;
        this.user_email = emailField.getText();
        this.user_pass = new String(passwordField.getPassword());
    }

    public Login() {
        setTitle("SIPARKIR");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        BackgroundPanel background = new BackgroundPanel("image/desktop1.jpg");
        background.setLayout(null);
        setContentPane(background);

        File logoFile = new File("image/logo.png");
        if (logoFile.exists()) {
            ImageIcon rawLogo = new ImageIcon("image/logo.png");
            Image scaledLogo = rawLogo.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            logoLabel = new JLabel(new ImageIcon(scaledLogo));
        } else {
            System.out.println("Gambar logo.png tidak ditemukan.");
            logoLabel = new JLabel("LOGO");
        }
        background.add(logoLabel);

        formPanel = new JPanel(null);
        formPanel.setBackground(new Color(255, 255, 255, 200));
        background.add(formPanel);

        JLabel emailLabel = new JLabel("Email");
        emailField = new JTextField();
        JLabel passLabel = new JLabel("Password");
        passwordField = new JPasswordField();
        JButton loginBtn = new JButton("Login");
        JLabel registerText = new JLabel("Don't have an account?");
        JButton registerBtn = new JButton("Register");
        JLabel forgotLabel = new JLabel("<HTML><U>Forgot Password?</U></HTML>");

        forgotLabel.setForeground(Color.BLUE);
        forgotLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(passLabel);
        formPanel.add(passwordField);
        formPanel.add(loginBtn);
        formPanel.add(registerText);
        formPanel.add(registerBtn);
        formPanel.add(forgotLabel);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());

            ImageIcon warningIcon = new ImageIcon("image/warning.png");
            ImageIcon errorIcon2 = new ImageIcon("image/eror.png");
            ImageIcon successIcon = new ImageIcon("image/berhasil.png");
            ImageIcon errorIcon = new ImageIcon("image/eror2.png");

            if (email.isEmpty() || password.isEmpty()) {
                popup_window(warningIcon, 300, 80);
                return;
            }

            if (!email.contains("@")) {
                popup_window(errorIcon2, 300, 70);
                emailField.setText("");
                passwordField.setText("");
                passwordField.requestFocus();
                return;
            }

            if (getUser(email, password)) {
                popup_window(successIcon, 300, 70);

                Timer delay = new Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        ((Timer) evt.getSource()).stop();

                        dispose();

                        SwingUtilities.invokeLater(() -> {
                            Booking bookingForm = new Booking(loggedInUserId);
                            bookingForm.setExtendedState(JFrame.MAXIMIZED_BOTH);
                            bookingForm.setVisible(true);
                        });
                    }
                });
                delay.setRepeats(false);
                delay.start();

            } else {
                popup_window(errorIcon, 300, 70);
                passwordField.setText("");
                emailField.setText("");
                passwordField.requestFocus();
            }
        });

        registerBtn.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> {
                Register registerApp = new Register();
                registerApp.setExtendedState(JFrame.MAXIMIZED_BOTH);
                registerApp.setVisible(true);
            });
        });

        forgotLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                SwingUtilities.invokeLater(() -> {
                    ForgotPasswordApp forgotApp = new ForgotPasswordApp();
                    forgotApp.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    forgotApp.setVisible(true);
                });
            }
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                logoLabel.setBounds(w / 2 - 60, 20, 120, 120);
                formPanel.setBounds(w / 2 - 150, 160, 300, 250);

                emailLabel.setBounds(30, 10, 240, 20);
                emailField.setBounds(30, 30, 240, 25);

                passLabel.setBounds(30, 65, 240, 20);
                passwordField.setBounds(30, 85, 240, 25);

                loginBtn.setBounds(30, 125, 240, 30);
                forgotLabel.setBounds(30, 165, 240, 20);

                registerText.setBounds(30, 195, 150, 20);
                registerBtn.setBounds(180, 190, 90, 30);
            }
        });

        dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
        setVisible(true);
    }

    private boolean getUser(String email, String password) {
        String url = "jdbc:sqlite:database/App.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            Statement pragmaStmt = conn.createStatement();
            pragmaStmt.execute("PRAGMA foreign_keys = ON;");

            String sql = "SELECT * FROM User WHERE email = ? AND user_pw = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                loggedInUserId = rs.getInt("user_id");
                UserSession.setUserId(loggedInUserId);
                rs.close();
                pstmt.close();
                pragmaStmt.close();
                return true;
            }

            rs.close();
            pstmt.close();
            pragmaStmt.close();
            return false;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Kesalahan koneksi ke database:\n" + e.getMessage(), "Error DB",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }
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

            @Override
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Login::new);
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                backgroundImage = new ImageIcon(imagePath).getImage();
                System.out.println("Background image loaded successfully.");
            } else {
                System.out.println("Gagal memuat gambar latar: " + imagePath);
            }
            setLayout(null);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    class UserSession {
        private static int userId;

        public static void setUserId(int id) {
            userId = id;
        }

        public static int getUserId() {
            return userId;
        }
    }

}
