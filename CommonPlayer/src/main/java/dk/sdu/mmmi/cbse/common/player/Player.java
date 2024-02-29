package dk.sdu.mmmi.cbse.common.player;

import dk.sdu.mmmi.cbse.common.data.Entity;

/**
 *
 * @author Emil
 */
public class Player extends Entity {
    private double fireTimer = 0;
    private double fireCooldown = 0;

    public void tickFireTimer(double delta) {
        if (fireTimer > fireCooldown)
            return;
        this.fireTimer += delta;
    }
    public double getFireTimer() {
        return fireTimer;
    }
    public void setFireCooldown(double fireCooldown) {
        this.fireCooldown = fireCooldown;
    }
    public double getFireCooldown() {
        return fireCooldown;
    }

    public boolean canFire() {
        if (fireTimer >= fireCooldown) {
            fireTimer -= fireCooldown;
            return true;
        }
        return false;
    }
}
