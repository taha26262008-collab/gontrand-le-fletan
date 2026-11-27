package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.geometry.Point2D;

// Avance vers la droite en oscillant verticalement (sinus)
public class Hippocampe extends Projectile {
    private static final double VITESSE = 500;   // px/s

    private final double amplitude;   // px, positive ou négative
    private final double periode;     // secondes pour une oscillation complète
    private final double yInitial;    // centre de Gontrand au moment du lancer
    private double temps = 0;         // temps total depuis la création

    public Hippocampe(Point2D centre) {
        super(centre, 20, 36, "hippocampe.png");
        vx = VITESSE;
        amplitude = Aleatoire.entre(30, 60) * Aleatoire.signe();
        periode = Aleatoire.entre(1.0, 3.0);
        yInitial = centre.getY();
    }

    @Override
    public void update(double dt) {
        temps += dt;
        updatePhysique(dt);

        // y de la formule = centre de l'hippocampe, et notre y = haut du rectangle
        double centreY = amplitude * Math.sin(2 * Math.PI * temps / periode) + yInitial;
        y = centreY - hauteur / 2;
    }
}
