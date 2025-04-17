Ce projet utilise un pipeline Jenkins complet pour automatiser l'intégration et le déploiement continus (CI/CD) de l'application kaddem-app. 
Le pipeline est structuré en plusieurs étapes essentielles, allant du téléchargement du code source à partir de la branche Département du dépôt GitHub, jusqu’au déploiement final à l’aide de Docker Compose. 
Les étapes incluent la vérification de Docker, la compilation du projet Maven, l'exécution des tests unitaires avec couverture, l’analyse statique du code via SonarQube, le déploiement de l’artefact vers un registre Nexus,
la construction et le push de l’image Docker sur DockerHub, et enfin le déploiement automatisé de l'application dans un environnement de conteneur. Aussi chaque stage est sécurisé par les credentials 
À chaque étape, la durée est mesurée pour permettre une meilleure visibilité sur les performances. Un système de notification par email(avec Mailtrap) informe automatiquement du résultat final du build. 
Ce pipeline assure un cycle DevOps fluide, reproductible et sécurisé.
