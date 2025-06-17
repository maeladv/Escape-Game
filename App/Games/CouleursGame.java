package App.Games;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.util.Arrays;

import App.Utils.GameUtils;
import App.Dialogue.DialogueManager;

public class CouleursGame extends Game implements MouseListener {
    private static final int GRID_SIZE = 3;
    private static final Color[] COLORS = {
        new Color(255, 0, 0),    // Rouge
        new Color(0, 255, 0),    // Vert
        new Color(0, 0, 255),    // Bleu
        new Color(255, 255, 0)   // Jaune
    };
    private int[][] grid;
    private int[][] solution;
    private JButton verifyButton;
    private String message = "";
    private Font gameFont;
    private JPanel panel;
    private Runnable onCloseCallback;

    public CouleursGame(boolean devMode, DialogueManager dialogueManager) {
        super(devMode, "Couleurs", "Trouvez le bon pattern de couleurs !", "assets/games/", dialogueManager);
        this.grid = new int[GRID_SIZE][GRID_SIZE];
        for (int[] row : grid) Arrays.fill(row, 0);
        this.solution = new int[][] {
            {0, 1, 2},
            {1, 2, 3},
            {2, 3, 0}
        };
        this.panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setFont(gameFont);
                int size = 80;
                int margin = 20;
                for (int i = 0; i < GRID_SIZE; i++) {
                    for (int j = 0; j < GRID_SIZE; j++) {
                        g.setColor(COLORS[grid[i][j]]);
                        g.fillRect(margin + j * (size + margin), margin + i * (size + margin), size, size);
                        g.setColor(Color.BLACK);
                        g.drawRect(margin + j * (size + margin), margin + i * (size + margin), size, size);
                    }
                }
                g.setColor(Color.BLACK);
                g.drawString(message, 20, 320);
            }
        };
        this.panel.setLayout(null);
        this.panel.setBounds(0, 0, 400, 400);
        this.verifyButton = new JButton("Vérifier");
        this.verifyButton.setBounds(100, 350, 120, 40);
        this.verifyButton.addActionListener(e -> verifyPattern());
        this.panel.add(verifyButton);
        this.panel.addMouseListener(this);
        this.gameFont = GameUtils.loadFont("assets/yoster.ttf", 24f);
    }

    @Override
    public JPanel getMainPanel() {
        return panel;
    }

    private void verifyPattern() {
        boolean correct = true;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] != solution[i][j]) {
                    correct = false;
                    break;
                }
            }
        }
        if (correct) {
            message = "Bravo ! Le défi est réussi.";
            panel.repaint();
            setFinished(true);
            if (dialogueManager != null) {
                dialogueManager.afficherScript(new String[]{"Bravo ! Le défi est réussi."}, "Suivant", () -> {
                    if (onCloseCallback != null) onCloseCallback.run();
                });
            } else if (onCloseCallback != null) {
                onCloseCallback.run();
            }
        } else {
            message = "Patern faux, l'orage se rapproche";
            panel.repaint();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int size = 80;
        int margin = 20;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int x = margin + j * (size + margin);
                int y = margin + i * (size + margin);
                if (e.getX() >= x && e.getX() <= x + size && e.getY() >= y && e.getY() <= y + size) {
                    grid[i][j] = (grid[i][j] + 1) % COLORS.length;
                    panel.repaint();
                    return;
                }
            }
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    @Override
    public void setOnCloseCallback(Runnable callback) {
        this.onCloseCallback = callback;
    }
}
