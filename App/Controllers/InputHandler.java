package App.Controllers;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import App.Joueur.Joueur;
import App.Map.Map;
import App.Utils.GameUtils;

/**
 * InputHandler manages all user inputs including keyboard and mouse events.
 * This separates input handling responsibility from the Map class.
 */
public class InputHandler {
    private GameController gameController;
    private Map map;
    private Joueur joueur;
    private int playerSize; // Taille du joueur obtenue de GameController
    
    /**
     * Constructor for InputHandler
     * @param gameController The game controller managing game state
     * @param map The map component to attach listeners to
     * @param joueur The player character to control
     */
    public InputHandler(GameController gameController, Map map, Joueur joueur) {
        this.gameController = gameController;
        this.map = map;
        this.joueur = joueur;
        this.playerSize = gameController.getPlayerSize();
        
        // Setup keyboard listener
        setupKeyboardListener();
        
        // Setup mouse listener (for dev mode)
        setupMouseListener();
    }
    
    /**
     * Setup keyboard event listener
     */
    private void setupKeyboardListener() {
        map.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // Only process inputs if player can move
                if (!joueur.canMove()) {
                    return;
                }
                
                int nextX = joueur.getX();
                int nextY = joueur.getY();
                int playerSize = gameController.getPlayerSize();
                boolean pressedA = false;
                
                // Handle key press
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        nextX -= joueur.getSpeed();
                        joueur.setState(0); // Left
                        break;
                    case KeyEvent.VK_RIGHT:
                        nextX += joueur.getSpeed();
                        joueur.setState(1); // Right
                        break;
                    case KeyEvent.VK_UP:
                        nextY -= joueur.getSpeed();
                        joueur.setState(2); // Up
                        break;
                    case KeyEvent.VK_DOWN:
                        nextY += joueur.getSpeed();
                        joueur.setState(3); // Down
                        break;
                    case KeyEvent.VK_A:
                        pressedA = true;
                        break;
                }
                
                // Check collision for movement keys
                if (e.getKeyCode() == KeyEvent.VK_LEFT || 
                    e.getKeyCode() == KeyEvent.VK_RIGHT || 
                    e.getKeyCode() == KeyEvent.VK_UP || 
                    e.getKeyCode() == KeyEvent.VK_DOWN) {
                    
                    // Only check the collision for the feet of the player
                    boolean collision = gameController.checkCollision(
                        nextX, 
                        nextY + ((2 * playerSize) / 3), 
                        playerSize, 
                        (playerSize / 3)
                    );
                    
                    // Move the player if no collision
                    if (!collision) {
                        switch (e.getKeyCode()) {
                            case KeyEvent.VK_LEFT:
                                joueur.deplacerGauche();
                                break;
                            case KeyEvent.VK_RIGHT:
                                joueur.deplacerDroite();
                                break;
                            case KeyEvent.VK_UP:
                                joueur.deplacerHaut();
                                break;
                            case KeyEvent.VK_DOWN:
                                joueur.deplacerBas();
                                break;
                        }
                    }
                }
                
                // Check for object interactions
                gameController.checkObjectInteractions(pressedA);
                gameController.checkDirectObjectCollisions(pressedA);
                
                // Repaint the map
                map.repaint();
            }
        });
    }
    
    /**
     * Setup mouse event listener (used in dev mode)
     */
    private void setupMouseListener() {
        map.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (gameController.isDevMode()) {
                    if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                        // Teleport player to mouse position in dev mode
                        joueur.setPosition(e.getX() - 20, e.getY() - 20);
                        map.repaint();
                    } else { // Left click or other
                        GameUtils.printDev("Clic à la position : x=" + e.getX() + ", y=" + e.getY(), gameController.isDevMode());
                    }
                }
            }
        });
    }
}
