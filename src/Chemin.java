/*
 * Implementation de la classe Chemin
 * file Chemin.java
 * authors Alexis Foulon, Florence Cloutier et Jonathan Siclait
 *
 * Ce programme contient les methodes qui ont pour but la construction de la classe
 * Chemin et l'implementation des methodes de cette derniere.
 */
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Chemin implements Comparable<Chemin> {
    //Liste est dans l'ordre debut -> fin
    List<Integer> chemin;

    Integer temps;
    Double niveauBatterie;
    boolean estPossible;
    Graphe.DroneType droneType;

    HashSet<Integer> chargeursVisite;

    //Creates an impossible Path
    Chemin() {
        this.estPossible = false;
    }

    Chemin(List<Integer> chemin, Integer temps, Double niveauBatterie, HashSet<Integer> chargeursVisite, Graphe.DroneType droneType, boolean estPossible) {
        this.chemin = chemin;
        this.temps = temps;
        this.niveauBatterie = niveauBatterie;
        this.chargeursVisite = chargeursVisite;
        this.droneType = droneType;
        this.estPossible = estPossible;
    }

    Integer start() {
        return chemin.get(chemin.size() - 1);
    }

    Integer end() {
        return chemin.get(0);
    }

    public void append(Chemin chemin) {
        //Ajouter des chemins impossible est inutile
        if (!chemin.estPossible || !estPossible)
            return;

        //On peut seulement ajouter des chemin dont la fin et le debut concorde
        if (end() != chemin.start())
            return;

        //Enleve le sommet qui est present dans les deux chemins et les combiner
        chemin.chemin.remove(chemin.chemin.size() - 1);
        List<Integer> cheminAjouter = new ArrayList<>();
        cheminAjouter.addAll(chemin.chemin);
        cheminAjouter.addAll(this.chemin);
        chemin.chemin.add(end());
        this.chemin = cheminAjouter;

        //On combine seulement des chemins qui passe par des stations de chargement il faut donc considerer le temps
        //de recharge
        this.temps += chemin.temps + 20;

        //Puisque passe par des stations de recharge on garde le meme niveau de batterie
        this.niveauBatterie = chemin.niveauBatterie;

        //Combine la liste des chargeurs visited
        chargeursVisite.addAll(chemin.chargeursVisite);
    }

    @Override
    public int compareTo(Chemin chemin) {
        return temps - chemin.temps;
    }

    @Override
    public String toString() {
        if (!estPossible)
            return "Nous ne pouvons malheureusement pas effectuer cette livraison.";

        StringBuilder string = new StringBuilder();

        //Type de drone
        string.append("Type : " + droneType + "\n");

        //Chemin
        string.append("Chemin : ");
        for (int i = chemin.size() - 1; i >= 0; i--) {
            string.append(chemin.get(i) + " -> ");
        }

        //Enlève la derniere flèche
        string.delete(string.length() - 4, string.length());

        //Temps vers la destination
        string.append("\nTemps : " + temps);

        //Niveau de la batterie à la destination
        string.append("\nNiveau de batterie a l'arrive : " + niveauBatterie);

        return string.toString();
    }
}
