package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.geometry.VPos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

// Ce qui s'affiche fixe à l'écran, par-dessus le jeu (la caméra ne l'affecte pas)
public class AffichageFixe {
    private static final double MARGE = 10;
    private static final double LARGEUR_BARRE = 150;
    private static final double HAUTEUR_BARRE = 15;
    private static final double HAUTEUR_ICONE = 28;

    // Classe de méthodes static seulement, pas d'instances autorisées
    private AffichageFixe() {
    }

    // Barre remplie selon les vies restantes, puis l'icône du projectile actuel
    public static void dessinerBarreDeVie(GraphicsContext gc, int vies, TypeProjectile projectile) {
        gc.setFill(Color.WHITE);
        gc.fillRect(MARGE, MARGE, LARGEUR_BARRE * vies / Gontrand.VIES_MAX, HAUTEUR_BARRE);

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.strokeRect(MARGE, MARGE, LARGEUR_BARRE, HAUTEUR_BARRE);

        Image icone = projectile.getIcone();
        // La largeur suit les proportions de l'image
        double largeurIcone = icone.getWidth() / icone.getHeight() * HAUTEUR_ICONE;
        double yIcone = MARGE + HAUTEUR_BARRE / 2 - HAUTEUR_ICONE / 2;
        gc.drawImage(icone, MARGE + LARGEUR_BARRE + MARGE, yIcone, largeurIcone, HAUTEUR_ICONE);
    }

    // Texte du débogage : sous la barre de vie
    public static void dessinerInfosDebug(GraphicsContext gc, int nbPoissons, int nbProjectiles, double pourcentageNiveau) {
        // save/restore : les réglages du texte ne doivent pas affecter le reste du dessin
        gc.save();
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("System", 11));
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setTextBaseline(VPos.TOP);

        double y = MARGE + HAUTEUR_BARRE + 8;
        gc.fillText("NB Poissons: " + nbPoissons, MARGE, y);
        gc.fillText("NB Projectiles: " + nbProjectiles, MARGE, y + 14);
        gc.fillText(String.format("Position Gontrand: %.2f%%", pourcentageNiveau), MARGE, y + 28);
        gc.restore();
    }


    public static void dessinerTexteCentre(GraphicsContext gc, String texte, Color couleur,
                                           double taille, FontWeight graisse) {
        gc.save();
        gc.setFill(couleur);
        gc.setFont(Font.font("System", graisse, taille));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setTextBaseline(VPos.CENTER);
        gc.fillText(texte, Constantes.LARGEUR_FENETRE / 2, Constantes.HAUTEUR_FENETRE / 2);
        gc.restore();
    }

   // Noir à 40% d'opacité
    public static void assombrir(GraphicsContext gc) {
        gc.save();
        gc.setFill(Color.rgb(0, 0, 0, 0.4));
        gc.fillRect(0, 0, Constantes.LARGEUR_FENETRE, Constantes.HAUTEUR_FENETRE);
        gc.restore();
    }
}

