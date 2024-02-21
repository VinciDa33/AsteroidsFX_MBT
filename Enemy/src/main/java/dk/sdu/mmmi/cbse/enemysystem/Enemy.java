package dk.sdu.mmmi.cbse.enemysystem;

import dk.sdu.mmmi.cbse.common.data.Entity;

/**
 *
 * @author Emil
 */
public class Enemy extends Entity {
    private int currentAction = 0;
    private double actionTimer;
    private double actionCooldown;

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

    public boolean canFire() {
        if (fireTimer >= fireCooldown) {
            fireTimer -= fireCooldown;
            return true;
        }
        return false;
    }


    public void tickActionTimer(double delta) {
        actionTimer += delta;
    }
    public double getActionTimer() {
        return actionTimer;
    }
    public void setActionCooldown(double actionCooldown) {
        this.actionCooldown = actionCooldown;
    }

    public void setCurrentAction(int action) {
        this.currentAction = action;
    }
    public int getCurrentAction() {
        return currentAction;
    }

    public boolean canDoAction() {
        if (actionTimer > actionCooldown) {
            actionTimer = 0;
            return true;
        }
        return false;
    }
}
