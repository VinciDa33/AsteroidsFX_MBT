package dk.sdu.mmmi.cbse.common.entitysegments;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;

public class OnScreenSegment implements EntitySegment{

    private Entity entity;
    private Vector displaySize;

    public OnScreenSegment(GameData gameData, Entity entity) {
        this.entity = entity;
        this.displaySize = gameData.getDisplaySize();
    }

    @Override
    public void process(GameData gameData, Entity entity) {

    }

    private boolean checkOnScreen() {
        if (!entity.hasSegment(TransformSegment.class))
            return false;

        TransformSegment transform = entity.getSegment(TransformSegment.class);

        if (transform.getPosition().x < 0 && transform.getPosition().x > displaySize.x)
            return false;
        if (transform.getPosition().y < 0 && transform.getPosition().y > displaySize.y)
            return false;

        return true;
    }

    private boolean checkColliderOnScreen() {
        if (!entity.hasSegment(TransformSegment.class))
            return false;
        if (!entity.hasSegment(CircleColliderSegment.class))
            return false;

        TransformSegment transform = entity.getSegment(TransformSegment.class);
        CircleColliderSegment collider = entity.getSegment(CircleColliderSegment.class);

        if (transform.getPosition().x - collider.getRadius() < 0 && transform.getPosition().x + collider.getRadius() > displaySize.x)
            return false;
        if (transform.getPosition().y - collider.getRadius() < 0 && transform.getPosition().y + collider.getRadius() > displaySize.y)
            return false;

        return true;
    }

    public boolean isOnScreen() {
        return checkOnScreen();
    }

    public boolean isColliderOnScreen() {
        return checkColliderOnScreen();
    }
}
