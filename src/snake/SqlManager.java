package snake;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SqlManager {
    private static final String URL = "jdbc:mysql://localhost:3306/SnakeGameDB";
    private static final String USER = "root";
    private static final String USERNAME = "";
    private static final String PASSWORD = "root";
    private Connection conn;

    public SqlManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Successfully connected!");
        } catch (Exception e) {
            System.err.println("Connection error: " + e.getMessage());
        }
    }

    public Connection getConnection() {
        return conn;
    }

    // Tạo người chơi mới và trả về playerID
    public String createPlayer(String name) throws SQLException {
        String playerID = UUID.randomUUID().toString();
        String sql = "INSERT INTO Player (playerID, name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerID);
            stmt.setString(2, name.trim());
            stmt.executeUpdate();
            System.out.println("Created player: " + name + ", ID: " + playerID);
            return playerID;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) { // Duplicate name
                // Tìm playerID của tên đã tồn tại
                return getPlayerIDByName(name);
            }
            throw e;
        }
    }

    // Lấy playerID từ tên người chơi
    public String getPlayerIDByName(String name) throws SQLException {
        String sql = "SELECT playerID FROM Player WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name.trim());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("playerID");
                }
            rs.close();
            throw new SQLException("Player not found: " + name);
        }
        }

    // Lưu điểm số với playerID
    public boolean savePlayerScore(String playerID, int score) {
        String sql = "INSERT INTO playerinfo (playerID, score, play_time) VALUES (?, ?, NOW())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerID);
            stmt.setInt(2, score);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Saved score for playerID: " + playerID + " - Score: " + score);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error saving score: " + e.getMessage());
            return false;
        }
    }

    // Lưu thông tin người chơi (alias cho savePlayerScore để tương thích)
    public boolean savePlayer(String playerID, String name, int score) {
        return savePlayerScore(playerID, score);
    }

    // Lấy top điểm cao nhất
    public List<ScoreboardPanel.PlayerScore> getTopScores(int limit) throws SQLException {
        List<ScoreboardPanel.PlayerScore> scores = new ArrayList<>();
        String sql = 
            "SELECT p.name, pi.score, pi.play_time " +
            "FROM playerinfo pi " +
            "JOIN Player p ON pi.playerID = p.playerID " +
            "ORDER BY pi.score DESC LIMIT ?";
        
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
            System.err.println("Error fetching top scores: " + e.getMessage());
        }
        
        return scores;
    }

    // Lấy điểm cao nhất của một người chơi
    public int getPlayerBestScore(String playerID) {
        String sql = "SELECT MAX(score) as best_score FROM playerinfo WHERE playerID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerID);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int bestScore = rs.getInt("best_score");
                rs.close();
                return bestScore;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error fetching best score: " + e.getMessage());
        }
        return 0;
    }

    // Kiểm tra xem có phải điểm mới hay không
    public boolean isNewRecord(String playerID, int score) {
        return score > getPlayerBestScore(playerID);
    }

    // Lấy tổng số người chơi
    public int getTotalPlayers() {
        String sql = "SELECT COUNT(*) as total FROM Player";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int total = rs.getInt("total");
                rs.close();
                return total;
            }
            rs.close();
        } catch (SQLException e) {
            System.err.println("Error counting players: " + e.getMessage());
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
            System.err.println("Error counting games: " + e.getMessage());
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
            System.err.println("Error calculating average score: " + e.getMessage());
        }
        return 0.0;
    }

    // Xóa tất cả dữ liệu (để test)
    public boolean clearAllScores() {
        String sql1 = "DELETE FROM playerinfo";
        String sql2 = "DELETE FROM Player";
        try (PreparedStatement stmt1 = conn.prepareStatement(sql1);
             PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            stmt1.executeUpdate();
            stmt2.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error clearing data: " + e.getMessage());
            return false;
        }
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Connection closed");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
        }
    }
}