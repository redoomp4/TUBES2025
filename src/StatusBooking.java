
    import javax.swing.*;
    import java.awt.*;
    import java.awt.event.MouseAdapter;
    import java.awt.event.MouseEvent;
    import java.sql.*;
    import java.awt.event.ActionEvent;
    import java.awt.event.ActionListener;

    public class StatusBooking extends JFrame {
        private JPanel orderListPanel;
        private JPanel selectedPanel = null;
        private String selectedPlate = null;

        public StatusBooking(int user_id) {
            setTitle("SiParkir");
            UserSession.setUserId(user_id);
            setSize(1100, 750);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());
            setExtendedState(JFrame.MAXIMIZED_BOTH);

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
                } else if (label.equals("Cek status laporan")) {
                    btn.addActionListener(e -> {
                        StatusLaporan statusLaporan = new StatusLaporan(UserSession.getUserId());
                        statusLaporan.setExtendedState(JFrame.MAXIMIZED_BOTH);
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

            // Konten utama
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(Color.WHITE);
            add(mainPanel, BorderLayout.CENTER);

            // Panel untuk title
            JPanel titlePanel = new JPanel();
            titlePanel.setBackground(new Color(230, 230, 230)); // abu-abu terang
            titlePanel.setPreferredSize(new Dimension(1000, 60));
            titlePanel.setLayout(new BorderLayout());

            // Label judul
            JLabel titleLabel = new JLabel("PESANAN", SwingConstants.CENTER);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setOpaque(false);

            // Tambahkan label ke panel title
            titlePanel.add(titleLabel, BorderLayout.CENTER);

            // Tambahkan titlePanel ke mainPanel bagian atas
            mainPanel.add(titlePanel, BorderLayout.NORTH);

            // Ganti layout ke GridBagLayout
            orderListPanel = new JPanel();
            orderListPanel.setLayout(new BoxLayout(orderListPanel, BoxLayout.Y_AXIS));
            orderListPanel.setBackground(Color.WHITE);

            // Bungkus dengan panel layout FlowLayout supaya scroll-nya tetap di kanan
            JPanel containerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
            containerPanel.setBackground(Color.WHITE);
            containerPanel.add(orderListPanel);

            // ScrollPane tetap
            JScrollPane scrollPane = new JScrollPane(containerPanel,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setBorder(null);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
            // Tombol hapus
            JButton hapusButton = new JButton();
            try {
                ImageIcon icon = new ImageIcon("image/hapus_button.png");
                Image img = icon.getImage().getScaledInstance(85, 90, Image.SCALE_SMOOTH);
                hapusButton.setIcon(new ImageIcon(img));
            } catch (Exception e) {
                hapusButton.setText("Hapus");
            }

            hapusButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            hapusButton.addActionListener(e -> hapusPesananTerpilih());

            JPanel hapusPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            hapusPanel.setBackground(Color.WHITE);
            hapusPanel.add(hapusButton);
            mainPanel.add(hapusPanel, BorderLayout.SOUTH);

            loadData();
            setVisible(true);
        }

        private void loadData() {
            orderListPanel.removeAll();

            int userId = UserSession.getUserId(); // Hanya ambil user_id

            try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database/App.db")) {
                String sql;
                PreparedStatement stmt;

                if (userId == 1) {
                    // Admin melihat semua data
                    sql = "SELECT PersonName, plateNumber, vehicleType, placeBooked, bookingDate, bookingTime FROM Ticket";
                    stmt = conn.prepareStatement(sql);
                } else {
                    // User biasa melihat data miliknya saja
                    sql = "SELECT PersonName, plateNumber, vehicleType, placeBooked, bookingDate, bookingTime " +
                            "FROM Ticket WHERE user_id = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setInt(1, userId);
                }

                ResultSet rs = stmt.executeQuery();
                java.util.List<JPanel> kotakList = new java.util.ArrayList<>();

                while (rs.next()) {
                    String nama = rs.getString("PersonName");
                    String plat = rs.getString("plateNumber");
                    String tipe = rs.getString("vehicleType");
                    String lokasi = rs.getString("placeBooked");
                    String tanggal = rs.getString("bookingDate");
                    String jam = rs.getString("bookingTime");

                    JPanel kotak = new JPanel();
                    kotak.setLayout(new BoxLayout(kotak, BoxLayout.Y_AXIS));
                    kotak.setPreferredSize(new Dimension(350, 150));
                    kotak.setMaximumSize(new Dimension(350, 150));
                    kotak.setBackground(new Color(240, 240, 240));
                    kotak.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                    Font labelFont = new Font("SansSerif", Font.BOLD, 13);

                    kotak.add(makeLabel("Nama pengguna : " + nama, labelFont));
                    kotak.add(makeLabel("Nomor plat : " + plat, labelFont));
                    kotak.add(makeLabel("Tipe kendaraan : " + tipe, labelFont));
                    kotak.add(makeLabel("Lokasi parkir : " + lokasi, labelFont));
                    kotak.add(makeLabel("Tanggal : " + tanggal, labelFont));
                    kotak.add(makeLabel("Jam : " + jam, labelFont));

                    kotak.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            if (selectedPanel != null) {
                                selectedPanel.setBackground(new Color(240, 240, 240));
                            }
                            selectedPanel = kotak;
                            selectedPlate = plat;
                            kotak.setBackground(new Color(200, 200, 255));
                        }
                    });

                    kotakList.add(kotak);
                }

                for (int i = 0; i < kotakList.size(); i += 2) {
                    JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 20));
                    rowPanel.setBackground(Color.WHITE);
                    rowPanel.add(kotakList.get(i));
                    if (i + 1 < kotakList.size()) {
                        rowPanel.add(kotakList.get(i + 1));
                    }
                    orderListPanel.add(rowPanel);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

            orderListPanel.revalidate();
            orderListPanel.repaint();
        }

        private JLabel makeLabel(String text, Font font) {
            JLabel lbl = new JLabel(text);
            lbl.setFont(font);
            lbl.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            return lbl;
        }

        private void hapusPesananTerpilih() {
            if (selectedPlate == null) {
                ImageIcon warningIcon = new ImageIcon("image/eror_pesanan1.png");
                popup_window(warningIcon, 300, 80);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus pesanan ini?", "Konfirmasi",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:sqlite:database/App.db")) {
                    PreparedStatement delStmt = conn.prepareStatement("DELETE FROM Ticket WHERE plateNumber = ?");
                    delStmt.setString(1, selectedPlate);
                    delStmt.executeUpdate();
                    selectedPanel = null;
                    selectedPlate = null;
                    loadData();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
                int userId = 3;
                UserSession.setUserId(userId);
                new StatusBooking(userId);
            });
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
