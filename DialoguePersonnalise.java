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
    private static final int MAX_CHARS_PER_LINE = 60;

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

    public DialoguePersonnalise(String message, Runnable onClose) {
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
        add(labelMessage, BorderLayout.CENTER);
        startTextAnimation();

        JButton boutonOk = new JButton("OK");
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
        JPanel panelBoutons = new JPanel();
        panelBoutons.setOpaque(false);
        panelBoutons.add(boutonOk);
        add(panelBoutons, BorderLayout.SOUTH);
    }

    private String formatMessage(String message) {
        String[] words = message.split(" ");
        StringBuilder formatted = new StringBuilder();
        StringBuilder line = new StringBuilder();
        int lineCount = 0;
        for (String word : words) {
            if (line.length() + word.length() + 1 > MAX_CHARS_PER_LINE) {
                if (lineCount < MAX_LINES - 1) {
                    formatted.append(line).append("\n");
                    line = new StringBuilder(word);
                    lineCount++;
                } else {
                    String truncated = line.toString();
                    if (truncated.length() > MAX_CHARS_PER_LINE - 3) {
                        truncated = truncated.substring(0, MAX_CHARS_PER_LINE - 3);
                    }
                    formatted.append(truncated).append("...");
                    return formatted.toString();
                }
            } else {
                if (line.length() > 0) line.append(" ");
                line.append(word);
            }
        }
        if (line.length() > 0 && lineCount < MAX_LINES) {
            formatted.append(line);
        }
        return formatted.toString();
    }

    private void startTextAnimation() {
        currentIndex = 0;
        currentMessage = "";
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