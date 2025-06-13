package App.Inventaire;
import java.util.ArrayList;

public class Inventaire {
    private ArrayList<Item> items;
    private int capacity;

    public Inventaire(int capacity) {
        this.capacity = capacity;
        this.items = new ArrayList<>(capacity);
    }

    public boolean ajouterItem(Item item) {
        if (items.size() < capacity) {
            items.add(item);
            return true; // Item ajouté avec succès
        } else {
            return false; // Inventaire plein
        }
    }

    public boolean retirerItem(Item item) {
        return items.remove(item); // Retourne true si l'élément a été retiré, false sinon
    }

    public boolean retirerItem(String itemName) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getNom().equals(itemName)) {
                items.remove(i);
                return true; // Item retiré avec succès
            }
        }
        return false; // Item non trouvé
    }

    public ArrayList<Item> getItems() {
        return new ArrayList<>(items); // Retourne une copie de la liste pour éviter des modifications externes
    }

    public int getItemCount() {
        return items.size();
    }
    
    public boolean contientItem(String itemName) {
        for (Item item : items) {
            if (item.getNom().equals(itemName)) {
                return true;
            }
        }
        return false;
    }
    
    public boolean contientItem(Item item) {
        return items.contains(item);
    }

    public void setItems(ArrayList<Item> items) { this.items = items; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}