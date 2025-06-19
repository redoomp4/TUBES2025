import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.sql.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResetPassword {
    public static void main(String[] args) throws Exception {
        int port = 5580;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        System.out.println("✅ Server aktif di http://localhost:" + port);

        // Sajikan file HTML statisa
        server.createContext("/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/"))
                path = "/ResetPassword.html"; // default halaman

            File file = new File("." + path); // ubah dari "public" jadi "."

            if (!file.exists()) {
                exchange.sendResponseHeaders(404, 0);
                exchange.getResponseBody().write("404 Not Found".getBytes());
                exchange.close();
                return;
            }

            byte[] bytes = Files.readAllBytes(file.toPath());
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.close();
        });

        // Tangani POST untuk reset password
        server.createContext("/reset", exchange -> {
            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
                BufferedReader br = new BufferedReader(isr);
                StringBuilder body = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null)
                    body.append(line);

                Map<String, String> data = parseFormData(body.toString());

                String email = data.get("email");
                String newPassword = data.get("newPassword");
                String confirmPassword = data.get("confirmPassword");

                System.out.println("📥 Permintaan reset dari: " + email);

                String responseText;
                String url = "jdbc:sqlite:database/App.db";

                if (email == null || newPassword == null || confirmPassword == null) {
                    responseText = "{\"error\":\"Form tidak lengkap\"}";
                    exchange.sendResponseHeaders(400, responseText.length());
                } else if (!newPassword.equals(confirmPassword)) {
                    responseText = "{\"error\":\"Password tidak cocok\"}";
                    exchange.sendResponseHeaders(400, responseText.length());
                } else {
                    try (Connection conn = DriverManager.getConnection(url)) {
                        PreparedStatement stmt = conn.prepareStatement(
                                "UPDATE User SET user_pw = ? WHERE email = ?");
                        stmt.setString(1, newPassword);
                        stmt.setString(2, email);
                        int updated = stmt.executeUpdate();

                        if (updated > 0) {
                            responseText = "{\"message\":\"Password berhasil diubah\"}";
                            exchange.sendResponseHeaders(200, responseText.length());
                        } else {
                            responseText = "{\"error\":\"Email tidak ditemukan\"}";
                            exchange.sendResponseHeaders(404, responseText.length());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        responseText = "{\"error\":\"" + e.getMessage() + "\"}";
                        exchange.sendResponseHeaders(500, responseText.length());
                    }
                }

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.getResponseBody().write(responseText.getBytes());
                exchange.close();
            }
        });

        server.setExecutor(null);
        server.start();
    }

    private static Map<String, String> parseFormData(String body) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] parts = pair.split("=");
            if (parts.length == 2) {
                String key = URLDecoder.decode(parts[0], "UTF-8");
                String value = URLDecoder.decode(parts[1], "UTF-8");
                map.put(key, value);
            }
        }
        return map;
    }
}
