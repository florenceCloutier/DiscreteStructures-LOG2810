/*
 * Implementation de la classe Drone
 * file Drone.java
 * authors Alexis Foulon, Florence Cloutier et Jonathan Siclait
 *
 * Ce programme contient les methodes qui ont pour but la construction de la classe
 * Drone et l'implementation des methodes de cette derniere.
 */
import java.util.ArrayList;
import java.util.List;

public class Drone {
    String currentLocation;
    String toLocation;
    boolean isBusy;
    List<List<Requete>> livraisons;
    //Graphe.DroneType type; // 3 ou 5 ampères

    /**
     * Constructeur par parametres
     * @param currentLocation
     */
    Drone(String currentLocation) {
        this.currentLocation = currentLocation;
        this.toLocation = null;
        this.isBusy = false;
        livraisons = new ArrayList<>();
    }
}