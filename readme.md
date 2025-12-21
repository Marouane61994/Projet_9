A/ Recommandations Green Code

1. Compréhension des enjeux
   Le Green Code (ou écoconception logicielle) n'est pas qu'une question d'écologie, c'est une démarche d'efficience technique.

- Objectif principal : Réduire l'empreinte environnementale du logiciel en optimisant la consommation des ressources (CPU, RAM, stockage) tout au long de son cycle de vie.

- Enjeux clés :

   * Réduction énergétique : Moins de cycles CPU = moins de consommation électrique dans les datacenters.

   * Durabilité matérielle : Un code léger permet de faire durer les serveurs et les terminaux plus longtemps avant qu'ils ne deviennent obsolètes.

   * Sobriété : Éviter le "gras numérique" en ne développant que les fonctionnalités essentielles.



2. Identification des gisements d'optimisation
   Pour identifier les parties du code qui consomment de la mémoire inutilement, nous utilisons :

- Profilage (Profiling) : Utilisation d'outils comme VisualVM ou JProfiler pour détecter les fuites de mémoire (memory leaks) et les objets qui stagnent dans la Heap.

- Analyse Statique : Outils comme SonarQube pour repérer les boucles inefficaces ou les instanciations d'objets redondantes.

- Docker Stats : Surveillance de la consommation réelle des conteneurs pour ajuster les ressources allouées.



3. Pistes d'amélioration pour le projet Medilabo
   Même si ces principes ne sont pas appliqués immédiatement, voici une analyse critique du projet actuel :

B/ Infrastructure & Docker
- Images Light : Passer de l'image openjdk:17 à des images Alpine (openjdk:17-alpine) pour réduire la taille des images disque de ~300Mo à ~100Mo.

- Limitation des ressources : Ajouter des deploy.resources.limits dans le docker-compose.yml pour empêcher un microservice de monopoliser le CPU de l'hôte.

C/ Architecture Microservices
- Désactivation des auto-configurations : Dans le microservice-front, désactiver explicitement les configurations MongoDB et JPA inutilisées pour accélérer le démarrage (réduction du temps de CPU au boot).

- Pagination : Implémenter la pagination sur l'affichage des listes de patients pour éviter de charger des centaines d'objets en mémoire vive si la base de données grandit.

D/ Communications
- Mise en cache : Utiliser un cache local (Caffeine) dans le DiabetesService pour les rapports déjà générés, évitant ainsi de solliciter inutilement la Gateway et les microservices Notes/Patient à chaque rafraîchissement de page.

- JSON sélectif : Ne transférer que les champs nécessaires entre les services (Data Transfer Objects) au lieu d'envoyer l'objet complet.