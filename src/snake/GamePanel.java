
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
    
    static final int SCREEN_WIDTH = 600; //Kích thước khung hình trò chơi (600x600 px)
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25; 	//Kích thước một ô (đơn vị rắn, táo) là 25x25 px
    static final int GAME_UNITS = (SCREEN_WIDTH / UNIT_SIZE) * (SCREEN_HEIGHT / UNIT_SIZE); // 	Tổng số ô trên màn hình
    static final int DELAY = 75; // toc do cua game;
    // Toạ độ của rắn
    final int x[] = new int[GAME_UNITS]; // Mảng lưu tọa độ X của các phần thân rắn
    final int y[] = new int[GAME_UNITS]; // Mảng lưu tọa độ Y của các phần thân rắn
    
    int bodyParts = 6; // Số phần thân ban đầu của rắn là 6 ô.
    int applesEaten; // Số táo đã ăn để tính điểm
    int appleX; //Toạ độ X của táo
    int appleY; //Toạ độ Y của táo
    char direction = 'R'; //Hướng di chuyển vào ban đầu của rắn R = Right 
    boolean running = false; // trạng thái trò chơi
    Timer timer;
    Random random; //Tạo toạ độ ngẫu nghiên của táo
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT)); // kich thuoc man hinh
        this.setBackground(Color.black); // nen den
        this.setFocusable(true); // doc key tu ban phim
        this.addKeyListener(new MyKeyAdapter()); 
        startGame();
    }
    // tạo ra vị trí random táo
    public void newApple(){
        appleX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
    }
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        backgroundMusic("src/snake/background.wav");
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    
    // vẽ ra rắn, táo
    public void draw(Graphics g) {
        // ve luoi
        for(int i=0;i<SCREEN_HEIGHT/UNIT_SIZE;i++) {
            g.drawLine(i*UNIT_SIZE,0,i*UNIT_SIZE,SCREEN_HEIGHT);
            g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
        }
        if(running){
        // vẽ táo
        g.setColor(Color.red); // đặt màu đỏ
        g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE); // set màu đầy quả táo
        // ve ran
        for(int i=0;i<bodyParts;i++){
            if(i==0){
                g.setColor(Color.green); // dau ran
                g.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 10, 10);
            }
            else{
                g.setColor(new Color(45,180,0)); // than ran
                g.fillRoundRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE, 10, 10);
            }
        }
        drawScore(g);
    }
        else{
            gameOver(g);
        }
    }
    public void move() {
        for(int i = bodyParts; i>0 ;i--){
            x[i]= x[i-1];
            y[i]= y[i-1];
        }
        switch(direction){
            case 'U': // len
            y[0]=y[0]- UNIT_SIZE;
            break;
            case 'D': // xuong
            y[0]=y[0]+ UNIT_SIZE;
            break;
            case 'L': // trai
            x[0]=x[0] - UNIT_SIZE;
            break;
            case 'R': // phai
            x[0]=x[0] + UNIT_SIZE;
            break;
        }
        // logic cho ran chay xuyen tuong 
        if(x[0]<0){
            x[0] = SCREEN_WIDTH - UNIT_SIZE;
        }
        else if (x[0] >= SCREEN_WIDTH){
            x[0] = 0;
        }
        if(y[0]<0){
            x[0] = SCREEN_HEIGHT - UNIT_SIZE;
        }
        else if (y[0] >= SCREEN_HEIGHT){
            y[0] = 0;
        }
        
    }
    // kiem tra ran an tao va thang kich co
    public void checkApple() {
        // kiem tra vi tri xem ran da an duoc tao chua
        if((x[0] == appleX) && (y[0] == appleY)){
            bodyParts++; //tang kich co
            applesEaten++; //tang diem;
            newApple(); //an xong thi tao tao moi o vi tri random
            eatSoundEffect("src/snake/eat.wav");

        }            
    }
    // kiem tra va cham 
    public void checkCollision() {
        for(int i=bodyParts;i>0;i--){
            // va cham voi chinh no
            if((x[0] == x[i]) && (y[0]== y[i])){
                running = false;
            }
        }
        // toa do x chay tu 0 toi screen_width
        // toa do y chay tu 0 toi screen_height
        // cham vao canh trai
//        if(x[0]<0){
//            running = false;
//        }
//        // cham vao canh phai
//        if(x[0]>SCREEN_WIDTH){
//            running = false;
//        }
//        // cham vao ben duoi
//        if(y[0]>SCREEN_HEIGHT){
//            running = false;
//        }
//        // cham vao ben tren
//        if(y[0]<0){
//            running = false;
//        }
//        if(!running){
//            timer.stop();
//        }
    }
    public void drawScore(Graphics g) {
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        FontMetrics metrics = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
    }
    public void gameOver(Graphics g) {
        // hien thi man hinh ket thuc tro choi
        g.setColor(Color.red);
        g.setFont(new Font("Montserrat", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        // hien thi diem so
        g.setColor(Color.white);
        g.setFont(new Font("Montserrat", Font.BOLD, 20));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        // dung nhac nen
        if(clip != null && clip.isRunning()){
            clip.stop();
        }
        // nhac ket thuc game;
        boolean gameOverSoundEffect = false;
        if(!gameOverSoundEffect){
            playSoundEffect("src/snake/gameover.wav");
            gameOverSoundEffect = true;
        }
    }
    @Override
    public void actionPerformed(ActionEvent e){
        if(running == true){
            move();
            checkApple();
            checkCollision();
        }
        repaint();
    }
    // dieu khien ran
    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()){
                case KeyEvent.VK_LEFT:
                    if(direction != 'R'){
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if(direction != 'L'){
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                    if(direction != 'D'){
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if(direction != 'U'){
                        direction = 'D';
                    }
                    break;    
            }
        }
    }
    // them am nhac background an va game over
    Clip clip;
    public void playSoundEffect(String filepath) {
    try {
        File musicPath = new File(filepath);

        if (musicPath.exists()) {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
            clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
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
            // giam am thanh
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-10.0f); // Âm lượng: giá trị từ -80.0f (rất nhỏ) đến 6.0f (lớn)

            clip.loop(Clip.LOOP_CONTINUOUSLY); // Phát lặp vô hạn
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
        Clip clip = AudioSystem.getClip();
        clip.open(audioInput);
        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(-10.0f); // Âm lượng: giá trị từ -80.0f (rất nhỏ) đến 6.0f (lớn)
        clip.start();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
    public void gameOverSoundEffect(String filepath) {
    try {
        File musicPath = new File(filepath);

        if (musicPath.exists()) {
            AudioInputStream audioInput = AudioSystem.getAudioInputStream(musicPath);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInput);
            clip.start();
        } else {
            System.out.println("Không tìm thấy tệp nhạc!");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
}

}
