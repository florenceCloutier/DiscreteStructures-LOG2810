import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;

public class Menu {
    //Variable de GUI
    public JPanel mainPanel;
    private JPanel optionsPanel;
    private JButton droneButton;
    private JButton recipeButton;
    private JButton quitProgramButton;
    private JPanel dronePanel;
    private JPanel recipePanel;
    private JPanel droneOptions;
    private JButton refreshMapButton;
    private JButton smallestPathButton;
    private JButton quitDroneButton;
    private JButton createAutomateButton;
    private JButton treatRequestButton;
    private JButton showStatsButton;
    private JPanel recipeOption;
    private JButton createRecipeButton;
    private JButton hasseDiagramButton;
    private JButton quitRecipeButton;
    private JPanel droneFunctionalityPanel;
    private JPanel refreshMapPanel;
    private JPanel shortestPathPanel;
    private JPanel statsPanel;
    private JPanel automatePanel;
    private JPanel treatRequestPanel;
    private JTextField graphFileTextFeild;
    private JButton createGraphButton;
    private JTextField startField;
    private JTextField endField;
    private JComboBox weightBox;
    private JButton calculatePathButton;
    private JTextArea graphOutput;
    private JTextArea shortestPathOutput;
    private JTextField automateFilePath;
    private JButton createAutomateFromFileButton;
    private JLabel automateCreationOutput;
    private JTextField requestFilePath;
    private JButton treatButton;
    private JLabel requestOutput;
    private JTextArea statsOutput;
    private JPanel recipeFunctionnality;
    private JPanel createRecipeGraphPanel;
    private JPanel hasseDiagramPanel;
    private JTextField recipePathInput;
    private JButton createRecipeGraphButton;
    private JTextArea recipeGraphOutput;
    private JTextArea hasseDiagramOutput;

    //Variable de donnees
    Graphe graphe = null;
    StartUp startUp = null;
    GrapheOriente recettes = null;

    public Menu() {
        //Navigation pour le GUI
        droneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cl = (CardLayout)mainPanel.getLayout();
                cl.show(mainPanel, "dronePanel");
            }
        });
        recipeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cl = (CardLayout)mainPanel.getLayout();
                cl.show(mainPanel, "recipePanel");
            }
        });
        quitProgramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                System.exit(0);
            }
        });
        quitDroneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cl = (CardLayout)mainPanel.getLayout();
                cl.show(mainPanel, "optionsPanel");
            }
        });
        quitRecipeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cl = (CardLayout)mainPanel.getLayout();
                cl.show(mainPanel, "optionsPanel");
            }
        });
        refreshMapButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cl = (CardLayout)droneFunctionalityPanel.getLayout();
                cl.show(droneFunctionalityPanel, "refreshMap");

                if (graphe != null) {
                    graphOutput.setText("Graphe actuel\n" + graphe.toString());
                }
            }
        });
        smallestPathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cl = (CardLayout)droneFunctionalityPanel.getLayout();
                cl.show(droneFunctionalityPanel, "shortestPath");

                if(graphe == null) {
                    shortestPathOutput.setText("Veuillez d'abord creer un graphe dans la section \"Mettre à jour la carte\".");
                } else {
                    shortestPathOutput.setText("");
                }
            }
        });
        showStatsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cl = (CardLayout)droneFunctionalityPanel.getLayout();
                cl.show(droneFunctionalityPanel, "stats");

                if (startUp == null) {
                    statsOutput.setText("Aucune statistique a afficher." +
                            "\nAller dans la section \"Créer l’automate\" pour specifier une liste d'adresse valide." +
                            "\nEnsuite, proceder au traitement d'une requete dans la section \"Traiter des requêtes\"");
                } else {
                    statsOutput.setText(startUp.getStats());
                }
            }
        });
        createAutomateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cl = (CardLayout)droneFunctionalityPanel.getLayout();
                cl.show(droneFunctionalityPanel, "automate");
            }
        });
        treatRequestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cl = (CardLayout)droneFunctionalityPanel.getLayout();
                cl.show(droneFunctionalityPanel, "treatRequest");

                if (startUp == null) {
                    requestOutput.setText("Veuillez tout d'abord creer un automate dans la section \"Créer l’automate\".");
                } else {
                    requestOutput.setText("");
                }
            }
        });
        createRecipeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cl = (CardLayout)recipeFunctionnality.getLayout();
                cl.show(recipeFunctionnality, "createRecipeGraph");

                if (recettes == null) {
                    recipeGraphOutput.setText("");
                } else {
                    recipeGraphOutput.setText("Graphe Actuel :\n" + recettes.toString());
                }
            }
        });
        hasseDiagramButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                CardLayout cl = (CardLayout)recipeFunctionnality.getLayout();
                cl.show(recipeFunctionnality, "hasseDiagram");

                if (recettes == null) {
                    hasseDiagramOutput.setText("Veuillez créer un graphe dans la section \"Créer et afficher le graphe des recettes\" pour en voir le diagramme de Hasse.");
                    return;
                }

                hasseDiagramOutput.setText(recettes.genererHasse());
            }
        });

        //Action Listener pour les fonctionnalités du programme

        //Créer un graphe à partir d'un fichier
        createGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String filePath = graphFileTextFeild.getText();
                try {
                    graphe = new Graphe(filePath);
                    graphOutput.setText(graphe.toString());
                } catch (FileNotFoundException e) {
                    graphe = null;
                    graphOutput.setText("Impossible d'ouvrir le fichier : " + filePath);
                }
            }
        });

        //Calculer le plus court chemin
        calculatePathButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                //Il faut un graphe pour calculer le plus petit chemin
                if (graphe == null) {
                    shortestPathOutput.setText("Veuillez d'abord creer un graphe dans la section \"Mettre à jour la carte\".");
                    return;
                }

                //Caluler le plus court chemin
                try {
                    int debut = Integer.parseInt(startField.getText());
                    int fin = Integer.parseInt(endField.getText());

                    //On regarde si les sommets specifieés sont dans le graphe
                    if (!graphe.sommets.containsKey(debut) || !graphe.sommets.containsKey(fin)) {
                        shortestPathOutput.setText("Veuillez entrer un point de depart et d'arrive contenue dans le graphe.");
                        return;
                    }

                    Graphe.PoidType poids = getPoids();

                    Chemin shortest = graphe.cheminEntre(debut, fin, poids);
                    shortestPathOutput.setText(shortest.toString());

                }
                //Si il n'y a pas de nombre valide donner par l'utilisateurs
                catch (NumberFormatException e) {
                    shortestPathOutput.setText("Veuillez entrer un nombre valide dans les champs \"Depart\" et \"Arrive\".");
                    return;
                }
            }
        });
        
        //Creation de l'automate qui detecte les codes postaux
        createAutomateFromFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String cheminFichier = automateFilePath.getText();

                try {
                    startUp = new StartUp(cheminFichier);
                    automateCreationOutput.setText("Creation d'un automate permettant de detecter les adresses valides reussis.");
                } catch (FileNotFoundException e) {
                    automateCreationOutput.setText("Impossible d'ouvrir le fichier : " + cheminFichier);
                }
            }
        });

        //Traitement des requetes
        treatButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (startUp == null)
                    return;

                String cheminFichier = requestFilePath.getText();
                try {
                    startUp.traiterLesRequetes(cheminFichier);
                    requestOutput.setText("La requete a ete traiter.");
                } catch (FileNotFoundException e) {
                    requestOutput.setText("Impossible d'ouvrir le fichier : " + cheminFichier);
                }
            }
        });
        createRecipeGraphButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String chemin = recipePathInput.getText();

                try {
                    recettes = new GrapheOriente(chemin);
                    recipeGraphOutput.setText(recettes.toString());
                } catch (FileNotFoundException e) {
                    recipeGraphOutput.setText("Impossible d'ouvrir le fichier : " + chemin);
                }
            }
        });
    }

    private Graphe.PoidType getPoids() {
        switch (weightBox.getSelectedIndex()) {
            case 1:
                return Graphe.PoidType.Moyen;
            case 2:
                return Graphe.PoidType.Lourd;
            default:
                return Graphe.PoidType.Plume;
        }
    }
}
