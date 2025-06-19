import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.*;

public class DetailStatus extends JFrame {
    private JLabel logoLabel;
    private JPanel formPanel;

    private JLabel labelKendala;
    private JTextArea textAreaKronologi;

    private int userId;

    private JButton backButton;
    private JScrollPane scrollPane;

    public DetailStatus(int user_id, int report_id) {
        this.userId = user_id;

        setTitle("Detail Status");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        BackgroundPanel background = new BackgroundPanel("image/desktop1.jpg");
        background.setLayout(null);
        setContentPane(background);

        // Logo
        ImageIcon rawLogo = new ImageIcon("image/logo.png");
        Image scaledLogo = rawLogo.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        logoLabel = new JLabel(new ImageIcon(scaledLogo));
        background.add(logoLabel);

        // Panel Transparan
        formPanel = new JPanel(null);
        formPanel.setBackground(new Color(255, 255, 255, 200));
        background.add(formPanel);

        // Judul
        JLabel titleLabel = new JLabel("Detail Status Laporan");
        titleLabel.setFont(new Font("Serif", Font.BOLD, 22));

        // Label Kendala
        labelKendala = new JLabel("Kendala: ");
        labelKendala.setFont(new Font("Arial", Font.PLAIN, 16));

        // TextArea Kronologi
        textAreaKronologi = new JTextArea();
        textAreaKronologi.setLineWrap(true);
        textAreaKronologi.setWrapStyleWord(true);
        textAreaKronologi.setEditable(false);
        scrollPane = new JScrollPane(textAreaKronologi);

        // Tombol Back
        backButton = new JButton("Kembali");
        backButton.setForeground(Color.BLACK); // Teks hitam
        backButton.setBackground(new Color(211, 211, 211)); // Light Gray
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.setFocusPainted(false);
        backButton.setFont(new Font("Arial", Font.BOLD, 18)); // Arial Bold ukuran 18

        // Action Tombol Back
        backButton.addActionListener(e -> {
            StatusLaporan statusLaporan = new StatusLaporan(userId);
            statusLaporan.setExtendedState(JFrame.MAXIMIZED_BOTH);
            statusLaporan.setVisible(true);
            dispose();
        });

        // Tambahkan ke panel
        formPanel.add(titleLabel);
        formPanel.add(labelKendala);
        formPanel.add(scrollPane);
        background.add(backButton);

        // Layout Responsif
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                int w = getWidth();

                logoLabel.setBounds(w / 2 - 60, 20, 120, 120);
                formPanel.setBounds(w / 2 - 300, 160, 600, 300);

                titleLabel.setBounds(30, 10, 400, 30);
                labelKendala.setBounds(30, 50, 550, 25);
                scrollPane.setBounds(30, 80, 540, 120);

                // Tombol Back di bawah formPanel, tengah, lebar 150px
                backButton.setBounds(w / 2 - 75, formPanel.getY() + formPanel.getHeight() + 20, 150, 40);
            }
        });

        // Load detail report
        getReport(report_id);

        dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
        setVisible(true);
    }

    private void getReport(int reportId) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database/App.db");
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Report WHERE report_id = ?")) {

            stmt.setInt(1, reportId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String kendala = rs.getString("kendala");
                String kronologi = rs.getString("kronologi");

                labelKendala.setText("Kendala: " + kendala);
                textAreaKronologi.setText(kronologi);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inner Class BackgroundPanel
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int userId = 3;
            UserSession.setUserId(userId);
            new StatusBooking(userId);
        });
    }

    static class UserSession {
        private static int userId;

        public static void setUserId(int id) {
            userId = id;
        }

        public static int getUserId() {
            return userId;
        }
    }
}
