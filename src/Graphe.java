/*
 * Implementation de la classe Graphe
 * file Graphe.java
 * authors Alexis Foulon, Florence Cloutier et Jonathan Siclait
 *
 * Ce programme contient les methodes qui ont pour but la construction de la classe
 * Graphe et l'implementation des methodes de cette derniere.
 */
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class Graphe {
    static final double BATTERIE_REMPLIE = 100.0;
    static final double BATTERIE_CRITIQUE = 20.0;
    static final int TEMPS_RECHARCHE = 20;

    static private class Sommet {
        Integer id;
        boolean chargeur;

        //Key: vertex to Node
        //Value: Time to node
        HashMap<Integer, Integer> adjacent;

        Sommet(Integer id, boolean chargeur) {
            this.id = id;
            this.chargeur = chargeur;
            adjacent = new HashMap<>();
        }

        public void connecter(Integer sommet, Integer temps) {
            adjacent.put(sommet, temps);
        }
    }

    //TODO - Combine with Node if possible
    static private class SommetDjikstra implements Comparable<SommetDjikstra>{
        Integer id;
        Integer provenance;
        Integer tempsVoyagement;
        Double niveauBatterie;

        SommetDjikstra(Integer id, Integer provenance, Integer tempsVoyagement, Double niveauBatterie) {
            this.id = id;
            this.provenance = provenance;
            this.tempsVoyagement = tempsVoyagement;
            this.niveauBatterie = niveauBatterie;
        }

        @Override
        public int compareTo(SommetDjikstra sommetDjikstra) {
            return tempsVoyagement - sommetDjikstra.tempsVoyagement;
        }
    }

    public enum PoidType {
        Plume,
        Moyen,
        Lourd;
    }

    enum DroneType {
        Petit,
        Large;

        @Override
        public String toString() {
            switch (this) {
                case Large:
                    return "Drone 5 Amperes";
                case Petit:
                    return "Drone 3.3 Amperes";
                default:
                    return "";
            }
        }
    }

    HashMap<Integer, Sommet> sommets;

    //Allows alternate path finding
    HashSet<Integer> sommetsChargeur;

    Graphe(String cheminFichier) throws FileNotFoundException{
        sommets = new HashMap<>();
        sommetsChargeur = new HashSet<>();
        creerGraphe(cheminFichier);
    }

    private void creerGraphe(String cheminFichier) throws FileNotFoundException {
        File fichier = new File(cheminFichier);
        Scanner lecteur = new Scanner(fichier);

        //La structure du fichier fait en sorte qu'il faut lire les sommets avant les arcs
        lireLignesSommet(lecteur);
        lireLignesArc(lecteur);
    }

    private void lireLignesSommet(Scanner lecteur) {

        //Si il n'y a pas de prochaine ligne au fichier on quitte la fonction
        if (!lecteur.hasNextLine())
            return;

        //Lit une ligne
        String ligne = lecteur.nextLine();

        //Si on lit une ligne vide, on à terminé de lire les sommets et on quitte la fonction
        if(ligne.equals(""))
            return;

        //Extraire les donné de la ligne
        String[] donneeLigne = ligne.split(",");
        Integer sommet = Integer.parseInt(donneeLigne[0]);
        Boolean chargeur = donneeLigne[1].equals("1");

        //Ajouter les donnée au graphe
        sommets.put(sommet, new Sommet(sommet, chargeur));

        //Si le sommet est une station de chargement on l'ajoute à la liste des stations de chargement
        if (chargeur)
            sommetsChargeur.add(sommet);

        //On lit les prochaines lignes récursivement
        lireLignesSommet(lecteur);
    }

    private void lireLignesArc(Scanner lecteur) {

        //Si il n'y a pas de prochaine ligne au fichier on quitte la fonction
        if (!lecteur.hasNextLine())
            return;

        //Lit une ligne
        String ligne = lecteur.nextLine();

        //Extraire les donné de la ligne
        String[] donneeLigne = ligne.split(",");
        Integer sommet1 = Integer.parseInt(donneeLigne[0]);
        Integer sommet2 = Integer.parseInt(donneeLigne[1]);
        Integer temps = Integer.parseInt(donneeLigne[2]);

        //Connecter les deux sommets du graphe
        connecter(sommet1, sommet2, temps);

        //Lire la prochaine ligne
        lireLignesArc(lecteur);
    }

    public void connecter(Integer sommet1, Integer sommet2, Integer temps) {
        sommets.get(sommet1).connecter(sommet2, temps);
        sommets.get(sommet2).connecter(sommet1, temps);
    }

    //Retourne le chemin optimal entre 2 sommets
    public Chemin cheminEntre(Integer debut, Integer fin, PoidType poid) {
        Chemin cheminPetitDrone = plusPetitChemin(debut, fin, poid, DroneType.Petit);

        if (cheminPetitDrone.estPossible)
            return cheminPetitDrone;

        return plusPetitChemin(debut, fin, poid, DroneType.Large);
    }

    //Cherche le plus petit chemin possible pour le type de drone
    private Chemin plusPetitChemin(Integer debut, Integer fin, PoidType poid, DroneType drone) {
        //Cherche un chemin directe
        Chemin plusCourtChemin = plusPetitCheminDirecte(debut, fin, poid, drone);

        PriorityQueue<Chemin> cheminsChargeur = new PriorityQueue<>();
        PriorityQueue<Chemin> cheminsChargeurValide = new PriorityQueue<>();

        if (plusCourtChemin.estPossible)
            return plusCourtChemin;

        //On essaie de faire des chemin avec des detours par des sommets chargeurs
        for (Integer sommetChargeur : sommetsChargeur) {
            Chemin chargeur = plusPetitCheminDirecte(sommetChargeur, fin, poid, drone);
            if (chargeur.estPossible)
                cheminsChargeur.add(chargeur);
        }

        while (!cheminsChargeur.isEmpty()) {

            Chemin cheminDestination = cheminsChargeur.poll();
            Chemin cheminDebut = plusPetitCheminDirecte(debut, cheminDestination.start(), poid, drone);

            //Si il y a un chemin possible on l'ajoute a la liste
            if (cheminDebut.estPossible) {
                cheminDebut.append(cheminDestination);
                cheminsChargeurValide.add(cheminDebut);
                continue;
            }

            //On essaie de faire des chemin avec des detours par des sommets chargeurs
            for (Integer sommetChargeur : sommetsChargeur) {
                //Il est impossible d'avoir un chemin qui passe 2 fois par le meme chargeur
                if (cheminDestination.chargeursVisite.contains(sommetChargeur))
                    continue;

                Chemin cheminChargeur = plusPetitCheminDirecte(sommetChargeur, cheminDestination.start(), poid, drone);
                if (cheminChargeur.estPossible) {
                    cheminChargeur.append(cheminDestination);
                    cheminsChargeur.add(cheminChargeur);
                }
            }

        }

        //Retourne le plus petit de tous les chemin valide
        if(!cheminsChargeurValide.isEmpty())
            return cheminsChargeurValide.poll();

        //Retourne un chemin invalide
        return new Chemin();
    }

    //Cherche le plus petit chemin directe
    private Chemin plusPetitCheminDirecte(Integer debut, Integer fin, PoidType poid, DroneType drone) {

        Sommet startingNode = sommets.get(debut);

        HashMap<Integer, SommetDjikstra> sommetsTerminer = new HashMap<>();
        PriorityQueue<SommetDjikstra> file = new PriorityQueue<>();

        file.add(new SommetDjikstra(startingNode.id, startingNode.id, 0, BATTERIE_REMPLIE));

        if (debut == fin) {
            SommetDjikstra sommet = file.poll();
            sommetsTerminer.put(sommet.id, sommet);
        }

        while (!sommetsTerminer.containsKey(fin) && !file.isEmpty()) {
            SommetDjikstra sommet = file.poll();

            //Si le niveau de batterie devient critique on cesse de continuer à l'explorer
            if (sommet.niveauBatterie < BATTERIE_CRITIQUE)
                continue;

            //Si on connait deja le plus court chemin vers un sommet on peut le sauter
            if (sommetsTerminer.containsKey(sommet.id))
                continue;

            sommetsTerminer.put(sommet.id, sommet);

            //Met a jour la distance de chaque sommet adjacent au sommet actuel
            for (Map.Entry<Integer, Integer> arc : sommets.get(sommet.id).adjacent.entrySet()) {
                //Seulement ajouter les sommets auquels on ne connait pas les plus petit chemin
                if (!sommetsTerminer.containsKey(arc.getKey())) {
                    //Batterie perdue
                    Double batteriePerdu = arc.getValue() * consommationBatterieMinute(poid, drone);
                    //Niveau de batterie en arrivant au sommet
                    Double nouveauNiveauBatterie = sommet.niveauBatterie - batteriePerdu;
                    //Temps de voyagement
                    Integer tempsVoyagement = sommet.tempsVoyagement + arc.getValue();

                    //Si le sommet est une station de recharge
                    //Ajouter le temps de recharge
                    //Remplir la batterie
                    if (sommets.get(sommet.id).chargeur && sommet.niveauBatterie != BATTERIE_REMPLIE) {
                        tempsVoyagement += TEMPS_RECHARCHE;
                        nouveauNiveauBatterie = BATTERIE_REMPLIE - batteriePerdu;
                    }

                    file.add(new SommetDjikstra(arc.getKey(), sommet.id, tempsVoyagement, nouveauNiveauBatterie));
                }
            }
        }

        if(!sommetsTerminer.containsKey(fin))
            return new Chemin();

        List<Integer> chemin = new ArrayList<>();
        HashSet<Integer> chargeursVisiter = new HashSet<>();

        SommetDjikstra actuel = sommetsTerminer.get(fin);
        chemin.add(actuel.id);

        do {
            actuel = sommetsTerminer.get(actuel.provenance);
            chemin.add(actuel.id);

            if (sommets.get(actuel.id).chargeur)
                chargeursVisiter.add(actuel.id);

        } while (actuel.provenance != actuel.id);

        //Si on est rendu à ce point le chemin est clairement valide
        return new Chemin(chemin, sommetsTerminer.get(fin).tempsVoyagement, sommetsTerminer.get(fin).niveauBatterie, chargeursVisiter, drone, true);
    }

    private double consommationBatterieMinute(PoidType poid, DroneType drone) {
        switch (drone) {
            case Petit:
            switch (poid) {
                case Lourd:
                    return 4.0;
                case Moyen:
                    return 2.0;
                case Plume:
                    return 1.0;
            }
        case Large:
            switch (poid) {
                case Lourd:
                    return 2.5;
                case Moyen:
                    return 1.5;
                case Plume:
                    return 1.0;
            }
        default:
            return 1.0;
        }
    }

    @Override
    public String toString() {
        StringBuilder graphe = new StringBuilder();

        for (Map.Entry<Integer, Sommet> sommet : sommets.entrySet()) {
            graphe.append("(" + sommet.getValue().id + ", (");
            for (Map.Entry<Integer, Integer> vertex : sommet.getValue().adjacent.entrySet()) {
                graphe.append("(" + vertex.getKey() + "," + vertex.getValue() + "), ");
            }
            //Enleve le dernier "," qui a été ajouté
            graphe.delete(graphe.length() - 2, graphe.length());
            graphe.append("))\n");
        }

        return graphe.toString();
    }
}