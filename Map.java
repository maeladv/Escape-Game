import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Map extends JPanel {
    int width;
    int height;
    Joueur joueur;
    String[] mapPath = {"assets/map1.png"};
    BufferedImage mapImage;
    ArrayList<Rectangle> murs = new ArrayList<>();

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
        joueur = new Joueur("Joueur", width/2-20, height/2-20, 10);
        // Exemple de murs (à modifier par l'utilisateur)
        murs.add(new Rectangle(100, 100, 200, 40));
        murs.add(new Rectangle(300, 200, 40, 200));
        setFocusable(true);
        requestFocusInWindow();
        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                int nextX = joueur.x;
                int nextY = joueur.y;
                int taille = 40;
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_LEFT:
                        nextX -= joueur.speed;
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        nextX += joueur.speed;
                        break;
                    case java.awt.event.KeyEvent.VK_UP:
                        nextY -= joueur.speed;
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        nextY += joueur.speed;
                        break;
                }
                Rectangle nextPos = new Rectangle(nextX, nextY, taille, taille);
                boolean collision = false;
                for (Rectangle mur : murs) {
                    if (nextPos.intersects(mur)) {
                        collision = true;
                        break;
                    }
                }
                if (!collision) {
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
        // Dessiner les murs pour debug (en couleur vive)
        g.setColor(Color.MAGENTA);
        for (Rectangle mur : murs) {
            g.fillRect(mur.x, mur.y, mur.width, mur.height);
        }
        if (joueur != null) {
            joueur.afficher(g);
        }
    }
}
