# IncidentManagerAI

## Description
IncidentManagerAI est une application légère et intelligente de gestion des incidents, construite avec **Java Spring Boot** et **Vue.js**.  
Elle intègre des fonctionnalités basées sur l'intelligence artificielle pour :

- **Détecter automatiquement des doublons** parmi les incidents signalés.
- **Prioriser les incidents** grâce à une gestion des degrés d'importance (faible, moyen, critique).
- **Créer une base de connaissances** pour suggérer des solutions à des incidents similaires.

### Fonctionnalités principales
- **CRUD des incidents** : Ajouter, lire, mettre à jour et supprimer des incidents.
- **Gestion des priorités** : Catégoriser chaque incident par degré d'importance (faible, moyen, critique).
- **Détection des doublons** : Comparer les titres et descriptions des incidents pour détecter les doublons automatiquement.
- **Base de connaissances** : Proposer des solutions à partir d'incidents résolus précédemment.

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
- **Node.js 16+** (pour le frontend)

### Étapes d'installation

#### Backend (Spring Boot)
1. Clonez ce dépôt :
   ```bash
   git clone https://github.com/DH-HUB/incidentManagerAI.git
