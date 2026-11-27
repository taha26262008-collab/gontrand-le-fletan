package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.geometry.Point2D;

import java.util.ArrayList;

// Champ électrique créé par les poissons (particules au centre de leur image)
public class ChampElectrique {

    // Constante de Coulomb du jeu
    public static final double K = 200;

    // Évite la division par zéro : sous 1px, on utilise 1px
    private static final double DISTANCE_MIN = 1;

    private ChampElectrique() {
    }

    // Somme des champs de tous les poissons à une position du monde
// Les poissons sont négatifs : le champ pointe vers eux (ce qui attire les sardines)
    public static Point2D calculer(ArrayList<PoissonEnnemi> poissons, Point2D position) {
        Point2D total = Point2D.ZERO;

        for (PoissonEnnemi poisson : poissons) {
            Point2D poissonVersPoint = position.subtract(poisson.getCentre());
            double distance = Math.max(poissonVersPoint.magnitude(), DISTANCE_MIN);

            double module = K * Math.abs(PoissonEnnemi.CHARGE) / (distance * distance);
            Point2D direction = poissonVersPoint.normalize();
            // Charge négative : le champ va vers la particule, donc à l'inverse de la direction
            double signe = Math.signum(PoissonEnnemi.CHARGE);

            total = total.add(direction.multiply(module * signe));
        }
        return total;
    }
}