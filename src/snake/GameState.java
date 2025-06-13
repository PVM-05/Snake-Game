package snake;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private int[] x;
    private int[] y;
    private int bodyParts;
    private int applesEaten;
    private int appleX;
    private int appleY;
    private char direction;
    private boolean running;
    private int currentMoveDelay;
    private List<PowerUp> powerUps;
    private boolean doubleScoreActive;
    private long doubleScoreEndTime;
    private boolean shieldActive;

    public GameState(int[] x, int[] y, int bodyParts, int applesEaten, int appleX, int appleY,
                    char direction, boolean running, int currentMoveDelay, List<PowerUp> powerUps,
                    boolean doubleScoreActive, long doubleScoreEndTime, boolean shieldActive) {
        this.x = x.clone(); // Sao chép mảng
        this.y = y.clone();
        this.bodyParts = bodyParts;
        this.applesEaten = applesEaten;
        this.appleX = appleX;
        this.appleY = appleY;
        this.direction = direction;
        this.running = running;
        this.currentMoveDelay = currentMoveDelay;
        this.powerUps = new ArrayList<>(powerUps);
        this.doubleScoreActive = doubleScoreActive;
        this.doubleScoreEndTime = doubleScoreEndTime;
        this.shieldActive = shieldActive;
    }

    public int[] getX() { return x.clone(); }
    public int[] getY() { return y.clone(); }
    public int getBodyParts() { return bodyParts; }
    public int getApplesEaten() { return applesEaten; }
    public int getAppleX() { return appleX; }
    public int getAppleY() { return appleY; }
    public char getDirection() { return direction; }
    public boolean isRunning() { return running; }
    public int getCurrentMoveDelay() { return currentMoveDelay; }
    public List<PowerUp> getPowerUps() { return new ArrayList<>(powerUps); }
    public boolean isDoubleScoreActive() { return doubleScoreActive; }
    public long getDoubleScoreEndTime() { return doubleScoreEndTime; }
    public boolean isShieldActive() { return shieldActive; }
}