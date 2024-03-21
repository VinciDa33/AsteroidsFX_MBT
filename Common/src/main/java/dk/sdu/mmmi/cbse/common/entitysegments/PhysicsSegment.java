package dk.sdu.mmmi.cbse.common.entitysegments;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;

public class PhysicsSegment implements EntitySegment{

    private Vector velocity;
    private double speed;
    private double rotationSpeed;

    public void setVelocity(Vector newVelocity) {
        velocity = newVelocity;
    }
    public Vector getVelocity() {
        return velocity;
    }


    public void setSpeed(double speed) {this.speed = speed;}
    public double getSpeed() {return speed;}

    public void setRotationSpeed(double rotationSpeed) {this.rotationSpeed = rotationSpeed;}
    public double getRotationSpeed() {return rotationSpeed;}
    @Override
    public void process(GameData gameData, Entity entity) {

    }
}
