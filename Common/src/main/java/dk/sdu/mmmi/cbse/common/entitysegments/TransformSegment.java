package dk.sdu.mmmi.cbse.common.entitysegments;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;

public class TransformSegment implements EntitySegment{

    private Vector position;
    private double rotation;

    public void setPosition(Vector newPosition) {
        position = newPosition;
    }
    public Vector getPosition() {
        return position;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }

    public double getRotation() {
        return rotation;
    }
    @Override
    public void process(GameData gameData, Entity entity) {

    }
}
