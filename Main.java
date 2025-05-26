import javax.swing.*;

public class Main {

        private static void initialiserFenetre() {
        // Créer une fenêtre Jframe avec un titre
        JFrame window = new JFrame("Escape From The Biblioteca");
        // Faire en sorte que le script se coupe  lorsque l'utilisateur clique sur la croix
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // create the jpanel to draw on.
        // this also initializes the game loop
        // Map map = new Map();
        // add the jpanel to the window
        // window.add(board);
        // pass keyboard inputs to the jpanel
        // window.addKeyListener(board);
        
        // don't allow the user to resize the window
        window.setResizable(false);
        // fit the window size around the components (just our jpanel).
        // pack() should be called after setResizable() to avoid issues on some platforms
        window.pack();
        // open window in the center of the screen
        window.setLocationRelativeTo(null);
        // display the window
        window.setVisible(true);
    }




    // fonction principale
    public static void main(String[] args) {
        initialiserFenetre();
        
        
        
    }
}
