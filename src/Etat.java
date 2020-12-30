/*
 * Implementation de la classe Etat
 * file Etat.java
 * authors Alexis Foulon, Florence Cloutier et Jonathan Siclait
 *
 * Ce programme contient les methodes qui ont pour but la construction de la classe
 * Etat et l'implementation des methodes de cette derniere.
 */
import java.util.ArrayList;
import java.util.List;

public class Etat {
    public Character valeur;
    public Etat fromState;
    public List<Etat> toStates;

    /**
     * Constructeur par parametres
     * @param valeur
     * @param fromState
     */
    Etat(char valeur, Etat fromState) {
        this.valeur = valeur;
        this.fromState = fromState;
        toStates = new ArrayList<>();
    }

    /**
     * Methode qui verifie si deux etats sont identitiques
     * @param node
     * @return
     */
    @Override
    public boolean equals(Object node) {
        boolean equals = false;

        if (node instanceof Etat)
            equals = this.valeur == ((Etat) node).valeur;
        return equals;
    }

    /**
     * Fonction de transition de l'automate
     * @param unCodePostal
     * @param idx
     * @return
     */
    public Etat validTransition(String unCodePostal, int idx) {
        Etat newState = new Etat('0', null);
        for (Etat etat : this.toStates) {
            boolean isThere = etat.valeur == unCodePostal.charAt(idx);
            if (isThere) {
                newState = etat;
                break;
            }
        }
        return newState;
    }
}
