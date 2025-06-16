package App.Games;

import App.Utils.GameUtils;
import App.Dialogue.DialogueManager;
import App.Map.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MorpionGame extends Game {
    private JButton[][] buttons = new JButton[3][3];
    private char currentPlayer = 'X';
    private JLabel statusLabel;

    // Constructeur principal :
    public MorpionGame(boolean devMode, DialogueManager dialogueManager) {
        super(devMode, "Morpion", "Mini-jeu de morpion", "assets/mini-jeux/morpion", dialogueManager);
    }

    public MorpionGame(boolean devMode) {
        super(devMode, "Morpion", "Mini-jeu de morpion", "assets/mini-jeux/morpion",
                () -> {
                    GameUtils.printDev("Morpion fermé, état réinitialisé", devMode);
                    // Afficher message dans un dialogue
                    String[] messages = {
                            "Bravo ! Vous avez terminé le mini-jeu de morpion.",
                            "Voici donc un premier indice :",
                    };
                    // dialogueManager.afficherDialogue("Indice : Le code du coffre est 1234.", "OK");

                    

                }

        );
    }

    public MorpionGame(DialogueManager dialogueManager) {
        super(false, "Morpion", "Mini-jeu de morpion", "assets/mini-jeux/morpion", dialogueManager);
    }

    // Simplifie la logique de jeu du bot et évite les répétitions
    private void botPlay() {
        if (tryBotMove(getEmptyPositions(new int[][]{ {0,0}, {0,2}, {2,0}, {2,2} }))) return; // coins aléatoires
        if (tryBotMove(getEmptyPositions(new int[][]{ {1,1} }))) return; // centre
        tryBotMove(getEmptyPositions(new int[][]{ {0,1}, {1,0}, {1,2}, {2,1} })); // bords
    }

    // Retourne la liste des positions vides parmi une liste de positions
    private java.util.List<int[]> getEmptyPositions(int[][] positions) {
        java.util.List<int[]> empty = new java.util.ArrayList<>();
        for (int[] pos : positions) {
            if (buttons[pos[0]][pos[1]].getText().isEmpty()) {
                empty.add(pos);
            }
        }
        java.util.Collections.shuffle(empty); // Pour les coins, l'ordre sera aléatoire
        return empty;
    }

    // Joue le premier coup possible dans la liste, retourne true si joué
    private boolean tryBotMove(java.util.List<int[]> positions) {
        if (!positions.isEmpty()) {
            int[] pos = positions.get(0);
            buttons[pos[0]][pos[1]].setText("O");
            endTurnOrContinue();
            return true;
        }
        return false;
    }

    // Gère la fin de tour du bot ou passe au joueur
    private void endTurnOrContinue() {
        if (checkWin()) {
            statusLabel.setText("Le bot a gagné !");
            setFinished(false);
        } else if (isFull()) {
            statusLabel.setText("Match nul !");
            setFinished(false);
        } else {
            currentPlayer = 'X';
            statusLabel.setText("Tour du joueur : X");
        }
    }

    @Override
    public JPanel getMainPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(240, 240, 255));
        int size = 80;
        int margin = 20;
        int offsetX = 100;
        int offsetY = 60;
        statusLabel = new JLabel("Tour du joueur : X");
        statusLabel.setBounds(offsetX, offsetY - 40, 300, 30);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(statusLabel);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                JButton btn = new JButton("");
                btn.setFont(new Font("Arial", Font.BOLD, 36));
                btn.setBounds(offsetX + j * (size + margin), offsetY + i * (size + margin), size, size);
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!isFinished() && btn.getText().equals("") && currentPlayer == 'X') {
                            btn.setText("X");
                            if (checkWin()) {
                                statusLabel.setText("Le joueur X a gagné !");
                                setFinished(true);
                            } else if (isFull()) {
                                statusLabel.setText("Match nul !");
                                setFinished(false);
                            } else {
                                currentPlayer = 'O';
                                statusLabel.setText("Tour du bot...");
                                // Laisser le temps à l'UI de se rafraîchir
                                javax.swing.Timer timer = new javax.swing.Timer(500, evt -> {
                                    botPlay();
                                });
                                timer.setRepeats(false);
                                timer.start();
                            }
                        }
                    }
                });
                buttons[i][j] = btn;
                panel.add(btn);
            }
        }
        return panel;
    }

    private boolean checkWin() {
        for (int i = 0; i < 3; i++) {
            if (!buttons[i][0].getText().isEmpty() &&
                    buttons[i][0].getText().equals(buttons[i][1].getText()) &&
                    buttons[i][1].getText().equals(buttons[i][2].getText()))
                return true;
            if (!buttons[0][i].getText().isEmpty() &&
                    buttons[0][i].getText().equals(buttons[1][i].getText()) &&
                    buttons[1][i].getText().equals(buttons[2][i].getText()))
                return true;
        }
        if (!buttons[0][0].getText().isEmpty() &&
                buttons[0][0].getText().equals(buttons[1][1].getText()) &&
                buttons[1][1].getText().equals(buttons[2][2].getText()))
            return true;
        if (!buttons[0][2].getText().isEmpty() &&
                buttons[0][2].getText().equals(buttons[1][1].getText()) &&
                buttons[1][1].getText().equals(buttons[2][0].getText()))
            return true;
        return false;
    }

    private boolean isFull() {
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (buttons[i][j].getText().isEmpty())
                    return false;
        return true;
    }

    @Override
    public void onClose() {
        // Réinitialiser l'état si besoin
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText("");
        currentPlayer = 'X';
        setFinished(false);
        if (statusLabel != null) {
            statusLabel.setText("Tour du joueur : X");
        }

    
    }
    
    @Override
    public void restart(Map map) {

        // Réinitialiser l'état du jeu
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                buttons[i][j].setText("");
        currentPlayer = 'X';
        setFinished(false);
        if (statusLabel != null) {
            statusLabel.setText("Tour du joueur : X");
        }
        super.restart(map);
    }}


