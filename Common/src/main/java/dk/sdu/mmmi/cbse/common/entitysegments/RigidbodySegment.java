package dk.sdu.mmmi.cbse.common.entitysegments;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;

public class RigidbodySegment extends TransformSegment {

    private Vector velocity;
    private double rotationSpeed;
    private boolean lockRotationToVelocity = false;


    public void setVelocity(Vector newVelocity) {
        velocity = newVelocity;
        if (lockRotationToVelocity)
            setRotation(velocity.toAngle());
    }
    public Vector getVelocity() {
        return velocity;
    }

    public void setRotationSpeed(double rotationSpeed) {this.rotationSpeed = rotationSpeed;}
    public double getRotationSpeed() {return rotationSpeed;}
    public void rotate(GameData gameData, double multiplier) {
        velocity.rotate(multiplier * rotationSpeed * gameData.getDeltaSec());
        if (lockRotationToVelocity)
            setRotation(velocity.toAngle());
    }

    public void setRotationLock(boolean value) {
        lockRotationToVelocity = value;
    }
    public boolean getRotationLock() {
        return lockRotationToVelocity;
    }

    @Override
    public void process(GameData gameData, Entity entity) {
        this.getPosition().add(velocity);
    }
}
