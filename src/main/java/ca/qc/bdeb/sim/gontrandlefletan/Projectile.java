package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// Son centre commence au centre de Gontrand. Il est retiré quand il sort de l'écran
public abstract class Projectile extends ObjetMobile {
    private final Image image;

    public Projectile(Point2D centre, double largeur, double hauteur, String nomImage) {
        super(centre.getX() - largeur / 2, centre.getY() - hauteur / 2, largeur, hauteur);
        image = Images.get(nomImage);
    }

    @Override
    public void draw(GraphicsContext gc, Camera camera) {

        dessinerImage(gc, camera, image);
    }


    // Rien par défaut (voir BoiteSardines)
    public void drawDebugChamp(GraphicsContext gc, Camera camera) {
    }
}