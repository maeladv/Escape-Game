package App.Games;

import App.Map.Map;
import App.Dialogue.DialogueManager;
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
    protected DialogueManager dialogueManager;


    public Game(boolean devMode, String name, String description, String assetsFolder) {
        this.name = name;
        this.description = description;
        this.assetsFolder = assetsFolder;
        this.devMode = devMode;
    }

    public Game(boolean devMode, String name, String description, String assetsFolder, Runnable onCloseCallback) {
        this(devMode, name, description, assetsFolder); // ON récupère le constructeur primaire
        this.onCloseCallback = onCloseCallback;
    }

    public Game(boolean devMode, String name, String description, String assetsFolder, DialogueManager dialogueManager) {
        this(devMode, name, description, assetsFolder);
        this.dialogueManager = dialogueManager;
    }

    // Getters
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getAssetsFolder() { return assetsFolder; }
    public boolean isFinished() { return finished; }
    public DialogueManager getDialogueManager() { return dialogueManager; }

    // Affiche le mini-jeu dans la même fenêtre (superposé à la map)
    public void afficherMiniJeu(Map map) {
        if (miniJeuPanel != null) return;
        miniJeuPanel = getMainPanel();
        miniJeuPanel.setLayout(null);
        miniJeuPanel.setBounds(20, 20, map.getWidth() - 40, map.getHeight() - 40);
        // Ajout du bouton Redémarrer automatiquement pour tous les jeux
        JButton restartBtn = new JButton("Redémarrer");
        restartBtn.setFont(new Font("Arial", Font.BOLD, 16));
        restartBtn.setBounds(miniJeuPanel.getWidth() - 160, 20, 140, 40); // Position en haut à droite
        restartBtn.addActionListener(e -> restart(map));
        miniJeuPanel.add(restartBtn);
        miniJeuPanel.setComponentZOrder(restartBtn, 0);
        map.setLayout(null); // Pour position absolue
        map.add(miniJeuPanel);
        map.setComponentZOrder(miniJeuPanel, 0); // Mettre au-dessus
        map.repaint();
        map.revalidate();
    }

    /**
     * Redémarre le mini-jeu à zéro (ferme et réaffiche, réinitialise l'état)
     * @param map la map sur laquelle afficher le mini-jeu
     */
    public void restart(Map map) {
        retirerMiniJeu(map);
        setFinished(false);
        afficherMiniJeu(map);
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

    public void setFinished(boolean finished) {
        this.finished = finished;
        if (finished && miniJeuPanel != null && miniJeuPanel.getParent() instanceof Map) {
            retirerMiniJeu((Map) miniJeuPanel.getParent());
        }
    }
    public void setOnCloseCallback(Runnable callback) { this.onCloseCallback = callback; }
    public void setDialogueManager(DialogueManager dialogueManager) { this.dialogueManager = dialogueManager; }
}
