package snake;

import javax.swing.*;
import java.awt.CardLayout;

public class GameFrame extends JFrame implements MenuPanel.MenuListener, GamePanel.GameListener, ScoreboardPanel.ScoreboardListener {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    private ScoreboardPanel scoreboardPanel;
    private String currentPlayerName = "";
    
    public GameFrame() {
        this.setTitle("Rắn Săn Mồi By PVM :D");
        // Sử dụng CardLayout để chuyển đổi giữa các panel
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Tạo menu panel
        menuPanel = new MenuPanel(this);
        
        // Tạo game panel
        gamePanel = new GamePanel();
        gamePanel.setGameListener(this);
        gamePanel.setParentFrame(this);
        
        // Tạo scoreboard panel
        scoreboardPanel = new ScoreboardPanel(this);
        
        // Thêm các panel vào CardLayout
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        mainPanel.add(scoreboardPanel, "SCOREBOARD");
        
        // Hiển thị menu đầu tiên
        cardLayout.show(mainPanel, "MENU");
        
        this.add(mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.pack();
        this.setLocationRelativeTo(null);
        
        // Focus vào menu để có thể nhận sự kiện phím
        menuPanel.requestFocusInWindow();
    }
    
    // Implement MenuPanel.MenuListener
    @Override
    public void onStartGame(String playerName) {
        // Lưu tên người chơi
        currentPlayerName = playerName;
        gamePanel.setPlayerName(playerName);
        
        // Chuyển sang game panel và bắt đầu game
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow();
        gamePanel.startGame();
    }
    
    @Override
    public void onExitGame() {
        // Dừng tất cả âm thanh trước khi thoát
        gamePanel.stopAllSounds();
        
        // Hiện dialog xác nhận thoát
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
        // Làm mới dữ liệu bảng xếp hạng và hiển thị
        scoreboardPanel.refreshScores();
        cardLayout.show(mainPanel, "SCOREBOARD");
        scoreboardPanel.requestFocusInWindow();
    }
    
    // Implement GamePanel.GameListener
    @Override
    public void onReturnToMenu() {
        // Quay lại menu từ game
        showMenu();
    }
    
    
    public void onGameOver(int finalScore) {
        // Xử lý khi game over - lưu điểm số nếu cần
        if (!currentPlayerName.isEmpty() && finalScore > 0) {
            SqlManager sqlManager = new SqlManager();
            try {
                sqlManager.savePlayerScore(currentPlayerName, finalScore);
                System.out.println("Đã lưu điểm số: " + currentPlayerName + " - " + finalScore);
                
                // Kiểm tra xem đây có phải là kỷ lục mới
                if (sqlManager.isNewRecord(currentPlayerName, finalScore)) {
                    showMessage(
                        "Chúc mừng " + currentPlayerName + "! Bạn đã đạt kỷ lục mới: " + finalScore,
                        "Kỷ Lục Mới!",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi lưu điểm số: " + e.getMessage());
                showMessage(
                    "Không thể lưu điểm số do lỗi kết nối. Vui lòng thử lại sau.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE
                );
            } finally {
                sqlManager.close();
            }
        }
        // Tự động làm mới bảng xếp hạng sau khi game over
        scoreboardPanel.refreshScores();
    }
    
    // Implement ScoreboardPanel.ScoreboardListener
    @Override
    public void onBackToMenu() {
        // Quay lại menu từ scoreboard
        showMenu();
    }
    
    // Phương thức để quay lại menu
    public void showMenu() {
        cardLayout.show(mainPanel, "MENU");
        menuPanel.requestFocusInWindow();
    }
    
    // Phương thức để lấy tên người chơi hiện tại
    public String getCurrentPlayerName() {
        return currentPlayerName;
    }
    
    // Phương thức để hiển thị thông báo
    public void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }
    
    // Phương thức để hiển thị dialog xác nhận
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
    
    // Phương thức để refresh scoreboard từ bên ngoài
    public void refreshScoreboard() {
        scoreboardPanel.refreshScores();
    }
    
    // Phương thức để kiểm tra panel hiện tại
    public String getCurrentPanel() {
        // Không có cách trực tiếp để lấy panel hiện tại từ CardLayout
        // Có thể track thủ công nếu cần
        return "UNKNOWN";
    }
}