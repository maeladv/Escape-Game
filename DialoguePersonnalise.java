import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class DialoguePersonnalise extends JPanel {
    private static Image backgroundImage;

    static {
        try {
            backgroundImage = javax.imageio.ImageIO.read(new java.io.File("assets/dialogue.png"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }
    }

    public DialoguePersonnalise(String message, Runnable onClose) {
        setLayout(new BorderLayout());
        setOpaque(true); // Rendre le panneau opaque pour qu'il soit visible

        // Ajouter le message
        JLabel labelMessage = new JLabel(message);
        labelMessage.setHorizontalAlignment(SwingConstants.CENTER);
        labelMessage.setForeground(Color.BLACK);
        labelMessage.setFont(new Font("Arial", Font.BOLD, 16));
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("assets/yoster.ttf")).deriveFont(16f);
            labelMessage.setFont(customFont);
        } catch (Exception e) {
            e.printStackTrace();
            labelMessage.setFont(new Font("Arial", Font.BOLD, 16)); // Police par défaut en cas d'erreur
        }
        add(labelMessage, BorderLayout.CENTER);

        // Ajouter un bouton "OK"
        JButton boutonOk = new JButton("OK");
        boutonOk.setIcon(new ImageIcon(new ImageIcon("assets/bouton.png").getImage().getScaledInstance(100, 50, Image.SCALE_SMOOTH))); // Redimensionner l'image
        boutonOk.setContentAreaFilled(false); // Supprimer le fond par défaut
        boutonOk.setFocusPainted(false); // Supprimer l'effet de focus
        boutonOk.setBorderPainted(false); // Supprimer la bordure
        boutonOk.setHorizontalTextPosition(SwingConstants.CENTER); // Centrer le texte sur l'image
        boutonOk.setVerticalTextPosition(SwingConstants.CENTER); // Centrer le texte verticalement
        boutonOk.setForeground(Color.WHITE); // Définir la couleur du texte sur blanc
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("assets/yoster.ttf")).deriveFont(16f);
            boutonOk.setFont(customFont);
        } catch (Exception e) {
            e.printStackTrace();
            boutonOk.setFont(new Font("Arial", Font.BOLD, 16)); // Police par défaut en cas d'erreur
        }
        boutonOk.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onClose.run(); // Appeler la méthode de fermeture
            }
        });
        JPanel panelBoutons = new JPanel();
        panelBoutons.setOpaque(false); // Rendre le fond transparent
        panelBoutons.add(boutonOk);
        add(panelBoutons, BorderLayout.SOUTH);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(Color.BLACK); // Fond temporaire noir si l'image n'est pas chargée
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}