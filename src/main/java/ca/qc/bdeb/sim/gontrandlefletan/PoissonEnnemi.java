package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// Arrive de la droite et accélère vers la gauche. Chargé négativement (voir ChampElectrique)
public class PoissonEnnemi extends ObjetMobile {
    public static final double CHARGE = -900;   // C

    private static final double ACCELERATION_X = -500;   // px/s²
    private static final double RATIO_LARGEUR = 120.0 / 104.0;   // proportions des images originales

    private final Image image;

    // x et y : coin supérieur gauche. La largeur garde les proportions de l'image
    public PoissonEnnemi(double x, double y, double hauteur, int niveau) {
        super(x, y, hauteur * RATIO_LARGEUR, hauteur);
        image = Images.get("poisson" + Aleatoire.entier(1, 5) + ".png");

        vx = -(100 * Math.pow(niveau, 0.33) + 200);
        vy = Aleatoire.entre(0, 100) * Aleatoire.signe();
        ax = ACCELERATION_X;
    }


    public static PoissonEnnemi aleatoire(int niveau, double x) {
        double hauteurJeu = Constantes.HAUTEUR_FENETRE;
        double y = Aleatoire.entre(hauteurJeu / 5, 4 * hauteurJeu / 5);
        double hauteur = Aleatoire.entre(50, 120);
        return new PoissonEnnemi(x, y, hauteur, niveau);
    }

    @Override
    public void update(double dt) {
        updatePhysique(dt);
    }

    @Override
    public void draw(GraphicsContext gc, Camera camera) {
        dessinerImage(gc, camera, image);
    }

    // À gauche de la caméra ou hors de l'écran en haut/bas : il ne reviendra jamais
    public boolean estSortiDuJeu(Camera camera) {
        return x + largeur < camera.getX()
                || y + hauteur < 0
                || y > Constantes.HAUTEUR_FENETRE;
    }
}

