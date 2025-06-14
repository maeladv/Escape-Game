package App.Controllers;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import App.Dialogue.DialogueManager;
import App.Inventaire.Inventaire;
import App.Inventaire.InventaireUI;
import App.Joueur.Joueur;
import App.Map.Map;
import App.Objets.Objet;

/**
 * GameController centralizes game logic and coordinates between different components.
 * This class is responsible for handling game state and managing interactions
 * between the player, map, inventory, and other game elements.
 */
public class GameController {
    private Map map;
    private Joueur joueur;
    private Inventaire inventaire;
    private InventaireUI inventaireUI;
    private DialogueManager dialogueManager;
    private boolean devMode = true;
    
    private int currentMapIndex = 0;
    private List<List<Objet>> objetsParMap;
    private List<List<Rectangle>> mursParMap;

    public GameController(Map map, Joueur joueur, Inventaire inventaire, InventaireUI inventaireUI) {
        this.map = map;
        this.joueur = joueur;
        this.inventaire = inventaire;
        this.inventaireUI = inventaireUI;
        
        // Initialize collections
        this.objetsParMap = new ArrayList<>();
        this.mursParMap = new ArrayList<>();
        
        // Create the DialogueManager
        this.dialogueManager = new DialogueManager(map);
        
        // Set the DialogueManager to the InventaireUI
        this.inventaireUI.setDialogueManager(dialogueManager);
        
        // Initialize the maps and objects
        initializeMapsAndObjects();
        
        // Show intro script if starting with the first map
        if (currentMapIndex == 0) {
            showIntroScript();
        }
    }
    
    /**
     * Initialize the maps and objects for the game
     */
    private void initializeMapsAndObjects() {
        // Initialize walls for map 0 (Introduction)
        List<Rectangle> mursIntro = new ArrayList<>();
        // bords de la map
        mursIntro.add(new Rectangle (-20, 0, 20, map.getHeightValue()));
        mursIntro.add(new Rectangle (0, -20, map.getWidthValue(), 20));
        mursIntro.add(new Rectangle(map.getWidthValue(), 0, 10, map.getHeightValue()));
        mursIntro.add(new Rectangle(0, map.getHeightValue(), map.getWidthValue(), 20));
        // limite chemin supérieure
        mursIntro.add(new Rectangle(0, 340, 520, 100));
        // limite inférieure
        mursIntro.add(new Rectangle(0, 520, map.getWidthValue(), 100));
        // porte d'entrée
        mursIntro.add(new Rectangle(520, 300, 120, 100));
        // suite porte à droite
        mursIntro.add(new Rectangle(640, 340, 200, 100));
        
        mursParMap.add(mursIntro);

        // Initialize objects for map 0 (Introduction)
        List<Objet> objetsIntro = new ArrayList<>();
        
        // Door object that changes map when interacted with
        objetsIntro.add(new Objet(
            new Rectangle(540, 310, 70, 110),
            () -> {
                changeMap(1);
                printDev("Interaction avec la porte de la map 0 ! Changement de map.");
                joueur.setPosition(50, 530);
                joueur.setState(1);
                map.repaint();
            }
        ));
        
        objetsParMap.add(objetsIntro);

        // Initialize walls for map 1 (Library)
        List<Rectangle> mursBibliotheque = new ArrayList<>();
        // bords de la map
        mursBibliotheque.add(new Rectangle (-20, 0, 20, map.getHeightValue()));
        mursBibliotheque.add(new Rectangle (0, -20, map.getWidthValue(), 20));
        mursBibliotheque.add(new Rectangle(map.getWidthValue() - 15, 0, 10, map.getHeightValue()));
        mursBibliotheque.add(new Rectangle(0, map.getHeightValue(), map.getWidthValue(), 20));
        // mur bas gauche
        mursBibliotheque.add(new Rectangle(0, 450, 150, 50));
        // mur bas centre
        mursBibliotheque.add(new Rectangle(290, 450, 90, 50));
        mursBibliotheque.add(new Rectangle(380, 450, 48, 50));
        mursBibliotheque.add(new Rectangle(480, 450, 40, 50));
        // mur vertical
        mursBibliotheque.add(new Rectangle(370, 90, 10, map.getHeightValue()));
        mursBibliotheque.add(new Rectangle(525, 275, 10, map.getHeightValue()));
        // mur bas droite
        mursBibliotheque.add(new Rectangle(540, 430, 40, 50));
        mursBibliotheque.add(new Rectangle(690, 440, 90, 50));
        // mur millieu droite
        mursBibliotheque.add(new Rectangle(660, 270, 150, 50));
        // mur millieu
        mursBibliotheque.add(new Rectangle(250, 265, 340, 50));
        // mur millieu gauche
        mursBibliotheque.add(new Rectangle(75, 265, 105, 50));
        mursBibliotheque.add(new Rectangle(75, 285, 10, 50));
        // mur haut gauche
        mursBibliotheque.add(new Rectangle(0, 0, 190, 135));
        // mur haut centre
        mursBibliotheque.add(new Rectangle(245, 90, 200, 50));
        // mur haut droite
        mursBibliotheque.add(new Rectangle(580, 0, 200, 130));
        // tables
        mursBibliotheque.add(new Rectangle(200, 355, 35, 30));
        mursBibliotheque.add(new Rectangle(600, 515, 45, 20));
        
        mursParMap.add(mursBibliotheque);

        // Initialize objects for map 1 (Library)
        List<Objet> objetsBibliotheque = new ArrayList<>();
        
        // Table with dialogue
        objetsBibliotheque.add(new Objet(
            new Rectangle(600, 510, 50, 20),
            () -> dialogueManager.afficherDialogue(
                "Ceci est une table de la bibliothèque. Appuyez sur OK pour continuer.", 
                "OK", 
                () -> joueur.setCanMove(true)
            )
        ));
        
        // Bookshelf with multi-step dialogue
        objetsBibliotheque.add(new Objet(
            new Rectangle(70, 480, 50, 20),
            () -> {
                String[] messages = {
                    "Ah! Il semblerait que l'on puisse interagir avec ces bibliotheques.",
                    "Ce vieux batiment est donc une bibliotheque.",
                    "Elle me semble très ancienne et poussiereuse. Certains murs tombent meme en ruines !",
                    "On dirait qu'elle n'a pas ete utilisee depuis des annees.",
                    "Peut-etre que quelqu'un a laisse un message ici ?",
                    "Il y a des livres sur les etageres, mais certaines sont encore trop poussiereuses pour etre ouvertes."
                };
                dialogueManager.afficherScript(messages, "Suivant", () -> joueur.setCanMove(true));
            }
        ));
        
        objetsParMap.add(objetsBibliotheque);
        
        // Set initial map's walls to Map class
        map.setMurs(new ArrayList<>(mursParMap.get(currentMapIndex)));
    }
    
    /**
     * Display the introduction script
     */
    private void showIntroScript() {
        String[] introScript = {
            "Bienvenue dans Escape From The Biblioteca !", 
            "Que voit-on au loin ? Une coline ? Un vieux village ?",
            "Je ne sais pas, cette foret est si sombre et si dense...",
            "Et que voici par ici ? Un vieux batiment ? Peut-etre un chateau abandonne ?",
            "Enfin, c'est etrange, il n'y a pas un bruit et des torches sont encore allumees !",
            "On pourrait croire que quelqu'un vit encore ici...",
            "Cette foret ne m'inspire pas confiance mais... le temps semble s'y etre fige.",
            "La nuit tombe, je n'ai d'autre choix que d'entrer dans ce batiment.",
        };
        
        // Show the introduction script
        javax.swing.SwingUtilities.invokeLater(() -> 
            dialogueManager.afficherScript(introScript, "Suivant", () -> joueur.setCanMove(true))
        );
    }
    
    /**
     * Change the current map
     * @param mapIndex The index of the map to change to
     */
    public void changeMap(int mapIndex) {
        currentMapIndex = mapIndex;
        map.setDisplayedMap(mapIndex);
        map.setMurs(new ArrayList<>(mursParMap.get(mapIndex)));
    }
    
    /**
     * Check if there is a collision at the given position
     * @param x X position to check
     * @param y Y position to check
     * @param width Width of the bounding box
     * @param height Height of the bounding box
     * @return true if there is a collision, false otherwise
     */
    public boolean checkCollision(int x, int y, int width, int height) {
        Rectangle nextPos = new Rectangle(x, y, width, height);
        for (Rectangle mur : map.getMurs()) {
            if (nextPos.intersects(mur)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Check for object interactions based on player position and state
     * @param pressedA True if the 'A' key is pressed
     */
    public void checkObjectInteractions(boolean pressedA) {
        int taille = 40; // Player size
        
        // Create interaction zone around player
        Rectangle zoneInteraction = new Rectangle(
            joueur.getX() - 5, joueur.getY() - 5, 
            taille + 10, taille + 10
        );
        
        // Check all objects in the current map
        for (Objet obj : objetsParMap.get(currentMapIndex)) {
            if (zoneInteraction.intersects(obj.getHitbox())) {
                // Calculate object and player centers for direction checking
                boolean regardeBonneDirection = false;
                int centreObjetX = obj.getHitbox().x + obj.getHitbox().width / 2;
                int centreObjetY = obj.getHitbox().y + obj.getHitbox().height / 2;
                int centreJoueurX = joueur.getX() + taille / 2;
                int centreJoueurY = joueur.getY() + taille / 2;
                
                // Check if player is facing the object
                int diffX = centreObjetX - centreJoueurX;
                int diffY = centreObjetY - centreJoueurY;
                
                switch (joueur.getState()) {
                    case 0: // Left
                        regardeBonneDirection = diffX < 0 && Math.abs(diffX) > Math.abs(diffY);
                        break;
                    case 1: // Right
                        regardeBonneDirection = diffX > 0 && Math.abs(diffX) > Math.abs(diffY);
                        break;
                    case 2: // Up
                        regardeBonneDirection = diffY < 0 && Math.abs(diffY) > Math.abs(diffX);
                        break;
                    case 3: // Down
                        regardeBonneDirection = diffY > 0 && Math.abs(diffY) > Math.abs(diffX);
                        break;
                }
                
                // Set object active state based on whether player is facing it
                obj.setActive(regardeBonneDirection);
                
                if (devMode && regardeBonneDirection) {
                    printDev("Objet interactif à proximité immédiate. Appuyez sur 'A' pour interagir.");
                }
                
                // Trigger the object if 'A' is pressed and player is facing it
                if (pressedA && regardeBonneDirection) {
                    obj.trigger();
                }
            } else {
                obj.setActive(false);
            }
        }
    }
    
    /**
     * Check for direct object collisions (touching objects)
     * @param pressedA True if the 'A' key is pressed
     */
    public void checkDirectObjectCollisions(boolean pressedA) {
        int taille = 40; // Player size
        
        // Create player hitbox
        Rectangle joueurBox = new Rectangle(joueur.getX(), joueur.getY(), taille, taille);
        
        // Check all objects in the current map
        for (Objet obj : objetsParMap.get(currentMapIndex)) {
            if (joueurBox.intersects(obj.getHitbox()) && pressedA) {
                obj.trigger();
            }
        }
    }
    
    /**
     * Debug print method
     * @param message Message to print
     */
    private void printDev(String message) {
        if (devMode) {
            System.out.println(message);
        }
    }
    
    // Getters and setters
    
    public Map getMap() {
        return map;
    }
    
    public Joueur getJoueur() {
        return joueur;
    }
    
    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }
    
    public List<List<Objet>> getObjetsParMap() {
        return objetsParMap;
    }
    
    public int getCurrentMapIndex() {
        return currentMapIndex;
    }
    
    public boolean isDevMode() {
        return devMode;
    }
    
    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }
}
