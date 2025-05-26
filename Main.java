import javax.swing.*;

public class Main {

        private static void initialiserFenetre() {
        // Créer une fenêtre Jframe avec un titre
        JFrame window = new JFrame("Escape From The Biblioteca");
        // Faire en sorte que le script se coupe  lorsque l'utilisateur clique sur la croix
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Créer et ajouter la map
        Map map = new Map();
        window.add(map);
        window.pack();
        // Empêcher la redimension de la fenêtre
        window.setResizable(false);
        // Ouvrir la fenêtre au centre de l'écran
        window.setLocationRelativeTo(null);
        // Afficher la fenêtre
        window.setVisible(true);
    }




    // fonction principale
    public static void main(String[] args) {
        initialiserFenetre();
        
        
        
    }
}
