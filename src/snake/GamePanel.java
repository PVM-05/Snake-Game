package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.sound.sampled.*;
import java.io.*;

public class GamePanel extends JPanel implements ActionListener {
    
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH / UNIT_SIZE) * (SCREEN_HEIGHT / UNIT_SIZE);
    static final int DELAY = 75;
    
    // Toạ độ của rắn
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean gameOverSoundPlayed = false;
    Timer timer;
    Random random;
    Clip clip; // Clip cho background music
    private GameListener gameListener;
    
    // Interface để callback khi cần quay về menu
    public interface GameListener {
        void onReturnToMenu();
    }
    
    public GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
    }
    
    // Setter cho GameListener
    public void setGameListener(GameListener listener) {
        this.gameListener = listener;
    }
    
    // Tạo ra vị trí random táo
    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }
    
    public void startGame() {
        // Reset game state
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        gameOverSoundPlayed = false;
        
        // Reset snake position
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        
        newApple();
        running = true;
        
        if (timer != null) {
            timer.stop();
        }
        timer = new Timer(DELAY, this);
        timer.start();
        backgroundMusic("src/snake/background.wav");
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    // Vẽ ra rắn, táo
    public void draw(Graphics g) {
        if (running) {
            // Vẽ táo
            g.setColor(Color.RED);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
            
            // Vẽ rắn
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.GREEN); // Đầu rắn
                    g.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 20, 20);
                } else {
                    g.setColor(new Color(45, 180, 0)); // Thân rắn
                    g.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 20, 20);
                }
            }
            drawScore(g);
        } else {
            gameOver(g);
        }
    }
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        
        switch (direction) {
            case 'U': // Lên
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D': // Xuống
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L': // Trái
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R': // Phải
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
        
        // Logic cho rắn chạy xuyên tường
        if (x[0] < 0) {
            x[0] = SCREEN_WIDTH - UNIT_SIZE;
        } else if (x[0] >= SCREEN_WIDTH) {
            x[0] = 0;
        }
        
        if (y[0] < 0) {
            y[0] = SCREEN_HEIGHT - UNIT_SIZE;
        } else if (y[0] >= SCREEN_HEIGHT) {
            y[0] = 0;
        }
    }
    // Kiểm tra rắn ăn táo và tăng kích cỡ
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
            eatSoundEffect("src/snake/eat.wav");
        }
    }
    
    // Kiểm tra va chạm
    public void checkCollision() {
        // Va chạm với chính nó
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                if (timer != null) {
                    timer.stop();
                }
                break;
            }
        }
    }
    
    public void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, 
                    (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, 
                    g.getFont().getSize());
    }
    
    public void gameOver(Graphics g) {
        // Hiển thị màn hình kết thúc trò chơi
        g.setColor(Color.RED);
        g.setFont(new Font("Montserrat", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", 
                    (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, 
                    SCREEN_HEIGHT / 2);
        
        // Hiển thị điểm số
        g.setColor(Color.WHITE);
        g.setFont(new Font("Montserrat", Font.BOLD, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, 
                    (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2, 
                    g.getFont().getSize());
        
        // Hiển thị hướng dẫn
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.setColor(Color.LIGHT_GRAY);
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        String instruction = "Press SPACE to restart or ESC to menu";
        g.drawString(instruction, 
                    (SCREEN_WIDTH - metrics3.stringWidth(instruction)) / 2, 
                    SCREEN_HEIGHT - 50);
        
        // Chỉ phát nhạc một lần khi game over
        if (!gameOverSoundPlayed) {
            // Dừng nhạc nền
            if (clip != null && clip.isRunning()) {
                clip.stop();
            }
            // Nhạc kết thúc game
            gameOverSoundEffect("src/snake/gameover.wav");
            gameOverSoundPlayed = true;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }
    
    // Điều khiển rắn bằng cách đọc từ bàn phím 
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!running) {
                // Xử lý khi game over
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    startGame(); // Restart game
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    // Quay về menu
                    if (gameListener != null) {
                        // Dừng nhạc nền nếu đang chạy
                        if (clip != null && clip.isRunning()) {
                            clip.stop();
                        }
                        gameListener.onReturnToMenu();
                    }
                }
                return;
            }
            
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
    
    // Các phương thức âm thanh
    public void playSoundEffect(String filepath) {
        try {
            File musicPath = new File(filepath);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip soundClip = AudioSystem.getClip();
                soundClip.open(audioInput);
                soundClip.start();
            } else {
                System.out.println("Không tìm thấy tệp nhạc!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void backgroundMusic(String filepath) {
        try {
            File musicPath = new File(filepath);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                clip = AudioSystem.getClip();
                clip.open(audioInput);
                // Giảm âm thanh
                FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
                clip.start();
            } else {
                System.out.println("Không tìm thấy tệp nhạc!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void eatSoundEffect(String filepath) {
        try {
            File soundFile = new File(filepath);
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(soundFile);
            Clip eatClip = AudioSystem.getClip();
            eatClip.open(audioInput);
            FloatControl gainControl = (FloatControl) eatClip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f);
            eatClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void gameOverSoundEffect(String filepath) {
        try {
            File musicPath = new File(filepath);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip gameOverClip = AudioSystem.getClip();
                gameOverClip.open(audioInput);
                gameOverClip.start();
            } else {
                System.out.println("Không tìm thấy tệp nhạc!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}