package dk.sdu.mmmi.cbse.common.entitysegments;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.GameKeys;

public class ShootingSegment implements EntitySegment{
    private double fireTimer = 0;
    private double fireCooldown = 0;

    private void tickFireTimer(double delta) {
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

    @Override
    public void process(GameData gameData, Entity entity) {
        tickFireTimer(gameData.getDeltaSec());
    }
}
