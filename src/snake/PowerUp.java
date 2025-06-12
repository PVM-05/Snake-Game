package snake;

import java.awt.Color;

public class PowerUp {
    public enum Type {
        SPEED_BOOST, DOUBLE_SCORE, SHIELD
    }
    
    private int x, y;
    private Type type;
    private long spawnTime;
    private static final long DURATION = 10000; // 10 giây
    
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
    public static long getDuration() { return DURATION; }
    
    public Color getColor() {
        switch (type) {
            case SPEED_BOOST:
                return Color.BLUE;
            case DOUBLE_SCORE:
                return Color.YELLOW;
            case SHIELD:
                return Color.GREEN;
            default:
                return Color.WHITE;
        }
    }
    
    public String getSymbol() {
        switch (type) {
            case SPEED_BOOST:
                return "S";
            case DOUBLE_SCORE:
                return "D";
            case SHIELD:
                return "H";
            default:
                return "?";
        }
    }
}