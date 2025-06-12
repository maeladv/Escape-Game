import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.List;

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
    boolean devMode = false; // Option de développement, à désactiver en prod

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
        joueur = new Joueur("Joueur", 30, 460, 10);

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
        // Nouvel objet ajouté à la map 0
        objetsIntro.add(new Objet(
            new Rectangle(540, 310, 70, 110),
            () -> {
                setDisplayedMap(1);
                // Met à jour les murs et objets pour la nouvelle map
                murs = new ArrayList<>(mursParMap.get(displayedMap));
                objets = new ArrayList<>(objetsParMap.get(displayedMap));
                printDev("Interaction avec la porte de la map 0 ! Changement de map.");
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
        mursBibliotheque.add(new Rectangle(600, 515, 45, 20));
        mursParMap.add(mursBibliotheque);


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
                    case java.awt.event.KeyEvent.VK_SPACE:
                        afficherDialogue("Vous avez cliqué sur la carte !");
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
                    }                }                // Test de proximité avec les objets interactifs
                // Zone de proximité réduite - le joueur doit être collé à l'objet
                // On ajoute seulement 10 pixels de marge au lieu de 20
                Rectangle zoneInteraction = new Rectangle(joueur.x - 5, joueur.y - 5, taille + 10, taille + 10);
                
                for (Objet obj : objets) {
                    // Vérifier si le joueur est proche de l'objet
                    if (zoneInteraction.intersects(obj.hitbox)) {// Vérifier si le joueur regarde dans la direction de l'objet
                        boolean regardeBonneDirection = false;
                        
                        // Calculer la position relative de l'objet par rapport au joueur
                        int centreObjetX = obj.hitbox.x + obj.hitbox.width / 2;
                        int centreObjetY = obj.hitbox.y + obj.hitbox.height / 2;
                        int centreJoueurX = joueur.x + taille / 2;
                        int centreJoueurY = joueur.y + taille / 2;
                        
                        // Déterminer si le joueur regarde dans la direction de l'objet
                        // État 0: gauche, 1: droite, 2: haut, 3: bas
                        switch (joueur.state) {
                            case 0: // Gauche
                                regardeBonneDirection = centreObjetX < centreJoueurX;
                                break;
                            case 1: // Droite
                                regardeBonneDirection = centreObjetX > centreJoueurX;
                                break;
                            case 2: // Haut
                                regardeBonneDirection = centreObjetY < centreJoueurY;
                                break;
                            case 3: // Bas
                                regardeBonneDirection = centreObjetY > centreJoueurY;
                                break;
                        }
                          // Marquer l'objet comme actif s'il est dans la bonne direction
                        obj.setActive(regardeBonneDirection);
                        
                        // Afficher un message d'aide en mode développement
                        if (devMode && regardeBonneDirection) {
                            printDev("Objet interactif à proximité immédiate. Appuyez sur 'A' pour interagir.");
                        }
                        
                        // Si le joueur appuie sur 'a' et regarde dans la bonne direction, déclencher l'action
                        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A && regardeBonneDirection) {
                            obj.trigger();
                        }
                    } else {
                        // Si le joueur n'est pas à proximité, l'objet n'est pas actif
                        obj.setActive(false);
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

    // Méthode pour afficher un dialogue personnalisé au-dessus de la carte
    private void afficherDialogue(String message) {
        final DialoguePersonnalise[] dialogueWrapper = new DialoguePersonnalise[1];
        dialogueWrapper[0] = new DialoguePersonnalise(message, () -> {
            remove(dialogueWrapper[0]); // Supprimer le dialogue après fermeture
            revalidate(); // Revalider le layout après suppression
            repaint(); // Rafraîchir l'affichage
        });
        DialoguePersonnalise dialogue = dialogueWrapper[0];
        add(dialogue); // Ajouter le dialogue avant de l'utiliser
        dialogue.setBounds((getWidth() - (getWidth() * 3 / 4)) / 2, getHeight() - getHeight() / 4 - 20, getWidth() * 3 / 4, getHeight() / 4);
        setLayout(null); // Permet de positionner le dialogue avec des coordonnées absolues
        revalidate(); // Revalider le layout après ajout
        repaint(); // Rafraîchir l'affichage
    }

    // Mettre à jour la position du joueur
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        //  Dessiner tous les layers de la map
        for (Layer layer : layers) {
            layer.draw(g);
        }
        // Si le mode développeur est activé, on affiche plus d'informations
        if (devMode ) {
            // Dessiner les murs pour debug (en couleur vive)
            g.setColor(new Color(255,255,255,50));
            for (Rectangle mur : murs) {
                g.fillRect(mur.x, mur.y, mur.width, mur.height);
            }
            // Dessiner les objets interactifs en debug
            for (Objet obj : objets) {
                // Objet actif (joueur regarde dans sa direction) : vert plus vif
                if (obj.isActive()) {
                    g.setColor(new Color(0, 255, 0, 160));
                } else {
                    // Objet inactif : vert plus transparent
                    g.setColor(new Color(0, 255, 0, 80));
                }
                g.fillRect(obj.hitbox.x, obj.hitbox.y, obj.hitbox.width, obj.hitbox.height);
            }
        }
    }




    // affichage en mode développeur uniquement
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
