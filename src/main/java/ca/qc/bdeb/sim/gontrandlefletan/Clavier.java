package ca.qc.bdeb.sim.gontrandlefletan;

import javafx.scene.input.KeyCode;

import java.util.HashSet;


// estEnfoncee : touche tenue en ce moment (pour bouger)
// consommerAppui : vrai une seule fois par appui, même si on garde la touche (pause, débogage...)
public class Clavier {
    private final HashSet<KeyCode> enfoncees = new HashSet<>();
    private final HashSet<KeyCode> nouveauxAppuis = new HashSet<>();

    public void toucheEnfoncee(KeyCode touche) {
        // add() retourne false si la touche était déjà enfoncée (répétition automatique du système)
        if (enfoncees.add(touche)) {
            nouveauxAppuis.add(touche);
        }
    }

    public void toucheRelachee(KeyCode touche) {
        enfoncees.remove(touche);
    }

    public boolean estEnfoncee(KeyCode touche) {
        return enfoncees.contains(touche);
    }


    public boolean consommerAppui(KeyCode touche) {
        return nouveauxAppuis.remove(touche);
    }

    // -1, 0 ou +1
    public int direction(KeyCode toucheNegative, KeyCode touchePositive) {
        int direction = 0;
        if (estEnfoncee(touchePositive)) {
            direction++;
        }
        if (estEnfoncee(toucheNegative)) {
            direction--;
        }
        return direction;
    }

    // À appeler à la fin de chaque image
    public void viderAppuis() {
        nouveauxAppuis.clear();
    }

    // Quand la fenêtre perd le focus, on ne reçoit plus les relâchements
    public void reinitialiser() {
        enfoncees.clear();
        nouveauxAppuis.clear();
    }
}