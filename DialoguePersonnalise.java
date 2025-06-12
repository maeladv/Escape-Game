import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class DialoguePersonnalise extends JPanel {
    private static Image backgroundImage;
    private static Font yosterFont;
    private JTextArea labelMessage;
    private String fullMessage;
    private String currentMessage = "";
    private Timer animationTimer;
    private int currentIndex = 0;
    private int animationSpeed = 40;
    private static final int MAX_LINES = 3;
    private static final int MAX_CHARS_PER_LINE = 50;

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
        setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30)); // Ajout du padding autour du panel
        this.fullMessage = formatMessage(message);
        labelMessage = new JTextArea(currentMessage);
        labelMessage.setEditable(false);
        labelMessage.setOpaque(false);
        labelMessage.setFocusable(false);
        labelMessage.setLineWrap(true);
        labelMessage.setWrapStyleWord(true);
        labelMessage.setForeground(Color.BLACK);
        labelMessage.setFont(yosterFont);
        labelMessage.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding interne du texte
        labelMessage.setRows(MAX_LINES); // Fixe la hauteur avant l'animation
        labelMessage.setColumns(MAX_CHARS_PER_LINE); // Ajouté pour garantir la largeur
        labelMessage.setPreferredSize(new Dimension(700, 90)); // Largeur et hauteur fixes pour 3 lignes
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
        JPanel panelBoutons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panelBoutons.setOpaque(false);
        panelBoutons.add(boutonOk);
        add(panelBoutons, BorderLayout.SOUTH);
        // S'assurer que le bouton ne prend pas trop de place
        panelBoutons.setMaximumSize(new Dimension(120, 60));
        panelBoutons.setPreferredSize(new Dimension(120, 60));
        panelBoutons.setMinimumSize(new Dimension(120, 60));
    }

    private String formatMessage(String message) {
        // Formate le message sur 3 lignes max, coupe avec ... si trop long, sans couper les mots
        String[] words = message.split(" ");
        StringBuilder[] lines = new StringBuilder[MAX_LINES];
        for (int i = 0; i < MAX_LINES; i++) lines[i] = new StringBuilder();
        int lineIdx = 0;
        for (String word : words) {
            if (lines[lineIdx].length() + word.length() + (lines[lineIdx].length() > 0 ? 1 : 0) > MAX_CHARS_PER_LINE) {
                if (lineIdx < MAX_LINES - 1) {
                    lineIdx++;
                } else {
                    // Ajoute ... à la fin de la dernière ligne si le texte dépasse
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
                if (formatted.length() > 0) formatted.append("\n");
                formatted.append(lines[i]);
            }
        }
        return formatted.toString();
    }

    private void startTextAnimation() {
        currentIndex = 0;
        currentMessage = "";
        labelMessage.setRows(MAX_LINES);
        // Correction : utiliser un tableau de caractères pour éviter les problèmes d'encodage
        char[] chars = fullMessage.toCharArray();
        animationTimer = new Timer(animationSpeed, e -> {
            if (currentIndex < chars.length) {
                currentMessage += chars[currentIndex];
                labelMessage.setText(currentMessage);
                currentIndex++;
            } else {
                animationTimer.stop();
            }
        });
        animationTimer.start();
        labelMessage.repaint();
        this.repaint();
    }

    public void setAnimationSpeed(int speedMs) {
        this.animationSpeed = speedMs;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}