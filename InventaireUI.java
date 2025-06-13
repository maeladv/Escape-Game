    import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class InventaireUI extends JPanel {
    private Inventaire inventaire;
    private int slotSize = 64; // Taille d'un emplacement d'inventaire
    private int padding = 10;  // Espacement entre les emplacements
    private int rows = 1;      // Nombre de lignes dans l'inventaire
    private BufferedImage defaultImage; // Image par défaut si l'image de l'item n'est pas disponible
    
    public InventaireUI(Inventaire inventaire) {
        this.inventaire = inventaire;
        try {
            // Charger une image par défaut pour les items sans image
            defaultImage = ImageIO.read(new java.io.File("assets/joueur/droite.png"));
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de l'image par défaut : " + e.getMessage());
        }
        
        int cols = (int) Math.ceil(inventaire.getItems().size() / (double) rows);
        
        // Définir la taille préférée du panneau d'inventaire
        int width = cols * (slotSize + padding) + padding;
        int height = rows * (slotSize + padding) + padding;
        setPreferredSize(new Dimension(width, height));
        
        // Définir un fond semi-transparent
        setBackground(new Color(50, 50, 50, 180));
        
        // Ajouter des écouteurs de souris pour interagir avec les items
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Déterminer quel slot a été cliqué
                int x = e.getX();
                int y = e.getY();
                
                int col = (x - padding) / (slotSize + padding);
                int row = (y - padding) / (slotSize + padding);
                
                int index = row * cols + col;
                
                ArrayList<Item> items = inventaire.getItems();
                if (index >= 0 && index < items.size()) {
                    Item selectedItem = items.get(index);
                    JOptionPane.showMessageDialog(
                        null,
                        selectedItem.getDescription(),
                        selectedItem.getNom(),
                        JOptionPane.INFORMATION_MESSAGE
                    );
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        ArrayList<Item> items = inventaire.getItems();
        int cols = (int) Math.ceil(items.size() / (double) rows);
        
        // Dessiner les emplacements d'inventaire
        for (int i = 0; i < items.size(); i++) {
            int row = i / cols;
            int col = i % cols;
            
            int x = padding + col * (slotSize + padding);
            int y = padding + row * (slotSize + padding);
            
            // Dessiner le fond de l'emplacement
            g.setColor(new Color(70, 70, 70, 200));
            g.fillRect(x, y, slotSize, slotSize);
            
            // Dessiner la bordure de l'emplacement
            g.setColor(new Color(120, 120, 120));
            g.drawRect(x, y, slotSize, slotSize);
            
            // Récupérer l'item pour cet emplacement
            Item item = items.get(i);
            
            // Dessiner l'image de l'item
            try {
                BufferedImage itemImage = null;
                try {
                    itemImage = ImageIO.read(item.getImageFile());
                } catch (IOException e) {
                    // Utiliser l'image par défaut si l'image de l'item n'est pas disponible
                    itemImage = defaultImage;
                    System.out.println("Utilisation de l'image par défaut pour : " + item.getNom());
                }
                
                if (itemImage != null) {
                    // Dessiner l'image centrée dans l'emplacement
                    int imgWidth = Math.min(itemImage.getWidth(), slotSize - 10);
                    int imgHeight = Math.min(itemImage.getHeight(), slotSize - 10);
                    
                    int imgX = x + (slotSize - imgWidth) / 2;
                    int imgY = y + (slotSize - imgHeight) / 2;
                    
                    g.drawImage(itemImage, imgX, imgY, imgWidth, imgHeight, null);
                }
            } catch (Exception e) {
                System.out.println("Erreur lors du dessin de l'image pour " + item.getNom() + ": " + e.getMessage());
            }
            
            // Dessiner le nom de l'item
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            FontMetrics fm = g.getFontMetrics();
            String name = item.getNom();
            int textWidth = fm.stringWidth(name);
            
            if (textWidth > slotSize) {
                name = name.substring(0, 5) + "...";
                textWidth = fm.stringWidth(name);
            }
            
            g.drawString(name, x + (slotSize - textWidth) / 2, y + slotSize + 15);
        }
    }
    
    // Méthode pour mettre à jour l'inventaire et redessiner
    public void updateInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
        repaint();
    }
}
