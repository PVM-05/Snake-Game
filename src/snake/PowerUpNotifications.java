package snake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.Serializable;

public class PowerUpNotifications implements Serializable {
    private static final long serialVersionUID = 1L;

    private String message;
    private long startTime;
    private static final long DURATION = 3000; // 3 giây

    public PowerUpNotifications(String message) {
        this.message = message;
        this.startTime = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - startTime > DURATION;
    }

    public void draw(Graphics2D g2d, int screenWidth, int y) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        int x = (screenWidth - g2d.getFontMetrics().stringWidth(message)) / 2;
        g2d.drawString(message, x, y);
    }
}