pipeline {
    agent any

    environment {
        DB_NAME = 'Kaddemdb'  // Correspond à spring.datasource.url
        DB_USER = 'root'  
        DB_PASS = 'ahmedequipe'  // Pas de mot de passe défini dans application-test.properties
        DB_PORT = '3306'  // Port par défaut, car localhost:3306 est utilisé
        MYSQL_CONTAINER = 'mysql-test'
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
                    # Ensure the Docker client is used from WSL
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
                    sh 'mvn clean compile'
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh 'mvn sonar:sonar'
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
    }
}
