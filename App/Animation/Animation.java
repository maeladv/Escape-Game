package App.Animation;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import App.Map.Map;
import App.Utils.Drawable;

/**
 * Animation class handles various visual effects in the game.
 * It supports map transitions (fade in/out) and teleportation effects.
 */
public class Animation {
    // Types d'animation
    public enum AnimationType {
        FADE_OUT,        // Fondu au noir (pour sortir d'une map)
        FADE_IN,         // Fondu inverse (pour entrer dans une map)
        TELEPORT,        // Effet de téléportation
        NONE             // Pas d'animation
    }
    
    private Map map;                     // Référence à la carte pour dessiner
    private AnimationType currentType;   // Type d'animation en cours
    private boolean isRunning;           // Indique si l'animation est en cours
    private float alpha;                 // Niveau d'opacité pour les fondus
    private long startTime;              // Temps de début de l'animation
    private long duration;               // Durée de l'animation en millisecondes
    private Consumer<Void> onComplete;   // Action à exécuter à la fin de l'animation
    private Drawable animationDrawable;  // Élément dessinable pour l'animation
    
    // Variables pour l'effet de téléportation
    private int playerX, playerY;        // Position du joueur pour l'effet de téléportation
    private int playerSize;              // Taille du joueur
    private List<Particle> particles;    // Particules pour l'effet de téléportation
    private Random random;               // Générateur de nombres aléatoires
    
    /**
     * Constructeur pour l'animation
     * @param map La carte sur laquelle dessiner l'animation
     */
    public Animation(Map map) {
        this.map = map;
        this.currentType = AnimationType.NONE;
        this.isRunning = false;
        this.alpha = 0f;
        this.particles = new ArrayList<>();
        this.random = new Random();
        
        // Initialiser l'élément dessinable pour l'animation
        this.animationDrawable = new Drawable() {
            @Override
            public void draw(Graphics g) {
                if (isRunning) {
                    switch (currentType) {
                        case FADE_IN:
                        case FADE_OUT:
                            drawFadeEffect(g);
                            break;
                        case TELEPORT:
                            drawTeleportEffect(g);
                            break;
                        default:
                            break;
                    }
                }
            }
        };
        
        // Ajouter l'élément dessinable à la carte
        map.addDebugLayerElement(this.animationDrawable);
    }
    
    /**
     * Dessiner l'effet de fondu
     * @param g Le contexte graphique
     */
    private void drawFadeEffect(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        long elapsedTime = System.currentTimeMillis() - startTime;
        float progress = Math.min(1.0f, (float) elapsedTime / duration);
        
        // Calculer l'alpha en fonction du type de fondu
        if (currentType == AnimationType.FADE_OUT) {
            alpha = progress;
        } else {
            alpha = 1.0f - progress;
        }
        
        // Définir la transparence
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, map.getWidthValue(), map.getHeightValue());
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        // Vérifier si l'animation est terminée
        if (progress >= 1.0f) {
            isRunning = false;
            if (onComplete != null) {
                onComplete.accept(null);
            }
        }
        
        // Demander un nouveau rendu
        map.repaint();
    }
    
    /**
     * Dessiner l'effet de téléportation
     * @param g Le contexte graphique
     */
    private void drawTeleportEffect(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        long elapsedTime = System.currentTimeMillis() - startTime;
        float progress = Math.min(1.0f, (float) elapsedTime / duration);
        
        // Dessiner un halo autour du joueur
        int centerX = playerX + playerSize / 2;
        int centerY = playerY + playerSize / 2;
        int haloSize = (int)(playerSize * 2 * (1 + progress * 0.5f));
        
        // Couleur avec transparence
        float alpha = 0.7f * (1.0f - progress);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        g2d.setColor(new Color(100, 100, 255, 200));
        g2d.fillOval(centerX - haloSize/2, centerY - haloSize/2, haloSize, haloSize);
        
        // Dessiner les particules
        for (Particle particle : particles) {
            particle.update(progress);
            particle.draw(g);
        }
        
        // Rétablir la transparence normale
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
        
        // Vérifier si l'animation est terminée
        if (progress >= 1.0f) {
            isRunning = false;
            if (onComplete != null) {
                onComplete.accept(null);
            }
        }
        
        // Demander un nouveau rendu
        map.repaint();
    }
    
    /**
     * Démarrer une animation de fondu au noir (sortie)
     * @param duration Durée de l'animation en millisecondes
     * @param onComplete Action à exécuter à la fin de l'animation
     */
    public void startFadeOut(long duration, Consumer<Void> onComplete) {
        this.currentType = AnimationType.FADE_OUT;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
        this.onComplete = onComplete;
        this.alpha = 0f;
        map.repaint();
    }
    
    /**
     * Démarrer une animation de fondu inverse (entrée)
     * @param duration Durée de l'animation en millisecondes
     * @param onComplete Action à exécuter à la fin de l'animation
     */
    public void startFadeIn(long duration, Consumer<Void> onComplete) {
        this.currentType = AnimationType.FADE_IN;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
        this.onComplete = onComplete;
        this.alpha = 1f;
        map.repaint();
    }
    
    /**
     * Démarrer une animation de téléportation
     * @param playerX Position X du joueur
     * @param playerY Position Y du joueur
     * @param playerSize Taille du joueur
     * @param duration Durée de l'animation en millisecondes
     * @param onComplete Action à exécuter à la fin de l'animation
     */
    public void startTeleport(int playerX, int playerY, int playerSize, long duration, Consumer<Void> onComplete) {
        this.currentType = AnimationType.TELEPORT;
        this.playerX = playerX;
        this.playerY = playerY;
        this.playerSize = playerSize;
        this.duration = duration;
        this.startTime = System.currentTimeMillis();
        this.isRunning = true;
        this.onComplete = onComplete;
        
        // Générer les particules
        generateParticles();
        
        map.repaint();
    }
    
    /**
     * Générer les particules pour l'effet de téléportation
     */
    private void generateParticles() {
        particles.clear();
        int numParticles = 100;  // Augmentation du nombre de particules pour un effet plus visible
        
        // Centre du joueur
        int centerX = playerX + playerSize / 2;
        int centerY = playerY + playerSize / 2;
        
        // Créer les particules
        for (int i = 0; i < numParticles; i++) {
            // Couleur aléatoire (bleu/violet pour effet magique)
            Color color = new Color(
                random.nextInt(100),              // R: peu de rouge
                random.nextInt(100) + 155,        // G: plus de vert pour brillance
                random.nextInt(50) + 205,         // B: beaucoup de bleu
                255                               // Alpha: opaque
            );
            
            // Position initiale (proche du joueur)
            int x = centerX + random.nextInt(playerSize) - playerSize / 2;
            int y = centerY + random.nextInt(playerSize) - playerSize / 2;
            
            // Vitesse et direction aléatoires
            float speedX = (random.nextFloat() * 2 - 1) * 8;  // Entre -8 et 8
            float speedY = (random.nextFloat() * 2 - 1) * 8;  // Entre -8 et 8
            
            // Taille aléatoire
            int size = random.nextInt(6) + 4;  // Entre 4 et 9
            
            // Ajouter la particule
            particles.add(new Particle(x, y, speedX, speedY, size, color));
        }
    }
    
    /**
     * Vérifier si une animation est en cours
     * @return true si une animation est en cours, false sinon
     */
    public boolean isRunning() {
        return isRunning;
    }
    
    /**
     * Arrêter l'animation en cours
     */
    public void stop() {
        isRunning = false;
        map.repaint();
    }
    
    /**
     * Classe interne pour représenter une particule
     */
    private class Particle {
        private float x, y;           // Position
        private float initialX, initialY; // Position initiale
        private float speedX, speedY;  // Vitesse
        private int size;             // Taille
        private Color color;          // Couleur
        
        /**
         * Constructeur pour une particule
         * @param x Position X initiale
         * @param y Position Y initiale
         * @param speedX Vitesse horizontale
         * @param speedY Vitesse verticale
         * @param size Taille
         * @param color Couleur
         */
        public Particle(float x, float y, float speedX, float speedY, int size, Color color) {
            this.x = x;
            this.y = y;
            this.initialX = x;
            this.initialY = y;
            this.speedX = speedX;
            this.speedY = speedY;
            this.size = size;
            this.color = color;
        }
        
        /**
         * Mettre à jour la position de la particule
         * @param progress Progression de l'animation (0.0 à 1.0)
         */
        public void update(float progress) {
            // Au début, les particules se déplacent vers l'extérieur
            if (progress < 0.5f) {
                float factor = progress * 2; // 0.0 -> 1.0 pour la première moitié
                x = initialX + speedX * factor * 30; // Distance augmentée pour un effet plus visible
                y = initialY + speedY * factor * 30;
                
                // Faire grossir légèrement les particules
                size = (int)(size * (1 + factor * 0.3f));
            } 
            // Puis elles convergent vers le centre
            else {
                float factor = (progress - 0.5f) * 2; // 0.0 -> 1.0 pour la seconde moitié
                
                // Mouvement plus rapide et plus concentré vers la fin
                float accel = 1 + factor * 2;
                x = initialX + speedX * 30 * (1 - factor * accel);
                y = initialY + speedY * 30 * (1 - factor * accel);
                
                // Faire rétrécir les particules
                size = (int)(size * (1 - factor * 0.5f));
            }
            
            // Faire disparaître progressivement les particules à la fin
            if (progress > 0.8f) {
                float alpha = 1.0f - (progress - 0.8f) * 5; // 1.0 -> 0.0 dans les 20% finaux
                color = new Color(color.getRed(), color.getGreen(), color.getBlue(), 
                                  Math.max(0, Math.min(255, (int)(alpha * 255))));
            }
        }
        
        /**
         * Dessiner la particule
         * @param g Le contexte graphique
         */
        public void draw(Graphics g) {
            g.setColor(color);
            g.fillOval((int)x - size/2, (int)y - size/2, size, size);
        }
    }

    public void setIsrunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
}
