package dk.sdu.mmmi.cbse.common.entitysegments;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;

public class RigidbodySegment implements EntitySegment{

    private TransformSegment transform;
    private Vector velocity;
    private double rotationSpeed;
    private boolean lockRotationToVelocity = false;

    public RigidbodySegment(Entity entity) {
        if (entity.getSegment(TransformSegment.class) == null) {
            transform = new TransformSegment();
            entity.addSegment(transform);
            return;
        }
        transform = entity.getSegment(TransformSegment.class);
    }

    public void setVelocity(Vector newVelocity) {
        velocity = newVelocity;
    }
    public Vector getVelocity() {
        return velocity;
    }

    public void setRotationSpeed(double rotationSpeed) {this.rotationSpeed = rotationSpeed;}
    public double getRotationSpeed() {return rotationSpeed;}
    public void rotate(GameData gameData, double multiplier) {
        velocity.rotate(multiplier * rotationSpeed * gameData.getDeltaSec());
        if (lockRotationToVelocity)
            this.setRotation(velocity.toAngle());
    }

    public void setRotationLock(boolean value) {
        lockRotationToVelocity = value;
    }
    public boolean getRotationLock() {
        return lockRotationToVelocity;
    }

    public void setPosition(Vector newPosition) {
        transform.setPosition(newPosition);
    }

    public Vector getPosition() {
        return transform.getPosition();
    }

    public void setRotation(double rotation) {
        transform.setRotation(rotation);
    }

    public double getRotation() {
        return transform.getRotation();
    }

    @Override
    public void process(GameData gameData, Entity entity) {
        transform.getPosition().add(velocity.multiplied(gameData.getDeltaSec()));
        if (lockRotationToVelocity)
            this.setRotation(velocity.toAngle());
    }
}
