package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// Un seul par niveau. Le toucher change le projectile de Gontrand
public class Baril extends ObjetJeu {
    private static final double PERIODE = 3;   // secondes pour une oscillation complète

    private final Image imageFerme = Images.get("baril.png");
    private final Image imageOuvert = Images.get("baril-ouvert.png");

    private double temps = 0;   // temps total depuis le début du niveau
    private boolean utilise = false;

    public Baril(double x) {
        super(x, (Constantes.HAUTEUR_FENETRE - 83) / 2, 70, 83);
    }

    @Override
    public void update(double dt) {
        temps += dt;
        double moitie = (Constantes.HAUTEUR_FENETRE - hauteur) / 2;
        y = moitie * Math.sin(2 * Math.PI * temps / PERIODE) + moitie;
    }

    @Override
    public void draw(GraphicsContext gc, Camera camera) {
        Image image = imageFerme;
        if (utilise) {
            image = imageOuvert;
        }
        dessinerImage(gc, camera, image);
    }


    public void utiliser() {
        utilise = true;
    }

    public boolean estUtilise() {
        return utilise;
    }
}

