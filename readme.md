# Projet Minispec - Générateur de Code

## Environnement Technique
* **Langage :** Java 21
* **Tests :** JUnit 5

## État d'avancement

### Partie 1-3 : Bases Minispec
- Parsing des Entités et Attributs.
- Gestion des Types simples (String, Integer) et Collections (List, Set, Array).
- Génération des classes Java (POJO) avec Getters/Setters.

### Partie 4 : Héritage et Configuration
- Gestion de l'héritage (`extends` dans le XML et `extends` en Java).
- **Configuration externe (`java-code`)** : Mapping des noms de modèles vers des packages Java (`m2tiil.space`, etc.).
- **Gestion des Imports** : Génération automatique des `import` uniquement si nécessaire (si l'entité référencée est dans un autre package).
- **Types Concrets** : Transformation dynamique des types (ex: `List` -> `ArrayList` selon la config).

### Partie 5 : Valeurs Initiales (En cours / Finalisé)
Implémentation du pattern **Generation Gap** (via Interfaces) pour gérer l'initialisation complexe.
- Lecture de l'attribut `default="..."` dans le XML.
- **Valeurs simples** : Génération de `public String nom = "Val";`.
- **Constructeurs** : Génération de `public Point p = new Point(0,0);`.
- **Appels de méthodes** : Détection automatique des appels (ex: `nextId()`).
