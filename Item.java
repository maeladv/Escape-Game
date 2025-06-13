import java.io.File;

public class Item {
    private String name;
    private String description;
    private File imageFile;

    public Item(String name, String description, File imageFile) {
        this.name = name;
        this.description = description;
        this.imageFile = imageFile;
    }

    public String getName() {
        return name;
    }
    
    // Alias pour compatibilité avec Inventaire
    public String getNom() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public File getImageFile() {
        return imageFile;
    }
}