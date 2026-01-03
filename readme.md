# 🏥 Medilabo Solutions - Système de Prévention du Diabète

Ce projet est une application basée sur une **architecture microservices** permettant le suivi des patients, la gestion des notes cliniques et l'évaluation des risques de diabète.

---

## 🛠 Configuration de l'Environnement

Le projet utilise des variables d'environnement pour orchestrer la communication entre les microservices et sécuriser les accès aux bases de données. Un fichier `.env` est requis à la racine du projet.

| Catégorie | Clé | Description |
| :--- | :--- | :--- |
| **Réseau** | `PORT_XXX` | Définit les ports d'exposition pour la Gateway (8083), le Front (8082) et les services métier (8081, 8084, 8085). |
| **Persistance SQL** | `MYSQL_ROOT_PASSWORD` | Mot de passe administrateur pour l'instance MySQL (données patients). |
| | `MYSQL_DATABASE` | Nom de la base de données relationnelle (`patientdb`). |
| **Persistance NoSQL**| `MONGO_DATABASE` | Nom de la base de données MongoDB pour le stockage des notes cliniques. |
| **Sécurité (Auth)** | `AUTH_XXX_USERNAME` | Identifiants techniques utilisés pour l'authentification "Service-to-Service" via Spring Security. |
| **Service Discovery**| `XXX_SERVICE_URL` | URLs internes permettant aux services de communiquer entre eux (ex: via le réseau Docker). |


## 🌿 Engagement Green Code & Éco-conception

Dans le cadre du développement, une attention particulière a été portée à la **sobriété numérique** (réduction de la consommation CPU/RAM et optimisation du cycle de vie matériel).

 - Réalisé (Implémenté)
    * Optimisation des algorithmes : Normalisation des "triggers" de diagnostic dans le constructeur des services pour éviter des calculs redondants à chaque requête.

    * DNS Interne Docker : Utilisation du réseau bridge de Docker pour une résolution d'hôte efficace sans passer par le réseau externe.

    * Pagination : Mise en œuvre de la pagination sur les listes de patients pour limiter la charge RAM et la bande passante.

    * Désactivation des Auto-configs : Optimisation du boot time en excluant les dépendances inutilisées (ex: exclusion de MongoDB dans le microservice Patient).

 - Pistes d'amélioration (Backlog)
    * Images: Migration progressive vers des images pour réduire l'empreinte disque de 60%.

    * Mise en cache (Redis) : Projet d'implémentation d'un cache distribué pour les scores de risques déjà calculés.

    * DTO (Data Transfer Objects) : Filtrage plus granulaire des données pour ne transférer que les champs requis par le Front-end.

## 🚀 Lancement du projet

1. **Clonage du dépôt** : `git clone https://github.com/Marouane61994/Projet_9.git
2. **Configuration** : Copier le fichier .env.example et le renommer en .env à la racine du projet, puis remplir les variables nécessaires.
3. **Exécution** :
   ```bash
   docker-compose up --build

## 🏗 Architecture & Modélisation

Bases de données

Le projet utilise une approche hybride (Polyglot Persistence) :

  - MySQL (Relationnel) : Utilisé pour les données patients. Le schéma est conçu en 3NF (Troisième Forme Normale) pour garantir l'intégrité des données et éliminer les redondances transitives.

  - MongoDB (NoSQL) : Utilisé pour les notes cliniques, offrant la flexibilité nécessaire pour stocker des rapports médicaux de longueurs et formats variables.