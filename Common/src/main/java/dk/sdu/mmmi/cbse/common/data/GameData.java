package dk.sdu.mmmi.cbse.common.data;

public class GameData {

    private int displayWidth  = 800 ;
    private int displayHeight = 800;
    private Vector displaySize = new Vector(800, 800);
    private final GameKeys keys = new GameKeys();


    private long delta;
    private double deltaSec;
    private double time;


    private int score;

    public GameKeys getKeys() {
        return keys;
    }

    public void setDisplaySize(Vector size) {
        this.displaySize = size;
    }
    public Vector getDisplaySize() {
        return displaySize;
    }

    public void setDelta(long delta) {
        this.delta = delta;
        this.deltaSec = delta * 0.001f;
        this.time += deltaSec;
    }
    public long getDelta() {
        return delta;
    }
    public double getDeltaSec() {
        return deltaSec;
    }
    public double getTime() {
        return time;
    }

    public int getScore() {
        return score;
    }
    public void setScore(int score) {
        this.score = score;
    }
    public void addScore(int amount) {
        this.score += amount;
    }
}
