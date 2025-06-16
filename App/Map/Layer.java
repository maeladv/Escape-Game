package App.Map;
import java.awt.Graphics;
import java.util.ArrayList;

import App.Utils.Drawable;

public class Layer {
    private ArrayList<Drawable> elements;

    public Layer() {
        elements = new ArrayList<>();
    }

    public void addElement(Drawable element) {
        elements.add(element);
    }
    
    public void clear() {
        elements.clear();
    }

    public void draw(Graphics g) {
        for (Drawable element : elements) {
            element.draw(g);
        }
    }

    public ArrayList<Drawable> getElements() {
        return elements;
    }

    public void setElements(ArrayList<Drawable> elements) {
        this.elements = elements;
    }
}
