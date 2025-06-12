import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Joueur {
    int x, y; // Position du joueur
    String nom;
    int state; // 0 : gauche, 1 : droite, 2 : haut, 3 : bas
    int speed;
    // Ordre : gauche, droite, haut, bas
    String[] image_paths = {"assets/joueur/gauche.png", "assets/joueur/droite.png", "assets/joueur/haut.png", "assets/joueur/bas.png"};
    BufferedImage[] joueurImages = new BufferedImage[4];

    public Joueur(String nom, int x, int y, int speed) {
        this.nom = nom;
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
}
