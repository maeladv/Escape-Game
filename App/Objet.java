package App;
import java.awt.Rectangle;

public class Objet {
    public Rectangle hitbox;
    public Runnable onCollision; // Action à exécuter lors de la collision
    private boolean alreadyTriggered = false;
    private boolean isActive = false; // Indique si l'objet est actif (joueur regarde dans sa direction)

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
    
    public void setActive(boolean active) {
        this.isActive = active;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    // Réinitialiser pour permettre une nouvelle interaction
    public void reset() {
        alreadyTriggered = false;
    }
}
