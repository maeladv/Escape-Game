package App.Inventaire;
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

    public void setName(String name) { 
        this.name = name; 
    }
    
    public void setDescription(String description) { 
        this.description = description; 
    }
    
    public void setImageFile(File imageFile) { 
        this.imageFile = imageFile; 
    }
}