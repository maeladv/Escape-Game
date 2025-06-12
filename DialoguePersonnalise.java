import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DialoguePersonnalise extends JPanel {
    public DialoguePersonnalise(String message, Runnable onClose) {
        setLayout(new BorderLayout());
        setOpaque(true); // Rendre le panneau opaque pour qu'il soit visible
        setBackground(new Color(0, 0, 0, 200)); // Fond semi-transparent mais visible

        // Ajouter le message
        JLabel labelMessage = new JLabel(message);
        labelMessage.setHorizontalAlignment(SwingConstants.CENTER);
        labelMessage.setForeground(Color.WHITE);
        labelMessage.setFont(new Font("Arial", Font.BOLD, 16));
        add(labelMessage, BorderLayout.CENTER);

        // Ajouter un bouton "OK"
        JButton boutonOk = new JButton("OK");
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
}