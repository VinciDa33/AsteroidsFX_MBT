package dk.sdu.mmmi.cbse.common.data;

public class GameData {

    private int displayWidth  = 800 ;
    private int displayHeight = 800;
    private final GameKeys keys = new GameKeys();


    private long delta;
    private double deltaSec;


    public GameKeys getKeys() {
        return keys;
    }

    public void setDisplayWidth(int width) {
        this.displayWidth = width;
    }

    public int getDisplayWidth() {
        return displayWidth;
    }

    public void setDisplayHeight(int height) {
        this.displayHeight = height;
    }

    public int getDisplayHeight() {
        return displayHeight;
    }

    public void setDelta(long delta) {
        this.delta = delta;
        this.deltaSec = delta * 0.001f;
    }
    public long getDelta() {
        return delta;
    }
    public double getDeltaSec() {
        return deltaSec;
    }
}
