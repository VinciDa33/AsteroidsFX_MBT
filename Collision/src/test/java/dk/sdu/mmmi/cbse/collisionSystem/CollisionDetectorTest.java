package dk.sdu.mmmi.cbse.collisionSystem;

import dk.sdu.mmmi.cbse.common.data.Entity;
import dk.sdu.mmmi.cbse.common.data.GameData;
import dk.sdu.mmmi.cbse.common.data.Vector;
import dk.sdu.mmmi.cbse.common.data.World;
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
        e1.setPosition(new Vector(0, 0));
        e1.setRadius(10);
        world.addEntity(e1);

        e2 = new Entity();
        e2.setPosition(new Vector(0, 0));
        e2.setRadius(10);
        world.addEntity(e2);
    }

    @BeforeEach
    void setUp() {
        world.addEntity(e1);
        world.addEntity(e2);

        e1.setDeletionFlag(false);
        e2.setDeletionFlag(false);

        e1.setPosition(new Vector(0, 0));
        e2.setPosition(new Vector(0, 0));

        e1.setVelocity(new Vector(0, 0));
        e2.setVelocity(new Vector(0, 0));

        e1.setRadius(10);
        e2.setRadius(10);
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
        assertTrue(e1.getDeletionFlag() && e2.getDeletionFlag());
    }
    @Test
    void nonCollisionTest() {
        e2.setPosition(new Vector(100, 100));

        CollisionDetector cd = new CollisionDetector();
        cd.process(data, world);
        assertTrue(!e1.getDeletionFlag() && !e2.getDeletionFlag());
    }
    @Test
    void movingCollisionTest() {
        e1.setVelocity(new Vector(10, 0));
        e2.setPosition(new Vector(25, 0));

        CollisionDetector cd = new CollisionDetector();
        cd.process(data, world);
        assertTrue(!e1.getDeletionFlag() && !e2.getDeletionFlag());

        e1.getPosition().add(e1.getVelocity());

        cd.process(data, world);
        assertTrue(e1.getDeletionFlag() && e2.getDeletionFlag());
    }
    @Test
    void largeCollisionTest() {
        e1.setRadius(100);
        e2.setPosition(new Vector(100, 0));

        CollisionDetector cd = new CollisionDetector();
        cd.process(data, world);
        assertTrue(e1.getDeletionFlag() && e2.getDeletionFlag());
    }

    @Test
    void onlyOneColliderTest() {
        world.removeEntity(e2);

        CollisionDetector cd = new CollisionDetector();
        cd.process(data, world);
        assertFalse(e1.getDeletionFlag());
    }
}