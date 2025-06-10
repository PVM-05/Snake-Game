package snake;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
            topScores = sqlManager.getTopScores(10);
            System.out.println("Loaded: " + topScores.size() + " scores from database");
        } catch (SQLException e) {
            System.err.println("Error loading scoreboard: " + e.getMessage());
            topScores.add(new PlayerScore("Sample Player 1", 25, "2025-09-01 10:00:00"));
            topScores.add(new PlayerScore("Sample Player 2", 20, "2025-09-02 11:00:00"));
            topScores.add(new PlayerScore("Sample Player 3", 15, "2025-09-03 12:00:00"));
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
        
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Montserrat", Font.BOLD, 36));
        FontMetrics titleMetrics = g.getFontMetrics();
        String title = "BẢNG XẾP HẠNG";
        int titleX = (SCREEN_WIDTH - titleMetrics.stringWidth(title)) / 2;
        g.drawString(title, titleX, 60);
        
        g.setColor(Color.GREEN);
        g.setFont(new Font("Montserrat", Font.BOLD, 18));
        g.drawString("Hạng", 50, 110);
        g.drawString("Tên", 120, 110);
        g.drawString("Điểm", 320, 110);
        g.drawString("Thời gian", 420, 110);
        
        g.setColor(Color.WHITE);
        g.drawLine(30, 120, SCREEN_WIDTH - 30, 120);
        
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
                
                if (i == 0) {
                    g.setColor(Color.YELLOW);
                } else if (i == 1) {
                    g.setColor(new Color(192, 192, 192));
                } else if (i == 2) {
                    g.setColor(new Color(205, 127, 50));
                } else {
                    g.setColor(Color.WHITE);
                }
                
                g.drawString(String.valueOf(i + 1), 60, yPosition);
                
                String displayName = player.name;
                if (displayName.length() > 20) {
                    displayName = displayName.substring(0, 17) + "...";
                }
                g.drawString(displayName, 120, yPosition);
                
                g.drawString(String.valueOf(player.score), 340, yPosition);
                
                String displayDate = player.date;
                if (displayDate != null) {
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
        
        drawStatistics(g);
        
        g.setFont(new Font("Montserrat", Font.PLAIN, 20));
        FontMetrics backMetrics = g.getFontMetrics();
        String backText = "QUAY LẠI";
        int backX = (SCREEN_WIDTH - backMetrics.stringWidth(backText)) / 2;
        int backY = SCREEN_HEIGHT - 80;
        
        if (backButtonHovered) {
            g.setColor(new Color(70, 200, 50));
        } else {
            g.setColor(Color.GREEN);
        }
        g.fillRoundRect(backX - 20, backY - 25, backMetrics.stringWidth(backText) + 40, 35, 15, 15);
        
        g.setColor(Color.WHITE);
        g.drawRoundRect(backX - 20, backY - 25, backMetrics.stringWidth(backText) + 40, 35, 15, 15);
        g.drawString(backText, backX, backY);
        
        backButtonBounds = new Rectangle(backX - 20, backY - 25, 
                                       backMetrics.stringWidth(backText) + 40, 35);
        
        g.setFont(new Font("Arial", Font.PLAIN, 12));
        g.setColor(Color.LIGHT_GRAY);
        FontMetrics instructionMetrics = g.getFontMetrics();
        String instruction = "Ấn ESC hoặc click QUAY LẠI để về menu chính";
        int instructionX = (SCREEN_WIDTH - instructionMetrics.stringWidth(instruction)) / 2;
        g.drawString(instruction, instructionX, SCREEN_HEIGHT - 20);
    }
    
    private void drawStatistics(Graphics g) {
        if (topScores.isEmpty()) return;
        
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
            System.err.println("Error fetching statistics: " + e.getMessage());
        } finally {
            sqlManager.close();
        }
    }
    
    public void refreshScores() {
        System.out.println("Refreshing scoreboard...");
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