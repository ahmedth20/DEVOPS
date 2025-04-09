pipeline {
    agent any

    environment {
        DB_NAME = 'kaddemdb'
        DB_USER = 'root'
        DB_PASS = 'my-secret-pw'
        DB_PORT = '3306'
        MYSQL_CONTAINER = 'mysql-test'
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        IMAGE_NAME = 'ramezzorgui/kaddem app'
        IMAGE_TAG = "v${env.BUILD_NUMBER}"
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        git branch: 'Département', url: 'https://github.com/ahmedth20/DEVOPS.git'
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape Checkout : ${duration}s"
                    }
                }
            }
        }

        stage('Test Docker') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        set -e
                        echo "Vérification de Docker..."
                        whoami
                        docker version
                        docker ps
                        '''
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape Test Docker : ${duration}s"
                    }
                }
            }
        }

        stage('Start MySQL') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        set -e
                        echo "Démarrage de MySQL..."

                        # Créer le réseau mynetwork s'il n'existe pas
                        if ! docker network ls --format '{{.Name}}' | grep -q "^mynetwork$"; then
                            echo "Création du réseau mynetwork..."
                            docker network create mynetwork
                        fi

                        if docker ps -a --format '{{.Names}}' | grep -q "^$MYSQL_CONTAINER$"; then
                            if docker ps --format '{{.Names}}' | grep -q "^$MYSQL_CONTAINER$"; then
                                echo "Le conteneur MySQL est déjà en cours d'exécution."
                            else
                                echo "Le conteneur MySQL existe mais est arrêté. Redémarrage..."
                                docker start $MYSQL_CONTAINER
                            fi
                        else
                            echo "Démarrage d'un nouveau conteneur MySQL..."
                            docker run --name $MYSQL_CONTAINER \
                                --network mynetwork \
                                -e MYSQL_DATABASE=$DB_NAME \
                                -e MYSQL_ROOT_PASSWORD=$DB_PASS \
                                -p $DB_PORT:3306 \
                                --memory="1g" \
                                --cpus="2" \
                                -d mysql:8.0
                        fi

                        echo "Attente de MySQL (90 sec)..."
                        sleep 90

                        if ! docker ps --format '{{.Names}}' | grep -q "^$MYSQL_CONTAINER$"; then
                            echo "MySQL n'a pas démarré !"
                            echo "Logs du conteneur MySQL :"
                            docker logs $MYSQL_CONTAINER
                            exit 1
                        fi

                        # Vérification de la connexion à MySQL
                        echo "Vérification de la connexion à MySQL..."
                        for i in {1..40}; do
                            echo "Tentative $i : Vérification de l'état du conteneur..."
                            docker ps -a --format '{{.Names}} {{.Status}}' | grep $MYSQL_CONTAINER
                            if docker exec $MYSQL_CONTAINER mysql -u$DB_USER -p$DB_PASS -e "SELECT 1;" > /dev/null 2>&1; then
                                echo "MySQL est prêt !"
                                break
                            fi
                            echo "MySQL n'est pas encore prêt, attente... (tentative $i)"
                            sleep 3
                        done

                        if ! docker exec $MYSQL_CONTAINER mysql -u$DB_USER -p$DB_PASS -e "SELECT 1;" > /dev/null 2>&1; then
                            echo "Échec de la connexion à MySQL après 40 tentatives !"
                            echo "Logs du conteneur MySQL :"
                            docker logs $MYSQL_CONTAINER
                            exit 1
                        fi
                        '''
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape Start MySQL : ${duration}s"
                    }
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh 'mvn clean compile'
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape Build : ${duration}s"
                    }
                }
            }
        }

        stage('Test with Coverage') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        echo "Exécution des tests avec couverture..."
                        mvn test
                        '''
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000  // Corrigé en secondes pour cohérence
                        echo "Durée de l'étape Test with Coverage : ${duration}s"
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        withSonarQubeEnv('SonarQube') {
                            sh 'mvn sonar:sonar'
                        }
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape SonarQube Analysis : ${duration}s"
                    }
                }
            }
        }

        stage('Deploy to Nexus') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        echo "Déploiement vers Nexus (tests ignorés)..."
                        mvn deploy -DskipTests
                        '''
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape Deploy to Nexus : ${duration}s"
                    }
                }
            }
        }

        stage('DOCKER IMAGE') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        echo "Construction de l'image Docker..."
                        docker build -t ${IMAGE_NAME}:${IMAGE_TAG} .
                        '''
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape DOCKER IMAGE : ${duration}s"
                    }
                }
            }
        }

        stage('DOCKER HUB') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        echo "Connexion à Docker Hub..."
                        echo $DOCKERHUB_CREDENTIALS_PSW | docker login -u $DOCKERHUB_CREDENTIALS_USR --password-stdin
                        echo "Pousse de l'image vers Docker Hub..."
                        docker push ${IMAGE_NAME}:${IMAGE_TAG}
                        '''
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape DOCKER HUB : ${duration}s"
                    }
                }
            }
        }

        stage('DOCKER-COMPOSE') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh '''
                        set -e
                        echo "Vérification de la présence de docker-compose.yml..."
                        ls -la
                        if [ ! -f docker-compose.yml ]; then
                            echo "Erreur : docker-compose.yml introuvable !"
                            exit 1
                        fi
                        echo "Vérification de l'image Docker..."
                        docker images | grep ${IMAGE_NAME}
                        echo "Vérification et arrêt du conteneur mysql-test..."
                        if docker ps -a --format '{{.Names}}' | grep -q "^mysql-test$"; then
                            echo "Arrêt et suppression du conteneur mysql-test..."
                            docker stop mysql-test || true
                            docker rm mysql-test || true
                        fi
                        echo "Vérification des ports utilisés..."
                        docker ps -a --format '{{.Names}} {{.Ports}}'
                        echo "Exportation de IMAGE_TAG pour Docker Compose..."
                        export IMAGE_TAG=${IMAGE_TAG}
                        echo "Lancement de Docker Compose avec IMAGE_TAG=${IMAGE_TAG}..."
                        docker-compose up -d --build
                        echo "Vérification des conteneurs lancés..."
                        docker-compose ps
                        '''
                    } finally {
                        def endTime = System.currentTimeMillis()
                        def duration = (endTime - startTime) / 1000
                        echo "Durée de l'étape DOCKER-COMPOSE : ${duration}s"
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                if (currentBuild.result == 'SUCCESS') {
                    sh '''
                    echo "Nettoyage de l'environnement (pipeline réussi)..."
                    if docker ps --format '{{.Names}}' | grep -q "^$MYSQL_CONTAINER$"; then
                        docker stop $MYSQL_CONTAINER || true
                        docker rm $MYSQL_CONTAINER || true
                    fi
                    if ls -1 | grep -i "^docker-compose\\.yml$"; then
                        docker-compose down || true
                    else
                        echo "Aucun fichier docker-compose.yml trouvé, pas de nettoyage Docker Compose nécessaire."
                    fi
                    '''
                } else {
                    sh '''
                    echo "Pipeline échoué, préservation du conteneur $MYSQL_CONTAINER pour le débogage..."
                    if ls -1 | grep -i "^docker-compose\\.yml$"; then
                        docker-compose down || true
                    else
                        echo "Aucun fichier docker-compose.yml trouvé, pas de nettoyage Docker Compose nécessaire."
                    fi
                    '''
                }
            }
        }
    }
}
