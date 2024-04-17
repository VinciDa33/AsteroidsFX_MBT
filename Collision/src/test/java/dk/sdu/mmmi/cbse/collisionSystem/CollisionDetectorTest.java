package dk.sdu.mmmi.cbse.collisionSystem;

import dk.sdu.mmmi.cbse.common.data.*;
import dk.sdu.mmmi.cbse.common.entitysegments.CircleColliderSegment;
import dk.sdu.mmmi.cbse.common.entitysegments.RigidbodySegment;
import dk.sdu.mmmi.cbse.common.events.CollisionEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CollisionDetectorTest {
    static Entity e1;
    static Entity e2;
    static GameData data;
    static World world;

    @BeforeAll
    static void init() {
        data = new GameData();
        world = new World();

        e1 = new Entity();
        e1.setTag(EntityTag.UNTAGGED);

        RigidbodySegment rigidbody1 = new RigidbodySegment(e1);
        rigidbody1.setPosition(new Vector(0, 0));
        e1.addSegment(rigidbody1);

        CircleColliderSegment collider1 = new CircleColliderSegment();
        collider1.setRadius(10);
        collider1.addCollisionEvent(EntityTag.UNTAGGED, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                world.removeEntity(e1);
            }
        });
        e1.addSegment(collider1);

        world.addEntity(e1);

        e2 = new Entity();
        e2.setTag(EntityTag.UNTAGGED);

        RigidbodySegment rigidbody2 = new RigidbodySegment(e2);
        rigidbody2.setPosition(new Vector(0, 0));
        e2.addSegment(rigidbody2);

        CircleColliderSegment collider2 = new CircleColliderSegment();
        collider2.setRadius(10);
        collider2.addCollisionEvent(EntityTag.UNTAGGED, new CollisionEvent() {
            @Override
            public void onCollision(Entity other) {
                world.removeEntity(e2);
            }
        });
        e2.addSegment(collider2);

        world.addEntity(e2);
    }

    @BeforeEach
    void setUp() {
        world.addEntity(e1);
        world.addEntity(e2);

        RigidbodySegment rigidbody1 = e1.getSegment(RigidbodySegment.class);
        rigidbody1.setPosition(new Vector(0, 0));

        RigidbodySegment rigidbody2 = e2.getSegment(RigidbodySegment.class);
        rigidbody2.setPosition(new Vector(0, 0));

        rigidbody1.setVelocity(new Vector(0, 0));
        rigidbody2.setVelocity(new Vector(0, 0));

        CircleColliderSegment collider1 = e1.getSegment(CircleColliderSegment.class);
        collider1.setRadius(10);

        CircleColliderSegment collider2 = e2.getSegment(CircleColliderSegment.class);
        collider2.setRadius(10);
    }

    @AfterEach
    void tearDown() {
        world.removeEntity(e1);
        world.removeEntity(e2);
    }

    @Test
    void collisionTest() {
        CollisionDetector cd = new CollisionDetector();
        cd.process(data, world);
        assertTrue(world.getEntity(e1.getID()) == null && world.getEntity(e2.getID()) == null);
    }
    @Test
    void nonCollisionTest() {
        RigidbodySegment rigidbody2 = e2.getSegment(RigidbodySegment.class);
        rigidbody2.setPosition(new Vector(100, 100));

        CollisionDetector cd = new CollisionDetector();
        cd.process(data, world);
        assertTrue(world.getEntity(e1.getID()) != null && world.getEntity(e2.getID()) != null);
    }
    @Test
    void movingCollisionTest() {
        RigidbodySegment rigidbody1 = e1.getSegment(RigidbodySegment.class);
        rigidbody1.setVelocity(new Vector(10, 0));

        RigidbodySegment rigidbody2 = e2.getSegment(RigidbodySegment.class);
        rigidbody2.setPosition(new Vector(25, 0));

        CollisionDetector cd = new CollisionDetector();
        cd.process(data, world);
        assertTrue(world.getEntity(e1.getID()) != null && world.getEntity(e2.getID()) != null);

        rigidbody1.getPosition().add(rigidbody1.getVelocity());

        cd.process(data, world);
        assertTrue(world.getEntity(e1.getID()) == null && world.getEntity(e2.getID()) == null);
    }
    @Test
    void largeCollisionTest() {
        CircleColliderSegment collider1 = e1.getSegment(CircleColliderSegment.class);
        collider1.setRadius(100);

        RigidbodySegment rigidbody2 = e2.getSegment(RigidbodySegment.class);
        rigidbody2.setPosition(new Vector(100, 0));

        CollisionDetector cd = new CollisionDetector();
        cd.process(data, world);
        assertTrue(world.getEntity(e1.getID()) == null && world.getEntity(e2.getID()) == null);
    }

    @Test
    void onlyOneColliderTest() {
        world.removeEntity(e2);

        CollisionDetector cd = new CollisionDetector();
        cd.process(data, world);
        assertNotNull(world.getEntity(e1.getID()));
    }
}