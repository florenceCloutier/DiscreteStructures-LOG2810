/*
 * Implementation de la classe Flotte
 * file Automate.java
 * authors Alexis Foulon, Florence Cloutier et Jonathan Siclait
 *
 * Ce programme contient les methodes qui ont pour but la construction de la classe
 * Automate et l'implementation des methodes de cette derniere.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Automate {
    Etat racine = new Etat((char) 0, null);
    private List<Etat> etats;

    /**
     * Constructeur par parametres
     * @param nomFichier
     * @throws FileNotFoundException
     */
    Automate(String nomFichier) throws FileNotFoundException {
        this.etats = new ArrayList<>();
        creerArbreAdresses(nomFichier);
    }

    /**
     * Methode qui cree et retourne l'automate avec les codes postaux valides
     * @param nomFichier
     * @return
     * @throws FileNotFoundException
     */
    private Automate creerArbreAdresses(String nomFichier) throws FileNotFoundException {
        File fichier = new File(nomFichier);
        Scanner reader = new Scanner(fichier);
        lireCodesPostauxFichier(reader);

        return this;
    }

    /**
     * Methode qui lit les codes postaux valide du fichier
     * @param reader
     */
    public void lireCodesPostauxFichier(Scanner reader) {
        while (reader.hasNextLine()) {
            String data = reader.nextLine();
            int idxString = 0;
            creerCheminCodePostal(data, racine, idxString);
        }
    }

    /**
     * Methode qui cree le chemin dans l'automate pour un code postal valide
     * @param data
     * @param fromState
     * @param idx
     */
    private void creerCheminCodePostal(String data, Etat fromState, int idx) {
        Etat child = new Etat(data.charAt(idx), fromState);
        if (!fromState.toStates.contains(child))
            fromState.toStates.add(child);
        else // Le prefixe du code postal se trouve deja dans l'arbre
            child = fromState.toStates.get(fromState.toStates.indexOf(child));
        // Verification si nous sommes arrives a la fin du code postal
        if (idx < data.length() - 1) {
            idx++;
            creerCheminCodePostal(data, child, idx);
        }
    }
}
