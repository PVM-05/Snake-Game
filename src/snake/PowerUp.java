package snake;

import java.awt.Color;
import java.io.Serializable;

public class PowerUp implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type {
        SPEED_BOOST, DOUBLE_SCORE, SHIELD
    }

    private int x;
    private int y;
    private Type type;
    private long spawnTime;

    public PowerUp(int x, int y, Type type) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.spawnTime = System.currentTimeMillis();
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public Type getType() { return type; }
    public long getSpawnTime() { return spawnTime; }
    public static long getDuration() { return 10000; } // 10 giây

    public Color getColor() {
        switch (type) {
            case SPEED_BOOST: return Color.YELLOW;
            case DOUBLE_SCORE: return Color.BLUE;
            case SHIELD: return Color.MAGENTA;
            default: return Color.WHITE;
        }
    }
}