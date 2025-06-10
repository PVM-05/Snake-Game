package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ScoreboardPanel extends JPanel {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    
    private List<PlayerScore> topScores;
    private ScoreboardListener scoreboardListener;
    private Rectangle backButtonBounds;
    private boolean backButtonHovered = false;
    
    public interface ScoreboardListener {
        void onBackToMenu();
    }
    
    public static class PlayerScore {
        public String name;
        public int score;
        public String date;
        
        public PlayerScore(String name, int score, String date) {
            this.name = name;
            this.score = score;
            this.date = date;
        }
    }
    
    public ScoreboardPanel(ScoreboardListener listener) {
        this.scoreboardListener = listener;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        
        this.addKeyListener(new ScoreboardKeyAdapter());
        this.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (backButtonBounds != null && backButtonBounds.contains(e.getPoint())) {
                    if (scoreboardListener != null) {
                        scoreboardListener.onBackToMenu();
                    }
                }
            }
        });
        
        this.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(java.awt.event.MouseEvent e) {
                boolean wasHovered = backButtonHovered;
                backButtonHovered = backButtonBounds != null && backButtonBounds.contains(e.getPoint());
                if (wasHovered != backButtonHovered) {
                    repaint();
                }
            }
        });
        
        loadTopScores();
    }
    
    private void loadTopScores() {
        topScores = new ArrayList<>();
        SqlManager sqlManager = new SqlManager();
        
        try {
            // Cập nhật SQL query để sử dụng bảng Players và cột play_time
            String sql = "SELECT name, score, play_time FROM playerinfo ORDER BY score DESC LIMIT 10";
            Connection conn = sqlManager.getConnection();
            if (conn != null) {
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    String name = rs.getString("name");
                    int score = rs.getInt("score");
                    String date = rs.getString("play_time");
                    topScores.add(new PlayerScore(name, score, date));
                }
                
                rs.close();
                stmt.close();
                System.out.println("Da tao " + topScores.size() + " diem so tu database");
            }
        } catch (SQLException e) {
            System.err.println("Loi tai bang xep hang: " + e.getMessage());
            // Thêm dữ liệu mẫu nếu không kết nối được database
            topScores.add(new PlayerScore("Người chơi mẫu 1", 25, "2025-09-01 10:00:00"));
            topScores.add(new PlayerScore("Người chơi mẫu 2", 20, "2025-09-02 11:00:00"));
            topScores.add(new PlayerScore("Người chơi mẫu 3", 15, "2025-09-03 12:00:00"));
        } finally {
            sqlManager.close();
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawScoreboard(g);
    }
    
    private void drawScoreboard(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Vẽ tiêu đề
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Montserrat", Font.BOLD, 36));
        FontMetrics titleMetrics = g.getFontMetrics();
        String title = "BẢNG XẾP HẠNG";
        int titleX = (SCREEN_WIDTH - titleMetrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, 60);
        
        // Vẽ header bảng
        g.setColor(Color.CYAN);
        g.setFont(new Font("Montserrat", Font.BOLD, 18));
        g.drawString("Hạng", 50, 110);
        g.drawString("Tên", 120, 110);
        g.drawString("Điểm", 320, 110);
        g.drawString("Thời gian", 420, 110);
        
        // Vẽ đường kẻ
        g.setColor(Color.WHITE);
        g.drawLine(30, 120, SCREEN_WIDTH - 30, 120);
        
        // Vẽ danh sách điểm
        g.setFont(new Font("Montserrat", Font.PLAIN, 16));
        int yPosition = 150;
        
        if (topScores.isEmpty()) {
            g.setColor(Color.LIGHT_GRAY);
            String noData = "Chưa có dữ liệu. Hãy chơi game để có điểm số!";
            FontMetrics noDataMetrics = g.getFontMetrics();
            int noDataX = (SCREEN_WIDTH - noDataMetrics.stringWidth(noData)) / 2;
            g.drawString(noData, noDataX, yPosition + 50);
        } else {
            for (int i = 0; i < topScores.size() && i < 10; i++) {
                PlayerScore player = topScores.get(i);
                
                // Màu sắc theo hạng
                if (i == 0) {
                    g.setColor(Color.YELLOW); // Vàng cho hạng 1
                } else if (i == 1) {
                    g.setColor(new Color(192, 192, 192)); // Bạc cho hạng 2
                } else if (i == 2) {
                    g.setColor(new Color(205, 127, 50)); // Đồng cho hạng 3
                } else {
                    g.setColor(Color.WHITE);
                }
                
                // Vẽ thông tin người chơi
                g.drawString(String.valueOf(i + 1), 60, yPosition);
                
                // Cắt tên nếu quá dài
                String displayName = player.name;
                if (displayName.length() > 20) {
                    displayName = displayName.substring(0, 17) + "...";
                }
                g.drawString(displayName, 120, yPosition);
                
                g.drawString(String.valueOf(player.score), 340, yPosition);
                
                // Định dạng ngày giờ đơn giản
                String displayDate = player.date;
                if (displayDate != null) {
                    // Chỉ lấy ngày tháng năm (bỏ giờ phút giây)
                    if (displayDate.length() > 10) {
                        displayDate = displayDate.substring(0, 10);
                    }
                } else {
                    displayDate = "N/A";
                }
                g.drawString(displayDate, 420, yPosition);
                
                yPosition += 30;
            }
        }
        
        // Hiển thị thống kê tổng quan
        drawStatistics(g);
        
        // Vẽ nút Back
        g.setFont(new Font("Montserrat", Font.PLAIN, 20));
        FontMetrics backMetrics = g.getFontMetrics();
        String backText = "QUAY LẠI";
        int backX = (SCREEN_WIDTH - backMetrics.stringWidth(backText)) / 2;
        int backY = SCREEN_HEIGHT - 80;
        
        if (backButtonHovered) {
            g.setColor(new Color(70, 70, 200));
        } else {
            g.setColor(new Color(50, 50, 150));
        }
        g.fillRoundRect(backX - 20, backY - 25, backMetrics.stringWidth(backText) + 40, 35, 15, 15);
        
        g.setColor(Color.WHITE);
        g.drawRoundRect(backX - 20, backY - 25, backMetrics.stringWidth(backText) + 40, 35, 15, 15);
        g.drawString(backText, backX, backY);
        
        backButtonBounds = new Rectangle(backX - 20, backY - 25, 
                                       backMetrics.stringWidth(backText) + 40, 35);
        
        // Vẽ hướng dẫn
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.LIGHT_GRAY);
        FontMetrics instructionMetrics = g.getFontMetrics();
        String instruction = "Ấn ESC hoặc click QUAY LẠI để về menu chính";
        int instructionX = (SCREEN_WIDTH - instructionMetrics.stringWidth(instruction)) / 2;
        g.drawString(instruction, instructionX, SCREEN_HEIGHT - 20);
    }
    
    private void drawStatistics(Graphics g) {
        if (topScores.isEmpty()) return;
        
        // Lấy thống kê từ database
        SqlManager sqlManager = new SqlManager();
        try {
            int totalPlayers = sqlManager.getTotalPlayers();
            int totalGames = sqlManager.getTotalGames();
            double avgScore = sqlManager.getAverageScore();
            
            g.setColor(Color.ORANGE);
            g.setFont(new Font("Arial", Font.PLAIN, 14));
            
            int statsY = SCREEN_HEIGHT - 150;
            g.drawString("Tổng số người chơi: " + totalPlayers, 50, statsY);
            g.drawString("Tổng số lượt chơi: " + totalGames, 250, statsY);
            g.drawString("Điểm trung bình: " + String.format("%.1f", avgScore), 450, statsY);
            
        } catch (Exception e) {
            System.err.println("Lỗi khi lấy thống kê: " + e.getMessage());
        } finally {
            sqlManager.close();
        }
    }
    
    public void refreshScores() {
        System.out.println("Lam moi bang xep hang...");
        loadTopScores();
        repaint();
    }
    
    private class ScoreboardKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (scoreboardListener != null) {
                    scoreboardListener.onBackToMenu();
                }
            }
        }
    }
}