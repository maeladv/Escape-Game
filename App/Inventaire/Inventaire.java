package App.Inventaire;
import java.util.ArrayList;

public class Inventaire {
    private ArrayList<Item> items;
    private int capacity;

    // Liste pour garder une trace des items qui ont été retirés
    private ArrayList<Item> itemsRetires = new ArrayList<>();

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

    // Modifier la méthode retirerItem pour garder une trace des items retirés
    public boolean retirerItem(Item item) {
        boolean result = items.remove(item);
        if (result) {
            itemsRetires.add(item); // Ajouter l'item à la liste des items retirés
        }
        return result;
    }

    public boolean retirerItem(String itemName) {
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getName().equals(itemName)) {
                Item item = items.get(i);
                items.remove(i);
                itemsRetires.add(item); // Ajouter l'item à la liste des items retirés
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
            if (item.getName().equals(itemName)) {
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

    // Vérifier si un item a déjà été retiré de l'inventaire
    public boolean itemAEteRetire(Item item) {
        return itemsRetires.contains(item);
    }
    
    // Surcharge pour vérifier par nom
    public boolean itemAEteRetire(String itemName) {
        for (Item item : itemsRetires) {
            if (item.getName().equals(itemName)) {
                return true;
            }
        }
        return false;
    }
}