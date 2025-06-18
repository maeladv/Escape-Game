package App.Controllers;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import App.Animation.Animation;
import App.Dialogue.DialogueManager;
import App.Inventaire.Inventaire;
import App.Inventaire.InventaireUI;
import App.Inventaire.Item;
import App.Joueur.Joueur;
import App.Map.Map;
import App.Objets.Objet;
import App.Utils.Drawable;
import App.Utils.GameUtils;
import App.Games.Game;
import App.Games.MorpionGame;
import App.Games.ParcheminGame;
import App.Games.CouleursGame;

/**
 * GameController centralizes game logic and coordinates between different
 * components.
 * This class is responsible for handling game state and managing interactions
 * between the player, map, inventory, and other game elements.
 */
public class GameController {
    private Map map;
    private Joueur joueur;
    private Inventaire inventaire;
    private InventaireUI inventaireUI;
    private DialogueManager dialogueManager;
    private boolean devMode;
    private int playerSize;
    private int interactionZoneSize;
    private Animation animation; // Animation system

    private int currentMapIndex;
    private List<List<Objet>> objetsParMap;
    private List<List<Rectangle>> mursParMap;
    private List<Item> allItems; // Liste de tous les items disponibles dans le jeu

    private List<Game> jeux; // Liste des mini-jeux disponibles

    public GameController(Map map, Joueur joueur, Inventaire inventaire, InventaireUI inventaireUI,
            boolean devMode, int playerSize, int interactionZoneSize) {
        this.map = map;
        this.joueur = joueur;
        this.inventaire = inventaire;
        this.inventaireUI = inventaireUI;
        this.devMode = devMode;
        this.playerSize = playerSize;
        this.interactionZoneSize = interactionZoneSize;

        // Initialize the animation system
        this.animation = new Animation(map);

        // Initialize collections
        this.objetsParMap = new ArrayList<>();
        this.mursParMap = new ArrayList<>();
        this.allItems = new ArrayList<>();

        // Create the DialogueManager with a reference to the player
        this.dialogueManager = new DialogueManager(map, joueur);

        // Set the DialogueManager to the InventaireUI
        this.inventaireUI.setDialogueManager(dialogueManager);

        // Synchronise currentMapIndex et displayedMap
        this.currentMapIndex = map.getDisplayedMap();

        // Initialiser les mini jeux disponibles
        initializeJeux();

        // Initialize the maps and objects
        initializeMapsAndObjects();

        // Show intro script if starting with the first map
        if (currentMapIndex == 0) {
            showIntroScript();
        }
    }

    /**
     * Initialize the maps and objects for the game
     */
    private void initializeMapsAndObjects() {
        // Initialize walls for map 0 (Introduction)
        List<Rectangle> mursIntro = new ArrayList<>();
        // bords de la map
        mursIntro.add(new Rectangle(-20, 0, 20, map.getHeightValue()));
        mursIntro.add(new Rectangle(0, -20, map.getWidthValue(), 20));
        mursIntro.add(new Rectangle(map.getWidthValue(), 0, 10, map.getHeightValue()));
        mursIntro.add(new Rectangle(0, map.getHeightValue(), map.getWidthValue(), 20));
        // limite chemin supérieure
        mursIntro.add(new Rectangle(0, 340, 520, 100));
        // limite inférieure
        mursIntro.add(new Rectangle(0, 520, map.getWidthValue(), 100));
        // porte d'entrée
        mursIntro.add(new Rectangle(520, 300, 120, 100));
        // suite porte à droite
        mursIntro.add(new Rectangle(640, 340, 200, 100));

        mursParMap.add(mursIntro);

        // Initialize objects for map 0 (Introduction)
        List<Objet> objetsIntro = new ArrayList<>();

        Objet cle = new Objet("cle",
                new Rectangle(780, 500, 20, 20), inventaire,
                () -> {
                    dialogueManager.afficherDialogue(
                            "Vous avez trouvé une clé ! Elle pourrait être utile pour ouvrir des portes.",
                            "OK");
                });
        objetsIntro.add(cle);

        Item itemCle = new Item("Clé", "Une clé rouillée qui semble ancienne.",
                new java.io.File("assets/items/clef.png"), cle);
        allItems.add(itemCle);

        // Door object that changes map when interacted with
        objetsIntro.add(new Objet("porte d'entrée",
                new Rectangle(540, 310, 70, 110), inventaire, itemCle,
                () -> {

                    // Démarrer l'animation de fade out avant de changer de map
                    animation.startFadeOut(500, (Void v) -> {
                        changeMap(1);
                        GameUtils.printDev("Interaction avec la porte de la map 0 ! Changement de map.", devMode);
                    });
                    joueur.setPosition(50, 530);
                    joueur.setState(1);
                },
                () -> {
                    String[] messages = {
                            "Vous avez besoin d'une clef pour ouvrir cette porte.",
                            "Peut-être qu'un garde a laissé un double derrière un buisson ?"
                    };
                    dialogueManager.afficherScript(
                            messages, "OK");
                }));

        // ajout d'un objet interactif pour un mini jeu Game
        objetsIntro.add(new Objet("objet interactif",
                new Rectangle(100, 450, 50, 50), inventaire,
                () -> {
                    if (!jeux.isEmpty()) {
                        Game miniJeu = jeux.get(0); // Prendre le premier mini-jeu
                        GameUtils.printDev("Lancement du mini-jeu: " + miniJeu.getName(), devMode);
                        miniJeu.afficherMiniJeu(map); // Affiche le mini-jeu dans la même fenêtre
                    } else {
                        dialogueManager.afficherDialogue(
                                "Aucun mini-jeu disponible pour le moment.",
                                "OK");
                    }
                }));

        // Objet qui lance le mini-jeu Couleurs

        objetsParMap.add(objetsIntro);

        // Ajouter un élément drawable pour afficher les informations de debug
        if (devMode) {
            map.addDebugLayerElement(new Drawable() {
                @Override
                public void draw(Graphics g) {
                    if (devMode && currentMapIndex < mursParMap.size() && currentMapIndex < objetsParMap.size()) {
                        GameUtils.drawDebugRectangles(g, mursParMap.get(currentMapIndex),
                                objetsParMap.get(currentMapIndex), devMode);
                    }
                }
            });
        }

        // Initialize walls for map 1 (Library)
        List<Rectangle> mursBibliotheque = new ArrayList<>();
        // bords de la map
        mursBibliotheque.add(new Rectangle(-20, 0, 20, map.getHeightValue()));
        mursBibliotheque.add(new Rectangle(0, -20, map.getWidthValue(), 20));
        mursBibliotheque.add(new Rectangle(map.getWidthValue() - 15, 0, 10, map.getHeightValue()));
        mursBibliotheque.add(new Rectangle(0, map.getHeightValue(), map.getWidthValue(), 20));
        // mur bas gauche
        mursBibliotheque.add(new Rectangle(0, 450, 150, 50));
        // mur bas centre
        mursBibliotheque.add(new Rectangle(290, 450, 90, 50));
        mursBibliotheque.add(new Rectangle(380, 450, 48, 50));
        mursBibliotheque.add(new Rectangle(480, 450, 40, 50));
        // mur vertical
        mursBibliotheque.add(new Rectangle(370, 90, 10, map.getHeightValue()));
        mursBibliotheque.add(new Rectangle(525, 275, 10, map.getHeightValue()));
        // mur bas droite
        mursBibliotheque.add(new Rectangle(540, 430, 40, 50));
        mursBibliotheque.add(new Rectangle(690, 440, 90, 50));
        // mur millieu droite
        mursBibliotheque.add(new Rectangle(660, 270, 150, 50));
        // mur millieu
        mursBibliotheque.add(new Rectangle(250, 265, 340, 50));
        // mur millieu gauche
        mursBibliotheque.add(new Rectangle(75, 265, 105, 50));
        mursBibliotheque.add(new Rectangle(75, 285, 10, 50));
        // mur haut gauche
        mursBibliotheque.add(new Rectangle(0, 0, 190, 135));
        // mur haut centre
        mursBibliotheque.add(new Rectangle(245, 90, 200, 50));
        // mur haut droite
        mursBibliotheque.add(new Rectangle(580, 0, 200, 130));
        // tables
        mursBibliotheque.add(new Rectangle(200, 355, 35, 30));
        mursBibliotheque.add(new Rectangle(600, 515, 45, 20));

        mursParMap.add(mursBibliotheque);
        // Ajout d'une troisième map vide si besoin
        if (mursParMap.size() < 3) {
            mursParMap.add(new ArrayList<>());
        }

        // Initialiser les objets de la map1 (Bibliothèque)
        List<Objet> objetsBibliotheque = new ArrayList<>();

        // Première bibliothèque (entrée)
        Objet bibliothequeEntree = new Objet("bibliothèque entree",
                new Rectangle(70, 480, 50, 20), inventaire,
                () -> {
                    String[] messages = {
                            "Ah! Il semblerait que l'on puisse interagir avec ces bibliothèques.",
                            "Ce vieux bâtiment est donc une vieille bibliothèque.",
                            "Elle me semble très ancienne et poussiéreuse. Certains murs tombent même en ruines !",
                            "On dirait qu'elle n'a pas ete visitée depuis des années, pourtant les bougies sont encore allumées...",
                            "Il y a des livres sur les etagères, mais certaines sont encore trop poussiéreuses pour etre ouvertes.",
                            "Oh, je viens de trouver un balai ! Cela pourra me servir !"
                    };
                    dialogueManager.afficherScript(messages, "Suivant");
                });
        objetsBibliotheque.add(bibliothequeEntree);

        // 2ème bibliothèque (informations sur le sorcier - parchemin)

        // item balai
        Item balai = new Item("Balai", "Un balai. Me demande-t-on faire le ménage !? Quel culot !",
                new java.io.File("assets/items/balai.png"), bibliothequeEntree);
        // Ajouter l'item à la liste globale des items
        allItems.add(balai);

        Objet bibliothequeSorcier = new Objet("bibliothèque sorcier",
                new Rectangle(0, 480, 40, 20), inventaire,
                balai,
                () -> {
                    String[] messages = {
                            "Ahh ! Cela fait du bien de nettoyer un peu ! Je peux enfin y voir plus clair !",
                            "Voyons voir, il semblerait y avoir un parchemin ici. Il semble encore lisible !",
                            "Voyons voir ce qu'il y a dessus ! Il est plutôt abimé.",
                            "Mince, je ne parviens pas bien à voir ce qu'il y a dessus. Il me faudrait un peu plus de lumière.",
                    };
                    dialogueManager.afficherScript(messages, "Suivant");
                },
                () -> {
                    String[] messages = {
                            "MMhh, il y a quelque chose ici, mais je ne parviens pas à voir ce que c'est...",
                            "Il y a trop de poussière, il faudrait que je nettoie un peu.",
                    };
                    dialogueManager.afficherScript(messages, "OK");
                });
        // Ajouter l'objet de la bibliothèque à la liste des objets de la map
        objetsBibliotheque.add(bibliothequeSorcier);

        // Ajouter l'objet de la bibliothèque à la liste des objets de la map
        objetsParMap.add(objetsBibliotheque);
        // Ajout d'une troisième map vide si besoin
        if (objetsParMap.size() < 3) {
            objetsParMap.add(new ArrayList<>());
        }

        Item parchemin = new Item("Parchemin",
                "Un vieux parchemin à examiner. Impossible de l'étudier dans un coin sombre, il faut trouver un endroit calme et éclairé.",
                new java.io.File("assets/items/parchemin.png"), bibliothequeSorcier,
                () -> {
                    // Action à exécuter lorsque l'item est cliqué
                    dialogueManager.afficherDialogue(
                            "Vous avez trouvé un parchemin ! Il semble ancien et difficile à lire.",
                            "OK",
                            () -> {
                                // Réactiver le mouvement du joueur après la lecture
                                joueur.setCanMove(true);
                            });
                });
        // Ajouter l'item à la liste globale des items
        allItems.add(parchemin);

        // Table n°1 de la bibliothèque
        Objet tableBibliotheque1 = new Objet("table de la bibliothèque 1",
                new Rectangle(200, 350, 40, 30), inventaire, parchemin,
                () -> {
                    String[] messages = {
                            "Cette table est plus propre que le reste de la bibliothèque. On dirait que quelqu'un s'est assis ici récemment.",
                            "La bougie est encore allumée, c'est étrange.",
                            "Je peux m'installer ici pour étudier le parchemin !"
                    };
                    dialogueManager.afficherScript(
                            messages,
                            "OK",
                            () -> joueur.setCanMove(true));
                    // lancer le mini jeu 2 après validation du dialogue
                    dialogueManager.afficherDialogue(
                            "Une table avec une bougie, j'y verrai mieux ici !", "S'asseoire",
                            () -> {
                                // Lancer le mini-jeu 2
                                if (!jeux.isEmpty()) {
                                    Game miniJeu = jeux.get(1); // Prendre le 2ème mini-jeu
                                    GameUtils.printDev("Lancement du mini-jeu: " + miniJeu.getName(), devMode);
                                    miniJeu.afficherMiniJeu(map); // Affiche le mini-jeu dans la même fenêtre
                                } else {
                                    dialogueManager.afficherDialogue(
                                            "Aucun mini-jeu disponible pour le moment.",
                                            "OK");
                                }
                            });

                },
                () -> dialogueManager.afficherDialogue(
                        "Mmmh, c'est étrange, la bougie de cette table est encore allumée...",
                        "Fermer",
                        () -> joueur.setCanMove(true)));

        objetsBibliotheque.add(tableBibliotheque1);

        // Grande bibliothèque qui donne un livre
        Objet grandeBibliotheque = new Objet("Grande bibliothèque",
                new Rectangle(40, 100, 60, 30), inventaire, null,
                () -> {
                    String[] messages = {
                            "Ah! Avec cette échelle tout est plus accessible !",
                            "Je vais pouvoir accéder à ce livre que je voyais d'en bas !",
                            "C'est bon! Il est très ancien !",
                            "Il serait écrit par un certain Elzéar et semble contenir la recette d'une potion magique...",
                            "On dirait que cette potion a déjà été préparée mais... où pourrait-elle se trouver ?",
                            "Il y a aussi des notes dans les marges, \"Ce livre vous rendra imbattable au morpion !\"",
                    };
                    dialogueManager.afficherScript(messages, "Suivant", () -> {
                    });
                },
                () -> {
                    String[] messages = {
                            "Cette bibliothèque est immense !",
                            "Il y a tellement de livres, je ne sais pas par où commencer.",
                            "Je vois un livre mais je ne parviens pas à l'atteindre... Il me faudrait quelque chose pour grimper.",
                    };
                    dialogueManager.afficherScript(messages, "OK");
                });
        objetsBibliotheque.add(grandeBibliotheque);

        // item livre
        Item livre = new Item("Livre ancien",
                "Un livre ancien, il semble avoir été écrit par un sorcier.",
                new java.io.File("assets/items/livre.png"), grandeBibliotheque,
                () -> {
                    // à définir + tard car la table n'est pas encore créée
                });

        // Ajouter l'item à la liste globale des items
        allItems.add(livre);

        // Table with dialogue
        Objet tableBibliotheque = new Objet("table de la bibliothèque",
                new Rectangle(600, 510, 50, 20), inventaire, livre,
                () -> {
                    // Lancer le mini-jeu de morpion après validation du dialogue

                    dialogueManager.afficherDialogue(
                        "Ceci est une table de la bibliothèque. Appuyez sur OK pour continuer.",
                        "OK",
                        () -> {
                            joueur.setCanMove(true);
                            if (jeux.size() > 0) {
                                Game miniJeu = jeux.get(0); // MorpionGame est le 1er mini-jeu
                                GameUtils.printDev("Lancement du mini-jeu: " + miniJeu.getName(), devMode);
                                miniJeu.afficherMiniJeu(map); // Affiche le mini-jeu dans la même fenêtre
                            } else {
                                dialogueManager.afficherDialogue(
                                        "Aucun mini-jeu disponible pour le moment.",
                                        "OK");
                            }
                        });
                        
                        
                        
                    },
                () -> {
                    String[] messages = {
                        "Il y a une potion et des ingrédients sur cette table mais...",
                        "La potion semble incomplète, peut-être que je peux la terminer ?",
                        "Il me faudrait en trouver la recette pour la terminer.",
                    };
                    dialogueManager.afficherScript(messages, "OK");}
                        );
        objetsBibliotheque.add(tableBibliotheque);

        // Créer d'abord l'item sans l'action
        Item potionItem = new Item("Potion de téléportation",
                "Vous allez être téléporté.e.",
                new java.io.File("assets/items/potion.png"),
                null);

        // Définir l'action après la création de l'item
        potionItem.setOnClick(() -> {
            // Téléporter le joueur à un autre endroit de la map avec animation
            teleportPlayer(430, 530, 2);
            inventaire.retirerItem(potionItem); // Maintenant on peut référencer potionItem
        });

        allItems.add(potionItem);

        // échelle item
        Item echelle = new Item("Échelle",
                "Une échelle en bois, elle semble solide.",
                new java.io.File("assets/items/echelle.png"), tableBibliotheque1,
                () -> {
                    // Action à exécuter lorsque l'item est cliqué
                });

        // Ajouter l'item à la liste globale des items
        allItems.add(echelle);

        grandeBibliotheque.setItemToInteract(echelle);

        // Set initial map's walls to Map class
        map.setMurs(new ArrayList<>(mursParMap.get(currentMapIndex)));

        // Objet qui lance le mini-jeu Couleurs (map 1)
        objetsBibliotheque.add(new Objet("dalle de couleurs",
                new Rectangle(420, 290, 50, 30), inventaire,
                () -> {
                    if (jeux.size() > 2) {
                        Game miniJeu = jeux.get(2); // CouleursGame est le 3ème mini-jeu
                        GameUtils.printDev("Lancement du mini-jeu: " + miniJeu.getName(), devMode);
                        miniJeu.afficherMiniJeu(map);
                    } else {
                        GameUtils.printDev("Erreur: jeux.size()=" + jeux.size() + ", impossible d'accéder à l'index 2 (CouleursGame)", devMode);
                        dialogueManager.afficherDialogue(
                                "Le mini-jeu Couleurs n'est pas disponible (erreur d'index).",
                                "OK");
                    }
                }));
    }

    // initialiser les mini-jeux disponibles
    private void initializeJeux() {
        jeux = new ArrayList<>();
        jeux.add(new MorpionGame(devMode, dialogueManager, () -> {
            String[] script = {
                    "Bravo! !",
                    "Vous avez réussi à bien mélanger la potion! Que pourriez-vous faire avec ?",
            };
            dialogueManager.afficherScript(script, "Suivant", () -> {
                // Donne la potion de téléportation à la fin du morpion
                for (Item item : allItems) {
                    if (item.getName().equals("Potion de téléportation")) {
                        if (!inventaire.contientItem(item.getName())) {
                            inventaire.ajouterItem(item);
                            GameUtils.printDev("Potion de téléportation ajoutée à l'inventaire après le mini-jeu de morpion.",
                                    devMode);
                            updateInventaireUI();
                        }
                        break;
                    }
                }
            });
            GameUtils.printDev("Mini-jeu de morpion terminé avec succès !", devMode);
        })); // Ajoute le morpion comme mini-jeu
        jeux.add(new ParcheminGame(devMode, dialogueManager, () -> {
            String[] script = {
                    "Bravo! Vous avez réussi le mini-jeu de parchemin !",
                    "Vous pouvez continuer votre aventure dans la bibliothèque."
            };
            dialogueManager.afficherScript(script, "Suivant");
            
            // donne l'échelle au joueur
            for (Item item : allItems) {
                if (item.getName().equals("Échelle")) {
                    if (!inventaire.contientItem(item.getName())) {
                        inventaire.ajouterItem(item);
                        GameUtils.printDev("Échelle ajoutée à l'inventaire après le mini-jeu de parchemin.",
                                devMode);
                        updateInventaireUI();
                    }
                    break;
                }
            }
        })); // Ajoute le parchemin comme mini-jeu
        jeux.add(new CouleursGame(devMode, dialogueManager));
        if (jeux.size() < 3) {
            GameUtils.printDev("Attention: la liste des mini-jeux ne contient pas 3 éléments! Taille actuelle: " + jeux.size(), devMode);
        }
    }

    /**
     * Display the introduction script
     */
    private void showIntroScript() {
        String[] introScript = {
                "Bienvenue dans Escape From The Biblioteca !",
                "Que voit-on au loin ? Une coline ? Un vieux village ?",
                "Je ne sais pas, cette forêt est si sombre et si dense...",
                "Et que voici par ici ? Un vieux bâtiment ? Peut-etre un château abandonné ?",
                "Enfin, c'est étrange, il n'y a pas un bruit et des torches sont encore allumées !",
                "On pourrait croire que quelqu'un vit encore ici...",
                "Cette forêt ne m'inspire pas confiance mais... le temps semble s'y etre figé.",
                "La nuit tombe, je n'ai d'autre choix que d'entrer dans ce bâtiment.",
        };

        // Show the introduction script
        // Utiliser un Timer pour s'assurer que l'interface est complètement initialisée
        javax.swing.Timer timer = new javax.swing.Timer(500, e -> {
            ((javax.swing.Timer) e.getSource()).stop();
            dialogueManager.afficherScript(introScript, "Suivant");
        });
        timer.setRepeats(false);
        timer.start();
    }

    /**
     * Change the current map
     * 
     * @param mapIndex The index of the map to change to
     */
    public void changeMap(int mapIndex) {
        // Si une animation est déjà en cours, ne rien faire
        if (animation.isRunning()) {
            return;
        }

        // Démarrer l'animation de fade out
        animation.startFadeOut(500, (Void v) -> {
            // Cette partie s'exécute après la fin du fade out
            currentMapIndex = mapIndex;
            map.setDisplayedMap(mapIndex);
            map.setMurs(new ArrayList<>(mursParMap.get(mapIndex)));

            // Mettre à jour l'élément de débogage pour la nouvelle carte
            if (devMode) {
                // Supprimer l'ancien élément de débogage et en ajouter un nouveau
                map.clearDebugLayer();
                map.addDebugLayerElement(new Drawable() {
                    @Override
                    public void draw(Graphics g) {
                        if (devMode && currentMapIndex < mursParMap.size() && currentMapIndex < objetsParMap.size()) {
                            GameUtils.drawDebugRectangles(g, mursParMap.get(currentMapIndex),
                                    objetsParMap.get(currentMapIndex), devMode);
                        }
                    }
                });
            }

            // Mettre à jour l'inventaire après changement de carte
            ajouterItemsInventaire();

            // Démarrer l'animation de fade in
            animation.startFadeIn(500, (Void v2) -> {
                // Réactiver les mouvements du joueur après la fin de l'animation
                joueur.setCanMove(true);
            });
        });
    }

    /**
     * Téléporte le joueur à une nouvelle position avec une animation
     * 
     * @param x     La nouvelle position X du joueur
     * @param y     La nouvelle position Y du joueur
     * @param state L'orientation du joueur après téléportation
     */
    public void teleportPlayer(int x, int y, int state) {
        GameUtils.printDev("État de l'animation avant téléportation: " + animation.isRunning(), devMode);

        // Réinitialiser complètement l'animation pour éviter les problèmes
        resetAnimation();

        GameUtils.printDev("Téléportation du joueur vers (" + x + ", " + y + ") avec état " + state, devMode);

        // Empêcher le joueur de bouger pendant l'animation
        joueur.setCanMove(false);

        // Démarrer l'animation de téléportation
        animation.startTeleport(joueur.getX(), joueur.getY(), playerSize, 1000, (Void v) -> {
            // Déplacer le joueur après l'animation
            joueur.setPosition(x, y);
            joueur.setState(state);
            // Réactiver les mouvements du joueur après la téléportation
            joueur.setCanMove(true);
        });
    }

    /**
     * Check if there is a collision at the given position
     * 
     * @param x      X position to check
     * @param y      Y position to check
     * @param width  Width of the bounding box
     * @param height Height of the bounding box
     * @return true if there is a collision, false otherwise
     */
    public boolean checkCollision(int x, int y, int width, int height) {
        Rectangle nextPos = new Rectangle(x, y, width, height);
        for (Rectangle mur : map.getMurs()) {
            if (nextPos.intersects(mur)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check for object interactions based on player position and state
     * 
     * @param pressedA True if the 'A' key is pressed
     */
    public void checkObjectInteractions(boolean pressedA) {
        // Create interaction zone around player using utility method
        Rectangle zoneInteraction = GameUtils.createInteractionZone(
                joueur.getX(), joueur.getY(), playerSize, interactionZoneSize);

        // Check all objects in the current map
        for (Objet obj : objetsParMap.get(currentMapIndex)) {
            if (zoneInteraction.intersects(obj.getHitbox())) {
                // Calculate object and player centers for direction checking
                int centreObjetX = obj.getHitbox().x + obj.getHitbox().width / 2;
                int centreObjetY = obj.getHitbox().y + obj.getHitbox().height / 2;
                int centreJoueurX = joueur.getX() + playerSize / 2;
                int centreJoueurY = joueur.getY() + playerSize / 2;

                // Check if player is facing the object using the utility method
                boolean regardeBonneDirection = GameUtils.playerFacingObject(
                        centreJoueurX, centreJoueurY, centreObjetX, centreObjetY, joueur.getState());

                // Set object active state based on whether player is facing it
                obj.setActive(regardeBonneDirection);

                if (devMode && regardeBonneDirection) {
                    GameUtils.printDev("Objet interactif à proximité immédiate. Appuyez sur 'A' pour interagir.",
                            devMode);
                }

                // Trigger the object if 'A' is pressed and player is facing it
                if (pressedA && regardeBonneDirection) {
                    obj.trigger();
                    if (obj.isAlreadyTriggered()) {
                        // Mettre à jour l'inventaire après le déclenchement de l'objet
                        ajouterItemsInventaire();
                        supprimerItemInventaire();
                    }
                }
            } else {
                obj.setActive(false);
            }
        }
    }

    /**
     * Check for direct object collisions (touching objects)
     * 
     * @param pressedA True if the 'A' key is pressed
     */
    public void checkDirectObjectCollisions(boolean pressedA) {
        // Create player hitbox
        Rectangle joueurBox = new Rectangle(joueur.getX(), joueur.getY(), playerSize, playerSize);

        // Check all objects in the current map
        for (Objet obj : objetsParMap.get(currentMapIndex)) {
            if (joueurBox.intersects(obj.getHitbox()) && pressedA) {
                obj.trigger();
                // Mettre à jour l'inventaire après le déclenchement de l'objet
                ajouterItemsInventaire();
                supprimerItemInventaire();
            }
        }
    }

    // ajouter les items de la map à l'inventaire
    public void ajouterItemsInventaire() {
        // Parcourir tous les items disponibles dans le jeu
        for (Item item : allItems) {
            // Vérifier si l'objet associé à cet item a été déclenché
            if (item.getObjet() != null && item.getObjet().isAlreadyTriggered()) {
                // Vérifier si l'item n'est pas déjà dans l'inventaire
                if (!inventaire.contientItem(item.getName()) && !inventaire.itemAEteRetire(item)) {
                    // Ajouter l'item à l'inventaire
                    inventaire.ajouterItem(item);
                    GameUtils.printDev("Item ajouté à l'inventaire: " + item.getName(), devMode);
                }
            }
        }
        // Mettre à jour l'interface de l'inventaire
        updateInventaireUI();
    }

    public void supprimerItemInventaire() {
        // Parcourir toutes les maps pour chercher les objets déclenchés
        for (int mapIndex = 0; mapIndex < objetsParMap.size(); mapIndex++) {
            for (Objet obj : objetsParMap.get(mapIndex)) {
                // Vérifier si l'objet a été déclenché et s'il a un item associé
                if (obj.isAlreadyTriggered() && obj.getItemToInteract() != null) {
                    // Supprimer l'item de l'inventaire
                    if (inventaire.contientItem(obj.getItemToInteract())) {
                        inventaire.retirerItem(obj.getItemToInteract());
                        GameUtils.printDev("Item supprimé de l'inventaire: " + obj.getItemToInteract().getName(),
                                devMode);
                    }
                }
            }
        }
        // Mettre à jour l'interface de l'inventaire
        updateInventaireUI();
    }

    /**
     * Update the inventory UI
     */
    public void updateInventaireUI() {
        inventaireUI.updateInventaire(inventaire);
    }

    // Les getters et setters ont été regroupés à la fin

    public Map getMap() {
        return map;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public DialogueManager getDialogueManager() {
        return dialogueManager;
    }

    public List<List<Objet>> getObjetsParMap() {
        return objetsParMap;
    }

    public int getCurrentMapIndex() {
        return currentMapIndex;
    }

    public boolean isDevMode() {
        return devMode;
    }

    public void setDevMode(boolean devMode) {
        this.devMode = devMode;
    }

    public int getPlayerSize() {
        return playerSize;
    }

    public int getInteractionZoneSize() {
        return interactionZoneSize;
    }

    public Animation getAnimation() {
        return animation;
    }

    /**
     * Réinitialise complètement l'état de l'animation
     */
    private void resetAnimation() {
        // Forcer l'arrêt de l'animation en cours
        animation.stop();

        // Créer une nouvelle instance d'animation pour remplacer l'ancienne
        animation = new Animation(map);

        // Ajouter un message de débogage
        GameUtils.printDev("Animation réinitialisée", devMode);
    }
}
