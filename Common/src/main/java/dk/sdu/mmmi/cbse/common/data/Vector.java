package dk.sdu.mmmi.cbse.common.data;

public class Vector {
    public double x;
    public double y;

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    //Multiplication
    public void multiply(double value) {
        this.x *= value;
        this.y *= value;
    }
    public Vector multiplied(double value) {
        return new Vector(x * value, y * value);
    }

    //Division
    public void divide(double value) {
        this.x /= value;
        this.y /= value;
    }
    public Vector divided(double value) {
        return new Vector(x / value, y / value);
    }

    //Addition
    public void add(Vector other) {
        this.x += other.x;
        this.y += other.y;
    }
    public Vector added(Vector other) {
        return new Vector(x + other.x, y + other.y);
    }

    //Retraction
    public void subtract(Vector other) {
        this.x -= other.x;
        this.y -= other.y;
    }
    public Vector subtracted(Vector other) {
        return new Vector(x - other.x, y - other.y);
    }


    public double magnitude() {
        return Math.sqrt(x * x + y * y);
    }

    public void normalize() {
        double mag = magnitude();

        if (mag == 0)
            return;

        x /= mag;
        y /= mag;
    }

    public Vector normalized() {
        double mag = magnitude();

        if (mag == 0)
            return new Vector(0, 0);

        return new Vector(x / mag, y / mag);
    }

    public static double distance(Vector v1, Vector v2) {
        double dx = v1.x - v2.x;
        double dy = v1.y - v2.y;

        return Math.sqrt(dx * dx + dy * dy);
    }

    //Vector rotation using 2D rotation matrix
    public void rotate(double angle) {
        double theta = Math.toRadians(angle);

        double cs = Math.cos(theta);
        double sn = Math.sin(theta);

        double px = x * cs - y * sn;
        double py = x * sn + y * cs;

        x = px;
        y = py;
    }

    public Vector rotated(double angle) {
        double theta = Math.toRadians(angle);

        double cs = Math.cos(theta);
        double sn = Math.sin(theta);

        double px = x * cs - y * sn;
        double py = x * sn + y * cs;

        return new Vector(px, py);
    }

    public void moveTowards(Vector target, double maxStep) {
        Vector deltaVector = target.subtracted(this).normalized();
        this.add(deltaVector.multiplied(maxStep));
    }

    public double toAngle() {
        return Math.toDegrees(Math.atan2(y, x));
    }

    public static double dot(Vector v1, Vector v2) {
        Vector vn1 = v1.normalized();
        Vector vn2 = v2.normalized();

        return vn1.x * vn2.x + vn1.y * vn2.y;
    }

    public Vector copy() {
        return new Vector(x, y);
    }

    @Override
    public String toString() {
        return x + " : " + y;
    }
}
