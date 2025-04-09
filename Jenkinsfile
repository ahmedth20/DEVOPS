pipeline {
    agent any

    environment {
        DB_NAME = 'test_db'
        DB_USER = 'root'
        DB_PASS = 'root'
        DB_PORT = '3306'
        MYSQL_CONTAINER = 'mysql-test'
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
                        echo "Durée de l'étape Checkout : ${(endTime - startTime) / 1000}s"
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
                        echo "Durée de l'étape Test Docker : ${(endTime - startTime) / 1000}s"
                    }
                }
            }
        }

        stage('Start MySQL') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        sh """
                        set -e
                        echo "Démarrage de MySQL..."

                        if docker ps -a --format '{{.Names}}' | grep -q "^${MYSQL_CONTAINER}\$"; then
                            if docker ps --format '{{.Names}}' | grep -q "^${MYSQL_CONTAINER}\$"; then
                                echo "Le conteneur MySQL est déjà en cours d'exécution."
                            else
                                echo "Le conteneur MySQL existe mais est arrêté. Redémarrage..."
                                docker start ${MYSQL_CONTAINER}
                            fi
                        else
                            echo "Démarrage d'un nouveau conteneur MySQL..."
                            docker run -d --name ${MYSQL_CONTAINER} \\
                                -e MYSQL_ROOT_PASSWORD='${DB_PASS}' \\
                                -e MYSQL_DATABASE=${DB_NAME} \\
                                -e MYSQL_ALLOW_EMPTY_PASSWORD=yes \\
                                -p ${DB_PORT}:3306 \\
                                --restart=no \\
                                mysql:5.7
                        fi

                        echo "Attente de MySQL (10 sec)..."
                        sleep 10

                        if ! docker ps --format '{{.Names}}' | grep -q "^${MYSQL_CONTAINER}\$"; then
                            echo "MySQL n'a pas démarré !" 
                            exit 1
                        fi

                        echo "Vérification de la connexion à MySQL..."
                        for i in {1..10}; do
                            if docker exec ${MYSQL_CONTAINER} mysql -u${DB_USER} -p${DB_PASS} -e "SELECT 1;" > /dev/null 2>&1; then
                                echo "MySQL est prêt !"
                                break
                            fi
                            echo "MySQL n'est pas encore prêt, attente..."
                            sleep 2
                        done

                        if ! docker exec ${MYSQL_CONTAINER} mysql -u${DB_USER} -p${DB_PASS} -e "SELECT 1;" > /dev/null 2>&1; then
                            echo "Échec de la connexion à MySQL après 10 tentatives !"
                            exit 1
                        fi
                        """
                    } finally {
                        def endTime = System.currentTimeMillis()
                        echo "Durée de l'étape Start MySQL : ${(endTime - startTime) / 1000}s"
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
                        echo "Durée de l'étape Build : ${(endTime - startTime) / 1000}s"
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
                        echo "Durée de l'étape Test with Coverage : ${(endTime - startTime) / 1000}s"
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
                        echo "Durée de l'étape SonarQube Analysis : ${(endTime - startTime) / 1000}s"
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
                        echo "Durée de l'étape Deploy to Nexus : ${(endTime - startTime) / 1000}s"
                    }
                }
            }
        }
                stage('Docker Build') {
            steps {
                script {
                    echo '🐳 Building Docker Image...'
                    sh 'docker build -t ramezzorgui/kaddem-app:0.0.1 .'
                }
            }
        }

        stage('List Docker Images') {
            steps {
                script {
                    echo '📦 Listing Docker Images...'
                    sh 'docker images'
                }
            }
        }

        stage('Push to DockerHub') {
            steps {
                script {
                    echo '🚀 Pushing Docker Image to DockerHub...'
                    withCredentials([usernamePassword(credentialsId: 'dockerhub', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PAT')]) {
                        sh '''
                            echo "$DOCKER_PAT" | docker login -u "$DOCKER_USER" --password-stdin
                            docker push ramezzorgui/kaddem-app:0.0.1
                        '''
                    }
                }
            }
        }
        stage('Deploy with Docker Compose') {
            steps {
                script {
                    echo '🚀 Deploying with Docker Compose...'
                    sh 'docker compose up -d'
                }
            }
        }
    }
}
