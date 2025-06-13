import javax.swing.*;
import java.awt.*;

public class DialogueManager {
    private JPanel parent;
    private DialoguePersonnalise currentDialogue;
    private JButton currentButton;
    private Runnable onCloseGlobal;

    public DialogueManager(JPanel parent) {
        this.parent = parent;
    }

    public void afficherDialogue(String message, String boutonTexte, Runnable onClose) {
        removeCurrentDialogue();
        currentDialogue = new DialoguePersonnalise(message, boutonTexte, () -> {
            removeCurrentDialogue();
            if (onClose != null) onClose.run();
            if (onCloseGlobal != null) onCloseGlobal.run();
        });
        parent.add(currentDialogue);
        currentDialogue.setBounds((parent.getWidth() - (parent.getWidth() * 3 / 4)) / 2, parent.getHeight() - parent.getHeight() / 4 - 20, parent.getWidth() * 3 / 4, parent.getHeight() / 4);
        parent.setLayout(null);
        parent.revalidate();
        parent.repaint();
    }

    public void afficherScript(String[] messages, String boutonTexte, Runnable onAllClose) {
        if (messages == null || messages.length == 0) return;
        final int[] index = {0};
        this.onCloseGlobal = onAllClose;
        Runnable nextDialogue = new Runnable() {
            @Override
            public void run() {
                if (index[0] < messages.length - 1) {
                    index[0]++;
                    afficherDialogue(messages[index[0]], boutonTexte, this);
                } else {
                    if (onAllClose != null) onAllClose.run();
                }
            }
        };
        afficherDialogue(messages[0], boutonTexte, nextDialogue);
    }

    public void removeCurrentDialogue() {
        if (currentDialogue != null) {
            parent.remove(currentDialogue);
            currentDialogue = null;
        }
        parent.revalidate();
        parent.repaint();
    }
}
