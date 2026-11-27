package ca.qc.bdeb.sim.gontrandlefletan;


import javafx.geometry.Point2D;
import javafx.scene.image.Image;

import java.util.ArrayList;

// Chaque sorte sait fabriquer les projectiles qu'elle lance
public enum TypeProjectile {
    ETOILE("etoile.png"),
    HIPPOCAMPES("hippocampe.png"),
    SARDINES("sardines.png");

    private final String nomIcone;

    TypeProjectile(String nomIcone) {
        this.nomIcone = nomIcone;
    }

    // poissons : seulement utilisé par les boîtes de sardines
    public ArrayList<Projectile> creer(Point2D centre, ArrayList<PoissonEnnemi> poissons) {
        ArrayList<Projectile> projectiles = new ArrayList<>();
        if (this == ETOILE) {
            projectiles.add(new Etoile(centre));
        } else if (this == HIPPOCAMPES) {
            projectiles.add(new Hippocampe(centre));
            projectiles.add(new Hippocampe(centre));
            projectiles.add(new Hippocampe(centre));
        } else {
            projectiles.add(new BoiteSardines(centre, poissons));
        }
        return projectiles;
    }

    public Image getIcone() {
        return Images.get(nomIcone);
    }

    // Utilisé quand Gontrand touche un baril
    public TypeProjectile autreAuHasard() {
        TypeProjectile[] valeurs = values();
        TypeProjectile choix;
        do {
            choix = valeurs[Aleatoire.entier(0, valeurs.length - 1)];
        } while (choix == this);
        return choix;
    }
}

