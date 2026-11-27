package ca.qc.bdeb.sim.gontrandlefletan;

import java.util.Random;

// Raccourcis pour tirer des nombres au hasard
public class Aleatoire {
    private static final Random RANDOM = new Random();

    private Aleatoire() {}

    // max exclu
    public static double entre(double min, double max) {
        return min + RANDOM.nextDouble() * (max - min);
    }

    // min et max inclus
    public static int entier(int min, int max) {
        return min + RANDOM.nextInt(max - min + 1);
    }


    public static int signe() {
        if (RANDOM.nextBoolean()) {
            return 1;
        }
        return -1;
    }
}

