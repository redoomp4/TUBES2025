import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StatusLaporan extends JFrame {
    private int selectedReportId = -1;
    private JPanel selectedPanel = null;

    public StatusLaporan(int user_id) {
        UserSession.setUserId(user_id);
        setTitle("Status Laporan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        // Sidebar
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
                    BookingForm bookingForm = new BookingForm(UserSession.getUserId());
                    bookingForm.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    bookingForm.setVisible(true);
                    setVisible(false);
                });
            } else if (label.equals("Daftar pesanan")) {
                btn.addActionListener(e -> {
                    StatusBooking statusBooking = new StatusBooking(UserSession.getUserId());
                    statusBooking.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    statusBooking.setVisible(true);
                    setVisible(false);
                });
            }
        }
        topButtons.add(Box.createRigidArea(new Dimension(0, 30)));
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
                btn.addActionListener(e -> {
                    Report reportApp = new Report(UserSession.getUserId());
                    reportApp.setExtendedState(JFrame.MAXIMIZED_BOTH);
                    reportApp.setVisible(true);
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

        // Main content
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(Color.WHITE);
        add(mainContentPanel, BorderLayout.CENTER);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(230, 230, 230));
        titlePanel.setPreferredSize(new Dimension(1000, 60));
        titlePanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("STATUS LAPORAN", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setOpaque(false);
        titlePanel.add(titleLabel, BorderLayout.CENTER);

        mainContentPanel.add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Perbaikan: Tambahkan scroll pane hanya sekali di sini

        // Isi laporan dari database
        getReport(mainPanel, user_id);

        // Buat tombol hapus (pakai gambar)
        JScrollPane srollPane = new JScrollPane(mainPanel,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        srollPane.setBorder(null);
        mainContentPanel.add(srollPane, BorderLayout.CENTER);

        setVisible(true);// Tombol hapus di pojok kanan bawah
        // Panel untuk membungkus tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10)); // Biar tombol ke kanan
        buttonPanel.setBackground(Color.WHITE); // Backgroundnya bisa kamu sesuaikan

        // Buat tombolnya
        JButton hapusSemuaButton = new JButton(new ImageIcon("image/hapus_button.png")); // Ganti dengan path ikon kamu
        hapusSemuaButton.setPreferredSize(new Dimension(115, 100)); // Biar ukurannya kecil
        hapusSemuaButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        hapusSemuaButton.addActionListener(e -> {
            if (selectedReportId <= 0) {
                ImageIcon warningIcon = new ImageIcon("image/eror_laporan1.png");
                popup_window(warningIcon, 300, 100);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Yakin ingin menghapus laporan ini?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                hapusLaporanTerpilih(selectedReportId, mainPanel, user_id);
                selectedReportId = -1;
                selectedPanel = null;
            }
        });

        // Tambahkan tombol ke buttonPanel
        buttonPanel.add(hapusSemuaButton);

        // Tambahkan buttonPanel ke bawah
        mainContentPanel.add(buttonPanel, BorderLayout.SOUTH);

    }

    private void getReport(JPanel mainPanel, int user_id) {
        mainPanel.removeAll();
        String url = "jdbc:sqlite:database/App.db";

        try (Connection conn = DriverManager.getConnection(url)) {
            PreparedStatement stmt;

            if (user_id == 1) {
                // Admin: ambil semua report
                stmt = conn.prepareStatement(
                        "SELECT r.report_id, r.kendala, t.plateNumber " +
                                "FROM Report r " +
                                "JOIN Ticket t ON r.ticket_id = t.ticket_id ");
            } else {
                // User biasa: hanya laporan miliknya
                stmt = conn.prepareStatement(
                        "SELECT r.report_id, r.kendala, t.plateNumber " +
                                "FROM Report r " +
                                "JOIN Ticket t ON r.ticket_id = t.ticket_id " +
                                "WHERE r.user_id = ?");
                stmt.setInt(1, user_id);
            }

            ResultSet rs = stmt.executeQuery();

            List<JPanel> kotakList = new ArrayList<>();

            while (rs.next()) {
                int reportId = rs.getInt("report_id");
                String kendala = rs.getString("kendala");
                String plat = rs.getString("plateNumber");

                JPanel kotak = new JPanel();
                kotak.setLayout(new BorderLayout(10, 0));
                kotak.setPreferredSize(new Dimension(750, 60));
                kotak.setMaximumSize(new Dimension(750, 60));
                kotak.setBackground(new Color(240, 240, 240));
                kotak.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 10));
                infoPanel.setOpaque(false);
                JLabel infoLabel = new JLabel(kendala + " | " + plat);
                infoLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                infoPanel.add(infoLabel);

                JPanel detailBox = new JPanel();
                detailBox.setPreferredSize(new Dimension(80, 40));
                detailBox.setBackground(new Color(200, 200, 200));
                detailBox.setLayout(new GridBagLayout());

                JLabel linkLabel = new JLabel("<html><a href=''>Details</a></html>");
                linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                linkLabel.setForeground(Color.BLUE);
                linkLabel.setFont(new Font("Arial", Font.PLAIN, 18));
                linkLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        DetailStatus detailPage = new DetailStatus(UserSession.getUserId(), reportId);
                        detailPage.setExtendedState(JFrame.MAXIMIZED_BOTH);
                        detailPage.setVisible(true);
                        setVisible(false);
                        dispose();
                    }
                });

                detailBox.add(linkLabel);

                kotak.add(infoPanel, BorderLayout.WEST);
                kotak.add(detailBox, BorderLayout.EAST);

                kotak.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        Component clickedComponent = e.getComponent();
                        if (clickedComponent instanceof JLabel)
                            return;

                        if (selectedPanel != null) {
                            selectedPanel.setBackground(new Color(240, 240, 240));
                        }
                        kotak.setBackground(new Color(200, 200, 200));
                        selectedReportId = reportId;
                        selectedPanel = kotak;
                    }
                });

                kotakList.add(kotak);
            }

            for (JPanel kotak : kotakList) {
                kotak.setAlignmentX(Component.LEFT_ALIGNMENT);
                kotak.setPreferredSize(new Dimension(500, 50));
                kotak.setMaximumSize(new Dimension(500, 50));
                mainPanel.add(kotak);
                mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void hapusLaporanTerpilih(int reportId, JPanel mainPanel, int userId) {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database/App.db")) {
            PreparedStatement delStmt = conn.prepareStatement("DELETE FROM Report WHERE report_id = ?");
            delStmt.setInt(1, reportId);
            delStmt.executeUpdate();

            // Refresh tampilan setelah penghapusan
            getReport(mainPanel, userId);

        } catch (SQLException e) {
            e.printStackTrace();
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
        SwingUtilities.invokeLater(() -> {
            int userId = 1;
            UserSession.setUserId(userId);
            new StatusLaporan(userId);
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
