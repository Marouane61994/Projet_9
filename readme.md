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

### 🚀 Optimisation de l'Infrastructure
* **Images Légères** : Utilisation de distributions **Alpine Linux** (`openjdk:17-alpine`) pour réduire l'empreinte disque et le temps de transfert réseau.
* **Limitation des Ressources** : Configuration de `limits` CPU et RAM dans le fichier `docker-compose.yml` pour garantir la stabilité de l'hôte.

### 💻 Efficience du Code
* **Pagination** : Mise en œuvre de la pagination pour éviter les surcharges de mémoire vive lors de la récupération des listes de patients.
* **Désactivation des Auto-configs** : Optimisation du temps de démarrage (boot time) en excluant les dépendances inutilisées (ex: exclusion de JPA dans les services NoSQL).

### 📡 Gestion des Flux
* **Mise en cache** : Utilisation d'un cache local pour limiter les appels réseau redondants.
* **DTO (Data Transfer Objects)** : Filtrage des données transférées pour ne véhiculer que le strict nécessaire.

---

## 🚀 Lancement du projet

1. **Clonage du dépôt** : `git clone <https://github.com/Marouane61994/Projet_9.git>`
2. **Configuration** : Remplir le fichier `.env.exemple` à la racine à partir des clés listées ci-dessus.
3. **Exécution** :
   ```bash
   docker-compose up --build
