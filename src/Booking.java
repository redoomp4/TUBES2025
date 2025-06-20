import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Booking extends JFrame {
    private JTextField namaField, platField, lokasiField;
    private JComboBox<String> tipeCombo;
    private JSpinner tanggalSpinner, jamSpinner;

    public Booking(String nama, String plat, String lokasi, String tipe, Date tanggal, Date jam) {
        this.namaField = new JTextField(nama);
        this.platField = new JTextField(plat);
        this.lokasiField = new JTextField(lokasi);
        this.tipeCombo = new JComboBox<>(new String[] { tipe });
        this.tanggalSpinner = new JSpinner(new SpinnerDateModel(tanggal, null, null, Calendar.DAY_OF_MONTH));
        this.jamSpinner = new JSpinner(new SpinnerDateModel(jam, null, null, Calendar.HOUR_OF_DAY));
    }

    public Booking(int user_id) {
        UserSession.setUserId(user_id);
        setTitle("SiParkir - Booking");
        setSize(1100, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainContent(), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(370, 0));
        sidebar.setBackground(new Color(44, 44, 44));

        sidebar.add(buildTopSidebarButtons(), BorderLayout.NORTH);
        sidebar.add(buildBottomSidebarButtons(), BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel buildTopSidebarButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(44, 44, 44));

        String[] labels = { "Booking", "Daftar pesanan" };
        for (String label : labels) {
            JButton button = createSidebarButton(label);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            panel.add(button);

            button.addActionListener(e -> {
                if (label.equals("Daftar pesanan")) {
                    StatusBooking statusBooking = new StatusBooking(UserSession.getUserId());
                    statusBooking.setExtendedState(JFrame.MAXIMIZED_BOTH); // pastikan fullscreen
                    statusBooking.setVisible(true);
                    setVisible(false);
                }
            });
        }
        return panel;
    }

    private JPanel buildBottomSidebarButtons() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(44, 44, 44));

        String[] labels = { "Laporan", "Cek status laporan" };
        for (String label : labels) {
            JButton button = createSidebarButton(label);
            panel.add(Box.createRigidArea(new Dimension(0, 15)));
            panel.add(button);

            button.addActionListener(e -> {
                if (label.equals("Laporan")) {
                    Report reportApp = new Report(UserSession.getUserId());
                    reportApp.setExtendedState(JFrame.MAXIMIZED_BOTH); // pastikan fullscreen
                    reportApp.setVisible(true);
                    setVisible(false);
                } else if (label.equals("Cek status laporan")) {
                    StatusLaporan statusLaporan = new StatusLaporan(UserSession.getUserId());
                    statusLaporan.setExtendedState(JFrame.MAXIMIZED_BOTH); // pastikan fullscreen
                    statusLaporan.setVisible(true);
                    setVisible(false);
                }
                dispose();
            });
        }

        // Buat tombol LOG OUT dengan tampilan custom
        JButton logoutBtn = new JButton("LOG OUT");
        logoutBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutBtn.setMaximumSize(new Dimension(200, 45)); // gunakan ukuran yang sesuai
        logoutBtn.setBackground(new Color(217, 217, 217)); // atau sesuaikan dengan 'buttonColor' kamu
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14)); // atau sesuaikan dengan 'buttonFont'
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        logoutBtn.setBorder(BorderFactory.createLineBorder(new Color(44, 44, 44), 10, true));

        // Aksi logout
        logoutBtn.addActionListener(e -> {
            Login loginApp = new Login();
            loginApp.setExtendedState(JFrame.MAXIMIZED_BOTH);
            loginApp.setVisible(true);
            setVisible(false);
        });

        // Tambahkan tombol logout ke panel (misalnya di bawah sidebar atau panel lain)
        panel.add(Box.createRigidArea(new Dimension(0, 25)));
        panel.add(logoutBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        return panel;
    }

    private JButton createSidebarButton(String label) {
        JButton button = new JButton(label);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 45));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setBackground(new Color(217, 217, 217));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return button;
    }

    private JPanel buildMainContent() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(230, 230, 230)); // abu-abu terang
        titlePanel.setPreferredSize(new Dimension(1000, 60));
        titlePanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("BOOKING", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setOpaque(false);

        // Tambahkan label ke panel title
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        // Tambahkan titlePanel ke mainPanel bagian atas
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        mainPanel.add(buildFormPanel(), BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel buildFormPanel() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font inputFont = new Font("Arial", Font.PLAIN, 16);

        int width = 400;
        int height = 35;

        int row = 0;
        formPanel.add(createLabel("Nama Pengguna", labelFont), createGbc(0, row));
        namaField = createField(inputFont, width, height);
        formPanel.add(namaField, createGbc(1, row++));

        formPanel.add(createLabel("Nomor Plat", labelFont), createGbc(0, row));
        platField = createField(inputFont, width, height);
        formPanel.add(platField, createGbc(1, row++));

        formPanel.add(createLabel("Tipe Kendaraan", labelFont), createGbc(0, row));
        tipeCombo = new JComboBox<>(new String[] { "Motor", "Mobil" });
        tipeCombo.setFont(inputFont);
        tipeCombo.setPreferredSize(new Dimension(width, height));
        formPanel.add(tipeCombo, createGbc(1, row++));

        formPanel.add(createLabel("Lokasi Parkir", labelFont), createGbc(0, row));
        lokasiField = createField(inputFont, width, height);
        formPanel.add(lokasiField, createGbc(1, row++));

        formPanel.add(createLabel("Tanggal Pemesanan", labelFont), createGbc(0, row));
        tanggalSpinner = new JSpinner(new SpinnerDateModel());
        tanggalSpinner.setEditor(new JSpinner.DateEditor(tanggalSpinner, "yyyy-MM-dd"));
        tanggalSpinner.setFont(inputFont);
        formPanel.add(tanggalSpinner, createGbc(1, row++));

        formPanel.add(createLabel("Jam Pemesanan", labelFont), createGbc(0, row));
        jamSpinner = new JSpinner(new SpinnerDateModel());
        jamSpinner.setEditor(new JSpinner.DateEditor(jamSpinner, "HH:mm"));
        jamSpinner.setFont(inputFont);
        formPanel.add(jamSpinner, createGbc(1, row++));

        JButton bookBtn = new JButton("Booking");
        bookBtn.setFont(new Font("Arial", Font.BOLD, 14));
        bookBtn.setBackground(new Color(217, 217, 217));
        bookBtn.setPreferredSize(new Dimension(200, 35)); // Ukuran lebih ramping
        bookBtn.addActionListener(this::prosesBooking);

        // Tambahkan ke formPanel tanpa fill HORIZONTAL dan tanpa gridwidth 2
        gbc = createGbc(0, row);
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.NONE; // <- Ini penting agar tidak memenuhi lebar 2 kolom
        formPanel.add(bookBtn, gbc);

        return formPanel;
    }

    private GridBagConstraints createGbc(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(15, 20, 15, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private JLabel createLabel(String text, Font font) {
        JLabel label = new JLabel(text, JLabel.RIGHT);
        label.setFont(font);
        return label;
    }

    private JTextField createField(Font font, int width, int height) {
        JTextField field = new JTextField();
        field.setFont(font);
        field.setPreferredSize(new Dimension(width, height));
        return field;
    }

    private void prosesBooking(ActionEvent e) {
        String nama = namaField.getText().trim();
        String plat = platField.getText().trim();
        String tipe = (String) tipeCombo.getSelectedItem();
        String lokasi = lokasiField.getText().trim();
        String tanggal = new SimpleDateFormat("yyyy-MM-dd").format((Date) tanggalSpinner.getValue());
        String jam = new SimpleDateFormat("HH:mm").format((Date) jamSpinner.getValue());

        if (nama.isEmpty() || plat.isEmpty() || lokasi.isEmpty()) {
            ImageIcon warningIcon = new ImageIcon("image/warning.png");
            popup_window(warningIcon, 250, 70);
            return;
        }

        int userId = UserSession.getUserId();

        if (getNameBooking(nama)) {
            ImageIcon warningIcon = new ImageIcon("image/eror_nama.png");
            popup_window(warningIcon, 300, 100);
            return;
        }

        if (getPlatTypeBooking(plat, tipe)) {
            ImageIcon warningIcon = new ImageIcon("image/eror_platType.png");
            popup_window(warningIcon, 300, 100);
            return;
        }

        if (!isValidPlate(plat)) {
            ImageIcon warningIcon = new ImageIcon("image/eror.png");
            popup_window(warningIcon, 300, 100);
            platField.setText("");
            return;
        }

        boolean success = createBooking(userId, nama, plat, tipe, lokasi, tanggal, jam);

        if (success) {
            if (success) {
                ImageIcon icon = new ImageIcon("image/berhasil_booked.png");
                popup_window(icon, 300, 70);

                namaField.setText("");
                platField.setText("");
                lokasiField.setText("");
                tipeCombo.setSelectedIndex(0);
                tanggalSpinner.setValue(new Date());
                jamSpinner.setValue(new Date());

                // Tambahkan delay 2 detik sebelum pindah ke StatusBooking
                Timer delay = new Timer(2000, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        ((Timer) evt.getSource()).stop();
                        dispose(); // tutup jendela BookingForm

                        // Buka halaman StatusBooking
                        SwingUtilities.invokeLater(() -> new StatusBooking(UserSession.getUserId()));
                    }
                });
                delay.setRepeats(false);
                delay.start();
            }

        }
    }

    private boolean createBooking(int userId, String name, String plate, String vehicleType, String location,
            String date, String time) {
        String url = "jdbc:sqlite:database/App.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            try (Statement pragmaStmt = conn.createStatement()) {
                pragmaStmt.execute("PRAGMA foreign_keys = ON;");
            }

            String createTableSQL = "CREATE TABLE IF NOT EXISTS Ticket (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER NOT NULL," +
                    "PersonName TEXT NOT NULL," +
                    "plateNumber TEXT NOT NULL," +
                    "vehicleType TEXT NOT NULL," +
                    "placeBooked TEXT NOT NULL," +
                    "bookingDate TEXT NOT NULL," +
                    "bookingTime TEXT NOT NULL," +
                    "FOREIGN KEY(user_id) REFERENCES Users(id)" +
                    ");";
            try (Statement stmt = conn.createStatement()) {
                stmt.execute(createTableSQL);
            }

            String sql = "INSERT INTO Ticket (user_id, PersonName, plateNumber, vehicleType, placeBooked, bookingDate, bookingTime) VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, userId);
                pstmt.setString(2, name);
                pstmt.setString(3, plate);
                pstmt.setString(4, vehicleType);
                pstmt.setString(5, location);
                pstmt.setString(6, date);
                pstmt.setString(7, time);
                pstmt.executeUpdate();
            }

            System.out.println("Data booking berhasil disimpan ke database.");
            return true;

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Kesalahan database: " + e.getMessage(), "Gagal Booking",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    private boolean isValidPlate(String plate) {
        return plate.toUpperCase().matches("^[A-Z]{1,2}\\s?[0-9]{1,4}\\s?[A-Z]{0,3}$");
    }

    private boolean getNameBooking(String nama) {
        String url = "jdbc:sqlite:database/App.db";
        String sql = "SELECT COUNT(*) FROM Ticket WHERE PersonName = ?";

        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nama);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean getPlatTypeBooking(String plat, String tipe) {
        String url = "jdbc:sqlite:database/App.db";
        String sql = "SELECT COUNT(*) FROM Ticket WHERE plateNumber = ? AND vehicleType = ?";

        try (Connection conn = DriverManager.getConnection(url);
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, plat);
            pstmt.setString(2, tipe);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
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
        // Di main method JANGAN pakai BookingForm(1)
        SwingUtilities.invokeLater(() -> {
            // Simulasi login berhasil
            int userId = 3; // Misalnya ini hasil dari database
            UserSession.setUserId(userId);
            new Booking(userId);
        });

    }
}

// Tambahan: Class UserSession
class UserSession {
    private static int userId;

    public static void setUserId(int id) {
        userId = id;
    }

    public static int getUserId() {
        return userId;
    }
}
