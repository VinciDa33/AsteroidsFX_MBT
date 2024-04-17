package dk.sdu.mmmi.cbse.asteroidsystem;

import dk.sdu.mmmi.cbse.common.data.Entity;

public class Asteroid extends Entity {
    private int asteroidSize;
    private boolean active = false;
    private double spawnDelay;
    private double spawnTimestamp;

    public int getAsteroidSize() {
        return asteroidSize;
    }
    public void setAsteroidSize(int asteroidSize) {
        this.asteroidSize = asteroidSize;
    }
    public double getSpawnDelay() {
        return spawnDelay;
    }
    public void setSpawnDelay(double spawnDelay) {
        this.spawnDelay = spawnDelay;
    }
    public double getSpawnTimestamp() {
        return spawnTimestamp;
    }
    public void setSpawnTimestamp(double spawnTimestamp) {
        this.spawnTimestamp = spawnTimestamp;
    }

    public boolean isActive () {
        return active;
    }
    public void setActive (boolean b) {
        this.active = b;
    }
}