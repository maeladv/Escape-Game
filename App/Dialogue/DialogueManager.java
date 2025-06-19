package App.Dialogue;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.awt.event.*;
import java.awt.image.BufferedImage;

import App.Joueur.Joueur;

public class DialogueManager {
    private JPanel parent;
    private DialoguePersonnalise currentDialogue;
    private JButton currentButton;
    private Runnable onCloseGlobal;
    private Joueur joueur;

    public DialogueManager(JPanel parent, Joueur joueur) {
        this.parent = parent;
        this.joueur = joueur;
    }    public void afficherDialogue(String message, String boutonTexte) {
        afficherDialogue(message, boutonTexte, null);
    }
    
    public void afficherDialogue(String message, String boutonTexte, Runnable onClose) {
        joueur.setCanMove(false); // Désactiver le mouvement du joueur pendant le dialogue
        removeCurrentDialogue();
        currentDialogue = new DialoguePersonnalise(message, boutonTexte, () -> {
            removeCurrentDialogue();
            joueur.setCanMove(true); // Réactiver le mouvement du joueur après fermeture
            if (onClose != null) {
                onClose.run();
            }
            // Ne pas appeler onCloseGlobal ici !
        });
        parent.add(currentDialogue);
        parent.setComponentZOrder(currentDialogue, 0); // Met le dialogue au premier plan
        currentDialogue.setBounds((parent.getWidth() - (parent.getWidth() * 3 / 4)) / 2, parent.getHeight() - parent.getHeight() / 4 - 20, parent.getWidth() * 3 / 4, parent.getHeight() / 4);
        parent.setLayout(null);
        parent.revalidate();
        parent.repaint();
        
        // S'assurer que la boîte de dialogue prend le focus pour capter les événements clavier
        currentDialogue.requestFocusInWindow();
    }    public void afficherScript(String[] messages, String boutonTexte) {
        afficherScript(messages, boutonTexte, null);
    }
    
    public void afficherScript(String[] messages, String boutonTexte, Runnable onAllClose) {
        if (messages == null || messages.length == 0) return;
        final int[] index = {0};
        Runnable nextDialogue = new Runnable() {
            @Override
            public void run() {
                if (index[0] < messages.length - 1) {
                    index[0]++;
                    afficherDialogue(messages[index[0]], boutonTexte, this);
                } else {
                    // Dernier message, on exécute le callback une seule fois
                    if (onAllClose != null) {
                        onAllClose.run();
                    }
                }
            }
        };
        afficherDialogue(messages[0], boutonTexte, nextDialogue);
    }

    public void removeCurrentDialogue() {
        if (currentDialogue != null) {
            parent.remove(currentDialogue);
            currentDialogue = null;
        }
        parent.revalidate();
        parent.repaint();
    }

    // Classe interne fusionnée de DialoguePersonnalise
    private static class DialoguePersonnalise extends JPanel {
        private static Image backgroundImage;
        private static Font yosterFont;
        private JTextArea labelMessage;
        private String fullMessage;
        private String currentMessage = "";
        private Timer animationTimer;
        private int currentIndex = 0;
        private int animationSpeed = 2; // Vitesse de l'animation en millisecondes
        private static final int MAX_LINES = 3;
        private static final int MAX_CHARS_PER_LINE = 50;
        private JPanel customPanel; // Pour le constructeur avec panel personnalisé

        static {
            try {
                yosterFont = Font.createFont(Font.TRUETYPE_FONT, new File("assets/yoster.ttf")).deriveFont(16f);
                GraphicsEnvironment.getLocalGraphicsEnvironment().registerFont(yosterFont);
            } catch (Exception e) {
                yosterFont = new Font("Arial", Font.BOLD, 16);
            }
            try {
                backgroundImage = javax.imageio.ImageIO.read(new File("assets/dialogue.png"));
            } catch (IOException e) {
                backgroundImage = null;
            }
        }

        public DialoguePersonnalise(String message, String boutonTexte, Runnable onClose) {
            setLayout(new BorderLayout());
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            this.fullMessage = formatMessage(message);
            labelMessage = new JTextArea(currentMessage);
            labelMessage.setEditable(false);
            labelMessage.setOpaque(false);
            labelMessage.setFocusable(false);
            labelMessage.setLineWrap(true);
            labelMessage.setWrapStyleWord(true);
            labelMessage.setForeground(Color.BLACK);
            labelMessage.setFont(yosterFont);
            labelMessage.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            labelMessage.setRows(MAX_LINES);
            labelMessage.setColumns(MAX_CHARS_PER_LINE);
            labelMessage.setPreferredSize(new Dimension(700, 90));
            add(labelMessage, BorderLayout.CENTER);
            startTextAnimation();

            if (boutonTexte == null || boutonTexte.length() == 0) boutonTexte = "OK";
            if (boutonTexte.length() > 10) boutonTexte = boutonTexte.substring(0, 10);
            JButton boutonOk = new JButton(boutonTexte);
            boutonOk.setIcon(new ImageIcon(new ImageIcon("assets/bouton.png").getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH)));
            boutonOk.setContentAreaFilled(false);
            boutonOk.setFocusPainted(false);
            boutonOk.setBorderPainted(false);
            boutonOk.setHorizontalTextPosition(SwingConstants.CENTER);
            boutonOk.setVerticalTextPosition(SwingConstants.CENTER);
            boutonOk.setForeground(Color.WHITE);
            boutonOk.setFont(yosterFont);
            boutonOk.addActionListener(e -> {
                if (animationTimer != null && animationTimer.isRunning()) animationTimer.stop();
                onClose.run();
            });
            
            // Ajouter un écouteur de clavier pour gérer la touche espace
            setFocusable(true);
            addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent e) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                        if (animationTimer != null && animationTimer.isRunning()) {
                            // Si l'animation est en cours, l'arrêter et afficher tout le texte
                            animationTimer.stop();
                            currentMessage = fullMessage;
                            labelMessage.setText(currentMessage);
                        } else {
                            // Sinon, fermer le dialogue
                            onClose.run();
                        }
                    }
                }
            });
            
            JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
            panelBoutons.setOpaque(false);
            panelBoutons.add(boutonOk);
            add(panelBoutons, BorderLayout.SOUTH);
            panelBoutons.setMaximumSize(new Dimension(120, 60));
            panelBoutons.setPreferredSize(new Dimension(120, 60));
            panelBoutons.setMinimumSize(new Dimension(120, 60));
        }

        private String formatMessage(String message) {
            String[] words = message.split(" ");
            StringBuilder[] lines = new StringBuilder[MAX_LINES];
            for (int i = 0; i < MAX_LINES; i++) lines[i] = new StringBuilder();
            int lineIdx = 0;
            for (String word : words) {
                if (lines[lineIdx].length() + word.length() + (lines[lineIdx].length() > 0 ? 1 : 0) > MAX_CHARS_PER_LINE) {
                    if (lineIdx < MAX_LINES - 1) {
                        lineIdx++;
                    } else {
                        if (lines[lineIdx].length() > MAX_CHARS_PER_LINE - 3) {
                            lines[lineIdx].setLength(MAX_CHARS_PER_LINE - 3);
                        }
                        lines[lineIdx].append("...");
                        break;
                    }
                }
                if (lines[lineIdx].length() > 0) lines[lineIdx].append(" ");
                lines[lineIdx].append(word);
            }
            StringBuilder formatted = new StringBuilder();
            for (int i = 0; i < MAX_LINES; i++) {
                if (lines[i].length() > 0) {
                    formatted.append(lines[i]);
                    if (i < MAX_LINES - 1) formatted.append("\n");
                }
            }
            return formatted.toString();
        }

        private void startTextAnimation() {
            currentIndex = 0;
            currentMessage = "";
            labelMessage.setText("");
            animationTimer = new Timer(animationSpeed, e -> {
                if (currentIndex < fullMessage.length()) {
                    currentMessage += fullMessage.charAt(currentIndex);
                    labelMessage.setText(currentMessage);
                    currentIndex++;
                } else {
                    animationTimer.stop();
                }
            });
            animationTimer.start();
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }
}
