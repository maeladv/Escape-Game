import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.awt.Graphics;

// Ajout de l'import de la classe Objet
// (pas besoin d'import si Objet.java est dans le même dossier et sans package)

public class Map extends JPanel {
    int width;
    int height;
    Joueur joueur;
    int displayedMap = 0; // Indice de la map affichée, utile si on veut changer de map
    String[] mapPath = {"assets/maps/intro/map.png","assets/maps/library/map.png"};
    String[] secondLayerPath = {"assets/maps/intro/layer.png","assets/maps/library/layer.png"};
    BufferedImage mapImage; // permet de stocker l'image de la map
    ArrayList<Rectangle> murs = new ArrayList<>();
    boolean devMode = true; // Option de développement, à désactiver en prod

    // Liste de listes de murs, un ensemble de murs par map
    private List<List<Rectangle>> mursParMap = new ArrayList<>();

    private List<Layer> layers;

    // Système d'objets interactifs
    private List<List<Objet>> objetsParMap = new ArrayList<>();
    private ArrayList<Objet> objets = new ArrayList<>();

    // Création et initialisation de la map
    public Map() {
        this.width = 800;
        this.height = 600;
        // Chargement de l'image de la map
        try {
            mapImage = ImageIO.read(new File(mapPath[displayedMap]));
        } catch (IOException e) {
            e.printStackTrace();
            printDev("Erreur lors du chargement de l'image de la map : " + e.getMessage());
        }
        this.setPreferredSize(new Dimension(width, height));
        joueur = new Joueur("Joueur", width/2-20, height/2-20, 10);

        // Initialisation des murs pour chaque map

        // Map 0 : Introduction
        List<Rectangle> mursIntro = new ArrayList<>();
        // bords de la map
        mursIntro.add(new Rectangle (-20,0,20,this.height));
        mursIntro.add(new Rectangle (0, -20,this.width, 20));
        mursIntro.add(new Rectangle(this.width,0,10,this.height));
        mursIntro.add(new Rectangle(0,this.height,this.width,20));
        // limite chemin supérieure
        mursIntro.add(new Rectangle(0, 340, 520, 100));
        // limite inférieure
        mursIntro.add(new Rectangle(0, 520, this.width, 100));

        // porte d'entrée
        mursIntro.add(new Rectangle(520, 300, 120, 100));

        // suite porte à droite
        mursIntro.add(new Rectangle(640, 340, 200, 100));
    
        
        mursParMap.add(mursIntro);

        // Objets map 0 (intro)
        List<Objet> objetsIntro = new ArrayList<>();
        // Exemple : objet de test
        objetsIntro.add(new Objet(
            new Rectangle(300, 200, 60, 60),
            () -> printDev("Collision avec l'objet de test de la map 0 !")
        ));
        // Nouvel objet ajouté à la map 0
        objetsIntro.add(new Objet(
            new Rectangle(540, 420, 70, 110),
            () -> {
                setDisplayedMap(1);
                // Met à jour les murs et objets pour la nouvelle map
                murs = new ArrayList<>(mursParMap.get(displayedMap));
                objets = new ArrayList<>(objetsParMap.get(displayedMap));
                printDev("Collision avec la porte de la map 0 ! Changement de map.");
                repaint();
            }
        ));
        objetsParMap.add(objetsIntro);






        // Map 1 :  Bibliothèque
        List<Rectangle> mursBibliotheque = new ArrayList<>();
        // bords de la map
        mursBibliotheque.add(new Rectangle (-20,0,20,this.height));
        mursBibliotheque.add(new Rectangle (0, -20,this.width, 20));
        mursBibliotheque.add(new Rectangle(this.width - 15,0,10,this.height));
        mursBibliotheque.add(new Rectangle(0,this.height,this.width,20));
        // mur bas gauche
        mursBibliotheque.add(new Rectangle(0,450,150,50));
        // mur bas centre
        mursBibliotheque.add(new Rectangle(290,450,90,50));
        mursBibliotheque.add(new Rectangle(380,450,48,50));
        mursBibliotheque.add(new Rectangle(480,450,40,50));
        // mur vertical
        mursBibliotheque.add(new Rectangle(370,90,10,this.height));
        mursBibliotheque.add(new Rectangle(525,275,10,this.height));
        // mur bas droite
        mursBibliotheque.add(new Rectangle(540,430,40,50));
        mursBibliotheque.add(new Rectangle(690,440,90,50));
        // mur millieu droite
        mursBibliotheque.add(new Rectangle(660,270,150,50));
        // mur millieu
        mursBibliotheque.add(new Rectangle(250,265,340,50));
        // mur millieu gauche
        mursBibliotheque.add(new Rectangle(75,265,105,50));
        mursBibliotheque.add(new Rectangle(75,285,10,50));
        // mur haut gauche
        mursBibliotheque.add(new Rectangle(0,0,190,135));
        // mur haut centre
        mursBibliotheque.add(new Rectangle(245,90,200,50));
        // mur haut droite
        mursBibliotheque.add(new Rectangle(580,0,200,130));
        // tables
        mursBibliotheque.add(new Rectangle(200, 355, 35, 30));
        mursBibliotheque.add(new Rectangle(600, 515, 45, 20));        mursParMap.add(mursBibliotheque);
        // Objets map 1 (bibliothèque)
        List<Objet> objetsBibliotheque = new ArrayList<>();
        
        objetsBibliotheque.add(new Objet(
            new Rectangle(600, 515, 45,20),
            () -> printDev("Collision avec un objet de la bibliothèque !")
        ));
        objetsParMap.add(objetsBibliotheque);



        // On initialise la liste de murs courante
        murs = new ArrayList<>(mursParMap.get(displayedMap));
        // On initialise la liste d'objets courante
        objets = new ArrayList<>(objetsParMap.get(displayedMap));

        layers = new ArrayList<>();

        // Initialisation des couches
        Layer backgroundLayer = new Layer();
        Layer playerLayer = new Layer();
        Layer topLayer = new Layer();
        BufferedImage map2Image = null;
        try {
            map2Image = ImageIO.read(new File(secondLayerPath[displayedMap]));
        } catch (IOException e) {
            e.printStackTrace();
            printDev("Erreur lors du chargement de l'image de la deuxième couche : " + e.getMessage());
        }
        // Utilise un tableau pour stocker les images de topLayer pour chaque map
        final BufferedImage[] finalMap2Images = new BufferedImage[secondLayerPath.length];
        finalMap2Images[displayedMap] = map2Image;
        topLayer.addElement(new Drawable() {
            @Override
            public void draw(Graphics g) {
                BufferedImage img = finalMap2Images[displayedMap];
                if (img != null) {
                    g.drawImage(img, 0, 0, width, height, null);
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
                        joueur.state = 0; // Mettre à jour l'état du joueur pour la direction gauche
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        nextX += joueur.speed;
                        joueur.state = 1; // Mettre à jour l'état du joueur pour la direction droite
                        break;
                    case java.awt.event.KeyEvent.VK_UP:
                        nextY -= joueur.speed;
                        joueur.state = 2; // Mettre à jour l'état du joueur pour la direction haut
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        nextY += joueur.speed;
                        joueur.state = 3; // Mettre à jour l'état du joueur pour la direction bas
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
                // Test de collision avec les objets interactifs
                Rectangle joueurBox = new Rectangle(joueur.x, joueur.y, taille, taille);
                for (Objet obj : objets) {
                    if (joueurBox.intersects(obj.hitbox)) {
                        obj.trigger();
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
        // Dessiner les objets interactifs en debug
        if (devMode) {
            g.setColor(new Color(0,255,0,80));
            for (Objet obj : objets) {
                g.fillRect(obj.hitbox.x, obj.hitbox.y, obj.hitbox.width, obj.hitbox.height);
            }
        }
        // SUPPRIME : dessin du joueur hors layers
    }




    // affichage en mode développeur
    private void printDev(String message) {
        if (devMode) {
            System.out.println(message);
        }
    }



    // getteurs 
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }    // setteurs
    // displayedMap
    void setDisplayedMap(int displayedMap) {
        this.displayedMap = displayedMap;
        // Charger la nouvelle image de la map
        try {
            mapImage = ImageIO.read(new File(mapPath[displayedMap]));
            // Mettre à jour aussi l'image de la couche supérieure
            BufferedImage map2Image = ImageIO.read(new File(secondLayerPath[displayedMap]));
            // Mettre à jour finalMap2Images dans topLayer
            final BufferedImage[] finalMap2Images = new BufferedImage[secondLayerPath.length];
            finalMap2Images[displayedMap] = map2Image;
            // Recréer topLayer
            layers.get(2).clear(); // On vide la couche supérieure
            layers.get(2).addElement(new Drawable() {
                @Override
                public void draw(Graphics g) {
                    BufferedImage img = finalMap2Images[displayedMap];
                    if (img != null) {
                        g.drawImage(img, 0, 0, width, height, null);
                    }
                }
            });
            joueur.setPosition(50, 540);
        } catch (IOException e) {
            e.printStackTrace();
            printDev("Erreur lors du chargement de l'image de la map : " + e.getMessage());
        }
    }
}
