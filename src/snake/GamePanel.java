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
import javax.swing.JFrame;

public class GamePanel extends JPanel implements ActionListener {
    private JFrame parentFrame;
    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH / UNIT_SIZE) * (SCREEN_HEIGHT / UNIT_SIZE);
    static final int INITIAL_DELAY = 100; // Tốc độ ban đầu chậm hơn
    static final int MIN_DELAY = 40; // Tốc độ tối đa (nhanh nhất)
    static final int SPEED_INCREASE_INTERVAL = 3; // Tăng tốc mỗi 3 điểm
    
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
    Clip backgroudMusicClip; // Clip cho background music
    Clip gameOverMusicClip;// clip cho game over music
    private GameListener gameListener;
    
    // Biến để quản lý tốc độ
    private int currentDelay = INITIAL_DELAY;
    private int lastSpeedIncreaseScore = 0;
    
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
    
    // Phương thức dừng tất cả âm thanh
    public void stopAllSounds() {
        if (backgroudMusicClip != null && backgroudMusicClip.isRunning()) {
            backgroudMusicClip.stop();
            backgroudMusicClip.close();
        }
        if (gameOverMusicClip != null && gameOverMusicClip.isRunning()) {
            gameOverMusicClip.stop();
            gameOverMusicClip.close();
        }
    }
    
    // Tạo ra vị trí random táo
    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }
    
    // Phương thức cập nhật tốc độ game
    private void updateGameSpeed() {
        // Tính toán tốc độ mới dựa trên điểm số
        int speedLevel = applesEaten / SPEED_INCREASE_INTERVAL;
        int newDelay = INITIAL_DELAY - (speedLevel * 8); // Giảm 8ms mỗi level
        
        // Đảm bảo không vượt quá tốc độ tối đa
        if (newDelay < MIN_DELAY) {
            newDelay = MIN_DELAY;
        }
        
        // Chỉ cập nhật nếu tốc độ thay đổi
        if (newDelay != currentDelay) {
            currentDelay = newDelay;
            
            // Restart timer với tốc độ mới
            if (timer != null) {
                timer.stop();
                timer = new Timer(currentDelay, this);
                timer.start();
            }
            
            // Hiển thị thông báo tăng tốc (tùy chọn)
            System.out.println("Tốc độ tăng! Level: " + (speedLevel + 1) + " - Delay: " + currentDelay + "ms");
        }
    }
    
    // Phương thức lấy level tốc độ hiện tại
    public int getCurrentSpeedLevel() {
        return (applesEaten / SPEED_INCREASE_INTERVAL) + 1;
    }
    
    public void startGame() {
        // Reset game state
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        gameOverSoundPlayed = false;
        currentDelay = INITIAL_DELAY; // Reset tốc độ về ban đầu
        lastSpeedIncreaseScore = 0;
        
        // Dừng tất cả âm thanh trước khi bắt đầu game mới
        stopAllSounds();
        
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
        timer = new Timer(currentDelay, this);
        timer.start();
        backgroundMusic("src/snake/background.wav");
    }
    
    @Override
    public void paintComponent(Graphics g) {
        if(running){
            super.paintComponent(g);
            draw(g);
        }
        else{
            draw(g);
        }
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
            drawSpeedLevel(g); // Hiển thị level tốc độ
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
            
            // Cập nhật tốc độ sau khi ăn táo
            updateGameSpeed();
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
        g.drawString("Điểm: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Điểm: " + applesEaten)) / 2,  g.getFont().getSize());
    }
    
    // Phương thức mới để hiển thị level tốc độ
    public void drawSpeedLevel(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Monsterrat", Font.BOLD, 16));
        FontMetrics metrics = getFontMetrics(g.getFont());
        String speedText = "Tốc độ: " + getCurrentSpeedLevel();
        g.drawString(speedText, 10, 30);
        
        // Hiển thị thanh tiến trình đến level tiếp theo
        int progressToNext = applesEaten % SPEED_INCREASE_INTERVAL;
        int progressWidth = (progressToNext * 100) / SPEED_INCREASE_INTERVAL;
        
        // Vẽ thanh tiến trình
        g.setColor(Color.GRAY);
        g.fillRect(10, 35, 100, 8);
        g.setColor(Color.CYAN);
        g.fillRect(10, 35, progressWidth, 8);
        
        // Vẽ viền thanh tiến trình
        g.setColor(Color.WHITE);
        g.drawRect(10, 35, 100, 8);
    }
    
    public void gameOver(Graphics g) {
        // Hiển thị màn hình kết thúc trò chơi
        g.setColor(Color.RED);
        g.setFont(new Font("Montserrat", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
       
        // Hiển thị điểm số
        g.setColor(Color.WHITE);
        g.setFont(new Font("Montserrat", Font.BOLD, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Điểm: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Điểm: " + applesEaten)) / 2, g.getFont().getSize());
        
        // Hiển thị level tốc độ đạt được
        g.setFont(new Font("Montserrat", Font.BOLD, 16));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        String speedLevelText = "Level tối đa bạn đạt được: " + getCurrentSpeedLevel();
        g.drawString(speedLevelText, (SCREEN_WIDTH - metrics3.stringWidth(speedLevelText)) / 2, g.getFont().getSize() + 30);
        
        // Hiển thị hướng dẫn
        g.setFont(new Font("Montserrat", Font.PLAIN, 16));
        g.setColor(Color.LIGHT_GRAY);
        FontMetrics metrics4 = getFontMetrics(g.getFont());
        String instruction = "Ấn SPACE để chơi lại hoặc ESC để thoát ra màn hình chính";
        g.drawString(instruction, (SCREEN_WIDTH - metrics4.stringWidth(instruction)) / 2, SCREEN_HEIGHT - 50);
        
        // Chỉ phát nhạc một lần khi game over
        if (!gameOverSoundPlayed) {
            // Dừng nhạc nền
            if (backgroudMusicClip != null && backgroudMusicClip.isRunning()) {
                backgroudMusicClip.stop();
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
    
    private void showPauseMenu() {
        // Tạm dừng nhạc nền khi hiện menu pause
        if (backgroudMusicClip != null && backgroudMusicClip.isRunning()) {
            backgroudMusicClip.stop();
        }
        
        PauseMenu pauseMenu = new PauseMenu(parentFrame,
            e -> {
                // Resume game và tiếp tục phát nhạc
                timer.start();
                if (backgroudMusicClip != null && !backgroudMusicClip.isRunning()) {
                    backgroudMusicClip.start();
                }
            },
            e -> {
                // Dừng tất cả âm thanh khi quay về menu chính
                stopAllSounds();
                running = false;
                if (gameListener != null) gameListener.onReturnToMenu();
            }
        );
        pauseMenu.setVisible(true);
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
                        // Dừng tất cả âm thanh trước khi quay về menu
                        stopAllSounds();
                        gameListener.onReturnToMenu();
                    }
                }
                return;
            }
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                // Tạm dừng khi đang chơi
                timer.stop(); // Tạm dừng game
                showPauseMenu(); // Hiện menu tạm dừng
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
                backgroudMusicClip = AudioSystem.getClip();
                backgroudMusicClip.open(audioInput);
                // Giảm âm thanh
                FloatControl gainControl = (FloatControl) backgroudMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f);
                backgroudMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                backgroudMusicClip.start();
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
                gameOverMusicClip = AudioSystem.getClip();
                gameOverMusicClip.open(audioInput);
                gameOverMusicClip.start();
            } else {
                System.out.println("Không tìm thấy tệp nhạc!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}