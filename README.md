# IncidentManagerAI

## Description
IncidentManagerAI est une application légère et intelligente de gestion des incidents, construite avec **Java Spring Boot** et **Vue.js**.
Elle intègre des fonctionnalités basées sur l'intelligence artificielle pour :
- **Détecter automatiquement des doublons** parmi les incidents signalés.
- **Prioriser les incidents** grâce à une gestion des degrés d'importance (faible, moyen, critique).
- **Créer une base de connaissances** pour suggérer des solutions à des incidents similaires.

### Fonctionnalités principales
1. **CRUD des incidents** :
    - Ajouter, lire, mettre à jour et supprimer des incidents.
2. **Gestion des priorités** :
    - Catégoriser chaque incident par degré d'importance (faible, moyen, critique).
3. **Détection des doublons** :
    - Comparer les titres et descriptions des incidents pour détecter les doublons automatiquement.
4. **Base de connaissances** :
    - Proposer des solutions à partir d'incidents résolus précédemment.

---

## Technologies utilisées
### Backend
- **Java Spring Boot** : Framework pour créer des APIs REST.
- **H2 Database** : Base de données légère pour stocker les incidents.
- **Smile** : Bibliothèque d'intelligence artificielle pour la détection de doublons.

### Frontend
- **Vue.js** : Framework JavaScript pour créer une interface utilisateur simple.
- **Axios** : Librairie HTTP pour les appels API REST.

---

## Installation
### Prérequis
- **Java 11+**
- **Maven**

### Étapes d'installation
1. Clonez ce dépôt :
   ```bash
   git clone https://github.com/DH-HUB/incidentManagerAI.git
   ```

2. Accédez au dossier du projet :
   ```bash
   cd IncidentManagerAI
   ```

3. Compilez et lancez l'application Spring Boot :
   ```bash
   mvn spring-boot:run
   ```

4. Ouvrez votre navigateur et accédez à :
   ```
   http://localhost:8080
   ```

---

## Utilisation
### Endpoints API
| Méthode | Endpoint          | Description                  |
|---------|-------------------|------------------------------|
| GET     | `/api/incidents`  | Récupérer tous les incidents |
| POST    | `/api/incidents`  | Ajouter un nouvel incident   |
| PUT     | `/api/incidents/{id}` | Mettre à jour un incident   |
| DELETE  | `/api/incidents/{id}` | Supprimer un incident       |
| GET     | `/api/incidents/duplicates` | Détecter les doublons    |

---

## Tests
Ce projet utilise une approche **TDD (Test-Driven Development)**. Les tests unitaires sont dans le dossier :
`src/test/java/com/example/incidentmanagerai`.

Pour exécuter les tests :
```bash
mvn test
```

---

## Auteur
Développé par Hakima Djermouni.
#   i n c i d e n t M a n a g e r A I  
 