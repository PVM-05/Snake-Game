package snake;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.io.*;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3306/SnakeGameDB";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private Connection conn;

    public DatabaseManager() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conn = DriverManager.getConnection(URL, USER, PASSWORD);
            if (conn != null) {
                System.out.println("Ket noi thanh cong");
            } else {
                throw new SQLException("Loi ket noi");
            }
        } catch (Exception e) {
            System.err.println("Loi ket noi: " + e.getMessage());
            conn = null;
        }
    }

    public Connection getConnection() {
        return conn;
    }

    public String createPlayer(String name) throws SQLException {
        if (conn == null) {
            throw new SQLException("Khong ton tai ket noi voi database");
        }
        String playerID = UUID.randomUUID().toString();
        String sql = "INSERT INTO Player (playerID, name) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerID);
            stmt.setString(2, name.trim());
            stmt.executeUpdate();
            System.out.println("Tao nguoi choi: " + name + ", ID: " + playerID);
            return playerID;
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                return getPlayerIDByName(name);
            }
            throw e;
        }
    }

    public String getPlayerIDByName(String name) throws SQLException {
        if (conn == null) {
            throw new SQLException("Khong ton tai ket noi voi database");
        }
        String sql = "SELECT playerID FROM Player WHERE name = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("playerID");
                }
                throw new SQLException("Khong tim thay nguoi choi: " + name);
            }
        }
    }

    public boolean savePlayerScore(String playerID, int score) {
        if (conn == null) {
            System.err.println("Khong ton tai ket noi voi database");
            return false;
        }
        String sql = "INSERT INTO playerinfo (playerID, score, play_time) VALUES (?, ?, NOW())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerID);
            stmt.setInt(2, score);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Luu diem cho playerID: " + playerID + " - Diem: " + score);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Loi khong luu duoc: " + e.getMessage());
            return false;
        }
    }

    public boolean savePlayer(String playerID, String name, int score) {
        return savePlayerScore(playerID, score);
    }

    public List<ScoreboardPanel.PlayerScore> getTopScores(int limit) throws SQLException {
        if (conn == null) {
            throw new SQLException("Khong ton tai ket noi voi database");
        }
        List<ScoreboardPanel.PlayerScore> scores = new ArrayList<>();
        String sql = 
            "SELECT p.name, MAX(pi.score) as max_score, MAX(pi.play_time) as latest_time " +
            "FROM playerinfo pi " +
            "JOIN Player p ON pi.playerID = p.playerID " +
            "GROUP BY p.playerID, p.name " +
            "ORDER BY max_score DESC LIMIT ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    String name = rs.getString("name");
                    int score = rs.getInt("max_score");
                    String date = rs.getString("latest_time");
                    scores.add(new ScoreboardPanel.PlayerScore(name, score, date));
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi lay top diem cao nhat: " + e.getMessage());
            throw e;
        }
        return scores;
    }

    public int getPlayerBestScore(String playerID) {
        if (conn == null) {
            System.err.println("Khong ton tai ket noi voi database");
            return 0;
        }
        String sql = "SELECT MAX(score) as best_score FROM playerinfo WHERE playerID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("best_score");
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi tai diem cao nhat: " + e.getMessage());
        }
        return 0;
    }

    public boolean isNewRecord(String playerID, int score) {
        return score > getPlayerBestScore(playerID);
    }

    public int getTotalPlayers() {
        if (conn == null) {
            System.err.println("Khong ton tai ket noi voi database");
            return 0;
        }
        String sql = "SELECT COUNT(*) as total FROM Player";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi dem nguoi choi: " + e.getMessage());
        }
        return 0;
    }

    public int getTotalGames() {
        if (conn == null) {
            System.err.println("Khong ton tai ket noi voi database");
            return 0;
        }
        String sql = "SELECT COUNT(*) as total FROM playerinfo";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("total");
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi dem so luot choi: " + e.getMessage());
        }
        return 0;
    }

    public double getAverageScore() {
        if (conn == null) {
            System.err.println("Khong ton tai ket noi voi database");
            return 0.0;
        }
        String sql = "SELECT AVG(score) as avg_score FROM playerinfo";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("avg_score");
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi lay diem trung binh: " + e.getMessage());
        }
        return 0.0;
    }

    public boolean clearAllScores() {
        if (conn == null) {
            System.err.println("Khong ton tai ket noi voi database");
            return false;
        }
        String sql1 = "DELETE FROM playerinfo";
        String sql2 = "DELETE FROM Player";
        try (PreparedStatement stmt1 = conn.prepareStatement(sql1);
             PreparedStatement stmt2 = conn.prepareStatement(sql2)) {
            stmt1.executeUpdate();
            stmt2.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Loi xoa du lieu: " + e.getMessage());
            return false;
        }
    }

    public boolean savePlayerSettings(String playerID, String theme, int soundVolumePercent) {
        if (conn == null) {
            System.err.println("Khong ton tai ket noi voi database");
            return false;
        }
        if (!isPlayerExists(playerID)) {
            System.err.println("PlayerID khong ton tai: " + playerID);
            return false;
        }
        String sql = "INSERT INTO Settings (player_id, theme, sound_volume_percent) VALUES (?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE theme = ?, sound_volume_percent = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerID);
            stmt.setString(2, theme);
            stmt.setInt(3, soundVolumePercent);
            stmt.setString(4, theme);
            stmt.setInt(5, soundVolumePercent);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Luu cai dat playerID: " + playerID + " - Theme: " + theme + ", Volume: " + soundVolumePercent + "%");
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Loi luu cai dat: " + e.getMessage());
            return false;
        }
    }

    boolean isPlayerExists(String playerID) {
        if (conn == null) {
            return false;
        }
        String sql = "SELECT COUNT(*) FROM Player WHERE playerID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi kiem tra playerID: " + e.getMessage());
        }
        return false;
    }

    public String ensurePlayerExists(String name) throws SQLException {
        if (conn == null) {
            throw new SQLException("Không tồn tại kết nối với database");
        }
        try {
            return createPlayer(name);
        } catch (SQLException e) {
            if (e.getSQLState().equals("23000")) {
                return getPlayerIDByName(name);
            }
            throw e;
        }
    }

    public Settings getPlayerSettings(String playerID) {
        if (conn == null) {
            System.err.println("Khong ton tai ket noi voi database");
            return null;
        }
        String sql = "SELECT theme, sound_volume_percent FROM Settings WHERE player_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String theme = rs.getString("theme");
                    int soundVolumePercent = rs.getInt("sound_volume_percent");
                    return new Settings(theme, soundVolumePercent);
                }
            }
        } catch (SQLException e) {
            System.err.println("Loi tai cai dat: " + e.getMessage());
        }
        return null;
    }

    public static class Settings {
        private String theme;
        private int soundVolumePercent;

        public Settings(String theme, int soundVolumePercent) {
            this.theme = theme;
            this.soundVolumePercent = soundVolumePercent;
        }

        public String getTheme() {
            return theme;
        }

        public int getSoundVolumePercent() {
            return soundVolumePercent;
        }
    }

    public String getPlayerNameByID(String playerID) throws SQLException {
        if (conn == null) {
            throw new SQLException("Khong ton tai ket noi database");
        }
        String sql = "SELECT name FROM Player WHERE playerID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        }
        return null;
    }

    public boolean saveGameState(String playerID, GameState state) {
        if (conn == null) {
            System.err.println("Khong ton tai ket noi database");
            return false;
        }
        String usedPlayerID = playerID;
        try {
            if (!isPlayerExists(playerID)) {
                System.out.println("PlayerID khong ton tai: " + playerID );
                String name = getPlayerNameByID(playerID);
                if (name == null) {
                    name = "Người chơi";
                }
                usedPlayerID = createPlayer(name);
                System.out.println("Da tao player moi voi ID: " + usedPlayerID + " cho ten: " + name);
            }
        } catch (SQLException e) {
            System.err.println("Loi tao nguoi choi: " + e.getMessage());
            return false;
        }

        String sql = "INSERT INTO GameState (player_id, state) VALUES (?, ?) " +
                     "ON DUPLICATE KEY UPDATE state = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(state);
            byte[] stateData = baos.toByteArray();

            stmt.setString(1, usedPlayerID);
            stmt.setBytes(2, stateData);
            stmt.setBytes(3, stateData);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Luu trang thai game playerID: " + usedPlayerID);
            return rowsAffected > 0;
        } catch (SQLException | IOException e) {
            System.err.println("Loi luu trang thai game: " + e.getMessage());
            return false;
        }
    }

    public GameState loadGameState(String playerID) {
        if (conn == null) {
            System.err.println("Khong ton tai ket noi database");
            return null;
        }
        String sql = "SELECT state FROM GameState WHERE player_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    byte[] stateData = rs.getBytes("state");
                    ByteArrayInputStream bais = new ByteArrayInputStream(stateData);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    return (GameState) ois.readObject();
                }
            }
        } catch (SQLException | IOException | ClassNotFoundException e) {
            System.err.println("Loi tai trang thai game: " + e.getMessage());
        }
        return null;
    }

    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
                System.out.println("Dong ket noi");
            }
        } catch (SQLException e) {
            System.err.println("Loi dong ket noi: " + e.getMessage());
        }
    }
}