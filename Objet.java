import java.awt.Rectangle;

public class Objet {
    public Rectangle hitbox;
    public Runnable onCollision; // Action à exécuter lors de la collision
    private boolean alreadyTriggered = false;

    public Objet(Rectangle hitbox, Runnable onCollision) {
        this.hitbox = hitbox;
        this.onCollision = onCollision;
    }

    public void trigger() {
        if (!alreadyTriggered && onCollision != null) {
            onCollision.run();
            alreadyTriggered = true;
        }
    }
}
