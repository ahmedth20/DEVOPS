pipeline {
    agent any

    environment {
        registryCredentials = "nexus"
        registry = "localhost:8083"
        DB_NAME = 'Kaddemdb'
        DB_USER = 'root'
        DB_PASS = 'ahmedequipe'
        DB_PORT = '3306'
        MYSQL_CONTAINER = 'mysql-test'
        IMAGE_NAME = "springbootapp:1.0"
        DOCKER_REGISTRY_URL = "http://localhost:8083"  // URL en HTTPS pour Nexus
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    git branch: 'Equipe', url: 'https://github.com/ahmedth20/DEVOPS.git'
                }
            }
        }

        stage('Start MySQL') {
            steps {
                script {
                    sh '''
                    WSL_DOCKER="docker"

                    if [ $(wsl $WSL_DOCKER ps -a -q -f name=$MYSQL_CONTAINER) ]; then
                        if [ $(wsl $WSL_DOCKER ps -q -f name=$MYSQL_CONTAINER) ]; then
                            echo "Le conteneur MySQL est déjà en cours d'exécution."
                        else
                            echo "Le conteneur MySQL existe mais est arrêté. Redémarrage..."
                            wsl $WSL_DOCKER start $MYSQL_CONTAINER
                        fi
                    else
                        echo "Démarrage de MySQL..."
                        wsl $WSL_DOCKER run --name $MYSQL_CONTAINER \
                            -e MYSQL_DATABASE=$DB_NAME \
                            -e MYSQL_ROOT_PASSWORD=$DB_PASS \
                            -p $DB_PORT:3306 \
                            -d mysql:8
                    fi

                    echo "Attente de MySQL (10 sec)..."
                    sleep 10

                    wsl $WSL_DOCKER ps | grep $MYSQL_CONTAINER || (echo "MySQL n'a pas démarré !" && exit 1)
                    '''
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh 'mvn clean package'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    sh 'mvn test'
                }
            }
        }

        /*  stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
                }
            }
        } */

        /* stage('Build Docker Image') {
            steps {
                script {
                    sh 'docker build -t $registry/$IMAGE_NAME .'
                }
            }
        } */

       stage('Deploy to Nexus') {
    steps {
        script {
               // Utiliser le bon contexte Docker
            sh 'docker context use desktop-linux'
            // Login to Nexus Docker registry using HTTP
            sh '''
            echo admin | docker login -u admin --password-stdin http://localhost:8083
            '''
            docker.withRegistry('http://localhost:8083', registryCredentials) {
                sh 'docker push $registry/$IMAGE_NAME'
            }
        }
    }
}


        stage('Run Application') {
            steps {
                script {
                    docker.withRegistry("http://$registry", registryCredentials) {
                        sh '''
                        docker pull $registry/$IMAGE_NAME

                        if [ "$(docker images -q $registry/$IMAGE_NAME)" == "" ]; then
                            echo "Erreur: L'image Docker n'a pas été téléchargée correctement"
                            exit 1
                        fi

                        echo "Création du fichier docker-compose.yml..."
                        cat <<EOF > docker-compose.yml
                        version: '3.8'
                        services:
                          db:
                            image: mysql:8
                            container_name: mysql-test
                            restart: always
                            environment:
                              MYSQL_DATABASE: ${DB_NAME}
                              MYSQL_ROOT_PASSWORD: ${DB_PASS}
                            ports:
                              - "3306:3306"
                          app:
                            image: ${registry}/${IMAGE_NAME}
                            container_name: springboot-app
                            depends_on:
                              - db
                            ports:
                              - "8080:8080"
                        EOF

                        echo "Démarrage des services avec Docker Compose..."
                        docker-compose up -d
                        '''
                    }
                }
            }
        }

        stage('Cleanup') {
            steps {
                script {
                    sh 'docker stop $MYSQL_CONTAINER || true'
                    sh 'docker rm $MYSQL_CONTAINER || true'  // Supprimer le conteneur après l'arrêt
                }
            }
        }
    }
}
