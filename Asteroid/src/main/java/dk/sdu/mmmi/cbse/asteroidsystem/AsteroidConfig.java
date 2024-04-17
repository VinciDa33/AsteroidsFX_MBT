package dk.sdu.mmmi.cbse.asteroidsystem;

import java.util.Random;

public class AsteroidConfig {
    public static double getRadius(int asteroidSize) {
        if (asteroidSize >= 3)
            return 40d;
        else if (asteroidSize == 2)
            return 25d;
        return 15d;
    }

    public static double getSpeed(int asteroidSize) {
        Random random = new Random();

        if (asteroidSize >= 3)
            return 60 + random.nextDouble(20, 50);
        else if (asteroidSize == 2)
            return 60 + random.nextDouble(40, 60);
        return 60 + random.nextDouble(50, 70);
    }
}
