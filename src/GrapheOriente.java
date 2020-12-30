/*
 * Implementation de la classe GrapheOriente
 * file GrapheOriente.java
 * authors Alexis Foulon, Florence Cloutier et Jonathan Siclait
 *
 * Ce programme contient les methodes qui ont pour but la construction de la classe
 * GrapheOriente et l'implementation des methodes de cette derniere.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.*;

public class GrapheOriente {
    /**
     * Classe decrivant les sommets du graphe oriente et leurs caracteristiques
     */
    static private class Sommet {
        Integer id;
        String nom;

        ArrayList<Integer> arcsEntrant;
        ArrayList<Integer> arcsSortant;

        /**
         * Constructeur par parametres
         * @param id
         * @param nom
         */
        Sommet(Integer id, String nom) {
            this.id = id;
            this.nom = nom;
            arcsEntrant = new ArrayList<>();
            arcsSortant = new ArrayList<>();
        }

        public void connecterArcsEntrant(Integer id) {
            this.arcsEntrant.add(id);
        }

        public void connecterArcsSortant(Integer id) {
            this.arcsSortant.add(id);
        }

        public void deconnecterArcsEntrant(Integer id) {
            arcsEntrant.remove(id);
        }

        public void deconnecterArcsSortant(Integer id) {
            arcsSortant.remove(id);
        }
    }

    ArrayList<Sommet> sommets;
    HashMap<Sommet,Integer> sommetsTraite;

    /**
     * Constructeur par parametres
     * @param cheminFichier
     * @throws FileNotFoundException
     */
    public GrapheOriente(String cheminFichier) throws FileNotFoundException{
        sommets = new ArrayList<>();
        sommetsTraite = new HashMap();
        creerGrapheOriente(cheminFichier);
    }

    private void creerGrapheOriente(String cheminFichier) throws FileNotFoundException {
        File fichier = new File(cheminFichier);
        Scanner lecteur = new Scanner(fichier);

        lireSommets(lecteur);
        lireArcs(lecteur);
    }

    /**
     * Methode qui lit les sommets en entree de l'objet Scanner
     * @param lecteur
     */
    private void lireSommets(Scanner lecteur){
        while(lecteur.hasNextLine()){
            String ligne = lecteur.nextLine();

            if(ligne.equals(""))
                return;

            String[] donneesLigne = ligne.split(",");
            Integer id = Integer.parseInt(donneesLigne[0]);
            String nom = donneesLigne[1];

            sommets.add(new Sommet(id,nom));
        }
    }

    /**
     * Methode qui lit les arcs en entree de l'objet Scanner
     * @param lecteur
     */
    private void lireArcs(Scanner lecteur){
        while(lecteur.hasNextLine()){
            String ligne = lecteur.nextLine();

            String[] donnesLigne = ligne.split(",");
            Integer sommet1 = Integer.parseInt(donnesLigne[0]);
            Integer sommet2 = Integer.parseInt(donnesLigne[1]);

            connecter(sommet1,sommet2);
        }
    }

    private void connecter(Integer sommet1, Integer sommet2){
        for(Sommet sommet : sommets){
            if(sommet.id == sommet1)
                sommet.connecterArcsSortant(sommet2);

            if(sommet.id == sommet2)
                sommet.connecterArcsEntrant(sommet1);
        }
    }

    private void deconnecter(Integer sommet1, Integer sommet2){
        for(Sommet sommet : sommets){
            if(sommet.id == sommet1)
                sommet.deconnecterArcsSortant(sommet2);

            if(sommet.id == sommet2)
                sommet.deconnecterArcsEntrant(sommet1);
        }
    }

    private int trouverSommet(Integer id){
        int index = -1;
        for(int i = 0; i < sommets.size(); i++){
            if(sommets.get(i).id == id)
                index = i;
        }
        return index;
    }

    public String genererHasse(){
        enleverBoucle();

        enleverTransitivite();

        return ordonnerElements();
    }

    /**
     * Methode qui enleve la caracteristique reflexive du graphe
     */
    private void enleverBoucle(){
        for(Sommet sommetActuel : sommets) {
            for (Integer arcEntrant : sommetActuel.arcsEntrant) {
                if(sommetActuel.id == arcEntrant)
                    deconnecter(arcEntrant,arcEntrant);
            }
        }
    }

    /**
     * Methode qui enleve la caracteristique transitive du graphe
     */
    private void enleverTransitivite(){
        HashMap<Integer,Integer> arcsInutile = new HashMap<>();
        for(Sommet sommetActuel : sommets){
            if(sommetActuel.arcsSortant.size() >= 2) {
                for (Integer voisinActuel : sommetActuel.arcsSortant) {
                    int indexVoisin = trouverSommet(voisinActuel);
                    for (Integer deuxiemeVoisin : sommets.get(indexVoisin).arcsSortant) {
                        for (Integer voisin : sommetActuel.arcsSortant) {
                            if (deuxiemeVoisin == voisin) {
                                arcsInutile.put(sommetActuel.id, deuxiemeVoisin);
                            }
                        }
                    }
                }
            }
        }
        for(Map.Entry<Integer, Integer> arc : arcsInutile.entrySet()){
            deconnecter(arc.getKey(),arc.getValue());
        }
    }

    private String ordonnerElements(){
        StringBuilder Hasse = new StringBuilder();
        Deque<Sommet> sommetsATraiter = new ArrayDeque<>();
        ArrayList<Sommet> sommetsFini = new ArrayList<>();
        int nbArcEntrant = 0;
        int nbListe = 1;
        while(sommets.size() != sommetsFini.size()) {
            for (Sommet sommetActuel : sommets) {
                if (sommetActuel.arcsEntrant.size() == nbArcEntrant){
                    sommetsATraiter.add(sommetActuel);
                }
            }
            nbArcEntrant++;
            for(Sommet sommetActuel : sommetsATraiter){
                if(sommetActuel.arcsSortant.size() > 0){
                    for(int indexATraiter = indexNonTraite(sommetActuel); indexATraiter < sommetActuel.arcsSortant.size(); indexATraiter++){
                        Hasse.append("Liste " + nbListe + " : " + sommetActuel.nom);
                        int indexCourant = trouverSommet(sommetActuel.arcsSortant.get(indexATraiter));
                        boolean obtenirArcs = true;
                        while(obtenirArcs) {
                            Hasse.append("->" + sommets.get(indexCourant).nom);
                            if (indexNonTraite(sommets.get(indexCourant)) != sommets.get(indexCourant).arcsSortant.size()) {
                                Integer ancienneValeur = sommetsTraite.remove(sommets.get(indexCourant));
                                int ancienneValeurIndex = indexCourant;
                                indexCourant = trouverSommet(sommets.get(indexCourant).arcsSortant.get(indexNonTraite(sommets.get(indexCourant))));
                                sommetsTraite.put(sommets.get(ancienneValeurIndex), ++ancienneValeur);
                            }
                            else {
                                Hasse.append("\n");
                                obtenirArcs = false;
                            }
                        }
                        sommetsTraite.remove(sommets.get(indexCourant));
                        int size = sommets.get(indexCourant).arcsSortant.size();
                        sommetsTraite.put(sommets.get(indexCourant),size);
                        nbListe++;
                    }
                    if(sommetsTraite.containsKey(sommetActuel)){
                        sommetsTraite.remove(sommetActuel);
                    }
                    sommetsTraite.put(sommetActuel,sommetActuel.arcsSortant.size());
                }
                else{
                    Hasse.append("Liste " + nbListe + " : " + sommetActuel.nom + "\n");
                    nbListe++;
                }
            }
            for(Sommet sommetActuel : sommetsATraiter){
                sommetsFini.add(sommetActuel);
            }
            sommetsATraiter.clear();
        }
        sommetsTraite.clear();
        return Hasse.toString();
    }

    public int indexNonTraite(Sommet sommet){
        if(!sommetsTraite.containsKey(sommet)){
            sommetsTraite.put(sommet,0);
        }
        return sommetsTraite.get(sommet);
    }

    public String toString() {
        StringBuilder graphe = new StringBuilder();

        for (Sommet sommet : sommets) {
            graphe.append("(" + sommet.nom + "," + sommet.id);
            if(!sommet.arcsSortant.isEmpty()) {
                graphe.append(", (");
                for (Integer arc : sommet.arcsSortant) {
                    graphe.append("(" + sommets.get(trouverSommet(arc)).nom + "," + sommets.get(trouverSommet(arc)).id + "), ");
                }
                //Enleve le dernier "," ajouter
                graphe.delete(graphe.length() - 2, graphe.length());
                graphe.append("))\n");
            }
            else
                graphe.append(")\n");
        }

        return graphe.toString();
    }
}

