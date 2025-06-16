package App.Games;

import App.Utils.GameUtils;
import App.Dialogue.DialogueManager;

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

    @Override
    public JPanel getMainPanel() {
        JPanel panel = new JPanel(null);
        panel.setBackground(new Color(240, 240, 255)); //  Couleur de fond blanche
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
                int row = i, col = j;
                btn.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (!isFinished() && btn.getText().equals("")) {
                            btn.setText(String.valueOf(currentPlayer));
                            if (checkWin()) {
                                statusLabel.setText("Le joueur " + currentPlayer + " a gagné !");
                                setFinished(true);
                            } else if (isFull()) {
                                statusLabel.setText("Match nul !");
                                setFinished(true);
                            } else {
                                currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
                                statusLabel.setText("Tour du joueur : " + currentPlayer);
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
        if (statusLabel != null)
            statusLabel.setText("Tour du joueur : X");
        if (dialogueManager != null) {
            dialogueManager.afficherDialogue("Bravo ! Vous avez terminé le mini-jeu de morpion. Indice : Le code du coffre est 1234.", "OK");
        }
    }
}
