# Escape Game

> Projet INSA HdF 2A - Dpt Info Module Développement d'applications
> **Groupe :** [Maël Advisse](https://github.com/maeladv) & [Louison Bednarowicz](https://github.com/BillyTheSecond)


## Idée de départ
Le jeu se se plongera dans un univers mysterieux et ensorcelé. Il était une fois, dans un monde lointain et à l'écart de toute civilisation, un sorcier pris d'une rage soudaine ensorcela son pays. Ce territoire reculé se figea alors et depuis, le temps s'y est stoppé. Dans la coincidence la plus totale, vous vous baladez dans une forêt proche et tombez sur une bibliothèque abandonnées où le temps semble avoir laissé des traces. Les bougies sont encore allumées mais aucun signe de vie ne laisse paraître. Vous rentrez dedans et soudainement. VLAN. La porte se referme violemment derrière vous. Arriverez-vous à vous en sortir? Et par la même occasion, arriverez-vous à libérer ce peuple de cette malédiction?

-> Dans chaque partie de la bibliothèque, le joueur pourra effectuer des défis et résoudre des énigmes pour trouver des morceaux du code secret final.****
-> Monde 2D où le personnage peut se déplacer




## Déroulé du jeu

1. Introduction
   - Démarrage avec une image de la foret et de la bibliothèque (le joueur peur rentrer dans la bibliothèque)
   - Le script de l'histoire apparait dans un context box
   - Le joueur rentre
2. la map de la bibliothèque apparait
 - On obtient plus de contexte sur l'histoire et les objectisf du jeu
  
3. certains objets peuvent s'activer au clic sur A -> Une image / mini jeu s'affiche par dessus la map

## Compilation et lancement du jeu

`javac -d out -encoding utf8 App\*.java`

`java -cp out App.Main`

