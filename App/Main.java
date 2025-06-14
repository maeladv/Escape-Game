package App;
import javax.swing.*;

import App.Controllers.GameController;
import App.Controllers.InputHandler;
import App.Inventaire.Inventaire;
import App.Inventaire.InventaireUI;
import App.Inventaire.Item;
import App.Joueur.Joueur;
import App.Map.Map;
import App.Utils.Drawable;
import App.Utils.GameUtils;

import java.awt.*;
import java.io.File;

public class Main {
    // Variables pour les composants du jeu
    private static JFrame window;
    private static Map map;
    private static Inventaire inventaire;
    private static InventaireUI inventaireUI;
    private static Joueur joueur;
    private static GameController gameController;
    private static InputHandler inputHandler;
    
    // Configuration globale du jeu (centralisée)
    private static boolean devMode = true;
    private static int playerSize = 40;
    private static int interactionZoneSize = 5; // Marge autour du joueur pour les interactions

    private static void initialiserFenetre() {
        // Créer une fenêtre Jframe avec un titre
        window = new JFrame("Escape From The Biblioteca");
        // Faire en sorte que le script se coupe lorsque l'utilisateur clique sur la croix
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Créer et initialiser l'inventaire
        inventaire = new Inventaire(5);
        // Créer l'interface de l'inventaire
        inventaireUI = new InventaireUI(inventaire);
        
        // Positionner l'inventaire en haut à gauche
        inventaireUI.setBounds(10, 10, 400, 100);
          // Créer et ajouter la map
        map = new Map(devMode);
        
        // Créer le joueur
        joueur = new Joueur(30, 460, 10);
        
        // Ajouter le joueur au layer du milieu de la map
        map.addPlayerLayerElement(new Drawable() {
            @Override
            public void draw(Graphics g) {
                joueur.afficher(g);
            }
        });
          // Créer le GameController pour gérer la logique du jeu
        gameController = new GameController(map, joueur, inventaire, inventaireUI, devMode, playerSize, interactionZoneSize);
        
        // Créer l'InputHandler pour gérer les entrées utilisateur
        inputHandler = new InputHandler(gameController, map, joueur);
          
        // Utiliser un BorderLayout pour organiser les composants
        window.setLayout(new BorderLayout());
        window.add(map, BorderLayout.CENTER);
        
        // Ajouter l'inventaire à la carte en tant que composant flottant
        map.setLayout(new BorderLayout());
        map.add(inventaireUI, BorderLayout.NORTH);
        
        window.pack();
        // Empêcher la redimension de la fenêtre
        window.setResizable(false);
        // Ouvrir la fenêtre au centre de l'écran
        window.setLocationRelativeTo(null);
        // Afficher la fenêtre
        window.setVisible(true);
    }

    // fonction principale
    public static void main(String[] args) {
        // Créer un dossier "objets" s'il n'existe pas
        File objetsDir = new File("assets/objets");
        if (!objetsDir.exists()) {
            objetsDir.mkdirs();
        }
        
        // Exécuter l'initialisation de la fenêtre dans l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            initialiserFenetre();

            
            // Ajouter des items à l'inventaire
            try {
                Item balai = new Item("Balai", "Un balai pour nettoyer les lieux", new File("assets/items/balai.png"));
                inventaire.ajouterItem(balai);
                
                Item livre = new Item("Livre", "Un livre ancien avec des inscriptions mystérieuses", new File("assets/joueur/gauche.png"));
                inventaire.ajouterItem(livre);
                
                Item clef = new Item("Clef", "Une clef en métal qui pourrait ouvrir une porte", new File("assets/joueur/haut.png"));
                inventaire.ajouterItem(clef);
                  // Mettre à jour l'affichage de l'inventaire
                inventaireUI.updateInventaire(inventaire);
                
                // Afficher les items dans la console
                inventaire.getItems().forEach(item -> {
                    GameUtils.printDev("Item dans l'inventaire : " + item.getNom() + " - " + item.getDescription(), devMode);
                });
            } catch (Exception e) {
                GameUtils.printDev("Erreur lors de l'initialisation des items : " + e.getMessage(), devMode);
                e.printStackTrace();
            }
        });
    }
}
