package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

import java.util.ArrayList;

// Couleur de fond au hasard + algues/coraux au fond, sur toute la largeur du niveau
public class Decor {
    private final Color couleurFond;
    private final ArrayList<ElementDecor> elements = new ArrayList<>();

    public Decor(double largeurNiveau, double hauteurEcran) {
        couleurFond = Color.hsb(Aleatoire.entre(190, 270), 0.84, 1.0);

        // Le bas de chaque image dépasse de 10px sous l'écran : elles semblent plantées dans le fond
        double y = hauteurEcran + 10 - ElementDecor.HAUTEUR;

        double x = Aleatoire.entre(0, 100);
        while (x < largeurNiveau) {
            String nomImage = "decor" + Aleatoire.entier(1, 6) + ".png";
            elements.add(new ElementDecor(x, y, nomImage));
            // Espace de 50 à 100px entre le côté droit de l'image précédente et la suivante
            x += ElementDecor.LARGEUR + Aleatoire.entre(50, 100);
        }
    }

    public void draw(GraphicsContext gc, Camera camera) {
        gc.setFill(couleurFond);
        gc.fillRect(0, 0, Constantes.LARGEUR_FENETRE, Constantes.HAUTEUR_FENETRE);

        for (ElementDecor element : elements) {
            if (camera.estVisible(element)) {
                element.draw(gc, camera);
            }
        }
    }
}


