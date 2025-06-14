package App.Map;
import javax.swing.*;

import App.Dialogue.DialogueManager;
import App.Joueur.Joueur;
import App.Objets.Objet;
import App.Utils.Drawable;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Ajout de l'import de la classe Objet
// (pas besoin d'import si Objet.java est dans le même dossier et sans package)

public class Map extends JPanel {
    private int width;
    private int height;
    private Joueur joueur;
    private int displayedMap = 0; // Indice de la map affichée, utile si on veut changer de map
    private String[] mapPath = {"assets/maps/intro/map.png","assets/maps/library/map.png"};
    private String[] secondLayerPath = {"assets/maps/intro/layer.png","assets/maps/library/layer.png"};
    private BufferedImage mapImage; // permet de stocker l'image de la map
    private ArrayList<Rectangle> murs = new ArrayList<>();
    private boolean devMode = true; // Option de développement, à désactiver en prod

    // Liste de listes de murs, un ensemble de murs par map
    private List<List<Rectangle>> mursParMap = new ArrayList<>();

    private List<Layer> layers;

    // Système d'objets interactifs
    private List<List<Objet>> objetsParMap = new ArrayList<>(); // Liste de listes d'objets, un ensemble d'objets par map

    // Système de dialogues
    private DialogueManager dialogueManager;

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
                printDev("Interaction avec la porte de la map 0 ! Changement de map.");
                joueur.setPosition(50, 530);
                joueur.setState(1);
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
        // Table avec déclenchement d'une boîte de dialogue au clic sur A
        objetsBibliotheque.add(new Objet(
            new Rectangle(600, 510, 50, 20),
            () -> dialogueManager.afficherDialogue("Ceci est une table de la bibliothèque. Appuyez sur OK pour continuer.", "OK", () -> joueur.setCanMove(true))
        ));
        // Première Bibliothèque
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



        // On initialise la liste de murs courante
        murs = new ArrayList<>(mursParMap.get(displayedMap));
        // On initialise la liste d'objets courante

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
                int nextX = joueur.getX();
                int nextY = joueur.getY();
                int taille = 40;
                switch (e.getKeyCode()) {
                    case java.awt.event.KeyEvent.VK_LEFT:
                        nextX -= joueur.getSpeed();
                        joueur.setState(0); // Mettre à jour l'état du joueur pour la direction gauche
                        break;
                    case java.awt.event.KeyEvent.VK_RIGHT:
                        nextX += joueur.getSpeed();
                        joueur.setState(1); // Mettre à jour l'état du joueur pour la direction droite
                        break;
                    case java.awt.event.KeyEvent.VK_UP:
                        nextY -= joueur.getSpeed();
                        joueur.setState(2); // Mettre à jour l'état du joueur pour la direction haut
                        break;
                    case java.awt.event.KeyEvent.VK_DOWN:
                        nextY += joueur.getSpeed();
                        joueur.setState(3); // Mettre à jour l'état du joueur pour la direction bas
                        break;
                    case java.awt.event.KeyEvent.VK_SPACE:
                        dialogueManager.afficherDialogue("Vous avez cliqué sur la carte !", "OK", () -> joueur.setCanMove(true));
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
                Rectangle zoneInteraction = new Rectangle(joueur.getX() - 5, joueur.getY() - 5, taille + 10, taille + 10);
                for (Objet obj : objetsParMap.get(displayedMap)) {
                    if (zoneInteraction.intersects(obj.getHitbox())) {
                        boolean regardeBonneDirection = false;
                        int centreObjetX = obj.getHitbox().x + obj.getHitbox().width / 2;
                        int centreObjetY = obj.getHitbox().y + obj.getHitbox().height / 2;
                        int centreJoueurX = joueur.getX() + taille / 2;
                        int centreJoueurY = joueur.getY() + taille / 2;
                        switch (joueur.getState()) {
                            case 0:
                                regardeBonneDirection = centreObjetX < centreJoueurX;
                                break;
                            case 1:
                                regardeBonneDirection = centreObjetX > centreJoueurX;
                                break;
                            case 2:
                                regardeBonneDirection = centreObjetY < centreJoueurY;
                                break;
                            case 3:
                                regardeBonneDirection = centreObjetY > centreJoueurY;
                                break;
                        }
                        obj.setActive(regardeBonneDirection);
                        if (devMode && regardeBonneDirection) {
                            printDev("Objet interactif à proximité immédiate. Appuyez sur 'A' pour interagir.");
                        }
                        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A && regardeBonneDirection) {
                            obj.trigger();
                        }
                    } else {
                        obj.setActive(false);
                    }
                }
                Rectangle joueurBox = new Rectangle(joueur.getX(), joueur.getY(), taille, taille);
                for (Objet obj : objetsParMap.get(displayedMap)) {
                    if (joueurBox.intersects(obj.getHitbox())) {
                        if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A) {
                            obj.trigger();
                        }
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
        
        // Initialisation du DialogueManager
        dialogueManager = new DialogueManager(this);

        // Script d'introduction (exemple)
        if (displayedMap == 0) {
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
            // Appeler le script après l'initialisation complète de la Map
            SwingUtilities.invokeLater(() -> dialogueManager.afficherScript(introScript, "Suivant", () -> joueur.setCanMove(true)));
        }
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
            for (Objet obj : objetsParMap.get(displayedMap)) {
                // Objet actif (joueur regarde dans sa direction) : vert plus vif
                if (obj.isActive()) {
                    g.setColor(new Color(0, 255, 0, 160));
                } else {
                    // Objet inactif : vert plus transparent
                    g.setColor(new Color(0, 255, 0, 80));
                }
                g.fillRect(obj.getHitbox().x, obj.getHitbox().y, obj.getHitbox().width, obj.getHitbox().height);
            }
        }
    }




    // affichage en mode développeur uniquement
    private void printDev(String message) {
        if (devMode) {
            System.out.println(message);
        }
    }

    



    // Getters et setters
    public int getWidthValue() { return width; }
    public void setWidthValue(int width) { this.width = width; }
    public int getHeightValue() { return height; }
    public void setHeightValue(int height) { this.height = height; }
    public Joueur getJoueur() { return joueur; }
    public void setJoueur(Joueur joueur) { this.joueur = joueur; }
    public int getDisplayedMap() { return displayedMap; }
    public String[] getMapPath() { return mapPath; }
    public void setMapPath(String[] mapPath) { this.mapPath = mapPath; }
    public String[] getSecondLayerPath() { return secondLayerPath; }
    public void setSecondLayerPath(String[] secondLayerPath) { this.secondLayerPath = secondLayerPath; }
    public BufferedImage getMapImage() { return mapImage; }
    public void setMapImage(BufferedImage mapImage) { this.mapImage = mapImage; }
    public ArrayList<Rectangle> getMurs() { return murs; }
    public void setMurs(ArrayList<Rectangle> murs) { this.murs = murs; }
    public boolean isDevMode() { return devMode; }
    public void setDevMode(boolean devMode) { this.devMode = devMode; }
    public List<List<Rectangle>> getMursParMap() { return mursParMap; }
    public void setMursParMap(List<List<Rectangle>> mursParMap) { this.mursParMap = mursParMap; }
    public List<Layer> getLayers() { return layers; }
    public void setLayers(List<Layer> layers) { this.layers = layers; }
    public List<List<Objet>> getObjetsParMap() { return objetsParMap; }
    public void setObjetsParMap(List<List<Objet>> objetsParMap) { this.objetsParMap = objetsParMap; }
    public DialogueManager getDialogueManager() { return dialogueManager; }
    public void setDialogueManager(DialogueManager dialogueManager) { this.dialogueManager = dialogueManager; }

    // setteurs
    // displayedMap
    void setDisplayedMap(int displayedMap) {
        this.displayedMap = displayedMap;
        // Charger la nouvelle image de la map
        try {
            mapImage = ImageIO.read(new File(mapPath[displayedMap]));
        } catch (IOException e) {
            e.printStackTrace();
            printDev("Erreur lors du chargement de l'image de la map : " + e.getMessage());
        }
        murs = new ArrayList<>(mursParMap.get(displayedMap));
        repaint();
    }
}
