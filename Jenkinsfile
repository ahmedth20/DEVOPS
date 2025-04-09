pipeline {
    agent any

    stages {
        stage('Setup MySQL for Tests') {
            steps {
                script {
                    echo 'Cleaning up any existing MySQL containers on port 3306...'
                    sh '''
                        docker ps -a -q --filter "expose=3306" | xargs -r docker stop || true
                        docker ps -a -q --filter "expose=3306" | xargs -r docker rm || true
                    '''
                    
                    echo 'Starting MySQL container for tests (MySQL 5.7)...'
                    sh '''
                        docker run -d --name mysql-test \
                            -e MYSQL_ROOT_PASSWORD= \
                            -e MYSQL_DATABASE=kaddemdb \
                            -e MYSQL_ALLOW_EMPTY_PASSWORD=yes \
                            -p 3306:3306 \
                            --restart=no \
                            mysql:5.7
                    '''
                    // Attendre un peu pour laisser le conteneur s'initialiser
                    sh 'sleep 5'
                    // Vérifier l'état du conteneur
                    sh 'docker ps -a --filter name=mysql-test'
                    // Capturer les logs détaillés
                    sh 'docker logs mysql-test'
                    // Attendre que MySQL soit prêt
                    sh '''
                        timeout 60s bash -c "until docker exec mysql-test mysqladmin -uroot -h 127.0.0.1 status; do sleep 2; echo 'Waiting for MySQL...'; done"
                    '''
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile'
            }
        }

        stage('Unit Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    def startTime = System.currentTimeMillis()
                    try {
                        withSonarQubeEnv('SQ1') {
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
