/*
 * Implementation de la classe Requete
 * file Requete.java
 * authors Alexis Foulon, Florence Cloutier et Jonathan Siclait
 *
 * Ce programme contient les methodes qui ont pour but la construction de la classe
 * Requete et l'implementation des methodes de cette derniere.
 */
public class Requete {
    String depart;
    String destination;
    int poids;

    /**
     * Constructeur par parametres
     * @param depart
     * @param destination
     * @param poids
     */
    Requete(String depart, String destination, int poids) {
        this.depart = depart;
        this.destination = destination;
        this.poids = poids;
    }
}
