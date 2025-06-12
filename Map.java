import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Map extends JPanel {
    int width;
    int height;
    Joueur joueur;
    String[] mapPath = {"assets/map1.png"};
    BufferedImage mapImage;
    ArrayList<Rectangle> murs = new ArrayList<>();
    boolean devMode = true; // Option de développement, à désactiver en prod

    private List<Layer> layers;

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
        // Murs
        // bords de la map
        murs.add(new Rectangle (-20,0,20,this.height));
        murs.add(new Rectangle (0, -20,this.width, 20));
        murs.add(new Rectangle(785,0,10,this.height));
        murs.add(new Rectangle(0,this.height,this.width,20));

        // mur bas gauche
        murs.add(new Rectangle(0,450,150,50));
        // mur bas centre
        murs.add(new Rectangle(290,450,90,50));
        murs.add(new Rectangle(380,450,48,50));

        //mur vertical
        murs.add(new Rectangle(370,90,10,this.height));
        murs.add(new Rectangle(525,275,10,this.height));
        
        // mur bas droite
        murs.add(new Rectangle(540,430,40,50));
        murs.add(new Rectangle(690,440,90,50));

        //mur millieu droite
        murs.add(new Rectangle(660,270,150,50));

        // mur millieu
        murs.add(new Rectangle(250,265,340,50));

        // mur millieu gauche
        murs.add(new Rectangle(75,265,105,50));
        murs.add(new Rectangle(75,285,10,50));

        // mur haut gauche
        murs.add(new Rectangle(0,0,190,135));

        // mur haut centre
        murs.add(new Rectangle(245,90,200,50));

        // mur haut droite
        murs.add(new Rectangle(580,0,200,130));

        // tables
        murs.add(new Rectangle(200, 355, 35, 30));
        murs.add(new Rectangle(600, 515, 45, 20));

        


        layers = new ArrayList<>();

        // Initialisation des couches
        Layer backgroundLayer = new Layer();
        Layer playerLayer = new Layer();
        Layer topLayer = new Layer();
        BufferedImage map2Image = null;
        try {
            map2Image = ImageIO.read(new File("assets/map2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        final BufferedImage finalMap2Image = map2Image;
        topLayer.addElement(new Drawable() {
            @Override
            public void draw(Graphics g) {
                if (finalMap2Image != null) {
                    g.drawImage(finalMap2Image, 0, 0, width, height, null);
                }
            }
        });

        // Ajouter la map au layer de fond
        backgroundLayer.addElement(new Drawable() {
            @Override
            public void draw(Graphics g) {
                if (mapImage != null) {
                    g.drawImage(mapImage, 0, 0, width, height, null);
                }
            }
        });

        // Ajouter le joueur au layer joueur
        playerLayer.addElement(new Drawable() {
            @Override
            public void draw(Graphics g) {
                joueur.afficher(g);
            }
        });

        // Ordre : fond, joueur, topLayer (toujours au-dessus)
        layers.add(backgroundLayer);
        layers.add(playerLayer);
        layers.add(topLayer);

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
                Rectangle nextPos = new Rectangle(nextX, nextY + ((2 * taille) / 3), taille, (taille / 3));
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
        // Option dev : afficher la position du clic souris
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (devMode && e.getButton() == java.awt.event.MouseEvent.BUTTON3) { // Clic droit
                    deplacerJoueurVersSouris(e.getX(), e.getY());
                } else if (devMode) { // Clic gauche ou autre
                    System.out.println("Clic à la position : x=" + e.getX() + ", y=" + e.getY());
                }
            }
        });
    }

    // Méthode pour déplacer le joueur aux coordonnées de la souris
    private void deplacerJoueurVersSouris(int mouseX, int mouseY) {
        if (devMode) {
            joueur.setPosition(mouseX - 20, mouseY - 20); // Centrer le joueur sur le clic
            repaint();
        }
    }

    // Mettre à jour la position du joueur
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Layer layer : layers) {
            layer.draw(g);
        }
        // Dessiner les murs pour debug (en couleur vive)
        if (devMode ) {
            g.setColor(new Color(255,255,255,50));
        } else {
            g.setColor(new Color(0, 0, 0, 0)); // Transparent color
        }
        for (Rectangle mur : murs) {
            g.fillRect(mur.x, mur.y, mur.width, mur.height);
        }
        // SUPPRIME : dessin du joueur hors layers
    }



    // getteurs 
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
}
