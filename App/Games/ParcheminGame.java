package App.Games;

import App.Utils.GameUtils;
import App.Dialogue.DialogueManager;
import App.Map.Map;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ParcheminGame extends Game {
    private JLabel parcheminLabel;
    private JTextField answerField;
    private JButton validateButton;
    private JLabel scriptLabel;
    private boolean solved = false;
    private final String correctCode = "INSACODE"; // À personnaliser

    // Constructeur principal :
    public ParcheminGame(boolean devMode, DialogueManager dialogueManager) {
        super(devMode, "Parchemin", "Mini-jeu du parchemin", "assets/items/", dialogueManager, () -> {
            dialogueManager.afficherDialogue("Bravo ! Le parchemin est déchiffré.", "Super !");
        });
    }

    public ParcheminGame(boolean devMode, DialogueManager dialogueManager, Runnable onCloseCallback) {
        super(devMode, "Parchemin", "Mini-jeu du parchemin", "assets/items/", dialogueManager, onCloseCallback);
    }

    @Override
    public JPanel getMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image bg = new ImageIcon("assets/games/bg-table.png").getImage();
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        mainPanel.setLayout(null);

        // Script label (top)
        scriptLabel = new JLabel("Oh, ce parchemin semble chiffré... Que pouvons-nous faire ?");
        scriptLabel.setBounds(50, 20, 500, 40);
        scriptLabel.setFont(new Font("Serif", Font.BOLD, 18));
        scriptLabel.setForeground(Color.WHITE);
        mainPanel.add(scriptLabel);

        // Parchemin image (right)
        parcheminLabel = new JLabel();
        ImageIcon parcheminIcon = new ImageIcon("assets/items/parchemin.png");
        Image parcheminImage = parcheminIcon.getImage().getScaledInstance(256, 256, Image.SCALE_SMOOTH);
        parcheminLabel.setIcon(new ImageIcon(parcheminImage));
        parcheminLabel.setBounds(400, 100, 256, 256);
        mainPanel.add(parcheminLabel);

        // Text field with background (left)
        answerField = new JTextField();
        answerField.setBounds(80, 180, 200, 40);
        answerField.setFont(new Font("Serif", Font.PLAIN, 20));
        // white text color
        answerField.setForeground(Color.WHITE);
        answerField.setOpaque(false); // 
        mainPanel.add(answerField);

        // Validate button with background
        validateButton = new JButton(new ImageIcon("assets/bouton.png"));
        validateButton.setText("Valider");
        validateButton.setHorizontalTextPosition(JButton.CENTER);
        validateButton.setVerticalTextPosition(JButton.CENTER);
        validateButton.setBounds(80, 240, 200, 50);
        validateButton.setFont(new Font("Serif", Font.BOLD, 20));
        validateButton.setForeground(Color.WHITE);
        validateButton.setContentAreaFilled(false);
        validateButton.setBorderPainted(false);
        validateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkAnswer();
            }
        });
        mainPanel.add(validateButton);
        return mainPanel;
    }

    private void checkAnswer() {
        if (solved) return;
        String answer = answerField.getText().trim();
        if (answer.equalsIgnoreCase(correctCode)) {
            parcheminLabel.setIcon(new ImageIcon("assets/items/parchemin_dechiffre.png"));
            scriptLabel.setText("Bravo ! Le parchemin est déchiffré.");
            solved = true;
            setFinished(true); // Ferme le mini-jeu à la réussite
        } else {
            scriptLabel.setText("Ce n'est pas la bonne réponse...");
        }
    }

    @Override
    public void onClose() {
        // Réinitialiser l'état si besoin
        solved = false;
        if (parcheminLabel != null) {
            parcheminLabel.setIcon(new ImageIcon("assets/items/parchemin.png"));
        }
        if (scriptLabel != null) {
            scriptLabel.setText("Oh, ce parchemin semble chiffré... Que pouvons-nous faire ?");
        }
        if (answerField != null) {
            answerField.setText("");
        }
        setFinished(false);
        super.onClose();
    }

    @Override
    public void restart(Map map) {
        // Réinitialiser l'état du jeu
        solved = false;
        if (parcheminLabel != null) {
            parcheminLabel.setIcon(new ImageIcon("assets/items/parchemin.png"));
        }
        if (scriptLabel != null) {
            scriptLabel.setText("Oh, ce parchemin semble chiffré... Que pouvons-nous faire ?");
        }
        if (answerField != null) {
            answerField.setText("");
        }
        setFinished(false);
        super.restart(map);
    }
}
