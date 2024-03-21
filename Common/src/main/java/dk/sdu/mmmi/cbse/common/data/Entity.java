package dk.sdu.mmmi.cbse.common.data;

import dk.sdu.mmmi.cbse.common.entitysegments.EntitySegment;

import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Entity implements Serializable {

    private final UUID ID = UUID.randomUUID();
    private EntityTag tag = EntityTag.UNTAGGED;
    private Map<Class, EntitySegment> segments;
    private boolean deletionFlag = false;

    public Entity() {
        segments = new ConcurrentHashMap<>();
    }

    public void setTag(EntityTag tag) {
        this.tag = tag;
    }

    public EntityTag getTag() {
        return tag;
    }

    public String getID() {
        return ID.toString();
    }

    public void addSegment(EntitySegment segment) {
        segments.put(segment.getClass(), segment);
    }

    public void removeSegment(Class segmentClass) {
        segments.remove(segmentClass);
    }

    public <E extends EntitySegment> E getSegment(Class segmentClass) {
        return (E) segments.get(segmentClass);
    }

    public void setDeletionFlag(boolean b) {
        deletionFlag = b;
    }
    public boolean getDeletionFlag() {
        return deletionFlag;
    }
}
