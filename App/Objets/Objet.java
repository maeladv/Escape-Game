package App.Objets;
import java.awt.Rectangle;

public class Objet {
    private Rectangle hitbox;
    private Runnable onCollision; // Action à exécuter lors de la collision
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
    
    public void reset() {
        alreadyTriggered = false;
    }

    // Getters et setters
    public Rectangle getHitbox() { return hitbox; }
    public void setHitbox(Rectangle hitbox) { this.hitbox = hitbox; }
    public Runnable getOnCollision() { return onCollision; }
    public void setOnCollision(Runnable onCollision) { this.onCollision = onCollision; }
    public boolean isAlreadyTriggered() { return alreadyTriggered; }
    public void setAlreadyTriggered(boolean alreadyTriggered) { this.alreadyTriggered = alreadyTriggered; }
}
