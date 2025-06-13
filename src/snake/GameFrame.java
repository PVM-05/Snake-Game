package snake;

import javax.swing.*;
import java.awt.CardLayout;
import java.sql.SQLException;

public class GameFrame extends JFrame implements MenuPanel.MenuListener, GamePanel.GameListener, ScoreboardPanel.ScoreboardListener, SettingPanel.SettingsListener {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private SettingPanel settingPanel;
    private ScoreboardPanel scoreboardPanel;
    private String currentPlayerID = "";
    private String currentPlayerName = "";
    
    public String getCurrentPlayerID() {
        return currentPlayerID;
    }
    
    public GameFrame() {
        this.setTitle("Rắn Săn Mồi By PVM :D");
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        menuPanel = new MenuPanel(this);
        gamePanel = new GamePanel();
        gamePanel.setGameListener(this);
        gamePanel.setParentFrame(this);
        scoreboardPanel = new ScoreboardPanel(this);
        settingPanel = new SettingPanel(currentPlayerID);
        
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(scoreboardPanel, "SCOREBOARD");
        mainPanel.add(settingPanel, "SETTINGS");
        
        cardLayout.show(mainPanel, "MENU");
        
        this.add(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        
        menuPanel.requestFocusInWindow();
    }
    
    public void showSettings(String playerID) {
        DatabaseManager sqlManager = new DatabaseManager();
        try {
            if (playerID == null || playerID.isEmpty()) {
                playerID = createDefaultPlayer();
                currentPlayerID = playerID;
                currentPlayerName = "Người chơi";
            } else if (!sqlManager.isPlayerExists(playerID)) {
                playerID = sqlManager.createPlayer(currentPlayerName.isEmpty() ? "Người chơi" : currentPlayerName);
                currentPlayerID = playerID;
            }

            settingPanel = new SettingPanel(playerID);
            settingPanel.setSettingsListener(this);
            mainPanel.add(settingPanel, "SETTINGS");
            cardLayout.show(mainPanel, "SETTINGS");
            settingPanel.requestFocusInWindow();

            DatabaseManager.Settings settings = sqlManager.getPlayerSettings(playerID);
            if (settings != null) {
                settingPanel.setTheme(Theme.Type.valueOf(settings.getTheme()));
                settingPanel.setSoundVolumePercent(settings.getSoundVolumePercent());
            } else {
                settingPanel.setTheme(Theme.Type.CLASSIC);
                settingPanel.setSoundVolumePercent(50);
            }
        } catch (SQLException e) {
            System.err.println("Error loading settings: " + e.getMessage());
            showMessage("Không thể tải cài đặt. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
        } finally {
            sqlManager.close();
        }
    }
   
    public void onSaveSettings(String playerID, Theme.Type theme, int soundVolumePercent) {
        DatabaseManager sqlManager = new DatabaseManager();
        try {
            boolean saved = sqlManager.savePlayerSettings(playerID, theme.toString(), soundVolumePercent);
            if (saved) {
                gamePanel.setTheme(theme);
                gamePanel.setSoundVolumePercent(soundVolumePercent);
                showMessage("Cài đặt đã được lưu!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
            } else {
                showMessage("Không thể lưu cài đặt!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        } finally {
            sqlManager.close();
        }
        showMenu();
    }
    
    public void onBack() {
        showMenu();
    }
    
    @Override
    public void onShowSettings(String playerID) {
        showSettings(playerID);
    }
    
    @Override
    public void onStartGame(String playerName) {
        DatabaseManager sqlManager = new DatabaseManager();
        try {
            currentPlayerID = sqlManager.ensurePlayerExists(playerName.trim());
            currentPlayerName = playerName.trim();
            gamePanel.setPlayerName(playerName);

            DatabaseManager.Settings settings = sqlManager.getPlayerSettings(currentPlayerID);
            if (settings != null) {
                gamePanel.setTheme(Theme.Type.valueOf(settings.getTheme()));
                gamePanel.setSoundVolumePercent(settings.getSoundVolumePercent());
            } else {
                gamePanel.setTheme(Theme.Type.CLASSIC);
                gamePanel.setSoundVolumePercent(50);
            }

            cardLayout.show(mainPanel, "GAME");
            gamePanel.requestFocusInWindow();
            gamePanel.startGame();
            gamePanel.loadGameState(currentPlayerID);
        } catch (SQLException e) {
            System.err.println("Lỗi tạo người chơi: " + e.getMessage());
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
            DatabaseManager sqlManager = new DatabaseManager();
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
        DatabaseManager sqlManager = new DatabaseManager();
        try {
            currentPlayerName = "Người chơi";
            String playerID = sqlManager.ensurePlayerExists(currentPlayerName);
            currentPlayerID = playerID;
            return playerID;
        } catch (SQLException e) {
            System.err.println("Lỗi tạo người chơi mặc định: " + e.getMessage());
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