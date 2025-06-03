package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;

public class MenuPanel extends JPanel {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    
    private Rectangle startButtonBounds;
    private Rectangle exitButtonBounds;
    private MenuListener menuListener;
    private boolean startButtonHovered = false;
    private boolean exitButtonHovered = false;
    
    // Interface để callback khi có sự kiện menu
    public interface MenuListener {
        void onStartGame();
        void onExitGame();
    }
    
    public MenuPanel(MenuListener listener) {
        this.menuListener = listener;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        
        // Thêm key listener
        this.addKeyListener(new MenuKeyAdapter());
        
        // Thêm mouse listener
        this.addMouseListener(new MenuMouseAdapter());
        this.addMouseMotionListener(new MenuMouseMotionAdapter());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMenu(g);
    }
    
    private void drawMenu(Graphics g) {
        // Vẽ tiêu đề
        g.setColor(Color.WHITE);
        g.setFont(new Font("Montserrat", Font.BOLD, 40));
        FontMetrics titleMetrics = g.getFontMetrics();
        String title = "RẮN SĂN MỒI";
        int titleX = (SCREEN_WIDTH - titleMetrics.stringWidth(title)) / 2;
        int titleY = SCREEN_HEIGHT / 3;
        g.drawString(title, titleX, titleY);
        
        // Vẽ nút Start Game
        g.setFont(new Font("Montserrat", Font.PLAIN, 24));
        FontMetrics buttonMetrics = g.getFontMetrics();
        String startText = "BẮT ĐẦU";
        int startX = (SCREEN_WIDTH - buttonMetrics.stringWidth(startText)) / 2;
        int startY = SCREEN_HEIGHT / 2;
        
        // Màu nút Start thay đổi khi hover
        if (startButtonHovered) {
            g.setColor(new Color(70, 200, 70)); // Sáng hơn khi hover
        } else {
            g.setColor(new Color(50, 150, 50));
        }
        g.fillRoundRect(startX - 20, startY - 30, buttonMetrics.stringWidth(startText) + 40, 40, 15, 15);
        
        // Viền nút Start
        g.setColor(Color.WHITE);
        g.drawRoundRect(startX - 20, startY - 30, buttonMetrics.stringWidth(startText) + 40, 40, 15, 15);
        g.drawString(startText, startX, startY);
        
        // Lưu vùng nút Start
        startButtonBounds = new Rectangle(startX - 20, startY - 30, 
                                        buttonMetrics.stringWidth(startText) + 40, 40);
        
        // Vẽ nút Exit
        String exitText = "THOÁT";
        int exitX = (SCREEN_WIDTH - buttonMetrics.stringWidth(exitText)) / 2;
        int exitY = startY + 80;
        
        // Màu nút Exit thay đổi khi hover
        if (exitButtonHovered) {
            g.setColor(new Color(220, 70, 70)); // Sáng hơn khi hover
        } else {
            g.setColor(new Color(200, 50, 50));
        }
        g.fillRoundRect(exitX - 20, exitY - 30, buttonMetrics.stringWidth(exitText) + 40, 40, 15, 15);
        
        // Viền nút Exit
        g.setColor(Color.WHITE);
        g.drawRoundRect(exitX - 20, exitY - 30, buttonMetrics.stringWidth(exitText) + 40, 40, 15, 15);
        g.drawString(exitText, exitX, exitY);
        
        // Lưu vùng nút Exit
        exitButtonBounds = new Rectangle(exitX - 20, exitY - 30, 
                                       buttonMetrics.stringWidth(exitText) + 40, 40);
        
        // Vẽ hướng dẫn điều khiển
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        g.setColor(Color.LIGHT_GRAY);
        FontMetrics instructionMetrics = g.getFontMetrics();
        String instruction1 = "Ấn Enter để bắt đầu trò chơi";
        String instruction2 = "Ấn Esc để thoát";
        
        int instruction1X = (SCREEN_WIDTH - instructionMetrics.stringWidth(instruction1)) / 2;
        int instruction2X = (SCREEN_WIDTH - instructionMetrics.stringWidth(instruction2)) / 2;
        int instructionY = SCREEN_HEIGHT - 80;
        
        g.drawString(instruction1, instruction1X, instructionY);
        g.drawString(instruction2, instruction2X, instructionY + 20);
    }
    
    // Xử lý sự kiện bàn phím
    private class MenuKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
                case KeyEvent.VK_ENTER:
                    if (menuListener != null) {
                        menuListener.onStartGame();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    if (menuListener != null) {
                        menuListener.onExitGame();
                    }
                    break;
            }
        }
    }
    
    // Xử lý sự kiện click chuột
    private class MenuMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            
            // Kiểm tra click vào nút Start
            if (startButtonBounds != null && startButtonBounds.contains(mouseX, mouseY)) {
                if (menuListener != null) {
                    menuListener.onStartGame();
                }
            }
            // Kiểm tra click vào nút Exit
            else if (exitButtonBounds != null && exitButtonBounds.contains(mouseX, mouseY)) {
                if (menuListener != null) {
                    menuListener.onExitGame();
                }
            }
        }
        
        @Override
        public void mousePressed(MouseEvent e) {
            // Có thể thêm hiệu ứng khi nhấn nút
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            // Có thể thêm hiệu ứng khi thả nút
        }
    }
    
    // Xử lý sự kiện di chuyển chuột (hover effect)
    private class MenuMouseMotionAdapter extends MouseAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            
            boolean wasStartHovered = startButtonHovered;
            boolean wasExitHovered = exitButtonHovered;
            
            // Kiểm tra hover trên nút Start
            startButtonHovered = startButtonBounds != null && 
                                startButtonBounds.contains(mouseX, mouseY);
            
            // Kiểm tra hover trên nút Exit
            exitButtonHovered = exitButtonBounds != null && 
                              exitButtonBounds.contains(mouseX, mouseY);
            
            // Chỉ repaint khi trạng thái hover thay đổi
            if (wasStartHovered != startButtonHovered || wasExitHovered != exitButtonHovered) {
                repaint();
            }
        }
    }
}