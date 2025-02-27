pipeline {
    agent any

    environment {
        DB_NAME = 'kaddemdb'
        DB_USER = 'root'
        DB_PASS = ''
        DB_PORT = '3306'
        MYSQL_CONTAINER = 'mysql-test'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    git branch: 'Etudiant', url: 'https://github.com/ahmedth20/DEVOPS.git'
                }
            }
        }

        stage('Test Docker') {
            steps {
                script {
                    sh '''
                    set -e
                    echo "Vérification de Docker..."
                    whoami
                    docker version
                    docker ps
                    '''
                }
            }
        }

        stage('Start MySQL') {
            steps {
                script {
                    sh '''
                    set -e
                    echo "Démarrage de MySQL..."

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
                            -e MYSQL_DATABASE=$DB_NAME \
                            -e MYSQL_ROOT_PASSWORD=$DB_PASS \
                            -p $DB_PORT:3306 \
                            -d mysql:8
                    fi

                    echo "Attente de MySQL (10 sec)..."
                    sleep 10

                    if ! docker ps --format '{{.Names}}' | grep -q "^$MYSQL_CONTAINER$"; then
                        echo "MySQL n'a pas démarré !" 
                        exit 1
                    fi
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
                withSonarQubeEnv('SQ1') {
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

    post {
        always {
            script {
                echo "Pipeline terminé."
            }
        }
        failure {
            script {
                echo "Une erreur est survenue."
            }
        }
    }
}
