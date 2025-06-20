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
    private static int playerSize = 40; // Taille du joueur par défaut en carré
    private static int interactionZoneSize = 5; // Marge autour du joueur pour les interactions

    private static void initialiserFenetre() {
        // Créer une fenêtre Jframe avec un titre
        window = new JFrame("Escape From The Biblioteca");
        // Faire en sorte que le script se coupe lorsque l'utilisateur clique sur la croix
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        // Créer et initialiser l'inventaire
        inventaire = new Inventaire(5);
        // Créer l'interface de l'inventaire
        inventaireUI = new InventaireUI(inventaire, devMode);
        
        // Positionner l'inventaire en haut à gauche
        inventaireUI.setBounds(10, 10, 400, 100);
          // Créer et ajouter la map
        map = new Map(devMode);
        
        // Créer le joueur
        joueur = new Joueur(30, 460, 10, devMode);

        playerSize = joueur.getPlayerWidth(); // Mettre à jour la taille du joueur depuis l'objet Joueur par défaut
        
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
        
        // Exécuter l'initialisation de la fenêtre dans l'EDT (Event Dispatch Thread)
        SwingUtilities.invokeLater(() -> {
            initialiserFenetre();
        });
    }
}
