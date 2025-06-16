package App.Objets;
import java.awt.Rectangle;

import App.Inventaire.Item;
import App.Inventaire.Inventaire;

public class Objet {
    private String name; // Nom de l'objet
    private Rectangle hitbox;
    private Runnable onCollision; // Action à exécuter lors de la collision
    private Runnable missingItem;
    private boolean alreadyTriggered = false;
    private boolean isActive = false; // Indique si l'objet est actif (joueur regarde dans sa direction)
    private Inventaire inventaire;
    private Item itemToInteract; // Item associé à cet objet, peut être null si pas d'interaction

    public Objet(String name, Rectangle hitbox, Inventaire inventaire, Runnable onCollision) {
        this.name = name;
        this.hitbox = hitbox;
        this.inventaire = inventaire;
        this.itemToInteract = null;
        this.onCollision = onCollision;
    }    
    
    public Objet(String name, Rectangle hitbox, Inventaire inventaire, Item itemToInteract, Runnable onCollision, Runnable missingItem) {
        this.name = name;
        this.hitbox = hitbox;
        this.inventaire = inventaire;
        this.itemToInteract = itemToInteract;
        this.onCollision = onCollision;
        this.missingItem = missingItem;
    }

    public void trigger() {
        if (!alreadyTriggered && missingItem != null && itemToInteract != null && !inventaire.contientItem(itemToInteract.getName())) {
            missingItem.run();
        } else if (!alreadyTriggered && onCollision != null && itemToInteract != null && inventaire.contientItem(itemToInteract.getName())) {
            onCollision.run();
            alreadyTriggered = true;
        } else if (!alreadyTriggered && onCollision != null && itemToInteract == null) {
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
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Rectangle getHitbox() { return hitbox; }
    public void setHitbox(Rectangle hitbox) { this.hitbox = hitbox; }
    public Runnable getOnCollision() { return onCollision; }
    public void setOnCollision(Runnable onCollision) { this.onCollision = onCollision; }
    public boolean isAlreadyTriggered() { return alreadyTriggered; }
    public void setAlreadyTriggered(boolean alreadyTriggered) { this.alreadyTriggered = alreadyTriggered; }
    public Item getItemToInteract() { return itemToInteract; }
    public void setItemToInteract(Item itemToInteract) { this.itemToInteract = itemToInteract; }
}
