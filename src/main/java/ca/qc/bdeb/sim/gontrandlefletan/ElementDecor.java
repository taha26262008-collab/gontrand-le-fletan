package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// Algue ou corail : ni logique, ni collisions
public class ElementDecor extends ObjetJeu {
    public static final double LARGEUR = 80;
    public static final double HAUTEUR = 119;

    private final Image image;

    public ElementDecor(double x, double y, String nomImage) {
        super(x, y, LARGEUR, HAUTEUR);
        image = Images.get(nomImage);
    }

    @Override
    public void update(double dt) {
    }

    @Override
    public void draw(GraphicsContext gc, Camera camera) {
        dessinerImage(gc, camera, image);
    }
}