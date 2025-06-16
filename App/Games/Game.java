package App.Games;

import App.Map.Map;
import javax.swing.*;
import java.awt.*;

public abstract class Game {
    private JPanel miniJeuPanel = null;
    private String name;
    private String description;
    private String assetsFolder;
    private boolean devMode;
    private boolean finished = false;
    private Runnable onCloseCallback;

    public Game(boolean devMode, String name, String description, String assetsFolder) {
        this.name = name;
        this.description = description;
        this.assetsFolder = assetsFolder;
        this.devMode = devMode;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAssetsFolder() { return assetsFolder; }
    public boolean isFinished() { return finished; }

    /**
     * Affiche le mini-jeu dans la même fenêtre (superposé à la map), avec une croix pour fermer.
     * @param map la map sur laquelle ajouter le mini-jeu
     */
    public void afficherMiniJeu(Map map) {
        if (miniJeuPanel != null) return;
        miniJeuPanel = getMainPanel();
        miniJeuPanel.setLayout(null);
        miniJeuPanel.setBounds(0, 0, map.getWidth(), map.getHeight());
        JButton closeBtn = new JButton("✖");
        closeBtn.setFocusable(false);
        closeBtn.setMargin(new Insets(0, 8, 0, 8));
        closeBtn.setBounds(8, 8, 40, 32);
        closeBtn.addActionListener(e -> retirerMiniJeu(map));
        miniJeuPanel.add(closeBtn);
        map.setLayout(null); // Pour position absolue
        map.add(miniJeuPanel);
        map.setComponentZOrder(miniJeuPanel, 0); // Mettre au-dessus
        map.repaint();
        map.revalidate();
    }

    /**
     * Retire le mini-jeu de l'affichage (à appeler quand le mini-jeu est terminé)
     * @param map la map sur laquelle retirer le mini-jeu
     */
    public void retirerMiniJeu(Map map) {
        if (miniJeuPanel != null) {
            map.remove(miniJeuPanel);
            miniJeuPanel = null;
            map.repaint();
            map.revalidate();
            onClose();
        }
    }

    /**
     * À override : retourne le panel principal du mini-jeu.
     */
    public abstract JPanel getMainPanel();

    /**
     * À override : actions à effectuer à la fermeture du mini-jeu.
     */
    public void onClose() {
        if (onCloseCallback != null) onCloseCallback.run();
    }

    public void setFinished(boolean finished) { this.finished = finished; }
    public void setOnCloseCallback(Runnable callback) { this.onCloseCallback = callback; }
}
