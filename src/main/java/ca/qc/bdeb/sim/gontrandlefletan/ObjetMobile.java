package ca.qc.bdeb.sim.gontrandlefletan;

// Bouge selon sa vitesse et son accélération (px/s et px/s²)
public abstract class ObjetMobile extends ObjetJeu {
    protected double vx;
    protected double vy;
    protected double ax;
    protected double ay;

    public ObjetMobile(double x, double y, double largeur, double hauteur) {
        super(x, y, largeur, hauteur);
    }


    public void updateVitesse(double dt) {
        vx += ax * dt;
        vy += ay * dt;
    }


    public void updatePosition(double dt) {
        x += vx * dt;
        y += vy * dt;
    }


    public void updatePhysique(double dt) {
        updateVitesse(dt);
        updatePosition(dt);
    }
}
