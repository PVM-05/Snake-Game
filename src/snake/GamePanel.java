package snake;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
    private ParticleSystem particleSystem;
    private String playerName = "";
    
    public void setParentFrame(JFrame parentFrame) {
        this.parentFrame = parentFrame;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
    
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH / UNIT_SIZE) * (SCREEN_HEIGHT / UNIT_SIZE);
    
    static final int TARGET_FPS = 60;
    static final int FRAME_DELAY = 1000 / TARGET_FPS;
    
    static final int INITIAL_MOVE_DELAY = 150;
    static final int MIN_MOVE_DELAY = 60;
    static final int SPEED_INCREASE_INTERVAL = 3;
    
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean gameOverSoundPlayed = false;
    Timer renderTimer;
    Random random;
    Clip backgroudMusicClip;
    Clip gameOverMusicClip;
    private GameListener gameListener;
    
    private long lastMoveTime = 0;
    private int currentMoveDelay = INITIAL_MOVE_DELAY;
    private long lastFrameTime = 0;
    
    private int frameCount = 0;
    private long fpsTimer = 0;
    private int currentFPS = 0;
    
    public interface GameListener {
        void onReturnToMenu();
    }
    
    public GamePanel() {
        particleSystem = new ParticleSystem();
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
    }
    
    public void setGameListener(GameListener listener) {
        this.gameListener = listener;
    }
    
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
    
    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE)) * UNIT_SIZE;
    }
    
    private void updateGameSpeed() {
        int speedLevel = applesEaten / SPEED_INCREASE_INTERVAL;
        int newMoveDelay = INITIAL_MOVE_DELAY - (speedLevel * 12);
        if (newMoveDelay < MIN_MOVE_DELAY) {
            newMoveDelay = MIN_MOVE_DELAY;
        }
        if (newMoveDelay != currentMoveDelay) {
            currentMoveDelay = newMoveDelay;
            System.out.println("Speed increased! Level: " + (speedLevel + 1) + " - Move Delay: " + currentMoveDelay + "ms");
        }
    }
    
    public int getCurrentSpeedLevel() {
        return (applesEaten / SPEED_INCREASE_INTERVAL) + 1;
    }
    
    public void startGame() {
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        gameOverSoundPlayed = false;
        currentMoveDelay = INITIAL_MOVE_DELAY;
        lastMoveTime = System.currentTimeMillis();
        lastFrameTime = System.currentTimeMillis();
        frameCount = 0;
        fpsTimer = System.currentTimeMillis();
        particleSystem.clear();
        
        stopAllSounds();
        
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }
        
        newApple();
        running = true;
        
        if (renderTimer != null) {
            renderTimer.stop();
        }
        renderTimer = new Timer(FRAME_DELAY, this);
        renderTimer.start();
        backgroundMusic("src/snake/background.wav");
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        updateFPS();
        if(running){
            draw(g);
        } else {
            draw(g);
        }
    }
    
    private void updateFPS() {
        frameCount++;
        long currentTime = System.currentTimeMillis();
        if (currentTime - fpsTimer >= 1000) {
            currentFPS = frameCount;
            frameCount = 0;
            fpsTimer = currentTime;
        }
    }
    
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        if (running) {
            particleSystem.createSparkle(appleX, appleY);
            drawApple(g2d);
            drawSnake(g2d);
            particleSystem.draw(g2d);
            drawScore(g2d);
            drawSpeedLevel(g2d);
            drawFPS(g2d);
        } else {
            particleSystem.draw(g2d);
            gameOver(g);
        }
    }
    
    private void drawApple(Graphics2D g2d) {
        g2d.setColor(Color.RED);
        g2d.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);
        g2d.setColor(new Color(255, 100, 100));
        g2d.fillOval(appleX + 5, appleY + 5, UNIT_SIZE/3, UNIT_SIZE/3);
    }
    
    private void drawSnake(Graphics2D g2d) {
        for (int i = 0; i < bodyParts; i++) {
            if (i == 0) {
                g2d.setColor(Color.GREEN);
                g2d.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 20, 20);
                g2d.setColor(Color.WHITE);
                g2d.fillOval(x[i] + 5, y[i] + 5, 6, 6);
                g2d.fillOval(x[i] + 14, y[i] + 5, 6, 6);
                g2d.setColor(Color.BLACK);
                g2d.fillOval(x[i] + 7, y[i] + 7, 2, 2);
                g2d.fillOval(x[i] + 16, y[i] + 7, 2, 2);
            } else {
                int greenValue = Math.max(100, 180 - (i * 10));
                g2d.setColor(new Color(45, greenValue, 0));
                g2d.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 15, 15);
            }
            if (i > 0) {
                particleSystem.createTrail(x[i], y[i]);
            }
        }
    }
    
    private void drawFPS(Graphics2D g2d) {
        g2d.setColor(Color.CYAN);
        g2d.setFont(new Font("Monospace", Font.PLAIN, 12));
        g2d.drawString("FPS: " + currentFPS, SCREEN_WIDTH - 80, 20);
    }
    
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }
        
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
        
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
    
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
            eatSoundEffect("src/snake/eat.wav");
            particleSystem.createExplosion(appleX, appleY);
            updateGameSpeed();
        }
    }
    
    public void checkCollision() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                particleSystem.createDeathEffect(x[0], y[0]);
                if (renderTimer != null) {
                    renderTimer.stop();
                }
                if (parentFrame != null) {
                    ((GameFrame) parentFrame).onGameOver(applesEaten);
                }
                break;
            }
        }
    }
    
    public void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Điểm: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Điểm: " + applesEaten)) / 2, g.getFont().getSize());
    }
    
    public void drawSpeedLevel(Graphics g) {
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Montserrat", Font.BOLD, 16));
        String speedText = "Tốc độ: " + getCurrentSpeedLevel();
        g.drawString(speedText, 10, 30);
        
        int progressToNext = applesEaten % SPEED_INCREASE_INTERVAL;
        int progressWidth = (progressToNext * 100) / SPEED_INCREASE_INTERVAL;
        
        g.setColor(Color.GRAY);
        g.fillRect(10, 35, 100, 8);
        g.setColor(Color.CYAN);
        g.fillRect(10, 35, progressWidth, 8);
        g.setColor(Color.WHITE);
        g.drawRect(10, 35, 100, 8);
    }
    
    public void gameOver(Graphics g) {
        g.setColor(Color.RED);
        g.setFont(new Font("Montserrat", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
       
        g.setColor(Color.WHITE);
        g.setFont(new Font("Montserrat", Font.BOLD, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Điểm: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Điểm: " + applesEaten)) / 2, g.getFont().getSize());
        
        g.setFont(new Font("Montserrat", Font.BOLD, 16));
        FontMetrics metrics3 = getFontMetrics(g.getFont());
        String speedLevelText = "Level tối đa bạn đạt được: " + getCurrentSpeedLevel();
        g.drawString(speedLevelText, (SCREEN_WIDTH - metrics3.stringWidth(speedLevelText)) / 2, g.getFont().getSize() + 30);
        
        g.setFont(new Font("Montserrat", Font.PLAIN, 16));
        g.setColor(Color.LIGHT_GRAY);
        FontMetrics metrics4 = getFontMetrics(g.getFont());
        String instruction = "Ấn SPACE để chơi lại hoặc ESC để thoát ra màn hình chính";
        g.drawString(instruction, (SCREEN_WIDTH - metrics4.stringWidth(instruction)) / 2, SCREEN_HEIGHT - 50);
        
        if (!gameOverSoundPlayed) {
            if (backgroudMusicClip != null && backgroudMusicClip.isRunning()) {
                backgroudMusicClip.stop();
            }
            gameOverSoundEffect("src/snake/gameover.wav");
            gameOverSoundPlayed = true;
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        long currentTime = System.currentTimeMillis();
        if (running) {
            if (currentTime - lastMoveTime >= currentMoveDelay) {
                move();
                checkApple();
                checkCollision();
                lastMoveTime = currentTime;
            }
        }
        particleSystem.update();
        repaint();
    }
    
    private void showPauseMenu() {
        if (backgroudMusicClip != null && backgroudMusicClip.isRunning()) {
            backgroudMusicClip.stop();
        }
        
        PauseMenu pauseMenu = new PauseMenu(parentFrame,
            e -> {
                if (renderTimer != null) {
                    renderTimer.start();
                }
                if (backgroudMusicClip != null && !backgroudMusicClip.isRunning()) {
                    backgroudMusicClip.start();
                }
                lastMoveTime = System.currentTimeMillis();
            },
            e -> {
                stopAllSounds();
                running = false;
                if (gameListener != null) gameListener.onReturnToMenu();
            }
        );
        pauseMenu.setVisible(true);
    }
    
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            if (!running) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    startGame();
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    stopAllSounds();
                    if (gameListener != null) {
                        gameListener.onReturnToMenu();
                    }
                }
                return;
            }
            
            if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                if (renderTimer != null) {
                    renderTimer.stop();
                }
                showPauseMenu();
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
    
    public void playSoundEffect(String filepath) {
        try {
            File musicPath = new File(filepath);
            if (musicPath.exists()) {
                AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
                Clip soundClip = AudioSystem.getClip();
                soundClip.open(audioInput);
                soundClip.start();
            } else {
                System.out.println("Sound file not found!");
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
                FloatControl gainControl = (FloatControl) backgroudMusicClip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(-10.0f);
                backgroudMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
                backgroudMusicClip.start();
            } else {
                System.out.println("Sound file not found!");
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
                System.out.println("Sound file not found!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void createSpecialEffect(int x, int y, String effectType) {
        switch (effectType) {
            case "powerup":
                for (int i = 0; i < 12; i++) {
                    particleSystem.createExplosion(x, y);
                }
                break;
            case "levelup":
                for (int i = 0; i < 20; i++) {
                    particleSystem.createSparkle(x + (int)(Math.random() * UNIT_SIZE), y + (int)(Math.random() * UNIT_SIZE));
                }
                break;
        }
    }
}