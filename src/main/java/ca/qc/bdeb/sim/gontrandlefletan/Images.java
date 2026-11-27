package ca.qc.bdeb.sim.gontrandlefletan;


import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

// Chaque image n'est lue qu'une fois : sans cache, chaque poisson, décor ou projectile relirait le fichier
public class Images {
    private static final HashMap<String, Image> CACHE = new HashMap<>();

    private Images() {}

    public static Image get(String nom) {
        Image image = CACHE.get(nom);
        if (image == null) {
            image = new Image(nom);
            CACHE.put(nom, image);
        }
        return image;
    }
}