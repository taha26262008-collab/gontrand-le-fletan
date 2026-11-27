package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

// Les flèches changent l'accélération, jamais directement la vitesse
public class Gontrand extends ObjetMobile {
    public static final int VIES_MAX = 4;

    private static final double LARGEUR = 102;
    private static final double HAUTEUR = 90;
    private static final double ACCELERATION = 1000;   // px/s²
    private static final double VITESSE_MAX = 300;     // px/s, sur chaque axe
    private static final double DUREE_INVINCIBILITE = 2.0;
    private static final double DUREE_CLIGNOTEMENT = 0.25;

    private final Image imageRepos = Images.get("gontrand.png");
    private final Image imageAvant = Images.get("gontrand-avant.png");
    private final Image imageOutch = Images.get("gontrand-outch.png");

    private int directionX;   // -1, 0 ou +1
    private int directionY;
    private int vies = VIES_MAX;
    private double tempsInvincible = 0;

    public Gontrand() {
        super(0, positionYDepart(), LARGEUR, HAUTEUR);
    }

    private static double positionYDepart() {
        return (Constantes.HAUTEUR_FENETRE - HAUTEUR) / 2;
    }

    // -1 (gauche/haut), 0 ou +1 (droite/bas)
    public void setDirection(int directionX, int directionY) {
        this.directionX = directionX;
        this.directionY = directionY;
    }

    @Override
    public void update(double dt) {
        ax = accelerationPour(directionX, vx);
        ay = accelerationPour(directionY, vy);

        double vxAvant = vx;
        double vyAvant = vy;
        updateVitesse(dt);
        vx = corrigerVitesse(vx, vxAvant, directionX);
        vy = corrigerVitesse(vy, vyAvant, directionY);
        updatePosition(dt);

        tempsInvincible = Math.max(0, tempsInvincible - dt);
    }

    // Même chose pour les deux axes : on accélère vers la direction, sinon on freine
    private double accelerationPour(int direction, double vitesse) {
        if (direction != 0) {
            return direction * ACCELERATION;
        }
        return -Math.signum(vitesse) * ACCELERATION;
    }

    private double corrigerVitesse(double vitesse, double vitesseAvant, int direction) {
        // Le freinage doit s'arrêter à zéro, pas repartir dans l'autre sens
        if (direction == 0 && vitesse * vitesseAvant < 0) {
            return 0;
        }
        return Math.clamp(vitesse, -VITESSE_MAX, VITESSE_MAX);
    }

    // Garde Gontrand dans les bornes ; la vitesse tombe à 0 sur l'axe du mur touché
    public void contraindre(double gaucheMin, double droiteMax, double hautMin, double basMax) {
        if (x < gaucheMin) {
            x = gaucheMin;
            vx = 0;
        } else if (x + largeur > droiteMax) {
            x = droiteMax - largeur;
            vx = 0;
        }

        if (y < hautMin) {
            y = hautMin;
            vy = 0;
        } else if (y + hauteur > basMax) {
            y = basMax - hauteur;
            vy = 0;
        }
    }

    // Les vies restent après le début d'un niveau
    public void replacerAuDebut() {
        x = 0;
        y = positionYDepart();
        vx = 0;
        vy = 0;
        ax = 0;
        ay = 0;
    }

    // Faux si invincible (aucune vie perdue)
    public void perdreVie() {
        if (estInvincible()) {
            return ;
        }
        vies--;
        tempsInvincible = DUREE_INVINCIBILITE;

    }

    public boolean estInvincible() {
        return tempsInvincible > 0;
    }

    public void redonnerVies() {
        vies = VIES_MAX;
    }

    public int getVies() {
        return vies;
    }

    public boolean estMort() {
        return vies <= 0;
    }

    @Override
    public void draw(GraphicsContext gc, Camera camera) {
        if (!estVisibleDansClignotement()) {
            return;
        }
        dessinerImage(gc, camera, choisirImage());
    }

    // 0.25 s visible, 0.25 s invisible pendant l'invincibilité
    private boolean estVisibleDansClignotement() {
        if (!estInvincible()) {
            return true;
        }
        double ecoule = DUREE_INVINCIBILITE - tempsInvincible;
        return ((int) (ecoule / DUREE_CLIGNOTEMENT)) % 2 == 0;
    }

    private Image choisirImage() {
        if (estInvincible()) {
            return imageOutch;
        }
        if (vx > 0) {
            return imageAvant;
        }
        return imageRepos;
    }
}
