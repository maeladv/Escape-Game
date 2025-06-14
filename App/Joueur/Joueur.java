package App.Joueur;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Joueur {
    private int x, y; // Position du joueur
    private int state; // 0 : gauche, 1 : droite, 2 : haut, 3 : bas
    private int speed;
    private boolean canMove = true; // Indique si le joueur peut se déplacer
    // Ordre : gauche, droite, haut, bas
    private final String[] image_paths = {"assets/joueur/gauche.png", "assets/joueur/droite.png", "assets/joueur/haut.png", "assets/joueur/bas.png"};
    private BufferedImage[] joueurImages = new BufferedImage[4];

    public Joueur(int x, int y, int speed) {
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.state = 1; // par défaut à droite
        for (int i = 0; i < 4; i++) {
            System.out.println("Chargement image joueur : " + image_paths[i]);
            try {
                joueurImages[i] = ImageIO.read(new File(image_paths[i]));
                if (joueurImages[i] != null) {
                    System.out.println("Image chargée pour direction " + i);
                } else {
                    System.out.println("Image NULL pour direction " + i);
                }
            } catch (IOException e) {
                System.out.println("Erreur chargement image pour direction " + i + " : " + e.getMessage());
                joueurImages[i] = null;
            }
        }
    }

    public void afficher(Graphics g) {
        BufferedImage img = joueurImages[state];
        if (img != null) {
            g.drawImage(img, x, y, 40, 40, null);
        } else {
            g.setColor(Color.RED);
            g.fillOval(x, y, 40, 40);
        }
    }

    public void deplacerGauche() { x -= speed; state = 0; }
    public void deplacerDroite() { x += speed; state = 1; }
    public void deplacerHaut() { y -= speed; state = 2; }
    public void deplacerBas() { y += speed; state = 3; }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    // Getters et setters
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }
    public int getState() { return state; }
    public void setState(int state) { this.state = state; }
    public int getSpeed() { return speed; }
    public void setSpeed(int speed) { this.speed = speed; }
    public boolean isCanMove() { return canMove; }
    public void setCanMove(boolean canMove) { this.canMove = canMove; }
    public boolean canMove() { return canMove; } // Alias pour la compatibilité
    public BufferedImage[] getJoueurImages() { return joueurImages; }
    public String[] getImagePaths() { return image_paths; }
}
