package App.Map;
import javax.swing.*;

import App.Objets.Objet;
import App.Utils.Drawable;
import App.Utils.GameUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The Map class is responsible for rendering the game map and objects.
 * This class has been refactored to focus primarily on display functionality,
 * with game logic moved to the GameController class.
 */
public class Map extends JPanel {
    private int width;
    private int height;
    private int displayedMap = 0; // Indice de la map affichée
    private String[] mapPath = {"assets/maps/intro/map.png","assets/maps/library/map.png","assets/maps/end/map.png"};
    private String[] secondLayerPath = {"assets/maps/intro/layer.png","assets/maps/library/layer.png","assets/maps/end/layer.png"};
    private BufferedImage mapImage; // permet de stocker l'image de la map
    private ArrayList<Rectangle> murs = new ArrayList<>();
    private boolean devMode; // Option de développement, à désactiver en prod
    
    private List<Layer> layers;
    
    // Création et initialisation de la map
    public Map(boolean devMode) {
        this.devMode = devMode;
        this.width = 800;
        this.height = 600;
        
        // Chargement de l'image de la map
        mapImage = null;
        try {
            String mapPathStr = mapPath[displayedMap];
            if (mapPathStr != null && !mapPathStr.isEmpty()) {
                File mapFile = new File(mapPathStr);
                if (mapFile.exists()) {
                    mapImage = ImageIO.read(mapFile);
                } else {
                    GameUtils.printDev("Fichier de map introuvable : " + mapPathStr, devMode);
                }
            }
        } catch (IOException e) {
            GameUtils.printDev("Erreur lors du chargement de l'image de la map : " + e.getMessage(), devMode);
        }
        
        this.setPreferredSize(new Dimension(width, height));
        
        // Initialize layers for rendering
        initializeLayers();
        
        // Make panel focusable for key events
        setFocusable(true);
        requestFocusInWindow();
    }
    
    /**
     * Initialize the layers for rendering the map
     */
    private void initializeLayers() {
        layers = new ArrayList<>();

        // Initialisation des couches
        Layer backgroundLayer = new Layer();
        Layer playerLayer = new Layer();
        Layer topLayer = new Layer();
        
        // Load second layer image
        BufferedImage map2Image = null;
        String layerPath = secondLayerPath[displayedMap];
        if (layerPath != null && !layerPath.isEmpty()) {
            File layerFile = new File(layerPath);
            if (layerFile.exists()) {
                try {
                    map2Image = ImageIO.read(layerFile);
                } catch (IOException e) {
                    GameUtils.printDev("Erreur lors du chargement de l'image de la deuxième couche : " + e.getMessage(), devMode);
                }
            } else {
                GameUtils.printDev("Fichier de layer introuvable : " + layerPath, devMode);
            }
        } else {
            GameUtils.printDev("Aucun chemin d'image pour la deuxième couche (index: " + displayedMap + ")", devMode);
        }
        
        // Store layer images for each map
        final BufferedImage[] finalMap2Images = new BufferedImage[secondLayerPath.length];
        finalMap2Images[displayedMap] = map2Image;
        
        // Add the top layer (drawn above player)
        topLayer.addElement(new Drawable() {
            @Override
            public void draw(Graphics g) {
                BufferedImage img = finalMap2Images[displayedMap];
                if (img != null) {
                    g.drawImage(img, 0, 0, width, height, null);
                }
            }
        });

        // Add background layer
        backgroundLayer.addElement(new Drawable() {
            @Override
            public void draw(Graphics g) {
                if (mapImage != null) {
                    g.drawImage(mapImage, 0, 0, width, height, null);
                }
            }
        });

        // Add layers in correct order: background, player, top
        layers.add(backgroundLayer);
        layers.add(playerLayer);
        layers.add(topLayer);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Dessiner tous les layers de la map
        for (Layer layer : layers) {
            layer.draw(g);
        }
    }

    // Getters et setters
    public int getWidthValue() { return width; }
    public void setWidthValue(int width) { this.width = width; }
    public int getHeightValue() { return height; }
    public void setHeightValue(int height) { this.height = height; }
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
    public List<Layer> getLayers() { return layers; }
    public void setLayers(List<Layer> layers) { this.layers = layers; }
    
    /**
     * Change la map affichée
     * @param displayedMap L'indice de la map à afficher
     */
    public void setDisplayedMap(int displayedMap) {
        this.displayedMap = displayedMap;
        // Vérification de l'index pour éviter ArrayIndexOutOfBoundsException
        if (displayedMap < 0 || displayedMap >= mapPath.length) {
            GameUtils.printDev("Index de map invalide : " + displayedMap, devMode);
            return;
        }
        try {
            File mapFile = new File(mapPath[displayedMap]);
            if (mapFile.exists()) {
                mapImage = ImageIO.read(mapFile);
            } else {
                mapImage = null;
                GameUtils.printDev("Fichier de map introuvable : " + mapPath[displayedMap], devMode);
            }
            // Mettre à jour l'image de la couche supérieure
            if (layers != null && layers.size() >= 3) {
                final Layer topLayer = layers.get(2);
                final BufferedImage[] finalMap2Images = new BufferedImage[secondLayerPath.length];
                if (displayedMap < secondLayerPath.length) {
                    File layerFile = new File(secondLayerPath[displayedMap]);
                    BufferedImage map2Image = null;
                    if (layerFile.exists()) {
                        map2Image = ImageIO.read(layerFile);
                    } else {
                        GameUtils.printDev("Fichier de layer introuvable : " + secondLayerPath[displayedMap], devMode);
                    }
                    finalMap2Images[displayedMap] = map2Image;
                    // Remplacer l'élément dans la couche supérieure
                    topLayer.clear();
                    topLayer.addElement(new Drawable() {
                        @Override
                        public void draw(Graphics g) {
                            BufferedImage img = finalMap2Images[displayedMap];
                            if (img != null) {
                                g.drawImage(img, 0, 0, width, height, null);
                            }
                        }
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            GameUtils.printDev("Erreur lors du chargement de l'image de la map : " + e.getMessage(), devMode);
        }
        repaint();
    }
    
    /**
     * Ajoute un élément à dessiner sur la couche du joueur
     * @param drawable L'élément à dessiner
     */
    public void addPlayerLayerElement(Drawable drawable) {
        if (layers != null && layers.size() >= 2) {
            layers.get(1).addElement(drawable);
        }
    }
    
    /**
     * Ajoute un élément à dessiner sur la couche de debug (par-dessus tout)
     * @param drawable L'élément à dessiner
     */
    public void addDebugLayerElement(Drawable drawable) {
        if (layers != null && layers.size() >= 3) {
            layers.get(2).addElement(drawable);
        }
    }
    
    /**
     * Supprime tous les éléments de debug ajoutés à la couche supérieure
     * tout en préservant l'image de fond de la couche
     */
    public void clearDebugLayer() {
        if (layers != null && layers.size() >= 3) {
            // Sauvegarder l'image de fond de la couche
            final Layer topLayer = layers.get(2);
            final BufferedImage[] finalMap2Images = new BufferedImage[secondLayerPath.length];
            try {
                BufferedImage map2Image = ImageIO.read(new File(secondLayerPath[displayedMap]));
                finalMap2Images[displayedMap] = map2Image;
                
                // Effacer la couche et remettre seulement l'image de fond
                topLayer.clear();
                topLayer.addElement(new Drawable() {
                    @Override
                    public void draw(Graphics g) {
                        BufferedImage img = finalMap2Images[displayedMap];
                        if (img != null) {
                            g.drawImage(img, 0, 0, width, height, null);
                        }
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
                GameUtils.printDev("Erreur lors du chargement de l'image de la deuxième couche : " + e.getMessage(), devMode);
            }
        }
    }
}
