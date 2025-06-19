import java.awt.*;
import java.awt.event.*;
import java.net.URLEncoder;
import java.sql.*;
import java.util.Properties;
import javax.swing.*;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class ForgotPasswordApp extends JFrame {
    private JTextField emailField;
    private JLabel logoLabel;
    private JPanel formPanel;

    public ForgotPasswordApp() {
        setTitle("Forgot Password");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        BackgroundPanel background = new BackgroundPanel("image/desktop1.jpg");
        background.setLayout(null);
        setContentPane(background);

        ImageIcon rawLogo = new ImageIcon("image/logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        logoLabel = new JLabel(new ImageIcon(scaledLogo));
        background.add(logoLabel);

        formPanel = new JPanel(null);
        formPanel.setBackground(new Color(255, 255, 255, 200));
        background.add(formPanel);

        JLabel titleLabel = new JLabel("Find your Account");
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 18));

        JLabel emailLabel = new JLabel("Email");
        emailField = new JTextField();

        JButton submitBtn = new JButton("Submit");
        submitBtn.setBackground(Color.BLACK);
        submitBtn.setForeground(Color.WHITE);

        formPanel.add(titleLabel);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(submitBtn);

        submitBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            if (email.isEmpty() || !email.contains("@")) {
                JOptionPane.showMessageDialog(this, "Silakan masukkan email yang valid.");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database/App.db")) {
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM User WHERE email = ?");
                stmt.setString(1, email);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    String password = rs.getString("user_pw"); // ← ini diganti sesuai kolom database kamu
                    sendEmail(email, password);
                    ImageIcon PasswordIcon = new ImageIcon("image/success_pass.png");
                    popup_window(PasswordIcon, 300, 70);
                } else {
                    ImageIcon warningIcon = new ImageIcon("image/eror_email.png");
                    popup_window(warningIcon, 200, 70);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Terjadi kesalahan: " + ex.getMessage());
            }
        });
        JButton backBtn = new JButton("Back to login");
        backBtn.setBackground(Color.BLACK);
        backBtn.setForeground(Color.WHITE);
        backBtn.setBounds(30, 145, 240, 30);
        formPanel.add(backBtn);

        backBtn.addActionListener(e -> {
            Login loginApp = new Login();
            loginApp.setExtendedState(JFrame.MAXIMIZED_BOTH);
            loginApp.setVisible(true);
        });

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                logoLabel.setBounds(w / 2 - 60, 20, 120, 120);
                formPanel.setBounds(w / 2 - 150, 160, 300, 180);

                titleLabel.setBounds(30, 10, 300, 30);
                emailLabel.setBounds(30, 50, 240, 20);
                emailField.setBounds(30, 70, 240, 25);
                submitBtn.setBounds(30, 110, 240, 30);
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

    // Panel background gambar
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String imagePath) {
            try {
                backgroundImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
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

    // Kirim email reset password
    private void sendEmail(String Email, String Password) {
        String from = "no-reply@localhost";
        String host = "localhost";
        int port = 2500; // default MailSlurper

        Properties props = new Properties();
        props.put("mail.smtp.host", "localhost");
        props.put("mail.smtp.port", "2500");
        props.put("mail.smtp.auth", "false");
        props.put("mail.smtp.starttls.enable", "false");

        Session session = Session.getInstance(props, null);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(Email));
            message.setSubject("Reset Password Anda");

            String encodedEmail = URLEncoder.encode(Email, "UTF-8");
            String resetLink = "http://localhost:5580/src/ResetPassword.html?email=" + encodedEmail;

            String htmlContent = "<h2>Reset Password</h2>" +
                    "<p>Password kamu saat ini: <strong>" + Password + "</strong></p>" +
                    "<p>Klik tombol di bawah ini untuk mereset password Anda:</p>" +
                    "<a href='" + resetLink
                    + "' style='padding:10px 20px; background:#000; color:#fff; text-decoration:none;'>Reset Password</a>";

            message.setContent(htmlContent, "text/html");

            Transport.send(message);
            System.out.println("✅ Email berhasil dikirim ke: " + Email);
        } catch (Exception e) {
            System.err.println("❌ Gagal mengirim email:");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ForgotPasswordApp::new);
    }
}
