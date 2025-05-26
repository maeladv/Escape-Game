import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Map extends JPanel {
    int width;
    int height;
    Joueur joueur;
    String[] mapPath = {"assets/map1.png"};
    BufferedImage mapImage;

    // create and init map
    public Map() {
        this.width = 800;
        this.height = 600;
        try {
            mapImage = ImageIO.read(new File(mapPath[0]));
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.setPreferredSize(new Dimension(width, height));
        // Création du joueur au centre
        joueur = new Joueur("Joueur", width/2-20, height/2-20, 10);
        // Ajout du KeyListener
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_LEFT:
                        joueur.deplacerGauche();
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        joueur.deplacerDroite();
                        break;
                    case java.awt.event.KeyEvent.VK_UP:
                        joueur.deplacerHaut();
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        joueur.deplacerBas();
                        break;
                }
                repaint();
            }
        });
    }


    // Mettre à jour la position du joueur
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (mapImage != null) {
            g.drawImage(mapImage, 0, 0, width, height, null);
        }
        if (joueur != null) {
            joueur.afficher(g);
        }
    }
}
