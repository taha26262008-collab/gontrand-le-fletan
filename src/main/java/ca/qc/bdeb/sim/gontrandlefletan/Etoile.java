package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.geometry.Point2D;

// Projectile de départ : tout droit vers la droite

public class Etoile extends Projectile {
    private static final double VITESSE = 800;   // px/s

    public Etoile(Point2D centre) {
        super(centre, 36, 35, "etoile.png");
        vx = VITESSE;
    }

    @Override
    public void update(double dt) {
        updatePhysique(dt);
    }
}
