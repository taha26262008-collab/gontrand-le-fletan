package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

import java.util.ArrayList;

// La logique (update) et l'affichage (draw), sans composant JavaFX
public class Jeu {
    private static final double DUREE_AFFICHAGE_NIVEAU = 4;   // secondes
    private static final double DUREE_FIN_DE_PARTIE = 3;      // secondes
    private static final double DELAI_ENTRE_TIRS = 0.5;       // secondes
    private static final double PAS_GRILLE_CHAMP = 50;        // px, pour la visualisation du champ

    private final Clavier clavier;

    private final Camera camera = new Camera(
            Constantes.LARGEUR_FENETRE, Constantes.HAUTEUR_FENETRE, Constantes.LARGEUR_NIVEAU);
    private final Gontrand gontrand = new Gontrand();
    private final ArrayList<PoissonEnnemi> poissons = new ArrayList<>();
    private final ArrayList<Projectile> projectiles = new ArrayList<>();

    private Decor decor;
    private Baril baril;
    private TypeProjectile typeProjectile = TypeProjectile.ETOILE;

    private int niveau;
    private double tempsNiveau;
    private double tempsDepuisGroupe;
    private double delaiTir;

    private boolean enPause;
    private boolean modeDebug;
    private boolean modeChamp;
    private boolean partieTerminee;
    private double tempsFinPartie;

    public Jeu(Clavier clavier) {
        this.clavier = clavier;
        demarrerNiveau(1);
    }

    // Le message « FIN DE PARTIE » a fini de s'afficher
    public boolean estTerminee() {
        return partieTerminee && tempsFinPartie >= DUREE_FIN_DE_PARTIE;
    }

    // ------------------------------------------------------------------ Logique

    public void update(double dt) {
        traiterTouchesSpeciales();

        if (partieTerminee) {
            mettreAJourFinDePartie(dt);
        } else if (!enPause) {
            mettreAJourPartie(dt);
        }

        clavier.viderAppuis();
    }

    // Tout est remis à zéro, sauf les vies et le projectile de Gontrand
    private void demarrerNiveau(int numero) {
        niveau = numero;
        tempsNiveau = 0;
        tempsDepuisGroupe = 0;

        poissons.clear();
        projectiles.clear();
        camera.reinitialiser();
        gontrand.replacerAuDebut();

        decor = new Decor(Constantes.LARGEUR_NIVEAU, Constantes.HAUTEUR_FENETRE);
        baril = new Baril(Aleatoire.entre(Constantes.LARGEUR_NIVEAU / 5, 4 * Constantes.LARGEUR_NIVEAU / 5));
    }

    private void traiterTouchesSpeciales() {
        if (clavier.consommerAppui(KeyCode.D)) {
            modeDebug = !modeDebug;
        }
        if (clavier.consommerAppui(KeyCode.F)) {
            modeChamp = !modeChamp;
        }
        if (partieTerminee) {
            return;
        }

        if (clavier.consommerAppui(KeyCode.P)) {
            enPause = !enPause;
        }
        if (clavier.consommerAppui(KeyCode.Q)) {
            typeProjectile = TypeProjectile.ETOILE;
        }
        if (clavier.consommerAppui(KeyCode.W)) {
            typeProjectile = TypeProjectile.HIPPOCAMPES;
        }
        if (clavier.consommerAppui(KeyCode.E)) {
            typeProjectile = TypeProjectile.SARDINES;
        }
        if (clavier.consommerAppui(KeyCode.R)) {
            gontrand.redonnerVies();
        }
        if (clavier.consommerAppui(KeyCode.T)) {
            demarrerNiveau(niveau + 1);
        }
    }

    private void mettreAJourFinDePartie(double dt) {

        tempsFinPartie += dt;
    }

    private void mettreAJourPartie(double dt) {
        tempsNiveau += dt;

        gontrand.setDirection(
                clavier.direction(KeyCode.LEFT, KeyCode.RIGHT),
                clavier.direction(KeyCode.UP, KeyCode.DOWN));
        gontrand.update(dt);
        // Jamais plus à gauche que la caméra, ni hors du niveau
        gontrand.contraindre(camera.getX(), Constantes.LARGEUR_NIVEAU, 0, Constantes.HAUTEUR_FENETRE);
        camera.suivre(gontrand);

        lancerProjectileSiDemande(dt);
        faireApparaitreLesPoissons(dt);

        baril.update(dt);
        for (PoissonEnnemi poisson : poissons) {
            poisson.update(dt);
        }
        for (Projectile projectile : projectiles) {
            projectile.update(dt);
        }

        gererCollisions();
        retirerObjetsSortis();

        if (gontrand.getX() + gontrand.getLargeur() >= Constantes.LARGEUR_NIVEAU - 0.001) {
            demarrerNiveau(niveau + 1);
        }
    }

    private void lancerProjectileSiDemande(double dt) {
        delaiTir = Math.max(0, delaiTir - dt);

        boolean demande = clavier.estEnfoncee(KeyCode.SPACE) || clavier.consommerAppui(KeyCode.SPACE);
        if (demande && delaiTir <= 0) {
            projectiles.addAll(typeProjectile.creer(gontrand.getCentre(), poissons));
            delaiTir = DELAI_ENTRE_TIRS;
        }
    }

    // Groupe de 1 à 5 poissons à droite de l'écran, de plus en plus souvent
    private void faireApparaitreLesPoissons(double dt) {
        tempsDepuisGroupe += dt;

        double intervalle = 0.75 + 1 / Math.sqrt(niveau);
        if (tempsDepuisGroupe >= intervalle) {
            tempsDepuisGroupe -= intervalle;

            int nombre = Aleatoire.entier(1, 5);
            double xDepart = camera.getX() + Constantes.LARGEUR_FENETRE;
            for (int i = 0; i < nombre; i++) {
                poissons.add(PoissonEnnemi.aleatoire(niveau, xDepart));
            }
        }
    }

    private void gererCollisions() {
        // Poissons contre Gontrand (l'invincibilité empêche de perdre toutes ses vies sur un seul poisson)
        for (PoissonEnnemi poisson : poissons) {
            if (gontrand.enCollision(poisson)) {
                gontrand.perdreVie();
            }
        }
        if (gontrand.estMort()) {
            partieTerminee = true;
            tempsFinPartie = 0;
        }

        // Projectiles contre poissons : le poisson disparaît, le projectile continue
        for (int i = poissons.size() - 1; i >= 0; i--) {
            if (touchePar(poissons.get(i))) {
                poissons.remove(i);
            }
        }

        // Baril : nouveau projectile (différent de l'actuel), une seule fois
        if (!baril.estUtilise() && gontrand.enCollision(baril)) {
            baril.utiliser();
            typeProjectile = typeProjectile.autreAuHasard();
        }
    }

    private boolean touchePar(PoissonEnnemi poisson) {
        for (Projectile projectile : projectiles) {
            if (projectile.enCollision(poisson)) {
                return true;
            }
        }
        return false;
    }

    private void retirerObjetsSortis() {
        for (int i = poissons.size() - 1; i >= 0; i--) {
            if (poissons.get(i).estSortiDuJeu(camera)) {
                poissons.remove(i);
            }
        }

        for (int i = projectiles.size() - 1; i >= 0; i--) {
            if (!camera.estVisible(projectiles.get(i))) {
                projectiles.remove(i);
            }
        }
    }

    // ------------------------------------------------------------------ Affichage

    public void draw(GraphicsContext gc) {
        decor.draw(gc, camera);

        baril.draw(gc, camera);
        for (Projectile projectile : projectiles) {
            projectile.draw(gc, camera);
        }
        for (PoissonEnnemi poisson : poissons) {
            poisson.draw(gc, camera);
        }
        gontrand.draw(gc, camera);

        if (modeChamp) {
            dessinerChampElectrique(gc);
        }
        if (modeDebug) {
            dessinerDebug(gc);
        }

        AffichageFixe.dessinerBarreDeVie(gc, gontrand.getVies(), typeProjectile);
        if (modeDebug) {
            double pourcentage = gontrand.getX() / (Constantes.LARGEUR_NIVEAU - gontrand.getLargeur()) * 100;
            AffichageFixe.dessinerInfosDebug(gc, poissons.size(), projectiles.size(), pourcentage);
        }

        if (partieTerminee) {
            AffichageFixe.dessinerTexteCentre(gc, "FIN DE PARTIE", Color.DARKRED, 64, FontWeight.BOLD);
        } else if (tempsNiveau < DUREE_AFFICHAGE_NIVEAU) {
            AffichageFixe.dessinerTexteCentre(gc, "NIVEAU " + niveau, Color.WHITE, 48, FontWeight.NORMAL);
        }

        if (enPause && !partieTerminee) {
            AffichageFixe.assombrir(gc);
            AffichageFixe.dessinerTexteCentre(gc, "PAUSE", Color.WHITE, 48, FontWeight.NORMAL);
        }
    }

    // Contours jaunes + ligne à 20 % de l'écran (où la caméra suit Gontrand)
    private void dessinerDebug(GraphicsContext gc) {
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(1);
        double xLigne = Constantes.LARGEUR_FENETRE * 0.2;
        gc.strokeLine(xLigne, 0, xLigne, Constantes.HAUTEUR_FENETRE);

        gontrand.drawDebug(gc, camera);
        baril.drawDebug(gc, camera);
        for (PoissonEnnemi poisson : poissons) {
            poisson.drawDebug(gc, camera);
        }
        for (Projectile projectile : projectiles) {
            projectile.drawDebug(gc, camera);
        }
    }

    // Une flèche de champ tous les 50 px + l'accélération des boîtes de sardines
    private void dessinerChampElectrique(GraphicsContext gc) {
        double premierX = Math.ceil(camera.getX() / PAS_GRILLE_CHAMP) * PAS_GRILLE_CHAMP;

        for (double x = premierX; x < camera.getX() + Constantes.LARGEUR_FENETRE; x += PAS_GRILLE_CHAMP) {
            for (double y = 0; y < Constantes.HAUTEUR_FENETRE; y += PAS_GRILLE_CHAMP) {
                Point2D positionMonde = new Point2D(x, y);
                Point2D champ = ChampElectrique.calculer(poissons, positionMonde);

                UtilitairesDessins.dessinerVecteurForce(camera.versEcran(positionMonde), champ, gc);
            }
        }

        for (Projectile projectile : projectiles) {
            projectile.drawDebugChamp(gc, camera);
        }
    }
}

