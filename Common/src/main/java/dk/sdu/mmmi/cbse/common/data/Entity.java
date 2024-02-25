package dk.sdu.mmmi.cbse.common.data;

import java.io.Serializable;
import java.util.UUID;

public class Entity implements Serializable {

    private final UUID ID = UUID.randomUUID();
    
    private double[] polygonCoordinates;
    private Vector position;
    private Vector velocity;
    private double rotation;
    private double speed;
    private double rotationSpeed;
    private int[] rgb = new int[3];

    private boolean deletionFlag = false;

    public String getID() {
        return ID.toString();
    }


    public void setPolygonCoordinates(double... coordinates ) {
        this.polygonCoordinates = coordinates;
    }

    public void setPolygonArray(double[] arr) {
        this.polygonCoordinates = arr;
    }
    public double[] getPolygonCoordinates() {
        return polygonCoordinates;
    }
       

    public void setPosition(Vector newPosition) {
        position = newPosition;
    }
    public Vector getPosition() {
        return position;
    }

    public void setVelocity(Vector newVelocity) {
        velocity = newVelocity;
    }
    public Vector getVelocity() {
        return velocity;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getRotation() {
        return rotation;
    }

    public void setSpeed(double speed) {this.speed = speed;}
    public double getSpeed() {return speed;}

    public void setRotationSpeed(double rotationSpeed) {this.rotationSpeed = rotationSpeed;}
    public double getRotationSpeed() {return rotationSpeed;}

    public void setColor(int r, int g, int b) {
        rgb[0] = r;
        rgb[1] = g;
        rgb[2] = b;
    }

    public void setColor(int[] color) {
        if (color.length == 3)
            rgb = color;
    }
    public int[] getColor() {
        return rgb;
    }

    public void setDeletionFlag(boolean b) {
        deletionFlag = b;
    }
    public boolean getDeletionFlag() {
        return deletionFlag;
    }
}
