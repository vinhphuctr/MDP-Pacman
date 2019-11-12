**Nom/Prénom Etudiant :** Canete Valentin 11502374


# Rapport TP1

## Question 5.1 Brigde Grid
*Donnez les valeurs des paramètres et la justification de ces choix*

Gamma = 0.9 / Bruit = 0.0 / Autre récompenses = 0.0

Si on rajoute du bruit, l'agent ne peut pas converger car il a trop de chances de tomber dans un état aborsant avec une récompense négative.

## Question 5.2 Discount Grid
*Donnez les valeurs des paramètres dans chaque cas et la justification de ces choix*

1. qui suit un chemin risqué pour atteindre l’état absorbant de récompense +1 :
   
    Gamma = 0.9 / Bruit = 0.2 / Autre récompenses = -2

    Comme chaque pas ont une récompense négative de -2, aller jusqu'à l'état +10 n'est plus rentable.

    </br>

2. qui suit un chemin risqué pour atteindre l’état absorbant de récompense +10 :

    Gamma = 0.9 / Bruit = 0.0 / Autre récompenses = 0

    Il n'y a plus de chemin risqué donc il devient le plus court pour arriver à cet état.

    </br>
3. qui suit un chemin sûr pour atteindre l’état absorbant de récompense +1 :

    Gamma = 0.2 / Bruit = 0.2 / Autre récompenses = 0.0

    On réduit la capacité d'exploration de l'agent, donc il converge plus vers le premier état qu'il trouve.

    </br>
4. qui évite les états absorbants :

    Gamma = 0.9 / Bruit = 0.2 / Autre récompenses = 10

    Parcourir la grille rapporte plus que s'arrêter, donc l'agent ne veut pas tomber dans un état absorbant.

    </br>


# Rapport TP2

## Question 1:
*Précisez et justifiez les éléments que vous avez utilisés pour la définition d’un état du MDP pour le jeu du Pacman (partie 2.2)*

Pour l'état du MDP, on a d'abord la matrice qui répresente les alentours du pacman à 2 cases.
Par exemple : 
```
00340
44040
04140
04040
00040
```
4 représente les murs, 3 les dot et les capsules et 2 les ghosts.

On a en plus, stockée sous forme d'int, l'action qui mène au dot le plus proche. On simule chaque action possible et on garde celle qui réduit la distance du dot le plus proche. Si le dot est déjà a porté, l'action est automatiquement assignée.

Cette représentation permet d'avoir une vue précise de l'environnement et peut guider le pacman vers les dots afin qu'il ne tourne pas en rond.
On pourrait ajouter de l'informations pour prévenir de la position des ghosts mais je n'ai pas trouvé de bonnes solutions sans ajouter trop de complexité.

### smallGrid

<div style="text-align:center"><img width="550" height="400" src="img_rapport/appr_smallGrid_QLearning.png" /></div>

<div style="text-align:center"><img width="300" height="100" src="img_rapport/console_smallGrid_QLearning.png" /></div>

### smallGrid2

<div style="text-align:center"><img width="550" height="400" src="img_rapport/appr_smallGrid2_QLearning.png" /></div>

<div style="text-align:center"><img width="300" height="100" src="img_rapport/console_smallGrid2_QLearning.png" /></div>

### mediumGrid

<div style="text-align:center"><img width="550" height="400" src="img_rapport/appr_medium_QLearning.png" /></div>

<div style="text-align:center"><img width="300" height="100" src="img_rapport/console_mediumGrid_QLearning.png" /></div>

## Question 2:
*Précisez et justifiez les fonctions caractéristiques que vous avez choisies pour la classe FeatureFunctionPacman (partie 2.3).*

J'ai repris les fonctions proposées par l'énoncé qui permettent d'atteindre facilement + de 80% sur les smallGrid, mais d'amélioration sur mediumGrid.