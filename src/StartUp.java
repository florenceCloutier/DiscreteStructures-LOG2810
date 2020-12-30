/*
 * Implementation de la classe StartUp
 * file StartUp.java
 * authors Alexis Foulon, Florence Cloutier et Jonathan Siclait
 *
 * Ce programme contient les methodes qui ont pour but la construction de la classe
 * StartUp et l'implementation des methodes de cette derniere.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.text.*;

public class StartUp {

    Automate arbreRequetesValides;
    List<Requete> requetesValides = new ArrayList<>();
    Queue<List<Requete>> requetesEnAttente = new LinkedList<>();
    List<String> quartiersValides = new ArrayList<>();
    Flotte flottePourRequetes;

    //Statistiques
    int requetesInvalides = 0;
    int nbRequetesTraitees = 0;
    HashMap<String, Integer> nbDronesDansQuartiers = new HashMap<>();

    /**
     * Constructeur par parametres
     * @param nomFichier
     * @throws FileNotFoundException
     */
    StartUp(String nomFichier) throws FileNotFoundException {
        arbreRequetesValides = new Automate(nomFichier);
        readValidAdresses(nomFichier);
        flottePourRequetes = new Flotte(requetesEnAttente, requetesValides, nbDronesDansQuartiers, quartiersValides);
    }

    /**
     * Methode qui traite les requetes d'un cycle
     * @param fileName
     * @throws FileNotFoundException
     */
    public void traiterLesRequetes(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner reader = new Scanner(file);
        lireRequetes(reader);
        flottePourRequetes.assignerColis();
        flottePourRequetes.equilibrerFlotte();
    }

    /**
     * Methode qui retourne une chaine de caracteres contenant les statistiques du graphe
     * @return
     */
    public String getStats() {
        StringBuilder stats = new StringBuilder();
        NumberFormat formatter = new DecimalFormat("#0.00");

        stats.append("Le nombre de requetes traitees depuis le lancement du programme: " + nbRequetesTraitees + "\n");
        stats.append("Le nombre de requetes invalides depuis le lancement du programme: " + requetesInvalides + "\n");
        stats.append("Le nombre de drone dans chaque quartier suite au reequilibrage: \n"
                + flottePourRequetes.printDronesQuartiers(nbDronesDansQuartiers) + "\n");
        stats.append("Le nombre moyen de colis transporte par un drone de petite taille : "
                + formatter.format(flottePourRequetes.colisMoyenDrones(Graphe.DroneType.Petit)) + "\n");
        stats.append("Le nombre moyen de colis transporte par un drone de grande taille : "
                + flottePourRequetes.colisMoyenDrones(Graphe.DroneType.Large) + "\n");

        return stats.toString();
    }

    /**
     * Methode qui lit les requetes d'un fichier de requetes
     * @param reader
     */
    public void lireRequetes(Scanner reader) {
        while(reader.hasNextLine()) {
            String data = reader.nextLine();

            String[] lineData = data.split(" ");
            Requete requete = new Requete(lineData[0], lineData[1], Integer.parseInt(lineData[2]));

            // Ajout des requetes valides dans la liste de requetes valides
            if (validerRequete(requete)) {
                requetesValides.add(requete);
                nbRequetesTraitees++;
            }
            else requetesInvalides++;
        }
    }

    /**
     * Methode qui valide les requetes lues selon l'automate de codes postaux valides
     * @param requete
     * @return
     */
    public boolean validerRequete(Requete requete) {
        boolean estValide = false;
        if (validerUnCodePostal(requete.depart, arbreRequetesValides.racine, 0)
                && validerUnCodePostal(requete.destination, arbreRequetesValides.racine, 0) && (requete.poids <= 5000))
            estValide = true;
        return estValide;
    }

    /**
     * Methode qui valide un code postal selon l'automate de codes postaux valides
     * @param unCodePostal
     * @param fromState
     * @param idx
     * @return
     */
    public boolean validerUnCodePostal(String unCodePostal, Etat fromState, int idx) {
        fromState = fromState.validTransition(unCodePostal, idx);
        if (fromState.fromState == null) return false;
        if (!fromState.toStates.isEmpty()) {
            idx++;
            validerUnCodePostal(unCodePostal, fromState, idx);
        }
        return true;
    }

    /**
     * Methode qui lit tous les quartiers valides du fichier de codes postaux et cree une liste
     * @param nomFichier
     * @throws FileNotFoundException
     */
    private void readValidAdresses(String nomFichier) throws FileNotFoundException {
        quartiersValides = new ArrayList<>();
        File fichier = new File(nomFichier);
        Scanner reader = new Scanner(fichier);
        while(reader.hasNextLine()) {
            String data = reader.nextLine();
            quartiersValides.add(data);
        }
    }
}