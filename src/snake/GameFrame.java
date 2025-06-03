package snake;

import javax.swing.*;
import java.awt.CardLayout;

public class GameFrame extends JFrame implements MenuPanel.MenuListener, GamePanel.GameListener {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private MenuPanel menuPanel;
    private GamePanel gamePanel;
    
    public GameFrame() {
        this.setTitle("Rắn Săn Mồi By PVM :D");
        
        // Sử dụng CardLayout để chuyển đổi giữa menu và game
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);
        
        // Tạo menu panel
        menuPanel = new MenuPanel(this);
        
        // Tạo game panel
        gamePanel = new GamePanel();
        gamePanel.setGameListener(this); // Thiết lập callback
        
        // Thêm các panel vào CardLayout
        mainPanel.add(menuPanel, "MENU");
        mainPanel.add(gamePanel, "GAME");
        
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
    
    @Override
    public void onStartGame() {
        // Chuyển sang game panel và bắt đầu game
        cardLayout.show(mainPanel, "GAME");
        gamePanel.requestFocusInWindow();
        gamePanel.startGame();
    }
    
    @Override
    public void onExitGame() {
        // Thoát game
        System.exit(0);
    }
    
    @Override
    public void onReturnToMenu() {
        // Quay lại menu từ game
        showMenu();
    }
    
    // Phương thức để quay lại menu (có thể gọi từ GamePanel khi game over)
    public void showMenu() {
        cardLayout.show(mainPanel, "MENU");
        menuPanel.requestFocusInWindow();
    }
}