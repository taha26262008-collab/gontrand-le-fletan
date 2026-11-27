package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

import java.util.ArrayList;

// Chargée positivement : attirée par les poissons (négatifs) selon le champ électrique
public class BoiteSardines extends Projectile {
    private static final double CHARGE = -PoissonEnnemi.CHARGE;   // +900 C, signe opposé aux poissons
    private static final double MASSE = 1;
    private static final double VITESSE_MAX = 400;   // px/s

    // Même liste que dans Jeu : elle se met à jour toute seule
    private final ArrayList<PoissonEnnemi> poissons;

    public BoiteSardines(Point2D centre, ArrayList<PoissonEnnemi> poissons) {
        super(centre, 35, 29, "sardines.png");
        this.poissons = poissons;
    }

    @Override
    public void update(double dt) {
        // Champ au centre de la boîte
        Point2D champ = ChampElectrique.calculer(poissons, getCentre());

        // a = F / m
        Point2D force = champ.multiply(CHARGE);
        Point2D acceleration = force.multiply(1 / MASSE);
        ax = acceleration.getX();
        ay = acceleration.getY();

        // L'accélération change la vitesse (limitée), qui change la position
        updateVitesse(dt);
        limiterVitesse();
        updatePosition(dt);

        rebondirSurLeHautEtLeBas();
    }

    private void limiterVitesse() {
        Point2D vitesse = new Point2D(vx, vy);
        if (vitesse.magnitude() > VITESSE_MAX) {
            vitesse = vitesse.normalize().multiply(VITESSE_MAX);
            vx = vitesse.getX();
            vy = vitesse.getY();
        }
    }

    private void rebondirSurLeHautEtLeBas() {
        if (y < 0) {
            y = 0;
            vy = Math.abs(vy);
        } else if (y + hauteur > Constantes.HAUTEUR_FENETRE) {
            y = Constantes.HAUTEUR_FENETRE - hauteur;
            vy = -Math.abs(vy);
        }
    }

    public Point2D getAcceleration() {
        return new Point2D(ax, ay);
    }

    // Mode champ (touche F) : dessine l'accélération de la boîte
    @Override
    public void drawDebugChamp(GraphicsContext gc, Camera camera) {
        UtilitairesDessins.dessinerVecteurForce(
                camera.versEcran(getCentre()),
                getAcceleration(),
                gc);
    }
}
