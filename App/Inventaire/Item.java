package App.Inventaire;
import java.io.File;
import App.Objets.Objet;

public class Item {
    private String name;
    private String description;
    private File imageFile;
    private Objet objet; // Référence à l'objet associé

    public Item(String name, String description, File imageFile, Objet objet) {
        this.name = name;
        this.description = description;
        this.imageFile = imageFile;
        this.objet = objet;
    }

    public String getName() {return name;}
    public void setName(String name) { this.name = name;}

    public String getDescription() {return description;}
    public void setDescription(String description) { this.description = description; }

    public File getImageFile() {return imageFile;}
    public void setImageFile(File imageFile) { this.imageFile = imageFile; }

    public Objet getObjet() {return objet;}
    public void setObjet(Objet objet) { this.objet = objet; }
}