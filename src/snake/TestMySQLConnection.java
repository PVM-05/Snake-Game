
package snake;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestMySQLConnection {
    public static void main(String[] args) {
        // Thông tin kết nối
        String url = "jdbc:mysql://localhost:3306/SnakeGameDB"; // Thay SnakeGameDB bằng tên DB của bạn nếu khác
        String user = "root"; // Mặc định trong XAMPP
        String password = ""; // Mặc định là rỗng

        try {
            // Nạp driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Kết nối
            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("Ket noi thanh cong!");

            conn.close();
        } catch (ClassNotFoundException e) {
            System.out.println("Khong tim thay jbc driver.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Ket noi that bai");
            e.printStackTrace();
        }
    }
}
