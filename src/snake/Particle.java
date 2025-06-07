package snake;

import java.awt.*;
import java.util.Random;

public class Particle {
    private float x, y;
    private float velocityX, velocityY;
    private float gravity;
    private Color color;
    private int life;
    private int maxLife;
    private float size;
    private float alpha; // Độ trong suốt
    private ParticleType type;
    
    public enum ParticleType {
        EXPLOSION,    // Khi ăn táo
        DEATH,        // Khi chết
        TRAIL,        // Đuôi rắn
        SPARKLE       // Lấp lánh
    }
    
    public Particle(float x, float y, ParticleType type) {
        this.x = x;
        this.y = y;
        this.type = type;
        
        Random random = new Random();
        
        switch (type) {
            case EXPLOSION:
                // Particles bay ra xung quanh khi ăn táo
                float angle = random.nextFloat() * 2 * (float)Math.PI;
                float speed = 2 + random.nextFloat() * 4;
                this.velocityX = (float)Math.cos(angle) * speed;
                this.velocityY = (float)Math.sin(angle) * speed;
                this.gravity = 0.1f;
                this.color = new Color(255, random.nextInt(100) + 155, 0); // Vàng-cam
                this.maxLife = 30 + random.nextInt(20);
                this.size = 3 + random.nextFloat() * 4;
                break;
                
            case DEATH:
                // Particles khi rắn chết
                angle = random.nextFloat() * 2 * (float)Math.PI;
                speed = 1 + random.nextFloat() * 3;
                this.velocityX = (float)Math.cos(angle) * speed;
                this.velocityY = (float)Math.sin(angle) * speed;
                this.gravity = 0.05f;
                this.color = new Color(255, 0, 0); // Đỏ
                this.maxLife = 40 + random.nextInt(30);
                this.size = 2 + random.nextFloat() * 3;
                break;
                
            case TRAIL:
                // Particles đuôi rắn
                this.velocityX = (random.nextFloat() - 0.5f) * 2;
                this.velocityY = (random.nextFloat() - 0.5f) * 2;
                this.gravity = 0.02f;
                this.color = new Color(0, 255, 0, 100); // Xanh lá trong suốt
                this.maxLife = 15 + random.nextInt(10);
                this.size = 1 + random.nextFloat() * 2;
                break;
                
            case SPARKLE:
                // Particles lấp lánh
                this.velocityX = (random.nextFloat() - 0.5f) * 1;
                this.velocityY = (random.nextFloat() - 0.5f) * 1;
                this.gravity = 0;
                this.color = new Color(255, 255, 255); // Trắng
                this.maxLife = 20 + random.nextInt(15);
                this.size = 1 + random.nextFloat() * 2;
                break;
        }
        
        this.life = maxLife;
        this.alpha = 1.0f;
    }
    
    public void update() {
        // Cập nhật vị trí
        x += velocityX;
        y += velocityY;
        
        // Áp dụng trọng lực
        velocityY += gravity;
        
        // Giảm tuổi thọ
        life--;
        
        // Tính toán độ trong suốt dựa trên tuổi thọ
        alpha = (float)life / maxLife;
        
        // Giảm kích thước theo thời gian (tùy chọn)
        if (type == ParticleType.EXPLOSION || type == ParticleType.DEATH) {
            size *= 0.98f;
        }
    }
    
    public void draw(Graphics2D g2d) {
        // Lưu trạng thái gốc
        Composite originalComposite = g2d.getComposite();
        
        // Thiết lập độ trong suốt
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        
        // Vẽ particle
        g2d.setColor(color);
        
        switch (type) {
            case EXPLOSION:
            case DEATH:
                // Vẽ hình tròn
                g2d.fillOval((int)(x - size/2), (int)(y - size/2), (int)size, (int)size);
                break;
                
            case TRAIL:
                // Vẽ hình tròn nhỏ
                g2d.fillOval((int)(x - size/2), (int)(y - size/2), (int)size, (int)size);
                break;
                
            case SPARKLE:
                // Vẽ hình sao nhỏ
                drawStar(g2d, (int)x, (int)y, (int)size);
                break;
        }
        
        // Khôi phục trạng thái gốc
        g2d.setComposite(originalComposite);
    }
    
    private void drawStar(Graphics2D g2d, int centerX, int centerY, int size) {
        // Vẽ hình sao đơn giản (dấu +)
        g2d.drawLine(centerX - size, centerY, centerX + size, centerY);
        g2d.drawLine(centerX, centerY - size, centerX, centerY + size);
    }
    
    public boolean isAlive() {
        return life > 0 && alpha > 0.01f;
    }
    
    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public ParticleType getType() { return type; }
}