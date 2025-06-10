package snake;

import javax.swing.*;
import java.awt.CardLayout;
import java.sql.SQLException;

public class GameFrame extends JFrame implements MenuPanel.MenuListener, GamePanel.GameListener, ScoreboardPanel.ScoreboardListener {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private ScoreboardPanel scoreboardPanel;
    private String currentPlayerID = "";
    private String currentPlayerName = "";
    
    public GameFrame() {
        this.setTitle("Rắn Săn Mồi By PVM :D");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel();
        gamePanel.setGameListener(this);
        gamePanel.setParentFrame(this);
        scoreboardPanel = new ScoreboardPanel(this);
        
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(scoreboardPanel, "SCOREBOARD");
        
        cardLayout.show(mainPanel, "MENU");
        
        this.add(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        
        menuPanel.requestFocusInWindow();
    }
    
    @Override
    public void onStartGame(String playerName) {
        SqlManager sqlManager = new SqlManager();
        try {
            currentPlayerID = sqlManager.createPlayer(playerName);
            currentPlayerName = playerName;
            gamePanel.setPlayerName(playerName);
            cardLayout.show(mainPanel, "GAME");
            gamePanel.requestFocusInWindow();
            gamePanel.startGame();
        } catch (SQLException e) {
            System.err.println("Error creating player: " + e.getMessage());
            showMessage("Không thể tạo người chơi. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            sqlManager.close();
        }
    }
    
    @Override
    public void onExitGame() {
        gamePanel.stopAllSounds();
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Bạn có chắc chắn muốn thoát game không?",
            "Xác nhận thoát",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    @Override
    public void onShowScoreboard() {
        scoreboardPanel.refreshScores();
        cardLayout.show(mainPanel, "SCOREBOARD");
        scoreboardPanel.requestFocusInWindow();
    }
    
    @Override
    public void onReturnToMenu() {
        showMenu();
    }
    
    public void onGameOver(int finalScore) {
        if (currentPlayerID.isEmpty()) {
            currentPlayerID = createDefaultPlayer();
        }
        if (finalScore > 0) {
            SqlManager sqlManager = new SqlManager();
            try {
                boolean saved = sqlManager.savePlayerScore(currentPlayerID, finalScore);
                if (saved) {
                    System.out.println("Saved score: " + currentPlayerName + " (ID: " + currentPlayerID + ") - " + finalScore);
                    if (sqlManager.isNewRecord(currentPlayerID, finalScore)) {
                        showMessage(
                            "Chúc mừng " + currentPlayerName + "! Bạn đã đạt kỷ lục mới: " + finalScore,
                            "Kỷ Lục Mới!",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                    }
                } else {
                    showMessage(
                        "Không thể lưu điểm số do lỗi hệ thống. Vui lòng thử lại.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            } catch (Exception e) {
                System.err.println("Error saving score: " + e.getMessage());
                showMessage(
                    "Không thể lưu điểm số do lỗi kết nối. Vui lòng thử lại sau.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            } finally {
                sqlManager.close();
            }
        }
        scoreboardPanel.refreshScores();
    }
    
    private String createDefaultPlayer() {
        SqlManager sqlManager = new SqlManager();
        try {
            currentPlayerName = "Người chơi";
            return sqlManager.createPlayer(currentPlayerName);
        } catch (SQLException e) {
            System.err.println("Error creating default player: " + e.getMessage());
            return "";
        } finally {
            sqlManager.close();
        }
    }
    
    @Override
    public void onBackToMenu() {
        showMenu();
    }
    
    public void showMenu() {
        cardLayout.show(mainPanel, "MENU");
        menuPanel.requestFocusInWindow();
    }
    
    public String getCurrentPlayerName() {
        return currentPlayerName;
    }
    
    public String getCurrentPlayerID() {
        return currentPlayerID;
    }
    
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    public boolean showConfirmDialog(String message, String title) {
        int choice = JOptionPane.showConfirmDialog(
            this,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        return choice == JOptionPane.YES_OPTION;
    }
    
    public void refreshScoreboard() {
        scoreboardPanel.refreshScores();
    }
    
    public String getCurrentPanel() {
        return "UNKNOWN";
    }
}