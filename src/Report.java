
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Report extends JFrame {
    private String plat;
    private String tipeKendaraan;
    private String kendala;
    private String kronologi;

    public Report(String plat, String tipeKendaraan, String kendala, String kronologi) {
        this.plat = plat;
        this.tipeKendaraan = tipeKendaraan;
        this.kendala = kendala;
        this.kronologi = kronologi;
    }

    public Report(int user_id) {
        setTitle("Laporan");
        UserSession.setUserId(user_id);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

        // SIDEBAR (dibiarkan tetap)
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(44, 44, 44));
        sidebar.setLayout(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(370, 0));
        add(sidebar, BorderLayout.WEST);

        Font buttonFont = new Font("SansSerif", Font.BOLD, 14);
        Color buttonColor = new Color(217, 217, 217);

        JPanel topButtons = new JPanel();
        topButtons.setLayout(new BoxLayout(topButtons, BoxLayout.Y_AXIS));
        topButtons.setBackground(new Color(44, 44, 44));
        String[] topMenu = { "Booking", "Daftar pesanan" };
        for (String label : topMenu) {
            JButton btn = new JButton(label);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 45));
            btn.setBackground(buttonColor);
            btn.setFont(buttonFont);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            topButtons.add(Box.createRigidArea(new Dimension(0, 15)));
            topButtons.add(btn);
            if (label.equals("Booking")) {
                btn.addActionListener(e -> {
                    Booking bookingForm = new Booking(UserSession.getUserId());
                    bookingForm.setExtendedState(JFrame.MAXIMIZED_BOTH); // pastikan fullscreen
                    bookingForm.setVisible(true);
                    setVisible(false);
                });
            } else if (label.equals("Daftar pesanan")) {
                btn.addActionListener(e -> {
                    StatusBooking statusBooking = new StatusBooking(UserSession.getUserId());
                    statusBooking.setExtendedState(JFrame.MAXIMIZED_BOTH); // pastikan fullscreen
                    statusBooking.setVisible(true);
                    setVisible(false);
                });
            }
        }
        sidebar.add(topButtons, BorderLayout.NORTH);

        JPanel bottomButtons = new JPanel();
        bottomButtons.setLayout(new BoxLayout(bottomButtons, BoxLayout.Y_AXIS));
        bottomButtons.setBackground(new Color(44, 44, 44));
        String[] bottomMenu = { "Laporan", "Cek status laporan" };
        for (String label : bottomMenu) {
            JButton btn = new JButton(label);
            btn.setAlignmentX(Component.CENTER_ALIGNMENT);
            btn.setMaximumSize(new Dimension(200, 45));
            btn.setBackground(buttonColor);
            btn.setFont(buttonFont);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            bottomButtons.add(Box.createRigidArea(new Dimension(0, 15)));
            bottomButtons.add(btn);
            if (label.equals("Laporan")) {
            } else if (label.equals("Cek status laporan")) {
                btn.addActionListener(e -> {
                    StatusLaporan statusLaporan = new StatusLaporan(UserSession.getUserId());
                    statusLaporan.setExtendedState(JFrame.MAXIMIZED_BOTH); // pastikan fullscreen
                    statusLaporan.setVisible(true);
                    setVisible(false);
                });
            }
        }

        JButton logoutButton = new JButton("LOG OUT");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setMaximumSize(new Dimension(200, 45));
        logoutButton.setBackground(buttonColor);
        logoutButton.setFont(buttonFont);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logoutButton.setBorder(BorderFactory.createLineBorder(new Color(44, 44, 44), 10, true));
        bottomButtons.add(Box.createRigidArea(new Dimension(0, 25)));
        bottomButtons.add(logoutButton);
        bottomButtons.add(Box.createRigidArea(new Dimension(0, 30)));
        sidebar.add(bottomButtons, BorderLayout.SOUTH);
        logoutButton.addActionListener(e -> {
            Login loginApp = new Login();
            loginApp.setExtendedState(JFrame.MAXIMIZED_BOTH);
            loginApp.setVisible(true);
            setVisible(false);
        });

        // Tambahkan tombol logout ke panel (misalnya di bawah sidebar atau panel lain
        sidebar.add(bottomButtons, BorderLayout.SOUTH);

        // === MAIN CONTENT ===
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);

        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(230, 230, 230));
        titlePanel.setPreferredSize(new Dimension(1000, 60));
        JLabel titleLabel = new JLabel("LAPORAN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        mainPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        Font labelFont = new Font("SansSerif", Font.PLAIN, 18);
        Dimension fieldSize = new Dimension(500, 30);

        // ===== FIELD: Plat Nomor =====
        gbc.insets = new Insets(13, 10, 3, 3);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblPlat = new JLabel("Plat Nomor:");
        lblPlat.setFont(labelFont);
        formPanel.add(lblPlat, gbc);

        gbc.gridx = 1;
        JTextField platField = new JTextField();
        platField.setPreferredSize(fieldSize);
        platField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        formPanel.add(platField, gbc);

        // ===== FIELD: Tipe Kendaraan =====
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblTipe = new JLabel("Tipe Kendaraan:");
        lblTipe.setFont(labelFont);
        formPanel.add(lblTipe, gbc);

        gbc.gridx = 1;
        JComboBox<String> tipeCombo = new JComboBox<>(new String[] { "Mobil", "Motor" });
        tipeCombo.setPreferredSize(fieldSize);
        tipeCombo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        formPanel.add(tipeCombo, gbc);

        // ===== FIELD: Kendala =====
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblKendala = new JLabel("Kendala:");
        lblKendala.setFont(labelFont);
        formPanel.add(lblKendala, gbc);

        gbc.gridx = 1;
        JTextField tfKendala = new JTextField();
        tfKendala.setPreferredSize(fieldSize);
        formPanel.add(tfKendala, gbc);
        tfKendala.setFont(new Font("SansSerif", Font.PLAIN, 18));

        // ===== FIELD: Kronologi =====
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblKronologi = new JLabel("Kronologi:");
        lblKronologi.setFont(labelFont);
        formPanel.add(lblKronologi, gbc);

        gbc.gridx = 1;
        JTextArea taKronologi = new JTextArea(5, 20);
        taKronologi.setLineWrap(true);
        taKronologi.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(taKronologi);
        scrollPane.setPreferredSize(new Dimension(500, 150));
        taKronologi.setFont(new Font("SansSerif", Font.PLAIN, 18));
        formPanel.add(scrollPane, gbc);

        // ===== BUTTON LAPOR =====
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Rentang dua kolom
        gbc.insets = new Insets(20, 10, 30, 10);
        gbc.anchor = GridBagConstraints.CENTER; // Posisi tengah secara horizontal
        gbc.fill = GridBagConstraints.NONE;

        JButton btnLaporan = new JButton("Laporan");
        btnLaporan.setPreferredSize(new Dimension(150, 40));
        btnLaporan.setFont(new Font("Arial", Font.BOLD, 14));
        btnLaporan.setBackground(buttonColor);
        btnLaporan.setForeground(Color.BLACK);
        btnLaporan.setFocusPainted(false);
        formPanel.add(btnLaporan, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        btnLaporan.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String plat = platField.getText().trim();
                String tipe = (String) tipeCombo.getSelectedItem();
                String kendala = tfKendala.getText().trim();
                String kronologi = taKronologi.getText().trim();

                if (plat.isEmpty() || tipe == null || kendala.isEmpty() || kronologi.isEmpty()) {
                    popup_window(new ImageIcon("image/warning.png"), 250, 70);
                    return;
                }

                if (!isValidPlate(plat)) {
                    popup_window(new ImageIcon("image/eror.png"), 300, 100);
                    platField.setText("");
                    return;
                }
                if (!getPlatType(plat, tipe)) {
                    popup_window(new ImageIcon("image/eror.png"), 300, 100);
                    platField.setText("");
                    return;
                }
                boolean success = saveToDB(user_id, plat, tipe, kendala, kronologi);

                if (success) {
                    if (success) {
                        ImageIcon icon = new ImageIcon("image/berhasil_laporan.png");
                        popup_window(icon, 300, 70);

                        // Kosongkan form
                        platField.setText("");
                        tipeCombo.setSelectedIndex(0);
                        tfKendala.setText("");
                        taKronologi.setText("");

                        // Delay lalu buka StatusBooking
                        Timer delay = new Timer(2000, new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent evt) {
                                ((Timer) evt.getSource()).stop();
                                dispose();
                                SwingUtilities.invokeLater(() -> {
                                    StatusLaporan statusLaporan = new StatusLaporan(UserSession.getUserId());
                                    statusLaporan.setExtendedState(JFrame.MAXIMIZED_BOTH);
                                    statusLaporan.setVisible(true);
                                });
                            }
                        });
                        delay.setRepeats(false);
                        delay.start();
                    }

                }
            }

        });

        setVisible(true);
    }

    private boolean isValidPlate(String plat) {
        return plat.toUpperCase().matches("^[A-Z]{1,2}\\s?[0-9]{1,4}\\s?[A-Z]{0,3}$");
    }

    private boolean saveToDB(int userId, String plat, String tipe, String kendala, String kronologi) {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Report (" +
                "reportid INTEGER PRIMARY KEY AUTOINCREMENT," +
                "ticket_id INTEGER NOT NULL," +
                "user_id INTEGER NOT NULL," +
                "kendala TEXT NOT NULL," +
                "kronologi TEXT NOT NULL," +
                "FOREIGN KEY(ticket_id) REFERENCES Ticket(ticket_id)," +
                "FOREIGN KEY(user_id) REFERENCES User(id)" +
                ");";

        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database/App.db")) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON");
                stmt.execute(createTableSQL);
            }

            // Ambil ticket_id berdasarkan plat dan tipe kendaraan milik user login
            String findTicketSQL = "SELECT ticket_id FROM Ticket " +
                    "WHERE user_id = ? AND plateNumber = ? AND vehicleType = ? " +
                    "ORDER BY ticket_id DESC LIMIT 1";

            int ticketId = -1;
            try (PreparedStatement pstmt = conn.prepareStatement(findTicketSQL)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, plat);
                pstmt.setString(3, tipe);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    ticketId = rs.getInt("ticket_id");
                } else {
                    JOptionPane.showMessageDialog(null, "Data tiket tidak ditemukan!");
                    return false;
                }
            }

            // Simpan ke tabel Report
            String insertSQL = "INSERT INTO Report (ticket_id, user_id, kendala, kronologi) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
                pstmt.setInt(1, ticketId);
                pstmt.setInt(2, userId);
                pstmt.setString(3, kendala);
                pstmt.setString(4, kronologi);
                pstmt.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Kesalahan saat menyimpan laporan: " + e.getMessage());
            return false;
        }
    }

    private boolean getPlatType(String plat, String tipe) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database/App.db")) {
            String sql = "SELECT COUNT(*) FROM Ticket WHERE plateNumber = ? AND vehicleType = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, plat);
                pstmt.setString(2, tipe);
                ResultSet rs = pstmt.executeQuery();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
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

        int targetX = getX() + (getWidth() - popup.getWidth()) / 2;
        int startY = getY() - popup.getHeight();
        int targetY = getY() + 30;

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
        SwingUtilities.invokeLater(() -> {
            // Simulasi login berhasil
            int userId = 3; // Misalnya ini hasil dari database
            UserSession.setUserId(userId);
            new Booking(userId);
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
