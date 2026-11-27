package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// Tout ce qui existe dans le monde : un rectangle en coordonnées du monde (pas de l'écran)
// update() = logique seulement, draw() = affichage seulement
public abstract class ObjetJeu {
    protected double x;
    protected double y;
    protected final double largeur;
    protected final double hauteur;

    public ObjetJeu(double x, double y, double largeur, double hauteur) {
        this.x = x;
        this.y = y;
        this.largeur = largeur;
        this.hauteur = hauteur;
    }

    public abstract void update(double dt);

    public abstract void draw(GraphicsContext gc, Camera camera);

    // Place l'image à l'écran en tenant compte de la caméra
    public void dessinerImage(GraphicsContext gc, Camera camera, Image image) {
        gc.drawImage(image, camera.versEcranX(x), y, largeur, hauteur);
    }

    public Rectangle2D getRectangle() {
        return new Rectangle2D(x, y, largeur, hauteur);
    }

    public Point2D getCentre() {
        return new Point2D(x + largeur / 2, y + hauteur / 2);
    }

    public boolean enCollision(ObjetJeu autre) {
        return getRectangle().intersects(autre.getRectangle());
    }

    // Contour jaune du rectangle de collision
    public void drawDebug(GraphicsContext gc, Camera camera) {
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(1);
        gc.strokeRect(camera.versEcranX(x), y, largeur, hauteur);
    }

    public double getX() { return x; }

    public double getLargeur() { return largeur; }

}

