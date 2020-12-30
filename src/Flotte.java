/*
 * Implementation de la classe Flotte
 * file Flotte.java
 * authors Alexis Foulon, Florence Cloutier et Jonathan Siclait
 *
 * Ce programme contient les methodes qui ont pour but la construction de la classe
 * Flotte et l'implementation des methodes de cette derniere.
 */
import java.util.*;

public class Flotte {
    List<Drone> drones3;
    List<Drone> drones5;
    final int nbDrone3 = 10, nbDrone5 = 5;

    Queue<List<Requete>> requetesEnAttente;
    List<Requete> requetesValides;
    HashMap<String, Integer> nbDronesDansQuartiers;
    List<String> quartiersValides;

    /**
     * Constructeur par parametres
     * @param requetesEnAttente
     * @param requetesValides
     * @param nbDronesDansQuartiers
     * @param quartiersValides
     */
    Flotte(Queue<List<Requete>> requetesEnAttente, List<Requete> requetesValides,
           HashMap<String, Integer> nbDronesDansQuartiers, List<String> quartiersValides) {
        this.requetesEnAttente = requetesEnAttente;
        this.requetesValides = requetesValides;
        this.nbDronesDansQuartiers = nbDronesDansQuartiers;
        this.quartiersValides = quartiersValides;

        // Initialisation drones 3.3 amperes
        drones3 = new ArrayList<Drone>(nbDrone3);
        int idxQuartier = 0;
        for (int i = 0; i < nbDrone3; ++i) {
            drones3.add(new Drone(quartiersValides.get(idxQuartier)));
            idxQuartier++;
        }

        // Initialisation drones 5.0 amperes
        drones5 = new ArrayList<Drone>(nbDrone5);
        for (int j = 0; j < nbDrone5; ++j) {
            drones5.add(new Drone(quartiersValides.get(idxQuartier)));
            idxQuartier++;
        }
    }

    /**
     * Methode qui traduit l'emplacement des drones dans les differents quartiers dans une chaine de caracteres
     * @param nbDronesDansQuartiers
     * @return
     */
    public String printDronesQuartiers(HashMap<String, Integer> nbDronesDansQuartiers) {
        StringBuilder mapAsString = new StringBuilder();
        for (String key : nbDronesDansQuartiers.keySet()) {
            mapAsString.append("Code postal quartier: " + key + "=" + nbDronesDansQuartiers.get(key) + ", \n");
        }
        mapAsString.append("Tous les autres quartiers sont depourvus de drone\n");
        return mapAsString.toString();
    }

    /**
     * Methode qui retrouve le nombre de colis moyen transporte par les deux types de drones
     * @param type
     * @return
     */
    public double colisMoyenDrones(Graphe.DroneType type) {
        double nbColisMoyen = 0.0;
        if (type == Graphe.DroneType.Petit) {
            double nbColis = 0.0;
            for (Drone drone : drones3) {
                for (List<Requete> delivery : drone.livraisons)
                    nbColis = nbColis + delivery.size();
            }
            nbColisMoyen = nbColis / nbDrone3;
        } else if (type == Graphe.DroneType.Large){
            double nbColis = 0.0;
            for (Drone drone : drones5) {
                for (List<Requete> delivery : drone.livraisons) {
                    nbColis = nbColis + delivery.size();
                }
            }
            nbColisMoyen = nbColis / nbDrone5;
        }
        return nbColisMoyen;
    }

    /**
     * Methode qui assigne un colis au type approprie de drone
     */
    public void assignerColis() {
        Deque<List<Requete>> sortedValidRequests = findMatches();

        while (!sortedValidRequests.isEmpty()) {
            List<Requete> listeMemeAdr = sortedValidRequests.poll();
            int poidsTotal = calculPoidsTotalDelivery(listeMemeAdr);

            if (poidsTotal > 5000) {
                for (Requete uneRequete : listeMemeAdr) {
                    List<Requete> newList = new ArrayList<>();
                    newList.add(uneRequete);
                    sortedValidRequests.addLast(newList);
                }
            }
            else if (poidsTotal > 1000) {
                boolean isAssigned = assignerDrone(drones5, listeMemeAdr);
                if (!isAssigned)
                    requetesEnAttente.add(listeMemeAdr);
            }
            else if (poidsTotal <= 1000) {
                boolean isAssigned = assignerDrone(drones3, listeMemeAdr);
                if (!isAssigned)
                    requetesEnAttente.add(listeMemeAdr);
            }
        }
    }

    /**
     * Methode qui trouve les colis qui ont le meme trajet de livraison
     * @return
     */
    public Deque<List<Requete>> findMatches() {
        HashMap<String, List<Requete>> sortedValidRequests = new HashMap<>();
        for (Requete uneRequete : requetesValides) {
            String key = uneRequete.depart + uneRequete.destination;
            if (sortedValidRequests.containsKey(key))
                sortedValidRequests.get(key).add(uneRequete);
            else {
                List<Requete> requetes = new ArrayList<>();
                requetes.add(uneRequete);
                sortedValidRequests.put(key, requetes);
            }
        }
        Deque<List<Requete>> requetesValides = new LinkedList<>();
        for (Map.Entry<String, List<Requete>> entry : sortedValidRequests.entrySet()) {
            requetesValides.add(entry.getValue());
        }
        return requetesValides;
    }

    /**
     * Methode qui assigne un drone du bon type a un colis
     * @param drones
     * @param listeMemeAdr
     * @return
     */
    public boolean assignerDrone(List<Drone> drones, List<Requete> listeMemeAdr) {
        boolean isAssigned = false;
        for (Drone drone : drones) {
            if (!drone.isBusy && drone.currentLocation.equals(listeMemeAdr.get(0).depart)) {
                drone.livraisons.add(listeMemeAdr);
                drone.toLocation = listeMemeAdr.get(0).destination;
                drone.isBusy = true;
                isAssigned = true;
                break;
            }
        }
        return isAssigned;
    }

    /**
     * Methode qui calcule le poids total d'une livraison a un ou plusieurs colis
     * @param requetes
     * @return
     */
    public int calculPoidsTotalDelivery(List<Requete> requetes) {
        int poidsTotal = 0;
        for (Requete uneRequete : requetes)
            poidsTotal += uneRequete.poids;
        return poidsTotal;
    }

    /**
     * Methode qui equilibre la flotte apres un cycle de requetes
     */
    public void equilibrerFlotte() {
        finCycle();

        // Departs des requetes en attentes prioritaire!
        List<String> quartiersHabites = new ArrayList<>(); // Keep track of assigned quartiers
        Queue<List<Requete>> nePeuventEtreTraite = new LinkedList<>(); // Requetes qu'il faut remettre dans le queue
        while (!requetesEnAttente.isEmpty()) {
            List<Requete> requeteEnAttente = requetesEnAttente.poll();
            int poids = calculPoidsTotalDelivery(requeteEnAttente);

            // Livraison pour les drones 5 amperes
            if (poids > 1000)
                equilibrerListeAttenteDrone(requeteEnAttente, quartiersHabites, nePeuventEtreTraite, drones5);

                // Livraison pour les drones 3 amperes
            else equilibrerListeAttenteDrone(requeteEnAttente, quartiersHabites, nePeuventEtreTraite, drones3);
        }
        for (List<Requete> uneRequeteNonTraitee : nePeuventEtreTraite)
            requetesEnAttente.add(uneRequeteNonTraitee);

        int idxQuartier = 0;
        idxQuartier = equilibrerDronesRestant(quartiersHabites, drones3, idxQuartier);
        equilibrerDronesRestant(quartiersHabites, drones5, idxQuartier);

        requetesValides.clear();
        nbDronesDansQuartiers.clear();
        trouverNbDronesQuartiers();
    }

    /**
     * Methode qui trouve le nombre de drones dans chacun des quartiers
     */
    private void trouverNbDronesQuartiers() {
        for (Drone drone : drones3) {
            if (nbDronesDansQuartiers.containsKey(drone.currentLocation)) {
                Integer nbDrones = nbDronesDansQuartiers.get(drone.currentLocation);
                nbDronesDansQuartiers.replace(drone.currentLocation, ++nbDrones);
            } else {
                nbDronesDansQuartiers.put(drone.currentLocation, 1);
            }
        }
        for (Drone drone : drones5) {
            if (nbDronesDansQuartiers.containsKey(drone.currentLocation)) {
                Integer nbDrones = nbDronesDansQuartiers.get(drone.currentLocation);
                nbDronesDansQuartiers.replace(drone.currentLocation, ++nbDrones);
            } else {
                nbDronesDansQuartiers.put(drone.currentLocation, 1);
            }
        }
    }

    /**
     * Methode qui s'occupe des drones effectuant une livraison lors d'un cycle pour les rendre a nouveau disponible
     * dans le quartier de destination
     */
    private void finCycle() {
        for (Drone drone : drones3) {
            if (drone.toLocation != null) {
                drone.currentLocation = drone.toLocation;
                drone.toLocation = null;
                drone.isBusy = false;
            }
        }
        for (Drone drone : drones5) {
            if (drone.toLocation != null) {
                drone.currentLocation = drone.toLocation;
                drone.toLocation = null;
                drone.isBusy = false;
            }
        }
    }

    /**
     * Methode qui envoie les drones a un quartier de depart d'une livraison en attente et qui assigne cette meme
     * livraison au drone a la fin d'un cycle
     * @param requeteEnAttente
     * @param quartiersHabites
     * @param nePeuventEtreTraite
     * @param drones
     */
    private void equilibrerListeAttenteDrone(List<Requete> requeteEnAttente, List<String> quartiersHabites,
                                             Queue<List<Requete>> nePeuventEtreTraite, List<Drone> drones) {
        boolean assigned = false;
        for (Drone drone : drones) {
            if (!drone.isBusy) {
                assignerColisAttente(requeteEnAttente, drone); // Assigne prioritairement ce colis
                assigned = true;
                quartiersHabites.add(requeteEnAttente.get(0).depart);
                break;
            }
        }
        if (!assigned)
            nePeuventEtreTraite.add(requeteEnAttente);
    }

    /**
     * Methode qui equilibre les drones qui n'ont pas ete assignes a des colis de la liste de livraisons en attente
     * @param quartiersHabites
     * @param drones
     * @param idxQuartier
     * @return
     */
    private int equilibrerDronesRestant(List<String> quartiersHabites, List<Drone> drones, int idxQuartier) {
        for (Drone drone : drones) {
            if (!drone.isBusy) {
                while (quartiersHabites.contains(quartiersValides.get(idxQuartier)))
                    idxQuartier++;
                drone.currentLocation = quartiersValides.get(idxQuartier);
                idxQuartier++;
            }
        }
        return idxQuartier;
    }

    /**
     * Methode qui assigne prioritairement les drones aux colis la liste de livraisons en attente
     * @param requete
     * @param drone
     */
    private void assignerColisAttente(List<Requete> requete, Drone drone) {
        drone.currentLocation = requete.get(0).depart;
        drone.toLocation = requete.get(0).destination;
        drone.isBusy = true;
        drone.livraisons.add(requete);
    }
}
