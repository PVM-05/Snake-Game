package snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

public class PowerUpNotifications {
    private String message;
    private long startTime;
    private static final long DURATION = 2000; // Hiển thị 2 giây
    private static final long FADE_DURATION = 500; // Thời gian mờ dần (0.5 giây)

    public PowerUpNotifications(String message) {
        this.message = message;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > DURATION;
    }

    public void draw(Graphics2D g2d, int screenWidth, int yPos) {
        long elapsed = System.currentTimeMillis() - startTime;
        float alpha = 1.0f;

        // Tính độ trong suốt cho hiệu ứng mờ dần
        if (elapsed > DURATION - FADE_DURATION) {
            alpha = 1.0f - ((float)(elapsed - (DURATION - FADE_DURATION)) / FADE_DURATION);
        } else if (elapsed < FADE_DURATION) {
            alpha = (float)elapsed / FADE_DURATION;
        }

        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        FontMetrics metrics = g2d.getFontMetrics();
        int textWidth = metrics.stringWidth(message);
        int x = (screenWidth - textWidth) / 2;
        
        // Vẽ nền mờ
        g2d.setColor(new Color(0, 0, 0, (int)(alpha * 100)));
        g2d.fillRect(x - 10, yPos - 20, textWidth + 20, 30);
        
        // Vẽ văn bản
        g2d.setColor(new Color(255, 255, 255, (int)(alpha * 255)));
        g2d.drawString(message, x, yPos);
    }
}