package snake;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqlManager {
    private static final String URL = "jdbc:mysql://localhost:3306/SnakeGameDB";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private Connection conn;

    public SqlManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Ket noi thanh cong!");
        } catch (Exception e) {
            System.err.println("Loi ket noi: " + e.getMessage());
        }
    }
    
    public Connection getConnection() {
        return conn;
    }

    // Lưu điểm số người chơi - phương thức này được gọi từ GameFrame
    public boolean savePlayerScore(String name, int score) {
        if (name == null || name.trim().isEmpty()) {
        name = "Người chơi";
    }
    String sql = "INSERT INTO playerinfo (name, score, play_time) VALUES (?, ?, NOW())";
    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, name.trim());
        stmt.setInt(2, score);
        int rowsAffected = stmt.executeUpdate();
        System.out.println("Da luu: " + name + " - Điểm: " + score);
        return rowsAffected > 0;
    } catch (SQLException e) {
        System.err.println("Loi luu diem so: " + e.getMessage());
        return false;
    }
}

    // Lưu thông tin người chơi (alias cho savePlayerScore để tương thích)
    public boolean savePlayer(String name, int score) {
        return savePlayerScore(name, score);
    }
    
    // Lấy top điểm cao nhất
    public List<ScoreboardPanel.PlayerScore> getTopScores(int limit) {
        List<ScoreboardPanel.PlayerScore> scores = new ArrayList<>();
        String sql = "SELECT name, score, play_time FROM playerinfo ORDER BY score DESC LIMIT ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String name = rs.getString("name");
                int score = rs.getInt("score");
                String date = rs.getString("play_time");
                scores.add(new ScoreboardPanel.PlayerScore(name, score, date));
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Loi lay du lieu top scores: " + e.getMessage());
        }
        
        return scores;
    }
    
    // Lấy điểm cao nhất của một người chơi
    public int getPlayerBestScore(String playerName) {
        String sql = "SELECT MAX(score) as best_score FROM playerinfo WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int bestScore = rs.getInt("best_score");
                rs.close();
                return bestScore;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Loi lay diem cao nhat: " + e.getMessage());
        }
        return 0;
    }
    
    // Kiểm tra xem có phải điểm mới hay không
    public boolean isNewRecord(String playerName, int score) {
        return score > getPlayerBestScore(playerName);
    }
    
    // Lấy tổng số người chơi
    public int getTotalPlayers() {
        String sql = "SELECT COUNT(DISTINCT name) as total FROM playerinfo";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                rs.close();
                return total;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Loi dem nguoi choi: " + e.getMessage());
        }
        return 0;
    }
    
    // Lấy tổng số lượt chơi
    public int getTotalGames() {
        String sql = "SELECT COUNT(*) as total FROM playerinfo";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                rs.close();
                return total;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Loi dem tong game: " + e.getMessage());
        }
        return 0;
    }
    
    // Lấy điểm trung bình
    public double getAverageScore() {
        String sql = "SELECT AVG(score) as avg_score FROM playerinfo";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double avgScore = rs.getDouble("avg_score");
                rs.close();
                return avgScore;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Loi tinh diem trung binh: " + e.getMessage());
        }
        return 0.0;
    }
    
    // Xóa tất cả dữ liệu (để test)
    public boolean clearAllScores() {
        String sql = "DELETE FROM playerinfo";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Loi xoa du lieu: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Ket noi da dong");
            }
        } catch (SQLException e) {
            System.err.println("Loi dong ket noi: " + e.getMessage());
        }
    }
}