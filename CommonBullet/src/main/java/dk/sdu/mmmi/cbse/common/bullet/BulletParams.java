package dk.sdu.mmmi.cbse.common.bullet;

import dk.sdu.mmmi.cbse.common.data.Vector;

public class BulletParams {
    public double speed;
    public double radius;
    public Vector direction;

    public BulletParams(Vector direction, double speed, double radius) {
        this.direction = direction;
        this.speed = speed;
        this.radius = radius;
    }
}
