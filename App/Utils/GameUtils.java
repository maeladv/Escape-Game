package App.Utils;

/**
 * Classe utilitaire qui contient des méthodes partagées par plusieurs classes
 * Permet de centraliser les méthodes communes pour éviter la duplication de code
 */
public class GameUtils {

    /**
     * Affiche un message de débogage si le mode développeur est activé
     * @param message Le message à afficher
     * @param devMode Booléen indiquant si le mode développeur est activé
     */
    public static void printDev(String message, boolean devMode) {
        if (devMode) {
            System.out.println(message);
        }
    }
    
    /**
     * Crée une zone d'interaction autour d'un point avec une marge donnée
     * @param x Coordonnée X du point central
     * @param y Coordonnée Y du point central
     * @param size Taille de l'objet
     * @param margin Marge autour de l'objet
     * @return Rectangle représentant la zone d'interaction
     */
    public static java.awt.Rectangle createInteractionZone(int x, int y, int size, int margin) {
        return new java.awt.Rectangle(x - margin, y - margin, size + (margin * 2), size + (margin * 2));
    }
    
    /**
     * Vérifie si le joueur regarde dans la direction d'un objet
     * @param joueurX Coordonnée X du joueur
     * @param joueurY Coordonnée Y du joueur
     * @param objetX Coordonnée X de l'objet
     * @param objetY Coordonnée Y de l'objet
     * @param joueurState État du joueur (0: gauche, 1: droite, 2: haut, 3: bas)
     * @return true si le joueur regarde dans la direction de l'objet, false sinon
     */
    public static boolean playerFacingObject(int joueurX, int joueurY, int objetX, int objetY, int joueurState) {
        int diffX = objetX - joueurX;
        int diffY = objetY - joueurY;
        
        switch (joueurState) {
            case 0: // Gauche
                return diffX < 0 && Math.abs(diffX) > Math.abs(diffY);
            case 1: // Droite
                return diffX > 0 && Math.abs(diffX) > Math.abs(diffY);
            case 2: // Haut
                return diffY < 0 && Math.abs(diffY) > Math.abs(diffX);
            case 3: // Bas
                return diffY > 0 && Math.abs(diffY) > Math.abs(diffX);
            default:
                return false;
        }
    }
}
