package ca.qc.bdeb.sim.gontrandlefletan;


import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

// Position (dans le monde) du côté gauche de l'écran. Suit Gontrand vers la droite, ne recule jamais
public class Camera {
    private static final double RATIO_SUIVI = 1.0 / 5.0;

    private final double largeurEcran;
    private final double hauteurEcran;
    private final double largeurNiveau;
    private double x = 0;

    public Camera(double largeurEcran, double hauteurEcran, double largeurNiveau) {
        this.largeurEcran = largeurEcran;
        this.hauteurEcran = hauteurEcran;
        this.largeurNiveau = largeurNiveau;
    }

    public double getX() {
        return x;
    }

    public void reinitialiser() {
        x = 0;
    }

    // Gontrand reste à 1/5 de l'écran, sauf au bout du niveau
    public void suivre(ObjetJeu cible) {
        double ligne = largeurEcran * RATIO_SUIVI;
        if (cible.getX() > x + ligne) {
            x = cible.getX() - ligne;
        }
        x = Math.min(x, largeurNiveau - largeurEcran);
    }

    public double versEcranX(double xMonde) {
        return xMonde - x;
    }

    public Point2D versEcran(Point2D positionMonde) {
        return new Point2D(versEcranX(positionMonde.getX()), positionMonde.getY());
    }

    public boolean estVisible(ObjetJeu objet) {
        Rectangle2D ecran = new Rectangle2D(x, 0, largeurEcran, hauteurEcran);
        return ecran.intersects(objet.getRectangle());
    }
}

