package snake;

import java.awt.Color;

public class Theme {
    public enum Type {
        CLASSIC, NEON, RETRO
    }
    
    private Type type;
    
    public Theme(Type type) {
        this.type = type;
    }
    
    public Color getSnakeHeadColor() {
        switch (type) {
            case CLASSIC: return Color.GREEN;
            case NEON: return Color.CYAN;
            case RETRO: return new Color(139, 69, 19); // Màu nâu
            default: return Color.GREEN;
        }
    }
    
    public Color getSnakeBodyColor(int index) {
        switch (type) {
            case CLASSIC: return new Color(45, Math.max(100, 180 - (index * 10)), 0);
            case NEON: return new Color(0, Math.max(100, 200 - (index * 10)), 200);
            case RETRO: return new Color(160, Math.max(100, 140 - (index * 10)), 82);
            default: return new Color(45, Math.max(100, 180 - (index * 10)), 0);
        }
    }
    
    public Color getAppleColor() {
        switch (type) {
            case CLASSIC: return Color.RED;
            case NEON: return Color.MAGENTA;
            case RETRO: return Color.ORANGE;
            default: return Color.RED;
        }
    }
    
    public Type getType() {
        return type;
    }
}