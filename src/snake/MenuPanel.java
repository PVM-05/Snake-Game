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
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class MenuPanel extends JPanel {
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600; 
    private Rectangle startButtonBounds;
    private Rectangle exitButtonBounds;
    private Rectangle scoreboardButtonBounds;
    private MenuListener menuListener;
    private boolean startButtonHovered = false;
    private boolean exitButtonHovered = false;
    private boolean scoreboardButtonHovered = false;
    private String playerName = ""; 
    private boolean isEnteringName = false;
    private Rectangle settingsButtonBounds; // Thêm biến cho nút cài đặt
    private boolean settingsButtonHovered = false;
    
    public interface MenuListener {
        void onStartGame(String playerName);
        void onExitGame();
        void onShowScoreboard();
        public void onShowSettings(String PlayerID);
    }
    
    public MenuPanel(MenuListener listener) {
        this.menuListener = listener;
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        
        this.addKeyListener(new MenuKeyAdapter());
        this.addMouseListener(new MenuMouseAdapter());
        this.addMouseMotionListener(new MenuMouseMotionAdapter());
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawMenu(g);
    }
    
    private void drawMenu(Graphics g) {
    // Draw title
    g.setColor(Color.WHITE);
    g.setFont(new Font("Montserrat", Font.BOLD, 40));
    FontMetrics titleMetrics = g.getFontMetrics();
    String title = "RẮN SĂN MỒI";
    int titleX = (SCREEN_WIDTH - titleMetrics.stringWidth(title)) / 2;
    int titleY = SCREEN_HEIGHT / 4;
    g.drawString(title, titleX, titleY);

    // Draw player name label and input box
    g.setFont(new Font("Montserrat", Font.PLAIN, 18));
    FontMetrics nameMetrics = g.getFontMetrics();
    String nameLabel = "Tên người chơi:";
    int nameLabelX = (SCREEN_WIDTH - nameMetrics.stringWidth(nameLabel)) / 2;
    int nameLabelY = titleY + 80;
    g.setColor(Color.CYAN);
    g.drawString(nameLabel, nameLabelX, nameLabelY);

    int nameBoxWidth = 300;
    int nameBoxHeight = 35;
    int nameBoxX = (SCREEN_WIDTH - nameBoxWidth) / 2;
    int nameBoxY = nameLabelY + 10;

    if (isEnteringName) {
        g.setColor(Color.GREEN);
    } else {
        g.setColor(Color.GRAY);
    }
    g.drawRect(nameBoxX, nameBoxY, nameBoxWidth, nameBoxHeight);

    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.PLAIN, 16));
    String displayName = playerName.isEmpty() ? "Nhấn Enter để nhập tên..." : playerName;
    if (playerName.isEmpty()) {
        g.setColor(Color.LIGHT_GRAY);
    }
    g.drawString(displayName, nameBoxX + 10, nameBoxY + 25);

    if (isEnteringName) {
        g.setColor(Color.WHITE);
        int cursorX = nameBoxX + 10 + g.getFontMetrics().stringWidth(playerName);
        g.drawLine(cursorX, nameBoxY + 8, cursorX, nameBoxY + nameBoxHeight - 8);
    }

    // Draw start button
    g.setFont(new Font("Montserrat", Font.PLAIN, 20));
    FontMetrics buttonMetrics = g.getFontMetrics();
    String startText = "BẮT ĐẦU";
    int startX = (SCREEN_WIDTH - buttonMetrics.stringWidth(startText)) / 2;
    int startY = nameBoxY + 90;

    if (startButtonHovered) {
        g.setColor(new Color(70, 200, 70));
    } else {
        g.setColor(Color.GREEN);
    }
    g.fillRoundRect(startX - 20, startY - 25, buttonMetrics.stringWidth(startText) + 40, 35, 15, 15);
    g.setColor(Color.WHITE);
    g.drawRoundRect(startX - 20, startY - 25, buttonMetrics.stringWidth(startText) + 40, 35, 15, 15);
    g.drawString(startText, startX, startY);

    startButtonBounds = new Rectangle(startX - 20, startY - 25, buttonMetrics.stringWidth(startText) + 40, 35);

    // Draw scoreboard button
    String scoreboardText = "BẢNG XẾP HẠNG";
    int scoreboardX = (SCREEN_WIDTH - buttonMetrics.stringWidth(scoreboardText)) / 2;
    int scoreboardY = startY + 60;

    if (scoreboardButtonHovered) {
        g.setColor(new Color(70, 70, 200));
    } else {
        g.setColor(new Color(50, 50, 150));
    }
    g.fillRoundRect(scoreboardX - 20, scoreboardY - 25, buttonMetrics.stringWidth(scoreboardText) + 40, 35, 15, 15);
    g.setColor(Color.WHITE);
    g.drawRoundRect(scoreboardX - 20, scoreboardY - 25, buttonMetrics.stringWidth(scoreboardText) + 40, 35, 15, 15);
    g.drawString(scoreboardText, scoreboardX, scoreboardY);

    scoreboardButtonBounds = new Rectangle(scoreboardX - 20, scoreboardY - 25, buttonMetrics.stringWidth(scoreboardText) + 40, 35);

    // Draw exit button
    String exitText = "THOÁT";
    int exitX = (SCREEN_WIDTH - buttonMetrics.stringWidth(exitText)) / 2;
    int exitY = scoreboardY + 60;

    if (exitButtonHovered) {
        g.setColor(new Color(220, 70, 70));
    } else {
        g.setColor(Color.RED);
    }
    g.fillRoundRect(exitX - 20, exitY - 25, buttonMetrics.stringWidth(exitText) + 40, 35, 15, 15);
    g.setColor(Color.WHITE);
    g.drawRoundRect(exitX - 20, exitY - 25, buttonMetrics.stringWidth(exitText) + 40, 35, 15, 15);
    g.drawString(exitText, exitX, exitY);

    exitButtonBounds = new Rectangle(exitX - 20, exitY - 25, buttonMetrics.stringWidth(exitText) + 40, 35);

    // Draw settings button
    String settingsText = "CÀI ĐẶT";
    int settingsX = (SCREEN_WIDTH - buttonMetrics.stringWidth(settingsText)) / 2;
    int settingsY = exitY + 60; // Đặt dưới nút Thoát với khoảng cách 60

    if (settingsButtonHovered) {
        g.setColor(new Color(70, 200, 200));
    } else {
        g.setColor(new Color(50, 150, 150));
    }
    g.fillRoundRect(settingsX - 20, settingsY - 25, buttonMetrics.stringWidth(settingsText) + 40, 35, 15, 15);
    g.setColor(Color.WHITE);
    g.drawRoundRect(settingsX - 20, settingsY - 25, buttonMetrics.stringWidth(settingsText) + 40, 35, 15, 15);
    g.drawString(settingsText, settingsX, settingsY);

    settingsButtonBounds = new Rectangle(settingsX - 20, settingsY - 25, buttonMetrics.stringWidth(settingsText) + 40, 35);

    // Draw instructions
    g.setFont(new Font("Arial", Font.PLAIN, 12));
    g.setColor(Color.LIGHT_GRAY);
    FontMetrics instructionMetrics = g.getFontMetrics();
    String instruction1 = "Enter: Nhập tên | Space: Bắt đầu | S: Bảng Xếp hạng | C: Cài đặt | Esc: Thoát";
    int instruction1X = (SCREEN_WIDTH - instructionMetrics.stringWidth(instruction1)) / 2;
    int instruction1Y = (SCREEN_HEIGHT - 30);
    g.drawString(instruction1, instruction1X, instruction1Y);
}
    
    public String getPlayerName() {
        return playerName;
    }
    
    private class MenuKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (isEnteringName) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    if (playerName.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(
                            MenuPanel.this,
                            "Vui lòng nhập tên hợp lệ!",
                            "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE
                        );
                    } else {
                        isEnteringName = false;
                        repaint();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
                    if (playerName.length() > 0) {
                        playerName = playerName.substring(0, playerName.length() - 1);
                        repaint();
                    }
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    isEnteringName = false;
                    repaint();
                }
                return;
            }
            
            switch (e.getKeyCode()) {
                case KeyEvent.VK_C: // Thêm phím C để mở cài đặt
                        if (menuListener != null) {
                            menuListener.onShowSettings(((GameFrame) getRootPane().getParent()).getCurrentPlayerID());
                        }
                        break;
                case KeyEvent.VK_ENTER:
                    isEnteringName = true;
                    repaint();
                    break;
                case KeyEvent.VK_SPACE:
                    if (playerName.trim().isEmpty()) {
                        JOptionPane.showMessageDialog(
                            MenuPanel.this,
                            "Vui lòng nhập tên trước khi bắt đầu!",
                            "Cảnh báo",
                            JOptionPane.WARNING_MESSAGE
                        );
                    } else if (menuListener != null) {
                        menuListener.onStartGame(playerName.trim());
                    }
                    break;
                case KeyEvent.VK_S:
                    if (menuListener != null) {
                        menuListener.onShowScoreboard();
                    }
                    break;
                case KeyEvent.VK_ESCAPE:
                    if (menuListener != null) {
                        menuListener.onExitGame();
                    }
                    break;
            }
        }
        
        @Override
        public void keyTyped(KeyEvent e) {
            if (isEnteringName) {
                char c = e.getKeyChar();
                if (c != KeyEvent.CHAR_UNDEFINED && c != '\b' && c != '\n' && c != '\r') {
                    if (playerName.length() < 20) {
                        playerName += c;
                        repaint();
                    }
                }
            }
        }
    }
    
    private class MenuMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            
            if (startButtonBounds != null && startButtonBounds.contains(mouseX, mouseY)) {
                if (playerName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(
                        MenuPanel.this,
                        "Vui lòng nhập tên trước khi bắt đầu!",
                        "Cảnh báo",
                        JOptionPane.WARNING_MESSAGE
                    );
                } else if (menuListener != null) {
                    menuListener.onStartGame(playerName.trim());
                }
            } else if (scoreboardButtonBounds != null && scoreboardButtonBounds.contains(mouseX, mouseY)) {
                if (menuListener != null) {
                    menuListener.onShowScoreboard();
                }
            } else if (exitButtonBounds != null && exitButtonBounds.contains(mouseX, mouseY)) {
                if (menuListener != null) {
                    menuListener.onExitGame();
                }
            }
        }
    }
    
    private class MenuMouseMotionAdapter extends MouseAdapter {
        @Override
        public void mouseMoved(MouseEvent e) {
            int mouseX = e.getX();
            int mouseY = e.getY();
            
            boolean wasStartHovered = startButtonHovered;
            boolean wasExitHovered = exitButtonHovered;
            boolean wasScoreboardHovered = scoreboardButtonHovered;
            boolean wasSettingsHovered = settingsButtonHovered;
            settingsButtonHovered = settingsButtonBounds != null && settingsButtonBounds.contains(mouseX, mouseY);
            
            startButtonHovered = startButtonBounds != null && 
                                 startButtonBounds.contains(mouseX, mouseY);
            
            exitButtonHovered = exitButtonBounds != null && 
                                exitButtonBounds.contains(mouseX, mouseY);
                              
            scoreboardButtonHovered = scoreboardButtonBounds != null && 
                                      scoreboardButtonBounds.contains(mouseX, mouseY);
            
            if (wasSettingsHovered != settingsButtonHovered || wasStartHovered != startButtonHovered || 
                wasExitHovered != exitButtonHovered || 
                wasScoreboardHovered != scoreboardButtonHovered) {
                repaint();
            }
        }
    }
}