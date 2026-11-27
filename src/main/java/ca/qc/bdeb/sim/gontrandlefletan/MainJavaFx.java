package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

// Seulement la fenêtre et le passage d'une scène à l'autre. La logique de jeu est dans Jeu
public class MainJavaFx extends Application {
    private static final Color COULEUR_FOND = Color.rgb(42, 127, 255);
    private static final double DT_MAX = 0.05;   // évite les gros sauts si la fenêtre gèle un instant

    private static final String MEMBRE_1 = "Lassoued Taha-Ali";


    private static final String TEXTE_EXPLICATIF =
            "Travail remis à Nicolas Hurtubise. "
                    + "Graphismes adaptés de https://game-icons.net/ et de https://openclipart.org/. "
                    + "Développé dans le cadre du cours 420-SF3-RE - Développement d'applications dans un "
                    + "environnement graphique, au Collège de Bois-de-Boulogne.";

    private Stage fenetre;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage fenetre) {
        this.fenetre = fenetre;

        fenetre.setTitle("Gontrand le Flétan");
        fenetre.getIcons().add(Images.get("gontrand.png"));
        fenetre.setResizable(false);

        fenetre.setScene(sceneAccueil());
        fenetre.show();
    }

    // Escape ferme le programme
    private Scene sceneAccueil() {
        ImageView logo = new ImageView(Images.get("logo.png"));
        logo.setPreserveRatio(true);
        logo.setFitHeight(Constantes.HAUTEUR_FENETRE - 70);

        Button boutonJouer = new Button("Jouer!");
        boutonJouer.setOnAction((e) -> {
            fenetre.setScene(sceneJeu());
        });

        Button boutonInfos = new Button("Infos");
        boutonInfos.setOnAction((e) -> {
            fenetre.setScene(sceneInfos());
        });

        HBox boutons = new HBox(10, boutonJouer, boutonInfos);
        boutons.setAlignment(Pos.CENTER);

        VBox racine = new VBox(10, logo, boutons);
        racine.setAlignment(Pos.CENTER);
        racine.setBackground(new Background(new BackgroundFill(COULEUR_FOND, null, null)));

        Scene scene = new Scene(racine, Constantes.LARGEUR_FENETRE, Constantes.HAUTEUR_FENETRE);
        scene.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                Platform.exit();
            }
        });
        return scene;
    }


    private Scene sceneInfos() {
        Label titre = new Label("Gontrand le Flétan");
        titre.setFont(Font.font("System", 48));

        ImageView poisson = new ImageView(Images.get("poisson" + Aleatoire.entier(1, 5) + ".png"));
        poisson.setPreserveRatio(true);
        poisson.setFitHeight(120);

        Label texte = new Label(TEXTE_EXPLICATIF);
        texte.setFont(Font.font("System", 11));
        texte.setWrapText(true);
        texte.setMaxWidth(560);
        texte.setTextAlignment(TextAlignment.JUSTIFY);

        Button boutonRetour = new Button("Retour");
        boutonRetour.setOnAction((e) -> {
            fenetre.setScene(sceneAccueil());
        });

        VBox racine = new VBox(12,
                titre,
                poisson,
                ligneNom("Par", MEMBRE_1),
                texte,
                boutonRetour);
        racine.setAlignment(Pos.CENTER);
        racine.setBackground(new Background(new BackgroundFill(COULEUR_FOND, null, null)));

        Scene scene = new Scene(racine, Constantes.LARGEUR_FENETRE, Constantes.HAUTEUR_FENETRE);
        scene.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                fenetre.setScene(sceneAccueil());
            }
        });
        return scene;
    }

    // « Par » en petit, suivi du nom en plus gros
    private HBox ligneNom(String mot, String nom) {
        Label petit = new Label(mot);
        petit.setFont(Font.font("System", 18));

        Label grand = new Label(nom);
        grand.setFont(Font.font("System", 26));

        HBox ligne = new HBox(8, petit, grand);
        ligne.setAlignment(Pos.BASELINE_CENTER);
        return ligne;
    }

    // Chaque appel crée une nouvelle partie. Escape (ou la fin de partie) retourne à l'accueil
    private Scene sceneJeu() {
        Pane racine = new Pane();
        Scene scene = new Scene(racine, Constantes.LARGEUR_FENETRE, Constantes.HAUTEUR_FENETRE);
        Canvas canvas = new Canvas(Constantes.LARGEUR_FENETRE, Constantes.HAUTEUR_FENETRE);
        racine.getChildren().add(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        Clavier clavier = new Clavier();
        Jeu jeu = new Jeu(clavier);

        AnimationTimer timer = new AnimationTimer() {
            private long dernierTemps = 0;

            @Override
            public void handle(long maintenant) {
                if (dernierTemps == 0) {
                    dernierTemps = maintenant;
                }
                double dt = Math.min((maintenant - dernierTemps) / 1_000_000_000.0, DT_MAX);
                dernierTemps = maintenant;

                // Fenêtre sans focus : on ne reçoit plus les relâchements de touches
                if (!fenetre.isFocused()) {
                    clavier.reinitialiser();
                }

                jeu.update(dt);
                jeu.draw(gc);

                if (jeu.estTerminee()) {
                    stop();   // important : on arrête l'AnimationTimer
                    fenetre.setScene(sceneAccueil());
                }
            }
        };

        scene.setOnKeyPressed((e) -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                // Important : on arrête l'AnimationTimer pour stopper le jeu
                timer.stop();
                fenetre.setScene(sceneAccueil());
            } else {
                clavier.toucheEnfoncee(e.getCode());
            }
        });
        scene.setOnKeyReleased((e) -> {
            clavier.toucheRelachee(e.getCode());
        });

        timer.start();
        return scene;
    }
}
//ff